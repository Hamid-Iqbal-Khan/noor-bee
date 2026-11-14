package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.layout.ContentScale
import kotlinx.coroutines.delay
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import android.content.Context

@Composable
fun SplashScreen(
    onStartClick: () -> Unit,
    onParentSetupClick: () -> Unit
) {
    var animateStart by remember { mutableStateOf(false) }
    var showDeveloperDialog by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        animateStart = true
    }

    val scaleAnimate by animateFloatAsState(
        targetValue = if (animateStart) 1f else 0.4f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    val beeRotation by rememberInfiniteTransition(label = "rotation").animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "beeRotation"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFFDE7), // Very pale, soft yellow at top
                        Color(0xFFFFF59D), // Mild soft warm yellow
                        Color(0xFFFBC02D)  // Golden warm yellow at bottom
                      )
                )
            )
            .testTag("splash_screen_container")
    ) {
        // Main illustration column
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Upper elegant Arabic inscription: "رَبِّ زِدْنِي عِلْمًا"
            Text(
                text = "رَبِّ زِدْنِي عِلْمًا",
                fontSize = 44.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF3E2723), // Deep brown/bronze
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            // Dynamic content center area (Star + Book with side brackets + Floating Bee)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {
                // Bracketed Book Logo
                Box(
                    modifier = Modifier.wrapContentSize(),
                    contentAlignment = Alignment.Center
                ) {
                    // Small Star directly above the book logo
                    Text(
                        text = "★",
                        color = Color(0xFFFBC02D),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .offset(y = (-20).dp)
                    )

                    // Side Arcs Canvas (yellow curved brackets on left and right)
                    Canvas(
                        modifier = Modifier.size(width = 280.dp, height = 180.dp)
                    ) {
                        val arcColor = Color(0xFFFBC02D) // Golden Yellow
                        val strokeWidth = 4.dp.toPx()
                        
                        // Left Arc Parentheses
                        drawArc(
                            color = arcColor,
                            startAngle = 120f,
                            sweepAngle = 120f,
                            useCenter = false,
                            style = androidx.compose.ui.graphics.drawscope.Stroke(
                                width = strokeWidth,
                                cap = androidx.compose.ui.graphics.StrokeCap.Round
                            ),
                            topLeft = androidx.compose.ui.geometry.Offset(10.dp.toPx(), 10.dp.toPx()),
                            size = androidx.compose.ui.geometry.Size(40.dp.toPx(), 160.dp.toPx())
                        )
                        
                        // Right Arc Parentheses
                        drawArc(
                            color = arcColor,
                            startAngle = -60f,
                            sweepAngle = 120f,
                            useCenter = false,
                            style = androidx.compose.ui.graphics.drawscope.Stroke(
                                width = strokeWidth,
                                cap = androidx.compose.ui.graphics.StrokeCap.Round
                            ),
                            topLeft = androidx.compose.ui.geometry.Offset(230.dp.toPx(), 10.dp.toPx()),
                            size = androidx.compose.ui.geometry.Size(40.dp.toPx(), 160.dp.toPx())
                        )
                    }

                    // Centered Book Logo with NOOR and BEE text overlay
                    Box(
                        modifier = Modifier
                            .size(190.dp)
                            .scale(scaleAnimate)
                            .testTag("app_logo_hero"),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = com.example.R.drawable.ic_book_no_words),
                            contentDescription = "Noor Bee App Logo",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )

                        // Left Page overlay "NOOR"
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.5f)
                                .fillMaxHeight()
                                .align(Alignment.CenterStart),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "NOOR",
                                style = androidx.compose.ui.text.TextStyle(
                                    fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif,
                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Black,
                                    fontSize = 17.sp,
                                    color = Color.Black
                                ),
                                modifier = Modifier.offset(x = 10.dp, y = (-2).dp)
                            )
                        }

                        // Right Page overlay "BEE"
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.5f)
                                .fillMaxHeight()
                                .align(Alignment.CenterEnd),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "BEE",
                                style = androidx.compose.ui.text.TextStyle(
                                    fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif,
                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Black,
                                    fontSize = 17.sp,
                                    color = Color.Black
                                ),
                                modifier = Modifier.offset(x = (-10).dp, y = (-2).dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // FLOATING DYNAMIC BEE MASCOT
                AnimatedNoorBeeMascot(
                    modifier = Modifier
                        .size(120.dp)
                        .testTag("floating_dynamic_bee"),
                    isCelebrating = false
                )
            }

            // Subtitle
            Text(
                text = "Learn English & Arabic — Playfully!",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = Color(0xFF5D4037),
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Start Learning CTA Button
            Button(
                onClick = onStartClick,
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(58.dp)
                    .testTag("submit_button")
                    .shadow(8.dp, RoundedCornerShape(29.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3E2723), // Deep bronze/brown
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(29.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Start Learning",
                    tint = Color(0xFFFFD54F)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Let's Play & Learn!",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFD54F)
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Developer / Business Hub Link
            TextButton(
                onClick = { showDeveloperDialog = true },
                modifier = Modifier.testTag("developer_hub_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Developer Info",
                        tint = Color(0xFF5D4037).copy(alpha = 0.8f),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Developer & Business Contact",
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = Color(0xFF5D4037).copy(alpha = 0.8f),
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }

    if (showDeveloperDialog) {
        val context = LocalContext.current
        val uriHandler = LocalUriHandler.current
        val devWhatsApp = "+919288410426"
        val devEmail = "iqbal.it.tech@gmail.com"

        AlertDialog(
            onDismissRequest = { showDeveloperDialog = false },
            containerColor = Color(0xFFFFFDE7), // Beautiful high-contrast honey cream background
            title = {
                Text(
                    text = "Developer & Business Contact",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF3E2723)
                    )
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Thank you for your interest.\n\nFor app development, business inquiries, partnerships, technical consulting, or support, please contact me using the details below.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF5D4037) // Deep honey-brown for excellent contrast and readability
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // WhatsApp Action Button
                    Button(
                        onClick = {
                            val cleanPhone = devWhatsApp.replace("+", "").replace(" ", "").trim()
                            val waUrl = "https://wa.me/$cleanPhone?text=Hello!%20I%20am%20interested%20in%20your%20Noor%20Bee%20app%20for%20business%20collaboration."
                            try {
                                uriHandler.openUri(waUrl)
                            } catch (e: Exception) {
                                // Fallback
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)), // WhatsApp Green
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = "WhatsApp",
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Connect via WhatsApp", color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Email Action Button
                    Button(
                        onClick = {
                            val emailIntent = android.content.Intent(android.content.Intent.ACTION_SENDTO).apply {
                                data = android.net.Uri.parse("mailto:$devEmail")
                                putExtra(android.content.Intent.EXTRA_SUBJECT, "Noor Bee App - Business Collaboration Inquiry")
                            }
                            try {
                                context.startActivity(emailIntent)
                            } catch (e: Exception) {
                                try {
                                    uriHandler.openUri("mailto:$devEmail?subject=Noor%20Bee%20Business%20Inquiry")
                                } catch (ex: Exception) {}
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3E2723)),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "Email",
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Send Business Email", color = Color.White)
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { showDeveloperDialog = false }
                ) {
                    Text("Close", color = Color(0xFF3E2723), fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
fun NoorBeeMascot(modifier: Modifier = Modifier, rotationDegrees: Float = 0f) {
    AnimatedNoorBeeMascot(modifier = modifier, isCelebrating = false, speedMultiplier = 1.0f)
}
