package edu.nd.pmcburne.hwapp.one

import kotlinx.serialization.Serializable

@Serializable
data class GameDTO(
    val gameID: String,
    val home: TeamDTO,
    val away: TeamDTO,
    val gameState: String,
    val startDate: String,
    val startTime: String,
)
