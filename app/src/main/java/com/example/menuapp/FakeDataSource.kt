package com.example.menuapp

object FakeDataSource {
    fun getMenuItems(): List<MenuItem> {
        return listOf(
            MenuItem(1, "Margherita Pizza", "Classic pizza with tomato, mozzarella, and basil", 12.99),
            MenuItem(2, "Pepperoni Pizza", "Pizza with pepperoni and mozzarella", 14.99),
            MenuItem(3, "Spaghetti Carbonara", "Pasta with eggs, cheese, pancetta, and pepper", 15.99),
            MenuItem(4, "Lasagna", "Layers of pasta with meat sauce and cheese", 16.99),
            MenuItem(5, "Caesar Salad", "Romaine lettuce with Caesar dressing, croutons, and Parmesan cheese", 9.99),
            MenuItem(6, "Tiramisu", "Coffee-flavored Italian dessert", 7.99)
        )
    }
}
