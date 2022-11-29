package com.google.android.material.composethemeadapter.sample.backstage

import android.content.Context
import android.util.Log
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

internal fun save(schedule: Schedule, context: Context) {
    Log.d("save","try to save to disk")
    saveAsJSON(schedule.getAllDDl(), context.openFileOutput("ddl.json", Context.MODE_PRIVATE))
    saveAsJSON(schedule.getAllCourse(), context.openFileOutput("course.json", Context.MODE_PRIVATE))
    saveAsJSON(schedule.termInfo, context.openFileOutput("term_info.json", Context.MODE_PRIVATE))
}

internal fun load(schedule: Schedule, context: Context)  {

    schedule.clear()

    val mapper = jacksonObjectMapper()

    loadJson("ddl.json", context)?.let {
            json -> mapper.readValue<List<DDlInfo>>(json).let {
        for (ddl in it)
            schedule.addDDl(ddl)
    }
    }


    loadJson("course.json", context)?.let {
            json -> mapper.readValue<List<CourseInfo>>(json).let {
        for (course in it)
            schedule.addCourse(course)
        for (course in it) {
            for (template in course.timeInfo) {
                template.info = course
            }
        }
    }
    }

    loadJson("term_info.json", context)?.let {
            json -> schedule.termInfo = mapper.readValue(json)
    }
}

private fun <T> saveAsJSON(obj:T, file: FileOutputStream) = file.use{it.writer().use{
        writer -> jacksonObjectMapper().writeValue(writer, obj)
        Log.d("saveAsJSON","saved: $obj")
}
}

private fun loadJson(fileName: String, context: Context): String? {
    var jsonData: String
    Log.d("loadJson", "try to load file: $fileName")
    try {
        val file = context.openFileInput(fileName)
        file.use {
            it.reader().use { reader ->
                jsonData = reader.readText()
            }
        }
        Log.d("loadJson", "loaded: $jsonData")
    }
    catch (e: IOException) {
        Log.d("loadJson", "no such file: $fileName")
        return null
    }
    return jsonData
}