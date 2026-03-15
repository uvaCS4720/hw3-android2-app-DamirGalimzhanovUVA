package edu.nd.pmcburne.hwapp.one

import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    @GET("basketball-{gender}/d1/{year}/{month}/{day}/all-conf")
    suspend fun getGames(
        @Path("gender") gender: String,
        @Path("year") year: String,
        @Path("month") month: String,
        @Path("day") day: String,
    ) : GamesDTO

}