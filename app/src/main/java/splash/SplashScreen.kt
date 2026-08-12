package com.example.ematalk.splash

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ematalk.navigation.Screen
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavController
) {

    // Logo animation
    val infiniteTransition = rememberInfiniteTransition(label = "logo")

    val glowScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1200,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    val logoScale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(900),
        label = "logoScale"
    )

    // Existing navigation logic
    LaunchedEffect(Unit) {

        delay(2500)

        if (FirebaseAuth.getInstance().currentUser != null) {

            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.Splash.route) {
                    inclusive = true
                }
            }

        } else {

            navController.navigate(Screen.Login.route) {
                popUpTo(Screen.Splash.route) {
                    inclusive = true
                }
            }
        }
    }

    // Premium dark purple background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF090014),
                        Color(0xFF16002D),
                        Color(0xFF050008)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // Glow behind logo
            Box(
                modifier = Modifier
                    .size(155.dp)
                    .scale(glowScale)
                    .shadow(
                        elevation = 35.dp,
                        shape = androidx.compose.foundation.shape.CircleShape,
                        ambientColor = Color(0xFF9C4DFF),
                        spotColor = Color(0xFF7B2CFF)
                    )
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFB86BFF),
                                Color(0xFF7027D9),
                                Color(0xFF25004A)
                            )
                        ),
                        shape = androidx.compose.foundation.shape.CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {

                // EmaTalk 3D-style microphone logo
                Text(
                    text = "🎙️",
                    fontSize = 65.sp,
                    modifier = Modifier.scale(logoScale)
                )
            }

            Spacer(
                modifier = Modifier.height(28.dp)
            )

            // App name
            Text(
                text = "EmaTalk",
                color = Color.White,
                fontSize = 38.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.5.sp
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            // Tagline
            Text(
                text = "Talk • Connect • Enjoy",
                color = Color(0xFFC99AFF),
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.sp
            )

            Spacer(
                modifier = Modifier.height(35.dp)
            )

            // Loading dots
            Text(
                text = "●  ●  ●",
                color = Color(0xFF9B4DFF),
                fontSize = 13.sp,
                modifier = Modifier.alpha(0.9f)
            )

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            Text(
                text = "Version 1.0",
                color = Color(0xFF77717F),
                fontSize = 11.sp
            )
        }
    }
}