package com.example

import com.example.data.model.Attendance
import com.example.data.model.AttendanceStatus
import com.example.data.model.ZambianCurriculumHelper
import org.junit.Assert.assertEquals
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testEczGradingScale() {
        assertEquals("1", ZambianCurriculumHelper.calculateEczGrade(85.0))
        assertEquals("2", ZambianCurriculumHelper.calculateEczGrade(72.0))
        assertEquals("3", ZambianCurriculumHelper.calculateEczGrade(68.0))
        assertEquals("4", ZambianCurriculumHelper.calculateEczGrade(62.0))
        assertEquals("5", ZambianCurriculumHelper.calculateEczGrade(57.0))
        assertEquals("6", ZambianCurriculumHelper.calculateEczGrade(52.0))
        assertEquals("7", ZambianCurriculumHelper.calculateEczGrade(47.0))
        assertEquals("8", ZambianCurriculumHelper.calculateEczGrade(42.0))
        assertEquals("9", ZambianCurriculumHelper.calculateEczGrade(35.0))
    }

    @Test
    fun testCbcPerformanceLevels() {
        assertEquals("EE", ZambianCurriculumHelper.calculateCbcLevel(88.0))
        assertEquals("ME", ZambianCurriculumHelper.calculateCbcLevel(72.0))
        assertEquals("AE", ZambianCurriculumHelper.calculateCbcLevel(55.0))
        assertEquals("BE", ZambianCurriculumHelper.calculateCbcLevel(40.0))
    }

    @Test
    fun testZambianAttendanceStatusAndCodes() {
        assertEquals("P", AttendanceStatus.PRESENT.code)
        assertEquals("A", AttendanceStatus.ABSENT.code)
        assertEquals("L", AttendanceStatus.LATE.code)
        assertEquals("E", AttendanceStatus.EXCUSED.code)
        assertEquals("S", AttendanceStatus.SICK.code)

        val attendance = Attendance(
            pupilId = 101L,
            pupilName = "Chileshe Bwalya",
            admissionNumber = "ZM-2026-0101",
            gradeLevel = 4,
            section = "Grade 4A",
            gender = "Male",
            date = "2026-11-08",
            term = "Term 2",
            academicYear = 2026,
            weekNumber = 9,
            session = "Morning",
            status = AttendanceStatus.PRESENT.title,
            statusCode = AttendanceStatus.PRESENT.code
        )

        assertEquals("P", attendance.statusCode)
        assertEquals("Present", attendance.status)
        assertEquals("Grade 4A", attendance.section)
    }
}
