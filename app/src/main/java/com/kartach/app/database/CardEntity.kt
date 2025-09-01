package com.kartach.app.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cards")
data class CardEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    @ColumnInfo(name = "store_name")
    val storeName: String,
    
    @ColumnInfo(name = "card_number")
    val cardNumber: String,
    
    @ColumnInfo(name = "card_type")
    val cardType: String,
    
    @ColumnInfo(name = "barcode")
    val barcode: String,
    
    @ColumnInfo(name = "barcode_format")
    val barcodeFormat: String,
    
    @ColumnInfo(name = "date_added")
    val dateAdded: Long = System.currentTimeMillis()
)