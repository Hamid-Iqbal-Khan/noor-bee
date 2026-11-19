package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.SlateGrey
import com.example.ui.theme.BorderGrey
import kotlinx.coroutines.delay
import kotlin.math.sin
import kotlin.random.Random

/**
 * An advanced, highly interactive animated Noor Bee mascot.
 * Centered on simulation of CSS keyframe motions:
 * - Hover / Bobbing (Vertical sine wave)
 * - Sway / Rotation (Back and forth gentle rotate)
 * - Pulsing scale (Gentle breathing)
 * - Fluttering wings (Rapid high-frequency scale/rotation of wings)
 */
data class NoorBeeStyle(
    val equippedOutfitId: String = "classic_bee",
    val mascotColor: String = "default"
)

val LocalNoorBeeStyle = androidx.compose.runtime.staticCompositionLocalOf { NoorBeeStyle() }

@Composable
fun AnimatedNoorBeeMascot(
    modifier: Modifier = Modifier,
    isCelebrating: Boolean = false,
    speedMultiplier: Float = 1.0f,
    outfitId: String? = null,
    mascotColor: String? = null
) {
    val style = LocalNoorBeeStyle.current
    val actualOutfit = outfitId ?: style.equippedOutfitId
    val actualColor = mascotColor ?: style.mascotColor

    val infiniteTransition = rememberInfiniteTransition(label = "NoorBeeMascotAnimations")

    // CSS Keyframe 1: Bobbing up and down (Y Translation)
    val floatDuration = (1800 / speedMultiplier).toInt()
    val bobbingOffset by infiniteTransition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(floatDuration, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Bobbing"
    )

    // CSS Keyframe 2: Gentle Sway/tilt
    val swayDuration = (2200 / speedMultiplier).toInt()
    val swayRotation by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(swayDuration, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Sway"
    )

    // CSS Keyframe 3: Breathing / Pulsing Scale
    val pulseDuration = (1400 / speedMultiplier).toInt()
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(pulseDuration, easing = EaseInOutQuad),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Beething"
    )

    // CSS Keyframe 4: Wing fluttering
    val wingFlutterDuration = if (isCelebrating) 60 else 110
    val leftWingScaleY by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(wingFlutterDuration, easing = EaseInOutQuad),
            repeatMode = RepeatMode.Reverse
        ),
        label = "LeftWingFlutter"
    )
    val rightWingScaleY by infiniteTransition.animateFloat(
        initialValue = 1.1f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(wingFlutterDuration, easing = EaseInOutQuad),
            repeatMode = RepeatMode.Reverse
        ),
        label = "RightWingFlutter"
    )

    Box(
        modifier = modifier
            .offset(y = bobbingOffset.dp)
            .rotate(swayRotation)
            .scale(if (isCelebrating) pulseScale * 1.15f else pulseScale)
            .testTag("animated_noor_bee_mascot"),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val cx = w / 2f
            val cy = h / 2f

            // 1. Draw wings with rapid fluttering scales
            val wingBrush = Brush.radialGradient(
                colors = listOf(Color(0xEAE0F7FA), Color(0x9080DEEA), Color(0x3000ACC1)),
                center = Offset(cx, cy)
            )

            // Left Wing Flutter
            rotate(-25f, pivot = Offset(cx - 30f, cy - 20f)) {
                drawOval(
                    brush = wingBrush,
                    topLeft = Offset(cx - 95f, cy - 85f),
                    size = Size(90f, 65f * leftWingScaleY)
                )
            }

            // Right Wing Flutter
            rotate(25f, pivot = Offset(cx + 30f, cy - 20f)) {
                drawOval(
                    brush = wingBrush,
                    topLeft = Offset(cx + 5f, cy - 85f),
                    size = Size(90f, 65f * rightWingScaleY)
                )
            }

            // Superhero Cloak (Outfit: "super_bee")
            if (actualOutfit == "super_bee") {
                val capeColor = Color(0xFFD32F2F)
                // Left side cape shape
                val leftCapePath = androidx.compose.ui.graphics.Path().apply {
                    moveTo(cx - 30f, cy + 20f)
                    lineTo(cx - 95f, cy + 70f)
                    lineTo(cx - 40f, cy + 80f)
                    close()
                }
                drawPath(leftCapePath, color = capeColor)
                
                // Right side cape shape
                val rightCapePath = androidx.compose.ui.graphics.Path().apply {
                    moveTo(cx + 30f, cy + 20f)
                    lineTo(cx + 95f, cy + 70f)
                    lineTo(cx + 40f, cy + 80f)
                    close()
                }
                drawPath(rightCapePath, color = capeColor)
            }

            // 2. Draw chubby bee body based on dynamic selected color
            val bodyBrush = when (actualColor) {
                "royal_purple" -> Brush.verticalGradient(
                    colors = if (isCelebrating) {
                        listOf(Color(0xFFE1BEE7), Color(0xFFBA68C8), Color(0xFF7B1FA2))
                    } else {
                        listOf(Color(0xFFCE93D8), Color(0xFF9C27B0), Color(0xFF4A148C))
                    }
                )
                "emerald_green" -> Brush.verticalGradient(
                    colors = if (isCelebrating) {
                        listOf(Color(0xFFC8E6C9), Color(0xFF81C784), Color(0xFF388E3C))
                    } else {
                        listOf(Color(0xFFA5D6A7), Color(0xFF4CAF50), Color(0xFF1B5E20))
                    }
                )
                "cosmic_pink" -> Brush.verticalGradient(
                    colors = if (isCelebrating) {
                        listOf(Color(0xFFF8BBD0), Color(0xFFF06292), Color(0xFFC2185B))
                    } else {
                        listOf(Color(0xFFF48FB1), Color(0xFFE91E63), Color(0xFF880E4F))
                    }
                )
                "ocean_blue" -> Brush.verticalGradient(
                    colors = if (isCelebrating) {
                        listOf(Color(0xFFB2EBF2), Color(0xFF4DD0E1), Color(0xFF0097A7))
                    } else {
                        listOf(Color(0xFF80DEEA), Color(0xFF00ACC1), Color(0xFF006064))
                    }
                )
                else -> Brush.verticalGradient( // "default" classic honey yellow
                    colors = if (isCelebrating) {
                        listOf(Color(0xFFFFF176), Color(0xFFFFC107), Color(0xFFFF8F00))
                    } else {
                        listOf(Color(0xFFFFEE58), Color(0xFFFFB300), Color(0xFFE65100))
                    }
                )
            }

            drawOval(
                brush = bodyBrush,
                topLeft = Offset(cx - 70f, cy - 50f),
                size = Size(140f, 110f)
            )

            // 3. Cute Black Stripes (Navy Charcoal or custom colors of customizer matches)
            val stripeColor = Color(0xFF263238)
            drawArc(
                color = stripeColor,
                startAngle = 10f,
                sweepAngle = 160f,
                useCenter = false,
                topLeft = Offset(cx - 65f, cy - 35f),
                size = Size(130f, 80f),
                style = Stroke(width = 18f, cap = StrokeCap.Round)
            )
            drawArc(
                color = stripeColor,
                startAngle = 10f,
                sweepAngle = 160f,
                useCenter = false,
                topLeft = Offset(cx - 55f, cy - 10f),
                size = Size(110f, 60f),
                style = Stroke(width = 18f, cap = StrokeCap.Round)
            )

            // 4. Large kawaii eyes
            // Eye Left
            drawCircle(color = Color.White, radius = 21f, center = Offset(cx - 28f, cy - 18f))
            drawCircle(color = Color(0xFF2196F3), radius = 13f, center = Offset(cx - 28f, cy - 16f))
            drawCircle(color = Color.Black, radius = 7f, center = Offset(cx - 26f, cy - 16f))
            drawCircle(color = Color.White, radius = 3.5f, center = Offset(cx - 29f, cy - 19f)) // Glint

            // Eye Right
            drawCircle(color = Color.White, radius = 21f, center = Offset(cx + 28f, cy - 18f))
            drawCircle(color = Color(0xFF2196F3), radius = 13f, center = Offset(cx + 28f, cy - 16f))
            drawCircle(color = Color.Black, radius = 7f, center = Offset(cx + 26f, cy - 16f))
            drawCircle(color = Color.White, radius = 3.5f, center = Offset(cx + 25f, cy - 19f)) // Glint

            // 5. Blushing cheeks
            val blushColor = Color(0xFFFF8A80).copy(alpha = 0.85f)
            drawCircle(color = blushColor, radius = 10f, center = Offset(cx - 45f, cy + 5f))
            drawCircle(color = blushColor, radius = 10f, center = Offset(cx + 45f, cy + 5f))

            // 6. Cute smiling mouth
            drawArc(
                color = stripeColor,
                startAngle = 15f,
                sweepAngle = 150f,
                useCenter = false,
                topLeft = Offset(cx - 15f, cy - 5f),
                size = Size(30f, 22f),
                style = Stroke(width = 5.5f, cap = StrokeCap.Round)
            )

            // 7. Sweet little Antennae
            val antennaeColor = Color(0xFF37474F)
            // Left Antenna
            drawArc(
                color = antennaeColor,
                startAngle = 160f,
                sweepAngle = 80f,
                useCenter = false,
                topLeft = Offset(cx - 36f, cy - 70f),
                size = Size(25f, 30f),
                style = Stroke(width = 6f, cap = StrokeCap.Round)
            )
            drawCircle(color = antennaeColor, radius = 8f, center = Offset(cx - 36f, cy - 68f))

            // Right Antenna
            drawArc(
                color = antennaeColor,
                startAngle = 300f,
                sweepAngle = 80f,
                useCenter = false,
                topLeft = Offset(cx + 11f, cy - 70f),
                size = Size(25f, 30f),
                style = Stroke(width = 6f, cap = StrokeCap.Round)
            )
            drawCircle(color = antennaeColor, radius = 8f, center = Offset(cx + 36f, cy - 68f))

            // Draw Head Accessories!
            when (actualOutfit) {
                "chef_hat", "bee_chef" -> {
                    // Chef hat
                    val hatY = cy - 52f
                    drawOval(Color.White, topLeft = Offset(cx - 18f, hatY - 32f), size = Size(36f, 36f))
                    drawOval(Color.White, topLeft = Offset(cx - 32f, hatY - 24f), size = Size(32f, 32f))
                    drawOval(Color.White, topLeft = Offset(cx + 0f, hatY - 24f), size = Size(32f, 32f))
                    // Base band
                    drawRoundRect(
                        color = Color(0xFFEEEEEE),
                        topLeft = Offset(cx - 24f, hatY - 8f),
                        size = Size(48f, 14f),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
                    )
                }
                "painter_hat", "bee_painter" -> {
                    // Painter beret tilted beautifully
                    val beretColor = Color(0xFFE53935)
                    rotate(-12f, pivot = Offset(cx, cy - 50f)) {
                        drawOval(beretColor, topLeft = Offset(cx - 32f, cy - 68f), size = Size(64f, 22f))
                        drawCircle(beretColor, radius = 4f, center = Offset(cx, cy - 70f))
                    }
                }
                "space_helmet", "bee_space" -> {
                    // Transparent bubble space helmet
                    drawCircle(
                        color = Color(0x3880DEEA), // Translucent cyan sheen
                        radius = 82f,
                        center = Offset(cx, cy - 10f),
                        style = Stroke(width = 5f)
                    )
                    drawOval(
                        color = Color.White.copy(alpha = 0.5f),
                        topLeft = Offset(cx - 55f, cy - 55f),
                        size = Size(32f, 14f)
                    )
                }
                "royal_crown", "bee_royal" -> {
                    // Shiny Golden Crown with ruby jewels
                    val crownPath = androidx.compose.ui.graphics.Path().apply {
                        moveTo(cx - 25f, cy - 38f) // bottom left
                        lineTo(cx - 32f, cy - 64f) // peak left
                        lineTo(cx - 8f, cy - 48f)  // valley
                        lineTo(cx, cy - 74f)       // peak center
                        lineTo(cx + 8f, cy - 48f)  // valley
                        lineTo(cx + 32f, cy - 64f) // peak right
                        lineTo(cx + 25f, cy - 38f) // bottom right
                        close()
                    }
                    drawPath(crownPath, color = Color(0xFFFFD54F))
                    drawPath(crownPath, color = Color(0xFFFF8F00), style = Stroke(width = 2.5f))
                    // Ruby jewels
                    drawCircle(Color(0xFFE91E63), radius = 3.5f, center = Offset(cx - 32f, cy - 64f))
                    drawCircle(Color(0xFFE91E63), radius = 4.5f, center = Offset(cx, cy - 74f))
                    drawCircle(Color(0xFFE91E63), radius = 3.5f, center = Offset(cx + 32f, cy - 64f))
                }
                "bee_detective" -> {
                    // Detective brown detective hat!
                    val hatColor = Color(0xFF795548)
                    drawOval(hatColor, topLeft = Offset(cx - 40f, cy - 45f), size = Size(80f, 10f)) // Brim
                    drawRoundRect(
                        color = hatColor,
                        topLeft = Offset(cx - 24f, cy - 68f),
                        size = Size(48f, 24f),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f)
                    )
                    // Black band
                    drawRect(Color.Black, topLeft = Offset(cx - 24f, cy - 50f), size = Size(48f, 6f))
                }
            }
        }
    }
}

/**
 * A cute customized speech popover bubble component
 */
@Composable
fun NoorBeeSpeechBubble(
    text: String,
    isArabic: Boolean,
    modifier: Modifier = Modifier
) {
    val bubbleShape = remember(isArabic) {
        GenericShape { size, _ ->
            val r = 24.dp.value
            // Draw rounded rectangle
            addRoundRect(
                androidx.compose.ui.geometry.RoundRect(
                    left = 0f,
                    top = 0f,
                    right = size.width,
                    bottom = size.height - 15f,
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(r, r)
                )
            )
            // Speech Bubble Pointer / Tail down-pointing
            if (isArabic) {
                // Pointer on the right side for Arabic RTL aesthetics
                moveTo(size.width - 60f, size.height - 15f)
                lineTo(size.width - 45f, size.height)
                lineTo(size.width - 30f, size.height - 15f)
            } else {
                // Pointer on the left side for English LTR aesthetics
                moveTo(40f, size.height - 15f)
                lineTo(55f, size.height)
                lineTo(70f, size.height - 15f)
            }
        }
    }

    Box(
        modifier = modifier
            .shadow(4.dp, shape = bubbleShape)
            .background(Color.White, shape = bubbleShape)
            .border(2.dp, Color(0xFFFFF176), shape = bubbleShape)
            .padding(start = 18.dp, top = 14.dp, end = 18.dp, bottom = 24.dp)
    ) {
        Text(
            text = text,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = DeepNavy,
            textAlign = TextAlign.Center,
            lineHeight = 21.sp,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * A beautiful Bursting Particle effect in Jetpack Compose
 */
@Composable
fun SparkleParticles(
    modifier: Modifier = Modifier,
    triggerKey: Any
) {
    val particleCount = 20
    val animators = remember(triggerKey) {
        List(particleCount) {
            val angle = Random.nextFloat() * 2f * Math.PI
            val distance = Random.nextFloat() * 150f + 50f
            val targetX = (cos(angle) * distance).toFloat()
            val targetY = (sin(angle) * distance).toFloat()
            Triple(
                targetX,
                targetY,
                Random.nextFloat() * 10f + 6f // size
            )
        }
    }

    val animatedProgress = remember(triggerKey) { Animatable(0f) }

    LaunchedEffect(triggerKey) {
        animatedProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(1200, easing = EaseOutCubic)
        )
    }

    if (animatedProgress.value < 1f) {
        Canvas(modifier = modifier) {
            val cx = size.width / 2f
            val cy = size.height / 2f
            val progress = animatedProgress.value
            val alpha = 1f - progress

            animators.forEachIndexed { i, particle ->
                val x = cx + particle.first * progress
                val y = cy + particle.second * progress
                val radius = particle.third * (1f - progress * 0.5f)

                val color = when (i % 4) {
                    0 -> Color(0xFFFFD54F) // Gold Star
                    1 -> Color(0xFF4CAF50) // Islamic Green
                    2 -> Color(0xFF03A9F4) // Sky Blue
                    else -> Color(0xFFFF4081) // Pink Sparkle
                }

                drawCircle(
                    color = color.copy(alpha = alpha),
                    radius = radius,
                    center = Offset(x, y)
                )
            }
        }
    }
}

// Helpers for math formulas
private fun cos(angle: Double): Double = kotlin.math.cos(angle)
private fun sin(angle: Double): Double = kotlin.math.sin(angle)

/**
 * Full page premium simulation CSS overlay when lessons are completed.
 * Features spring-ups, bursting particles, speech bubbles and TTS triggers.
 */
@Composable
fun NoorBeeCelebrationOverlay(
    visible: Boolean,
    coinsEarned: Int,
    starsEarned: Int,
    lessonName: String,
    currentLanguage: String = "English",
    onDismiss: () -> Unit,
    onSoundTrigger: (String) -> Unit = {}
) {
    val isArabic = remember(currentLanguage) { currentLanguage == "Arabic" }

    val motivatorsEnglish = remember {
        listOf(
            "Woohoo! You did it, little champion! 🌟🐝",
            "Bzzzt! Excellent learning today! So sweet! 🍯💖",
            "Mashallah! You are growing so smart every day! 🎓⚡",
            "Double scoop of happiness! You finished the lesson! 🍨🎉",
            "Noor Bee is so happy! High five, superstar! 🖐️⭐"
        )
    }

    val motivatorsArabic = remember {
        listOf(
            "يا بطل! عمل مذهل تبارك الله! 🌟🐝",
            "بزززت! إنجاز رائع وحلو مثل العسل! 🍯💖",
            "ما شاء الله! أنت ذكي وتتعلم بسرعة فائقة! 🎓⚡",
            "عسل وجنة! لقد أكملت الدرس بنجاح رائع! 🍨🎉",
            "نورا النحلة سعيدة جداً ومسرورة بك يا بطل! 🖐️⭐"
        )
    }

    val randomMessage = remember(visible) {
        if (isArabic) motivatorsArabic.random() else motivatorsEnglish.random()
    }

    // Trigger TTS and play celebration when visible goes from false -> true
    LaunchedEffect(visible) {
        if (visible) {
            val audioPrefix = if (isArabic) "text:arabic:" else "text:english:"
            onSoundTrigger("$audioPrefix$randomMessage")
        }
    }

    if (visible) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable { onDismiss() } // Tap outer area to dismiss
                .testTag("noor_bee_celebration_overlay"),
            contentAlignment = Alignment.Center
        ) {
            // Burst particles behind
            SparkleParticles(
                modifier = Modifier.fillMaxSize(),
                triggerKey = visible
            )

            // Premium spring sliding card
            var animOffset by remember { mutableStateOf(300.dp) }
            LaunchedEffect(Unit) {
                animOffset = 0.dp
            }

            val animatedOffset by animateDpAsState(
                targetValue = animOffset,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                label = "CardSpringAnimation"
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .offset(y = animatedOffset)
                    .clickable(enabled = false) {} // block click through
                    .testTag("celebration_card"),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(2.dp, Color(0xFFFFF176)),
                elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Close button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.testTag("celebration_close_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = SlateGrey
                            )
                        }
                    }

                    // 1. Double Animated Mascot
                    Box(
                        modifier = Modifier
                            .size(140.dp)
                            .padding(bottom = 12.dp)
                    ) {
                        AnimatedNoorBeeMascot(
                            modifier = Modifier.fillMaxSize(),
                            isCelebrating = true,
                            speedMultiplier = 1.3f
                        )
                    }

                    // 2. Chat speech bubble containing positive reinforcement
                    NoorBeeSpeechBubble(
                        text = randomMessage,
                        isArabic = isArabic,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 3. Lesson name Badge
                    Surface(
                        color = Color(0xFFFFFDE7),
                        border = BorderStroke(1.dp, Color(0xFFFFD54F)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        Text(
                            text = lessonName,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE65100),
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }

                    // 4. Rewards Summary Grid (Golden Coins and Stars with high-contrast icons!)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Golden Honey Jar (Coins)
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFDEC)),
                            border = BorderStroke(1.dp, Color(0xFFFFD54F))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("🍯", fontSize = 24.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "+$coinsEarned",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 18.sp,
                                        color = Color(0xFFE65100)
                                    )
                                    Text(
                                        text = if (isArabic) "عسل كسبت" else "Honey Jars",
                                        fontSize = 10.sp,
                                        color = SlateGrey,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }

                        // Star points (Stars)
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                            border = BorderStroke(1.dp, Color(0xFF90CAF9))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Star",
                                    tint = Color(0xFF1976D2),
                                    modifier = Modifier.size(26.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "+$starsEarned",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 18.sp,
                                        color = Color(0xFF1976D2)
                                    )
                                    Text(
                                        text = if (isArabic) "نجوم ذهبية" else "Star Coins",
                                        fontSize = 10.sp,
                                        color = SlateGrey,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // 5. M3 Action Button
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("celebration_continue_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB300)),
                        shape = RoundedCornerShape(26.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                    ) {
                        Text(
                            text = if (isArabic) "أحسنت! استمر يا بطل 👍" else "Awesome! Continue 👍",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF3E2723)
                        )
                    }
                }
            }
        }
    }
}
