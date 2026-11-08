package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "school_profile")
data class SchoolProfile(
    @PrimaryKey val id: Int = 1,
    val schoolName: String = "Chilenje Model Primary & Secondary School",
    val motto: String = "One Zambia, One Nation - Knowledge, Integrity, Leadership",
    val emisNumber: String = "ZM-LUS-2026-4401",
    val province: String = "Lusaka Province",
    val district: String = "Lusaka District",
    val address: String = "P.O. Box 50012, Burma Road, Chilenje, Lusaka, Zambia",
    val headteacherName: String = "Mr. Patrick M. Mwamba",
    val headteacherTitle: String = "Headteacher (B.Ed, UNZA)",
    val currentTerm: String = "Term 2, 2026",
    val academicYear: Int = 2026,
    val nextTermBegins: String = "07 September 2026",
    val showCoatOfArms: Boolean = true,
    val showNationalFlag: Boolean = true,
    val defaultCurriculum: String = "ODC", // "ODC" or "CBC"
    val schoolPhone: String = "+260 211 254890",
    val schoolEmail: String = "admin@chilenjemodel.edu.zm"
)

@Entity(tableName = "pupils")
data class Pupil(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fullName: String,
    val admissionNumber: String,
    val gradeLevel: Int, // 1 to 12
    val section: String, // e.g. "Grade 4A", "Grade 7 Blue", "Grade 9 Green", "Grade 11 Science"
    val levelCategory: String, // "PRIMARY" (Grades 1-7) or "SECONDARY" (Grades 8-12)
    val gender: String, // "Male" or "Female"
    val dateOfBirth: String,
    val parentGuardianName: String,
    val parentPhone: String,
    val parentEmail: String = "",
    val attendanceDaysPresent: Int = 58,
    val totalSchoolDays: Int = 60,
    val conductRemarks: String = "Well behaved, punctual and dedicated pupil."
)

@Entity(tableName = "teachers")
data class Teacher(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fullName: String,
    val tsNumber: String, // Teaching Service Commission Number (e.g. TS-884920)
    val department: String, // "Mathematics & Science", "Languages", "Social Sciences", "Primary Dept"
    val assignedClasses: String, // "Grade 7, Grade 9", "Grade 11 Science"
    val qualification: String, // "B.Sc. Ed (UNZA)", "Dip. Primary Ed (Evelyn Hone)", "B.Ed (Mukuba)"
    val phone: String,
    val email: String,
    val role: String // "Headteacher", "Deputy Head", "Senior Teacher", "Head of Dept (HOD)", "Class Teacher"
)

@Entity(tableName = "assessments")
data class AssessmentRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val pupilId: Long,
    val subject: String,
    val term: String = "Term 2",
    val year: Int = 2026,
    val gradeLevel: Int,
    val curriculumType: String = "ODC", // "ODC" or "CBC"
    val continuousAssessment: Double, // CA marks (max 30)
    val midTermScore: Double,         // Mid-Term exam (max 20)
    val terminalExamScore: Double,    // End-of-Term exam (max 50)
    val totalScore: Double,           // Auto: CA + Mid + Terminal (max 100)
    val eczGrade: String,             // ECZ standard: 1 to 9
    val cbcPerformanceLevel: String,  // CBC: EE (Exceeding), ME (Meeting), AE (Approaching), BE (Below)
    val teacherComment: String = "Good performance, keep striving."
)

@Entity(tableName = "attendance")
data class AttendanceRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val pupilId: Long,
    val date: String, // YYYY-MM-DD
    val session: String = "Morning", // "Morning" or "Afternoon"
    val status: String = "Present",  // "Present", "Absent", "Late", "Excused"
    val reason: String = ""
)

@Entity(tableName = "announcements")
data class Announcement(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val message: String,
    val category: String, // "URGENT", "EVENT", "ACADEMIC", "FEATURE_UPDATE", "PTA"
    val date: String,
    val author: String = "Administration",
    val isRealTime: Boolean = true
)

@Entity(tableName = "parent_messages")
data class ParentMessage(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val pupilName: String,
    val parentPhone: String,
    val subject: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val messageType: String = "ACADEMIC", // "ACADEMIC", "ATTENDANCE", "DISCIPLINE", "FEE"
    val isSent: Boolean = true
)

@Entity(tableName = "schemes_of_work")
data class SchemeOfWork(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val grade: String,
    val subject: String,
    val term: String,
    val weekNumber: Int,
    val topic: String,
    val specificOutcomes: String,
    val teachingMethods: String,
    val teachingAids: String,
    val references: String,
    val status: String = "Completed" // "Completed", "In Progress", "Pending"
)

@Entity(tableName = "lesson_plans")
data class LessonPlan(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val teacherName: String,
    val grade: String,
    val subject: String,
    val date: String,
    val duration: String = "40 Mins",
    val topic: String,
    val generalOutcome: String,
    val specificCompetences: String,
    val teachingAids: String,
    val introStep: String,
    val developmentSteps: String,
    val evaluationStep: String,
    val homeworkAssignment: String,
    val selfReflection: String
)

@Entity(tableName = "teacher_meetings")
data class TeacherMeeting(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val date: String,
    val department: String,
    val attendeeCount: Int,
    val agenda: String,
    val resolutions: String
)

@Entity(tableName = "timetable_slots")
data class TimetableSlot(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dayOfWeek: String, // Monday to Friday
    val periodNumber: Int, // 1 to 8
    val timeRange: String, // e.g. "07:30 - 08:15"
    val grade: String,     // e.g. "Grade 7A", "Grade 9"
    val subject: String,
    val teacherName: String,
    val room: String
)

// Helper calculations for Zambian Curriculums
object ZambianCurriculumHelper {
    /**
     * Examinations Council of Zambia (ECZ) Secondary & Primary scale
     * 1: Distinction (75-100%)
     * 2: Distinction (70-74%)
     * 3: Merit (65-69%)
     * 4: Merit (60-64%)
     * 5: Credit (55-59%)
     * 6: Credit (50-54%)
     * 7: Pass (45-49%)
     * 8: Pass (40-44%)
     * 9: Unsatisfactory (0-39%)
     */
    fun calculateEczGrade(score: Double): String {
        return when {
            score >= 75.0 -> "1"
            score >= 70.0 -> "2"
            score >= 65.0 -> "3"
            score >= 60.0 -> "4"
            score >= 55.0 -> "5"
            score >= 50.0 -> "6"
            score >= 45.0 -> "7"
            score >= 40.0 -> "8"
            else -> "9"
        }
    }

    fun getEczGradeDescription(grade: String): String {
        return when (grade) {
            "1" -> "Distinction (Exceptional)"
            "2" -> "Distinction (Very Good)"
            "3" -> "Merit (Above Average)"
            "4" -> "Merit (Good)"
            "5" -> "Credit (Competent)"
            "6" -> "Credit (Fair Competence)"
            "7" -> "Satisfactory Pass"
            "8" -> "Basic Pass"
            "9" -> "Unsatisfactory (Needs Support)"
            else -> "Ungraded"
        }
    }

    /**
     * Competence-Based Curriculum (CBC) / SBA Performance Descriptors:
     * Level 4: Exceeding Expectations (EE) [80-100%]
     * Level 3: Meeting Expectations (ME) [65-79%]
     * Level 2: Approaching Expectations (AE) [50-64%]
     * Level 1: Below Expectations (BE) [0-49%]
     */
    fun calculateCbcLevel(score: Double): String {
        return when {
            score >= 80.0 -> "EE"
            score >= 65.0 -> "ME"
            score >= 50.0 -> "AE"
            else -> "BE"
        }
    }

    fun getCbcLevelDescription(level: String): String {
        return when (level) {
            "EE" -> "Exceeding Expectations (Level 4)"
            "ME" -> "Meeting Expectations (Level 3)"
            "AE" -> "Approaching Expectations (Level 2)"
            "BE" -> "Below Expectations (Level 1)"
            else -> "Not Evaluated"
        }
    }
}
