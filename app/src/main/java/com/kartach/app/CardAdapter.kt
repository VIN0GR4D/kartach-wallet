package com.kartach.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CardAdapter(
    private val onCardClick: (Card) -> Unit,
    private val onCardMenuClick: (Card) -> Unit = {}
) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    private var cards = listOf<Card>()

    fun updateCards(newCards: List<Card>) {
        cards = newCards
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_card, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val card = cards[position]
        holder.bind(card)
    }

    override fun getItemCount() = cards.size

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val storeNameText: TextView = itemView.findViewById(R.id.storeName)
        private val cardTypeText: TextView = itemView.findViewById(R.id.cardType)
        private val cardNumberText: TextView = itemView.findViewById(R.id.cardNumber)
        private val storeIconText: TextView = itemView.findViewById(R.id.storeIcon)
        private val barcodeIndicator: ImageView = itemView.findViewById(R.id.barcodeIndicator)
        private val cardMenuButton: ImageButton = itemView.findViewById(R.id.cardMenuButton)

        fun bind(card: Card) {
            storeNameText.text = card.storeName
            cardTypeText.text = "${card.cardType} карта"

            // Форматирование номера карты для лучшего отображения
            val formattedNumber = formatCardNumber(card.cardNumber)
            cardNumberText.text = formattedNumber

            // Первая буква названия магазина как иконка
            storeIconText.text = card.storeName.firstOrNull()?.toString()?.uppercase() ?: "?"

            // Показать индикатор штрихкода если он есть
            barcodeIndicator.visibility = if (card.barcode.isNotEmpty()) View.VISIBLE else View.GONE

            // Обработчики кликов
            itemView.setOnClickListener {
                onCardClick(card)
            }

            cardMenuButton.setOnClickListener {
                onCardMenuClick(card)
            }
        }

        private fun formatCardNumber(number: String): String {
            return when {
                number.length <= 4 -> number
                number.length <= 8 -> "•••• ${number.takeLast(4)}"
                number.length <= 12 -> "•••• •••• ${number.takeLast(4)}"
                else -> "•••• •••• •••• ${number.takeLast(4)}"
            }
        }
    }
}