package com.google.android.material.composethemeadapter.sample.widget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.icu.text.IDNA.Info
import android.os.Bundle
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.platform.LocalContext
import androidx.core.os.bundleOf
import com.example.myapplication.backstage.DDlInfo
import com.example.myapplication.backstage.Schedule
import com.example.myapplication.backstage.TestDataConfig
import com.example.myapplication.backstage.getWeekDay
import com.google.android.material.composethemeadapter.sample.MainActivity
import com.google.android.material.composethemeadapter.sample.R
import java.util.*

private const val REMOTE_VIEW_COUNT: Int = 10
const val TOAST_ACTION = "com.example.android.stackwidget.TOAST_ACTION"
const val BUTTON_ACTION = "com.example.android.stackwidget.BUTTON_ACTION"
const val EXTRA_ITEM = "com.example.android.stackwidget.EXTRA_ITEM"

class StackWidgetService : RemoteViewsService() {

    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return StackRemoteViewsFactory(this.applicationContext, intent)
    }
}

class StackRemoteViewsFactory(
    private val context: Context,
    intent: Intent
) : RemoteViewsService.RemoteViewsFactory {

    lateinit var list: MutableList<DDlInfo>

    private val appWidgetId: Int = intent.getIntExtra(
        AppWidgetManager.EXTRA_APPWIDGET_ID,
        AppWidgetManager.INVALID_APPWIDGET_ID
    )

    override fun onCreate() {

        val config = TestDataConfig(20,1000,20,12)
        val schedule = Schedule(context, config)
        val curWeekDay = getWeekDay(
            schedule.termStartTime,
            Calendar.getInstance().timeInMillis
        )
        list= schedule.getDDlFromRelativeTime(curWeekDay.week.toInt(),curWeekDay.day.toInt()).toMutableStateList()

    }

    override fun onDataSetChanged() {
    }

    override fun onDestroy() {

    }

    override fun getCount(): Int = list.size


    override fun getViewAt(position: Int): RemoteViews {
        return RemoteViews(context.packageName, R.layout.widget_item).apply {
            setTextViewText(R.id.widget_item, list[position].getString())
            val fillInIntent = Intent().apply {
                Bundle().also { extras ->
                    extras.putInt(EXTRA_ITEM, position)
                    putExtras(extras)
                }
            }
            setOnClickFillInIntent(R.id.widget_item, fillInIntent)
        }
    }

    override fun getLoadingView(): RemoteViews?=null


    override fun getViewTypeCount(): Int {
       return 1
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun hasStableIds(): Boolean {
        return true
    }

}
