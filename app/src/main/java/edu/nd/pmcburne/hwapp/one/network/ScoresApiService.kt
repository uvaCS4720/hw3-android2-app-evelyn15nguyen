package edu.nd.pmcburne.hwapp.one.network

import edu.nd.pmcburne.hwapp.one.model.ScoreboardResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

// Search: How to turn HTTP api into interface: 
// https://square.github.io/retrofit/

interface ScoresApiService {

    @GET("scoreboard/basketball-{gender}/d1/{year}/{month}/{day}")
    suspend fun getScores(
        @Path("gender") gender: String,
        @Path("year") year: String,
        @Path("month") month: String,
        @Path("day") day: String
    ): ScoreboardResponse

    companion object {
        private const val BASE_URL = "https://ncaa-api.henrygd.me/"

        fun create(): ScoresApiService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ScoresApiService::class.java)
        }
    }
}