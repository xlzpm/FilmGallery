package dev.example.filmgallery.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.example.filmgallery.domain.state.MainScreenState
import dev.example.network.domain.repo.FilmApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AppViewModel(
    private val filmApiService: FilmApiService
): ViewModel() {
    private val _mainScreenState = MutableStateFlow<MainScreenState>(MainScreenState.Loading)
    val mainScreenState = _mainScreenState.asStateFlow()

    var selectedGenre = MutableStateFlow<String?>(null)

    init {
        loadingAllFilms()
        Log.d("AppViewModel", "ViewModel created")
    }

    fun toggleGenreFilter(genre: String?) {
        if (selectedGenre.value == genre) {
            selectedGenre.value = null
        } else {
            selectedGenre.value = genre
        }
        loadingAllFilms(selectedGenre.value)
        Log.d("GenreFilter", "Selected genre: ${selectedGenre.value}")
    }

    private fun loadingAllFilms(selectedGenre: String? = null) {
        viewModelScope.launch {
            _mainScreenState.value = MainScreenState.Loading
            try {
                val films = filmApiService.getFilms().films
                val filteredFilms = selectedGenre?.let {
                    films.filter { film -> film.genres.contains(it) }
                } ?: films
                _mainScreenState.value = MainScreenState.Success(filteredFilms)
            } catch (e: Exception) {
                Log.e("loadingAllFilms", e.toString())
                _mainScreenState.value = MainScreenState.Error
            }
        }
    }

    fun retryLoading(selectedGenre: String? = null){
        _mainScreenState.value = MainScreenState.Loading
        loadingAllFilms(selectedGenre)
    }

    fun getGenres(): List<String>{
        return listOf("Биография", "Боевик", "Детектив", "Драма", "Комедия", "Криминал",    "Мелодрама",
            "Мюзикл", "Приключение", "Триллер", "Ужасы", "Фантастика")
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("AppViewModel", "Clear")
    }
}