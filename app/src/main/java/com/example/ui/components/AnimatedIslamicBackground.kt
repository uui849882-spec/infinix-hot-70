package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private data class StarParticle(
    val xRatio: Float,
    val yRatio: Float,
    val radius: Float,
    val speed: Float,
    val baseAlpha: Float
)

@Composable
fun AnimatedIslamicBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "islamic_bg_anim")

    // Slow rotation of Islamic geometric motif
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 60000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    // Pulse alpha for ambient glow
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    // Floating particles offset
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 10000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float"
    )

    val particles = remember {
        val random = Random(42)
        List(25) {
            StarParticle(
                xRatio = random.nextFloat(),
                yRatio = random.nextFloat(),
                radius = random.nextFloat() * 2.5f + 1f,
                speed = random.nextFloat() * 0.4f + 0.1f,
                baseAlpha = random.nextFloat() * 0.4f + 0.2f
            )
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // 1. Deep Islamic emerald gradient background
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF071B14),
                        Color(0xFF0C2B20),
                        Color(0xFF061510)
                    )
                )
            )

            // 2. Central Islamic 8-Pointed Star geometric watermark (Rub el Hizb)
            val centerOffset = Offset(width * 0.5f, height * 0.35f)
            val baseRadius = width * 0.38f

            rotate(rotationAngle, pivot = centerOffset) {
                // Outer subtle circle
                drawCircle(
                    color = Color(0xFFD4AF37).copy(alpha = glowPulse * 0.4f),
                    radius = baseRadius * 1.15f,
                    center = centerOffset,
                    style = Stroke(width = 1.5f)
                )

                // First square
                drawIslamicSquare(centerOffset, baseRadius, Color(0xFFD4AF37).copy(alpha = glowPulse * 0.35f))

                // Second square rotated 45 degrees
                rotate(45f, pivot = centerOffset) {
                    drawIslamicSquare(centerOffset, baseRadius, Color(0xFFD4AF37).copy(alpha = glowPulse * 0.35f))
                }

                // Inner ornate star
                rotate(-rotationAngle * 1.5f, pivot = centerOffset) {
                    drawIslamicSquare(centerOffset, baseRadius * 0.55f, Color(0xFFFFDF73).copy(alpha = glowPulse * 0.5f))
                    rotate(45f, pivot = centerOffset) {
                        drawIslamicSquare(centerOffset, baseRadius * 0.55f, Color(0xFFFFDF73).copy(alpha = glowPulse * 0.5f))
                    }
                }
            }

            // 3. Floating celestial golden dust particles
            particles.forEach { p ->
                val px = (p.xRatio * width + floatOffset * p.speed) % width
                val py = (p.yRatio * height - floatOffset * p.speed * 0.5f + height) % height
                drawCircle(
                    color = Color(0xFFFFE58F).copy(alpha = p.baseAlpha * glowPulse * 2.5f),
                    radius = p.radius,
                    center = Offset(px, py)
                )
            }
        }

        // Foreground application content
        content()
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawIslamicSquare(
    center: Offset,
    halfSize: Float,
    color: Color
) {
    val path = Path().apply {
        moveTo(center.x - halfSize, center.y - halfSize)
        lineTo(center.x + halfSize, center.y - halfSize)
        lineTo(center.x + halfSize, center.y + halfSize)
        lineTo(center.x - halfSize, center.y + halfSize)
        close()
    }
    drawPath(path = path, color = color, style = Stroke(width = 1.5f))
}
