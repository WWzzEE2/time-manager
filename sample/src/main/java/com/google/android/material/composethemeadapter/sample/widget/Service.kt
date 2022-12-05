package com.google.android.material.composethemeadapter.sample.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.compose.runtime.toMutableStateList
import com.google.android.material.composethemeadapter.sample.R
import com.google.android.material.composethemeadapter.sample.backstage.DDlInfo
import com.google.android.material.composethemeadapter.sample.backstage.Schedule
import com.google.android.material.composethemeadapter.sample.backstage.getWeekDay
import com.google.android.material.composethemeadapter.sample.backstage.load
import java.util.*

const val TOAST_ACTION = "com.example.android.stackwidget.TOAST_ACTION"
const val BUTTON_ACTION = "com.example.android.stackwidget.BUTTON_ACTION"
const val EXTRA_ITEM = "com.example.android.stackwidget.EXTRA_ITEM"
const val DDL_NAME = "com.example.android.stackwidget.DDL_NAME"
const val UPDATE="com.ideal.note.widget"
class StackWidgetService : RemoteViewsService() {

    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return StackRemoteViewsFactory(this.applicationContext, intent)
    }
}

class StackRemoteViewsFactory(
    private val context: Context,
    intent: Intent
) : RemoteViewsService.RemoteViewsFactory {


    val schedule = Schedule(context)
    val curWeekDay = getWeekDay(
        schedule.termInfo.startingTime,
        Calendar.getInstance().timeInMillis
    )

    lateinit var list: MutableList<DDlInfo>

    private val appWidgetId: Int = intent.getIntExtra(
        AppWidgetManager.EXTRA_APPWIDGET_ID,
        AppWidgetManager.INVALID_APPWIDGET_ID
    )

    override fun onCreate() {
    }

    override fun onDataSetChanged() {
        //Log.println(Log.ERROR,"114514","onDataSetChanged")
        load(schedule,context)
        list = schedule.getDDlFromRelativeTime(curWeekDay.week, curWeekDay.day)
            .toMutableStateList()
/*        for (i in list)
            Log.println(Log.ERROR,"114514","list have "+i.name)*/
    }

    override fun onDestroy() {
    }

    override fun getCount(): Int = list.size


    override fun getViewAt(position: Int): RemoteViews {
        return RemoteViews(context.packageName, R.layout.widget_item).apply {
            setTextViewText(R.id.timeView, list[position].getString(schedule.termInfo))
            setTextViewText(R.id.infoView, list[position].name)
            val toastInIntent = Intent().apply {
                action=TOAST_ACTION
                Bundle().also { extras ->
                    extras.putInt(EXTRA_ITEM, list[position].id.toInt())
                    putExtras(extras)
                }
            }
            val buttonInIntent = Intent().apply {
                action=BUTTON_ACTION
                Bundle().also { extras ->
                    extras.putInt(EXTRA_ITEM, list[position].id.toInt())
                    extras.putString(DDL_NAME, list[position].name)
                    putExtras(extras)
                }
            }
            setOnClickFillInIntent(R.id.button,buttonInIntent )
            setOnClickFillInIntent(R.id.widget_item, toastInIntent)
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
