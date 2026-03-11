package com.example.calorieapp.data.repository

import com.example.calorieapp.data.DataSource.local.ProductDao
import com.example.calorieapp.data.DataSource.remote.BarcodeApiService
import com.example.calorieapp.data.Models.ProductEntity
import com.example.calorieapp.data.Models.toDomainProduct
import com.example.calorieapp.data.Models.toEntity
import com.example.calorieapp.domain.entities.DailyMacrosSummary
import com.example.calorieapp.domain.entities.Product
import com.example.calorieapp.domain.repository.BarcodeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BarcodeRepositoryImpl @Inject constructor(
    private val productDao: ProductDao,
    private val api: BarcodeApiService
): BarcodeRepository {

    override suspend fun addMeal(meal: Product) {
        val product = meal.toEntity()
        productDao.insertProduct(product)
    }
    override suspend fun scanProduct(barcode: String): Result<Product> {
        return try {
            val response = api.getProductByBarcode(barcode)
            if(response.status == 1 && response.product != null){
                val product = response.product
                val entity = ProductEntity(
                    barcode = barcode,
                    productName = product.productName,
                    brand = product.brand,
                    imageUrl = product.imageUrl,
                    calories = product.nutriments?.calories ?: 0.0,
                    protein = product.nutriments?.protein ?: 0.0,
                    carbs = product.nutriments?.carbs ?: 0.0,
                    fat = product.nutriments?.fat ?: 0.0,
                    fiber = product.nutriments?.fiber,
                    sugars = product.nutriments?.sugars
                )
                productDao.insertProduct(entity)
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

    override fun getDailySummary(selectedDate: String): Flow<DailyMacrosSummary?> {
        return productDao.getTodayTotalMacros(selectedDate)
    }

    override suspend fun deleteProduct(barcode: String) {
        return productDao.softDeleteProduct(barcode)
    }
}