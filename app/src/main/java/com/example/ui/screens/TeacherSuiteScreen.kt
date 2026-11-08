package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LessonPlan
import com.example.data.model.SchemeOfWork
import com.example.data.model.TeacherMeeting
import com.example.ui.theme.ExcelBorder
import com.example.ui.theme.ZambiaBlack
import com.example.ui.theme.ZambiaGreen
import com.example.ui.theme.ZambiaGreenDark
import com.example.ui.theme.ZambiaOrange
import com.example.ui.theme.ZambiaRed
import com.example.ui.viewmodel.SchoolViewModel

@Composable
fun TeacherSuiteScreen(
    viewModel: SchoolViewModel,
    modifier: Modifier = Modifier
) {
    val schemes by viewModel.allSchemes.collectAsState()
    val lessonPlans by viewModel.allLessonPlans.collectAsState()
    val meetings by viewModel.allMeetings.collectAsState()

    var selectedSubTab by remember { mutableStateOf(0) }
    var showNewSchemeDialog by remember { mutableStateOf(false) }
    var showNewPlanDialog by remember { mutableStateOf(false) }
    var showNewMeetingDialog by remember { mutableStateOf(false) }

    val tabs = listOf("Schemes of Work", "Lesson Plans", "SBA & Rubrics", "CPD & Group Meetings")

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    when (selectedSubTab) {
                        0 -> showNewSchemeDialog = true
                        1 -> showNewPlanDialog = true
                        3 -> showNewMeetingDialog = true
                        else -> showNewSchemeDialog = true
                    }
                },
                containerColor = ZambiaGreen,
                contentColor = Color.White,
                modifier = Modifier.testTag("fab_teacher_suite")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFFF1F5F9))
                .padding(padding)
                .testTag("teacher_suite_screen")
        ) {
            // Header Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, ExcelBorder)
            ) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    Text(
                        text = "Teacher Academic Preparation & Planning Suite",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = ZambiaGreenDark
                        ),
                        modifier = Modifier.padding(horizontal = 14.dp)
                    )
                    Text(
                        text = "Aligned with Ministry of Education CBC and ODC Pedagogical Standards",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.sp,
                            color = Color.Gray
                        ),
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 2.dp)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    ScrollableTabRow(
                        selectedTabIndex = selectedSubTab,
                        containerColor = Color.White,
                        contentColor = ZambiaGreenDark,
                        edgePadding = 12.dp,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[selectedSubTab]),
                                color = ZambiaGreen
                            )
                        }
                    ) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedSubTab == index,
                                onClick = { selectedSubTab = index },
                                text = {
                                    Text(
                                        title,
                                        fontWeight = if (selectedSubTab == index) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 12.sp,
                                        color = if (selectedSubTab == index) ZambiaGreenDark else Color.Gray
                                    )
                                },
                                modifier = Modifier.testTag("teacher_tab_$index")
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Sub Tab Content
            when (selectedSubTab) {
                0 -> SchemesTab(schemes = schemes, onStatusChange = { s, st -> viewModel.updateSchemeStatus(s, st) })
                1 -> LessonPlansTab(plans = lessonPlans)
                2 -> SbaRubricsTab()
                3 -> CpdMeetingsTab(meetings = meetings)
            }
        }
    }

    // New Scheme of Work Dialog
    if (showNewSchemeDialog) {
        var grade by remember { mutableStateOf("Grade 7") }
        var subject by remember { mutableStateOf("Mathematics") }
        var term by remember { mutableStateOf("Term 2") }
        var week by remember { mutableStateOf("6") }
        var topic by remember { mutableStateOf("") }
        var outcomes by remember { mutableStateOf("") }
        var methods by remember { mutableStateOf("Guided discovery, group investigation") }
        var aids by remember { mutableStateOf("Chalkboard, charts, math kits") }
        var references by remember { mutableStateOf("Zambia Basic Education Syllabus") }

        AlertDialog(
            onDismissRequest = { showNewSchemeDialog = false },
            title = { Text("Prepare Scheme of Work", fontWeight = FontWeight.Bold, color = ZambiaGreenDark) },
            text = {
                Column {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = grade,
                            onValueChange = { grade = it },
                            label = { Text("Grade") },
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        OutlinedTextField(
                            value = subject,
                            onValueChange = { subject = it },
                            label = { Text("Subject") },
                            modifier = Modifier.weight(1.5f)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = term,
                            onValueChange = { term = it },
                            label = { Text("Term") },
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        OutlinedTextField(
                            value = week,
                            onValueChange = { week = it },
                            label = { Text("Week Number") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = topic,
                        onValueChange = { topic = it },
                        label = { Text("Topic & Subtopics") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = outcomes,
                        onValueChange = { outcomes = it },
                        label = { Text("Specific Learning Outcomes") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.addSchemeOfWork(
                            SchemeOfWork(
                                grade = grade,
                                subject = subject,
                                term = term,
                                weekNumber = week.toIntOrNull() ?: 1,
                                topic = topic.ifBlank { "Algebraic Equations" },
                                specificOutcomes = outcomes.ifBlank { "Solve linear single-variable equations." },
                                teachingMethods = methods,
                                teachingAids = aids,
                                references = references,
                                status = "Planned"
                            )
                        )
                        showNewSchemeDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ZambiaGreen)
                ) {
                    Text("Save Scheme")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showNewSchemeDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // New Lesson Plan Dialog
    if (showNewPlanDialog) {
        var teacherName by remember { mutableStateOf("Mrs. Mulenga") }
        var grade by remember { mutableStateOf("Grade 7") }
        var subject by remember { mutableStateOf("Integrated Science") }
        var topic by remember { mutableStateOf("Human Digestive System") }
        var duration by remember { mutableStateOf("40 Mins") }
        var outcomes by remember { mutableStateOf("Identify main organs of digestion and their functions.") }
        var aids by remember { mutableStateOf("Wall chart of digestive system, plastic model") }
        var intro by remember { mutableStateOf("Ask pupils what happens to a slice of bread when eaten (5 mins).") }
        var steps by remember { mutableStateOf("Step 1: Oral cavity & enzyme action.\nStep 2: Stomach & acid role.\nStep 3: Small intestine absorption.") }
        var eval by remember { mutableStateOf("Pupils sketch and label the system in exercise books.") }
        var homework by remember { mutableStateOf("Review questions 1-5 on page 44.") }

        AlertDialog(
            onDismissRequest = { showNewPlanDialog = false },
            title = { Text("Create Zambian 5-Step Lesson Plan", fontWeight = FontWeight.Bold, color = ZambiaGreenDark) },
            text = {
                Column {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = grade,
                            onValueChange = { grade = it },
                            label = { Text("Grade") },
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        OutlinedTextField(
                            value = subject,
                            onValueChange = { subject = it },
                            label = { Text("Subject") },
                            modifier = Modifier.weight(1.5f)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = topic,
                        onValueChange = { topic = it },
                        label = { Text("Lesson Topic") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = outcomes,
                        onValueChange = { outcomes = it },
                        label = { Text("Specific Competency Outcome") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = steps,
                        onValueChange = { steps = it },
                        label = { Text("Lesson Development (Steps 1-3)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.addLessonPlan(
                            LessonPlan(
                                teacherName = teacherName,
                                grade = grade,
                                subject = subject,
                                date = "2026-11-08",
                                duration = duration,
                                topic = topic,
                                generalOutcome = outcomes,
                                specificCompetences = "Critical thinking, scientific inquiry.",
                                teachingAids = aids,
                                introStep = intro,
                                developmentSteps = steps,
                                evaluationStep = eval,
                                homeworkAssignment = homework,
                                selfReflection = "Pupils actively engaged with the chart demonstration."
                            )
                        )
                        showNewPlanDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ZambiaGreen)
                ) {
                    Text("Save Lesson Plan")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showNewPlanDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // New CPD Meeting Dialog
    if (showNewMeetingDialog) {
        var title by remember { mutableStateOf("") }
        var dept by remember { mutableStateOf("All Academic Staff") }
        var agenda by remember { mutableStateOf("") }
        var resolutions by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showNewMeetingDialog = false },
            title = { Text("Record CPD / Department Meeting", fontWeight = FontWeight.Bold, color = ZambiaGreenDark) },
            text = {
                Column {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Meeting Title / Focus") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = dept,
                        onValueChange = { dept = it },
                        label = { Text("Department / Group") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = agenda,
                        onValueChange = { agenda = it },
                        label = { Text("Agenda Items") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = resolutions,
                        onValueChange = { resolutions = it },
                        label = { Text("Key Resolutions & Action Points") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.addTeacherMeeting(
                            TeacherMeeting(
                                title = title.ifBlank { "Departmental Review" },
                                date = "08 Nov 2026",
                                department = dept,
                                attendeeCount = 18,
                                agenda = agenda.ifBlank { "1. Syllabi completion\n2. SBA assessment records" },
                                resolutions = resolutions.ifBlank { "Teachers to submit weekly lesson plans every Monday by 08:00hrs." }
                            )
                        )
                        showNewMeetingDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ZambiaGreen)
                ) {
                    Text("Save Minutes")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showNewMeetingDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun SchemesTab(
    schemes: List<SchemeOfWork>,
    onStatusChange: (SchemeOfWork, String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(schemes) { scheme ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, ExcelBorder)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = ZambiaGreen.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "${scheme.grade} • ${scheme.subject} (Week ${scheme.weekNumber})",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = ZambiaGreenDark
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        // Status Badge
                        val (statusColor, nextStatus) = when (scheme.status) {
                            "Completed" -> ZambiaGreen to "In Progress"
                            "In Progress" -> ZambiaOrange to "Completed"
                            else -> Color.Gray to "In Progress"
                        }
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = statusColor.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, statusColor),
                            modifier = Modifier.clickable { onStatusChange(scheme, nextStatus) }
                        ) {
                            Text(
                                text = "● ${scheme.status} (Tap to change)",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = statusColor,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Topic: ${scheme.topic}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = ZambiaBlack
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Specific Outcomes: ${scheme.specificOutcomes}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp,
                            color = Color(0xFF334155)
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Methods: ${scheme.teachingMethods} • Aids: ${scheme.teachingAids}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 10.sp,
                            color = Color.Gray
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun LessonPlansTab(plans: List<LessonPlan>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(plans) { plan ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, ExcelBorder)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${plan.grade} • ${plan.subject} (${plan.duration})",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = ZambiaGreenDark
                            )
                        )
                        Text(
                            text = "Teacher: ${plan.teacherName}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 10.sp,
                                color = Color.Gray
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Lesson: ${plan.topic}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = ZambiaBlack
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Outcomes: ${plan.generalOutcome}",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = Color(0xFF475569))
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFFF8FAFC),
                        shape = RoundedCornerShape(6.dp),
                        border = androidx.compose.foundation.BorderStroke(0.5.dp, ExcelBorder)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text("1. Introduction: ${plan.introStep}", fontSize = 11.sp)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("2. Development: ${plan.developmentSteps}", fontSize = 11.sp)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("3. Evaluation: ${plan.evaluationStep}", fontSize = 11.sp)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("4. Homework: ${plan.homeworkAssignment}", fontSize = 11.sp, color = ZambiaOrange)
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Teacher Self-Reflection: ${plan.selfReflection}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = ZambiaGreenDark
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun SbaRubricsTab() {
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
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, ExcelBorder)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Zambian School-Based Assessment (SBA) Weighting Matrix",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = ZambiaGreenDark
                        )
                    )
                    Text(
                        text = "Continuous Assessment components contributing to ECZ Final Candidate Certification",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = Color.Gray)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    SbaWeightRow("Task 1: Practical Projects & Field Inquiries", "20%", "Term 1 & 2")
                    SbaWeightRow("Task 2: Continuous Assessment Tests (CA Tests 1 & 2)", "30%", "Bi-monthly")
                    SbaWeightRow("Task 3: Portfolio, Homework & Assignments", "10%", "Term-long")
                    SbaWeightRow("Task 4: Terminal Examination (Theory & Practical)", "40%", "Week 12-13")
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
                        text = "CBC 4-Level Performance Descriptors Guide",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = ZambiaGreenDark
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    DescriptorCard("Level 4: Exceeding Expectations (EE) [80-100%]", "Consistently exhibits creative problem solving, evaluates alternatives independently, leads peer collaboration, and applies concepts in new contexts.", ZambiaGreen)
                    Spacer(modifier = Modifier.height(6.dp))
                    DescriptorCard("Level 3: Meeting Expectations (ME) [65-79%]", "Mastery of required knowledge, carries out prescribed tasks accurately, works productively with peers with standard supervision.", ZambiaOrange)
                    Spacer(modifier = Modifier.height(6.dp))
                    DescriptorCard("Level 2: Approaching Expectations (AE) [50-64%]", "Demonstrates foundational concepts but requires occasional teacher scaffolding and structured prompts to complete multi-step tasks.", Color(0xFF1E88E5))
                    Spacer(modifier = Modifier.height(6.dp))
                    DescriptorCard("Level 1: Below Expectations (BE) [0-49%]", "Requires continuous direct intervention, difficulty understanding foundational vocabulary, enrolled in school remedial clinic.", ZambiaRed)
                }
            }
        }
    }
}

@Composable
private fun SbaWeightRow(task: String, weight: String, timing: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(task, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium))
            Text(timing, style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp, color = Color.Gray))
        }
        Surface(
            shape = RoundedCornerShape(4.dp),
            color = ZambiaGreen.copy(alpha = 0.15f)
        ) {
            Text(
                text = weight,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = ZambiaGreenDark
                ),
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }
    }
}

@Composable
private fun DescriptorCard(title: String, desc: String, color: Color) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = color.copy(alpha = 0.08f),
        shape = RoundedCornerShape(6.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(title, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = color))
            Spacer(modifier = Modifier.height(2.dp))
            Text(desc, style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = ZambiaBlack))
        }
    }
}

@Composable
private fun CpdMeetingsTab(meetings: List<TeacherMeeting>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(meetings) { meeting ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, ExcelBorder)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = ZambiaOrange.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = meeting.department,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = ZambiaOrange
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Text(
                            text = "${meeting.date} • ${meeting.attendeeCount} Teachers Attended",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp, color = Color.Gray)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = meeting.title,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = ZambiaBlack
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Agenda:\n${meeting.agenda}",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = Color(0xFF334155))
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFFF1F8E9),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(
                                text = "Agreed Resolutions & Action Points:",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = ZambiaGreenDark
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = meeting.resolutions,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = ZambiaBlack)
                            )
                        }
                    }
                }
            }
        }
    }
}
