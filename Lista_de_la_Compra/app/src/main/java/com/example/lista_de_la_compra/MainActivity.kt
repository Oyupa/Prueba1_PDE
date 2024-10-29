package com.example.lista_de_la_compra

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.EditText
import android.widget.TextView
import android.widget.Button
import kotlin.math.round

class MainActivity : ComponentActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ShoppingListAdapter
    private lateinit var totalItemsText: TextView
    private lateinit var productNameEditText: EditText
    private lateinit var productQuantityEditText: EditText
    private lateinit var productPriceEditText: EditText
    private lateinit var addProductButton: Button

    private val sharedPreferences by lazy { getSharedPreferences("shoppingList", Context.MODE_PRIVATE) }
    private val products = mutableListOf<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        totalItemsText = findViewById(R.id.totalItemsText)
        productNameEditText = findViewById(R.id.productNameEditText)
        productQuantityEditText = findViewById(R.id.productQuantityEditText)
        productPriceEditText = findViewById(R.id.productPriceEditText)
        addProductButton = findViewById(R.id.addProductButton)

        adapter = ShoppingListAdapter(products)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        loadProducts()
        updateSummary()

        addProductButton.setOnClickListener {
            val name = productNameEditText.text.toString()
            val quantity = productQuantityEditText.text.toString().toIntOrNull() ?: 1
            val price = productPriceEditText.text.toString().toDoubleOrNull() ?: 0.0

            if (name.isNotEmpty()) {
                val product = Product(name, quantity, price)
                products.add(product)
                adapter.notifyDataSetChanged()
                saveProducts()
                updateSummary()
                clearInputs()
            }
        }
    }

    private fun saveProducts() {
        val editor = sharedPreferences.edit()
        val productListString = products.joinToString(";") { "${it.name},${it.quantity},${it.price}" }
        editor.putString("products", productListString)
        editor.apply()
    }

    private fun loadProducts() {
        sharedPreferences.getString("products", "")?.split(";")?.forEach { productString ->
            val parts = productString.split(",")
            if (parts.size == 3) {
                val name = parts[0]
                val quantity = parts[1].toIntOrNull() ?: 1
                val price = parts[2].toDoubleOrNull() ?: 0.0
                products.add(Product(name, quantity, price))
            }
        }
        adapter.notifyDataSetChanged()
    }

    private fun updateSummary() {
        val totalItems = products.size
        val totalPrice = products.sumOf { it.price * it.quantity }
        totalItemsText.text = "Productos: $totalItems, Precio Total: ${round(totalPrice * 100) / 100}€"
    }

    private fun clearInputs() {
        productNameEditText.text.clear()
        productQuantityEditText.text.clear()
        productPriceEditText.text.clear()
    }
}

data class Product(val name: String, val quantity: Int, val price: Double)
