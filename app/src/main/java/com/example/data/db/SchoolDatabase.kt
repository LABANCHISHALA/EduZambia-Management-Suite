package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.SchoolDao
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
import com.example.data.model.ZambianCurriculumHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        SchoolProfile::class,
        Pupil::class,
        Teacher::class,
        AssessmentRecord::class,
        AttendanceRecord::class,
        Attendance::class,
        Announcement::class,
        ParentMessage::class,
        SchemeOfWork::class,
        LessonPlan::class,
        TeacherMeeting::class,
        TimetableSlot::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SchoolDatabase : RoomDatabase() {

    abstract fun schoolDao(): SchoolDao

    companion object {
        @Volatile
        private var INSTANCE: SchoolDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): SchoolDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SchoolDatabase::class.java,
                    "zed_school_database"
                )
                    .addCallback(SchoolDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class SchoolDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database.schoolDao())
                    }
                }
            }
        }

        suspend fun populateDatabase(dao: SchoolDao) {
            // 1. Initial School Profile
            dao.insertOrUpdateProfile(
                SchoolProfile(
                    id = 1,
                    schoolName = "Chilenje Combined Primary & Secondary School",
                    motto = "One Zambia, One Nation - Knowledge, Integrity, Leadership",
                    emisNumber = "ZM-LUS-2026-4401",
                    province = "Lusaka Province",
                    district = "Lusaka District",
                    address = "Burma Road, Chilenje South, Lusaka, Zambia",
                    headteacherName = "Mr. Patrick M. Mwamba",
                    headteacherTitle = "Headteacher (B.Sc. Ed, UNZA)",
                    currentTerm = "Term 2, 2026",
                    academicYear = 2026,
                    nextTermBegins = "07 September 2026",
                    showCoatOfArms = true,
                    showNationalFlag = true,
                    defaultCurriculum = "ODC"
                )
            )

            // 2. Realistic Zambian Pupils (Primary & Secondary)
            val initialPupils = listOf(
                // Primary: Grade 4
                Pupil(1, "Chileshe Bwalya", "ZM-2026-0101", 4, "Grade 4A", "PRIMARY", "Male", "2016-04-12", "Mr. Peter Bwalya", "+260 977 452101", "pbwalya@gmail.com", 58, 60),
                Pupil(2, "Mutale Phiri", "ZM-2026-0102", 4, "Grade 4A", "PRIMARY", "Female", "2016-07-25", "Mrs. Mary Phiri", "+260 966 882310", "mphiri@gmail.com", 59, 60),
                Pupil(3, "Natasha Tembo", "ZM-2026-0103", 4, "Grade 4A", "PRIMARY", "Female", "2016-02-18", "Dr. Kelvin Tembo", "+260 971 334455", "ktembo@yahoo.com", 60, 60),
                Pupil(4, "Kondwani Banda", "ZM-2026-0104", 4, "Grade 4A", "PRIMARY", "Male", "2016-09-03", "Mr. Moses Banda", "+260 978 112233", "mbanda@gmail.com", 54, 60),
                Pupil(5, "Mapalo Lungu", "ZM-2026-0105", 4, "Grade 4A", "PRIMARY", "Female", "2016-11-14", "Ms. Grace Lungu", "+260 965 998877", "glungu@gmail.com", 57, 60),

                // Primary: Grade 7 (ECZ Composite Exam Class)
                Pupil(6, "Mwamba Mulenga", "ZM-2026-0201", 7, "Grade 7 Blue", "PRIMARY", "Male", "2013-05-19", "Eng. John Mulenga", "+260 977 778899", "jmulenga@gmail.com", 60, 60),
                Pupil(7, "Choolwe Hachambwa", "ZM-2026-0202", 7, "Grade 7 Blue", "PRIMARY", "Female", "2013-08-30", "Mrs. Rabecca Hachambwa", "+260 976 121212", "rhachambwa@gmail.com", 58, 60),
                Pupil(8, "Kabwe Musonda", "ZM-2026-0203", 7, "Grade 7 Blue", "PRIMARY", "Male", "2013-01-11", "Mr. Joseph Musonda", "+260 967 343434", "jmusonda@gmail.com", 56, 60),
                Pupil(9, "Bupe Chanda", "ZM-2026-0204", 7, "Grade 7 Blue", "PRIMARY", "Female", "2013-10-05", "Mrs. Alice Chanda", "+260 979 454545", "achanda@gmail.com", 59, 60),

                // Junior Secondary: Grade 9 (ECZ Junior Secondary Leaving Exam)
                Pupil(10, "Luyando Siame", "ZM-2026-0301", 9, "Grade 9 Green", "SECONDARY", "Female", "2011-03-14", "Mr. Collins Siame", "+260 977 676767", "csiame@gmail.com", 58, 60),
                Pupil(11, "Chikondi Zulu", "ZM-2026-0302", 9, "Grade 9 Green", "SECONDARY", "Male", "2011-06-22", "Mrs. Thandiwe Zulu", "+260 966 787878", "tzulu@gmail.com", 57, 60),
                Pupil(12, "Dalitso Sakala", "ZM-2026-0303", 9, "Grade 9 Green", "SECONDARY", "Male", "2011-09-08", "Pastor Aaron Sakala", "+260 971 898989", "asakala@gmail.com", 59, 60),

                // Senior Secondary: Grade 11 & 12 (ECZ School Certificate)
                Pupil(13, "Taonga Mwale", "ZM-2026-0401", 11, "Grade 11 Science", "SECONDARY", "Female", "2009-04-17", "Prof. Clement Mwale", "+260 977 909090", "cmwale@unza.zm", 60, 60),
                Pupil(14, "Nchimunya Mudenda", "ZM-2026-0402", 11, "Grade 11 Science", "SECONDARY", "Male", "2009-12-01", "Mrs. Brenda Mudenda", "+260 965 232323", "bmudenda@gmail.com", 58, 60),
                Pupil(15, "Thabo Kalunga", "ZM-2026-0403", 11, "Grade 11 Science", "SECONDARY", "Male", "2009-08-15", "Mr. George Kalunga", "+260 978 343434", "gkalunga@gmail.com", 55, 60)
            )
            dao.insertAllPupils(initialPupils)

            // 3. Realistic Zambian Teachers
            val initialTeachers = listOf(
                Teacher(1, "Mr. Patrick M. Mwamba", "TS-551020", "Administration", "All Grades", "B.Sc. Ed (UNZA), M.Ed", "+260 977 110022", "head@chilenjemodel.edu.zm", "Headteacher"),
                Teacher(2, "Mrs. Gertrude N. Mulenga", "TS-642188", "Mathematics & Science", "Grade 9, Grade 11", "B.Ed Mathematics (Mukuba Univ)", "+260 966 332211", "gmulenga@chilenjemodel.edu.zm", "Deputy Head & HOD Math"),
                Teacher(3, "Mr. David C. Banda", "TS-720194", "Languages", "Grade 7, Grade 9", "B.A. Ed English & Chinyanja (UNZA)", "+260 971 445566", "dbanda@chilenjemodel.edu.zm", "Senior Teacher"),
                Teacher(4, "Mrs. Charity K. Phiri", "TS-811409", "Primary Education", "Grade 4A", "Dip. Primary Education (Evelyn Hone)", "+260 965 778899", "cphiri@chilenjemodel.edu.zm", "Class Teacher"),
                Teacher(5, "Mr. Emmanuel Tembo", "TS-884920", "Social Sciences", "Grade 11 & 12", "B.Ed Civic Ed & History (Kwame Nkrumah)", "+260 978 990011", "etembo@chilenjemodel.edu.zm", "Subject Teacher")
            )
            dao.insertAllTeachers(initialTeachers)

            // 4. Sample Assessment Records for Automated Excel Workbook & Report Cards
            val subjectsPrimary = listOf("Mathematics", "English Language", "Integrated Science", "Social Studies", "CTS (Creative & Tech)", "Zambian Language (Chinyanja)")
            val subjectsSecondary = listOf("Mathematics", "English Language", "Biology", "Chemistry", "Physics", "Civic Education", "Geography")

            val initialAssessments = mutableListOf<AssessmentRecord>()

            // Assessments for Grade 4 pupils
            for (p in initialPupils.filter { it.gradeLevel == 4 }) {
                for (sub in subjectsPrimary) {
                    val ca = (20..28).random().toDouble()
                    val mid = (14..19).random().toDouble()
                    val terminal = (32..48).random().toDouble()
                    val total = ca + mid + terminal
                    val ecz = ZambianCurriculumHelper.calculateEczGrade(total)
                    val cbc = ZambianCurriculumHelper.calculateCbcLevel(total)
                    initialAssessments.add(
                        AssessmentRecord(
                            pupilId = p.id,
                            subject = sub,
                            term = "Term 2",
                            year = 2026,
                            gradeLevel = 4,
                            curriculumType = "ODC",
                            continuousAssessment = ca,
                            midTermScore = mid,
                            terminalExamScore = terminal,
                            totalScore = total,
                            eczGrade = ecz,
                            cbcPerformanceLevel = cbc,
                            teacherComment = if (total >= 70) "Excellent comprehension and application." else if (total >= 50) "Satisfactory progress, encourage more practice." else "Needs remedial support."
                        )
                    )
                }
            }

            // Assessments for Grade 7 pupils (ODC & CBC dual ready)
            for (p in initialPupils.filter { it.gradeLevel == 7 }) {
                for (sub in subjectsPrimary) {
                    val ca = (22..29).random().toDouble()
                    val mid = (15..20).random().toDouble()
                    val terminal = (35..49).random().toDouble()
                    val total = ca + mid + terminal
                    val ecz = ZambianCurriculumHelper.calculateEczGrade(total)
                    val cbc = ZambianCurriculumHelper.calculateCbcLevel(total)
                    initialAssessments.add(
                        AssessmentRecord(
                            pupilId = p.id,
                            subject = sub,
                            term = "Term 2",
                            year = 2026,
                            gradeLevel = 7,
                            curriculumType = "ODC",
                            continuousAssessment = ca,
                            midTermScore = mid,
                            terminalExamScore = terminal,
                            totalScore = total,
                            eczGrade = ecz,
                            cbcPerformanceLevel = cbc,
                            teacherComment = if (total >= 75) "Outstanding ECZ candidate readiness!" else "Steady performance."
                        )
                    )
                }
            }

            // Assessments for Grade 9 & 11
            for (p in initialPupils.filter { it.gradeLevel in listOf(9, 11) }) {
                for (sub in subjectsSecondary.take(5)) {
                    val ca = (19..28).random().toDouble()
                    val mid = (13..19).random().toDouble()
                    val terminal = (30..47).random().toDouble()
                    val total = ca + mid + terminal
                    val ecz = ZambianCurriculumHelper.calculateEczGrade(total)
                    val cbc = ZambianCurriculumHelper.calculateCbcLevel(total)
                    initialAssessments.add(
                        AssessmentRecord(
                            pupilId = p.id,
                            subject = sub,
                            term = "Term 2",
                            year = 2026,
                            gradeLevel = p.gradeLevel,
                            curriculumType = "ODC",
                            continuousAssessment = ca,
                            midTermScore = mid,
                            terminalExamScore = terminal,
                            totalScore = total,
                            eczGrade = ecz,
                            cbcPerformanceLevel = cbc,
                            teacherComment = if (total >= 75) "Distinction level, critical thinker." else "Competent."
                        )
                    )
                }
            }
            dao.insertAllAssessments(initialAssessments)

            // 5. Real-time Announcements & Updates
            val announcements = listOf(
                Announcement(
                    1,
                    "ECZ Grade 7 & 9 Mock Examination Timetable 2026",
                    "The Ministry of General Education provincial mock examinations timetable has been officially finalized. Parents are urged to ensure candidates arrive by 07:15hrs.",
                    "ACADEMIC",
                    "Today, 08:30"
                ),
                Announcement(
                    2,
                    "Zambia Flag & Cultural Heritage Day Celebrations",
                    "In honour of One Zambia One Nation, the school will host traditional performances, Bemba, Tonga, Nyanja poetry and cultural exhibits this Friday at the main hall.",
                    "EVENT",
                    "Yesterday"
                ),
                Announcement(
                    3,
                    "Continuous Assessment (CA) & SBA Submission Deadline",
                    "All subject teachers must finalize automated workbook entry for Term 2 Continuous Assessment marks by Friday 16:00hrs.",
                    "FEATURE_UPDATE",
                    "05 Nov 2026"
                ),
                Announcement(
                    4,
                    "PTA General Meeting & Report Card Day",
                    "Term 2 closing day and parent-teacher consultations are scheduled for Friday, 07 August 2026. Electronic and printed report cards will be distributed.",
                    "PTA",
                    "01 Nov 2026"
                )
            )
            for (a in announcements) dao.insertAnnouncement(a)

            // 6. Schemes of Work (Term 2)
            val schemes = listOf(
                SchemeOfWork(1, "Grade 4", "Mathematics", "Term 2", 1, "Fractions & Decimals", "Identify proper, improper fractions and convert to simple decimals.", "Expository, group discussion, flashcards", "Fraction charts, real fruit cuts", "Primary Maths Book 4, pg 45", "Completed"),
                SchemeOfWork(2, "Grade 4", "Integrated Science", "Term 2", 2, "Water Cycle & Conservation", "Describe evaporation, condensation and water purification in Zambia.", "Guided inquiry, simple distillation experiment", "Beakers, ice, kettle, diagrams", "Science Syllabus Gr 4, pg 22", "Completed"),
                SchemeOfWork(3, "Grade 7", "Mathematics", "Term 2", 3, "Algebraic Expressions & Equations", "Solve linear single-variable equations and evaluate expressions.", "Problem-solving heuristic, peer grading", "Algebra tiles, board examples", "ECZ Composite Revision 2026", "In Progress"),
                SchemeOfWork(4, "Grade 9", "English Language", "Term 2", 4, "Summary Writing & Comprehension", "Extract 5 main points on environmental degradation in Copperbelt.", "Silent reading, text markup, drafting", "Sample past ECZ papers 2025", "Junior Secondary English, pg 98", "In Progress"),
                SchemeOfWork(5, "Grade 11", "Biology", "Term 2", 5, "Plant Nutrition & Photosynthesis", "Demonstrate requirements for photosynthesis and starch test in leaves.", "Laboratory practical, hypothesis testing", "Iodine solution, ethanol, burner, variegated leaf", "Senior Biology Book 11, pg 70", "Planned")
            )
            dao.insertAllSchemes(schemes)

            // 7. Lesson Plans (Zambian Standard 5-Step)
            val lessonPlans = listOf(
                LessonPlan(
                    1,
                    "Mrs. Charity K. Phiri",
                    "Grade 4A",
                    "Mathematics",
                    "2026-11-08",
                    "40 Mins",
                    "Addition of Fractions with Like Denominators",
                    "Pupils should be able to add two fractions with equal denominators and simplify.",
                    "Critical thinking, numeracy fluency, team collaboration.",
                    "Paper strips, colored markers, chalkboard fraction circles.",
                    "Review halves and quarters using paper folding (5 mins).",
                    "Step 1: Teacher demonstrates 1/4 + 2/4 = 3/4 with strip.\nStep 2: Pupils work in pairs on worksheet.\nStep 3: Class discussion of common denominator rule.",
                    "Oral questions and 4 chalkboard exercises (5 mins).",
                    "Exercise 4B, Numbers 1 to 8 on page 52.",
                    "Lesson was successful; 85% of pupils mastered the like-denominator concept."
                ),
                LessonPlan(
                    2,
                    "Mrs. Gertrude N. Mulenga",
                    "Grade 9 Green",
                    "Mathematics",
                    "2026-11-07",
                    "80 Mins",
                    "Pythagoras Theorem in Real-Life Application",
                    "Apply a² + b² = c² to calculate distances and ladder leaning problems.",
                    "Spatial reasoning, ECZ examination techniques.",
                    "Geometrical sets, measuring tape, chalkboard grid.",
                    "Recall right-angled triangle properties and hypotenuse definition.",
                    "Step 1: Derive Pythagoras visually with unit squares.\nStep 2: Solve 3 classic Zambian past-paper problems.\nStep 3: Group inquiry on height of a flag post.",
                    "Individual speed test on 3 hypotenuse calculations.",
                    "Senior Prep Sheet 9: Problems 1 to 5.",
                    "Great pupil engagement during group measurement on the assembly quadrangle."
                )
            )
            dao.insertAllLessonPlans(lessonPlans)

            // 8. Teacher Meetings & CPD
            val meetings = listOf(
                TeacherMeeting(
                    1,
                    "CPD: Transitioning to CBC SBA in Primary & Junior Secondary",
                    "04 Nov 2026",
                    "All Academic Staff",
                    24,
                    "1. Understanding CBC rubric descriptors (EE, ME, AE, BE)\n2. Aligning Continuous Assessment with ECZ portals\n3. Teacher record keeping in automated workbooks",
                    "1. All teachers to maintain digital and physical SBA markbooks.\n2. Standardized rubric sheets to be attached to pupil portfolios.\n3. Next peer lesson review scheduled for 18th November."
                ),
                TeacherMeeting(
                    2,
                    "Science & Mathematics Departmental Briefing",
                    "28 Oct 2026",
                    "Math & Science Dept",
                    8,
                    "1. Laboratory safety inspection\n2. Grade 7 and 9 practical test dates\n3. Remedial classes for struggling learners",
                    "Saturday catch-up classes to commence at 08:00 for Grades 7 and 9."
                )
            )
            dao.insertAllMeetings(meetings)

            // 9. Timetable Slots
            val timetable = listOf(
                TimetableSlot(1, "Monday", 1, "07:30 - 08:15", "Grade 7 Blue", "Mathematics", "Mrs. Mulenga", "Room 7A"),
                TimetableSlot(2, "Monday", 2, "08:15 - 09:00", "Grade 7 Blue", "Integrated Science", "Mr. Banda", "Room 7A"),
                TimetableSlot(3, "Monday", 3, "09:00 - 09:45", "Grade 7 Blue", "English Language", "Mr. Banda", "Room 7A"),
                TimetableSlot(4, "Monday", 4, "10:15 - 11:00", "Grade 7 Blue", "Social Studies", "Mr. Tembo", "Room 7A"),
                TimetableSlot(5, "Monday", 5, "11:00 - 11:45", "Grade 7 Blue", "Zambian Language (Chinyanja)", "Mr. Banda", "Room 7A"),
                TimetableSlot(6, "Monday", 6, "11:45 - 12:30", "Grade 7 Blue", "CTS (Creative & Tech)", "Mrs. Phiri", "Workshop"),
                TimetableSlot(7, "Monday", 7, "13:30 - 14:15", "Grade 7 Blue", "Special Paper 1", "Mrs. Mulenga", "Room 7A"),
                TimetableSlot(8, "Monday", 8, "14:15 - 15:00", "Grade 7 Blue", "Special Paper 2 / Guidance", "Mr. Mwamba", "Room 7A"),

                TimetableSlot(9, "Monday", 1, "07:30 - 08:15", "Grade 11 Science", "Chemistry", "Mrs. Mulenga", "Lab 2"),
                TimetableSlot(10, "Monday", 2, "08:15 - 09:00", "Grade 11 Science", "Physics", "Mrs. Mulenga", "Lab 2"),
                TimetableSlot(11, "Monday", 3, "09:00 - 09:45", "Grade 11 Science", "Mathematics", "Mrs. Mulenga", "Room 11B"),
                TimetableSlot(12, "Monday", 4, "10:15 - 11:00", "Grade 11 Science", "English Language", "Mr. Banda", "Room 11B"),
                TimetableSlot(13, "Monday", 5, "11:00 - 11:45", "Grade 11 Science", "Biology", "Mr. Mwamba", "Lab 1")
            )
            dao.insertAllTimetableSlots(timetable)

            // 10. Parent Messages
            val messages = listOf(
                ParentMessage(1, "Mutale Phiri", "+260 966 882310", "Term 2 Mid-Term Commendation", "Dear Mrs. Phiri, Mutale has demonstrated exemplary leadership and attained 92% in Grade 4 Mathematics. Keep encouraging her!", System.currentTimeMillis() - 86400000, "ACADEMIC", true),
                ParentMessage(2, "Kondwani Banda", "+260 978 112233", "Attendance Follow-up", "Dear Mr. Banda, Kondwani was absent on Thursday morning. Please remember to send an excuse note to the class teacher.", System.currentTimeMillis() - 172800000, "ATTENDANCE", true),
                ParentMessage(3, "Mwamba Mulenga", "+260 977 778899", "Grade 7 Mock Prep & Clinic", "Greetings Eng. Mulenga, Saturday mock revision sessions for Grade 7 candidates begin this Saturday at 08:00hrs.", System.currentTimeMillis() - 250000000, "ACADEMIC", true)
            )
            // 11. Initial Zambian MoGE Standard Student Attendance
            val initialAttendance = mutableListOf<Attendance>()
            val todayStr = "2026-11-08"
            initialPupils.forEachIndexed { idx, p ->
                val (status, code) = when (idx) {
                    3 -> "Absent" to "A"
                    4 -> "Late" to "L"
                    7 -> "Sick" to "S"
                    else -> "Present" to "P"
                }
                initialAttendance.add(
                    Attendance(
                        pupilId = p.id,
                        pupilName = p.fullName,
                        admissionNumber = p.admissionNumber,
                        gradeLevel = p.gradeLevel,
                        section = p.section,
                        gender = p.gender,
                        date = todayStr,
                        term = "Term 2",
                        academicYear = 2026,
                        weekNumber = 9,
                        session = "Morning",
                        status = status,
                        statusCode = code,
                        reason = if (code == "S") "Treated at Chilenje First Level Hospital" else if (code == "A") "Unexcused absence" else if (code == "L") "Arrived at 07:42hrs after assembly" else "",
                        recordedByTsNumber = "TS-811409"
                    )
                )
            }
            dao.insertAllStudentAttendance(initialAttendance)
        }
    }
}
