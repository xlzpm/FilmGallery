package dev.example.filmgallery.domain.state

import dev.example.network.domain.model.Film

sealed class MainScreenState {
    data object Loading: MainScreenState()
    data class Success(val films: List<Film>): MainScreenState()
    data object Error: MainScreenState()
}