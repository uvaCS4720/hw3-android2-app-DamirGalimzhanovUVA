package edu.nd.pmcburne.hwapp.one

import kotlinx.serialization.Serializable

@Serializable
data class GameWrapperDTO(
    val game: GameDTO
)
