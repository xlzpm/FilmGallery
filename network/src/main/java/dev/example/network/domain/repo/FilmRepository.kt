package dev.example.network.domain.repo

import dev.example.network.domain.model.Films
import retrofit2.http.GET

interface FilmApiService {
    @GET("films.json")
    suspend fun getFilms(): Films
}