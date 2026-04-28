package com.smartshop.util

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Utility class for audio (beep) and haptic feedback.
 * Uses ToneGenerator for reliable beep and Vibrator for haptic feedback.
 * Default PIN: 0000
 */
@Singleton
class FeedbackManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var toneGenerator: ToneGenerator? = null
    
    private val vibrator: Vibrator by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }
    
    /**
     * Initialize ToneGenerator for beep sound.
     */
    fun initialize() {
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100)
        } catch (e: Exception) {
            // Silently fail - device may not support tone generation
        }
    }
    
    /**
     * Play beep sound using system ToneGenerator.
     */
    fun playBeep() {
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 150)
        } catch (e: Exception) {
            // Silently fail
        }
    }
    
    /**
     * Trigger haptic vibration.
     */
    fun vibrate() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(
                    VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(100)
            }
        } catch (e: Exception) {
            // Silently fail
        }
    }
    
    /**
     * Play beep and vibrate together.
     */
    fun beepAndVibrate() {
        playBeep()
        vibrate()
    }
    
    /**
     * Release resources.
     */
    fun release() {
        toneGenerator?.release()
        toneGenerator = null
    }
}