package com.example.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.example.data.PrayerTimesCalculator
import com.example.model.DayPrayerTimes
import com.example.model.PrayerTimeItem
import com.example.notifications.PrayerAlertManager
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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.CityLocationsProvider
import com.example.model.CityLocation
import com.example.sensors.CompassSensorManager
import com.example.ui.theme.IslamicEmeraldDark
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldBright
import com.google.android.gms.location.LocationServices
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun QiblaScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val compassSensorManager = remember { CompassSensorManager(context) }
    val compassData by compassSensorManager.compassData.collectAsState()

    var selectedCity by remember { mutableStateOf(CityLocationsProvider.cities[4]) } // Default to Cairo
    var isGpsActive by remember { mutableStateOf(false) }
    var showCityDialog by remember { mutableStateOf(false) }

    val vibrator = remember { context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator }

    // Start/Stop sensors with lifecycle
    DisposableEffect(Unit) {
        compassSensorManager.startListening()
        onDispose {
            compassSensorManager.stopListening()
        }
    }

    // Recalculate Qibla angle and Prayer Times when city changes
    LaunchedEffect(selectedCity) {
        val qiblaAngle = CityLocationsProvider.calculateQiblaAngle(selectedCity.latitude, selectedCity.longitude)
        compassSensorManager.setQiblaAngle(qiblaAngle.toFloat())
    }

    val prayerTimes: DayPrayerTimes = remember(selectedCity) {
        PrayerTimesCalculator.calculatePrayerTimes(
            latitude = selectedCity.latitude,
            longitude = selectedCity.longitude,
            cityName = selectedCity.nameArabic
        )
    }
    val nextPrayer = prayerTimes.nextPrayer

    // Haptic vibration pulse when aligned with Kaaba
    LaunchedEffect(compassData.isFacingQibla) {
        if (compassData.isFacingQibla) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(100)
                }
            } catch (_: Exception) {}
        }
    }

    // GPS Permission Launcher
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (fineGranted || coarseGranted) {
            fetchGpsLocation(context) { loc ->
                isGpsActive = true
                selectedCity = CityLocation("موقعي الدقيق (GPS)", "إحداثيات حية", loc.latitude, loc.longitude)
                Toast.makeText(context, "تم ضبط القبلة ومواقيت الصلاة بدقة على إحداثياتك الحالية!", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "لم يتم منح إذن الموقع. يمكنك اختيار مدينتك من القائمة.", Toast.LENGTH_LONG).show()
        }
    }

    val scrollState = rememberScrollState()

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Title
            Text(
                text = "اتجاه القبلة للصلاة",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = IslamicGoldBright
            )
            Text(
                text = "بوصلة إلكترونية دقيقة مع ضبط المكان",
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.75f)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Location card with quick switch and GPS button
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                color = Color(0x990C261D),
                border = androidx.compose.foundation.BorderStroke(1.dp, IslamicGold.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { showCityDialog = true },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = IslamicGoldBright,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = selectedCity.nameArabic,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "${selectedCity.countryArabic} (اضغط للتغيير)",
                                color = IslamicGoldBright.copy(alpha = 0.8f),
                                fontSize = 11.sp
                            )
                        }
                    }

                    // GPS Button
                    Button(
                        onClick = {
                            val fineCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                            if (fineCheck == PackageManager.PERMISSION_GRANTED) {
                                fetchGpsLocation(context) { loc ->
                                    isGpsActive = true
                                    selectedCity = CityLocation("موقعي الدقيق (GPS)", "إحداثيات حية", loc.latitude, loc.longitude)
                                    Toast.makeText(context, "تم ضبط القبلة على موقعك الحالي بالضبط!", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                locationPermissionLauncher.launch(
                                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B4E3C)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Icon(Icons.Default.GpsFixed, contentDescription = "GPS", tint = IslamicGoldBright, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("GPS دقيق", fontSize = 11.sp, color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Alignment Status Banner
            val isAligned = compassData.isFacingQibla
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = if (isAligned) Color(0xD9104A36) else Color(0x66081F18),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isAligned) Color(0xFF66BB6A) else IslamicGold.copy(alpha = 0.3f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (isAligned) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF81C784), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "أنت باتجاه الكعبة المشرفة والقبلة الآن 🕋",
                            color = Color(0xFFA5D6A7),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    } else {
                        val diff = ((compassData.qiblaBearing - compassData.currentHeading + 540) % 360 - 180).toInt()
                        val turnText = if (diff > 0) "أدر الهاتف يميناً بمقدار $diff°" else "أدر الهاتف يساراً بمقدار ${abs(diff)}°"
                        Text(
                            text = turnText,
                            color = IslamicGoldBright,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Dynamic Qibla Compass Visual Dial
            Box(
                modifier = Modifier
                    .size(280.dp),
                contentAlignment = Alignment.Center
            ) {
                val animatedHeading by animateFloatAsState(targetValue = -compassData.currentHeading, label = "headingAnim")
                val animatedNeedle by animateFloatAsState(targetValue = compassData.needleAngle, label = "needleAnim")

                // Compass Rose / Outer Dial (rotates with heading)
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .rotate(animatedHeading)
                ) {
                    val center = Offset(size.width / 2, size.height / 2)
                    val radius = size.width / 2 - 12.dp.toPx()

                    // Outer Circle Ring
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFF0F3628), Color(0xFF061A12))
                        ),
                        radius = radius,
                        center = center
                    )

                    drawCircle(
                        color = Color(0xFFD4AF37),
                        radius = radius,
                        center = center,
                        style = Stroke(width = 3.dp.toPx())
                    )

                    // Inner decorative circle
                    drawCircle(
                        color = Color(0x40D4AF37),
                        radius = radius * 0.75f,
                        center = center,
                        style = Stroke(width = 1.dp.toPx())
                    )

                    // 4 Cardinal points markers (N, S, E, W)
                    for (i in 0 until 360 step 30) {
                        val angleRad = Math.toRadians(i.toDouble())
                        val tickLen = if (i % 90 == 0) 14.dp.toPx() else 8.dp.toPx()
                        val color = if (i == 0) Color(0xFFFF5252) else Color(0xFFD4AF37)
                        val stroke = if (i % 90 == 0) 3.dp.toPx() else 1.5.dp.toPx()

                        val startX = center.x + (radius - tickLen) * sin(angleRad).toFloat()
                        val startY = center.y - (radius - tickLen) * cos(angleRad).toFloat()
                        val endX = center.x + radius * sin(angleRad).toFloat()
                        val endY = center.y - radius * cos(angleRad).toFloat()

                        drawLine(
                            color = color,
                            start = Offset(startX, startY),
                            end = Offset(endX, endY),
                            strokeWidth = stroke
                        )
                    }
                }

                // Kaaba Qibla Pointer Needle
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .rotate(animatedNeedle),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(top = 18.dp)
                    ) {
                        // Kaaba Icon & Needle Tip
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color.Black)
                                .border(2.dp, Color(0xFFFFD700), RoundedCornerShape(6.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🕋", fontSize = 20.sp)
                        }

                        // Needle line downwards towards center
                        Box(
                            modifier = Modifier
                                .width(4.dp)
                                .height(85.dp)
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color(0xFFFFD700),
                                            Color(0x80D4AF37),
                                            Color.Transparent
                                        )
                                    )
                                )
                        )
                    }
                }

                // Central Pivot Jewel
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF091C15))
                        .border(2.dp, IslamicGoldBright, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(IslamicGoldBright)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Angle details row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0x800C251C))
                    .border(1.dp, IslamicGold.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .padding(vertical = 8.dp, horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("اتجاه القبلة", fontSize = 11.sp, color = Color.White.copy(alpha = 0.7f))
                    Text("${compassData.qiblaBearing.toInt()}°", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = IslamicGoldBright)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("اتجاه هاتفك الحالي", fontSize = 11.sp, color = Color.White.copy(alpha = 0.7f))
                    Text("${compassData.currentHeading.toInt()}°", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Prayer Times Section (مواقيت الصلاة الدقيقة وتنبيهات الأذان)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xB30A241B),
                border = androidx.compose.foundation.BorderStroke(1.dp, IslamicGold.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Mosque, contentDescription = null, tint = IslamicGoldBright, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "مواقيت الصلاة الدقيقة لـ ${selectedCity.nameArabic}",
                                    fontWeight = FontWeight.Bold,
                                    color = IslamicGoldBright,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "حساب دقيق بحسب الموقع الجغرافي",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 10.sp
                                )
                            }
                        }

                        // Next prayer pill
                        nextPrayer?.let { next ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF1B533E))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "القادمة: ${next.type.nameArabic} (${next.formattedTime})",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Prayer grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        prayerTimes.times.forEach { prayer ->
                            val isNext = prayer.isNext
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isNext) Color(0xFF1E5B44) else Color(0x400C251C))
                                    .padding(vertical = 6.dp, horizontal = 5.dp)
                            ) {
                                Text(
                                    text = prayer.type.nameArabic,
                                    color = if (isNext) IslamicGoldBright else Color.White.copy(alpha = 0.8f),
                                    fontSize = 11.sp,
                                    fontWeight = if (isNext) FontWeight.Bold else FontWeight.Normal
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = prayer.formattedTime,
                                    color = if (isNext) Color.White else IslamicGoldBright.copy(alpha = 0.9f),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Voice Alert Test Button
                    Button(
                        onClick = {
                            val activeNext = nextPrayer ?: prayerTimes.times.first()
                            PrayerAlertManager.testVoiceAnnouncement(context, activeNext.type.nameArabic)
                            Toast.makeText(context, "حان الآن موعد صلاة ${activeNext.type.nameArabic} بصوت واضح", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B4E3C)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().height(36.dp)
                    ) {
                        Icon(Icons.Default.VolumeUp, contentDescription = null, tint = IslamicGoldBright, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "تجربة صوت التنبيه: حان موعد وقت الصلاة 🔊",
                            fontSize = 11.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "نصيحة لمعايرة الحساسات: حرّك الهاتف في الهواء على شكل رقم 8 بالإنجليزية للحصول على أعلى دقة",
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // City Selector Dialog
        if (showCityDialog) {
            CitySelectorDialog(
                currentCity = selectedCity,
                onDismiss = { showCityDialog = false },
                onSelectCity = { city ->
                    selectedCity = city
                    isGpsActive = false
                    showCityDialog = false
                    Toast.makeText(context, "تم تحديد: ${city.nameArabic}", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}

@Composable
private fun CitySelectorDialog(
    currentCity: CityLocation,
    onDismiss: () -> Unit,
    onSelectCity: (CityLocation) -> Unit
) {
    var query by remember { mutableStateOf("") }
    val filtered = remember(query) {
        if (query.isBlank()) CityLocationsProvider.cities
        else CityLocationsProvider.cities.filter {
            it.nameArabic.contains(query.trim()) || it.countryArabic.contains(query.trim())
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء", color = IslamicGoldBright)
            }
        },
        title = {
            Text(
                text = "اختر مدينتك لضبط القبلة بدقة",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = IslamicGoldBright
            )
        },
        text = {
            Column(modifier = Modifier.height(380.dp)) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text("ابحث عن مدينة أو دولة...", fontSize = 12.sp, color = Color.White.copy(alpha = 0.5f)) },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0x400F2B21),
                        unfocusedContainerColor = Color(0x200F2B21),
                        focusedIndicatorColor = IslamicGold,
                        unfocusedIndicatorColor = IslamicGold.copy(alpha = 0.3f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(filtered) { city ->
                        val isSelected = city.nameArabic == currentCity.nameArabic
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                                .clickable { onSelectCity(city) },
                            color = if (isSelected) Color(0x991B533E) else Color(0x400C251C),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) IslamicGold else Color.White.copy(alpha = 0.1f)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = city.nameArabic,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) IslamicGoldBright else Color.White,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = city.countryArabic,
                                        fontSize = 11.sp,
                                        color = Color.White.copy(alpha = 0.65f)
                                    )
                                }

                                if (isSelected) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = IslamicGoldBright, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }
        },
        containerColor = Color(0xF2091C15)
    )
}

private fun fetchGpsLocation(context: Context, onLocationFound: (Location) -> Unit) {
    try {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                onLocationFound(location)
            } else {
                Toast.makeText(context, "جاري تحديد الموقع بواسطة الأقمار الصناعية...", Toast.LENGTH_SHORT).show()
            }
        }
    } catch (_: SecurityException) {}
}
