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
import android.util.Log
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
        val year = extractableDate.year.toString()
        val month = extractableDate.monthValue.toString().padStart(2, '0')
        val day = extractableDate.dayOfMonth.toString().padStart(2, '0')
        val requestedDateString = extractableDate.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))

        try {
            Log.d("Repo", "requested date = $month/$day/$year")
            val gamesDTO = api.getGames(gender, year, month, day)
            Log.d("Repo", "api returned ${gamesDTO.games.size} games")
            for (gameWrapper in gamesDTO.games) {
                val game = gameWrapper.game
                Log.d("Repo", "loop game startDate=${game.startDate}, id=${game.gameID}, home=${game.home.names.name}")
                if (game.startDate != requestedDateString) continue

                val winner = when {
                    game.home.winner -> game.home.names.name
                    game.away.winner -> game.away.names.name
                    else -> null
                }

                val newGame = Game(
                    gameID = game.gameID.toIntOrNull() ?: continue,
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

                gameDao.insertGame(newGame)
                Log.d("Repo", "inserted game id=${newGame.gameID} startDate=${newGame.startDate}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }






}