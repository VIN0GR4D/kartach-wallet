// AddCardActivity.kt - Активность для добавления карты
package com.kartach.app

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class AddCardActivity : AppCompatActivity() {
    private lateinit var cardRepository: CardRepository
    private lateinit var storeNameEdit: EditText
    private lateinit var cardNumberEdit: EditText
    private lateinit var cardTypeSpinner: Spinner
    private lateinit var barcodeEdit: EditText
    private lateinit var scanButton: Button
    private lateinit var saveButton: Button

    private val scanLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val barcode = result.data?.getStringExtra("barcode")
            if (barcode != null) {
                barcodeEdit.setText(barcode)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_card)

        supportActionBar?.title = "Добавить карту"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        cardRepository = CardRepository.getInstance()

        initViews()
        setupSpinner()
        setupButtons()
    }

    private fun initViews() {
        storeNameEdit = findViewById(R.id.storeNameEdit)
        cardNumberEdit = findViewById(R.id.cardNumberEdit)
        cardTypeSpinner = findViewById(R.id.cardTypeSpinner)
        barcodeEdit = findViewById(R.id.barcodeEdit)
        scanButton = findViewById(R.id.scanButton)
        saveButton = findViewById(R.id.saveButton)
    }

    private fun setupSpinner() {
        val cardTypes = arrayOf("Скидочная", "Бонусная")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, cardTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        cardTypeSpinner.adapter = adapter
    }

    private fun setupButtons() {
        scanButton.setOnClickListener {
            // Запуск сканера штрихкода
            val intent = Intent(this, BarcodeScannerActivity::class.java)
            scanLauncher.launch(intent)
        }

        saveButton.setOnClickListener {
            saveCard()
        }
    }

    private fun saveCard() {
        val storeName = storeNameEdit.text.toString().trim()
        val cardNumber = cardNumberEdit.text.toString().trim()
        val cardType = cardTypeSpinner.selectedItem.toString()
        val barcode = barcodeEdit.text.toString().trim()

        if (storeName.isEmpty() || cardNumber.isEmpty()) {
            Toast.makeText(this, "Заполните обязательные поля", Toast.LENGTH_SHORT).show()
            return
        }

        val card = Card(
            storeName = storeName,
            cardNumber = cardNumber,
            cardType = cardType,
            barcode = barcode,
            barcodeFormat = "CODE_128" // Можно определять автоматически
        )

        cardRepository.insertCard(card)
        Toast.makeText(this, "Карта сохранена", Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}