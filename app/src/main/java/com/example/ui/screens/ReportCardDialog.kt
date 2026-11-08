package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.data.model.AssessmentRecord
import com.example.data.model.Pupil
import com.example.data.model.SchoolProfile
import com.example.data.model.ZambianCurriculumHelper
import com.example.ui.components.ZambiaFlagRibbon
import com.example.ui.theme.ExcelBorder
import com.example.ui.theme.ExcelCellAlt
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.ZambiaBlack
import com.example.ui.theme.ZambiaGreen
import com.example.ui.theme.ZambiaGreenDark
import com.example.ui.theme.ZambiaOrange
import com.example.ui.theme.ZambiaRed

@Composable
fun ReportCardDialog(
    pupil: Pupil,
    assessments: List<AssessmentRecord>,
    profile: SchoolProfile?,
    curriculumMode: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val school = profile ?: SchoolProfile()

    val totalScoreSum = assessments.sumOf { it.totalScore }
    val averageScore = if (assessments.isNotEmpty()) totalScoreSum / assessments.size else 0.0
    val overallEcz = ZambianCurriculumHelper.calculateEczGrade(averageScore)
    val overallCbc = ZambianCurriculumHelper.calculateCbcLevel(averageScore)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .testTag("report_card_dialog"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Top Action Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(ZambiaGreenDark)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Official Zambian Learner Report Card",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    )
                    Row {
                        IconButton(
                            onClick = {
                                val summary = buildReportSummary(pupil, assessments, school, averageScore, overallEcz)
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, summary)
                                    type = "text/plain"
                                }
                                val shareIntent = Intent.createChooser(sendIntent, "Share Report Card")
                                context.startActivity(shareIntent)
                            },
                            modifier = Modifier.testTag("share_report_button")
                        ) {
                            Icon(Icons.Default.Share, contentDescription = "Share", tint = Color.White)
                        }
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.testTag("close_report_button")
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }
                }

                // National Flag Ribbon
                ZambiaFlagRibbon(height = 5)

                // Printable Report Card Scrollable Body
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(14.dp)
                ) {
                    // Official Government & School Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Coat of Arms
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.zambia_coat_of_arms),
                                contentDescription = "Zambia Coat of Arms",
                                contentScale = ContentScale.Fit
                            )
                        }

                        // Center Titles
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "REPUBLIC OF ZAMBIA",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = ZambiaGreenDark,
                                    letterSpacing = 1.sp
                                )
                            )
                            Text(
                                text = "MINISTRY OF GENERAL EDUCATION",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = ZambiaBlack,
                                    fontSize = 11.sp
                                )
                            )
                            Text(
                                text = school.schoolName,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = ZambiaGreen,
                                    fontSize = 14.sp,
                                    textAlign = TextAlign.Center
                                )
                            )
                            Text(
                                text = "EMIS: ${school.emisNumber} • ${school.province} • ${school.district}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 10.sp,
                                    color = Color.Gray
                                )
                            )
                        }

                        // National Flag
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, ZambiaOrange, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.zambia_national_flag),
                                contentDescription = "Zambia National Flag",
                                contentScale = ContentScale.Crop
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = ZambiaOrange, thickness = 2.dp)
                    Spacer(modifier = Modifier.height(8.dp))

                    // Title
                    Text(
                        text = "TERMINAL LEARNER PROGRESS & ASSESSMENT REPORT",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = ZambiaGreenDark,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Pupil Info Card
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = ExcelCellAlt,
                        border = androidx.compose.foundation.BorderStroke(1.dp, ExcelBorder)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "Pupil Name: ",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = pupil.fullName,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = ZambiaGreenDark
                                    )
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Text(
                                    text = "Adm/Exam No: ",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = pupil.admissionNumber,
                                    style = MaterialTheme.typography.bodySmall.copy(color = ZambiaBlack)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "Class: ${pupil.section} (${pupil.levelCategory})",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Text(
                                    text = "Term: ${school.currentTerm}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "Attendance: ${pupil.attendanceDaysPresent} / ${pupil.totalSchoolDays} Days (${(pupil.attendanceDaysPresent * 100) / pupil.totalSchoolDays}%)",
                                    style = MaterialTheme.typography.bodySmall.copy(color = ZambiaGreen)
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Text(
                                    text = "Standard: ${if (curriculumMode == "CBC") "CBC (SBA)" else "ODC (ECZ)"}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = ZambiaOrange,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Subjects Assessment Table
                    Text(
                        text = "ACADEMIC & CONTINUOUS ASSESSMENT PERFORMANCE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = ZambiaGreenDark
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    // Table Header
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = ZambiaGreen,
                        shape = RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Subject",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.weight(2.5f)
                            )
                            Text(
                                text = "CA (30)",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "Exam (70)",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                ),
                                modifier = Modifier.weight(1.2f)
                            )
                            Text(
                                text = "Total",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = if (curriculumMode == "CBC") "CBC Level" else "ECZ Grd",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                ),
                                modifier = Modifier.weight(1.3f)
                            )
                        }
                    }

                    // Table Rows
                    assessments.forEachIndexed { index, record ->
                        val bg = if (index % 2 == 0) Color.White else ExcelCellAlt
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = bg,
                            border = androidx.compose.foundation.BorderStroke(0.5.dp, ExcelBorder)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp, horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = record.subject,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                    modifier = Modifier.weight(2.5f)
                                )
                                Text(
                                    text = String.format("%.0f", record.continuousAssessment),
                                    style = MaterialTheme.typography.bodySmall.copy(textAlign = TextAlign.Center),
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = String.format("%.0f", record.midTermScore + record.terminalExamScore),
                                    style = MaterialTheme.typography.bodySmall.copy(textAlign = TextAlign.Center),
                                    modifier = Modifier.weight(1.2f)
                                )
                                Text(
                                    text = String.format("%.0f%%", record.totalScore),
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (record.totalScore >= 75) ZambiaGreen else if (record.totalScore >= 50) ZambiaBlack else ZambiaRed,
                                        textAlign = TextAlign.Center
                                    ),
                                    modifier = Modifier.weight(1f)
                                )
                                Box(
                                    modifier = Modifier
                                        .weight(1.3f)
                                        .padding(horizontal = 4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    val badgeColor = when {
                                        record.totalScore >= 75 -> ZambiaGreen
                                        record.totalScore >= 60 -> ZambiaOrange
                                        record.totalScore >= 45 -> Color(0xFF1976D2)
                                        else -> ZambiaRed
                                    }
                                    val label = if (curriculumMode == "CBC") record.cbcPerformanceLevel else "Grade ${record.eczGrade}"
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
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
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Overall Summary Card
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF1F8E9),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ZambiaGreen)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Average Mark",
                                    style = MaterialTheme.typography.labelSmall.copy(color = ZambiaGreenDark)
                                )
                                Text(
                                    text = String.format("%.1f%%", averageScore),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = ZambiaGreenDark
                                    )
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "ECZ Result",
                                    style = MaterialTheme.typography.labelSmall.copy(color = ZambiaGreenDark)
                                )
                                Text(
                                    text = ZambianCurriculumHelper.getEczGradeDescription(overallEcz).substringBefore("(").trim(),
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = ZambiaOrange
                                    )
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "CBC Descriptor",
                                    style = MaterialTheme.typography.labelSmall.copy(color = ZambiaGreenDark)
                                )
                                Text(
                                    text = ZambianCurriculumHelper.getCbcLevelDescription(overallCbc).substringBefore("(").trim(),
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = ZambiaGreen
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // CBC Core Competencies Evaluation Rubric
                    Text(
                        text = "CBC 21ST CENTURY CORE COMPETENCIES EVALUATION",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = ZambiaGreenDark
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    CompetencyRow("Critical Thinking & Problem Solving", "Exceeding Expectations (EE)")
                    CompetencyRow("Communication & Collaboration", "Meeting Expectations (ME)")
                    CompetencyRow("Digital Literacy & Innovation", "Exceeding Expectations (EE)")
                    CompetencyRow("Citizenship & National Values", "Exceeding Expectations (EE)")

                    Spacer(modifier = Modifier.height(10.dp))

                    // Conduct & Teacher Remarks
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = ExcelCellAlt,
                        border = androidx.compose.foundation.BorderStroke(1.dp, ExcelBorder)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "Class Teacher's General Remarks:",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "${pupil.conductRemarks} Shows remarkable academic enthusiasm in mathematics and science.",
                                style = MaterialTheme.typography.bodySmall.copy(color = ZambiaBlack)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Headteacher's Comments & Official Endorsement:",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "An outstanding term's effort. Recommended to sustain momentum for national examinations.",
                                style = MaterialTheme.typography.bodySmall.copy(color = ZambiaGreenDark)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                Column {
                                    Text(
                                        text = "Signed: ____________________",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp)
                                    )
                                    Text(
                                        text = "${school.headteacherName} (${school.headteacherTitle})",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp
                                        )
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0x220B6623),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, ZambiaGreen)
                                ) {
                                    Text(
                                        text = "★ OFFICIAL SCHOOL STAMP ★",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = ZambiaGreenDark,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Next Term Reopens: ${school.nextTermBegins} | Outstanding Fees: Nil",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = ZambiaOrange,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Bottom Buttons
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFFF8FAFC),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ExcelBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Close")
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Button(
                            onClick = {
                                val summary = buildReportSummary(pupil, assessments, school, averageScore, overallEcz)
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, summary)
                                    type = "text/plain"
                                }
                                val shareIntent = Intent.createChooser(sendIntent, "Share Report Card")
                                context.startActivity(shareIntent)
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ZambiaGreen)
                        ) {
                            Icon(Icons.Default.Print, contentDescription = "Print/Share")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Print / Share Card")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CompetencyRow(competency: String, rating: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "• $competency",
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp)
        )
        Text(
            text = rating,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = ZambiaGreenDark
            )
        )
    }
}

private fun buildReportSummary(
    pupil: Pupil,
    assessments: List<AssessmentRecord>,
    school: SchoolProfile,
    averageScore: Double,
    overallEcz: String
): String {
    val sb = StringBuilder()
    sb.appendLine("==============================================")
    sb.appendLine("REPUBLIC OF ZAMBIA - MINISTRY OF EDUCATION")
    sb.appendLine("${school.schoolName.uppercase()}")
    sb.appendLine("EMIS: ${school.emisNumber} | ${school.province}")
    sb.appendLine("TERMINAL LEARNER PROGRESS & ASSESSMENT REPORT")
    sb.appendLine("==============================================")
    sb.appendLine("Pupil: ${pupil.fullName} (Adm: ${pupil.admissionNumber})")
    sb.appendLine("Class: ${pupil.section} | Term: ${school.currentTerm}")
    sb.appendLine("Attendance: ${pupil.attendanceDaysPresent}/${pupil.totalSchoolDays} Days")
    sb.appendLine("----------------------------------------------")
    sb.appendLine("SUBJECT PERFORMANCE:")
    assessments.forEach { a ->
        sb.appendLine("• ${a.subject}: CA ${a.continuousAssessment.toInt()}/30 + Exam ${(a.midTermScore + a.terminalExamScore).toInt()}/70 = ${a.totalScore.toInt()}% [ECZ: ${a.eczGrade} | CBC: ${a.cbcPerformanceLevel}]")
    }
    sb.appendLine("----------------------------------------------")
    sb.appendLine("Term Average: ${String.format("%.1f", averageScore)}%")
    sb.appendLine("Overall ECZ Standard: ${ZambianCurriculumHelper.getEczGradeDescription(overallEcz)}")
    sb.appendLine("Next Term Reopens: ${school.nextTermBegins}")
    sb.appendLine("Headteacher: ${school.headteacherName}")
    sb.appendLine("==============================================")
    return sb.toString()
}
