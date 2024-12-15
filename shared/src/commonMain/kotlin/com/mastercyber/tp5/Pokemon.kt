package com.mastercyber.tp5

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class Pokemon {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
    }

    suspend fun fetchPokemon(): String? {
        val random = (1..1025).random()
        return try {
            val response: Pokemon = client.get("https://tyradex.vercel.app/api/v1/pokemon/$random").body()
            response.name?.fr ?: "Unknown"
        } catch (e: Exception) {
            "Unknown"
        } finally {
            client.close()
        }
    }

    @Serializable
    data class Pokemon(
        val pokedex_id: String? = null,
        val category: String? = null,
        val name: PokemonName? = null,
        val sprites: Sprites? = null
    )

    @Serializable
    data class Sprites(
        val regular: String? = null,
        val shiny: String? = null,
        val gmax: String? = null
    )

    @Serializable
    data class PokemonName(
        val fr: String? = null,
        val en: String? = null,
        val jp: String? = null
    )
}