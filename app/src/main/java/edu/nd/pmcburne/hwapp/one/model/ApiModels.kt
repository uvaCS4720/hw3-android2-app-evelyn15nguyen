package edu.nd.pmcburne.hwapp.one.model

import com.google.gson.annotations.SerializedName

data class ScoreboardResponse(
    @SerializedName("games")
    val games: List<GameWrapper>
)

data class GameWrapper(
    @SerializedName("game")
    val game: ApiGame
)

data class ApiGame(
    @SerializedName("gameID")
    val gameId: String,

    @SerializedName("away")
    val away: ApiTeam,

    @SerializedName("home")
    val home: ApiTeam,

    @SerializedName("startTime")
    val startTime: String,

    @SerializedName("startTimeEpoch")
    val startTimeEpoch: String,

    @SerializedName("gameState")
    val gameState: String,

    @SerializedName("currentPeriod")
    val currentPeriod: String,

    @SerializedName("contestClock")
    val contestClock: String
)

data class ApiTeam(
    @SerializedName("score")
    val score: String,

    @SerializedName("winner")
    val winner: Boolean,

    @SerializedName("names")
    val names: TeamNames
)

data class TeamNames(
    @SerializedName("short")
    val short: String
)