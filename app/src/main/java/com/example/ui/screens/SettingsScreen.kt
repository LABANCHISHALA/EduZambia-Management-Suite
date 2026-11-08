package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.SchoolProfile
import com.example.ui.components.ZambiaFlagRibbon
import com.example.ui.theme.ExcelBorder
import com.example.ui.theme.ZambiaBlack
import com.example.ui.theme.ZambiaGreen
import com.example.ui.theme.ZambiaGreenDark
import com.example.ui.theme.ZambiaOrange
import com.example.ui.theme.ZambiaRed
import com.example.ui.viewmodel.SchoolViewModel

@Composable
fun SettingsScreen(
    viewModel: SchoolViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val schoolProfile by viewModel.schoolProfile.collectAsState()
    val initial = schoolProfile ?: SchoolProfile()

    var schoolName by remember(initial) { mutableStateOf(initial.schoolName) }
    var motto by remember(initial) { mutableStateOf(initial.motto) }
    var emisNumber by remember(initial) { mutableStateOf(initial.emisNumber) }
    var province by remember(initial) { mutableStateOf(initial.province) }
    var district by remember(initial) { mutableStateOf(initial.district) }
    var headteacherName by remember(initial) { mutableStateOf(initial.headteacherName) }
    var headteacherTitle by remember(initial) { mutableStateOf(initial.headteacherTitle) }
    var term by remember(initial) { mutableStateOf(initial.currentTerm) }
    var nextTermBegins by remember(initial) { mutableStateOf(initial.nextTermBegins) }
    var showCoatOfArms by remember(initial) { mutableStateOf(initial.showCoatOfArms) }
    var showNationalFlag by remember(initial) { mutableStateOf(initial.showNationalFlag) }
    var defaultCurriculum by remember(initial) { mutableStateOf(initial.defaultCurriculum) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF1F5F9))
            .verticalScroll(rememberScrollState())
            .padding(14.dp)
            .testTag("settings_screen"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Admin Configuration Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = ZambiaGreenDark)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                ZambiaFlagRibbon(height = 4)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.AdminPanelSettings,
                            contentDescription = "Admin",
                            tint = ZambiaGreenDark,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "Admin School Configuration & Emblems",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        )
                        Text(
                            text = "Edit school details, Coat of Arms and National Flag display",
                            style = MaterialTheme.typography.bodySmall.copy(color = ZambiaOrange, fontSize = 11.sp)
                        )
                    }
                }
            }
        }

        // Emblem Display Toggles
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.dp, ExcelBorder)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "National Emblems & Logo Display",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = ZambiaGreenDark
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Coat of Arms Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .border(1.dp, ExcelBorder, RoundedCornerShape(6.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.zambia_coat_of_arms),
                                contentDescription = "Coat of Arms",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.padding(2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Show Zambia Coat of Arms", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            Text("Displays 'One Zambia, One Nation' crest on headers & report cards", fontSize = 10.sp, color = Color.Gray)
                        }
                    }
                    Switch(
                        checked = showCoatOfArms,
                        onCheckedChange = { showCoatOfArms = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = ZambiaGreen, checkedTrackColor = Color(0xFFC8E6C9)),
                        modifier = Modifier.testTag("switch_coat_of_arms")
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFE2E8F0))

                // National Flag Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .border(1.dp, ZambiaOrange, RoundedCornerShape(6.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.zambia_national_flag),
                                contentDescription = "National Flag",
                                contentScale = ContentScale.Crop
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Show Zambia National Flag", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            Text("Displays Eagle in flight with green, red, black, orange", fontSize = 10.sp, color = Color.Gray)
                        }
                    }
                    Switch(
                        checked = showNationalFlag,
                        onCheckedChange = { showNationalFlag = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = ZambiaOrange, checkedTrackColor = Color(0xFFFFE0B2)),
                        modifier = Modifier.testTag("switch_national_flag")
                    )
                }
            }
        }

        // Editable School Profile Details
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.dp, ExcelBorder)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "School Identification & Contacts",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = ZambiaGreenDark
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = schoolName,
                    onValueChange = { schoolName = it },
                    label = { Text("School Name") },
                    modifier = Modifier.fillMaxWidth().testTag("input_school_name")
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = motto,
                    onValueChange = { motto = it },
                    label = { Text("School Motto") },
                    modifier = Modifier.fillMaxWidth().testTag("input_motto")
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = emisNumber,
                        onValueChange = { emisNumber = it },
                        label = { Text("EMIS School Code") },
                        modifier = Modifier.weight(1f).testTag("input_emis")
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = province,
                        onValueChange = { province = it },
                        label = { Text("Province") },
                        modifier = Modifier.weight(1f).testTag("input_province")
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = district,
                        onValueChange = { district = it },
                        label = { Text("District") },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = term,
                        onValueChange = { term = it },
                        label = { Text("Current Term") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = headteacherName,
                        onValueChange = { headteacherName = it },
                        label = { Text("Headteacher Name") },
                        modifier = Modifier.weight(1.2f).testTag("input_headteacher")
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = headteacherTitle,
                        onValueChange = { headteacherTitle = it },
                        label = { Text("Qualifications") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = nextTermBegins,
                    onValueChange = { nextTermBegins = it },
                    label = { Text("Next Term Reopening Date") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Save Changes Action
        Button(
            onClick = {
                viewModel.updateSchoolProfile(
                    initial.copy(
                        schoolName = schoolName,
                        motto = motto,
                        emisNumber = emisNumber,
                        province = province,
                        district = district,
                        headteacherName = headteacherName,
                        headteacherTitle = headteacherTitle,
                        currentTerm = term,
                        nextTermBegins = nextTermBegins,
                        showCoatOfArms = showCoatOfArms,
                        showNationalFlag = showNationalFlag,
                        defaultCurriculum = defaultCurriculum
                    )
                )
                Toast.makeText(context, "School profile updated successfully!", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("save_settings_button"),
            colors = ButtonDefaults.buttonColors(containerColor = ZambiaGreen),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(Icons.Default.Save, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Save School Profile Changes", fontWeight = FontWeight.Bold)
        }
    }
}
