package com.google.android.material.composethemeadapter.sample.widget

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import android.widget.Toast
import com.google.android.material.composethemeadapter.sample.MainActivity
import com.google.android.material.composethemeadapter.sample.R


class TimeManagerWidgetProvider : AppWidgetProvider() {

    override fun onReceive(context: Context, intent: Intent) {
        val mgr: AppWidgetManager = AppWidgetManager.getInstance(context)
        if (intent.action == TOAST_ACTION) {
            val appWidgetId: Int = intent.getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
            )
            val viewIndex: Int = intent.getIntExtra(EXTRA_ITEM, 0)
            Toast.makeText(context, "Touched view $viewIndex", Toast.LENGTH_SHORT).show()
            val pendingIntent=Intent(context, MainActivity::class.java)
            pendingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(pendingIntent)
        }
        super.onReceive(context, intent)
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach { appWidgetId ->
            val intent = Intent(context, StackWidgetService::class.java).apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                data = Uri.parse(toUri(Intent.URI_INTENT_SCHEME))
            }
            val rv = RemoteViews(context.packageName, R.layout.widget_layout).apply {
                setRemoteAdapter(R.id.stack_view, intent)

                setEmptyView(R.id.stack_view, R.id.empty_view)
            }

            val toastPendingIntent: PendingIntent = Intent(
                context,
                TimeManagerWidgetProvider::class.java
            ).run {
                action = TOAST_ACTION
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                data = Uri.parse(toUri(Intent.URI_INTENT_SCHEME))

                PendingIntent.getBroadcast(context, 0, this, PendingIntent.FLAG_UPDATE_CURRENT)
            }
            rv.setPendingIntentTemplate(R.id.stack_view, toastPendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, rv)
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

}


