package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Announcement
import com.example.data.model.AssessmentRecord
import com.example.data.model.Attendance
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

@Dao
interface SchoolDao {

    // School Profile
    @Query("SELECT * FROM school_profile WHERE id = 1 LIMIT 1")
    fun getSchoolProfile(): Flow<SchoolProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: SchoolProfile)

    // Pupils
    @Query("SELECT * FROM pupils ORDER BY gradeLevel ASC, fullName ASC")
    fun getAllPupils(): Flow<List<Pupil>>

    @Query("SELECT * FROM pupils WHERE gradeLevel = :gradeLevel ORDER BY fullName ASC")
    fun getPupilsByGrade(gradeLevel: Int): Flow<List<Pupil>>

    @Query("SELECT * FROM pupils WHERE id = :id LIMIT 1")
    suspend fun getPupilById(id: Long): Pupil?

    @Query("SELECT COUNT(*) FROM pupils")
    fun getPupilCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPupil(pupil: Pupil): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllPupils(pupils: List<Pupil>)

    @Update
    suspend fun updatePupil(pupil: Pupil)

    @Delete
    suspend fun deletePupil(pupil: Pupil)

    // Teachers
    @Query("SELECT * FROM teachers ORDER BY fullName ASC")
    fun getAllTeachers(): Flow<List<Teacher>>

    @Query("SELECT COUNT(*) FROM teachers")
    fun getTeacherCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeacher(teacher: Teacher): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllTeachers(teachers: List<Teacher>)

    @Update
    suspend fun updateTeacher(teacher: Teacher)

    @Delete
    suspend fun deleteTeacher(teacher: Teacher)

    // Assessments & Workbook
    @Query("SELECT * FROM assessments WHERE term = :term AND year = :year ORDER BY id ASC")
    fun getAssessmentsForTerm(term: String, year: Int): Flow<List<AssessmentRecord>>

    @Query("SELECT * FROM assessments WHERE gradeLevel = :gradeLevel AND term = :term AND year = :year ORDER BY id ASC")
    fun getAssessmentsByGradeAndTerm(gradeLevel: Int, term: String, year: Int): Flow<List<AssessmentRecord>>

    @Query("SELECT * FROM assessments WHERE pupilId = :pupilId AND term = :term AND year = :year")
    fun getAssessmentsForPupil(pupilId: Long, term: String, year: Int): Flow<List<AssessmentRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssessment(assessment: AssessmentRecord): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllAssessments(assessments: List<AssessmentRecord>)

    @Update
    suspend fun updateAssessment(assessment: AssessmentRecord)

    @Delete
    suspend fun deleteAssessment(assessment: AssessmentRecord)

    // Attendance
    @Query("SELECT * FROM attendance WHERE date = :date")
    fun getAttendanceForDate(date: String): Flow<List<AttendanceRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(record: AttendanceRecord): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllAttendance(records: List<AttendanceRecord>)

    // Zambian MoGE Standard Student Daily Attendance
    @Query("SELECT * FROM student_attendance ORDER BY date DESC, pupilName ASC")
    fun getAllStudentAttendance(): Flow<List<Attendance>>

    @Query("SELECT * FROM student_attendance WHERE date = :date AND session = :session ORDER BY pupilName ASC")
    fun getAttendanceForDateAndSession(date: String, session: String): Flow<List<Attendance>>

    @Query("SELECT * FROM student_attendance WHERE gradeLevel = :gradeLevel AND date = :date AND session = :session ORDER BY pupilName ASC")
    fun getAttendanceByGradeDateAndSession(gradeLevel: Int, date: String, session: String): Flow<List<Attendance>>

    @Query("SELECT * FROM student_attendance WHERE pupilId = :pupilId AND term = :term AND academicYear = :year ORDER BY date ASC")
    fun getPupilTermAttendance(pupilId: Long, term: String, year: Int): Flow<List<Attendance>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudentAttendance(attendance: Attendance): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllStudentAttendance(attendances: List<Attendance>)

    @Update
    suspend fun updateStudentAttendance(attendance: Attendance)

    @Delete
    suspend fun deleteStudentAttendance(attendance: Attendance)

    // Announcements
    @Query("SELECT * FROM announcements ORDER BY id DESC")
    fun getAllAnnouncements(): Flow<List<Announcement>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnnouncement(announcement: Announcement): Long

    @Delete
    suspend fun deleteAnnouncement(announcement: Announcement)

    // Parent Communication
    @Query("SELECT * FROM parent_messages ORDER BY timestamp DESC")
    fun getAllParentMessages(): Flow<List<ParentMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParentMessage(message: ParentMessage): Long

    // Schemes of Work
    @Query("SELECT * FROM schemes_of_work ORDER BY weekNumber ASC")
    fun getAllSchemes(): Flow<List<SchemeOfWork>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScheme(scheme: SchemeOfWork): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllSchemes(schemes: List<SchemeOfWork>)

    @Update
    suspend fun updateScheme(scheme: SchemeOfWork)

    @Delete
    suspend fun deleteScheme(scheme: SchemeOfWork)

    // Lesson Plans
    @Query("SELECT * FROM lesson_plans ORDER BY id DESC")
    fun getAllLessonPlans(): Flow<List<LessonPlan>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLessonPlan(plan: LessonPlan): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllLessonPlans(plans: List<LessonPlan>)

    @Update
    suspend fun updateLessonPlan(plan: LessonPlan)

    @Delete
    suspend fun deleteLessonPlan(plan: LessonPlan)

    // Teacher Meetings & CPD
    @Query("SELECT * FROM teacher_meetings ORDER BY id DESC")
    fun getAllMeetings(): Flow<List<TeacherMeeting>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeeting(meeting: TeacherMeeting): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllMeetings(meetings: List<TeacherMeeting>)

    // Timetable Slots
    @Query("SELECT * FROM timetable_slots ORDER BY periodNumber ASC")
    fun getAllTimetableSlots(): Flow<List<TimetableSlot>>

    @Query("SELECT * FROM timetable_slots WHERE grade = :grade ORDER BY periodNumber ASC")
    fun getTimetableForGrade(grade: String): Flow<List<TimetableSlot>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTimetableSlot(slot: TimetableSlot): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllTimetableSlots(slots: List<TimetableSlot>)
}
