package com.google.android.material.composethemeadapter.sample.backstage

import android.content.Context
import android.util.Log
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.io.FileOutputStream
import java.io.IOException

internal fun save(schedule: Schedule, context: Context) {
    //Log.d("save","try to save to disk")
    saveAsJSON(schedule.getAllDDl(), context.openFileOutput("ddl.json", Context.MODE_PRIVATE))
    saveAsJSON(schedule.getAllCourse(), context.openFileOutput("course.json", Context.MODE_PRIVATE))
    saveAsJSON(schedule.termInfo, context.openFileOutput("term_info.json", Context.MODE_PRIVATE))
    saveAsJSON(schedule.pulledDDl.toList(), context.openFileOutput("pulled.json", Context.MODE_PRIVATE))
}

internal fun load(schedule: Schedule, context: Context):Boolean  {

    var success = true

    schedule.clear()

    val mapper = jacksonObjectMapper()

    loadJson("ddl.json", context)?.let { json ->
        try {
            mapper.readValue<List<DDlInfo>>(json).let {
                for (ddl in it)
                    schedule.addDDl(ddl)
            }
        } catch (e: Exception) {
            Log.d("LoadErr", "DDL load failed")
            success = false
        }
    }


    loadJson("course.json", context)?.let { json ->
        try {
            mapper.readValue<List<CourseInfo>>(json).let {
                for (course in it)
                    schedule.addCourse(course)
                for (course in it)
                    for (template in course.timeInfo)
                        template.info = course
            }
        } catch (e: Exception) {
            Log.d("LoadErr", "Course load failed")
            success = false
        }
    }

    loadJson("term_info.json", context)?.let { json ->
        try {
            schedule.termInfo = mapper.readValue(json)
        }
        catch (e:Exception) {
            Log.d("LoadErr", "Term load failed")
            success = false
        }
    }

    loadJson("pulled.json", context)?.let {json ->
        try {
            schedule.pulledDDl.addAll(mapper.readValue<List<String>>(json))
        }
        catch (e:Exception) {
            Log.d("LoadErr", "Term load failed")
            success = false
        }
    }

    return success
}

private fun <T> saveAsJSON(obj:T, file: FileOutputStream) = file.use{it.writer().use{
        writer -> jacksonObjectMapper().writeValue(writer, obj)
        //Log.d("saveAsJSON","saved: $obj")
}
}

private fun loadJson(fileName: String, context: Context): String? {
    var jsonData: String
    //Log.d("loadJson", "try to load file: $fileName")
    try {
        val file = context.openFileInput(fileName)
        file.use {
            it.reader().use { reader ->
                jsonData = reader.readText()
            }
        }
        //Log.d("loadJson", "loaded: $jsonData")
    }
    catch (e: IOException) {
        Log.d("loadJson", "no such file: $fileName")
        return null
    }
    return jsonData
}