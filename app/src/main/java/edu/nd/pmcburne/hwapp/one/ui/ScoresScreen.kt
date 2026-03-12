package edu.nd.pmcburne.hwapp.one.ui

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import edu.nd.pmcburne.hwapp.one.data.GameEntity
import edu.nd.pmcburne.hwapp.one.model.Gender
import edu.nd.pmcburne.hwapp.one.viewmodel.ScoresUiState
import java.util.Calendar

// Search: how to use pull to refresh
// https://developer.android.com/develop/ui/compose/components/pull-to-refresh

@Composable
fun ScoresScreen(
    uiState: ScoresUiState,
    onDateSelected: (String) -> Unit,
    onGenderSelected: (Gender) -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val (year, month, day) = parseDate(uiState.selectedDate)

    val datePickerDialog = remember(uiState.selectedDate) {
        DatePickerDialog(
            context,
            { _, selectedYear, selectedMonth, selectedDay ->
                val formatted = String.format(
                    "%04d-%02d-%02d",
                    selectedYear,
                    selectedMonth + 1,
                    selectedDay
                )
                onDateSelected(formatted)
            },
            year,
            month - 1,
            day
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Basketball Scores",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { datePickerDialog.show() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = formatDateForDisplay(uiState.selectedDate),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                Button(
                    onClick = onRefresh,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh"
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text("Refresh")
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = uiState.selectedGender == Gender.MEN,
                    onClick = { onGenderSelected(Gender.MEN) },
                    label = { Text("Men") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                        containerColor = MaterialTheme.colorScheme.surface,
                        labelColor = MaterialTheme.colorScheme.onSurface
                    )
                )

                FilterChip(
                    selected = uiState.selectedGender == Gender.WOMEN,
                    onClick = { onGenderSelected(Gender.WOMEN) },
                    label = { Text("Women") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                        containerColor = MaterialTheme.colorScheme.surface,
                        labelColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            }

            if (uiState.isOffline) {
                Text(
                    text = "Offline mode: showing saved scores if available.",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            uiState.message?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (uiState.games.isEmpty() && !uiState.isLoading) {
                Text(
                    text = "No games found for this date.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.games) { game ->
                    GameCard(
                        game = game,
                        selectedGender = uiState.selectedGender
                    )
                }
            }
        }

        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun GameCard(
    game: GameEntity,
    selectedGender: Gender
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "${game.awayTeam} @ ${game.homeTeam}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = buildStatusLine(game, selectedGender),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )

            if (game.gameState == "pre") {
                Text(
                    text = "Starts at ${game.startTime}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            } else {
                TeamScoreRow(label = "Away", team = game.awayTeam, score = game.awayScore)
                TeamScoreRow(label = "Home", team = game.homeTeam, score = game.homeScore)
            }

            if (game.gameState == "final") {
                Text(
                    text = "Winner: ${winnerName(game)}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun TeamScoreRow(
    label: String,
    team: String,
    score: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$label: $team",
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = score.ifBlank { "-" },
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

private fun buildStatusLine(game: GameEntity, gender: Gender): String {
    return when (game.gameState) {
        "pre" -> "UPCOMING"
        "live" -> "LIVE • ${formatPeriod(game.currentPeriod, gender)} • ${game.contestClock} remaining"
        "final" -> "FINAL"
        else -> game.gameState.uppercase()
    }
}

private fun formatPeriod(period: String, gender: Gender): String {
    if (period.equals("FINAL", ignoreCase = true)) return "Final"

    return when (gender) {
        Gender.MEN -> when (period.lowercase()) {
            "1st" -> "1st Half"
            "2nd" -> "2nd Half"
            else -> period
        }

        Gender.WOMEN -> when (period.lowercase()) {
            "1st" -> "1st Quarter"
            "2nd" -> "2nd Quarter"
            "3rd" -> "3rd Quarter"
            "4th" -> "4th Quarter"
            else -> period
        }
    }
}

private fun winnerName(game: GameEntity): String {
    return when {
        game.awayWinner -> game.awayTeam
        game.homeWinner -> game.homeTeam
        else -> "Unknown"
    }
}

private fun parseDate(date: String): Triple<Int, Int, Int> {
    val parts = date.split("-")
    return Triple(
        parts[0].toInt(),
        parts[1].toInt(),
        parts[2].toInt()
    )
}

private fun formatDateForDisplay(date: String): String {
    val (year, month, day) = parseDate(date)
    Calendar.getInstance().apply {
        set(year, month - 1, day)
    }

    val monthName = when (month) {
        1 -> "Jan"
        2 -> "Feb"
        3 -> "Mar"
        4 -> "Apr"
        5 -> "May"
        6 -> "Jun"
        7 -> "Jul"
        8 -> "Aug"
        9 -> "Sep"
        10 -> "Oct"
        11 -> "Nov"
        12 -> "Dec"
        else -> ""
    }

    return "$monthName $day, $year"
}