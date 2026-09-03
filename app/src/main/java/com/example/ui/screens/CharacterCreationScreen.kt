package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
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
import com.example.model.ChineseAnimal
import com.example.model.MonthZodiac
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.BorderLight
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

@Composable
fun CharacterCreationScreen(
  initialMonth: Int = 8, // August / Leo
  initialYear: Int = 2000, // Dragon
  onAwakenGuardian: (month: Int, year: Int) -> Unit
) {
  var selectedMonth by remember { mutableIntStateOf(initialMonth) }
  var selectedYear by remember { mutableIntStateOf(initialYear) }

  val derivedZodiac = MonthZodiac.fromMonth(selectedMonth)
  val derivedAnimal = ChineseAnimal.fromYear(selectedYear)

  val scrollState = rememberScrollState()

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(ObsidianBg)
      .testTag("character_creation_screen")
  ) {
    // Ambient celestial background aura
    Box(
      modifier = Modifier
        .align(Alignment.TopCenter)
        .size(320.dp)
        .blur(80.dp)
        .background(AntiqueGold.copy(alpha = 0.08f), CircleShape)
    )

    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(scrollState)
        .padding(horizontal = 20.dp, vertical = 24.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Spacer(modifier = Modifier.height(16.dp))

      // Header Tag
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(20.dp))
          .background(SurfacePanel)
          .border(1.dp, BorderSlate, RoundedCornerShape(20.dp))
          .padding(horizontal = 14.dp, vertical = 6.dp)
      ) {
        Text(
          text = "CELESTIAL REFORGING",
          color = AntiqueGold,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 2.sp
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      Text(
        text = "Zodiac Fusion Guardian",
        color = TextPrimary,
        fontSize = 26.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.5.sp,
        textAlign = TextAlign.Center
      )

      Text(
        text = "Simplified month/year system rather than an exact astrological reading.",
        color = TextMuted,
        fontSize = 12.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
      )

      Spacer(modifier = Modifier.height(20.dp))

      // Live Fusion Preview Card (Artistic Flair Card)
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(24.dp))
          .background(
            Brush.verticalGradient(
              listOf(SurfacePanel, SurfaceDark)
            )
          )
          .border(1.dp, AntiqueGold.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
          .padding(20.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(
            text = "FUSED GUARDIAN IDENTITY",
            color = TextDim,
            fontSize = 10.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 2.sp
          )

          Spacer(modifier = Modifier.height(8.dp))

          // Luminous Glyph and Animal Silhouette Avatar
          Box(
            modifier = Modifier
              .size(100.dp)
              .clip(CircleShape)
              .background(
                Brush.radialGradient(
                  listOf(AntiqueGold.copy(alpha = 0.25f), Color.Transparent)
                )
              )
              .border(2.dp, BrightGold, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = derivedAnimal.emoji,
              fontSize = 44.sp
            )
            // Constellation glyph badge
            Box(
              modifier = Modifier
                .align(Alignment.TopEnd)
                .size(32.dp)
                .clip(CircleShape)
                .background(SurfaceElevated)
                .border(1.dp, AntiqueGold, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = derivedZodiac.glyph,
                color = BrightGold,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          Text(
            text = "${derivedZodiac.signName}–${derivedAnimal.animalName} Guardian",
            color = TextPrimary,
            fontSize = 20.sp,
            fontFamily = FontFamily.Serif,
            fontStyle = FontStyle.Italic,
            fontWeight = FontWeight.Bold
          )

          Spacer(modifier = Modifier.height(14.dp))

          // Trait & Technique Summary Badges
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            // Month Technique
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(14.dp))
                .background(SurfaceElevated)
                .border(1.dp, BorderSlate, RoundedCornerShape(14.dp))
                .padding(10.dp)
            ) {
              Column {
                Text(
                  text = "SIGN TECHNIQUE",
                  color = AntiqueGold,
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold
                )
                Text(
                  text = derivedZodiac.techniqueName,
                  color = TextSoft,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.SemiBold
                )
              }
            }

            // Animal Trait
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(14.dp))
                .background(SurfaceElevated)
                .border(1.dp, BorderSlate, RoundedCornerShape(14.dp))
                .padding(10.dp)
            ) {
              Column {
                Text(
                  text = "ANIMAL TRAIT",
                  color = AntiqueGold,
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold
                )
                Text(
                  text = derivedAnimal.traitName,
                  color = TextSoft,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.SemiBold
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      // Section 1: Birth Month Selection
      Column(modifier = Modifier.fillMaxWidth()) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "1. BIRTH MONTH (ZODIAC)",
            color = TextSoft,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
          )
          Text(
            text = "${derivedZodiac.monthName} • ${derivedZodiac.glyph} ${derivedZodiac.signName}",
            color = AntiqueGold,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Month Grid / Horizontal Selector
        val monthsScroll = rememberScrollState()
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(monthsScroll),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          MonthZodiac.entries.forEach { zodiac ->
            val isSelected = zodiac.monthNumber == selectedMonth
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .background(if (isSelected) SurfaceHighlight else SurfacePanel)
                .border(
                  width = if (isSelected) 2.dp else 1.dp,
                  color = if (isSelected) BrightGold else BorderSlate,
                  shape = RoundedCornerShape(14.dp)
                )
                .clickable {
                  selectedMonth = zodiac.monthNumber
                  GameSoundPlayer.playClick()
                }
                .padding(horizontal = 14.dp, vertical = 10.dp)
                .testTag("month_btn_${zodiac.monthNumber}")
            ) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                  text = zodiac.glyph,
                  color = if (isSelected) BrightGold else TextMuted,
                  fontSize = 20.sp
                )
                Text(
                  text = zodiac.monthName.take(3),
                  color = if (isSelected) TextPrimary else TextDim,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      // Section 2: Birth Year Selection (1900 - 2099)
      Column(modifier = Modifier.fillMaxWidth()) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "2. BIRTH YEAR (ANIMAL)",
            color = TextSoft,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
          )
          Text(
            text = "Year $selectedYear • ${derivedAnimal.emoji} ${derivedAnimal.animalName}",
            color = AntiqueGold,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Stepper & Quick presets
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfacePanel)
            .border(1.dp, BorderSlate, RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          IconButton(
            onClick = {
              if (selectedYear > 1900) {
                selectedYear -= 1
                GameSoundPlayer.playClick()
              }
            },
            modifier = Modifier.testTag("year_decrement_btn")
          ) {
            Icon(Icons.Default.Remove, contentDescription = "Decrease Year", tint = AntiqueGold)
          }

          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = "$selectedYear",
              color = TextPrimary,
              fontSize = 28.sp,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "Year of the ${derivedAnimal.animalName}",
              color = TextMuted,
              fontSize = 11.sp
            )
          }

          IconButton(
            onClick = {
              if (selectedYear < 2099) {
                selectedYear += 1
                GameSoundPlayer.playClick()
              }
            },
            modifier = Modifier.testTag("year_increment_btn")
          ) {
            Icon(Icons.Default.Add, contentDescription = "Increase Year", tint = AntiqueGold)
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Quick Decade Buttons
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          listOf(1990, 1996, 2000, 2004, 2010, 2020).forEach { yr ->
            val isCurrent = selectedYear == yr
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(if (isCurrent) SurfaceHighlight else SurfaceDark)
                .border(1.dp, if (isCurrent) BrightGold else BorderLight, RoundedCornerShape(8.dp))
                .clickable {
                  selectedYear = yr
                  GameSoundPlayer.playClick()
                }
                .padding(vertical = 6.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "$yr",
                color = if (isCurrent) BrightGold else TextDim,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(32.dp))

      // Awaken Button
      Button(
        onClick = {
          GameSoundPlayer.playTechnique()
          onAwakenGuardian(selectedMonth, selectedYear)
        },
        modifier = Modifier
          .fillMaxWidth()
          .height(56.dp)
          .testTag("awaken_guardian_button"),
        shape = RoundedCornerShape(28.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = AntiqueGold,
          contentColor = ObsidianBg
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center
        ) {
          Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = ObsidianBg)
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "AWAKEN MYTHIC GUARDIAN",
            fontSize = 15.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(30.dp))
    }
  }
}
