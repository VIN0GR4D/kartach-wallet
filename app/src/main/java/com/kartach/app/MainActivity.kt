package com.kartach.app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var cardAdapter: CardAdapter
    private lateinit var emptyStateLayout: LinearLayout
    private lateinit var cardRepository: CardRepository
    private lateinit var searchEdit: TextInputEditText
    private lateinit var menuButton: ImageButton
    private lateinit var addFirstCardButton: MaterialButton

    private var allCards = listOf<Card>()

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            openAddCardActivity()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Инициализация репозитория
        cardRepository = CardRepository.getInstance(this)

        initViews()
        setupRecyclerView()
        setupSearch()
        setupButtons()
        loadCards()
    }

    private fun initViews() {
        recyclerView = findViewById(R.id.recyclerView)
        emptyStateLayout = findViewById(R.id.emptyStateLayout)
        searchEdit = findViewById(R.id.searchEdit)
        menuButton = findViewById(R.id.menuButton)
        addFirstCardButton = findViewById(R.id.addFirstCardButton)
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)

        cardAdapter = CardAdapter(
            onCardClick = { card ->
                // Открыть карту для показа
                val intent = Intent(this, CardDisplayActivity::class.java)
                intent.putExtra("cardId", card.id)
                startActivity(intent)
            },
            onCardMenuClick = { card ->
                showCardMenu(card)
            }
        )
        recyclerView.adapter = cardAdapter
    }

    private fun setupSearch() {
        searchEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                filterCards(s.toString())
            }
        })
    }

    private fun setupButtons() {
        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            checkCameraPermissionAndAdd()
        }

        addFirstCardButton.setOnClickListener {
            checkCameraPermissionAndAdd()
        }

        menuButton.setOnClickListener {
            showMainMenu()
        }
    }

    private fun filterCards(query: String) {
        val filteredCards = if (query.isBlank()) {
            allCards
        } else {
            allCards.filter { card ->
                card.storeName.contains(query, ignoreCase = true) ||
                        card.cardNumber.contains(query, ignoreCase = true) ||
                        card.cardType.contains(query, ignoreCase = true)
            }
        }

        cardAdapter.updateCards(filteredCards)
        updateEmptyState(filteredCards.isEmpty())
    }

    private fun showCardMenu(card: Card) {
        AlertDialog.Builder(this)
            .setTitle(card.storeName)
            .setItems(arrayOf(
                "Редактировать",
                "Удалить"
            )) { _, which ->
                when (which) {
                    0 -> editCard(card)
                    1 -> deleteCard(card)
                }
            }
            .show()
    }

    private fun editCard(card: Card) {
        // Открыть экран редактирования
        val intent = Intent(this, AddCardActivity::class.java)
        intent.putExtra("editCardId", card.id)
        startActivity(intent)
    }

    private fun deleteCard(card: Card) {
        AlertDialog.Builder(this)
            .setTitle("Удалить карту?")
            .setMessage("Карта ${card.storeName} будет удалена безвозвратно")
            .setPositiveButton("Удалить") { _, _ ->
                lifecycleScope.launch {
                    cardRepository.deleteCard(card)
                    loadCards()
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun showMainMenu() {
        AlertDialog.Builder(this)
            .setTitle("Меню")
            .setItems(arrayOf(
                "О приложении",
                "Настройки",
                "Поддержка"
            )) { _, which ->
                when (which) {
                    0 -> showAboutDialog()
                    1 -> openSettings()
                    2 -> openSupport()
                }
            }
            .show()
    }

    private fun showAboutDialog() {
        AlertDialog.Builder(this)
            .setTitle("О приложении")
            .setMessage("Картач - приложение для хранения карт лояльности\n\nВерсия: 1.0\nРазработчик: Ваше имя")
            .setPositiveButton("OK", null)
            .show()
    }

    private fun openSettings() {
        // Здесь можно открыть экран настроек
    }

    private fun openSupport() {
        // Здесь можно открыть экран поддержки или отправить email
    }

    private fun checkCameraPermissionAndAdd() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                openAddCardActivity()
            }
            else -> {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun openAddCardActivity() {
        val intent = Intent(this, AddCardActivity::class.java)
        startActivity(intent)
    }

    private fun loadCards() {
        lifecycleScope.launch {
            allCards = cardRepository.getAllCardsList()
            filterCards(searchEdit.text.toString())
        }
    }

    private fun updateEmptyState(isEmpty: Boolean) {
        if (isEmpty && allCards.isEmpty()) {
            // Нет карт вообще
            recyclerView.visibility = View.GONE
            emptyStateLayout.visibility = View.VISIBLE
        } else if (isEmpty && allCards.isNotEmpty()) {
            // Есть карты, но поиск ничего не нашел
            recyclerView.visibility = View.GONE
            emptyStateLayout.visibility = View.VISIBLE
            // Можно изменить текст для случая пустого поиска
        } else {
            // Есть карты для показа
            recyclerView.visibility = View.VISIBLE
            emptyStateLayout.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        loadCards()
    }
}