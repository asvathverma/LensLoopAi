package com.example.models

data class LocationTag(
    val id: String,
    val name: String,
    val address: String,
    val category: String,
    val latitude: Double,
    val longitude: Double,
    val isCityLevel: Boolean = false
)
