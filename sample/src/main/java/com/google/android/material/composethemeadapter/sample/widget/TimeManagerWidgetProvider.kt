package com.google.android.material.composethemeadapter.sample.widget

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import com.google.android.material.composethemeadapter.sample.MainActivity
import com.google.android.material.composethemeadapter.sample.R
import com.google.android.material.composethemeadapter.sample.backstage.Schedule
import java.util.*

var idsSet: MutableSet<Int> = HashSet()

class TimeManagerWidgetProvider : AppWidgetProvider() {
    override fun onReceive(context: Context, intent: Intent) {
        val mgr: AppWidgetManager = AppWidgetManager.getInstance(context)
        //Toast.makeText(context,intent.action,Toast.LENGTH_SHORT).show()
        val appWidgetId: Int = intent.getIntExtra(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        )
        intent.action?.let { Log.println(Log.ERROR,"114514","Action is "+ it) }
        //Log.println(Log.ERROR,"114514", "receive "+appWidgetId.toString())
        //Log.println(Log.ERROR,"114514", "idset is"+idsSet.toString())
        if (intent.action == TOAST_ACTION) {
            val MainIntent=Intent(context, MainActivity::class.java)
            val bundle = Bundle()
            //可以通过Bundle添加数据了
            MainIntent.putExtras(bundle)
            MainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(MainIntent)
        }
        else if(intent.action == BUTTON_ACTION){
            val appWidgetId: Int = intent.getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
            )
            val viewIndex: Int= intent.getIntExtra(EXTRA_ITEM, 0)
            val viewName: String? = intent.getStringExtra(DDL_NAME)
            Toast.makeText(context,"Task "+viewIndex+":"+viewName+" Finish",Toast.LENGTH_SHORT).show()

            val schedule=Schedule(context)
            //schedule.removeDDlFromId(viewIndex)
            schedule.saveAll()
            UpdateWidget(context,appWidgetId,mgr)
        }
        else if (intent.action==UPDATE){
            for (id in idsSet) {
                UpdateWidget(context,id,mgr)
            }
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
            idsSet.add(appWidgetId)
            //Log.println(Log.ERROR,"114514", "create:"+appWidgetId.toString())
            UpdateWidget(context,appWidgetId,appWidgetManager)
        }
        //.println(Log.ERROR,"114514", "idset is"+idsSet.toString())
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    override fun onDeleted(context: Context?, appWidgetIds: IntArray?) {
        for (appWidgetId in appWidgetIds!!) {
            idsSet.remove(appWidgetId)
        }
        super.onDeleted(context, appWidgetIds)
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    fun UpdateWidget(context: Context, appWidgetId: Int,appWidgetManager: AppWidgetManager){
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
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            data = Uri.parse(toUri(Intent.URI_INTENT_SCHEME))
            PendingIntent.getBroadcast(context, 0, this, PendingIntent.FLAG_UPDATE_CURRENT)
        }
        rv.setPendingIntentTemplate(R.id.stack_view, toastPendingIntent)
        appWidgetManager.updateAppWidget(appWidgetId, rv)
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId,R.id.stack_view)
    }

}


