package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.GameSoundPlayer
import com.example.model.GuardianFusion
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.BorderSlate
import com.example.ui.theme.BrightGold
import com.example.ui.theme.DarkGold
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.SurfaceHighlight
import com.example.ui.theme.SurfacePanel
import com.example.ui.theme.TextDim
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSoft
import kotlinx.coroutines.delay

@Composable
fun FusionRevealScreen(
  fusion: GuardianFusion,
  onProceedToCampaign: () -> Unit
) {
  var beat by remember { mutableIntStateOf(1) }
  var timerSeconds by remember { mutableFloatStateOf(0f) }

  val pulseAnim = remember { Animatable(1f) }
  val rotationAnim = remember { Animatable(0f) }

  LaunchedEffect(Unit) {
    rotationAnim.animateTo(
      targetValue = 360f,
      animationSpec = infiniteRepeatable(
        animation = tween(12000, easing = LinearEasing),
        repeatMode = RepeatMode.Restart
      )
    )
  }

  // 5-second reveal sequence
  LaunchedEffect(Unit) {
    // Beat 1: Animal silhouette & mask
    beat = 1
    GameSoundPlayer.playSlash(1)
    delay(1600)

    // Beat 2: Constellation armor
    beat = 2
    GameSoundPlayer.playSlash(2)
    delay(1600)

    // Beat 3: Luminous sign glyph overhead
    beat = 3
    GameSoundPlayer.playTechnique()
    delay(1800)

    // Beat 4: Final assembled title
    beat = 4
    GameSoundPlayer.playVictory()
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(ObsidianBg)
      .testTag("fusion_reveal_screen"),
    contentAlignment = Alignment.Center
  ) {
    // Rotating celestial background ring
    Box(
      modifier = Modifier
        .size(360.dp)
        .rotate(rotationAnim.value)
        .border(1.dp, BorderSlate.copy(alpha = 0.3f), CircleShape)
    )

    // Center Golden Glow
    Box(
      modifier = Modifier
        .size(260.dp)
        .blur(60.dp)
        .background(AntiqueGold.copy(alpha = if (beat >= 3) 0.25f else 0.12f), CircleShape)
    )

    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      // Top status
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(top = 16.dp)
      ) {
        Text(
          text = when (beat) {
            1 -> "BEAT I • AWAKENING ANIMAL SILHOUETTE"
            2 -> "BEAT II • FORGING CONSTELLATION ARMOR"
            3 -> "BEAT III • IGNITING LUMINOUS SIGN GLYPH"
            else -> "GUARDIAN PRIME ASSEMBLED"
          },
          color = AntiqueGold,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = "${fusion.zodiac.monthName} × Year ${fusion.birthYear}",
          color = TextMuted,
          fontSize = 13.sp
        )
      }

      // Central Guardian Assembly Visual
      Box(
        modifier = Modifier
          .size(280.dp),
        contentAlignment = Alignment.Center
      ) {
        // Celestial ground ring
        Box(
          modifier = Modifier
            .align(Alignment.BottomCenter)
            .size(width = 200.dp, height = 24.dp)
            .blur(16.dp)
            .background(AntiqueGold.copy(alpha = 0.4f))
        )

        // Beat 1: Animal Silhouette Base
        if (beat >= 1) {
          Box(
            modifier = Modifier
              .size(160.dp)
              .clip(RoundedCornerShape(32.dp))
              .background(
                Brush.verticalGradient(
                  listOf(SurfaceElevated, SurfaceDark)
                )
              )
              .border(
                width = 2.dp,
                brush = Brush.linearGradient(
                  listOf(BrightGold, DarkGold)
                ),
                shape = RoundedCornerShape(32.dp)
              ),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = fusion.animal.emoji,
              fontSize = 72.sp,
              modifier = Modifier.scale(if (beat >= 2) 1f else 1.1f)
            )
          }
        }

        // Beat 2: Constellation Armor Aura & Border Ring
        if (beat >= 2) {
          Box(
            modifier = Modifier
              .size(200.dp)
              .border(1.5.dp, BrightGold.copy(alpha = 0.6f), CircleShape)
          )
        }

        // Beat 3: Luminous Sign Glyph Overhead
        if (beat >= 3) {
          Box(
            modifier = Modifier
              .align(Alignment.TopCenter)
              .size(68.dp)
              .clip(CircleShape)
              .background(SurfacePanel)
              .border(2.dp, BrightGold, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = fusion.zodiac.glyph,
              color = BrightGold,
              fontSize = 36.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }

      // Bottom Title & Details
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
      ) {
        AnimatedVisibility(
          visible = beat >= 4,
          enter = fadeIn(tween(400))
        ) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
          ) {
            Text(
              text = "${fusion.comboTitle} Guardian",
              color = TextPrimary,
              fontSize = 26.sp,
              fontFamily = FontFamily.Serif,
              fontStyle = FontStyle.Italic,
              fontWeight = FontWeight.Bold,
              textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
              text = "Active: ${fusion.zodiac.techniqueName} • Trait: ${fusion.animal.traitName}",
              color = AntiqueGold,
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold,
              textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
              onClick = {
                GameSoundPlayer.playTechnique()
                onProceedToCampaign()
              },
              modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("begin_quest_button"),
              shape = RoundedCornerShape(27.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = AntiqueGold,
                contentColor = ObsidianBg
              )
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
              ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = ObsidianBg)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "ENTER REALM GATES",
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Black,
                  letterSpacing = 1.sp
                )
              }
            }
          }
        }

        if (beat < 4) {
          Text(
            text = "Forging celestial bond...",
            color = TextDim,
            fontSize = 13.sp,
            fontStyle = FontStyle.Italic
          )
          Spacer(modifier = Modifier.height(24.dp))
        }
      }
    }
  }
}
