package com.example.menuapp

object Cart {
    private val items = mutableListOf<MenuItem>()

    fun addItem(item: MenuItem) {
        items.add(item)
    }

    fun getCartItems(): List<MenuItem> {
        return items
    }

    fun clearCart() {
        items.clear()
    }

    fun getTotalPrice(): Double {
        return items.sumOf { it.price }
    }
}
