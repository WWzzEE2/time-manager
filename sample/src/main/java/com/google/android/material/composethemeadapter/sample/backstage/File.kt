package com.google.android.material.composethemeadapter.sample.backstage

import android.content.Context
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.io.FileInputStream
import java.io.FileOutputStream

internal fun save(schedule: Schedule, context: Context) {
    saveAsJSON(schedule.getAllDDl(), context.openFileOutput("ddl.json", Context.MODE_PRIVATE))
    saveAsJSON(schedule.getAllCourse(), context.openFileOutput("course.json", Context.MODE_PRIVATE))
    saveAsJSON(schedule.termInfo, context.openFileOutput("term_info.json", Context.MODE_PRIVATE))
}

internal fun load(schedule: Schedule, context: Context)  {

    schedule.clear()

    val mapper = jacksonObjectMapper()

    loadJson(context.openFileInput("ddl.json"))?.let {
            json -> mapper.readValue<List<DDlInfo>>(json).let {
        for (ddl in it)
            schedule.addDDl(ddl)
    }
    }


    loadJson(context.openFileInput("course.json"))?.let {
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

    loadJson(context.openFileInput("term_info.json"))?.let {
            json -> schedule.termInfo = mapper.readValue(json)
    }
}

private fun <T> saveAsJSON(obj:T, file: FileOutputStream) = file.use{it.writer().use{
        writer -> jacksonObjectMapper().writeValue(writer, obj)
}
}

private fun loadJson(file: FileInputStream): String? {
    var jsonData: String
    try {
        file.use {
            it.reader().use { reader ->
                jsonData = reader.readText()
            }
        }
    }
    catch (e: NoSuchFileException) {
        return null
    }
    return jsonData
}