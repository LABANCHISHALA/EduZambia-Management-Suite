package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.SchoolProfile
import com.example.ui.theme.ZambiaBlack
import com.example.ui.theme.ZambiaGreen
import com.example.ui.theme.ZambiaGreenDark
import com.example.ui.theme.ZambiaOrange
import com.example.ui.theme.ZambiaRed

/**
 * National Flag Tri-Color Ribbon representing Zambia's heritage:
 * Red (Freedom struggle), Black (The Zambian people), Orange (Mineral wealth) on Green base
 */
@Composable
fun ZambiaFlagRibbon(modifier: Modifier = Modifier, height: Int = 6) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(height.dp)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(height.dp)
                .background(ZambiaRed)
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .height(height.dp)
                .background(ZambiaBlack)
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .height(height.dp)
                .background(ZambiaOrange)
        )
    }
}

/**
 * Header card displaying School Identity, Zambia Coat of Arms and National Flag
 */
@Composable
fun ZambiaSchoolHeader(
    profile: SchoolProfile?,
    modifier: Modifier = Modifier
) {
    val school = profile ?: SchoolProfile()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("school_header_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ZambiaGreenDark),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Flag Accent Ribbon
            ZambiaFlagRibbon(height = 6)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // School Crest / Logo
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(2.dp, ZambiaOrange, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.zed_school_logo),
                            contentDescription = "School Crest Logo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    // School Name and Motto
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = school.schoolName,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                letterSpacing = 0.3.sp
                            )
                        )
                        Text(
                            text = school.motto,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFFFFD54F),
                                fontSize = 11.sp
                            )
                        )
                        Text(
                            text = "EMIS: ${school.emisNumber} • ${school.province}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFFC8E6C9),
                                fontSize = 10.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Coat of Arms or Flag if enabled
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (school.showCoatOfArms) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.White)
                                    .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.zambia_coat_of_arms),
                                    contentDescription = "Coat of Arms - One Zambia One Nation",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.padding(2.dp)
                                )
                            }
                        }

                        if (school.showNationalFlag) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.White)
                                    .border(1.dp, ZambiaOrange, RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.zambia_national_flag),
                                    contentDescription = "Zambia National Flag",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Academic Term & Status Banner
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0x33000000),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🇿🇲 Ministry of Education Standards",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp
                            )
                        )
                        Text(
                            text = "${school.currentTerm} | ODC & CBC Compliant",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = ZambiaOrange,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }
    }
}
