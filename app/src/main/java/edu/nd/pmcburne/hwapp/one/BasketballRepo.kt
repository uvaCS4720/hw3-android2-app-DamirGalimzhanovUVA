package edu.nd.pmcburne.hwapp.one

import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.time.ZoneId
import java.util.Date
import androidx.room.*
import android.content.Context
import java.time.format.DateTimeFormatter

class BasketballRepo (
    private val api: ApiService,
    private val gameDao: GameDao
){

    suspend fun getGamesFromLocal(showMen: Boolean, date: Date): List<Game>{
        val gender = if (showMen) "men" else "women"
        val dateString = date
            .toInstant()
            .atZone(ZoneId.of("America/New_York"))
            .toLocalDate()
            .format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))
        val games = gameDao.getGamesByDateAndGender(dateString, gender)
        return games;
    }

    suspend fun pullGamesFromRemote(showMen: Boolean, date: Date) {
        val gender = if (showMen) "men" else "women"
        val extractableDate = date
            .toInstant()
            .atZone(ZoneId.of("America/New_York"))
            .toLocalDate()
        val year = extractableDate.year
        val month = extractableDate.month.value
        val day = extractableDate.dayOfMonth

        try {
            val gamesDTO = api.getGames(gender, year, month, day)
            for (game in gamesDTO.games) {
                val winner: String? = if (game.home.winner) {
                    game.home.names.name
                } else if (game.away.winner) {
                    game.away.names.name
                } else {
                    null
                }
                val newGame = Game(
                    gameID = game.gameID.toInt(),
                    homeTeam = game.home.names.name,
                    awayTeam = game.away.names.name,
                    gameStatus = game.gameState,
                    homeTeamScore = game.home.score.toIntOrNull(),
                    awayTeamScore = game.away.score.toIntOrNull(),
                    winner = winner,
                    startDate = game.startDate,
                    startTime = game.startTime,
                    gender = gender
                )
                gameDao.insertGame(newGame);
            }
        } catch (e: Exception) {
            println("Error: ${e.message}")
        }

    }






}