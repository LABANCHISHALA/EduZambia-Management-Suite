package com.example.ui.screens

import android.content.Intent
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Announcement
import com.example.data.model.ParentMessage
import com.example.ui.theme.ExcelBorder
import com.example.ui.theme.ZambiaBlack
import com.example.ui.theme.ZambiaGreen
import com.example.ui.theme.ZambiaGreenDark
import com.example.ui.theme.ZambiaOrange
import com.example.ui.theme.ZambiaRed
import com.example.ui.viewmodel.SchoolViewModel

@Composable
fun CommunicationScreen(
    viewModel: SchoolViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val announcements by viewModel.allAnnouncements.collectAsState()
    val messages by viewModel.allParentMessages.collectAsState()
    val pupils by viewModel.allPupils.collectAsState()

    var selectedTabIndex by remember { mutableStateOf(0) } // 0 = Announcements, 1 = Parent Messages
    var showBroadcastDialog by remember { mutableStateOf(false) }
    var showDirectMessageDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (selectedTabIndex == 0) showBroadcastDialog = true else showDirectMessageDialog = true
                },
                containerColor = ZambiaGreen,
                contentColor = Color.White,
                modifier = Modifier.testTag("fab_communication")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Notice or Message")
            }
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFFF1F5F9))
                .padding(padding)
                .testTag("communication_screen")
        ) {
            // Tab Row
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
                    text = { Text("School Announcements (${announcements.size})", fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.Campaign, contentDescription = null) },
                    modifier = Modifier.testTag("tab_announcements")
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    text = { Text("Parent-Teacher SMS (${messages.size})", fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.Message, contentDescription = null) },
                    modifier = Modifier.testTag("tab_parent_messages")
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (selectedTabIndex == 0) {
                // Announcements Feed
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(announcements) { notice ->
                        Card(
                            modifier = Modifier.fillMaxWidth().testTag("notice_card_${notice.id}"),
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
                                    val (badgeBg, badgeText) = when (notice.category) {
                                        "URGENT" -> ZambiaRed to Color.White
                                        "EVENT" -> ZambiaOrange to Color.White
                                        "FEATURE_UPDATE" -> Color(0xFF1E88E5) to Color.White
                                        else -> ZambiaGreenDark to Color.White
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = badgeBg
                                    ) {
                                        Text(
                                            text = notice.category,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = badgeText,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 9.sp
                                            ),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = notice.date,
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp, color = Color.Gray)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        IconButton(
                                            onClick = {
                                                val sendIntent = Intent().apply {
                                                    action = Intent.ACTION_SEND
                                                    putExtra(Intent.EXTRA_TEXT, "${notice.title}\n${notice.message}")
                                                    type = "text/plain"
                                                }
                                                val shareIntent = Intent.createChooser(sendIntent, "Share Announcement")
                                                context.startActivity(shareIntent)
                                            },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(Icons.Default.Share, contentDescription = "Share", tint = ZambiaGreenDark, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = notice.title,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = ZambiaBlack
                                    )
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = notice.message,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 12.sp,
                                        color = Color(0xFF334155)
                                    )
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "Posted by: ${notice.author} • Real-time Broadcast",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 10.sp,
                                        color = ZambiaGreenDark,
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            }
                        }
                    }
                }
            } else {
                // Parent Messages Log
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(messages) { msg ->
                        Card(
                            modifier = Modifier.fillMaxWidth().testTag("msg_card_${msg.id}"),
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
                                        color = when (msg.messageType) {
                                            "ACADEMIC" -> ZambiaGreen.copy(alpha = 0.15f)
                                            "ATTENDANCE" -> ZambiaOrange.copy(alpha = 0.15f)
                                            else -> Color(0xFFE2E8F0)
                                        }
                                    ) {
                                        Text(
                                            text = msg.messageType,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = when (msg.messageType) {
                                                    "ACADEMIC" -> ZambiaGreenDark
                                                    "ATTENDANCE" -> ZambiaOrange
                                                    else -> ZambiaBlack
                                                },
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 9.sp
                                            ),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.DoneAll,
                                            contentDescription = "Delivered",
                                            tint = ZambiaGreen,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Delivered via SMS", fontSize = 10.sp, color = ZambiaGreenDark)
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "To Parent of: ${msg.pupilName} (${msg.parentPhone})",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = ZambiaBlack)
                                )
                                Text(
                                    text = "Subject: ${msg.subject}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = ZambiaGreenDark
                                    )
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    color = Color(0xFFF8FAFC),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = msg.message,
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = Color(0xFF334155)),
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Broadcast Announcement Dialog
    if (showBroadcastDialog) {
        var title by remember { mutableStateOf("") }
        var message by remember { mutableStateOf("") }
        var category by remember { mutableStateOf("ACADEMIC") }

        AlertDialog(
            onDismissRequest = { showBroadcastDialog = false },
            title = { Text("Broadcast School Announcement", fontWeight = FontWeight.Bold, color = ZambiaGreenDark) },
            text = {
                Column {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Announcement Headline") },
                        modifier = Modifier.fillMaxWidth().testTag("input_notice_title")
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = message,
                        onValueChange = { message = it },
                        label = { Text("Announcement Body") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth().testTag("input_notice_msg")
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf("ACADEMIC", "EVENT", "URGENT", "PTA").forEach { cat ->
                            val isSel = category == cat
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSel) ZambiaGreen else Color(0xFFE2E8F0))
                                    .clickable { category = cat }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(cat, fontSize = 10.sp, color = if (isSel) Color.White else ZambiaBlack)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank() && message.isNotBlank()) {
                            viewModel.addAnnouncement(title, message, category)
                            showBroadcastDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ZambiaGreen),
                    modifier = Modifier.testTag("submit_broadcast")
                ) {
                    Text("Broadcast Now")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showBroadcastDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Compose Parent Direct Message Dialog
    if (showDirectMessageDialog) {
        var selectedPupilName by remember { mutableStateOf(pupils.firstOrNull()?.fullName ?: "Chileshe Bwalya") }
        var phone by remember { mutableStateOf(pupils.firstOrNull()?.parentPhone ?: "+260 977 123456") }
        var subject by remember { mutableStateOf("Academic Progress Commendation") }
        var body by remember { mutableStateOf("Dear Parent, we commend your child's diligent continuous assessment scores in Mathematics and Science this term.") }

        AlertDialog(
            onDismissRequest = { showDirectMessageDialog = false },
            title = { Text("Send Direct SMS / Notice to Parent", fontWeight = FontWeight.Bold, color = ZambiaGreenDark) },
            text = {
                Column {
                    OutlinedTextField(
                        value = selectedPupilName,
                        onValueChange = { selectedPupilName = it },
                        label = { Text("Pupil Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Parent Mobile Phone") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = subject,
                        onValueChange = { subject = it },
                        label = { Text("Message Subject") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = body,
                        onValueChange = { body = it },
                        label = { Text("SMS / Message Content") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.sendParentMessage(selectedPupilName, phone, subject, body, "ACADEMIC")
                        showDirectMessageDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ZambiaGreen)
                ) {
                    Text("Send SMS Notice")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDirectMessageDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
