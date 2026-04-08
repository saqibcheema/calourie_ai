package com.example.calorieapp.data.DataSource.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.calorieapp.data.Models.ProductEntity
import com.example.calorieapp.domain.entities.DailyMacrosSummary
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    // Will insert a new row because ID is autoGenerate
    @Insert
    suspend fun insertProduct(product: ProductEntity)

    @Query("SELECT * FROM scanned_products WHERE isDeleted = 0 ORDER BY scannedAt DESC")
    fun getAllScannedProducts(): Flow<List<ProductEntity>>

    // Retrieve the most recent active product so we append quantity properly
    @Query("SELECT * FROM scanned_products WHERE barcode = :barcode AND isDeleted = 0 ORDER BY scannedAt DESC LIMIT 1")
    suspend fun getProductByBarcode(barcode: String): ProductEntity?

    @Query("UPDATE scanned_products SET isDeleted = 1 WHERE id = (SELECT id FROM scanned_products WHERE barcode = :barcode AND isDeleted = 0 ORDER BY scannedAt DESC LIMIT 1)")
    suspend fun softDeleteProduct(barcode: String)

    @Query("UPDATE scanned_products SET quantity = :quantity WHERE id = (SELECT id FROM scanned_products WHERE barcode = :barcode AND isDeleted = 0 ORDER BY scannedAt DESC LIMIT 1)")
    suspend fun updateProductQuantity(barcode: String, quantity: Int)

    @Query("DELETE FROM scanned_products WHERE isDeleted = 1")
    suspend fun deleteOldProducts()

    @Query("SELECT * FROM scanned_products WHERE isDeleted = 0 AND date(scannedAt / 1000, 'unixepoch', 'localtime') = :currentDate ORDER BY scannedAt DESC")
    fun getMealsByDate(currentDate: String): Flow<List<ProductEntity>>

    @Query("""
    SELECT 
        SUM(calories * quantity) as totalCalories, 
        SUM(protein * quantity) as totalProtein, 
        SUM(fat * quantity) as totalFats, 
        SUM(carbs * quantity) as totalCarbs 
    FROM scanned_products 
    WHERE isDeleted = 0 AND date(scannedAt / 1000, 'unixepoch', 'localtime') = :currentDate
""")
    fun getTodayTotalMacros(currentDate: String): Flow<DailyMacrosSummary?>

    @Query("""
    SELECT 
        date(scannedAt / 1000, 'unixepoch', 'localtime') as dateString,
        SUM(calories * quantity) as totalCalories, 
        SUM(protein * quantity) as totalProtein, 
        SUM(fat * quantity) as totalFats, 
        SUM(carbs * quantity) as totalCarbs 
    FROM scanned_products 
    WHERE isDeleted = 0 AND date(scannedAt / 1000, 'unixepoch', 'localtime') LIKE :monthPattern
    GROUP BY dateString
    ORDER BY dateString ASC
""")
    fun getMonthlyMacros(monthPattern: String): Flow<List<com.example.calorieapp.domain.entities.DateMacroSummary>>
    @Query("""
        SELECT DISTINCT date(scannedAt / 1000, 'unixepoch', 'localtime') as dateString
        FROM scanned_products
        WHERE isDeleted = 0
        ORDER BY dateString DESC
    """)
    fun getLoggedDates(): Flow<List<String>>
}