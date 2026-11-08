package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Zambian Ministry of Education Standard Attendance Status Codes
 */
enum class AttendanceStatus(val code: String, val title: String, val description: String) {
    PRESENT("P", "Present", "Pupil present on time for assembly & register roll call"),
    ABSENT("A", "Absent", "Unexcused absence / truant"),
    LATE("L", "Late", "Arrived after 07:30hrs morning assembly roll call"),
    EXCUSED("E", "Excused", "Excused with valid written guardian explanation"),
    SICK("S", "Sick", "Pupil absent due to illness / registered clinic visit")
}

enum class AttendanceSession(val title: String) {
    MORNING("Morning"),
    AFTERNOON("Afternoon")
}

/**
 * Room Entity representing Student Daily Attendance formatted according to
 * the Republic of Zambia Ministry of General Education (MoGE) School Register standards.
 */
@Entity(
    tableName = "student_attendance",
    indices = [
        Index(value = ["pupilId", "date", "session"], unique = true),
        Index(value = ["gradeLevel", "date", "session"])
    ]
)
data class Attendance(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val pupilId: Long,
    val pupilName: String,
    val admissionNumber: String,
    val gradeLevel: Int,              // 1 to 7 (Primary), 8 to 12 (Secondary)
    val section: String,                 // e.g. "Grade 4A", "Grade 7 Blue"
    val gender: String = "Male",         // "Male" or "Female" for MoGE gender disaggregation
    val date: String,                    // ISO YYYY-MM-DD, e.g. "2026-11-08"
    val term: String = "Term 2",         // "Term 1", "Term 2", "Term 3"
    val academicYear: Int = 2026,
    val weekNumber: Int = 9,             // Term week 1 to 13
    val session: String = "Morning",     // "Morning" or "Afternoon"
    val status: String = "Present",      // Present, Absent, Late, Excused, Sick
    val statusCode: String = "P",        // P, A, L, E, S (Official Zambian Register Symbol)
    val reason: String = "",             // Optional explanation
    val recordedByTsNumber: String = "TS-811409", // Teaching Service Commission Number
    val timestamp: Long = System.currentTimeMillis()
)
