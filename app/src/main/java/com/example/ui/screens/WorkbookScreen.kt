package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.AssessmentRecord
import com.example.data.model.Pupil
import com.example.data.model.ZambianCurriculumHelper
import com.example.ui.components.ZambiaFlagRibbon
import com.example.ui.theme.ExcelBorder
import com.example.ui.theme.ExcelCellAlt
import com.example.ui.theme.ExcelHighlight
import com.example.ui.theme.ZambiaBlack
import com.example.ui.theme.ZambiaGreen
import com.example.ui.theme.ZambiaGreenDark
import com.example.ui.theme.ZambiaOrange
import com.example.ui.theme.ZambiaRed
import com.example.ui.viewmodel.SchoolViewModel

data class RowEditState(
    val pupil: Pupil,
    val assessment: AssessmentRecord?,
    val subject: String
)

@Composable
fun WorkbookScreen(
    viewModel: SchoolViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val schoolProfile by viewModel.schoolProfile.collectAsState()
    val allPupils by viewModel.allPupils.collectAsState()
    val allAssessments by viewModel.assessmentsForSelectedGrade.collectAsState()
    val selectedGrade by viewModel.selectedWorkbookGrade.collectAsState()
    val selectedSubject by viewModel.selectedWorkbookSubject.collectAsState()
    val selectedTerm by viewModel.selectedWorkbookTerm.collectAsState()
    val curriculumMode by viewModel.curriculumMode.collectAsState()

    // Filter pupils by grade
    val filteredPupils = remember(allPupils, selectedGrade) {
        allPupils.filter { it.gradeLevel == selectedGrade }
    }

    // Map pupilId to AssessmentRecord for selectedSubject and selectedTerm
    val assessmentMap = remember(allAssessments, selectedSubject, selectedTerm) {
        allAssessments.filter { it.subject == selectedSubject && it.term == selectedTerm }
            .associateBy { it.pupilId }
    }

    // Calculate class statistics
    val scores = filteredPupils.mapNotNull { assessmentMap[it.id]?.totalScore }
    val classAvg = if (scores.isNotEmpty()) scores.average() else 0.0
    val maxScore = scores.maxOrNull() ?: 0.0
    val minScore = scores.minOrNull() ?: 0.0
    val passCount = scores.count { it >= 40.0 }
    val passRate = if (scores.isNotEmpty()) (passCount.toDouble() / scores.size) * 100 else 0.0

    // State for live editing dialog
    var editTarget by remember { mutableStateOf<RowEditState?>(null) }

    // State for Broadsheet Export dialog
    var showExportDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF1F5F9))
            .testTag("workbook_screen")
    ) {
        // Excel Top Title Banner with Zambian Flag Colors
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = ZambiaGreenDark)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                ZambiaFlagRibbon(height = 4)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.White,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                Icons.Default.TableChart,
                                contentDescription = "Excel Workbook",
                                tint = ZambiaGreenDark,
                                modifier = Modifier.padding(6.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "AUTOMATED CONTINUOUS ASSESSMENT WORKBOOK",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 11.sp
                                )
                            )
                            Text(
                                text = "ECZ & CBC Standardized Broad Sheet • Zambia",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = ZambiaOrange,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }

                    // Coat of Arms Mini Badge
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(1.dp, ZambiaOrange, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.zambia_coat_of_arms),
                            contentDescription = "Coat of Arms",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.padding(3.dp)
                        )
                    }
                }
            }
        }

        // Filter & Controls Bar
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            shape = RoundedCornerShape(10.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, ExcelBorder)
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                // Curriculum standard & Term switcher
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Standard:",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        FilterChip(
                            selected = curriculumMode == "ODC",
                            onClick = { viewModel.toggleCurriculumMode("ODC") },
                            label = { Text("ODC (ECZ 1-9)", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ZambiaGreen,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.height(32.dp).testTag("filter_odc")
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        FilterChip(
                            selected = curriculumMode == "CBC",
                            onClick = { viewModel.toggleCurriculumMode("CBC") },
                            label = { Text("CBC (SBA)", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ZambiaOrange,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.height(32.dp).testTag("filter_cbc")
                        )
                    }

                    // Term Selector
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        listOf("Term 1", "Term 2", "Term 3").forEach { term ->
                            val isSelected = selectedTerm == term
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 2.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSelected) ZambiaGreenDark else Color(0xFFE2E8F0))
                                    .clickable { viewModel.selectWorkbookTerm(term) }
                                    .padding(horizontal = 6.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = term,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color.White else ZambiaBlack
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
                HorizontalDivider(color = Color(0xFFE2E8F0))
                Spacer(modifier = Modifier.height(6.dp))

                // Grades Horizontal Selector
                Text(
                    text = "Select School Grade Level:",
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
                    val grades = listOf(
                        4 to "Grade 4 (Primary)",
                        7 to "Grade 7 (Primary ECZ)",
                        9 to "Grade 9 (Junior Sec)",
                        11 to "Grade 11 (Senior Sec)"
                    )
                    grades.forEach { (g, label) ->
                        val isSelected = selectedGrade == g
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.selectWorkbookGrade(g) },
                            label = { Text(label, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ZambiaGreen,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier
                                .padding(end = 6.dp)
                                .height(32.dp)
                                .testTag("grade_chip_$g")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Subjects Horizontal Selector
                val availableSubjects = if (selectedGrade <= 7) {
                    listOf("Mathematics", "English Language", "Integrated Science", "Social Studies", "CTS", "Zambian Language (Chinyanja)")
                } else {
                    listOf("Mathematics", "English Language", "Biology", "Chemistry", "Physics", "Civic Education")
                }

                Text(
                    text = "Subject Marks Sheet:",
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
                    availableSubjects.forEach { sub ->
                        val isSelected = selectedSubject == sub
                        Box(
                            modifier = Modifier
                                .padding(end = 6.dp, top = 2.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) ZambiaOrange else Color(0xFFF1F5F9))
                                .border(1.dp, if (isSelected) ZambiaOrange else ExcelBorder, RoundedCornerShape(6.dp))
                                .clickable { viewModel.selectWorkbookSubject(sub) }
                                .padding(horizontal = 8.dp, vertical = 5.dp)
                                .testTag("subject_chip_$sub")
                        ) {
                            Text(
                                text = sub,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else ZambiaBlack
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Excel Statistical Summary Strip
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            shape = RoundedCornerShape(8.dp),
            color = Color(0xFFE8F5E9),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFC8E6C9))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Candidates: ${filteredPupils.size}",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Class Avg: ${String.format("%.1f", classAvg)}%",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = ZambiaGreenDark
                    )
                )
                Text(
                    text = "Highest: ${String.format("%.0f", maxScore)}%",
                    style = MaterialTheme.typography.labelSmall.copy(color = ZambiaGreen)
                )
                Text(
                    text = "Pass Rate: ${String.format("%.0f", passRate)}%",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = ZambiaOrange
                    )
                )
                IconButton(
                    onClick = { showExportDialog = true },
                    modifier = Modifier.size(24.dp).testTag("export_broadsheet_btn")
                ) {
                    Icon(
                        Icons.Default.FileDownload,
                        contentDescription = "Export Broad Sheet",
                        tint = ZambiaGreenDark,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Interactive Excel SpreadSheet Table
        Surface(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            shape = RoundedCornerShape(8.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, ExcelBorder)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Spreadsheet Grid Header Styled in Zambia Green
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = ZambiaGreen
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 6.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "#",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.width(22.dp)
                        )
                        Text(
                            text = "Pupil Name",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.weight(2f)
                        )
                        Text(
                            text = "CA\n(30)",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                fontSize = 10.sp
                            ),
                            modifier = Modifier.width(36.dp)
                        )
                        Text(
                            text = "Mid\n(20)",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                fontSize = 10.sp
                            ),
                            modifier = Modifier.width(36.dp)
                        )
                        Text(
                            text = "Exam\n(50)",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                fontSize = 10.sp
                            ),
                            modifier = Modifier.width(38.dp)
                        )
                        Text(
                            text = "Total\n(100)",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                fontSize = 10.sp
                            ),
                            modifier = Modifier.width(42.dp)
                        )
                        Text(
                            text = if (curriculumMode == "CBC") "CBC\nLevel" else "ECZ\nGrade",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                fontSize = 10.sp
                            ),
                            modifier = Modifier.width(44.dp)
                        )
                        Text(
                            text = "Actions",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                fontSize = 10.sp
                            ),
                            modifier = Modifier.width(68.dp)
                        )
                    }
                }

                // Table Rows
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("workbook_table_list")
                ) {
                    itemsIndexed(filteredPupils) { index, pupil ->
                        val record = assessmentMap[pupil.id]
                        val total = record?.totalScore ?: 0.0
                        val rowBg = when {
                            index % 2 == 1 -> ExcelCellAlt
                            else -> Color.White
                        }

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    editTarget = RowEditState(pupil, record, selectedSubject)
                                },
                            color = rowBg,
                            border = androidx.compose.foundation.BorderStroke(0.5.dp, ExcelBorder)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 6.dp, vertical = 7.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${index + 1}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 11.sp,
                                        color = Color.Gray
                                    ),
                                    modifier = Modifier.width(22.dp)
                                )
                                Column(modifier = Modifier.weight(2f)) {
                                    Text(
                                        text = pupil.fullName,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 11.sp
                                        )
                                    )
                                    Text(
                                        text = pupil.admissionNumber,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontSize = 9.sp,
                                            color = Color.Gray
                                        )
                                    )
                                }
                                Text(
                                    text = record?.let { String.format("%.0f", it.continuousAssessment) } ?: "-",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 11.sp,
                                        textAlign = TextAlign.Center
                                    ),
                                    modifier = Modifier.width(36.dp)
                                )
                                Text(
                                    text = record?.let { String.format("%.0f", it.midTermScore) } ?: "-",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 11.sp,
                                        textAlign = TextAlign.Center
                                    ),
                                    modifier = Modifier.width(36.dp)
                                )
                                Text(
                                    text = record?.let { String.format("%.0f", it.terminalExamScore) } ?: "-",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 11.sp,
                                        textAlign = TextAlign.Center
                                    ),
                                    modifier = Modifier.width(38.dp)
                                )
                                Text(
                                    text = if (record != null) String.format("%.0f%%", total) else "-",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (total >= 70) ZambiaGreenDark else if (total >= 40) ZambiaBlack else ZambiaRed,
                                        textAlign = TextAlign.Center
                                    ),
                                    modifier = Modifier.width(42.dp)
                                )

                                // Grade / Descriptor Badge
                                Box(
                                    modifier = Modifier.width(44.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (record != null) {
                                        val badgeColor = when {
                                            total >= 75 -> ZambiaGreen
                                            total >= 60 -> ZambiaOrange
                                            total >= 40 -> Color(0xFF1E88E5)
                                            else -> ZambiaRed
                                        }
                                        val label = if (curriculumMode == "CBC") record.cbcPerformanceLevel else record.eczGrade
                                        Surface(
                                            shape = RoundedCornerShape(3.dp),
                                            color = badgeColor.copy(alpha = 0.15f),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, badgeColor)
                                        ) {
                                            Text(
                                                text = label,
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = badgeColor,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 10.sp
                                                ),
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                            )
                                        }
                                    } else {
                                        Text("-", fontSize = 10.sp)
                                    }
                                }

                                // Actions (Edit & Report Card)
                                Row(
                                    modifier = Modifier.width(68.dp),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    IconButton(
                                        onClick = {
                                            editTarget = RowEditState(pupil, record, selectedSubject)
                                        },
                                        modifier = Modifier.size(28.dp).testTag("edit_marks_${pupil.id}")
                                    ) {
                                        Icon(
                                            Icons.Default.Edit,
                                            contentDescription = "Edit Marks",
                                            tint = ZambiaOrange,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    IconButton(
                                        onClick = {
                                            viewModel.openReportCard(pupil)
                                        },
                                        modifier = Modifier.size(28.dp).testTag("report_card_btn_${pupil.id}")
                                    ) {
                                        Icon(
                                            Icons.Default.Description,
                                            contentDescription = "Generate Report Card",
                                            tint = ZambiaGreenDark,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }

    // Live Edit Marks Dialog
    editTarget?.let { target ->
        var caInput by remember { mutableStateOf(target.assessment?.continuousAssessment?.toInt()?.toString() ?: "20") }
        var midInput by remember { mutableStateOf(target.assessment?.midTermScore?.toInt()?.toString() ?: "15") }
        var examInput by remember { mutableStateOf(target.assessment?.terminalExamScore?.toInt()?.toString() ?: "35") }
        var commentInput by remember { mutableStateOf(target.assessment?.teacherComment ?: "Demonstrates solid progress.") }

        val caVal = caInput.toDoubleOrNull() ?: 0.0
        val midVal = midInput.toDoubleOrNull() ?: 0.0
        val examVal = examInput.toDoubleOrNull() ?: 0.0
        val liveTotal = caVal + midVal + examVal
        val liveEcz = ZambianCurriculumHelper.calculateEczGrade(liveTotal)
        val liveCbc = ZambianCurriculumHelper.calculateCbcLevel(liveTotal)

        AlertDialog(
            onDismissRequest = { editTarget = null },
            title = {
                Text(
                    text = "Edit Marks: ${target.pupil.fullName}",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = ZambiaGreenDark
                    )
                )
            },
            text = {
                Column {
                    Text(
                        text = "Subject: ${target.subject} • Class: ${target.pupil.section}",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = caInput,
                            onValueChange = { if (it.length <= 2) caInput = it },
                            label = { Text("CA (Max 30)", fontSize = 10.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f).testTag("input_ca")
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        OutlinedTextField(
                            value = midInput,
                            onValueChange = { if (it.length <= 2) midInput = it },
                            label = { Text("Mid (Max 20)", fontSize = 10.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f).testTag("input_mid")
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        OutlinedTextField(
                            value = examInput,
                            onValueChange = { if (it.length <= 2) examInput = it },
                            label = { Text("Exam (Max 50)", fontSize = 10.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f).testTag("input_exam")
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Live Calculated Result Preview
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFFF1F8E9),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ZambiaGreen)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Calculated Total", fontSize = 10.sp, color = Color.Gray)
                                Text(
                                    "${liveTotal.toInt()}%",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ZambiaGreenDark
                                )
                            }
                            Column {
                                Text("ECZ Grade", fontSize = 10.sp, color = Color.Gray)
                                Text(
                                    "Grade $liveEcz",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ZambiaOrange
                                )
                            }
                            Column {
                                Text("CBC Level", fontSize = 10.sp, color = Color.Gray)
                                Text(
                                    liveCbc,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ZambiaGreenDark
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = commentInput,
                        onValueChange = { commentInput = it },
                        label = { Text("Teacher Remarks / Assessment Notes") },
                        modifier = Modifier.fillMaxWidth().testTag("input_comment")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.saveOrUpdateAssessmentScore(
                            pupilId = target.pupil.id,
                            subject = target.subject,
                            gradeLevel = target.pupil.gradeLevel,
                            continuousAssessment = caVal,
                            midTermScore = midVal,
                            terminalExamScore = examVal,
                            comment = commentInput
                        )
                        editTarget = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ZambiaGreen),
                    modifier = Modifier.testTag("save_marks_button")
                ) {
                    Text("Save to Workbook")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { editTarget = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Broadsheet Export Summary Dialog
    if (showExportDialog) {
        val broadsheetText = remember(filteredPupils, assessmentMap, selectedSubject, selectedGrade, schoolProfile) {
            val sb = StringBuilder()
            sb.appendLine("==================================================")
            sb.appendLine("ZAMBIA CONTINUOUS ASSESSMENT & EXAM BROADSHEET")
            sb.appendLine("School: ${schoolProfile?.schoolName ?: "Zed School"}")
            sb.appendLine("Grade: Grade $selectedGrade | Subject: $selectedSubject | Term: $selectedTerm")
            sb.appendLine("==================================================")
            sb.appendLine("# | Pupil Name | Adm No | CA/30 | Mid/20 | Exam/50 | Total% | ECZ | CBC")
            sb.appendLine("--------------------------------------------------")
            filteredPupils.forEachIndexed { i, p ->
                val r = assessmentMap[p.id]
                val ca = r?.continuousAssessment?.toInt() ?: 0
                val mid = r?.midTermScore?.toInt() ?: 0
                val ex = r?.terminalExamScore?.toInt() ?: 0
                val tot = r?.totalScore?.toInt() ?: 0
                val ecz = r?.eczGrade ?: "-"
                val cbc = r?.cbcPerformanceLevel ?: "-"
                sb.appendLine("${i + 1}. ${p.fullName} | ${p.admissionNumber} | $ca | $mid | $ex | $tot% | $ecz | $cbc")
            }
            sb.appendLine("--------------------------------------------------")
            sb.appendLine("Class Average: ${String.format("%.1f", classAvg)}% | Pass Rate: ${String.format("%.0f", passRate)}%")
            sb.appendLine("==================================================")
            sb.toString()
        }

        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            title = { Text("Class Broadsheet Summary", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        text = "Automated summary prepared for administrative filing & ECZ submissions:",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth().height(220.dp),
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFF8FAFC),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ExcelBorder)
                    ) {
                        LazyColumn(modifier = Modifier.padding(8.dp)) {
                            item {
                                Text(
                                    text = broadsheetText,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp,
                                    color = ZambiaBlack
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, broadsheetText)
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, "Share Class Broadsheet")
                        context.startActivity(shareIntent)
                        showExportDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ZambiaGreen)
                ) {
                    Text("Share / Export")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showExportDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}
