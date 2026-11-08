package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.SchoolDatabase
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
import com.example.data.model.ZambianCurriculumHelper
import com.example.data.repository.SchoolRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class ScreenTab {
    DASHBOARD,
    WORKBOOK,
    PUPILS_TEACHERS,
    ATTENDANCE,
    TEACHER_SUITE,
    TIMETABLE_POLICY,
    COMMUNICATION,
    SETTINGS
}

class SchoolViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SchoolRepository

    init {
        val db = SchoolDatabase.getDatabase(application, viewModelScope)
        repository = SchoolRepository(db.schoolDao())
    }

    // Navigation State
    private val _currentTab = MutableStateFlow(ScreenTab.DASHBOARD)
    val currentTab: StateFlow<ScreenTab> = _currentTab.asStateFlow()

    fun setTab(tab: ScreenTab) {
        _currentTab.value = tab
    }

    // Database Streams
    val schoolProfile: StateFlow<SchoolProfile?> = repository.schoolProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allPupils: StateFlow<List<Pupil>> = repository.allPupils
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTeachers: StateFlow<List<Teacher>> = repository.allTeachers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAnnouncements: StateFlow<List<Announcement>> = repository.allAnnouncements
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allParentMessages: StateFlow<List<ParentMessage>> = repository.allParentMessages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allSchemes: StateFlow<List<SchemeOfWork>> = repository.allSchemes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allLessonPlans: StateFlow<List<LessonPlan>> = repository.allLessonPlans
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allMeetings: StateFlow<List<TeacherMeeting>> = repository.allMeetings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTimetableSlots: StateFlow<List<TimetableSlot>> = repository.allTimetableSlots
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Workbook & Assessment State
    private val _selectedWorkbookGrade = MutableStateFlow(4) // default Grade 4
    val selectedWorkbookGrade: StateFlow<Int> = _selectedWorkbookGrade.asStateFlow()

    private val _selectedWorkbookSubject = MutableStateFlow("Mathematics")
    val selectedWorkbookSubject: StateFlow<String> = _selectedWorkbookSubject.asStateFlow()

    private val _selectedWorkbookTerm = MutableStateFlow("Term 2")
    val selectedWorkbookTerm: StateFlow<String> = _selectedWorkbookTerm.asStateFlow()

    private val _curriculumMode = MutableStateFlow("ODC") // "ODC" or "CBC"
    val curriculumMode: StateFlow<String> = _curriculumMode.asStateFlow()

    val assessmentsForSelectedGrade: StateFlow<List<AssessmentRecord>> =
        repository.getAssessmentsForTerm("Term 2", 2026)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Report Card Dialog State
    private val _reportCardPupil = MutableStateFlow<Pupil?>(null)
    val reportCardPupil: StateFlow<Pupil?> = _reportCardPupil.asStateFlow()

    private val _pupilAssessmentsForReport = MutableStateFlow<List<AssessmentRecord>>(emptyList())
    val pupilAssessmentsForReport: StateFlow<List<AssessmentRecord>> = _pupilAssessmentsForReport.asStateFlow()

    fun selectWorkbookGrade(grade: Int) {
        _selectedWorkbookGrade.value = grade
    }

    fun selectWorkbookSubject(subject: String) {
        _selectedWorkbookSubject.value = subject
    }

    fun selectWorkbookTerm(term: String) {
        _selectedWorkbookTerm.value = term
    }

    fun toggleCurriculumMode(mode: String) {
        _curriculumMode.value = mode
    }

    fun openReportCard(pupil: Pupil) {
        _reportCardPupil.value = pupil
        viewModelScope.launch {
            repository.getAssessmentsForPupil(pupil.id, _selectedWorkbookTerm.value, 2026)
                .collect { list ->
                    _pupilAssessmentsForReport.value = list
                }
        }
    }

    fun closeReportCard() {
        _reportCardPupil.value = null
        _pupilAssessmentsForReport.value = emptyList()
    }

    // Assessment Operations (Excel Workbook live editing)
    fun saveOrUpdateAssessmentScore(
        pupilId: Long,
        subject: String,
        gradeLevel: Int,
        continuousAssessment: Double,
        midTermScore: Double,
        terminalExamScore: Double,
        comment: String = ""
    ) {
        viewModelScope.launch {
            val total = continuousAssessment + midTermScore + terminalExamScore
            val ecz = ZambianCurriculumHelper.calculateEczGrade(total)
            val cbc = ZambianCurriculumHelper.calculateCbcLevel(total)

            val existing = pupilAssessmentsForReport.value.find { it.pupilId == pupilId && it.subject == subject }
                ?: assessmentsForSelectedGrade.value.find { it.pupilId == pupilId && it.subject == subject }

            if (existing != null) {
                repository.updateAssessment(
                    existing.copy(
                        continuousAssessment = continuousAssessment,
                        midTermScore = midTermScore,
                        terminalExamScore = terminalExamScore,
                        totalScore = total,
                        eczGrade = ecz,
                        cbcPerformanceLevel = cbc,
                        teacherComment = comment.ifBlank { existing.teacherComment }
                    )
                )
            } else {
                repository.insertAssessment(
                    AssessmentRecord(
                        pupilId = pupilId,
                        subject = subject,
                        term = _selectedWorkbookTerm.value,
                        year = 2026,
                        gradeLevel = gradeLevel,
                        curriculumType = _curriculumMode.value,
                        continuousAssessment = continuousAssessment,
                        midTermScore = midTermScore,
                        terminalExamScore = terminalExamScore,
                        totalScore = total,
                        eczGrade = ecz,
                        cbcPerformanceLevel = cbc,
                        teacherComment = comment.ifBlank { "Performance assessed per Zambian curriculum standard." }
                    )
                )
            }
        }
    }

    // Pupil Management
    fun addPupil(pupil: Pupil) {
        viewModelScope.launch {
            val newId = repository.insertPupil(pupil)
            // Also generate baseline subject assessments for new pupil
            val subjects = if (pupil.gradeLevel <= 7) {
                listOf("Mathematics", "English Language", "Integrated Science", "Social Studies", "CTS", "Zambian Language (Chinyanja)")
            } else {
                listOf("Mathematics", "English Language", "Biology", "Chemistry", "Physics", "Civic Education")
            }
            subjects.forEach { sub ->
                repository.insertAssessment(
                    AssessmentRecord(
                        pupilId = newId,
                        subject = sub,
                        term = "Term 2",
                        year = 2026,
                        gradeLevel = pupil.gradeLevel,
                        curriculumType = _curriculumMode.value,
                        continuousAssessment = 20.0,
                        midTermScore = 15.0,
                        terminalExamScore = 35.0,
                        totalScore = 70.0,
                        eczGrade = ZambianCurriculumHelper.calculateEczGrade(70.0),
                        cbcPerformanceLevel = ZambianCurriculumHelper.calculateCbcLevel(70.0),
                        teacherComment = "Newly enrolled pupil."
                    )
                )
            }
        }
    }

    fun updatePupil(pupil: Pupil) {
        viewModelScope.launch {
            repository.updatePupil(pupil)
        }
    }

    fun deletePupil(pupil: Pupil) {
        viewModelScope.launch {
            repository.deletePupil(pupil)
        }
    }

    // Teacher Management
    fun addTeacher(teacher: Teacher) {
        viewModelScope.launch {
            repository.insertTeacher(teacher)
        }
    }

    fun updateTeacher(teacher: Teacher) {
        viewModelScope.launch {
            repository.updateTeacher(teacher)
        }
    }

    fun deleteTeacher(teacher: Teacher) {
        viewModelScope.launch {
            repository.deleteTeacher(teacher)
        }
    }

    // Announcements
    fun addAnnouncement(title: String, message: String, category: String) {
        viewModelScope.launch {
            repository.insertAnnouncement(
                Announcement(
                    title = title,
                    message = message,
                    category = category,
                    date = "Today, Just now",
                    author = schoolProfile.value?.headteacherName ?: "Administration",
                    isRealTime = true
                )
            )
        }
    }

    fun deleteAnnouncement(announcement: Announcement) {
        viewModelScope.launch {
            repository.deleteAnnouncement(announcement)
        }
    }

    // Parent Communication
    fun sendParentMessage(pupilName: String, phone: String, subject: String, message: String, type: String) {
        viewModelScope.launch {
            repository.insertParentMessage(
                ParentMessage(
                    pupilName = pupilName,
                    parentPhone = phone,
                    subject = subject,
                    message = message,
                    timestamp = System.currentTimeMillis(),
                    messageType = type,
                    isSent = true
                )
            )
        }
    }

    // Teacher Suite: Schemes of Work, Lesson Plans, Meetings
    fun addSchemeOfWork(scheme: SchemeOfWork) {
        viewModelScope.launch {
            repository.insertScheme(scheme)
        }
    }

    fun updateSchemeStatus(scheme: SchemeOfWork, newStatus: String) {
        viewModelScope.launch {
            repository.updateScheme(scheme.copy(status = newStatus))
        }
    }

    fun addLessonPlan(plan: LessonPlan) {
        viewModelScope.launch {
            repository.insertLessonPlan(plan)
        }
    }

    fun addTeacherMeeting(meeting: TeacherMeeting) {
        viewModelScope.launch {
            repository.insertMeeting(meeting)
        }
    }

    // Attendance
    fun markAttendance(pupilId: Long, status: String, reason: String = "") {
        viewModelScope.launch {
            repository.insertAttendance(
                AttendanceRecord(
                    pupilId = pupilId,
                    date = "2026-11-08",
                    session = "Morning",
                    status = status,
                    reason = reason
                )
            )
        }
    }

    // School Profile Settings
    fun updateSchoolProfile(profile: SchoolProfile) {
        viewModelScope.launch {
            repository.updateSchoolProfile(profile)
        }
    }
}
