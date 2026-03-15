package edu.nd.pmcburne.hwapp.one

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TeamNameDTO(
    @SerialName("short")
    val name: String
)
