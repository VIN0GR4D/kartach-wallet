// CardRepository.kt - Простое хранение в памяти
package com.kartach.app

// Упрощенная модель карты без Room
data class Card(
    val id: Long = System.currentTimeMillis(),
    val storeName: String,
    val cardNumber: String,
    val cardType: String,
    val barcode: String,
    val barcodeFormat: String,
    val dateAdded: Long = System.currentTimeMillis()
)

// Простой репозиторий для хранения данных в памяти
class CardRepository {
    private val cards = mutableListOf<Card>()
    private var nextId = 1L

    fun getAllCards(): List<Card> {
        return cards.toList()
    }

    fun getCardById(id: Long): Card? {
        return cards.find { it.id == id }
    }

    fun insertCard(card: Card): Long {
        val newCard = card.copy(id = nextId++)
        cards.add(newCard)
        return newCard.id
    }

    fun updateCard(card: Card) {
        val index = cards.indexOfFirst { it.id == card.id }
        if (index != -1) {
            cards[index] = card
        }
    }

    fun deleteCard(card: Card) {
        cards.removeAll { it.id == card.id }
    }

    companion object {
        @Volatile
        private var INSTANCE: CardRepository? = null

        fun getInstance(): CardRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = CardRepository()
                INSTANCE = instance
                instance
            }
        }
    }
}