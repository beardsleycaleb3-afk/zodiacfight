package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

object GameSoundPlayer {
  private val scope = CoroutineScope(Dispatchers.Default)
  var isMuted: Boolean = false
  var sfxVolume: Float = 0.8f

  private const val SAMPLE_RATE = 22050

  private fun playBuffer(pcm: ShortArray) {
    if (isMuted || sfxVolume <= 0.01f) return
    scope.launch {
      try {
        val audioTrack = AudioTrack.Builder()
          .setAudioAttributes(
            AudioAttributes.Builder()
              .setUsage(AudioAttributes.USAGE_GAME)
              .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
              .build()
          )
          .setAudioFormat(
            AudioFormat.Builder()
              .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
              .setSampleRate(SAMPLE_RATE)
              .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
              .build()
          )
          .setBufferSizeInBytes(pcm.size * 2)
          .setTransferMode(AudioTrack.MODE_STATIC)
          .build()

        audioTrack.write(pcm, 0, pcm.size)
        audioTrack.play()
        // Release after finished
        kotlinx.coroutines.delay((pcm.size * 1000L / SAMPLE_RATE) + 50)
        audioTrack.release()
      } catch (_: Exception) {}
    }
  }

  fun playSlash(comboStep: Int) {
    val durationMs = 120
    val numSamples = (SAMPLE_RATE * durationMs) / 1000
    val buffer = ShortArray(numSamples)
    val baseFreq = when (comboStep) {
      1 -> 350.0
      2 -> 460.0
      else -> 620.0
    }

    for (i in 0 until numSamples) {
      val t = i.toDouble() / SAMPLE_RATE
      val progress = i.toDouble() / numSamples
      val freq = baseFreq + (1.0 - progress) * 200.0
      val env = (1.0 - progress) * (1.0 - exp(-progress * 20.0))
      val sample = sin(2.0 * PI * freq * t) * env * sfxVolume
      buffer[i] = (sample * 16000).toInt().coerceIn(-32767, 32767).toShort()
    }
    playBuffer(buffer)
  }

  fun playHit() {
    val durationMs = 90
    val numSamples = (SAMPLE_RATE * durationMs) / 1000
    val buffer = ShortArray(numSamples)

    for (i in 0 until numSamples) {
      val t = i.toDouble() / SAMPLE_RATE
      val progress = i.toDouble() / numSamples
      val freq = 160.0 * (1.0 - progress * 0.7)
      val env = (1.0 - progress) * (1.0 - progress)
      val sample = (sin(2.0 * PI * freq * t) + (Math.random() * 0.4 - 0.2)) * env * sfxVolume
      buffer[i] = (sample * 24000).toInt().coerceIn(-32767, 32767).toShort()
    }
    playBuffer(buffer)
  }

  fun playDash() {
    val durationMs = 150
    val numSamples = (SAMPLE_RATE * durationMs) / 1000
    val buffer = ShortArray(numSamples)

    for (i in 0 until numSamples) {
      val progress = i.toDouble() / numSamples
      val env = sin(progress * PI)
      val noise = (Math.random() * 2.0 - 1.0)
      val sample = noise * env * 0.4 * sfxVolume
      buffer[i] = (sample * 20000).toInt().coerceIn(-32767, 32767).toShort()
    }
    playBuffer(buffer)
  }

  fun playTechnique() {
    val durationMs = 260
    val numSamples = (SAMPLE_RATE * durationMs) / 1000
    val buffer = ShortArray(numSamples)

    for (i in 0 until numSamples) {
      val t = i.toDouble() / SAMPLE_RATE
      val progress = i.toDouble() / numSamples
      val env = (1.0 - progress)
      val freq1 = 523.25 + progress * 200.0 // C5
      val freq2 = 659.25 + progress * 250.0 // E5
      val freq3 = 783.99 + progress * 300.0 // G5
      val sample = (sin(2.0 * PI * freq1 * t) * 0.4 + sin(2.0 * PI * freq2 * t) * 0.3 + sin(2.0 * PI * freq3 * t) * 0.3) * env * sfxVolume
      buffer[i] = (sample * 20000).toInt().coerceIn(-32767, 32767).toShort()
    }
    playBuffer(buffer)
  }

  fun playShadowTransform() {
    val durationMs = 450
    val numSamples = (SAMPLE_RATE * durationMs) / 1000
    val buffer = ShortArray(numSamples)

    for (i in 0 until numSamples) {
      val t = i.toDouble() / SAMPLE_RATE
      val progress = i.toDouble() / numSamples
      val env = sin(progress * PI)
      val freq = 90.0 - progress * 40.0 // Deep bass drop
      val rumble = (Math.random() * 0.3)
      val sample = (sin(2.0 * PI * freq * t) * 0.7 + rumble) * env * sfxVolume
      buffer[i] = (sample * 26000).toInt().coerceIn(-32767, 32767).toShort()
    }
    playBuffer(buffer)
  }

  fun playHeal() {
    val durationMs = 280
    val numSamples = (SAMPLE_RATE * durationMs) / 1000
    val buffer = ShortArray(numSamples)

    for (i in 0 until numSamples) {
      val t = i.toDouble() / SAMPLE_RATE
      val progress = i.toDouble() / numSamples
      val env = (1.0 - progress) * sin(progress * PI)
      val freq = 440.0 + progress * 440.0 // Rising harmonic
      val sample = sin(2.0 * PI * freq * t) * env * sfxVolume
      buffer[i] = (sample * 18000).toInt().coerceIn(-32767, 32767).toShort()
    }
    playBuffer(buffer)
  }

  fun playVictory() {
    val durationMs = 500
    val numSamples = (SAMPLE_RATE * durationMs) / 1000
    val buffer = ShortArray(numSamples)

    for (i in 0 until numSamples) {
      val t = i.toDouble() / SAMPLE_RATE
      val progress = i.toDouble() / numSamples
      val noteIndex = (progress * 4).toInt()
      val freq = when (noteIndex) {
        0 -> 523.25 // C5
        1 -> 659.25 // E5
        2 -> 783.99 // G5
        else -> 1046.50 // C6
      }
      val env = 1.0 - (progress % 0.25) * 2.0
      val sample = sin(2.0 * PI * freq * t) * env * sfxVolume * 0.6
      buffer[i] = (sample * 22000).toInt().coerceIn(-32767, 32767).toShort()
    }
    playBuffer(buffer)
  }

  fun playDefeat() {
    val durationMs = 400
    val numSamples = (SAMPLE_RATE * durationMs) / 1000
    val buffer = ShortArray(numSamples)

    for (i in 0 until numSamples) {
      val t = i.toDouble() / SAMPLE_RATE
      val progress = i.toDouble() / numSamples
      val freq = 300.0 - progress * 180.0
      val env = 1.0 - progress
      val sample = sin(2.0 * PI * freq * t) * env * sfxVolume * 0.5
      buffer[i] = (sample * 20000).toInt().coerceIn(-32767, 32767).toShort()
    }
    playBuffer(buffer)
  }

  fun playClick() {
    val durationMs = 40
    val numSamples = (SAMPLE_RATE * durationMs) / 1000
    val buffer = ShortArray(numSamples)

    for (i in 0 until numSamples) {
      val t = i.toDouble() / SAMPLE_RATE
      val progress = i.toDouble() / numSamples
      val sample = sin(2.0 * PI * 880.0 * t) * (1.0 - progress) * sfxVolume * 0.3
      buffer[i] = (sample * 14000).toInt().coerceIn(-32767, 32767).toShort()
    }
    playBuffer(buffer)
  }
}
