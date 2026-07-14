package com.habitsehat.app.ui.widgets

import android.content.Context
import androidx.work.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class WidgetUpdateWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val appWidgetManager = android.appwidget.AppWidgetManager.getInstance(applicationContext)
            val ids = appWidgetManager.getAppWidgetIds(
                android.content.ComponentName(applicationContext, HabitWidgetProvider::class.java)
            )
            for (id in ids) {
                val widgetInfo = appWidgetManager.getAppWidgetOptions(id)
                val minWidth = widgetInfo.getInt(android.appwidget.AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, 110)
                val widgetType = when {
                    minWidth >= 250 -> "large"
                    minWidth >= 200 -> "medium"
                    else -> "mini"
                }
                HabitWidgetProvider.updateAppWidget(applicationContext, appWidgetManager, id, widgetType)
            }
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        private const val WORK_NAME = "widget_update"

        fun enqueueUpdate(context: Context) {
            val request = OneTimeWorkRequestBuilder<WidgetUpdateWorker>()
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                        .build()
                )
                .build()
            WorkManager.getInstance(context)
                .enqueueUniqueWork(WORK_NAME, ExistingWorkPolicy.REPLACE, request)
        }

        fun schedulePeriodic(context: Context) {
            val request = PeriodicWorkRequestBuilder<WidgetUpdateWorker>(30, TimeUnit.MINUTES)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                        .build()
                )
                .build()
            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, request)
        }
    }
}
