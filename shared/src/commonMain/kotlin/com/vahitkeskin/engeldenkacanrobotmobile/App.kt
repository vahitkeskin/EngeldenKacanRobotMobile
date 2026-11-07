package com.vahitkeskin.engeldenkacanrobotmobile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vahitkeskin.engeldenkacanrobotmobile.data.MockTelemetryRepository
import com.vahitkeskin.engeldenkacanrobotmobile.i18n.LanguageMode
import com.vahitkeskin.engeldenkacanrobotmobile.i18n.Strings
import com.vahitkeskin.engeldenkacanrobotmobile.presentation.ui.DashboardScreen
import com.vahitkeskin.engeldenkacanrobotmobile.presentation.ui.Robot3DScreen
import com.vahitkeskin.engeldenkacanrobotmobile.theme.AppColors
import com.vahitkeskin.engeldenkacanrobotmobile.theme.ThemeMode
import kotlinx.coroutines.launch

/**
 * 📱 App - Root Multiplatform Compose Container
 * Tüm platformlarda (Android, iOS, Desktop) tam ekran kenardan kenara (edge-to-edge) çalışan,
 * ekranlar arası yatay kaydırma (HorizontalPager swipe) ve senkronize 2 sekmeli
 * Bottom Navigation View (BNV) sunan ana kapsayıcı.
 *
 * Tab 0: 📊 Telemetri (DashboardScreen) - Radar, Servo Gauge, HC-SR04 Metrikler ve Günlük
 * Tab 1: 🤖 Robot 3D (Robot3DScreen)   - Tam ekranı kaplayan, iki parmakla zoom/çevirme/gezinme destekli 3D Dijital İkiz
 */
@Composable
fun App() {
    val repository = remember { MockTelemetryRepository() }
    var themeMode by remember { mutableStateOf(ThemeMode.SYSTEM) }
    var languageMode by remember { mutableStateOf(LanguageMode.SYSTEM) }

    val systemInDark = isSystemInDarkTheme()
    val isDark = when (themeMode) {
        ThemeMode.SYSTEM -> systemInDark
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
    }
    val colors = if (isDark) AppColors.Dark else AppColors.Light

    // Status bar & Nav bar ikon renklerini anlık senkronize et
    SetSystemBarsTheme(isDark = isDark)
    Strings.currentLanguage = languageMode

    // EKRANLAR ARASI KAYDIRMA (HorizontalPager Swipe) - Sayfa 0: Telemetri, Sayfa 1: Robot 3D
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 2 })
    val coroutineScope = rememberCoroutineScope()
    // 3D Etkileşim Kilidi: 3D aktifken HorizontalPager kaydırma KESİNLİKLE engellenir
    var is3dActive by remember { mutableStateOf(false) }

    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.bgPrimary)
        ) {
            // 1. KAYDIRILABİLİR EKRAN SAYFALARI (Swipe / Gestures)
            // 3D aktifken ekran kaydırma kesinlikle bloke edilir; sadece Telemetri butonuyla geri dönülebilir!
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                userScrollEnabled = if (pagerState.currentPage == 1) !is3dActive else true
            ) { page ->
                when (page) {
                    0 -> DashboardScreen(
                        repository = repository,
                        themeMode = themeMode,
                        languageMode = languageMode,
                        onThemeSelected = { themeMode = it },
                        onLanguageSelected = { languageMode = it }
                    )
                    1 -> Robot3DScreen(
                        repository = repository,
                        is3dActive = is3dActive,
                        onToggle3dActive = { is3dActive = !is3dActive },
                        onNavigateToTelemetry = {
                            coroutineScope.launch { pagerState.animateScrollToPage(0) }
                        },
                        themeMode = themeMode,
                        languageMode = languageMode,
                        onThemeSelected = { themeMode = it },
                        onLanguageSelected = { languageMode = it }
                    )
                }
            }

            // 2. BOTTOM NAVIGATION VIEW (BNV)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .navigationBarsPadding() // Sistemin alt gezinme çubuğunun kesinlikle üstünde durur
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                BottomNavigationBar(
                    selectedTab = pagerState.currentPage,
                    onTabSelected = { tabIndex ->
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(tabIndex)
                        }
                    },
                    colors = colors
                )
            }
        }
    }
}

@Composable
private fun BottomNavigationBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    colors: AppColors
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(26.dp))
            .background(colors.bgSecondary.copy(alpha = 0.96f))
            .border(1.2.dp, colors.border, RoundedCornerShape(26.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // TAB 1: TELEMETRİ
        BottomNavItem(
            icon = "📊",
            label = Strings.navTelemetry,
            isSelected = selectedTab == 0,
            colors = colors,
            modifier = Modifier.weight(1f),
            onClick = { onTabSelected(0) }
        )

        // TAB 2: ROBOT 3D DİJİTAL İKİZ
        BottomNavItem(
            icon = "🤖",
            label = Strings.navRobotTwin,
            isSelected = selectedTab == 1,
            colors = colors,
            modifier = Modifier.weight(1f),
            onClick = { onTabSelected(1) }
        )
    }
}

@Composable
private fun BottomNavItem(
    icon: String,
    label: String,
    isSelected: Boolean,
    colors: AppColors,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(22.dp))
            .background(if (isSelected) Color(0xFF2563EB) else Color.Transparent)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = icon,
                fontSize = 15.sp,
                modifier = Modifier.padding(end = 6.dp)
            )
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                color = if (isSelected) Color.White else colors.textMuted
            )
        }
    }
}
