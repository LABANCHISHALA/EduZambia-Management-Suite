package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.SchoolDatabase
import com.example.data.model.Attendance
import com.example.data.model.AttendanceSession
import com.example.data.model.AttendanceStatus
import com.example.data.model.Pupil
import com.example.data.model.SchoolProfile
import com.example.data.repository.SchoolRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * UI representation of a student in the daily attendance register
 */
data class AttendanceUiItem(
    val pupil: Pupil,
    val attendanceId: Long? = null,
    val currentStatus: AttendanceStatus = AttendanceStatus.PRESENT,
    val statusCode: String = "P",
    val reason: String = "",
    val recordedByTsNumber: String = "TS-811409"
)

/**
 * Ministry of General Education (Zambia) Gender-Disaggregated Register Statistics
 */
data class ZambianAttendanceStats(
    val totalEnrolled: Int = 0,
    val presentCount: Int = 0,
    val absentCount: Int = 0,
    val lateCount: Int = 0,
    val excusedCount: Int = 0,
    val sickCount: Int = 0,
    val overallAttendanceRate: Double = 0.0,
    val maleEnrolled: Int = 0,
    val malePresent: Int = 0,
    val maleAttendanceRate: Double = 0.0,
    val femaleEnrolled: Int = 0,
    val femalePresent: Int = 0,
    val femaleAttendanceRate: Double = 0.0
)

class AttendanceViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SchoolRepository

    init {
        val db = SchoolDatabase.getDatabase(application, viewModelScope)
        repository = SchoolRepository(db.schoolDao())
    }

    // Active Filters
    private val _selectedDate = MutableStateFlow("2026-11-08")
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    private val _selectedSession = MutableStateFlow("Morning")
    val selectedSession: StateFlow<String> = _selectedSession.asStateFlow()

    private val _selectedGrade = MutableStateFlow(4) // Grade 4 Default
    val selectedGrade: StateFlow<Int> = _selectedGrade.asStateFlow()

    private val _selectedTerm = MutableStateFlow("Term 2")
    val selectedTerm: StateFlow<String> = _selectedTerm.asStateFlow()

    // School Profile
    val schoolProfile: StateFlow<SchoolProfile?> = repository.schoolProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // All registered pupils
    val allPupils: StateFlow<List<Pupil>> = repository.allPupils
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // All student attendance records from Room database
    val allAttendance: StateFlow<List<Attendance>> = repository.allStudentAttendance
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Combined UI Register state for current grade, date & session
    val currentRegister: StateFlow<List<AttendanceUiItem>> = combine(
        allPupils,
        allAttendance,
        _selectedGrade,
        _selectedDate,
        _selectedSession
    ) { pupils, attendanceList, grade, date, session ->
        val filteredPupils = pupils.filter { it.gradeLevel == grade }
        val attendanceMap = attendanceList
            .filter { it.date == date && it.session == session }
            .associateBy { it.pupilId }

        filteredPupils.map { pupil ->
            val record = attendanceMap[pupil.id]
            val status = when (record?.statusCode) {
                "A" -> AttendanceStatus.ABSENT
                "L" -> AttendanceStatus.LATE
                "E" -> AttendanceStatus.EXCUSED
                "S" -> AttendanceStatus.SICK
                else -> AttendanceStatus.PRESENT
            }
            AttendanceUiItem(
                pupil = pupil,
                attendanceId = record?.id,
                currentStatus = status,
                statusCode = status.code,
                reason = record?.reason ?: "",
                recordedByTsNumber = record?.recordedByTsNumber ?: "TS-811409"
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Official MoGE Zambian Register Disaggregated Statistics
    val attendanceStats: StateFlow<ZambianAttendanceStats> = currentRegister.combine(_selectedGrade) { items, _ ->
        val total = items.size
        if (total == 0) return@combine ZambianAttendanceStats()

        val present = items.count { it.currentStatus == AttendanceStatus.PRESENT }
        val absent = items.count { it.currentStatus == AttendanceStatus.ABSENT }
        val late = items.count { it.currentStatus == AttendanceStatus.LATE }
        val excused = items.count { it.currentStatus == AttendanceStatus.EXCUSED }
        val sick = items.count { it.currentStatus == AttendanceStatus.SICK }

        // Zambian MoGE considers Present + Late as attended school
        val effectivePresent = present + late
        val rate = (effectivePresent.toDouble() / total) * 100

        val maleItems = items.filter { it.pupil.gender.equals("Male", ignoreCase = true) }
        val femaleItems = items.filter { it.pupil.gender.equals("Female", ignoreCase = true) }

        val maleEnrolled = maleItems.size
        val malePres = maleItems.count { it.currentStatus == AttendanceStatus.PRESENT || it.currentStatus == AttendanceStatus.LATE }
        val maleRate = if (maleEnrolled > 0) (malePres.toDouble() / maleEnrolled) * 100 else 0.0

        val femaleEnrolled = femaleItems.size
        val femalePres = femaleItems.count { it.currentStatus == AttendanceStatus.PRESENT || it.currentStatus == AttendanceStatus.LATE }
        val femaleRate = if (femaleEnrolled > 0) (femalePres.toDouble() / femaleEnrolled) * 100 else 0.0

        ZambianAttendanceStats(
            totalEnrolled = total,
            presentCount = present,
            absentCount = absent,
            lateCount = late,
            excusedCount = excused,
            sickCount = sick,
            overallAttendanceRate = rate,
            maleEnrolled = maleEnrolled,
            malePresent = malePres,
            maleAttendanceRate = maleRate,
            femaleEnrolled = femaleEnrolled,
            femalePresent = femalePres,
            femaleAttendanceRate = femaleRate
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ZambianAttendanceStats())

    // Filters Actions
    fun setDate(date: String) {
        _selectedDate.value = date
    }

    fun setSession(session: String) {
        _selectedSession.value = session
    }

    fun setGrade(grade: Int) {
        _selectedGrade.value = grade
    }

    fun setTerm(term: String) {
        _selectedTerm.value = term
    }

    /**
     * Mark an individual pupil with standard Zambian MoGE codes:
     * P = Present, A = Absent, L = Late, E = Excused, S = Sick
     */
    fun markPupilStatus(pupilId: Long, status: AttendanceStatus, reason: String = "") {
        viewModelScope.launch {
            val pupil = repository.getPupilById(pupilId) ?: return@launch
            val date = _selectedDate.value
            val session = _selectedSession.value
            val term = _selectedTerm.value

            val existing = allAttendance.value.find { it.pupilId == pupilId && it.date == date && it.session == session }

            val updatedAttendance = Attendance(
                id = existing?.id ?: 0,
                pupilId = pupil.id,
                pupilName = pupil.fullName,
                admissionNumber = pupil.admissionNumber,
                gradeLevel = pupil.gradeLevel,
                section = pupil.section,
                gender = pupil.gender,
                date = date,
                term = term,
                academicYear = 2026,
                weekNumber = 9,
                session = session,
                status = status.title,
                statusCode = status.code,
                reason = reason.ifBlank { existing?.reason ?: "" },
                recordedByTsNumber = "TS-811409",
                timestamp = System.currentTimeMillis()
            )

            if (existing != null) {
                repository.updateStudentAttendance(updatedAttendance)
            } else {
                repository.insertStudentAttendance(updatedAttendance)
            }

            // Also keep general pupil cumulative count in sync
            if (status == AttendanceStatus.PRESENT) {
                repository.updatePupil(pupil.copy(attendanceDaysPresent = (pupil.attendanceDaysPresent + 1).coerceAtMost(pupil.totalSchoolDays)))
            }
        }
    }

    /**
     * Mark all pupils in current grade as Present (Zambian roll call baseline)
     */
    fun markAllPresent() {
        viewModelScope.launch {
            val date = _selectedDate.value
            val session = _selectedSession.value
            val term = _selectedTerm.value
            val grade = _selectedGrade.value

            val currentItems = currentRegister.value
            val listToSave = currentItems.map { item ->
                Attendance(
                    id = item.attendanceId ?: 0,
                    pupilId = item.pupil.id,
                    pupilName = item.pupil.fullName,
                    admissionNumber = item.pupil.admissionNumber,
                    gradeLevel = grade,
                    section = item.pupil.section,
                    gender = item.pupil.gender,
                    date = date,
                    term = term,
                    academicYear = 2026,
                    weekNumber = 9,
                    session = session,
                    status = AttendanceStatus.PRESENT.title,
                    statusCode = AttendanceStatus.PRESENT.code,
                    reason = "",
                    recordedByTsNumber = "TS-811409",
                    timestamp = System.currentTimeMillis()
                )
            }
            repository.insertAllStudentAttendance(listToSave)
        }
    }

    /**
     * Generate an official Ministry of General Education Zambian Register Broadsheet text
     */
    fun generateMoGEAttendanceRegisterReport(): String {
        val profile = schoolProfile.value ?: SchoolProfile()
        val stats = attendanceStats.value
        val items = currentRegister.value
        val grade = _selectedGrade.value
        val date = _selectedDate.value
        val session = _selectedSession.value
        val term = _selectedTerm.value

        val sb = StringBuilder()
        sb.appendLine("==========================================================")
        sb.appendLine("REPUBLIC OF ZAMBIA - MINISTRY OF GENERAL EDUCATION")
        sb.appendLine("OFFICIAL CLASS ATTENDANCE REGISTER - FORM ZM-ATT/2026")
        sb.appendLine("==========================================================")
        sb.appendLine("School: ${profile.schoolName.uppercase()}")
        sb.appendLine("EMIS No: ${profile.emisNumber} | Province: ${profile.province}")
        sb.appendLine("Class/Grade: Grade $grade | Session: $session | Date: $date")
        sb.appendLine("Term: $term, 2026 (Week 9 of 13)")
        sb.appendLine("Teacher-in-Charge TS: TS-811409")
        sb.appendLine("----------------------------------------------------------")
        sb.appendLine("# | Pupil Full Name | Adm No | Sex | Code | Status & Remarks")
        sb.appendLine("----------------------------------------------------------")
        items.forEachIndexed { index, item ->
            val p = item.pupil
            val reasonStr = if (item.reason.isNotBlank()) "(${item.reason})" else ""
            sb.appendLine("${index + 1}. ${p.fullName} | ${p.admissionNumber} | ${p.gender.take(1)} | [${item.statusCode}] ${item.currentStatus.title} $reasonStr")
        }
        sb.appendLine("----------------------------------------------------------")
        sb.appendLine("ZAMBIAN MoGE DISAGGREGATED STATISTICAL SUMMARY:")
        sb.appendLine("• Total Enrolled: ${stats.totalEnrolled} (Boys: ${stats.maleEnrolled}, Girls: ${stats.femaleEnrolled})")
        sb.appendLine("• Present: ${stats.presentCount} | Late: ${stats.lateCount} | Absent: ${stats.absentCount}")
        sb.appendLine("• Sick / Clinic: ${stats.sickCount} | Excused: ${stats.excusedCount}")
        sb.appendLine("• Overall Class Attendance: ${String.format("%.1f", stats.overallAttendanceRate)}%")
        sb.appendLine("• Boys Attendance Rate: ${String.format("%.1f", stats.maleAttendanceRate)}%")
        sb.appendLine("• Girls Attendance Rate: ${String.format("%.1f", stats.femaleAttendanceRate)}%")
        sb.appendLine("----------------------------------------------------------")
        sb.appendLine("Register Legend: [P] Present | [A] Absent | [L] Late | [E] Excused | [S] Sick")
        sb.appendLine("Verified By Class Teacher: _________________ Date: $date")
        sb.appendLine("Approved By Headteacher: ${profile.headteacherName}")
        sb.appendLine("==========================================================")
        return sb.toString()
    }

    /**
     * Generate standard SMS notice to parent of absent learner
     */
    fun formatParentAbsenteeSms(pupil: Pupil, status: AttendanceStatus): String {
        val school = schoolProfile.value?.schoolName ?: "Chilenje Model School"
        return when (status) {
            AttendanceStatus.ABSENT ->
                "Dear Parent/Guardian of ${pupil.fullName}, your child was marked ABSENT for the ${_selectedSession.value} roll call on ${_selectedDate.value} at $school. Please submit an excuse note or contact the class teacher."
            AttendanceStatus.LATE ->
                "Dear Parent/Guardian of ${pupil.fullName}, your child arrived LATE (after 07:30hrs assembly) on ${_selectedDate.value} at $school. Please encourage prompt morning arrival."
            AttendanceStatus.SICK ->
                "Dear Parent/Guardian, ${pupil.fullName} was recorded SICK at the school clinic on ${_selectedDate.value} at $school. We wish them a speedy recovery."
            else -> ""
        }
    }
}
