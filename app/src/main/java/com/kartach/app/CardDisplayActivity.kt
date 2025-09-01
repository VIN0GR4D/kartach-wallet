// CardDisplayActivity.kt - Показ карты на кассе
package com.kartach.app

import android.graphics.Bitmap
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.zxing.BarcodeFormat
import kotlinx.coroutines.launch
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder

class CardDisplayActivity : AppCompatActivity() {
    private lateinit var cardRepository: CardRepository
    private lateinit var storeNameText: TextView
    private lateinit var cardNumberText: TextView
    private lateinit var cardTypeText: TextView
    private lateinit var barcodeImage: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_display)

        supportActionBar?.title = "Карта магазина"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        cardRepository = CardRepository.getInstance(this)

        initViews()
        loadCard()

        // Увеличить яркость экрана для лучшего сканирования
        window.attributes = window.attributes.apply {
            screenBrightness = 1.0f
        }
    }

    private fun initViews() {
        storeNameText = findViewById(R.id.storeNameDisplay)
        cardNumberText = findViewById(R.id.cardNumberDisplay)
        cardTypeText = findViewById(R.id.cardTypeDisplay)
        barcodeImage = findViewById(R.id.barcodeImage)
    }

    private fun loadCard() {
        val cardId = intent.getLongExtra("cardId", -1)
        if (cardId == -1L) {
            finish()
            return
        }

        lifecycleScope.launch {
            val card = cardRepository.getCardById(cardId)
            card?.let { displayCard(it) } ?: finish()
        }
    }

    private fun displayCard(card: Card) {
        storeNameText.text = card.storeName
        cardNumberText.text = "№ ${card.cardNumber}"
        cardTypeText.text = "${card.cardType} карта"

        if (card.barcode.isNotEmpty()) {
            generateBarcode(card.barcode, card.barcodeFormat)
        }
    }

    private fun generateBarcode(text: String, formatString: String) {
        try {
            val format = when (formatString) {
                "CODE_128" -> BarcodeFormat.CODE_128
                "CODE_39" -> BarcodeFormat.CODE_39
                "EAN_13" -> BarcodeFormat.EAN_13
                "EAN_8" -> BarcodeFormat.EAN_8
                "QR_CODE" -> BarcodeFormat.QR_CODE
                else -> BarcodeFormat.CODE_128
            }

            val multiFormatWriter = MultiFormatWriter()
            val bitMatrix = multiFormatWriter.encode(text, format, 800, 200)
            val barcodeEncoder = BarcodeEncoder()
            val bitmap: Bitmap = barcodeEncoder.createBitmap(bitMatrix)
            barcodeImage.setImageBitmap(bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}