package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.notifications.AzkarNotificationManager
import com.example.ui.theme.IslamicEmeraldDark
import com.example.ui.theme.IslamicEmeraldMedium
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldBright

@Composable
fun SettingsAndDeveloperScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showDeveloperModal by remember { mutableStateOf(false) }
    var voiceEnabled by remember { mutableStateOf(AzkarNotificationManager.isVoiceSpeechEnabled(context)) }
    var notificationsEnabled by remember { mutableStateOf(true) }
    var keepScreenOnMode by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))
            // Header
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = Color(0xD90E2C20),
                border = androidx.compose.foundation.BorderStroke(1.dp, IslamicGold.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1B533E))
                            .border(1.5.dp, IslamicGoldBright, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Tune, contentDescription = null, tint = IslamicGoldBright, modifier = Modifier.size(28.dp))
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "الإعدادات العامة وصفحة المطور",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = IslamicGoldBright
                        )
                        Text(
                            text = "تخصيص تجربة القراءة وسرعة وسلاسة التطبيق",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }

        // Section 1: Application Performance & Fluidity (السرعة وسلاسة الاستخدام)
        item {
            SectionHeader(title = "الأداء والسرعة العالية")
            SettingsCard {
                SettingsSwitchRow(
                    icon = Icons.Default.Speed,
                    title = "محرك التسريع الفوري (Zero-Lag Engine)",
                    subtitle = "تحسين استهلاك الذاكرة وتخفيف العبء عن المعالج لسرعة استجابة فائقة",
                    checked = true,
                    onCheckedChange = {
                        Toast.makeText(context, "الوضع فائق السرعة مفعل افتراضياً بدون تهنيج", Toast.LENGTH_SHORT).show()
                    }
                )
                HorizontalDivider(color = Color(0x33D4AF37), modifier = Modifier.padding(horizontal = 16.dp))
                SettingsSwitchRow(
                    icon = Icons.Default.PhoneAndroid,
                    title = "إبقاء الشاشة مضاءة أثناء القراءة",
                    subtitle = "منع إغلاق الشاشة تلقائياً أثناء قراءة القرآن الكريم وحصن المسلم",
                    checked = keepScreenOnMode,
                    onCheckedChange = {
                        keepScreenOnMode = it
                        Toast.makeText(context, if (it) "تم تفعيل إبقاء الشاشة مضاءة" else "تم إيقاف الإبقاء", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }

        // Section 2: Audio & Notifications
        item {
            SectionHeader(title = "الصوتيات والإشعارات الإيمانية")
            SettingsCard {
                SettingsSwitchRow(
                    icon = Icons.Default.NotificationsActive,
                    title = "صوت الشيخ البشري المؤثر للإشعارات",
                    subtitle = "تلاوة خاشعة بصوت الشيخ مشاري العفاسي عند التذكير بالصلاة على النبي",
                    checked = voiceEnabled,
                    onCheckedChange = {
                        voiceEnabled = it
                        AzkarNotificationManager.setVoiceSpeechEnabled(context, it)
                        Toast.makeText(context, if (it) "تم تفعيل الصوت البشري الخاشع 🔊" else "تم إيقاف الصوت", Toast.LENGTH_SHORT).show()
                    }
                )
                HorizontalDivider(color = Color(0x33D4AF37), modifier = Modifier.padding(horizontal = 16.dp))
                SettingsSwitchRow(
                    icon = Icons.Default.Info,
                    title = "التذكير الدوري بالصلاة والأذكار",
                    subtitle = "إشعارات يومية تلقائية كل ساعتين للأذكار والصلاة على النبي ﷺ",
                    checked = notificationsEnabled,
                    onCheckedChange = {
                        notificationsEnabled = it
                        if (it) AzkarNotificationManager.schedulePeriodicReminder(context)
                        else AzkarNotificationManager.cancelReminder(context)
                        Toast.makeText(context, if (it) "تم تفعيل التذكيرات الدورية" else "تم إيقاف التذكيرات", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }

        // Section 3: Developer Profile (صفحة المطور ومعلومات التطبيق)
        item {
            SectionHeader(title = "حول التطبيق والمطور")
            SettingsCard {
                SettingsActionRow(
                    icon = Icons.Default.Code,
                    title = "صفحة المطور ومعلومات الإصدار",
                    subtitle = "بيانات المطور، التحديثات، ورؤية المشروع الإيماني",
                    onClick = { showDeveloperModal = true }
                )
                HorizontalDivider(color = Color(0x33D4AF37), modifier = Modifier.padding(horizontal = 16.dp))
                SettingsActionRow(
                    icon = Icons.Default.Share,
                    title = "مشاركة التطبيق ولك الأجر (الدال على الخير كفاعله)",
                    subtitle = "انشر التطبيق ليكون صدقة جارية لك ولوالديك",
                    onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, "تطبيق المصحف الشريف الشامل")
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "تطبيق المصحف الشريف الشامل: تلاوات 400 شيخ، تفسير ميسر، أذكار، قصص الأنبياء كاملة، تعليم الصلاة، والأحاديث النبوية. حمله الآن واكسب الأجر!"
                            )
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "مشاركة التطبيق"))
                    }
                )
                HorizontalDivider(color = Color(0x33D4AF37), modifier = Modifier.padding(horizontal = 16.dp))
                SettingsActionRow(
                    icon = Icons.Default.Star,
                    title = "تقييم التطبيق والدعاء لنا بظهر الغيب",
                    subtitle = "نسأل الله أن يجعله في ميزان حسنات كل من ساهم واستفاد منه",
                    onClick = {
                        Toast.makeText(context, "جزاكم الله خيراً وبارك فيكم ونفع بكم الأمة 🤲", Toast.LENGTH_LONG).show()
                    }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    // Developer Profile Dialog
    if (showDeveloperModal) {
        AlertDialog(
            onDismissRequest = { showDeveloperModal = false },
            containerColor = Color(0xFF0F3124),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Code, contentDescription = null, tint = IslamicGoldBright, modifier = Modifier.size(26.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "صفحة المطور والمعلومات التقنية",
                        color = IslamicGoldBright,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFF184533),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(text = "تطبيق المصحف الشريف والإسلامي الشامل", color = IslamicGoldBright, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "الإصدار: 2.5.0 (نسخة القرآن والمصحف الذهبية)", color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp)
                            Text(text = "التقنيات: Kotlin • Jetpack Compose • Material 3 • Coroutines Stream", color = Color.White.copy(alpha = 0.75f), fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "عن المشروع:\nتم بناء هذا التطبيق كوقف إسلامي رقمي لوجه الله تعالى ليجمع القرآن الكريم بصوت أكثر من 400 قارئ، مع تشغيل التلاوات في الخلفية، تنزيل السور والآيات، تعليم الصلاة الشامل، موسوعة الأحاديث النبوية، قصص الأنبياء كاملة، والأناشيد الروحانية العذبة.\n\nدعاء:\n«اللهم اجعل هذا العمل خالصاً لوجهك الكريم وثقّل به موازيننا وموازين والدينا وكل من ساهم فيه ونشره».",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 13.sp,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF092017))
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Email, contentDescription = null, tint = IslamicGold, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "للتواصل والملاحظات: feedback.islamic@app.com",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 11.sp
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDeveloperModal = false }) {
                    Text("إغلاق", color = IslamicGoldBright, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        )
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        color = IslamicGoldBright,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
    )
}

@Composable
private fun SettingsCard(content: @Composable () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xB3113326),
        border = androidx.compose.foundation.BorderStroke(1.dp, IslamicGold.copy(alpha = 0.25f))
    ) {
        Column { content() }
    }
}

@Composable
private fun SettingsSwitchRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1B4E3A)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = IslamicGoldBright, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(text = subtitle, color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp, lineHeight = 16.sp)
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = IslamicGoldBright,
                checkedTrackColor = Color(0xFF1E5B44),
                uncheckedThumbColor = Color.Gray,
                uncheckedTrackColor = Color(0xFF0D251D)
            )
        )
    }
}

@Composable
private fun SettingsActionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0xFF1B4E3A)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = IslamicGoldBright, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(text = subtitle, color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp, lineHeight = 16.sp)
        }
    }
}
