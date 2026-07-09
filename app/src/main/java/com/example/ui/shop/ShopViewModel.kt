package com.example.ui.shop

import androidx.lifecycle.ViewModel
import com.example.models.Product
import com.example.models.UserShort
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ShopState(
    val products: List<Product> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false
)

class ShopViewModel : ViewModel() {
    private val _state = MutableStateFlow(ShopState())
    val state: StateFlow<ShopState> = _state.asStateFlow()

    init {
        _state.update {
            it.copy(
                products = listOf(
                    Product("p1", "Smartphone 15", "Latest tech", 999.0, imageUrl = "https://example.com/phone.jpg", merchant = UserShort("u2", "mkbhd", null)),
                    Product("p2", "Wireless Earbuds", "Noise cancelling", 249.0, imageUrl = "https://example.com/buds.jpg", merchant = UserShort("u2", "mkbhd", null)),
                    Product("p3", "Smart Watch", "Track your fitness", 349.0, imageUrl = "https://example.com/watch.jpg", merchant = UserShort("u2", "mkbhd", null))
                )
            )
        }
    }

    fun getProduct(id: String): Product? {
        return _state.value.products.find { it.id == id }
    }
}
