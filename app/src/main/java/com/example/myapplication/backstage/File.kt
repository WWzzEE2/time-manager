package com.example.myapplication.backstage

import android.content.Context
import androidx.activity.ComponentActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.FileInputStream
import java.io.FileOutputStream

fun save(ddlList: List<DDlInfo>, courseList: List<CourseInfo>, activity: ComponentActivity) {
    saveAsJSON(ddlList, activity.openFileOutput("ddl.json", Context.MODE_PRIVATE))
    saveAsJSON(courseList, activity.openFileOutput("course.json", Context.MODE_PRIVATE))

}

fun load(activity: ComponentActivity) : Pair<List<DDlInfo>, List<CourseInfo>> {
    val ddlList = loadAndParse<DDlInfo>(activity.openFileInput("ddl.json"))
    val courseList = loadAndParse<CourseInfo>(activity.openFileInput("course.json"))

    for (course in courseList) {
        for (template in course.TimeInfo) {
            template.info = course
        }
    }
    return Pair(ddlList, courseList)
}

private fun <T> saveAsJSON(list:List<T>, file: FileOutputStream) = file.use{it.writer().use{ writer -> writer.write(Gson().toJson(list))}}

private fun <T> loadAndParse(file: FileInputStream): List<T> {
    var jsonData: String
    file.use {
        it.reader().use {
            reader -> jsonData = reader.readText()
        }
    }
    return Gson().fromJson(jsonData, object : TypeToken<List<T>>() {}.type)
}