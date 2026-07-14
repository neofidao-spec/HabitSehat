package com.habitsehat.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habitsehat.app.ui.theme.StreakGreen
import com.habitsehat.app.ui.theme.StreakOrange
import java.time.LocalDate
import java.time.format.DateTimeFormatter

val dateFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd")

@Composable
fun HeatmapCalendar(
    dateToLevel: Map<String, Int>,
    streakCount: Int,
    monthsToShow: Int = 3
) {
    val today = LocalDate.now()
    val startDate = today.minusMonths(monthsToShow.toLong())

    val weeks = mutableListOf<List<LocalDate?>>()
    var cursor = startDate
    val dayOfWeek = cursor.dayOfWeek.value
    cursor = cursor.minusDays((dayOfWeek - 1).toLong())

    while (cursor <= today) {
        val week = mutableListOf<LocalDate?>()
        for (i in 0..6) {
            if (cursor < startDate || cursor > today) {
                week.add(null)
            } else {
                week.add(cursor)
            }
            cursor = cursor.plusDays(1)
        }
        weeks.add(week)
    }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 28.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            val months = weeks.flatten().filterNotNull().map { it.month.name.take(3) }.distinct().take(6)
            months.forEach { label ->
                Text(label, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.width(28.dp), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            }
        }

        Spacer(Modifier.height(4.dp))

        for (week in weeks) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val firstNonNull = week.firstOrNull { it != null }
                val dayLabel = if (firstNonNull != null && firstNonNull.dayOfWeek.value == 1) ""
                else firstNonNull?.let { dayNames[it.dayOfWeek.value] } ?: ""

                Text(dayLabel, fontSize = 8.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.width(24.dp))

                for (d in week) {
                    val boxColor = if (d == null) Color.Transparent
                    else if (d == today) StreakGreen
                    else dateLevelColor(dateToLevel[d.format(dateFmt)] ?: 0)

                    Box(
                        modifier = Modifier
                            .padding(1.dp)
                            .size(14.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(boxColor)
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text("Kurang", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                for (level in 0..4) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(dateLevelColor(level))
                    )
                }
                Text("Baik", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            if (streakCount > 0) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🔥", fontSize = 12.sp)
                    Spacer(Modifier.width(2.dp))
                    Text("$streakCount hari", fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
                        color = StreakOrange)
                }
            }
        }
    }
}

private val dayNames = mapOf(
    1 to "S", 2 to "S", 3 to "R", 4 to "K", 5 to "J", 6 to "S", 7 to "M"
)

private fun dateLevelColor(level: Int): Color = when (level) {
    0 -> Color(0xFFEBEDF0)
    1 -> Color(0xFF9BE9A8)
    2 -> Color(0xFF40C463)
    3 -> Color(0xFF30A14E)
    4 -> Color(0xFF216E39)
    else -> Color.Transparent
}

fun buildHeatmapData(
    dateToCount: Map<String, Int>,
    maxCount: Int = 4
): Map<String, Int> {
    return dateToCount.mapValues { (_, count) ->
        if (count == 0) 0
        else ((count.toFloat() / maxCount.coerceAtLeast(1)) * 4).toInt().coerceIn(0, 4)
    }
}
