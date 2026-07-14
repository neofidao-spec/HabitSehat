package com.habitsehat.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habitsehat.app.data.model.Habit
import com.habitsehat.app.ui.theme.StreakGreen
import com.habitsehat.app.ui.theme.StreakOrange
import com.habitsehat.app.ui.theme.StreakRed
import com.habitsehat.app.ui.theme.WaterBlue

@Composable
fun WaterCard(
    total: Int,
    goal: Int,
    onAdd: (Int) -> Unit,
    onUndo: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = if (goal > 0) (total.toFloat() / goal).coerceIn(0f, 1f) else 0f

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Outlined.WaterDrop,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "Minum Air",
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }

            Spacer(Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    "${total}ml",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    "/ $goal",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                )
            }

            Spacer(Modifier.height(8.dp))

            // Progress bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(WaterBlue.copy(alpha = 0.15f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(6.dp))
                        .background(WaterBlue)
                )
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf(200, 300, 500).forEach { ml ->
                    FilledTonalButton(
                        onClick = { onAdd(ml) },
                        modifier = Modifier.height(40.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    ) {
                        Text("+${ml}ml", fontSize = 13.sp)
                    }
                }
            }

            if (total > 0) {
                Spacer(Modifier.height(4.dp))
                TextButton(onClick = onUndo) {
                    Icon(Icons.Outlined.Undo, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Undo", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun HabitItem(
    habit: Habit,
    isChecked: Boolean,
    currentCount: Int,
    onCheck: () -> Unit,
    onArchive: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor by animateColorAsState(
        targetValue = if (isChecked) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
        else MaterialTheme.colorScheme.surface,
        label = "bg"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isChecked) MaterialTheme.colorScheme.primary else Color.Transparent,
        label = "border"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { if (!isChecked) onCheck() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        border = if (isChecked) androidx.compose.foundation.BorderStroke(1.5.dp, borderColor) else null
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Check circle
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        if (isChecked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                    )
                    .clickable { onCheck() },
                contentAlignment = Alignment.Center
            ) {
                if (isChecked) {
                    Icon(
                        Icons.Filled.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                } else {
                    Text(
                        "${habit.targetCount}x",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    habit.name,
                    fontWeight = if (isChecked) FontWeight.SemiBold else FontWeight.Normal,
                    fontSize = 15.sp,
                    color = if (isChecked) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurface
                )
                if (habit.reminderEnabled) {
                    Text(
                        "${"%02d".format(habit.reminderHour)}:${
                            "%02d".format(habit.reminderMinute)
                        }",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Counter
            if (habit.targetCount > 1) {
                Text(
                    "$currentCount/${habit.targetCount}",
                    fontSize = 13.sp,
                    color = if (isChecked) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.width(8.dp))
            }

            // Streak
            if (isChecked) {
                Icon(
                    Icons.Filled.LocalFireDepartment,
                    contentDescription = null,
                    tint = StreakOrange,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun StreakBar(
    done: Int,
    total: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.LocalFireDepartment, contentDescription = null, tint = StreakOrange)
            Spacer(Modifier.width(8.dp))
            Text(
                if (total > 0) "$done dari $total kebiasaan selesai" else "Belum ada kebiasaan",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}
