package com.example.menuapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class MenuAdapter(private val menuItems: List<MenuItem>) :
    RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    class MenuViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.item_name)
        val descriptionTextView: TextView = view.findViewById(R.id.item_description)
        val priceTextView: TextView = view.findViewById(R.id.item_price)
        val addToCartButton: Button = view.findViewById(R.id.add_to_cart_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.menu_item, parent, false)
        return MenuViewHolder(view)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val menuItem = menuItems[position]
        holder.nameTextView.text = menuItem.name
        holder.descriptionTextView.text = menuItem.description
        holder.priceTextView.text = "$${menuItem.price}"
        holder.addToCartButton.setOnClickListener {
            Cart.addItem(menuItem)
            Toast.makeText(holder.itemView.context, "${menuItem.name} added to cart", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount() = menuItems.size
}
