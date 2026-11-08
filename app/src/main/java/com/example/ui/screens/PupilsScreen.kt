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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Pupil
import com.example.data.model.Teacher
import com.example.ui.theme.ExcelBorder
import com.example.ui.theme.ZambiaBlack
import com.example.ui.theme.ZambiaGreen
import com.example.ui.theme.ZambiaGreenDark
import com.example.ui.theme.ZambiaOrange
import com.example.ui.theme.ZambiaRed
import com.example.ui.viewmodel.SchoolViewModel

@Composable
fun PupilsScreen(
    viewModel: SchoolViewModel,
    modifier: Modifier = Modifier
) {
    val pupils by viewModel.allPupils.collectAsState()
    val teachers by viewModel.allTeachers.collectAsState()

    var selectedTabIndex by remember { mutableStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedGradeFilter by remember { mutableStateOf(0) } // 0 = all

    var showAddPupilDialog by remember { mutableStateOf(false) }
    var showAddTeacherDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (selectedTabIndex == 0) showAddPupilDialog = true else showAddTeacherDialog = true
                },
                containerColor = ZambiaGreen,
                contentColor = Color.White,
                modifier = Modifier.testTag("fab_add_record")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Record")
            }
        }
    ) { paddingVals ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFFF1F5F9))
                .padding(paddingVals)
                .testTag("pupils_screen")
        ) {
            // Tab Selector
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color.White,
                contentColor = ZambiaGreenDark,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = ZambiaGreen
                    )
                }
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    text = {
                        Text(
                            "Pupils Records (${pupils.size})",
                            fontWeight = if (selectedTabIndex == 0) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTabIndex == 0) ZambiaGreenDark else Color.Gray
                        )
                    },
                    icon = { Icon(Icons.Default.School, contentDescription = null) },
                    modifier = Modifier.testTag("tab_pupils")
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    text = {
                        Text(
                            "Teachers Database (${teachers.size})",
                            fontWeight = if (selectedTabIndex == 1) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTabIndex == 1) ZambiaGreenDark else Color.Gray
                        )
                    },
                    icon = { Icon(Icons.Default.Groups, contentDescription = null) },
                    modifier = Modifier.testTag("tab_teachers")
                )
            }

            // Search Bar
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                shape = RoundedCornerShape(10.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, ExcelBorder)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            if (selectedTabIndex == 0) "Search by pupil name, admission no..." else "Search by teacher name, TS no...",
                            fontSize = 12.sp
                        )
                    },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.Gray) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(2.dp)
                        .testTag("search_input")
                )
            }

            if (selectedTabIndex == 0) {
                // Grade Filter Chips for Pupils
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .horizontalScroll(rememberScrollState()),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val filterOptions = listOf(
                        0 to "All Grades",
                        4 to "Grade 4 (Primary)",
                        7 to "Grade 7 (ECZ Exam)",
                        9 to "Grade 9 (Junior)",
                        11 to "Grade 11 (Senior)"
                    )
                    filterOptions.forEach { (grade, label) ->
                        val isSelected = selectedGradeFilter == grade
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedGradeFilter = grade },
                            label = { Text(label, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ZambiaGreen,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.padding(end = 6.dp).height(32.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Filtered Pupils List
                val filteredPupils = remember(pupils, searchQuery, selectedGradeFilter) {
                    pupils.filter { p ->
                        val matchesGrade = (selectedGradeFilter == 0 || p.gradeLevel == selectedGradeFilter)
                        val matchesQuery = searchQuery.isBlank() ||
                                p.fullName.contains(searchQuery, ignoreCase = true) ||
                                p.admissionNumber.contains(searchQuery, ignoreCase = true)
                        matchesGrade && matchesQuery
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredPupils) { pupil ->
                        Card(
                            modifier = Modifier.fillMaxWidth().testTag("pupil_card_${pupil.id}"),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ExcelBorder)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(38.dp)
                                                .clip(CircleShape)
                                                .background(ZambiaGreen.copy(alpha = 0.15f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = pupil.fullName.take(2).uppercase(),
                                                style = MaterialTheme.typography.titleSmall.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = ZambiaGreenDark
                                                )
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = pupil.fullName,
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = ZambiaBlack
                                                )
                                            )
                                            Text(
                                                text = "${pupil.admissionNumber} • ${pupil.gender} • DOB: ${pupil.dateOfBirth}",
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = Color.Gray,
                                                    fontSize = 10.sp
                                                )
                                            )
                                        }
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = if (pupil.levelCategory == "PRIMARY") Color(0xFFE8F5E9) else Color(0xFFFFF3E0)
                                    ) {
                                        Text(
                                            text = pupil.section,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = if (pupil.levelCategory == "PRIMARY") ZambiaGreenDark else ZambiaOrange,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 10.sp
                                            ),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Parent: ${pupil.parentGuardianName} (${pupil.parentPhone})",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = Color(0xFF475569))
                                    )

                                    Row {
                                        Button(
                                            onClick = { viewModel.openReportCard(pupil) },
                                            colors = ButtonDefaults.buttonColors(containerColor = ZambiaGreen),
                                            shape = RoundedCornerShape(6.dp),
                                            modifier = Modifier.height(30.dp)
                                        ) {
                                            Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Report Card", fontSize = 10.sp)
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                        IconButton(
                                            onClick = { viewModel.deletePupil(pupil) },
                                            modifier = Modifier.size(30.dp)
                                        ) {
                                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Gray, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // Teachers List
                val filteredTeachers = remember(teachers, searchQuery) {
                    teachers.filter { t ->
                        searchQuery.isBlank() ||
                                t.fullName.contains(searchQuery, ignoreCase = true) ||
                                t.tsNumber.contains(searchQuery, ignoreCase = true) ||
                                t.department.contains(searchQuery, ignoreCase = true)
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredTeachers) { teacher ->
                        Card(
                            modifier = Modifier.fillMaxWidth().testTag("teacher_card_${teacher.id}"),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ExcelBorder)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(38.dp)
                                                .clip(CircleShape)
                                                .background(ZambiaOrange.copy(alpha = 0.15f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                Icons.Default.Badge,
                                                contentDescription = null,
                                                tint = ZambiaOrange,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = teacher.fullName,
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = ZambiaBlack
                                                )
                                            )
                                            Text(
                                                text = "TS No: ${teacher.tsNumber} • ${teacher.qualification}",
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = Color.Gray,
                                                    fontSize = 10.sp
                                                )
                                            )
                                        }
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = ZambiaGreen.copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = teacher.role,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = ZambiaGreenDark,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 10.sp
                                            ),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "Dept: ${teacher.department} | Assigned: ${teacher.assignedClasses}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = ZambiaGreenDark
                                    )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Phone: ${teacher.phone} • ${teacher.email}",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontSize = 10.sp,
                                            color = Color.Gray
                                        )
                                    )
                                    IconButton(
                                        onClick = { viewModel.deleteTeacher(teacher) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Gray, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Pupil Dialog
    if (showAddPupilDialog) {
        var name by remember { mutableStateOf("") }
        var admNo by remember { mutableStateOf("ZM-2026-${(100..999).random()}") }
        var gradeLevel by remember { mutableStateOf("4") }
        var section by remember { mutableStateOf("Grade 4A") }
        var gender by remember { mutableStateOf("Male") }
        var dob by remember { mutableStateOf("2016-05-15") }
        var parentName by remember { mutableStateOf("") }
        var parentPhone by remember { mutableStateOf("+260 ") }

        AlertDialog(
            onDismissRequest = { showAddPupilDialog = false },
            title = { Text("Enroll New Zambian Pupil", fontWeight = FontWeight.Bold, color = ZambiaGreenDark) },
            text = {
                Column {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Pupil Full Name") },
                        modifier = Modifier.fillMaxWidth().testTag("input_pupil_name")
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = admNo,
                        onValueChange = { admNo = it },
                        label = { Text("Admission/Exam Number") },
                        modifier = Modifier.fillMaxWidth().testTag("input_pupil_adm")
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = gradeLevel,
                            onValueChange = {
                                gradeLevel = it
                                val g = it.toIntOrNull() ?: 1
                                section = if (g <= 7) "Grade $g (Primary)" else "Grade $g (Secondary)"
                            },
                            label = { Text("Grade (1-12)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f).testTag("input_pupil_grade")
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        OutlinedTextField(
                            value = gender,
                            onValueChange = { gender = it },
                            label = { Text("Gender (M/F)") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = parentName,
                        onValueChange = { parentName = it },
                        label = { Text("Parent / Guardian Full Name") },
                        modifier = Modifier.fillMaxWidth().testTag("input_parent_name")
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = parentPhone,
                        onValueChange = { parentPhone = it },
                        label = { Text("Parent Contact Number") },
                        modifier = Modifier.fillMaxWidth().testTag("input_parent_phone")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val g = gradeLevel.toIntOrNull() ?: 4
                        viewModel.addPupil(
                            Pupil(
                                fullName = name.ifBlank { "New Pupil" },
                                admissionNumber = admNo,
                                gradeLevel = g,
                                section = section,
                                levelCategory = if (g <= 7) "PRIMARY" else "SECONDARY",
                                gender = gender,
                                dateOfBirth = dob,
                                parentGuardianName = parentName.ifBlank { "Parent" },
                                parentPhone = parentPhone
                            )
                        )
                        showAddPupilDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ZambiaGreen),
                    modifier = Modifier.testTag("submit_add_pupil")
                ) {
                    Text("Register Pupil")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showAddPupilDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Add Teacher Dialog
    if (showAddTeacherDialog) {
        var name by remember { mutableStateOf("") }
        var tsNo by remember { mutableStateOf("TS-${(500000..999999).random()}") }
        var dept by remember { mutableStateOf("Mathematics & Science") }
        var qualification by remember { mutableStateOf("B.Sc. Ed (UNZA)") }
        var assignedClasses by remember { mutableStateOf("Grade 7 & 9") }
        var phone by remember { mutableStateOf("+260 ") }
        var email by remember { mutableStateOf("") }
        var role by remember { mutableStateOf("Subject Teacher") }

        AlertDialog(
            onDismissRequest = { showAddTeacherDialog = false },
            title = { Text("Add Teacher to Database", fontWeight = FontWeight.Bold, color = ZambiaGreenDark) },
            text = {
                Column {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Teacher Full Name") },
                        modifier = Modifier.fillMaxWidth().testTag("input_teacher_name")
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = tsNo,
                        onValueChange = { tsNo = it },
                        label = { Text("TS (Teaching Service) Number") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = dept,
                        onValueChange = { dept = it },
                        label = { Text("Academic Department") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = qualification,
                        onValueChange = { qualification = it },
                        label = { Text("Qualifications (e.g. B.Ed UNZA)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone Number") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.addTeacher(
                            Teacher(
                                fullName = name.ifBlank { "Teacher" },
                                tsNumber = tsNo,
                                department = dept,
                                assignedClasses = assignedClasses,
                                qualification = qualification,
                                phone = phone,
                                email = email.ifBlank { "${name.lowercase().replace(" ", "")}@school.zm" },
                                role = role
                            )
                        )
                        showAddTeacherDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ZambiaGreen),
                    modifier = Modifier.testTag("submit_add_teacher")
                ) {
                    Text("Save Teacher")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showAddTeacherDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
