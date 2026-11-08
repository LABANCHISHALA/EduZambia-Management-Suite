package com.example.ui.screens

import android.content.Intent
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
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.HowToReg
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Send
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.AttendanceStatus
import com.example.ui.theme.ExcelBorder
import com.example.ui.theme.ZambiaBlack
import com.example.ui.theme.ZambiaGreen
import com.example.ui.theme.ZambiaGreenDark
import com.example.ui.theme.ZambiaOrange
import com.example.ui.theme.ZambiaRed
import com.example.ui.viewmodel.AttendanceUiItem
import com.example.ui.viewmodel.AttendanceViewModel
import com.example.ui.viewmodel.SchoolViewModel

@Composable
fun AttendanceScreen(
    viewModel: SchoolViewModel,
    attendanceViewModel: AttendanceViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val registerItems by attendanceViewModel.currentRegister.collectAsState()
    val stats by attendanceViewModel.attendanceStats.collectAsState()
    val selectedGrade by attendanceViewModel.selectedGrade.collectAsState()
    val selectedDate by attendanceViewModel.selectedDate.collectAsState()
    val selectedSession by attendanceViewModel.selectedSession.collectAsState()
    val selectedTerm by attendanceViewModel.selectedTerm.collectAsState()
    val schoolProfile by attendanceViewModel.schoolProfile.collectAsState()

    var showReportDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF1F5F9))
            .padding(14.dp)
            .testTag("attendance_screen")
    ) {
        // Attendance Header & Controls
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.dp, ExcelBorder)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Daily Pupil Attendance Register",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = ZambiaGreenDark
                            )
                        )
                        Text(
                            text = "Republic of Zambia MoGE Form ZM-ATT • $selectedDate",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = Color.Gray)
                        )
                    }

                    // Session Switcher (Morning Assembly vs Afternoon Session)
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF1F5F9))
                            .padding(2.dp)
                    ) {
                        listOf("Morning", "Afternoon").forEach { session ->
                            val isSel = selectedSession == session
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSel) ZambiaGreenDark else Color.Transparent)
                                    .clickable { attendanceViewModel.setSession(session) }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                    .testTag("session_tab_$session")
                            ) {
                                Text(
                                    text = session,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSel) Color.White else ZambiaBlack
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Grade Chips Filter
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val grades = listOf(
                        4 to "Grade 4A (Primary)",
                        7 to "Grade 7 Blue (Primary)",
                        9 to "Grade 9 Green (Sec)",
                        11 to "Grade 11 Science (Sec)"
                    )
                    grades.forEach { (g, label) ->
                        FilterChip(
                            selected = selectedGrade == g,
                            onClick = { attendanceViewModel.setGrade(g) },
                            label = { Text(label, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ZambiaGreen,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.padding(end = 6.dp).height(32.dp).testTag("att_grade_chip_$g")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Actions: Mark All Present & View MoGE Register Broadsheet
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { attendanceViewModel.markAllPresent() },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(32.dp).testTag("btn_mark_all_present")
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = ZambiaGreen, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Mark All Present", fontSize = 11.sp, color = ZambiaGreenDark)
                    }

                    Button(
                        onClick = { showReportDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = ZambiaGreenDark),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(32.dp).testTag("btn_view_moge_register")
                    ) {
                        Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("MoGE Register", fontSize = 11.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Zambian MoGE Disaggregated Attendance Statistics Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            color = Color(0xFFE8F5E9),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFC8E6C9))
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatusKpi("P: Present", stats.presentCount, ZambiaGreen)
                    StatusKpi("A: Absent", stats.absentCount, ZambiaRed)
                    StatusKpi("L: Late", stats.lateCount, ZambiaOrange)
                    StatusKpi("S: Sick", stats.sickCount, Color(0xFF7B1FA2))
                    StatusKpi("Overall", "${String.format("%.1f", stats.overallAttendanceRate)}%", ZambiaGreenDark)
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp), color = Color(0xFFC8E6C9))

                // Gender-Disaggregated MoGE Metrics
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Boys: ${stats.malePresent}/${stats.maleEnrolled} (${String.format("%.0f", stats.maleAttendanceRate)}%)",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1565C0)
                        )
                    )
                    Text(
                        text = "Girls: ${stats.femalePresent}/${stats.femaleEnrolled} (${String.format("%.0f", stats.femaleAttendanceRate)}%)",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFC2185B)
                        )
                    )
                    Text(
                        text = "Total Roll: ${stats.totalEnrolled}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = ZambiaGreenDark
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Student Attendance List (Zambian Register Format)
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(registerItems) { item ->
                val pupil = item.pupil
                val currentStatus = item.currentStatus

                Card(
                    modifier = Modifier.fillMaxWidth().testTag("attendance_row_${pupil.id}"),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ExcelBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = pupil.fullName,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = ZambiaBlack
                                    )
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "(${pupil.gender.take(1)})",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 10.sp,
                                        color = if (pupil.gender == "Female") Color(0xFFC2185B) else Color(0xFF1565C0),
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                            Text(
                                text = "Adm: ${pupil.admissionNumber} • Parent: ${pupil.parentPhone}",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp, color = Color.Gray)
                            )
                            if (item.reason.isNotBlank()) {
                                Text(
                                    text = "Note: ${item.reason}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 10.sp,
                                        color = ZambiaOrange,
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            }
                        }

                        // Zambian Standard 5-Code Status Selectors: P, A, L, E, S
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            ZambianCodeChip(
                                label = "P",
                                title = "Present",
                                isSelected = currentStatus == AttendanceStatus.PRESENT,
                                color = ZambiaGreen,
                                onClick = {
                                    attendanceViewModel.markPupilStatus(pupil.id, AttendanceStatus.PRESENT)
                                }
                            )
                            ZambianCodeChip(
                                label = "A",
                                title = "Absent",
                                isSelected = currentStatus == AttendanceStatus.ABSENT,
                                color = ZambiaRed,
                                onClick = {
                                    attendanceViewModel.markPupilStatus(pupil.id, AttendanceStatus.ABSENT, "Unexcused absence")
                                }
                            )
                            ZambianCodeChip(
                                label = "L",
                                title = "Late",
                                isSelected = currentStatus == AttendanceStatus.LATE,
                                color = ZambiaOrange,
                                onClick = {
                                    attendanceViewModel.markPupilStatus(pupil.id, AttendanceStatus.LATE, "Late arrival after assembly")
                                }
                            )
                            ZambianCodeChip(
                                label = "E",
                                title = "Excused",
                                isSelected = currentStatus == AttendanceStatus.EXCUSED,
                                color = Color(0xFF1E88E5),
                                onClick = {
                                    attendanceViewModel.markPupilStatus(pupil.id, AttendanceStatus.EXCUSED, "Authorized with parent note")
                                }
                            )
                            ZambianCodeChip(
                                label = "S",
                                title = "Sick",
                                isSelected = currentStatus == AttendanceStatus.SICK,
                                color = Color(0xFF7B1FA2),
                                onClick = {
                                    attendanceViewModel.markPupilStatus(pupil.id, AttendanceStatus.SICK, "Medical excuse / clinic attendance")
                                }
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Send Absentee SMS Alerts to Parents
        Button(
            onClick = {
                val absentPupils = registerItems.filter { it.currentStatus == AttendanceStatus.ABSENT || it.currentStatus == AttendanceStatus.SICK }
                val sb = StringBuilder()
                absentPupils.forEach {
                    sb.appendLine(attendanceViewModel.formatParentAbsenteeSms(it.pupil, it.currentStatus))
                }
                val msg = sb.toString().ifBlank {
                    "All pupils in Grade $selectedGrade are present for $selectedSession session on $selectedDate."
                }
                val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, msg)
                    type = "text/plain"
                }
                val shareIntent = Intent.createChooser(sendIntent, "Send Absentee Alerts to Parents")
                context.startActivity(shareIntent)
            },
            modifier = Modifier.fillMaxWidth().testTag("notify_absent_parents_button"),
            colors = ButtonDefaults.buttonColors(containerColor = ZambiaGreenDark),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Send Absentee Alerts to Parents (${stats.absentCount + stats.sickCount})")
        }
    }

    // Official MoGE Zambian Register Broadsheet Dialog
    if (showReportDialog) {
        val registerReport = remember(registerItems, stats, selectedGrade, selectedDate, selectedSession) {
            attendanceViewModel.generateMoGEAttendanceRegisterReport()
        }

        AlertDialog(
            onDismissRequest = { showReportDialog = false },
            title = {
                Text(
                    text = "Official MoGE Class Register",
                    fontWeight = FontWeight.Bold,
                    color = ZambiaGreenDark
                )
            },
            text = {
                Column {
                    Text(
                        text = "Standard Republic of Zambia Ministry of General Education Register Log:",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth().height(260.dp),
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFF8FAFC),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ExcelBorder)
                    ) {
                        LazyColumn(modifier = Modifier.padding(8.dp)) {
                            item {
                                Text(
                                    text = registerReport,
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
                            putExtra(Intent.EXTRA_TEXT, registerReport)
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, "Share MoGE Register")
                        context.startActivity(shareIntent)
                        showReportDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ZambiaGreen)
                ) {
                    Text("Share Register")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showReportDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
private fun StatusKpi(title: String, value: Any, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(title, fontSize = 10.sp, color = color, fontWeight = FontWeight.SemiBold)
        Text("$value", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable
private fun ZambianCodeChip(
    label: String,
    title: String,
    isSelected: Boolean,
    color: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(30.dp)
            .clip(CircleShape)
            .background(if (isSelected) color else color.copy(alpha = 0.1f))
            .border(1.dp, color, CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) Color.White else color
        )
    }
}
