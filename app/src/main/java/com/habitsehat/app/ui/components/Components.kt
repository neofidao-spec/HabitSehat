package com.habitsehat.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habitsehat.app.data.model.Habit
import com.habitsehat.app.ui.theme.StreakOrange
import com.habitsehat.app.ui.theme.WaterBlue
import kotlin.math.PI
import kotlin.math.sin

private val WaterBlueBg = Color(0xFFB3E5FC)
private val WaterBlueLight = Color(0xFF81D4FA)
private val WaterBlue60 = Color(0xFF42A5F5)
private val WaterBlueDark = Color(0xFF0D47A1)

// ──────────────────────────────────────
// WATER CARD — Circular Animated Progress
// ──────────────────────────────────────
@Composable
fun WaterCard(
    total: Int,
    goal: Int,
    onAdd: (Int) -> Unit,
    onUndo: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = if (goal > 0) (total.toFloat() / goal).coerceIn(0f, 1f) else 0f
    val animatedProgress = remember { Animatable(0f) }

    LaunchedEffect(progress) {
        animatedProgress.animateTo(
            targetValue = progress,
            animationSpec = spring(dampingRatio = 0.7f, stiffness = 100f)
        )
    }

    // Glow wave animation
    val infiniteTransition = rememberInfiniteTransition(label = "waterWave")
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wavePhase"
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = WaterBlueBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.WaterDrop, contentDescription = null, tint = WaterBlue60)
                Spacer(Modifier.width(8.dp))
                Text("Minum Air", fontWeight = FontWeight.SemiBold, color = WaterBlueDark)
            }

            Spacer(Modifier.height(16.dp))

            // Circular progress + quick buttons side by side
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Circular progress
                Box(
                    modifier = Modifier.size(130.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(Modifier.fillMaxSize()) {
                        val stroke = 10.dp.toPx()
                        val radius = (size.minDimension - stroke) / 2
                        val topLeft = Offset(
                            (size.width - radius * 2) / 2,
                            (size.height - radius * 2) / 2
                        )

                        // Background ring
                        drawCircle(
                            color = WaterBlueLight,
                            radius = radius,
                            center = center,
                            style = Stroke(width = stroke, cap = StrokeCap.Round)
                        )

                        // Animated progress arc with gradient
                        val sweepAngle = 360f * animatedProgress.value
                        val gradient = Brush.sweepGradient(
                            colors = listOf(
                                WaterBlue60.copy(alpha = 0.6f),
                                WaterBlue,
                                WaterBlueDark
                            ),
                            center = center
                        )
                        drawArc(
                            brush = gradient,
                            startAngle = -90f,
                            sweepAngle = sweepAngle,
                            useCenter = false,
                            topLeft = topLeft,
                            size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
                            style = Stroke(width = stroke, cap = StrokeCap.Round)
                        )

                        // Glow dot at progress edge
                        if (animatedProgress.value > 0.01f) {
                            val glowAngle = -90f + sweepAngle
                            val glowRad = Math.toRadians(glowAngle.toDouble())
                            val dotX = center.x + radius * kotlin.math.cos(glowRad).toFloat()
                            val dotY = center.y + radius * kotlin.math.sin(glowRad).toFloat()
                            drawCircle(
                                color = WaterBlue,
                                radius = 6.dp.toPx(),
                                center = Offset(dotX, dotY)
                            )
                        }
                    }

                    // Center text
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "${total}ml",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = WaterBlueDark
                        )
                        Text(
                            "/ $goal",
                            fontSize = 13.sp,
                            color = WaterBlueDark.copy(alpha = 0.7f)
                        )
                    }
                }

                Spacer(Modifier.width(16.dp))

                // Quick add buttons
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(200, 300, 500).forEach { ml ->
                        FilledTonalButton(
                            onClick = { onAdd(ml) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = WaterBlueLight,
                                contentColor = WaterBlue60
                            )
                        ) {
                            Icon(
                                Icons.Filled.Add,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text("${ml}ml", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }

            // Undo
            if (total > 0) {
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onUndo) {
                        Icon(Icons.Outlined.Undo, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Undo", fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

// ──────────────────────────────────────
// HABIT ITEM — with Spring Bounce on Check
// ──────────────────────────────────────
@Composable
fun HabitItem(
    habit: Habit,
    isChecked: Boolean,
    currentCount: Int,
    onCheck: () -> Unit,
    onArchive: () -> Unit,
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val primary = MaterialTheme.colorScheme.primary
    val bgColor by animateColorAsState(
        targetValue = if (isChecked) primary.copy(alpha = 0.08f)
        else MaterialTheme.colorScheme.surface,
        label = "bg"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isChecked) primary else Color.Transparent,
        label = "border"
    )

    // Spring scale animation
    val scale = remember { Animatable(1f) }
    var justChecked by remember { mutableStateOf(false) }

    LaunchedEffect(isChecked) {
        if (isChecked && !justChecked) {
            justChecked = true
            scale.animateTo(0.93f, tween(80))
            scale.animateTo(1.0f, spring(dampingRatio = 0.4f, stiffness = 500f))
        } else if (!isChecked) {
            justChecked = false
        }
    }

    // Bounce on tap (even if already checked)
    var bounceTrigger by remember { mutableStateOf(0) }
    val bounceScale = remember { Animatable(1f) }
    LaunchedEffect(bounceTrigger) {
        if (bounceTrigger > 0) {
            bounceScale.animateTo(0.95f, tween(60))
            bounceScale.animateTo(1.0f, spring(dampingRatio = 0.3f, stiffness = 800f))
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer(
                scaleX = scale.value * bounceScale.value,
                scaleY = scale.value * bounceScale.value
            )
            .clickable { bounceTrigger++; onCheck() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        border = if (isChecked) androidx.compose.foundation.BorderStroke(1.5.dp, borderColor) else null,
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isChecked) 0.dp else 1.dp
        )
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
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(
                        if (isChecked) primary else MaterialTheme.colorScheme.surfaceVariant
                    )
                    .clickable { bounceTrigger++; onCheck() },
                contentAlignment = Alignment.Center
            ) {
                if (isChecked) {
                    Icon(
                        Icons.Filled.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
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

            // Habit name + reminder
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    habit.name,
                    fontWeight = if (isChecked) FontWeight.SemiBold else FontWeight.Normal,
                    fontSize = 15.sp,
                    color = if (isChecked) primary else MaterialTheme.colorScheme.onSurface
                )
                if (habit.reminderEnabled) {
                    Text(
                        "${
                            "%02d".format(habit.reminderHour)
                        }:${
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
                    fontWeight = FontWeight.Medium,
                    color = if (isChecked) primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.width(8.dp))
            }

            // Fire streak icon
            if (isChecked) {
                Icon(
                    Icons.Filled.LocalFireDepartment,
                    contentDescription = null,
                    tint = StreakOrange,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Menu
            var showMenu by remember { mutableStateOf(false) }
            IconButton(onClick = { showMenu = true }, modifier = Modifier.size(32.dp)) {
                Icon(
                    Icons.Filled.MoreVert,
                    contentDescription = "Opsi",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
            DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                DropdownMenuItem(
                    text = { Text("Edit") },
                    onClick = { showMenu = false; onEdit() },
                    leadingIcon = { Icon(Icons.Filled.Edit, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )
                HorizontalDivider()
                DropdownMenuItem(
                    text = { Text("Arsipkan") },
                    onClick = { showMenu = false; onArchive() },
                    leadingIcon = { Icon(Icons.Filled.Archive, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )
                HorizontalDivider()
                DropdownMenuItem(
                    text = { Text("Hapus", color = MaterialTheme.colorScheme.error) },
                    onClick = { showMenu = false; onDelete() },
                    leadingIcon = { Icon(Icons.Filled.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp)) }
                )
            }
        }
    }
}

// ──────────────────────────────────────
// STREAK BAR — Circular Animated Progress
// ──────────────────────────────────────
@Composable
fun StreakBar(
    done: Int,
    total: Int,
    modifier: Modifier = Modifier
) {
    val animatedDone by animateFloatAsState(
        targetValue = done.toFloat(),
        animationSpec = spring(dampingRatio = 0.7f, stiffness = 120f),
        label = "streakCount"
    )
    val progress = if (total > 0) done.toFloat() / total else 0f
    val animatedProgress = remember { Animatable(0f) }

    LaunchedEffect(progress) {
        animatedProgress.animateTo(progress, spring(dampingRatio = 0.7f, stiffness = 100f))
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Circular ring
            Box(
                modifier = Modifier.size(52.dp),
                contentAlignment = Alignment.Center
            ) {
                val containerColor = MaterialTheme.colorScheme.primaryContainer
                Canvas(Modifier.fillMaxSize()) {
                    val stroke = 4.dp.toPx()
                    val radius = (size.minDimension - stroke) / 2
                    // Background
                    drawCircle(
                        color = containerColor,
                        radius = radius,
                        center = center,
                        style = Stroke(width = stroke, cap = StrokeCap.Round)
                    )
                    // Progress
                    drawArc(
                        color = StreakOrange,
                        startAngle = -90f,
                        sweepAngle = 360f * animatedProgress.value,
                        useCenter = false,
                        topLeft = Offset(
                            (size.width - radius * 2) / 2,
                            (size.height - radius * 2) / 2
                        ),
                        size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
                        style = Stroke(width = stroke, cap = StrokeCap.Round)
                    )
                }
                Icon(
                    Icons.Filled.LocalFireDepartment,
                    contentDescription = null,
                    tint = StreakOrange,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(Modifier.width(16.dp))

            Column {
                Text(
                    "${animatedDone.toInt()} dari $total",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    "kebiasaan selesai",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }
        }
    }
}
