package edu.nd.pmcburne.hwapp.one.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface GameDao {

    // get games when device does not have internet:
    // https://divyanshutw.medium.com/how-to-make-an-offline-cache-in-android-using-room-database-and-mvvm-architecture-6d1b011e819c
    @Query(
        """
        SELECT * FROM games
        WHERE date = :date AND gender = :gender
        ORDER BY startTimeEpoch ASC
        """
    )
    suspend fun getGamesForDateAndGender(date: String, gender: String): List<GameEntity>

    // save scores after downloading from API
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGames(games: List<GameEntity>)

    // overwrite outdated scores with new ones
    @Query("DELETE FROM games WHERE date = :date AND gender = :gender")
    suspend fun deleteGamesForDateAndGender(date: String, gender: String)
}