package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.HowToReg
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ZambiaFlagRibbon
import com.example.ui.screens.AttendanceScreen
import com.example.ui.screens.CommunicationScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.PupilsScreen
import com.example.ui.screens.ReportCardDialog
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.TeacherSuiteScreen
import com.example.ui.screens.TimetableScreen
import com.example.ui.screens.WorkbookScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.ZambiaBlack
import com.example.ui.theme.ZambiaGreen
import com.example.ui.theme.ZambiaGreenDark
import com.example.ui.theme.ZambiaOrange
import com.example.ui.theme.ZambiaRed
import com.example.ui.viewmodel.SchoolViewModel
import com.example.ui.viewmodel.ScreenTab

class MainActivity : ComponentActivity() {

    private val viewModel: SchoolViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppContent(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContent(viewModel: SchoolViewModel) {
    val currentTab by viewModel.currentTab.collectAsState()
    val schoolProfile by viewModel.schoolProfile.collectAsState()
    val reportCardPupil by viewModel.reportCardPupil.collectAsState()
    val pupilAssessments by viewModel.pupilAssessmentsForReport.collectAsState()
    val curriculumMode by viewModel.curriculumMode.collectAsState()

    var showMoreMenu by remember { mutableStateOf(false) }

    // Hardware back button navigation: return to Dashboard if on sub-screen
    BackHandler(enabled = currentTab != ScreenTab.DASHBOARD) {
        viewModel.setTab(ScreenTab.DASHBOARD)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ZambiaGreenDark)
                    .windowInsetsPadding(WindowInsets.statusBars)
            ) {
                ZambiaFlagRibbon(height = 4)
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                                    .border(1.dp, ZambiaOrange, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.zed_school_logo),
                                    contentDescription = "ZedEdu Logo",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "ZedEdu Manager",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                )
                                Text(
                                    text = "Zambia Primary & Secondary • ODC & CBC",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = ZambiaOrange,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                    },
                    actions = {
                        // Quick switch between ODC & CBC
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = ZambiaOrange,
                            modifier = Modifier.padding(end = 4.dp)
                        ) {
                            Text(
                                text = curriculumMode,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }

                        // More Actions Dropdown
                        Box {
                            IconButton(
                                onClick = { showMoreMenu = true },
                                modifier = Modifier.testTag("btn_more_menu")
                            ) {
                                Icon(
                                    Icons.Default.MoreHoriz,
                                    contentDescription = "More Modules",
                                    tint = Color.White
                                )
                            }

                            DropdownMenu(
                                expanded = showMoreMenu,
                                onDismissRequest = { showMoreMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("School Timetable") },
                                    leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null, tint = ZambiaGreenDark) },
                                    onClick = {
                                        viewModel.setTab(ScreenTab.TIMETABLE_POLICY)
                                        showMoreMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Parent Notices & SMS") },
                                    leadingIcon = { Icon(Icons.Default.Campaign, contentDescription = null, tint = ZambiaOrange) },
                                    onClick = {
                                        viewModel.setTab(ScreenTab.COMMUNICATION)
                                        showMoreMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Admin Settings") },
                                    leadingIcon = { Icon(Icons.Default.Settings, contentDescription = null, tint = ZambiaBlack) },
                                    onClick = {
                                        viewModel.setTab(ScreenTab.SETTINGS)
                                        showMoreMenu = false
                                    }
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = ZambiaGreenDark,
                        titleContentColor = Color.White
                    )
                )
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 8.dp,
                    modifier = Modifier.testTag("bottom_nav_bar")
                ) {
                    NavigationBarItem(
                        selected = currentTab == ScreenTab.DASHBOARD,
                        onClick = { viewModel.setTab(ScreenTab.DASHBOARD) },
                        icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
                        label = { Text("Dashboard", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = ZambiaGreenDark,
                            indicatorColor = ZambiaGreen
                        ),
                        modifier = Modifier.testTag("nav_dashboard")
                    )

                    NavigationBarItem(
                        selected = currentTab == ScreenTab.WORKBOOK,
                        onClick = { viewModel.setTab(ScreenTab.WORKBOOK) },
                        icon = { Icon(Icons.Default.TableChart, contentDescription = "Workbook") },
                        label = { Text("Workbook", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = ZambiaGreenDark,
                            indicatorColor = ZambiaGreen
                        ),
                        modifier = Modifier.testTag("nav_workbook")
                    )

                    NavigationBarItem(
                        selected = currentTab == ScreenTab.PUPILS_TEACHERS,
                        onClick = { viewModel.setTab(ScreenTab.PUPILS_TEACHERS) },
                        icon = { Icon(Icons.Default.School, contentDescription = "Records") },
                        label = { Text("Records", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = ZambiaGreenDark,
                            indicatorColor = ZambiaGreen
                        ),
                        modifier = Modifier.testTag("nav_records")
                    )

                    NavigationBarItem(
                        selected = currentTab == ScreenTab.ATTENDANCE,
                        onClick = { viewModel.setTab(ScreenTab.ATTENDANCE) },
                        icon = { Icon(Icons.Default.HowToReg, contentDescription = "Attendance") },
                        label = { Text("Attendance", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = ZambiaGreenDark,
                            indicatorColor = ZambiaGreen
                        ),
                        modifier = Modifier.testTag("nav_attendance")
                    )

                    NavigationBarItem(
                        selected = currentTab == ScreenTab.TEACHER_SUITE,
                        onClick = { viewModel.setTab(ScreenTab.TEACHER_SUITE) },
                        icon = { Icon(Icons.Default.MenuBook, contentDescription = "Teacher") },
                        label = { Text("Teacher", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = ZambiaGreenDark,
                            indicatorColor = ZambiaGreen
                        ),
                        modifier = Modifier.testTag("nav_teacher")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                ScreenTab.DASHBOARD -> DashboardScreen(viewModel = viewModel)
                ScreenTab.WORKBOOK -> WorkbookScreen(viewModel = viewModel)
                ScreenTab.PUPILS_TEACHERS -> PupilsScreen(viewModel = viewModel)
                ScreenTab.ATTENDANCE -> AttendanceScreen(viewModel = viewModel)
                ScreenTab.TEACHER_SUITE -> TeacherSuiteScreen(viewModel = viewModel)
                ScreenTab.TIMETABLE_POLICY -> TimetableScreen(viewModel = viewModel)
                ScreenTab.COMMUNICATION -> CommunicationScreen(viewModel = viewModel)
                ScreenTab.SETTINGS -> SettingsScreen(viewModel = viewModel)
            }
        }
    }

    // Modal Report Card Dialog when triggered from any row or screen
    reportCardPupil?.let { pupil ->
        ReportCardDialog(
            pupil = pupil,
            assessments = pupilAssessments,
            profile = schoolProfile,
            curriculumMode = curriculumMode,
            onDismiss = { viewModel.closeReportCard() }
        )
    }
}
