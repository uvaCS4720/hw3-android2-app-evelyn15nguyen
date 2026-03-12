package edu.nd.pmcburne.hwapp.one.repository

import androidx.room.withTransaction
import edu.nd.pmcburne.hwapp.one.data.AppDatabase
import edu.nd.pmcburne.hwapp.one.data.GameEntity
import edu.nd.pmcburne.hwapp.one.data.toEntity
import edu.nd.pmcburne.hwapp.one.model.Gender
import edu.nd.pmcburne.hwapp.one.network.ScoresApiService

class ScoresRepository(
    private val api: ScoresApiService,
    private val db: AppDatabase
) {
    private val dao = db.gameDao()

    suspend fun loadScores(date: String, gender: Gender): LoadScoresResult {
        return try {
            val (year, month, day) = date.split("-")
            val response = api.getScores(
                gender = gender.apiValue,
                year = year,
                month = month,
                day = day
            )

            val entities = response.games.map { wrapper ->
                wrapper.game.toEntity(date, gender)
            }

            db.withTransaction {
                dao.deleteGamesForDateAndGender(date, gender.apiValue)
                dao.insertGames(entities)
            }

            LoadScoresResult(
                games = dao.getGamesForDateAndGender(date, gender.apiValue),
                isOffline = false,
                errorMessage = null
            )
        } catch (e: Exception) {
            val localGames = dao.getGamesForDateAndGender(date, gender.apiValue)

            LoadScoresResult(
                games = localGames,
                isOffline = true,
                errorMessage = if (localGames.isEmpty()) {
                    "Could not load scores - No saved data is available for this date"
                } else {
                    "Showing saved scores because the network is unavailable"
                }
            )
        }
    }
}

data class LoadScoresResult(
    val games: List<GameEntity>,
    val isOffline: Boolean,
    val errorMessage: String?
)