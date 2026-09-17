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
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Language
import com.example.model.Strings

@Composable
fun AppNavigationDrawer(
    customerName: String,
    customerPhone: String,
    currentLanguage: Language,
    activeOrderCount: Int,
    cartItemCount: Int,
    onNavigateHome: () -> Unit,
    onNavigateTracking: () -> Unit,
    onNavigateHistory: () -> Unit,
    onNavigateProfile: () -> Unit,
    onOpenCart: () -> Unit,
    onOpenSettings: () -> Unit,
    onSwitchAccount: () -> Unit,
    onLogout: () -> Unit,
    onOpenAuth: () -> Unit = {},
    onCloseDrawer: () -> Unit,
    isAdmin: Boolean = false,
    onNavigateAdmin: () -> Unit = {},
    brandName: String = "",
    supportPhone: String = "",
    onContactSupport: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var showLogoutConfirm by remember { mutableStateOf(false) }
    val isGuest = customerName.isBlank() && customerPhone.isBlank()

    ModalDrawerSheet(
        modifier = modifier
            .width(320.dp)
            .fillMaxHeight(),
        drawerContainerColor = MaterialTheme.colorScheme.surface,
        drawerTonalElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
        ) {
            // Header Profile Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (isGuest) Icons.Default.PersonOutline else Icons.Default.Person,
                                    contentDescription = "User Avatar",
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (!isGuest) customerName else Strings.guestUser(currentLanguage),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    maxLines = 1,
                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                )
                                if (isAdmin) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.primary
                                    ) {
                                        Text(
                                            text = "ADMIN",
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = MaterialTheme.colorScheme.onPrimary
                                        )
                                    }
                                }
                            }
                            Text(
                                text = if (!isGuest) customerPhone else Strings.guestUserDesc(currentLanguage),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.75f),
                                fontSize = 11.sp,
                                maxLines = 1
                            )
                        }

                        // Back Button in Customer Drawer
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .clickable { onCloseDrawer() }
                                .testTag("drawer_back_button")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Quick Action pill button in header: Log In / Sign Up if guest, Switch Account if logged in
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier
                            .clickable {
                                onCloseDrawer()
                                if (isGuest) onOpenAuth() else onSwitchAccount()
                            }
                            .testTag("drawer_switch_account_pill")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = if (isGuest) Icons.AutoMirrored.Filled.Login else Icons.Default.SwapHoriz,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isGuest) Strings.loginOrSignUp(currentLanguage) else Strings.switchAccount(currentLanguage),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Main Navigation Section
            DrawerNavSectionTitle(
                title = if (currentLanguage == Language.BURMESE) "ပင်မ စာမျက်နှာများ" else "MAIN MENU"
            )

            DrawerItem(
                icon = Icons.Default.Home,
                label = Strings.homeTab(currentLanguage),
                testTag = "drawer_item_home",
                onClick = {
                    onCloseDrawer()
                    onNavigateHome()
                }
            )

            DrawerItem(
                icon = Icons.Default.ShoppingCart,
                label = Strings.cart(currentLanguage),
                badgeText = if (cartItemCount > 0) "$cartItemCount" else null,
                testTag = "drawer_item_cart",
                onClick = {
                    onCloseDrawer()
                    onOpenCart()
                }
            )

            DrawerItem(
                icon = Icons.Default.LocationOn,
                label = Strings.trackingTab(currentLanguage),
                badgeText = if (activeOrderCount > 0) "$activeOrderCount Active" else null,
                testTag = "drawer_item_tracking",
                onClick = {
                    onCloseDrawer()
                    onNavigateTracking()
                }
            )

            DrawerItem(
                icon = Icons.Default.History,
                label = Strings.historyTab(currentLanguage),
                testTag = "drawer_item_history",
                onClick = {
                    onCloseDrawer()
                    onNavigateHistory()
                }
            )

            DrawerItem(
                icon = Icons.Default.AdminPanelSettings,
                label = Strings.adminPortal(currentLanguage),
                badgeText = if (isAdmin) "Admin" else null,
                testTag = "drawer_item_admin_console",
                onClick = {
                    onCloseDrawer()
                    onNavigateAdmin()
                }
            )

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )

            // Account & Preferences Section
            DrawerNavSectionTitle(
                title = if (currentLanguage == Language.BURMESE) "အကောင့်နှင့် ဆက်တင်များ" else "ACCOUNT & PREFERENCES"
            )

            if (isGuest) {
                DrawerItem(
                    icon = Icons.AutoMirrored.Filled.Login,
                    label = Strings.loginOrSignUp(currentLanguage),
                    textColor = MaterialTheme.colorScheme.primary,
                    iconTint = MaterialTheme.colorScheme.primary,
                    testTag = "drawer_item_login_signup",
                    onClick = {
                        onCloseDrawer()
                        onOpenAuth()
                    }
                )

                DrawerItem(
                    icon = Icons.Default.Settings,
                    label = Strings.settingsTitle(currentLanguage),
                    testTag = "drawer_item_settings",
                    onClick = {
                        onCloseDrawer()
                        onOpenSettings()
                    }
                )
            } else {
                DrawerItem(
                    icon = Icons.Default.Person,
                    label = Strings.profileTab(currentLanguage),
                    testTag = "drawer_item_profile",
                    onClick = {
                        onCloseDrawer()
                        onNavigateProfile()
                    }
                )

                DrawerItem(
                    icon = Icons.Default.SwapHoriz,
                    label = Strings.switchAccount(currentLanguage),
                    testTag = "drawer_item_switch_account",
                    onClick = {
                        onCloseDrawer()
                        onSwitchAccount()
                    }
                )

                DrawerItem(
                    icon = Icons.Default.Settings,
                    label = Strings.settingsTitle(currentLanguage),
                    testTag = "drawer_item_settings",
                    onClick = {
                        onCloseDrawer()
                        onOpenSettings()
                    }
                )

                DrawerItem(
                    icon = Icons.AutoMirrored.Filled.Logout,
                    label = Strings.logout(currentLanguage),
                    textColor = MaterialTheme.colorScheme.error,
                    iconTint = MaterialTheme.colorScheme.error,
                    testTag = "drawer_item_logout",
                    onClick = {
                        showLogoutConfirm = true
                    }
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )

            // Help & Support Section
            DrawerNavSectionTitle(
                title = if (currentLanguage == Language.BURMESE) "အကူအညီနှင့် ဝန်ဆောင်မှု" else "SUPPORT"
            )

            DrawerItem(
                icon = Icons.Default.Phone,
                label = "${Strings.customerSupport(currentLanguage)} (${if (supportPhone.isNotBlank()) supportPhone else Strings.hotlinePhone()})",
                testTag = "drawer_item_support",
                onClick = {
                    onCloseDrawer()
                    if (onContactSupport != null) {
                        onContactSupport()
                    } else {
                        onOpenSettings()
                    }
                }
            )

            Spacer(modifier = Modifier.weight(1f, fill = false))
            Spacer(modifier = Modifier.height(24.dp))

            // Footer info
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                val displayTitle = brandName.ifBlank { Strings.appTitle(currentLanguage) }
                Text(
                    text = "$displayTitle v2.4.0",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                val hotlineText = if (supportPhone.isNotBlank()) " • Hotline: $supportPhone" else ""
                Text(
                    text = "${Strings.appSubtitle(currentLanguage)}$hotlineText",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
    }

    // Logout Confirmation Dialog
    if (showLogoutConfirm) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirm = false },
            icon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    text = Strings.logoutConfirmTitle(currentLanguage),
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = Strings.logoutConfirmDesc(currentLanguage),
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutConfirm = false
                        onCloseDrawer()
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.testTag("dialog_confirm_logout")
                ) {
                    Text(text = Strings.logout(currentLanguage), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showLogoutConfirm = false }
                ) {
                    Text(text = Strings.cancel(currentLanguage))
                }
            }
        )
    }
}

@Composable
private fun DrawerNavSectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.8.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
    )
}

@Composable
private fun DrawerItem(
    icon: ImageVector,
    label: String,
    testTag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    badgeText: String? = null,
    textColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface,
    iconTint: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.primary
) {
    Surface(
        color = androidx.compose.ui.graphics.Color.Transparent,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 2.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(22.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = textColor,
                modifier = Modifier.weight(1f)
            )

            if (badgeText != null) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 6.dp)
                ) {
                    Text(
                        text = badgeText,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outlineVariant,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
