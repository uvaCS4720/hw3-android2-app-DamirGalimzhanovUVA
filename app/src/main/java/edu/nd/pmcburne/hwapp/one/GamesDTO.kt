package edu.nd.pmcburne.hwapp.one

import kotlinx.serialization.Serializable

@Serializable
data class GamesDTO(
    val games: List<GameDTO>
)
