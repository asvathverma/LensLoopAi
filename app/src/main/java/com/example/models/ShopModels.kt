package com.example.models

data class Product(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val currency: String = "USD",
    val imageUrl: String,
    val merchant: UserShort,
    val inStock: Boolean = true
)

data class Order(
    val id: String,
    val products: List<Product>,
    val totalAmount: Double,
    val currency: String,
    val status: String,
    val trackingNumber: String? = null
)
