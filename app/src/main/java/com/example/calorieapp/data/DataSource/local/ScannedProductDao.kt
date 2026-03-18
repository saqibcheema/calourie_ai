package com.example.calorieapp.data.DataSource.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.calorieapp.data.Models.ScannedProductEntity

@Dao
interface ScannedProductDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScannedProduct(product: ScannedProductEntity)

    @Query("SELECT * FROM scanned_products_cache WHERE barcode = :barcode LIMIT 1")
    suspend fun getScannedProductByBarcode(barcode: String): ScannedProductEntity?
}
