package com.google.android.material.composethemeadapter.sample.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.compose.runtime.toMutableStateList
import com.google.android.material.composethemeadapter.sample.MainActivity
import com.google.android.material.composethemeadapter.sample.backstage.DDlInfo
import com.google.android.material.composethemeadapter.sample.backstage.Schedule
import com.google.android.material.composethemeadapter.sample.backstage.TestDataConfig
import com.google.android.material.composethemeadapter.sample.backstage.getWeekDay
import com.google.android.material.composethemeadapter.sample.R
import java.util.*

private const val REMOTE_VIEW_COUNT: Int = 10
const val TOAST_ACTION = "com.example.android.stackwidget.TOAST_ACTION"
const val BUTTON_ACTION = "com.example.android.stackwidget.BUTTON_ACTION"
const val EXTRA_ITEM = "com.example.android.stackwidget.EXTRA_ITEM"

class StackWidgetService : RemoteViewsService() {

    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        Log.d("ddllist", "222")
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
        Log.d("ddllist", "111")
        val config = TestDataConfig(20, 1000, 20, 12)
        val schedule = Schedule(context, config)
        val curWeekDay = getWeekDay(
            schedule.termInfo.startingTime,
            Calendar.getInstance().timeInMillis
        )
        list = schedule.getDDlFromRelativeTime(curWeekDay.week, curWeekDay.day)
            .toMutableStateList()
        Log.d("ddllist", list.size.toString())
    }

    override fun onDataSetChanged() {
    }

    override fun onDestroy() {

    }

    override fun getCount(): Int = list.size


    override fun getViewAt(position: Int): RemoteViews {
        return RemoteViews(context.packageName, R.layout.widget_item).apply {
            setTextViewText(R.id.timeView, list[position].getString(MainActivity.GlobalInformation.activity.schedule.termInfo)) // TODO 给个Schedule
            setTextViewText(R.id.infoView, list[position].name)
            val fillInIntent = Intent().apply {
                Bundle().also { extras ->
                    extras.putInt(EXTRA_ITEM, position)
                    putExtras(extras)
                }
            }
            setOnClickFillInIntent(R.id.widget_item, fillInIntent)
        }
    }

    override fun getLoadingView(): RemoteViews? = null


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
