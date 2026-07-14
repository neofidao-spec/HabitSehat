package com.habitsehat.app.ui.widgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.habitsehat.app.MainActivity
import com.habitsehat.app.R
import com.habitsehat.app.data.db.AppDatabase
import com.habitsehat.app.data.model.Habit
import com.habitsehat.app.data.repository.HabitRepository
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class HabitWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        WidgetUpdateWorker.enqueueUpdate(context)

        for (appWidgetId in appWidgetIds) {
            val widgetInfo = appWidgetManager.getAppWidgetOptions(appWidgetId)
            val minWidth = widgetInfo.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, 110)
            val widgetType = when {
                minWidth >= 250 -> "large"
                minWidth >= 200 -> "medium"
                else -> "mini"
            }
            updateAppWidget(context, appWidgetManager, appWidgetId, widgetType)
        }
    }

    companion object {
        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int,
            widgetType: String = "mini"
        ) {
            val layoutId = when (widgetType) {
                "large" -> R.layout.widget_large
                "medium" -> R.layout.widget_medium
                else -> R.layout.widget_mini
            }

            val views = RemoteViews(context.packageName, layoutId)
            val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val today = LocalDate.now().format(dateFormat)
            val displayDate = LocalDate.now().format(DateTimeFormatter.ofPattern("d MMM yyyy", Locale("id", "ID")))

            try {
                val db = AppDatabase.getInstance(context)
                val repository = HabitRepository(
                    db.habitDao(), db.habitLogDao(), db.waterLogDao(),
                    db.badHabitDao(), db.badHabitLogDao(), db.pomodoroDao()
                )
                runBlocking {
                    val habits = repository.getAllHabits()
                    var doneCount = 0
                    for (habit in habits) {
                        if (repository.isHabitChecked(habit.id, today)) doneCount++
                    }
                    val waterTotal = repository.getWaterTotal(today)
                    val totalHabits = habits.size

                    var bestStreak = 0
                    for (habit in habits) {
                        val since = LocalDate.now().minusDays(365).format(dateFormat)
                        val streak = repository.getStreak(habit.id, since)
                        if (streak > bestStreak) bestStreak = streak
                    }

                    when (widgetType) {
                        "mini" -> {
                            views.setTextViewText(R.id.widget_streak_text, "🔥 $bestStreak")
                            views.setTextViewText(R.id.widget_today_progress, "$doneCount/$totalHabits selesai")
                            views.setTextViewText(R.id.widget_date, displayDate)
                        }
                        "medium" -> {
                            views.setTextViewText(R.id.widget_streak_text, "🔥 ${bestStreak} hari")
                            views.setTextViewText(R.id.widget_water_progress, "💧 ${formatWater(waterTotal)}")
                            views.setTextViewText(R.id.widget_today_progress, "✅ $doneCount/$totalHabits kebiasaan selesai")
                            views.setTextViewText(R.id.widget_date, displayDate)
                            setMediumHabitList(views, habits, today, repository)
                        }
                        "large" -> {
                            views.setTextViewText(R.id.widget_title, "HabitSehat")
                            views.setTextViewText(R.id.widget_streak_text, "🔥 $bestStreak")
                            views.setTextViewText(R.id.widget_today_progress, "✅ $doneCount/$totalHabits selesai")
                            val progress = if (totalHabits > 0) (doneCount * 100 / totalHabits) else 0
                            views.setProgressBar(R.id.widget_progress_bar, 100, progress, false)
                            views.setTextViewText(R.id.widget_water_progress, "💧 $waterTotal / 2.500 ml")
                            views.setTextViewText(R.id.widget_date, displayDate)
                            setLargeHabitLines(views, habits, today, repository)
                        }
                    }
                }
            } catch (e: Exception) {
                views.setTextViewText(R.id.widget_today_progress, "Buka app untuk memulai")
            }

            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(android.R.id.background, pendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        private fun setMediumHabitList(
            views: RemoteViews,
            habits: List<Habit>,
            today: String,
            repository: HabitRepository
        ) {
            val sb = StringBuilder()
            for ((i, habit) in habits.withIndex()) {
                if (i >= 4) break
                val checked = runCatching { runBlocking { repository.isHabitChecked(habit.id, today) } }.getOrDefault(false)
                val prefix = if (checked) "✅" else "○"
                sb.append("$prefix ${habit.name}\n")
            }
            views.setTextViewText(R.id.widget_habit_list, sb.toString().trimEnd())
        }

        private fun setLargeHabitLines(
            views: RemoteViews,
            habits: List<Habit>,
            today: String,
            repository: HabitRepository
        ) {
            val ids = listOf(
                R.id.widget_habit_1, R.id.widget_habit_2, R.id.widget_habit_3,
                R.id.widget_habit_4, R.id.widget_habit_5
            )
            for ((i, id) in ids.withIndex()) {
                if (i < habits.size) {
                    val habit = habits[i]
                    val checked = runCatching { runBlocking { repository.isHabitChecked(habit.id, today) } }.getOrDefault(false)
                    views.setTextViewText(id, "${if (checked) "✅" else "○"} ${habit.name}")
                } else {
                    views.setTextViewText(id, "")
                }
            }
        }

        private fun formatWater(ml: Int): String {
            return if (ml >= 1000) "${ml / 1000}.${(ml % 1000) / 100}L" else "${ml}ml"
        }
    }
}
