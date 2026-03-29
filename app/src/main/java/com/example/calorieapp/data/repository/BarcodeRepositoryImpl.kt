package com.example.calorieapp.data.repository

import com.example.calorieapp.data.DataSource.local.ProductDao
import com.example.calorieapp.data.DataSource.local.ScannedProductDao
import com.example.calorieapp.data.DataSource.remote.BarcodeApiService
import com.example.calorieapp.data.Models.ScannedProductEntity
import com.example.calorieapp.data.Models.toDomainProduct
import com.example.calorieapp.data.Models.toEntity
import com.example.calorieapp.data.Models.toProductEntity
import com.example.calorieapp.domain.entities.DailyMacrosSummary
import com.example.calorieapp.domain.entities.Product
import com.example.calorieapp.domain.repository.BarcodeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BarcodeRepositoryImpl @Inject constructor(
    private val productDao: ProductDao,
    private val scannedProductDao: ScannedProductDao,
    private val api: BarcodeApiService
): BarcodeRepository {

    override suspend fun addMeal(meal: Product) {
        val existing = productDao.getProductByBarcode(meal.barcode)
        if (existing != null && !existing.isDeleted) {
            val finalQuantity = existing.quantity + meal.quantity
            productDao.updateProductQuantity(meal.barcode, finalQuantity)
        } else {
            val product = meal.toEntity().copy(quantity = meal.quantity, scannedAt = java.util.Date())
            productDao.insertProduct(product)
        }
    }

    override suspend fun addMealFromScan(product: Product) {
        val existing = productDao.getProductByBarcode(product.barcode)
        if (existing != null && !existing.isDeleted) {
            val finalQuantity = existing.quantity + product.quantity
            productDao.updateProductQuantity(product.barcode, finalQuantity)
        } else {
            val entity = product.toEntity().copy(quantity = product.quantity, scannedAt = java.util.Date())
            productDao.insertProduct(entity)
        }
    }

    override suspend fun scanProduct(barcode: String): Result<Product> {
        return try {
            // First check the scan cache
            val cachedProduct = scannedProductDao.getScannedProductByBarcode(barcode)
            if (cachedProduct != null) {
                val updatedProduct = cachedProduct.copy(scannedAt = java.util.Date())
                scannedProductDao.insertScannedProduct(updatedProduct)
                return Result.success(updatedProduct.toDomainProduct())
            }

            // If not in cache, fetch from API
            val response = api.getProductByBarcode(barcode)
            if(response.status == 1 && response.product != null){
                val product = response.product
                
                // Calculate scale ratio based on product quantity vs 100g
                val totalGrams = product.productQuantity ?: product.quantityPerUnitValue ?: 100.0
                val scaleRatio = totalGrams / 100.0

                val entity = ScannedProductEntity(
                    barcode = barcode,
                    productName = product.productName,
                    brand = product.brand,
                    imageUrl = product.imageUrl,
                    calories = (product.nutriments?.calories ?: 0.0) * scaleRatio,
                    protein = (product.nutriments?.protein ?: 0.0) * scaleRatio,
                    carbs = (product.nutriments?.carbs ?: 0.0) * scaleRatio,
                    fat = (product.nutriments?.fat ?: 0.0) * scaleRatio,
                    fiber = product.nutriments?.fiber?.times(scaleRatio),
                    sugars = product.nutriments?.sugars?.times(scaleRatio)
                )
                // Save to cache table only (NOT to meal log)
                scannedProductDao.insertScannedProduct(entity)
                Result.success(entity.toDomainProduct())
            } else {
                Result.failure(Exception("Product not found in database"))
            }

        }catch(e:Exception){
            return Result.failure(e)
        }
    }

    override fun getScanHistory(): Flow<List<Product>> {
        return productDao.getAllScannedProducts().map {entity->
            entity.map { it.toDomainProduct() }
        }
    }

    override fun getMealsByDate(selectedDate: String): Flow<List<Product>> {
        return productDao.getMealsByDate(selectedDate).map { entities ->
            entities.map { it.toDomainProduct() }
        }
    }

    override suspend fun updateMealQuantity(barcode: String, newQuantity: Int) {
        productDao.updateProductQuantity(barcode, newQuantity)
    }

    override fun getDailySummary(selectedDate: String): Flow<DailyMacrosSummary?> {
        return productDao.getTodayTotalMacros(selectedDate)
    }

    override suspend fun deleteProduct(barcode: String) {
        return productDao.softDeleteProduct(barcode)
    }
}