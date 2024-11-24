package com.example.firstcodelab

import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import com.example.firstcodelab.ui.theme.FirstCodelabTheme
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FirstCodelabTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    InteractiveCounterScreen();
                }
            }
        }
    }
}

@Composable
fun InteractiveCounterScreen() {
    var count by remember { mutableStateOf(0) }
    var backgroundColor by remember { mutableStateOf(Color(0xFFFAF3E0)) }
    var textColor by remember { mutableStateOf(Color(0xFF333333)) }
    var tapPosition by remember { mutableStateOf(Offset.Zero) }
    var showIncrementText by remember { mutableStateOf(false) }
    var rotationAngle by remember { mutableStateOf(0f) }
    var showConfetti by remember { mutableStateOf(false) }

    val colors = listOf(
        Color(0xFFFFE4C4) to Color(0xFF444444),
        Color(0xFFB2DFDB) to Color(0xFF004D40),
        Color(0xFFFFF9C4) to Color(0xFF3E2723),
        Color(0xFFE1BEE7) to Color(0xFF4A148C),
        Color(0xFFBBDEFB) to Color(0xFF1E88E5)
    )

    val context = LocalContext.current
    val confettiSound = remember {
        MediaPlayer.create(context, R.raw.confetti_sound)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .pointerInput(Unit) {
                detectTapGestures(onTap = { offset ->
                    count++
                    val (newBackground, newTextColor) = colors.random()
                    backgroundColor = newBackground
                    textColor = newTextColor
                    tapPosition = offset
                    showIncrementText = true
                    rotationAngle = Random.nextFloat() * 360

                    if (count % 100 == 0) {
                        showConfetti = true
                        confettiSound.start()

                        kotlinx.coroutines.MainScope().launch {
                            kotlinx.coroutines.delay(2000)
                            showConfetti = false
                        }
                    }

                    kotlinx.coroutines.MainScope().launch {
                        kotlinx.coroutines.delay(1000)
                        showIncrementText = false
                    }
                })
            },
        contentAlignment = Alignment.Center
    ) {
        // Confeti detrás del contador
        if (showConfetti) {
            ConfettiEffect()
        }

        // Contador centrado en un recuadro
        Box(
            modifier = Modifier
                .border(4.dp, textColor, RoundedCornerShape(16.dp))
                .padding(32.dp)
        ) {
            Text(
                text = "Count: $count",
                color = textColor,
                style = MaterialTheme.typography.headlineLarge
            )
        }

        // Incremento detrás del contador
        if (showIncrementText) {
            Box(modifier = Modifier.fillMaxSize()) {
                BasicText(
                    text = "INCREMENT!",
                    modifier = Modifier
                        .offset {
                            val offsetX = tapPosition.x.roundToInt()
                            val offsetY = tapPosition.y.roundToInt()
                            val safeOffsetX = if (offsetX in 100..900) offsetX else 100
                            val safeOffsetY = if (offsetY in 100..1800) offsetY else 200
                            IntOffset(safeOffsetX, safeOffsetY)
                        }
                        .graphicsLayer(
                            rotationZ = rotationAngle,
                            scaleX = 0.75f,
                            scaleY = 0.75f
                        ),
                    style = TextStyle(
                        color = textColor.copy(alpha = 0.6f),
                        fontSize = 20.sp,
                        letterSpacing = 2.sp
                    )
                )
            }
        }
    }
}

@Composable
fun ConfettiEffect() {
    // Partículas de confeti
    val confettiParticles = List(50) {
        Offset(
            Random.nextFloat() * 1000f,
            Random.nextFloat() * 1000f
        )
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        confettiParticles.forEach { position ->
            drawCircle(
                color = Color(
                    Random.nextFloat(),
                    Random.nextFloat(),
                    Random.nextFloat(),
                    1f
                ),
                radius = Random.nextFloat() * 20f,
                center = position
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun CounterScreenPreview() {
    FirstCodelabTheme {
        InteractiveCounterScreen()
    }
}