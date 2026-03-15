package edu.nd.pmcburne.hwapp.one

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface GameDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGame(game: Game)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGames(games: List<Game>)

    @Query("SELECT * FROM games WHERE startDate = :startDate AND gender = :gender")
    suspend fun getGamesByDateAndGender(startDate: String, gender: String) : List<Game>
}