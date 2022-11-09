package com.example.myapplication.backstage


import android.content.Context
import androidx.compose.foundation.interaction.DragInteraction
import java.util.*
import kotlin.collections.HashSet
import kotlin.collections.ArrayList
import kotlin.math.abs
import kotlin.math.min


/**
 * @author WWzzEE2
 * @since v1
 *
 * The backstage functions of the program
 */

private class TemplateMap(val bindSchedule: Schedule) : TreeMap<Long, HashSet<CourseTemplate>>() {

    /**
     * Add a template
     * @return The conflict course when add fails
     */
    fun addTemplate(template: CourseTemplate, check: Boolean = true) : Boolean {
        if (check && conflictCheck(template)) return false
        val set = this[template.StartingTime]?: HashSet()
        set.add(template)
        this[template.StartingTime] = set
        template.info.TimeInfo.add(template)
        return true
    }

    /**
     * Add a course
     */
    fun removeTemplate(template: CourseTemplate) {
        this[template.StartingTime]?.remove(template)
        template.info.TimeInfo.remove(template)
    }

    /**
     *  Check if there are any courses at given time
     *  The course's time is treated as [startTime, endTime)
     */
    fun getTemplate(time:Long, week:Long) : CourseTemplate? {
        for ((k, v) in this) {
            if (k > time) break
            for (t in v) {
                if (t.EndingTime > time && bindSchedule.templateAvailable(t, week.toShort())) return t
            }
        }
        return null
    }

    /**
     * Check the given template conflict in current map
     */
    fun conflictCheck(template: CourseTemplate) : Boolean {
        for ((k, v) in this) {
            if (k > template.EndingTime) break
            for (t in v) if (conflictCheck(t, template)) return true
        }
        return false
    }

    /**
     * Check 2 course template conflict or not
     * @return True when conflict
     */
    fun conflictCheck(t1: CourseTemplate, t2: CourseTemplate) : Boolean {
        if (crossover(t1.StartingTime.toInt(), t1.EndingTime.toInt(), t2.StartingTime.toInt(), t2.EndingTime.toInt()) &&
            crossover(t1.info.StartingTime, t1.info.EndingTime, t2.info.StartingTime, t2.info.EndingTime)) {

            val laterT = if(t1.info.StartingTime > t2.info.StartingTime) t1 else t2
            val otherT = if(laterT == t1) t2 else t1
            val otherStart = bindSchedule.getWeek(otherT.info.StartingTime)
            val endWeek = min(bindSchedule.getWeek(t1.info.EndingTime), bindSchedule.getWeek(t2.info.EndingTime))

            for (w in bindSchedule.getWeek(laterT.info.StartingTime)until endWeek step laterT.Period.toLong()) {
                if ((w - otherStart) % otherT.Period == 0L) return true
            }
        }
        return false
    }

}

private class DDLMap(val bindSchedule: Schedule) : TreeMap<Long, HashSet<DDlInfo>>() {

    /**
     * @return False when ddl already exists
     */
    fun addDDl(ddl: DDlInfo) : Boolean {
        val set = this[ddl.EndingTime]?: HashSet()
        this[ddl.EndingTime] = set
        return set.add(ddl)
    }

    fun removeDDl(ddl: DDlInfo) =
        this[ddl.EndingTime]?.let {
            it.remove(ddl)
            if (it.isEmpty()) remove(ddl.EndingTime)
        }

    /**
     * DDl is different from course (about ddl at 24:00)
     */
    fun getDDl(fromTime: Long, toTime: Long) : List<DDlInfo> = subMap(fromTime, false, toTime, true).values.let {
        val res = ArrayList<DDlInfo>()
        for (set in it)
            res.addAll(set)
        return res
    }

    fun addAll(ddlCollection: Collection<DDlInfo>) {
        for (ddl in ddlCollection)
            addDDl(ddl)
    }

    fun toList() : List<DDlInfo> = this.values.let {
        val res = ArrayList<DDlInfo>()
        for (set in it)
            res.addAll(set)
        return res
    }
}

/**
 * Config used to generate data
 */
data class TestDataConfig(val courseTryCnt: Int, val ddlCnt: Int, val totWeek: Long, val maxTime: Long)

class Schedule(private val context: Context, testData: TestDataConfig? = null) {

    private val courseMap = Array(7) { TemplateMap(this) }

    private val ddlMap = DDLMap(this)

    private val courseSet = HashSet<CourseInfo>()

    var termStartTime: Long = getTimeStamp(2022,9,5)


    /**
     * Load all data from disk
     * @see load
     */
    init {

        if (testData != null) {
            termInfo.StartingTime=termStartTime
            val rand = Random()
            val weekSec = 1000 * 3600 * 24 * 7
            for (i in 0 until testData.ddlCnt)
                addDDl(DDlInfo("Test ddl$i", i.toLong(),
                    abs(rand.nextLong()) % (weekSec * testData.totWeek)+termStartTime, "This is DDL $i balabalawalawala\n" +
                            "bababababababa", termStartTime))
            for (i in 0 until testData.courseTryCnt) {
                val course = CourseInfo("Test Course$i", termStartTime, weekSec * testData.totWeek+termStartTime, ArrayList(), "This is Course $i", "Classroom $i")
                val strTime = (rand.nextInt(testData.maxTime.toInt())).toLong()
                val template = CourseTemplate(rand.nextInt(7).toLong(),
                    strTime,
                    min((strTime + 2 + rand.nextInt(2)).toLong(), (testData.maxTime)),
                    1
                )
                template.info = course
                course.TimeInfo.add(template)
                addCourse(course)
            }
        }
        else {
            load(this, context)

            for (course in this.courseSet)
                addCourse(course)
        }
    }

    /**
     * @see com.example.myapplication.backstage.getWeek
     */
    fun getWeek(time: Long) = getWeek(termStartTime, time)

    /**
     * Save all data to disk
     * @see save
     */
    fun saveAll() = save(this, context)

    fun getTemplate(time: Long) : CourseTemplate? = getTemplate(getDay(time), getHour(time) , getWeek(time))

    /**
     * @see TemplateMap.getTemplate
     */
    fun getTemplate(time: Long, colum: Long, week: Long) : CourseTemplate? {
        return courseMap[colum.toInt()].getTemplate(time,week)
    }

    /**
     * Get all template in given day of week
     */
    fun getTemplate(colum: Long, week:Long) : List<CourseTemplate> {
        val dayMap = courseMap[colum.toInt()]
        val res = ArrayList<CourseTemplate>()
        for ((k, v) in dayMap) {
            if (res.isNotEmpty() && k < res.last().EndingTime) continue
            for (template in v) {
                if (templateAvailable(template, week.toShort())) {
                    res.add(template)
                    break
                }
            }
        }
        return res
    }

    /**
     * @see TemplateMap.addTemplate
     */
    fun addTemplate(courseTemplate: CourseTemplate) : Boolean {
        return courseMap[courseTemplate.Column.toInt()].addTemplate(courseTemplate)
    }

    /**
     * @see TemplateMap.removeTemplate
     */
    fun removeTemplate(courseTemplate: CourseTemplate) {
        courseMap[courseTemplate.Column.toInt()].removeTemplate(courseTemplate)
    }

    /**
     * Add a course to schedule
     * @return False when add fail due to conflict
     */
    fun addCourse(course: CourseInfo) : Boolean {

        for (template in course.TimeInfo)
            if(courseMap[template.Column.toInt()].conflictCheck(template))
                return false

        for (template in course.TimeInfo)
            courseMap[template.Column.toInt()].addTemplate(template, false)

        courseSet.add(course)

        return true
    }

    /**
     * Remove a course from schedule
     */
    fun removeCourse(course: CourseInfo) {
        for (template in course.TimeInfo)
            courseMap[template.Column.toInt()].removeTemplate(template)

        courseSet.remove(course)
    }

    /**
     * @see DDLMap.addDDl
     */
    fun addDDl(ddl: DDlInfo) {
        ddlMap.addDDl(ddl)
    }

    /**
     * @see DDLMap.removeDDl
     */
    fun removeDDl(ddl : DDlInfo) {
        ddlMap.removeDDl(ddl)
    }

    /**
     * @see DDLMap.getDDl
     */
    fun getDDl(fromTime: Long, toTime: Long) : List<DDlInfo> = ddlMap.getDDl(fromTime, toTime)

    /**
     * Get all ddl in given day (day and week start from 0)
     */
    fun getDDl(week: Int, day: Int):  List<DDlInfo> = (termStartTime + 1L * (week * 7 + day) * 24 * 3600 * 1000).let {
        return getDDl(it, it + 24 * 3600 * 1000)
    }

    fun getAllDDl() : List<DDlInfo> = ddlMap.toList()

    fun getAllCourse() : List<CourseInfo> = courseSet.toList()


    internal fun templateAvailable(template: CourseTemplate, week: Short) : Boolean {
        val startTime = getWeek(template.info.StartingTime)
        val endTime = getWeek(template.info.EndingTime)
        return week in startTime until endTime && (week - startTime) % template.Period == 0L

    }
}
