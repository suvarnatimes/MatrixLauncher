package com.matrixlauncher.ui.common

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.matrixlauncher.ui.mvi.HapticFeedbackType

class HapticHelper(context: Context) {

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    fun performHaptic(type: HapticFeedbackType, enabled: Boolean = true) {
        if (!enabled || vibrator == null || !vibrator.hasVibrator()) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val effect = when (type) {
                HapticFeedbackType.TICK -> VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK)
                HapticFeedbackType.CLICK -> VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
                HapticFeedbackType.HEAVY_CLICK -> VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK)
                HapticFeedbackType.DOUBLE_CLICK -> VibrationEffect.createPredefined(VibrationEffect.EFFECT_DOUBLE_CLICK)
            }
            vibrator.vibrate(effect)
        } else {
            val duration = when (type) {
                HapticFeedbackType.TICK -> 10L
                HapticFeedbackType.CLICK -> 20L
                HapticFeedbackType.HEAVY_CLICK -> 40L
                HapticFeedbackType.DOUBLE_CLICK -> 35L
            }
            val amplitude = when (type) {
                HapticFeedbackType.TICK -> 50
                HapticFeedbackType.CLICK -> 100
                HapticFeedbackType.HEAVY_CLICK -> 200
                HapticFeedbackType.DOUBLE_CLICK -> 150
            }
            vibrator.vibrate(VibrationEffect.createOneShot(duration, amplitude))
        }
    }
}
