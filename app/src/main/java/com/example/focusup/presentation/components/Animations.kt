package com.example.focusup.presentation.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

// Animaciones de entrada/salida para pantallas
@OptIn(ExperimentalAnimationApi::class)
fun fadeIn() = fadeIn(animationSpec = tween(300))

@OptIn(ExperimentalAnimationApi::class)
fun fadeOut() = fadeOut(animationSpec = tween(300))

@OptIn(ExperimentalAnimationApi::class)
fun slideInFromRight() = slideInHorizontally(
    initialOffsetX = { it },
    animationSpec = tween(300)
)

@OptIn(ExperimentalAnimationApi::class)
fun slideOutToLeft() = slideOutHorizontally(
    targetOffsetX = { -it },
    animationSpec = tween(300)
)

@OptIn(ExperimentalAnimationApi::class)
fun slideInFromBottom() = slideInVertically(
    initialOffsetY = { it },
    animationSpec = tween(300)
)

@OptIn(ExperimentalAnimationApi::class)
fun slideOutToBottom() = slideOutVertically(
    targetOffsetY = { it },
    animationSpec = tween(300)
)

// Animaciones de escala
@OptIn(ExperimentalAnimationApi::class)
fun scaleIn() = scaleIn(
    initialScale = 0.8f,
    animationSpec = tween(300)
)

@OptIn(ExperimentalAnimationApi::class)
fun scaleOut() = scaleOut(
    targetScale = 0.8f,
    animationSpec = tween(300)
)

// Combinaciones comunes
@OptIn(ExperimentalAnimationApi::class)
fun fadeInSlideIn() = fadeIn() + slideInFromRight()

@OptIn(ExperimentalAnimationApi::class)
fun fadeOutSlideOut() = fadeOut() + slideOutToLeft()

@OptIn(ExperimentalAnimationApi::class)
fun fadeInScale() = fadeIn() + scaleIn()

@OptIn(ExperimentalAnimationApi::class)
fun fadeOutScale() = fadeOut() + scaleOut()

// Durations constantes
object AnimationDurations {
    const val FAST = 150
    const val NORMAL = 300
    const val SLOW = 500
}

// Easing constantes
object AnimationEasings {
    val EMPHASIZED = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1.0f)
    val STANDARD = CubicBezierEasing(0.4f, 0.0f, 0.6f, 1.0f)
    val EMPHASIZED_DECELERATE = CubicBezierEasing(0.0f, 0.0f, 0.2f, 1.0f)
    val EMPHASIZED_ACCELERATE = CubicBezierEasing(0.4f, 0.0f, 1.0f, 1.0f)
}
