package com.example.compose_dessert_clicker.ui.theme

import androidx.lifecycle.ViewModel
import com.example.compose_dessert_clicker.data.Datasource
import com.example.compose_dessert_clicker.model.Dessert
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ShopViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ShopUiState())
    val uiState = _uiState.asStateFlow()
    private val desserts = Datasource.dessertList
    private lateinit var currentDessert: Dessert

    init {
        setup()
    }

    private fun setup() {
        currentDessert = Datasource.dessertList.first()
        _uiState.value = ShopUiState(
            currentDessertImageId = currentDessert.imageId,
            currentDessertPrice = currentDessert.price
        )
    }

    fun sell() {

        // Update the revenue
        _uiState.update { currentState ->
            currentState.copy(
                revenue = currentState.revenue.plus(currentDessert.price),
                dessertsSold = currentState.dessertsSold.inc()
            )
        }

        // Show the next dessert
        currentDessert = determineDessertToShow(desserts, _uiState.value.dessertsSold)
        _uiState.update { currentState ->
            currentState.copy(
                currentDessertImageId = currentDessert.imageId,
                currentDessertPrice = currentDessert.price
            )
        }
    }

    /**
     * Determine which dessert to show.
     */
    private fun determineDessertToShow(desserts: List<Dessert>, dessertsSold: Int): Dessert {
        var dessertToShow = desserts.first()
        for (dessert in desserts) {
            if (dessertsSold >= dessert.startProductionAmount) {
                dessertToShow = dessert
            } else {
                // The list of desserts is sorted by startProductionAmount. As you sell more desserts,
                // you'll start producing more expensive desserts as determined by startProductionAmount
                // We know to break as soon as we see a dessert who's "startProductionAmount" is greater
                // than the amount sold.
                break
            }
        }
        return dessertToShow
    }
}