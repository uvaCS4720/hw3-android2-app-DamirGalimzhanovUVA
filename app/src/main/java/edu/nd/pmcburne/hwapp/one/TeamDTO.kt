package edu.nd.pmcburne.hwapp.one

import kotlinx.serialization.Serializable

@Serializable
data class TeamDTO(
    val score: String,
    val names: TeamNameDTO,
    val winner: Boolean
)
