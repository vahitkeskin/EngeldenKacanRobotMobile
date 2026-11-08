package com.vahitkeskin.engeldenkacanrobotmobile

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import com.vahitkeskin.engeldenkacanrobotmobile.presentation.ui.RobotControllerScreen
import com.vahitkeskin.engeldenkacanrobotmobile.presentation.ui.SplashScreen
import com.vahitkeskin.engeldenkacanrobotmobile.theme.AppColors
import com.vahitkeskin.engeldenkacanrobotmobile.theme.ThemeMode
import kotlinx.coroutines.launch
import androidx.compose.ui.tooling.preview.Preview

/**
 * 📱 App - Root Multiplatform Compose Container
 * Tüm platformlarda (Android, iOS, Desktop) tam ekran kenardan kenara (edge-to-edge) çalışan,
 * ekranlar arası yatay kaydırma (HorizontalPager swipe) ve senkronize 3 sekmeli
 * Bottom Navigation View (BNV) sunan ana kapsayıcı.
 *
 * Tab 0: 📊 Telemetri (DashboardScreen)      - Radar, HC-SR04, Batarya Yönetimi ve Anasayfa Hızlı Müdahale
 * Tab 1: 🎮 Kumanda (RobotControllerScreen)  - Tam Sanal Kokpit, D-Pad Direksiyon, Servo Kadranı, Vitesler
 * Tab 2: 🤖 Robot 3D (Robot3DScreen)        - Tam ekranı kaplayan, iki parmakla zoom/çevirme destekli 3D Dijital İkiz
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

    // EKRANLAR ARASI KAYDIRMA (HorizontalPager Swipe) - 3 SEKMELİ YAPI
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 3 })
    val coroutineScope = rememberCoroutineScope()
    // 3D Etkileşim Kilidi: 3D aktifken HorizontalPager kaydırma KESİNLİKLE engellenir
    var is3dActive by remember { mutableStateOf(false) }

    // Başlangıç Teşhis ve Donanım Yükleme Ekranı (Splash Screen)
    var showSplash by remember { mutableStateOf(true) }

    MaterialTheme {
        AnimatedContent(
            targetState = showSplash,
            transitionSpec = {
                fadeIn(animationSpec = tween(400)) togetherWith fadeOut(animationSpec = tween(400))
            },
            label = "SplashTransition"
        ) { isSplash ->
            if (isSplash) {
                SplashScreen(
                    onFinished = { showSplash = false },
                    themeMode = themeMode,
                    languageMode = languageMode,
                    onThemeSelected = { themeMode = it },
                    onLanguageSelected = { languageMode = it }
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(colors.bgPrimary)
                ) {
                    // 1. KAYDIRILABİLİR EKRAN SAYFALARI (Swipe / Gestures)
                    // 3D aktifken ekran kaydırma kesinlikle bloke edilir; sadece butonla geri dönülebilir!
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize(),
                        userScrollEnabled = if (pagerState.currentPage == 2) !is3dActive else true
                    ) { page ->
                        when (page) {
                            0 -> DashboardScreen(
                                repository = repository,
                                themeMode = themeMode,
                                languageMode = languageMode,
                                onThemeSelected = { themeMode = it },
                                onLanguageSelected = { languageMode = it }
                            )
                            1 -> RobotControllerScreen(
                                repository = repository,
                                themeMode = themeMode,
                                languageMode = languageMode,
                                onNavigateTo3D = {
                                    coroutineScope.launch { pagerState.animateScrollToPage(2) }
                                }
                            )
                            2 -> Robot3DScreen(
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
                            .padding(horizontal = 16.dp, vertical = 10.dp)
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
        horizontalArrangement = Arrangement.spacedBy(4.dp),
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

        // TAB 2: KUMANDA (KOKPİT)
        BottomNavItem(
            icon = "🎮",
            label = Strings.navController,
            isSelected = selectedTab == 1,
            colors = colors,
            modifier = Modifier.weight(1f),
            onClick = { onTabSelected(1) }
        )

        // TAB 3: ROBOT 3D DİJİTAL İKİZ
        BottomNavItem(
            icon = "🤖",
            label = Strings.navRobotTwin,
            isSelected = selectedTab == 2,
            colors = colors,
            modifier = Modifier.weight(1f),
            onClick = { onTabSelected(2) }
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
                fontSize = 14.sp,
                modifier = Modifier.padding(end = 4.dp)
            )
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                color = if (isSelected) Color.White else colors.textMuted
            )
        }
    }
}

// ==========================================
// COMPOSE PREVIEW
// ==========================================
@Preview
@Composable
private fun AppPreview() {
    App()
}

