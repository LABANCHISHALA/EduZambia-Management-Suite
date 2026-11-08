package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.HowToReg
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Announcement
import com.example.ui.components.ZambiaSchoolHeader
import com.example.ui.theme.ExcelBorder
import com.example.ui.theme.ZambiaBlack
import com.example.ui.theme.ZambiaGreen
import com.example.ui.theme.ZambiaGreenDark
import com.example.ui.theme.ZambiaOrange
import com.example.ui.theme.ZambiaRed
import com.example.ui.viewmodel.SchoolViewModel
import com.example.ui.viewmodel.ScreenTab

@Composable
fun DashboardScreen(
    viewModel: SchoolViewModel,
    modifier: Modifier = Modifier
) {
    val schoolProfile by viewModel.schoolProfile.collectAsState()
    val pupils by viewModel.allPupils.collectAsState()
    val teachers by viewModel.allTeachers.collectAsState()
    val announcements by viewModel.allAnnouncements.collectAsState()
    val curriculumMode by viewModel.curriculumMode.collectAsState()

    val primaryCount = pupils.count { it.levelCategory == "PRIMARY" }
    val secondaryCount = pupils.count { it.levelCategory == "SECONDARY" }

    var showUpdatesDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF1F5F9))
            .padding(horizontal = 14.dp)
            .testTag("dashboard_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 24.dp)
    ) {
        // School Identity & Emblem Header
        item {
            ZambiaSchoolHeader(profile = schoolProfile)
        }

        // New System Updates Alert Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showUpdatesDialog = true }
                    .testTag("banner_new_updates"),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                border = androidx.compose.foundation.BorderStroke(1.dp, ZambiaOrange)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(ZambiaOrange),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.NewReleases,
                            contentDescription = "New Updates",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "WHAT'S NEW • V2.4 UPDATES",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = ZambiaOrange,
                                    fontSize = 11.sp
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = ZambiaGreenDark
                            ) {
                                Text(
                                    text = "TAP FOR DETAILS",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 8.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                        Text(
                            text = "New MoGE Register, Zambian ECZ & CBC automated workbooks, and attendance sync active.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.sp,
                                color = Color(0xFF4E2600)
                            )
                        )
                    }
                }
            }
        }

        // Metrics Grid (4 KPI Cards)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                KpiCard(
                    title = "Total Pupils",
                    value = "${pupils.size}",
                    sub = "Primary: $primaryCount • Sec: $secondaryCount",
                    icon = Icons.Default.School,
                    accentColor = ZambiaGreen,
                    modifier = Modifier.weight(1f).testTag("kpi_pupils")
                )
                KpiCard(
                    title = "Teaching Staff",
                    value = "${teachers.size}",
                    sub = "5 Certified Depts",
                    icon = Icons.Default.Groups,
                    accentColor = ZambiaOrange,
                    modifier = Modifier.weight(1f).testTag("kpi_teachers")
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                KpiCard(
                    title = "Today's Attendance",
                    value = "96.7%",
                    sub = "Morning Session Complete",
                    icon = Icons.Default.HowToReg,
                    accentColor = Color(0xFF1E88E5),
                    modifier = Modifier.weight(1f).testTag("kpi_attendance")
                )
                KpiCard(
                    title = "Academic Term",
                    value = "Term 2",
                    sub = "Week 9 of 13 • 2026",
                    icon = Icons.Default.DateRange,
                    accentColor = ZambiaRed,
                    modifier = Modifier.weight(1f).testTag("kpi_term")
                )
            }
        }

        // Primary Call to Action: Automated Excel Workbook
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.setTab(ScreenTab.WORKBOOK) }
                    .testTag("dashboard_banner_workbook"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = ZambiaGreen),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color.White,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            Icons.Default.TableChart,
                            contentDescription = "Excel Workbook",
                            tint = ZambiaGreenDark,
                            modifier = Modifier.padding(10.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "AUTOMATED EXCEL WORKBOOK",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White,
                                    fontSize = 12.sp
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = ZambiaOrange
                            ) {
                                Text(
                                    text = "FEATURED",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Enter continuous assessments (CA) & exams. Automatically computes ECZ grades, CBC levels & terminal report cards.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFFE8F5E9),
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }

        // Quick Action Hub
        item {
            Text(
                text = "Administrative Modules & Academic Suite",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = ZambiaGreenDark
                )
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DashboardActionItem(
                    title = "Pupils & Staff",
                    sub = "Database records",
                    icon = Icons.Default.Person,
                    color = ZambiaGreenDark,
                    onClick = { viewModel.setTab(ScreenTab.PUPILS_TEACHERS) },
                    modifier = Modifier.weight(1f).testTag("action_pupils")
                )
                DashboardActionItem(
                    title = "Attendance",
                    sub = "Daily Register",
                    icon = Icons.Default.HowToReg,
                    color = Color(0xFF1E88E5),
                    onClick = { viewModel.setTab(ScreenTab.ATTENDANCE) },
                    modifier = Modifier.weight(1f).testTag("action_attendance")
                )
                DashboardActionItem(
                    title = "Teacher Suite",
                    sub = "Schemes & Plans",
                    icon = Icons.Default.MenuBook,
                    color = ZambiaOrange,
                    onClick = { viewModel.setTab(ScreenTab.TEACHER_SUITE) },
                    modifier = Modifier.weight(1f).testTag("action_teachersuite")
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DashboardActionItem(
                    title = "Timetable",
                    sub = "Periods 1 to 8",
                    icon = Icons.Default.DateRange,
                    color = Color(0xFF6A1B9A),
                    onClick = { viewModel.setTab(ScreenTab.TIMETABLE_POLICY) },
                    modifier = Modifier.weight(1f).testTag("action_timetable")
                )
                DashboardActionItem(
                    title = "Parent Notices",
                    sub = "SMS & alerts",
                    icon = Icons.Default.Campaign,
                    color = ZambiaRed,
                    onClick = { viewModel.setTab(ScreenTab.COMMUNICATION) },
                    modifier = Modifier.weight(1f).testTag("action_communication")
                )
                DashboardActionItem(
                    title = "Settings",
                    sub = "Admin Profile",
                    icon = Icons.Default.Settings,
                    color = ZambiaBlack,
                    onClick = { viewModel.setTab(ScreenTab.SETTINGS) },
                    modifier = Modifier.weight(1f).testTag("action_settings")
                )
            }
        }

        // Real-Time Announcements & Notifications
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.NotificationsActive,
                        contentDescription = "Alerts",
                        tint = ZambiaRed,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Real-Time School Announcements",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = ZambiaGreenDark
                        )
                    )
                }
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = ZambiaRed.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "LIVE UPDATES",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = ZambiaRed,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }

        // Announcements Cards
        items(announcements.take(3)) { announcement ->
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
                            color = when (announcement.category) {
                                "URGENT" -> ZambiaRed.copy(alpha = 0.15f)
                                "EVENT" -> ZambiaOrange.copy(alpha = 0.15f)
                                "ACADEMIC" -> ZambiaGreen.copy(alpha = 0.15f)
                                else -> Color(0xFFE2E8F0)
                            }
                        ) {
                            Text(
                                text = announcement.category,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = when (announcement.category) {
                                        "URGENT" -> ZambiaRed
                                        "EVENT" -> ZambiaOrange
                                        "ACADEMIC" -> ZambiaGreenDark
                                        else -> ZambiaBlack
                                    },
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Text(
                            text = announcement.date,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 10.sp,
                                color = Color.Gray
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = announcement.title,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = ZambiaBlack
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = announcement.message,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp,
                            color = Color(0xFF475569)
                        )
                    )
                }
            }
        }
    }

    if (showUpdatesDialog) {
        AlertDialog(
            onDismissRequest = { showUpdatesDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.NewReleases, contentDescription = null, tint = ZambiaOrange)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ZedEdu Manager V2.4 Release Notes",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = ZambiaGreenDark
                        )
                    )
                }
            },
            text = {
                Column {
                    Text(
                        text = "Recently deployed features & curriculum enhancements:",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    UpdateBulletPoint("🇿🇲 Zambian MoGE Attendance Entity & Register", "New Room database entity tracking official codes: [P] Present, [A] Absent, [L] Late, [E] Excused, [S] Sick with gender disaggregation (Boys vs Girls %) and Form ZM-ATT/2026 export.")
                    UpdateBulletPoint("📊 Excel Automated Continuous Assessment Workbook", "Full spreadsheet view for Primary (Grades 1-7) & Secondary (Grades 8-12) with instant live recalculation of CA (30%), Mid-Term (20%), and Terminal Exam (50%).")
                    UpdateBulletPoint("📄 Official Terminal Report Cards", "Automated report cards with Zambia Coat of Arms ('One Zambia, One Nation'), National Flag, ECZ 1-9 grades, CBC 21st-century rubrics, and school stamp.")
                    UpdateBulletPoint("📚 Teacher Academic & Planning Suite", "Full suites for Schemes of Work, 5-Step Lesson Plans, SBA task weights, and CPD Department Meeting minutes.")
                    UpdateBulletPoint("⏰ Period 1-8 Timetable & National Homework Policy", "Periods schedule with morning flag raising & anthem, breaks, clubs, and Ministry stage-by-stage study allowances.")
                    UpdateBulletPoint("⚙️ Admin Customization", "Editable School Name, EMIS code, Motto, Province, District, and Coat of Arms / Flag visibility toggles.")
                }
            },
            confirmButton = {
                Button(
                    onClick = { showUpdatesDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = ZambiaGreen)
                ) {
                    Text("Got It")
                }
            }
        )
    }
}

@Composable
private fun UpdateBulletPoint(title: String, desc: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(title, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = ZambiaGreenDark))
        Text(desc, style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = Color(0xFF334155)))
    }
}

@Composable
private fun KpiCard(
    title: String,
    value: String,
    sub: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, ExcelBorder)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.Gray,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        icon,
                        contentDescription = title,
                        tint = accentColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = ZambiaBlack,
                    fontSize = 20.sp
                )
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = sub,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 10.sp,
                    color = accentColor,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}

@Composable
private fun DashboardActionItem(
    title: String,
    sub: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, ExcelBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = title,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = ZambiaBlack,
                    fontSize = 11.sp
                )
            )
            Text(
                text = sub,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color.Gray,
                    fontSize = 9.sp
                )
            )
        }
    }
}
