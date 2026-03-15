package edu.nd.pmcburne.hwapp.one

import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    @GET("basketball-{gender}/d1/{year}/{month}/{day}")
    suspend fun getGames(
        @Path("gender") gender: String,
        @Path("year") year: Int,
        @Path("month") month: Int,
        @Path("day") day: Int,
    ) : GamesDTO

}