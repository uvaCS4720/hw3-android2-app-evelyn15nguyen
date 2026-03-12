package edu.nd.pmcburne.hwapp.one.data

import androidx.room.Entity
import edu.nd.pmcburne.hwapp.one.model.ApiGame
import edu.nd.pmcburne.hwapp.one.model.Gender

@Entity(
    tableName = "games",
    primaryKeys = ["gameId", "date", "gender"]
)
data class GameEntity(
    val gameId: String,
    val date: String,
    val gender: String,

    val awayTeam: String,
    val homeTeam: String,

    val awayScore: String,
    val homeScore: String,

    val awayWinner: Boolean,
    val homeWinner: Boolean,

    val gameState: String,
    val startTime: String,
    val startTimeEpoch: Long,

    val currentPeriod: String,
    val contestClock: String
)

fun ApiGame.toEntity(date: String, gender: Gender): GameEntity {
    return GameEntity(
        gameId = gameId,
        date = date,
        gender = gender.apiValue,
        awayTeam = away.names.short,
        homeTeam = home.names.short,
        awayScore = away.score,
        homeScore = home.score,
        awayWinner = away.winner,
        homeWinner = home.winner,
        gameState = gameState,
        startTime = startTime,
        startTimeEpoch = startTimeEpoch.toLongOrNull() ?: 0L,
        currentPeriod = currentPeriod,
        contestClock = contestClock
    )
}