package com.example.data.repository

import com.example.data.dao.SchoolDao
import com.example.data.model.Announcement
import com.example.data.model.AssessmentRecord
import com.example.data.model.AttendanceRecord
import com.example.data.model.LessonPlan
import com.example.data.model.ParentMessage
import com.example.data.model.Pupil
import com.example.data.model.SchemeOfWork
import com.example.data.model.SchoolProfile
import com.example.data.model.Teacher
import com.example.data.model.TeacherMeeting
import com.example.data.model.TimetableSlot
import kotlinx.coroutines.flow.Flow

class SchoolRepository(private val dao: SchoolDao) {

    val schoolProfile: Flow<SchoolProfile?> = dao.getSchoolProfile()
    val allPupils: Flow<List<Pupil>> = dao.getAllPupils()
    val allTeachers: Flow<List<Teacher>> = dao.getAllTeachers()
    val pupilCount: Flow<Int> = dao.getPupilCount()
    val teacherCount: Flow<Int> = dao.getTeacherCount()
    val allAnnouncements: Flow<List<Announcement>> = dao.getAllAnnouncements()
    val allParentMessages: Flow<List<ParentMessage>> = dao.getAllParentMessages()
    val allSchemes: Flow<List<SchemeOfWork>> = dao.getAllSchemes()
    val allLessonPlans: Flow<List<LessonPlan>> = dao.getAllLessonPlans()
    val allMeetings: Flow<List<TeacherMeeting>> = dao.getAllMeetings()
    val allTimetableSlots: Flow<List<TimetableSlot>> = dao.getAllTimetableSlots()

    fun getPupilsByGrade(grade: Int): Flow<List<Pupil>> = dao.getPupilsByGrade(grade)

    suspend fun getPupilById(id: Long): Pupil? = dao.getPupilById(id)

    fun getAssessmentsForTerm(term: String, year: Int): Flow<List<AssessmentRecord>> =
        dao.getAssessmentsForTerm(term, year)

    fun getAssessmentsByGradeAndTerm(gradeLevel: Int, term: String, year: Int): Flow<List<AssessmentRecord>> =
        dao.getAssessmentsByGradeAndTerm(gradeLevel, term, year)

    fun getAssessmentsForPupil(pupilId: Long, term: String, year: Int): Flow<List<AssessmentRecord>> =
        dao.getAssessmentsForPupil(pupilId, term, year)

    fun getAttendanceForDate(date: String): Flow<List<AttendanceRecord>> =
        dao.getAttendanceForDate(date)

    // Zambian MoGE Student Daily Attendance
    val allStudentAttendance: Flow<List<com.example.data.model.Attendance>> = dao.getAllStudentAttendance()

    fun getAttendanceForDateAndSession(date: String, session: String): Flow<List<com.example.data.model.Attendance>> =
        dao.getAttendanceForDateAndSession(date, session)

    fun getAttendanceByGradeDateAndSession(gradeLevel: Int, date: String, session: String): Flow<List<com.example.data.model.Attendance>> =
        dao.getAttendanceByGradeDateAndSession(gradeLevel, date, session)

    fun getPupilTermAttendance(pupilId: Long, term: String, year: Int): Flow<List<com.example.data.model.Attendance>> =
        dao.getPupilTermAttendance(pupilId, term, year)

    suspend fun insertStudentAttendance(attendance: com.example.data.model.Attendance) =
        dao.insertStudentAttendance(attendance)

    suspend fun insertAllStudentAttendance(attendances: List<com.example.data.model.Attendance>) =
        dao.insertAllStudentAttendance(attendances)

    suspend fun updateStudentAttendance(attendance: com.example.data.model.Attendance) =
        dao.updateStudentAttendance(attendance)

    suspend fun deleteStudentAttendance(attendance: com.example.data.model.Attendance) =
        dao.deleteStudentAttendance(attendance)

    fun getTimetableForGrade(grade: String): Flow<List<TimetableSlot>> =
        dao.getTimetableForGrade(grade)

    suspend fun updateSchoolProfile(profile: SchoolProfile) = dao.insertOrUpdateProfile(profile)

    suspend fun insertPupil(pupil: Pupil) = dao.insertPupil(pupil)
    suspend fun updatePupil(pupil: Pupil) = dao.updatePupil(pupil)
    suspend fun deletePupil(pupil: Pupil) = dao.deletePupil(pupil)

    suspend fun insertTeacher(teacher: Teacher) = dao.insertTeacher(teacher)
    suspend fun updateTeacher(teacher: Teacher) = dao.updateTeacher(teacher)
    suspend fun deleteTeacher(teacher: Teacher) = dao.deleteTeacher(teacher)

    suspend fun insertAssessment(assessment: AssessmentRecord) = dao.insertAssessment(assessment)
    suspend fun updateAssessment(assessment: AssessmentRecord) = dao.updateAssessment(assessment)
    suspend fun deleteAssessment(assessment: AssessmentRecord) = dao.deleteAssessment(assessment)

    suspend fun insertAttendance(record: AttendanceRecord) = dao.insertAttendance(record)
    suspend fun insertAllAttendance(records: List<AttendanceRecord>) = dao.insertAllAttendance(records)

    suspend fun insertAnnouncement(announcement: Announcement) = dao.insertAnnouncement(announcement)
    suspend fun deleteAnnouncement(announcement: Announcement) = dao.deleteAnnouncement(announcement)

    suspend fun insertParentMessage(message: ParentMessage) = dao.insertParentMessage(message)

    suspend fun insertScheme(scheme: SchemeOfWork) = dao.insertScheme(scheme)
    suspend fun updateScheme(scheme: SchemeOfWork) = dao.updateScheme(scheme)
    suspend fun deleteScheme(scheme: SchemeOfWork) = dao.deleteScheme(scheme)

    suspend fun insertLessonPlan(plan: LessonPlan) = dao.insertLessonPlan(plan)
    suspend fun updateLessonPlan(plan: LessonPlan) = dao.updateLessonPlan(plan)
    suspend fun deleteLessonPlan(plan: LessonPlan) = dao.deleteLessonPlan(plan)

    suspend fun insertMeeting(meeting: TeacherMeeting) = dao.insertMeeting(meeting)

    suspend fun insertTimetableSlot(slot: TimetableSlot) = dao.insertTimetableSlot(slot)
}
