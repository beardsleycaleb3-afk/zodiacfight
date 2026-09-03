package com.example.ui.screens

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
import com.example.ui.theme.AmberGold
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
fun CodexScreen(
  isCodexUnlocked: (zodiac: MonthZodiac, animal: ChineseAnimal) -> Boolean,
  onClose: () -> Unit
) {
  var selectedAnimalFilter by remember { mutableStateOf<ChineseAnimal?>(null) }
  var selectedEntry by remember { mutableStateOf<Pair<MonthZodiac, ChineseAnimal>?>(null) }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(ObsidianBg)
      .testTag("codex_screen")
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
    ) {
      // Top Navigation
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(
            onClick = {
              GameSoundPlayer.playClick()
              onClose()
            },
            modifier = Modifier.testTag("codex_back_button")
          ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextSoft)
          }

          Spacer(modifier = Modifier.width(4.dp))

          Column {
            Text(
              text = "CELESTIAL CODEX",
              color = AntiqueGold,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 2.sp
            )
            Text(
              text = "12×12 Fusion Matrix",
              color = TextPrimary,
              fontSize = 18.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }

        // Total Count
        var count = 0
        MonthZodiac.entries.forEach { z ->
          ChineseAnimal.entries.forEach { a ->
            if (isCodexUnlocked(z, a)) count++
          }
        }
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(SurfacePanel)
            .border(1.dp, BorderSlate, RoundedCornerShape(14.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
          Text(
            text = "$count / 144 Discovered",
            color = AmberGold,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Animal Filter Carousel
      val filterScroll = rememberScrollState()
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(filterScroll),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        // "All" filter chip
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (selectedAnimalFilter == null) SurfaceHighlight else SurfacePanel)
            .border(1.dp, if (selectedAnimalFilter == null) BrightGold else BorderSlate, RoundedCornerShape(12.dp))
            .clickable {
              selectedAnimalFilter = null
              GameSoundPlayer.playClick()
            }
            .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
          Text(
            text = "All Animals",
            color = if (selectedAnimalFilter == null) BrightGold else TextMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )
        }

        ChineseAnimal.entries.forEach { animal ->
          val isSel = selectedAnimalFilter == animal
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .background(if (isSel) SurfaceHighlight else SurfacePanel)
              .border(1.dp, if (isSel) BrightGold else BorderSlate, RoundedCornerShape(12.dp))
              .clickable {
                selectedAnimalFilter = animal
                GameSoundPlayer.playClick()
              }
              .padding(horizontal = 10.dp, vertical = 6.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
              Text(animal.emoji, fontSize = 14.sp)
              Text(
                text = animal.animalName,
                color = if (isSel) TextPrimary else TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // 12x12 Matrix Grid
      val displayedPairs = remember(selectedAnimalFilter) {
        val list = mutableListOf<Pair<MonthZodiac, ChineseAnimal>>()
        val animals = if (selectedAnimalFilter != null) listOf(selectedAnimalFilter!!) else ChineseAnimal.entries
        animals.forEach { a ->
          MonthZodiac.entries.forEach { z ->
            list.add(Pair(z, a))
          }
        }
        list
      }

      LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.weight(1f)
      ) {
        items(displayedPairs.size) { i ->
          val (zodiac, animal) = displayedPairs[i]
          val unlocked = isCodexUnlocked(zodiac, animal)

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(14.dp))
              .background(if (unlocked) SurfacePanel else SurfaceDark)
              .border(
                width = 1.dp,
                color = if (unlocked) AntiqueGold.copy(alpha = 0.5f) else BorderSlate.copy(alpha = 0.3f),
                shape = RoundedCornerShape(14.dp)
              )
              .clickable {
                GameSoundPlayer.playClick()
                selectedEntry = Pair(zodiac, animal)
              }
              .padding(8.dp),
            contentAlignment = Alignment.Center
          ) {
            if (unlocked) {
              Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
              ) {
                Text(animal.emoji, fontSize = 22.sp)
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = zodiac.glyph,
                  color = zodiac.accentColor,
                  fontSize = 18.sp,
                  fontWeight = FontWeight.Bold
                )
                Text(
                  text = "${zodiac.signName.take(3)}-${animal.animalName.take(3)}",
                  color = TextSoft,
                  fontSize = 9.sp,
                  fontWeight = FontWeight.SemiBold,
                  maxLines = 1
                )
              }
            } else {
              // Mysterious Silhouette
              Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
              ) {
                Icon(
                  Icons.Default.Lock,
                  contentDescription = "Locked",
                  tint = TextDim,
                  modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = "???",
                  color = TextDim,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }
        }
      }
    }

    // Detail Inspector Modal Card
    selectedEntry?.let { (zodiac, animal) ->
      val isUnlocked = isCodexUnlocked(zodiac, animal)
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(ObsidianBg.copy(alpha = 0.88f))
          .clickable { selectedEntry = null }
          .padding(24.dp),
        contentAlignment = Alignment.Center
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
              Brush.verticalGradient(listOf(SurfacePanel, SurfaceDark))
            )
            .border(2.dp, if (isUnlocked) BrightGold else BorderSlate, RoundedCornerShape(24.dp))
            .padding(20.dp)
            .clickable(enabled = false) {}
        ) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = if (isUnlocked) "CODEX ENTRY INSCRIPTION" else "UNDISCOVERED COMBINATION",
                color = if (isUnlocked) AntiqueGold else TextDim,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
              )
              IconButton(onClick = { selectedEntry = null }, modifier = Modifier.size(28.dp)) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
              }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Avatar Card
            Box(
              modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
                .background(SurfaceElevated)
                .border(2.dp, if (isUnlocked) BrightGold else BorderLight, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              if (isUnlocked) {
                Text(animal.emoji, fontSize = 44.sp)
                Box(
                  modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(SurfacePanel)
                    .border(1.dp, BrightGold, CircleShape),
                  contentAlignment = Alignment.Center
                ) {
                  Text(zodiac.glyph, color = BrightGold, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
              } else {
                Icon(Icons.Default.Lock, contentDescription = "Locked", tint = TextDim, modifier = Modifier.size(36.dp))
              }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
              text = if (isUnlocked) "${zodiac.signName}–${animal.animalName} Guardian" else "Unknown Fusion",
              color = TextPrimary,
              fontSize = 20.sp,
              fontFamily = FontFamily.Serif,
              fontStyle = FontStyle.Italic,
              fontWeight = FontWeight.Bold
            )

            if (isUnlocked) {
              Spacer(modifier = Modifier.height(14.dp))

              // Technique details
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(12.dp))
                  .background(SurfaceElevated)
                  .border(1.dp, BorderSlate, RoundedCornerShape(12.dp))
                  .padding(12.dp)
              ) {
                Column {
                  Text(
                    text = "SIGN TECHNIQUE: ${zodiac.techniqueName} (${zodiac.glyph})",
                    color = AntiqueGold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                  )
                  Text(
                    text = zodiac.techniqueDescription,
                    color = TextSoft,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(top = 2.dp)
                  )
                }
              }

              Spacer(modifier = Modifier.height(8.dp))

              // Animal trait details
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(12.dp))
                  .background(SurfaceElevated)
                  .border(1.dp, BorderSlate, RoundedCornerShape(12.dp))
                  .padding(12.dp)
              ) {
                Column {
                  Text(
                    text = "ANIMAL TRAIT: ${animal.traitName} (${animal.emoji})",
                    color = AntiqueGold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                  )
                  Text(
                    text = animal.traitDescription,
                    color = TextSoft,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(top = 2.dp)
                  )
                }
              }
            } else {
              Text(
                text = "Battle and defeat both Normal and Shadow forms of this guardian in Realm ${animal.index + 1} (Gate ${zodiac.monthNumber}) to record this fusion in your codex.",
                color = TextMuted,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 16.dp)
              )
            }
          }
        }
      }
    }
  }
}
