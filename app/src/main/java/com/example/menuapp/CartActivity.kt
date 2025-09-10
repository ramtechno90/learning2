package com.example.menuapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar

class CartActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var totalPriceTextView: TextView
    private lateinit var cartAdapter: CartAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recyclerView = findViewById(R.id.cart_recycler_view)
        totalPriceTextView = findViewById(R.id.total_price_text_view)

        cartAdapter = CartAdapter(Cart.getCartItems())
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = cartAdapter

        updateTotal()

        val placeOrderButton = findViewById<Button>(R.id.place_order_button)
        placeOrderButton.setOnClickListener {
            if (Cart.getCartItems().isNotEmpty()) {
                Toast.makeText(this, "Order placed successfully!", Toast.LENGTH_SHORT).show()
                Cart.clearCart()
                finish()
            } else {
                Toast.makeText(this, "Your cart is empty.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        cartAdapter.updateData(Cart.getCartItems())
        updateTotal()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun updateTotal() {
        totalPriceTextView.text = "Total: $${String.format("%.2f", Cart.getTotalPrice())}"
    }
}
