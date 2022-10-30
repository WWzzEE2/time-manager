package com.example.myapplication.backstage

import java.util.TreeMap
import java.util.TreeSet
import kotlin.collections.ArrayList
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * @author WWzzEE2
 * @since v1
 *
 * The backstage functions of the program
 */

tailrec fun gcd(a: Int, b: Int): Int = if (b == 0) abs(a) else gcd(b, a % b)
tailrec fun gcd(a: Long, b: Long): Long = if (b == 0L) abs(a) else gcd(b, a % b)
fun crossover(s1: Int, e1: Int, s2: Int, e2: Int) = max(s1, s2) < min(e1, e2)
fun crossover(s1: Long, e1: Long, s2: Long, e2: Long) = max(s1, s2) < min(e1, e2)

// TODO not my work
fun getWeek(time:Long) : Int {
    return 114514
}

class TemplateMap : TreeMap<Short, HashSet<CourseTemplate>>() {

    /**
     * Add a template
     * @return The conflict course when add fails
     */
    fun addTemplate(template: CourseTemplate, check: Boolean = true) : Boolean {
        if (check && conflictCheck(template)) return false
        val set = this[template.StartingTime]?: HashSet()
        set.add(template)
        this[template.StartingTime] = set
        return true
    }

    /**
     * Add a course
     */
    fun removeTemplate(template: CourseTemplate) = this[template.StartingTime]?.remove(template)

    /**
     *  Check if there are any courses at given time
     *  The course's time is treated as [startTime, endTime)
     */
    fun getTemplate(time:Short, week:Long) : CourseTemplate? {
        for ((k, v) in this) {
            if (k > time) break
            for (t in v) {
                val startWeek = getWeek(t.info.StartingTime)
                if (t.EndingTime > time && week > startWeek && ((week - startWeek) % t.Period == 0L)) return t
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
            for (t in v) if (conflictCheck(t, template)) return false
        }
        return true
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
            val otherStart = getWeek(otherT.info.StartingTime)
            val endWeek = min(getWeek(t1.info.EndingTime), getWeek(t2.info.EndingTime))

            for (w in getWeek(laterT.info.StartingTime)until endWeek step laterT.Period.toInt()) {
                if ((w - otherStart) % otherT.Period == 0L) return true
            }
        }
        return false
    }

}

object Schedule {

    private val course_map = Array(7) { TemplateMap() }

    private val ddl_set = TreeSet<DDlInfo> { o1, o2 ->
        (o1.EndingTime - o2.EndingTime).toInt()
    }

    /**
     * Load course and ddl from disk
     */
    fun initAll(courseList: List<CourseInfo>, ddlList: List<DDlInfo>) {
        for (course in courseList)
            addCourse(course)

        for (ddl in ddlList)
            addDDl(ddl)
    }

    /**
     * @see TemplateMap.getTemplate
     */
    fun getTemplate(colum: Short, time: Short, week: Long) : CourseTemplate? {
        return course_map[colum.toInt()].getTemplate(time ,week)
    }

    /**
     * @see TemplateMap.addTemplate
     */
    fun addTemplate(courseTemplate: CourseTemplate) : Boolean {
        return course_map[courseTemplate.Column.toInt()].addTemplate(courseTemplate)
    }

    /**
     * @see TemplateMap.removeTemplate
     */
    fun removeTemplate(courseTemplate: CourseTemplate) {
        course_map[courseTemplate.Column.toInt()].removeTemplate(courseTemplate)
    }

    /**
     * Add a course to schedule
     * @return False when add fail due to conflict
     */
    fun addCourse(course: CourseInfo) : Boolean {

        for (template in course.TimeInfo)
            if(!course_map[template.Column.toInt()].conflictCheck(template))
                return false

        for (template in course.TimeInfo)
            course_map[template.Column.toInt()].addTemplate(template, false)

        return true
    }

    /**
     * Remove a course from schedule
     */
    fun removeCourse(course: CourseInfo) {
        for (template in course.TimeInfo)
            course_map[template.Column.toInt()].removeTemplate(template)
    }

    /**
     * Add a new ddl
     */
    fun addDDl(ddl: DDlInfo) {
        ddl_set.add(ddl)
    }

    /**
     * Remove a ddl
     */
    fun removeDDl(ddl : DDlInfo) {
        ddl_set.remove(ddl)
    }

    fun getDDl(fromTime: DDlInfo, endTime: DDlInfo) : Set<DDlInfo> = ddl_set.subSet(fromTime, endTime)

}