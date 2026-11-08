package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TimetableSlot
import com.example.ui.theme.ExcelBorder
import com.example.ui.theme.ZambiaBlack
import com.example.ui.theme.ZambiaGreen
import com.example.ui.theme.ZambiaGreenDark
import com.example.ui.theme.ZambiaOrange
import com.example.ui.theme.ZambiaRed
import com.example.ui.viewmodel.SchoolViewModel

@Composable
fun TimetableScreen(
    viewModel: SchoolViewModel,
    modifier: Modifier = Modifier
) {
    val timetableSlots by viewModel.allTimetableSlots.collectAsState()

    var selectedTab by remember { mutableStateOf(0) } // 0 = Timetable, 1 = Homework Policy
    var selectedGrade by remember { mutableStateOf("Grade 7 Blue") }
    var selectedDay by remember { mutableStateOf("Monday") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF1F5F9))
            .testTag("timetable_screen")
    ) {
        // Tab Header
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.White,
            contentColor = ZambiaGreenDark,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = ZambiaGreen
                )
            }
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("School Timetable", fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                modifier = Modifier.testTag("tab_school_timetable")
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Homework Policy", fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.Policy, contentDescription = null) },
                modifier = Modifier.testTag("tab_homework_policy")
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (selectedTab == 0) {
            // Timetable View
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp)
            ) {
                // Grade & Day Selection Bar
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ExcelBorder)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "Select Class / Grade Timetable:",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = ZambiaGreenDark
                            )
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            listOf("Grade 4A", "Grade 7 Blue", "Grade 9 Green", "Grade 11 Science").forEach { gr ->
                                FilterChip(
                                    selected = selectedGrade == gr,
                                    onClick = { selectedGrade = gr },
                                    label = { Text(gr, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = ZambiaGreen,
                                        selectedLabelColor = Color.White
                                    ),
                                    modifier = Modifier.padding(end = 6.dp).height(32.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Day of Week:",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = ZambiaGreenDark
                            )
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday").forEach { day ->
                                val isSel = selectedDay == day
                                Box(
                                    modifier = Modifier
                                        .padding(end = 6.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isSel) ZambiaOrange else Color(0xFFF1F5F9))
                                        .clickable { selectedDay = day }
                                        .padding(horizontal = 10.dp, vertical = 5.dp)
                                ) {
                                    Text(
                                        text = day,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSel) Color.White else ZambiaBlack
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Schedule Breakdown List
                val filteredSlots = remember(timetableSlots, selectedGrade, selectedDay) {
                    timetableSlots.filter { it.grade == selectedGrade && it.dayOfWeek == selectedDay }
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Morning Assembly Header
                    item {
                        ScheduleSpecialItem(
                            time = "07:15 - 07:30",
                            title = "Morning Assembly & Flag Raising Ceremony",
                            desc = "National Anthem ('Stand and sing of Zambia'), school prayer, announcements",
                            color = ZambiaGreenDark
                        )
                    }

                    // Scheduled Periods
                    val displayList = if (filteredSlots.isNotEmpty()) filteredSlots else listOf(
                        TimetableSlot(1, selectedDay, 1, "07:30 - 08:15", selectedGrade, "Mathematics", "Mrs. Mulenga", "Room 4"),
                        TimetableSlot(2, selectedDay, 2, "08:15 - 09:00", selectedGrade, "English Language", "Mr. Banda", "Room 4"),
                        TimetableSlot(3, selectedDay, 3, "09:00 - 09:45", selectedGrade, "Integrated Science", "Mrs. Phiri", "Lab 1")
                    )

                    items(displayList) { slot ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ExcelBorder)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = ZambiaGreen.copy(alpha = 0.12f),
                                    modifier = Modifier.size(42.dp)
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            "P${slot.periodNumber}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = ZambiaGreenDark
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = slot.subject,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = ZambiaBlack
                                        )
                                    )
                                    Text(
                                        text = "${slot.timeRange} • ${slot.teacherName} • ${slot.room}",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontSize = 11.sp,
                                            color = Color.Gray
                                        )
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFFF1F8E9)
                                ) {
                                    Text(
                                        text = "45 mins",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 10.sp,
                                            color = ZambiaGreenDark,
                                            fontWeight = FontWeight.SemiBold
                                        ),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Mid-day breaks
                    item {
                        ScheduleSpecialItem(
                            time = "09:45 - 10:15",
                            title = "Morning Tea & Recreation Break",
                            desc = "Staff tea, pupil snacks & playground recreation",
                            color = ZambiaOrange
                        )
                    }

                    item {
                        ScheduleSpecialItem(
                            time = "12:30 - 13:30",
                            title = "Lunch & Refectory Break",
                            desc = "Pupil feeding programme, library study",
                            color = Color(0xFF1E88E5)
                        )
                    }

                    item {
                        ScheduleSpecialItem(
                            time = "15:00 - 16:30",
                            title = "Co-Curricular & Sports Clubs",
                            desc = "JETS Science Club, Football, Netball, Traditional Dancing, Debate Society",
                            color = ZambiaRed
                        )
                    }
                }
            }
        } else {
            // Official Zambian Homework Policy
            val completedHomeworkTasks = remember { mutableStateMapOf<Int, Boolean>() }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = ZambiaGreenDark)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "REPUBLIC OF ZAMBIA • MINISTRY OF GENERAL EDUCATION",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = ZambiaOrange,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Official National Homework & Independent Study Policy",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Text(
                                text = "Standard guidelines to foster competence, critical thinking, and regular study habits at home.",
                                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFE8F5E9), fontSize = 11.sp)
                            )
                        }
                    }
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ExcelBorder)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "1. Recommended Time Allowances by School Stage",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = ZambiaGreenDark
                                )
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            PolicyStageCard("Lower Primary (Grades 1 - 4)", "15 to 20 minutes daily", "Foundational phonics, reading aloud with parents, basic numeracy sums, local language vocabulary.")
                            Spacer(modifier = Modifier.height(6.dp))
                            PolicyStageCard("Upper Primary (Grades 5 - 7)", "30 to 45 minutes daily", "ECZ examination preparation, short composition writing, science inquiries, mathematics exercises.")
                            Spacer(modifier = Modifier.height(6.dp))
                            PolicyStageCard("Junior Secondary (Grades 8 - 9)", "45 to 60 minutes daily", "Subject-specific continuous assessments, laboratory reports, history essays, summary exercises.")
                            Spacer(modifier = Modifier.height(6.dp))
                            PolicyStageCard("Senior Secondary (Grades 10 - 12)", "60 to 90 minutes daily", "Past ECZ papers revision, physics/chemistry calculations, literature analytical reading.")
                        }
                    }
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ExcelBorder)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "2. Roles & Governance Responsibilities",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = ZambiaGreenDark
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("• Teachers: Must provide clear instructions, relevant to syllabus, and mark/return homework within 48 hours with constructive feedback.", fontSize = 11.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("• Pupils: Must record assignments in their homework log, attempt tasks independently, and submit punctually every morning.", fontSize = 11.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("• Parents / Guardians: Urged to provide a quiet, well-lit study environment and sign the pupil's homework diary daily.", fontSize = 11.sp)
                        }
                    }
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ExcelBorder)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "3. Active Homework Monitoring Checklist",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = ZambiaGreenDark
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            val homeworkTasks = listOf(
                                1 to "Grade 4: English Reading & Phonics page 34",
                                2 to "Grade 7: ECZ Past Paper 2024 Math Section A",
                                3 to "Grade 9: Integrated Science - Plant Stomata sketch",
                                4 to "Grade 11: Physics - Speed, Velocity & Acceleration calculations"
                            )

                            homeworkTasks.forEach { (id, title) ->
                                val isDone = completedHomeworkTasks[id] ?: false
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = isDone,
                                        onCheckedChange = { completedHomeworkTasks[id] = it },
                                        colors = CheckboxDefaults.colors(checkedColor = ZambiaGreen)
                                    )
                                    Text(
                                        text = title,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = if (isDone) Color.Gray else ZambiaBlack,
                                            fontWeight = if (isDone) FontWeight.Normal else FontWeight.Medium
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ScheduleSpecialItem(
    time: String,
    title: String,
    desc: String,
    color: Color
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = color.copy(alpha = 0.08f),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "$time • $title",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = color
                    )
                )
                Text(
                    text = desc,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 10.sp,
                        color = Color(0xFF475569)
                    )
                )
            }
        }
    }
}

@Composable
private fun PolicyStageCard(stage: String, duration: String, details: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFFF8FAFC),
        shape = RoundedCornerShape(6.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, ExcelBorder)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stage, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = ZambiaGreenDark))
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = ZambiaOrange.copy(alpha = 0.15f)
                ) {
                    Text(
                        duration,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = ZambiaOrange, fontSize = 10.sp),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(details, style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = Color(0xFF334155)))
        }
    }
}
