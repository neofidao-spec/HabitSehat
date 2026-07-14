package com.habitsehat.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChallengesScreen(
    viewModel: ChallengesViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tantangan", fontWeight = FontWeight.SemiBold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Kembali") } }
            )
        }
    ) { padding ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Spacer(Modifier.height(8.dp))
                    Text("Tantangan Aktif", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                    Spacer(Modifier.height(8.dp))
                }

                if (state.challenges.isEmpty()) {
                    item {
                        Card(shape = RoundedCornerShape(16.dp)) {
                            Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                Text("Tidak ada tantangan tersedia", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                } else {
                    items(state.challenges) { cwp ->
                        ChallengeCard(
                            challenge = cwp.challenge,
                            progress = cwp.progress,
                            onJoin = { viewModel.joinChallenge(cwp.challenge.id) }
                        )
                    }
                }

                if (state.completedBadges.isNotEmpty()) {
                    item {
                        Spacer(Modifier.height(16.dp))
                        Text("Lencana Selesai 🏅", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                        Spacer(Modifier.height(8.dp))
                    }
                    items(state.completedBadges) { cwp ->
                        CompletedBadgeCard(cwp.challenge, cwp.progress)
                    }
                }

                item { Spacer(Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
private fun ChallengeCard(
    challenge: com.habitsehat.app.data.model.Challenge,
    progress: com.habitsehat.app.data.model.ChallengeProgress?,
    onJoin: () -> Unit
) {
    val isJoined = progress != null
    val currentDays = progress?.currentDays ?: 0
    val targetDays = challenge.targetDays
    val progressPercent = if (targetDays > 0) (currentDays * 100f / targetDays).toInt() else 0

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(challenge.icon, fontSize = 28.sp)
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(challenge.name, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    Text(challenge.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                if (isJoined) {
                    Text("${currentDays}/${targetDays} hari", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.primary)
                }
            }

            if (isJoined) {
                Spacer(Modifier.height(12.dp))

                // Progress bar
                LinearProgressIndicator(
                    progress = { currentDays.toFloat() / targetDays.toFloat().coerceAtLeast(1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    color = if (currentDays >= targetDays) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                )

                Spacer(Modifier.height(4.dp))
                Text("$progressPercent% selesai", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = onJoin,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Ikuti Tantangan", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun CompletedBadgeCard(
    challenge: com.habitsehat.app.data.model.Challenge,
    progress: com.habitsehat.app.data.model.ChallengeProgress?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("🏅", fontSize = 32.sp)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(challenge.name, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                Text("Selesai ${challenge.targetDays} hari!", fontSize = 12.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
            }
            Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        }
    }
}
