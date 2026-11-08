package com.vahitkeskin.engeldenkacanrobotmobile.presentation.ui
import com.vahitkeskin.engeldenkacanrobotmobile.theme.EKRobotColor

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vahitkeskin.engeldenkacanrobotmobile.i18n.LanguageMode
import com.vahitkeskin.engeldenkacanrobotmobile.i18n.Strings
import com.vahitkeskin.engeldenkacanrobotmobile.theme.ThemeMode
import engeldenkacanrobotmobile.shared.generated.resources.Res
import engeldenkacanrobotmobile.shared.generated.resources.app_icon
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import androidx.compose.ui.tooling.preview.Preview

/**
 * 🚀 SplashScreen
 * Uygulamanın seçilen sevimli app icon'ının (Option 5) birebir aynısını
 * zarif animasyonlarla (sonar dalgaları, hafif süzülme ve nefes alma efekti)
 * merkezde sunan, sade, kibar ve modern açılış ekranı.
 *
 * Ekranda hiçbir yazı kalabalığı bulunmaz; sadece en altta UX/UI ile %100 uyumlu,
 * kibar bir "vahitkeskin tarafından geliştirildi" imzası yer alır.
 */
@Composable
fun SplashScreen(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier,
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    languageMode: LanguageMode = LanguageMode.SYSTEM,
    onThemeSelected: (ThemeMode) -> Unit = {},
    onLanguageSelected: (LanguageMode) -> Unit = {}
) {
    val systemInDark = isSystemInDarkTheme()
    val isDark = when (themeMode) {
        ThemeMode.SYSTEM -> systemInDark
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
    }

    // 2.2 saniye sonra otomatik ana ekrana geçiş (veya ekrana dokununca anında geçiş)
    LaunchedEffect(Unit) {
        delay(2200L)
        onFinished()
    }

    // 1. ZARİF VE KİBAR ANİMASYON MOTORU
    val infiniteTransition = rememberInfiniteTransition(label = "SplashAnimation")

    // Hafif dikey süzülme (Floating levitation: -5dp .. +5dp)
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "FloatOffset"
    )

    // Kibar nefes alma ölçekleme efekti (Scale: 0.97f .. 1.03f)
    val breathingScale by infiniteTransition.animateFloat(
        initialValue = 0.97f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "BreathingScale"
    )

    // Sevimli robotun kafasını hafifçe yana eğme mikro hareketi (-2.5° .. +2.5°)
    val subtleTilt by infiniteTransition.animateFloat(
        initialValue = -2.2f,
        targetValue = 2.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(2600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "SubtleTilt"
    )

    // Dairesel sonar radar dalgası yayılma fazı (0f .. 1f)
    val sonarPulse by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "SonarPulse"
    )

    // Arka plan rengi (Koyu modda tam ikon rengi #060914, açık modda temiz pürüzsüz #F8FAFC)
    val bgGradient = if (isDark) {
        Brush.verticalGradient(
            listOf(
                EKRobotColor.Alpha.NightDark,
                EKRobotColor.Alpha.NightDeeper,
                EKRobotColor.Alpha.NightDark
            )
        )
    } else {
        Brush.verticalGradient(
            listOf(
                EKRobotColor.Theme.Dark.TextPrimary,
                EKRobotColor.Theme.Light.BgPrimary,
                EKRobotColor.Theme.Light.RadarBg
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(bgGradient)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                onFinished()
            }
    ) {
        // ==========================================
        // 1. MERKEZ ALAN: APP ICON & SONAR DALGALARI
        // ==========================================
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(360.dp),
            contentAlignment = Alignment.Center
        ) {
            // A. DIŞA DOĞRU YAYILAN ZARİF SONAR RADAR DALGALARI (Canvas)
            Canvas(modifier = Modifier.fillMaxSize()) {
                val cx = size.width / 2f
                val cy = size.height / 2f

                // 3 kademeli yumuşak sonar dalgası
                for (i in 0..2) {
                    val phase = (sonarPulse + i * 0.33f) % 1f
                    val waveRadius = 90.dp.toPx() + phase * 85.dp.toPx()
                    val waveAlpha = ((1f - phase) * 0.45f).coerceIn(0f, 1f)
                    val waveColor = if (isDark) EKRobotColor.Brand.CyanAccent else EKRobotColor.Brand.SkyCyan

                    drawCircle(
                        color = waveColor.copy(alpha = waveAlpha),
                        radius = waveRadius,
                        center = Offset(cx, cy),
                        style = Stroke(width = (1.6f - phase * 0.8f).dp.toPx())
                    )
                }

                // Arkada yumuşak bir cyan/mavi parıltı halesi (Ambient Glow)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            (if (isDark) EKRobotColor.Brand.CyanAccent else EKRobotColor.Brand.SkyLight).copy(alpha = 0.18f),
                            Color.Transparent
                        ),
                        center = Offset(cx, cy),
                        radius = 125.dp.toPx()
                    ),
                    radius = 125.dp.toPx(),
                    center = Offset(cx, cy)
                )
            }

            // B. APP ICON'IN BİREBİR AYNISI (Animasyonlu Süzülme + Nefes Alma + Hafif Açı)
            Box(
                modifier = Modifier
                    .offset(y = floatOffset.dp)
                    .scale(breathingScale)
                    .rotate(subtleTilt)
                    .size(200.dp)
                    .shadow(
                        elevation = if (isDark) 20.dp else 14.dp,
                        shape = RoundedCornerShape(44.dp),
                        ambientColor = if (isDark) EKRobotColor.Brand.CyanAccent.copy(alpha = 0.35f) else Color.Black.copy(alpha = 0.08f),
                        spotColor = if (isDark) EKRobotColor.Brand.BluePrimary.copy(alpha = 0.5f) else Color.Black.copy(alpha = 0.12f)
                    )
                    .clip(RoundedCornerShape(44.dp))
                    .background(EKRobotColor.Alpha.NightDark)
                    .border(
                        width = 1.2.dp,
                        brush = Brush.linearGradient(
                            listOf(
                                EKRobotColor.Brand.CyanAccent.copy(alpha = if (isDark) 0.6f else 0.4f),
                                EKRobotColor.Brand.BluePrimary.copy(alpha = if (isDark) 0.3f else 0.2f),
                                EKRobotColor.Brand.CyanAccent.copy(alpha = if (isDark) 0.5f else 0.3f)
                            )
                        ),
                        shape = RoundedCornerShape(44.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(Res.drawable.app_icon),
                    contentDescription = "EK Robot App Icon",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // ==========================================
        // 2. EN ALT ALAN: SADECE GELİŞTİRİCİ İMZASI
        // ==========================================
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 28.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        if (isDark) EKRobotColor.Theme.Dark.BgSecondary.copy(alpha = 0.5f)
                        else Color.White.copy(alpha = 0.85f)
                    )
                    .border(
                        width = 0.8.dp,
                        color = if (isDark) EKRobotColor.Theme.Dark.Border.copy(alpha = 0.6f) else EKRobotColor.Theme.Light.RadarBg,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(horizontal = 14.dp, vertical = 7.dp)
            ) {
                // Kibar, parıldayan mavi/cyan aksan noktası
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(if (isDark) EKRobotColor.Brand.SkyLight else EKRobotColor.Brand.SkyCyan)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = Strings.splashDevelopedBy,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isDark) EKRobotColor.Theme.Dark.TextMuted else EKRobotColor.Theme.Light.TextMuted,
                    letterSpacing = 0.4.sp
                )
            }
        }
    }
}

// ==========================================
// COMPOSE PREVIEW
// ==========================================
@Preview
@Composable
private fun SplashScreenDarkPreview() {
    SplashScreen(
        onFinished = {},
        themeMode = ThemeMode.DARK,
        languageMode = LanguageMode.TR
    )
}

@Preview
@Composable
private fun SplashScreenLightPreview() {
    SplashScreen(
        onFinished = {},
        themeMode = ThemeMode.LIGHT,
        languageMode = LanguageMode.EN
    )
}
