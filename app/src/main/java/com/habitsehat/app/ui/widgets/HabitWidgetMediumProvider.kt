package com.habitsehat.app.ui.widgets

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context

class HabitWidgetMediumProvider : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        WidgetUpdateWorker.enqueueUpdate(context)
        for (appWidgetId in appWidgetIds) {
            HabitWidgetProvider.updateAppWidget(context, appWidgetManager, appWidgetId, "medium")
        }
    }
}
