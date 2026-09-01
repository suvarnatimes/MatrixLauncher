package com.matrixlauncher.ui.common

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerId
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import kotlin.math.abs
import kotlin.math.hypot

/**
 * Unified, high-performance gesture detector for single-finger and two-finger interactions.
 */
fun Modifier.detectMatrixGestures(
    onSwipeUp: () -> Unit = {},
    onSwipeDown: () -> Unit = {},
    onSwipeLeft: () -> Unit = {},
    onSwipeRight: () -> Unit = {},
    onTwoFingerSwipeUp: () -> Unit = {},
    onTwoFingerSwipeDown: () -> Unit = {},
    onPinchIn: () -> Unit = {},
    onPinchOut: () -> Unit = {},
    onDoubleTap: () -> Unit = {}
): Modifier = pointerInput(Unit) {
    var lastTapTime = 0L

    awaitEachGesture {
        val down = awaitFirstDown(requireUnconsumed = false)
        val startTime = System.currentTimeMillis()
        val startPos = down.position
        var maxPointers = 1
        var totalDragX = 0f
        var totalDragY = 0f
        var initialSpan = 0f
        var currentSpan = 0f
        var gestureTriggered = false

        while (true) {
            val event: PointerEvent = awaitPointerEvent()
            val activePointers = event.changes.filter { it.pressed }
            if (activePointers.size > maxPointers) {
                maxPointers = activePointers.size
            }

            if (activePointers.size >= 2) {
                val p1 = activePointers[0].position
                val p2 = activePointers[1].position
                val span = hypot(p1.x - p2.x, p1.y - p2.y)
                if (initialSpan == 0f) {
                    initialSpan = span
                }
                currentSpan = span

                // Track two-finger drag
                val dy1 = activePointers[0].positionChange().y
                val dy2 = activePointers[1].positionChange().y
                totalDragY += (dy1 + dy2) / 2f
            } else if (activePointers.size == 1) {
                val change = activePointers[0].positionChange()
                totalDragX += change.x
                totalDragY += change.y
            }

            if (activePointers.isEmpty()) {
                // All fingers lifted up - evaluate gesture
                val duration = System.currentTimeMillis() - startTime
                val distFromStart = hypot(startPos.x - down.position.x, startPos.y - down.position.y)

                if (maxPointers == 1) {
                    if (abs(totalDragY) > 80f && abs(totalDragY) > abs(totalDragX)) {
                        if (totalDragY < -80f) {
                            onSwipeUp()
                            gestureTriggered = true
                        } else if (totalDragY > 80f) {
                            onSwipeDown()
                            gestureTriggered = true
                        }
                    } else if (abs(totalDragX) > 80f && abs(totalDragX) > abs(totalDragY)) {
                        if (totalDragX < -80f) {
                            onSwipeLeft()
                            gestureTriggered = true
                        } else if (totalDragX > 80f) {
                            onSwipeRight()
                            gestureTriggered = true
                        }
                    } else if (duration < 300 && distFromStart < 30f) {
                        // Potential tap or double tap
                        val now = System.currentTimeMillis()
                        if (now - lastTapTime < 350) {
                            onDoubleTap()
                            lastTapTime = 0L
                        } else {
                            lastTapTime = now
                        }
                    }
                } else if (maxPointers >= 2) {
                    if (initialSpan > 0f && currentSpan > 0f) {
                        val zoomRatio = currentSpan / initialSpan
                        if (zoomRatio < 0.78f) {
                            onPinchIn()
                            gestureTriggered = true
                        } else if (zoomRatio > 1.25f) {
                            onPinchOut()
                            gestureTriggered = true
                        }
                    }

                    if (!gestureTriggered) {
                        if (totalDragY < -80f) {
                            onTwoFingerSwipeUp()
                        } else if (totalDragY > 80f) {
                            onTwoFingerSwipeDown()
                        }
                    }
                }
                break
            }
        }
    }
}
