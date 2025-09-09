package com.example.menuapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CartAdapter(private var cartItems: List<MenuItem>) :
    RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.cart_item_name)
        val priceTextView: TextView = view.findViewById(R.id.cart_item_price)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cart_item, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val menuItem = cartItems[position]
        holder.nameTextView.text = menuItem.name
        holder.priceTextView.text = "$${String.format("%.2f", menuItem.price)}"
    }

    override fun getItemCount() = cartItems.size

    fun updateData(newCartItems: List<MenuItem>) {
        this.cartItems = newCartItems
        notifyDataSetChanged()
    }
}
