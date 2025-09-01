package com.kartach.app

import android.content.Context
import com.kartach.app.database.AppDatabase
import com.kartach.app.database.CardDao
import com.kartach.app.database.CardEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class Card(
    val id: Long = 0,
    val storeName: String,
    val cardNumber: String,
    val cardType: String,
    val barcode: String,
    val barcodeFormat: String,
    val dateAdded: Long = System.currentTimeMillis()
)

class CardRepository private constructor(private val cardDao: CardDao) {
    
    fun getAllCards(): Flow<List<Card>> {
        return cardDao.getAllCards().map { entities ->
            entities.map { it.toCard() }
        }
    }
    
    suspend fun getAllCardsList(): List<Card> {
        return cardDao.getAllCardsList().map { it.toCard() }
    }
    
    suspend fun getCardById(id: Long): Card? {
        return cardDao.getCardById(id)?.toCard()
    }
    
    fun searchCards(query: String): Flow<List<Card>> {
        return cardDao.searchCards(query).map { entities ->
            entities.map { it.toCard() }
        }
    }
    
    suspend fun insertCard(card: Card): Long {
        return cardDao.insertCard(card.toEntity())
    }
    
    suspend fun updateCard(card: Card) {
        cardDao.updateCard(card.toEntity())
    }
    
    suspend fun deleteCard(card: Card) {
        cardDao.deleteCard(card.toEntity())
    }
    
    suspend fun deleteCardById(id: Long) {
        cardDao.deleteCardById(id)
    }
    
    private fun CardEntity.toCard(): Card {
        return Card(
            id = id,
            storeName = storeName,
            cardNumber = cardNumber,
            cardType = cardType,
            barcode = barcode,
            barcodeFormat = barcodeFormat,
            dateAdded = dateAdded
        )
    }
    
    private fun Card.toEntity(): CardEntity {
        return CardEntity(
            id = id,
            storeName = storeName,
            cardNumber = cardNumber,
            cardType = cardType,
            barcode = barcode,
            barcodeFormat = barcodeFormat,
            dateAdded = dateAdded
        )
    }
    
    companion object {
        @Volatile
        private var INSTANCE: CardRepository? = null
        
        fun getInstance(context: Context): CardRepository {
            return INSTANCE ?: synchronized(this) {
                val database = AppDatabase.getDatabase(context)
                val instance = CardRepository(database.cardDao())
                INSTANCE = instance
                instance
            }
        }
    }
}