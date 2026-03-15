package edu.nd.pmcburne.hwapp.one

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [Index(value = ["gameID", "gender"], unique = true)],
    tableName = "games"
)
data class Game(
    @PrimaryKey(autoGenerate = true)
    val id: Int=0,
    val gameID: Int,
    val homeTeam: String,
    val awayTeam: String,
    val gameStatus: String,
    val homeTeamScore: Int?,
    val awayTeamScore: Int?,
    val winner: String?,
    val startDate: String,
    val startTime: String,
    val gender: String,
)
