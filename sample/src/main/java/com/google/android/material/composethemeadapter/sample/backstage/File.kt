package com.example.myapplication.backstage

import android.content.Context
import com.google.android.material.composethemeadapter.sample.backstage.CourseInfo
import com.google.android.material.composethemeadapter.sample.backstage.DDlInfo
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.FileInputStream
import java.io.FileOutputStream

internal fun save(schedule: Schedule, context: Context) {
    saveAsJSON(schedule.getAllDDl(), context.openFileOutput("ddl.json", Context.MODE_PRIVATE))
    saveAsJSON(schedule.getAllCourse(), context.openFileOutput("course.json", Context.MODE_PRIVATE))

}

internal fun load(schedule: Schedule, context: Context)  {
    val ddlList = loadAndParse<DDlInfo>(context.openFileInput("ddl.json"))
    val courseList = loadAndParse<CourseInfo>(context.openFileInput("course.json"))

    for (ddl in ddlList)
        schedule.addDDl(ddl)

    for (course in courseList)
        schedule.addCourse(course)

    for (course in courseList) {
        for (template in course.TimeInfo) {
            template.info = course
        }
    }

    // TODO load it
    schedule.termStartTime = 0
}

private fun <T> saveAsJSON(list:List<T>, file: FileOutputStream) = file.use{it.writer().use{ writer -> writer.write(Gson().toJson(list))}}

private fun <T> loadAndParse(file: FileInputStream): List<T> {
    var jsonData: String
    try {
        file.use {
            it.reader().use { reader ->
                jsonData = reader.readText()
            }
        }
    }
    catch (e: NoSuchFileException) {
        return emptyList()
    }
    return Gson().fromJson(jsonData, object : TypeToken<List<T>>() {}.type)
}