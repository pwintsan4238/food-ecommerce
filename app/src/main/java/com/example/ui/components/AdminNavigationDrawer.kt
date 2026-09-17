package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AdminSection
import com.example.model.Language

// Primary Coral/Red colors matching user reference
private val AdminPrimaryRed = Color(0xFFD32F2F)
private val AdminActiveBackground = Color(0xFFFDE8E8)
private val AdminInactiveText = Color(0xFF374151)
private val AdminInactiveIcon = Color(0xFF6B7280)
private val AdminDrawerBg = Color(0xFFFBFBFC)

@Composable
fun AdminNavigationDrawer(
    currentSection: AdminSection,
    currentLanguage: Language,
    onSelectSection: (AdminSection) -> Unit,
    onLogout: () -> Unit,
    onCloseDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalDrawerSheet(
        modifier = modifier
            .width(280.dp)
            .fillMaxHeight(),
        drawerShape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp),
        drawerContainerColor = AdminDrawerBg,
        drawerTonalElevation = 6.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(horizontal = 16.dp, vertical = 20.dp)
        ) {
            // Header: Fork & Knife Icon + MYANMAR FOOD Brand + Back Button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 8.dp)
                    .testTag("admin_drawer_header")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Restaurant,
                        contentDescription = null,
                        tint = AdminPrimaryRed,
                        modifier = Modifier.size(26.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "MYANMAR FOOD",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1E293B),
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = if (currentLanguage == Language.BURMESE) "တိမ်တမန် စီမံခန့်ခွဲရေး" else "Taim Ta Man Admin",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                // Back Button in Admin Drawer
                Surface(
                    shape = CircleShape,
                    color = AdminActiveBackground,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .clickable { onCloseDrawer() }
                        .testTag("admin_drawer_back_button")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = AdminPrimaryRed,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // In-Drawer Back / Return to Page Banner
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = AdminActiveBackground.copy(alpha = 0.6f),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { onCloseDrawer() }
                    .testTag("admin_drawer_back_chip")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        tint = AdminPrimaryRed,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (currentLanguage == Language.BURMESE) "စာမျက်နှာသို့ ပြန်သွားရန်" else "Back to Page",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = AdminPrimaryRed
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Scrollable Menu Items
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                AdminSection.entries.forEach { section ->
                    val isSelected = section == currentSection

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) AdminActiveBackground else Color.Transparent,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                onSelectSection(section)
                                onCloseDrawer()
                            }
                            .testTag("admin_drawer_${section.name.lowercase()}")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 13.dp)
                        ) {
                            Icon(
                                imageVector = section.icon,
                                contentDescription = section.titleEn,
                                tint = if (isSelected) AdminPrimaryRed else AdminInactiveIcon,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = section.titleEn,
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) AdminPrimaryRed else AdminInactiveText
                            )
                        }
                    }
                }
            }

            // Bottom Logout Action
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.Transparent,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
                        onLogout()
                        onCloseDrawer()
                    }
                    .testTag("admin_drawer_logout")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = "Logout",
                        tint = AdminPrimaryRed,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = if (currentLanguage == Language.BURMESE) "အကောင့်ထွက်မည် (Logout)" else "Logout",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = AdminPrimaryRed
                    )
                }
            }
        }
    }
}
