package edu.nd.pmcburne.hwapp.one

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

class BasketballApp : Application() {

    lateinit var db: AppDatabase

    lateinit var api: ApiService

    lateinit var repo: BasketballRepo

    val json = Json {
        ignoreUnknownKeys = true
    }

    override fun onCreate() {
        super.onCreate()

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "games-database"
        ).build()

        api = Retrofit.Builder()
            .baseUrl("https://ncaa-api.henrygd.me/scoreboard/")
            .addConverterFactory(
                json.asConverterFactory("application/json".toMediaType())
            )
            .build()
            .create(ApiService::class.java)

        repo = BasketballRepo(api, db.gameDao())

    }


}