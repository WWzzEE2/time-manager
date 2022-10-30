package com.example.myapplication.backstage

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import java.util.TreeMap
import java.util.TreeSet
import androidx.activity.ComponentActivity


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

    private val courseMap = Array(7) { TemplateMap() }

    private val ddlSet = TreeSet<DDlInfo> { o1, o2 -> (o1.EndingTime - o2.EndingTime).toInt() }

    private lateinit var courseSet: HashSet<CourseInfo>

    /**
     * Load all data from disk
     * @see load
     */
    fun initAll(activity: ComponentActivity) {

        val (ddl_list, course_list) = load(activity)

        courseSet = HashSet(course_list)

        for (course in this.courseSet)
            addCourse(course)

        ddlSet.addAll(ddl_list)

    }

    /**
     * Save all data to disk
     * @see save
     */
    fun saveAll(activity: ComponentActivity) = save(ddlSet.toList(), courseSet.toList(), activity)

    /**
     * @see TemplateMap.getTemplate
     */
    fun getTemplate(colum: Short, time: Short, week: Long) : CourseTemplate? {
        return courseMap[colum.toInt()].getTemplate(time ,week)
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
     * Add a new ddl
     */
    fun addDDl(ddl: DDlInfo) {
        ddlSet.add(ddl)
    }

    /**
     * Remove a ddl
     */
    fun removeDDl(ddl : DDlInfo) {
        ddlSet.remove(ddl)
    }

    fun getDDl(fromTime: DDlInfo, endTime: DDlInfo) : Set<DDlInfo> = ddlSet.subSet(fromTime, endTime)

}