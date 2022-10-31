package com.example.myapplication

import com.example.myapplication.backstage.Schedule
import com.example.myapplication.backstage.TestDataConfig
import com.example.myapplication.backstage.crossover
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ScheduleTest {

    @Test
    fun addition_isCorrect() {
        assertEquals(crossover(7,10, 8, 11), true)
    }
}