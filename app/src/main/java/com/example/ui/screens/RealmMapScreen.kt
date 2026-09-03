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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
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
import com.example.model.EnemyFusion
import com.example.model.GuardianFusion
import com.example.model.MonthZodiac
import com.example.ui.theme.AmberGold
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.BorderLight
import com.example.ui.theme.BorderSlate
import com.example.ui.theme.BrightGold
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.ShadowViolet
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.SurfaceHighlight
import com.example.ui.theme.SurfacePanel
import com.example.ui.theme.TextDim
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSoft

@Composable
fun RealmMapScreen(
  playerFusion: GuardianFusion,
  currentRealmIndex: Int,
  currentGateIndex: Int,
  shardsCount: Int,
  unlockedCodexCount: Int,
  isGateCleared: (realmIndex: Int, gateIndex: Int) -> Boolean,
  onSelectGate: (realmIndex: Int, gateIndex: Int, enemyFusion: EnemyFusion) -> Unit,
  onOpenCodex: () -> Unit,
  onReforgeCharacter: () -> Unit
) {
  var selectedRealm by remember { mutableIntStateOf(currentRealmIndex) }
  val realmAnimal = ChineseAnimal.entries[selectedRealm]

  val bonusPercent = (shardsCount / 3) * 5

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(ObsidianBg)
      .testTag("realm_map_screen")
  ) {
    // Ambient gold glow
    Box(
      modifier = Modifier
        .align(Alignment.TopCenter)
        .size(340.dp)
        .blur(80.dp)
        .background(AntiqueGold.copy(alpha = 0.06f), CircleShape)
    )

    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp, vertical = 18.dp)
    ) {
      // Top Header: Player Guardian & Meta Nav
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Box(
            modifier = Modifier
              .size(42.dp)
              .clip(CircleShape)
              .background(SurfacePanel)
              .border(1.5.dp, AntiqueGold, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Text(playerFusion.animal.emoji, fontSize = 22.sp)
          }

          Column {
            Text(
              text = playerFusion.comboTitle,
              color = TextPrimary,
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "Realm Bonus: +$bonusPercent% DMG",
              color = AntiqueGold,
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold
            )
          }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          // Codex Pill
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(16.dp))
              .background(SurfaceElevated)
              .border(1.dp, BorderSlate, RoundedCornerShape(16.dp))
              .clickable {
                GameSoundPlayer.playClick()
                onOpenCodex()
              }
              .padding(horizontal = 10.dp, vertical = 6.dp)
              .testTag("map_codex_button")
          ) {
            Text(
              text = "Codex $unlockedCodexCount/144",
              color = AmberGold,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold
            )
          }

          // Reforge / Change Birthdate
          IconButton(
            onClick = {
              GameSoundPlayer.playClick()
              onReforgeCharacter()
            },
            modifier = Modifier.size(34.dp).testTag("reforge_character_button")
          ) {
            Icon(Icons.Default.Refresh, contentDescription = "Change Birthdate", tint = TextMuted)
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Realm Selector (Horizontal Scroll of 12 Animals)
      Text(
        text = "YEAR REALMS (12 TOTAL)",
        color = TextDim,
        fontSize = 10.sp,
        fontWeight = FontWeight.Black,
        letterSpacing = 1.sp
      )

      Spacer(modifier = Modifier.height(6.dp))

      val realmScroll = rememberScrollState()
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(realmScroll),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        ChineseAnimal.entries.forEach { animal ->
          val isSelected = animal.index == selectedRealm
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
                selectedRealm = animal.index
                GameSoundPlayer.playClick()
              }
              .padding(horizontal = 12.dp, vertical = 8.dp)
              .testTag("realm_btn_${animal.index}")
          ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
              Text(animal.emoji, fontSize = 16.sp)
              Text(
                text = animal.animalName,
                color = if (isSelected) TextPrimary else TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Current Realm Banner Card
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(18.dp))
          .background(
            Brush.horizontalGradient(
              listOf(SurfacePanel, SurfaceDark)
            )
          )
          .border(1.dp, AntiqueGold.copy(alpha = 0.4f), RoundedCornerShape(18.dp))
          .padding(14.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "REALM ${selectedRealm + 1} OF 12",
              color = AntiqueGold,
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.5.sp
            )
            Text(
              text = "Realm of the ${realmAnimal.animalName}",
              color = TextPrimary,
              fontSize = 18.sp,
              fontFamily = FontFamily.Serif,
              fontStyle = FontStyle.Italic,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "Trait: ${realmAnimal.traitName} — ${realmAnimal.traitDescription}",
              color = TextMuted,
              fontSize = 11.sp,
              modifier = Modifier.padding(top = 2.dp)
            )
          }

          Text(
            text = realmAnimal.emoji,
            fontSize = 38.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Month Gates Grid (12 Gates + Chimera Boss)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "MONTH GATES",
          color = TextDim,
          fontSize = 10.sp,
          fontWeight = FontWeight.Black,
          letterSpacing = 1.sp
        )
        Text(
          text = "Normal + Shadow Phase per Gate",
          color = TextMuted,
          fontSize = 10.sp
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.weight(1f)
      ) {
        // Gates 1 through 12
        items(12) { index ->
          val gateNum = index + 1
          val gateZodiac = MonthZodiac.entries[index]
          val cleared = isGateCleared(selectedRealm, gateNum)
          val isCurrent = selectedRealm == currentRealmIndex && gateNum == currentGateIndex
          val isRecovery = gateNum % 4 == 0

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(16.dp))
              .background(if (isCurrent) SurfaceHighlight else SurfacePanel)
              .border(
                width = if (isCurrent) 2.dp else 1.dp,
                color = when {
                  isCurrent -> BrightGold
                  cleared -> AntiqueGold.copy(alpha = 0.6f)
                  else -> BorderSlate
                },
                shape = RoundedCornerShape(16.dp)
              )
              .clickable {
                GameSoundPlayer.playClick()
                val enemy = EnemyFusion(
                  zodiac = gateZodiac,
                  animal = realmAnimal,
                  realmIndex = selectedRealm,
                  gateIndex = gateNum
                )
                onSelectGate(selectedRealm, gateNum, enemy)
              }
              .padding(10.dp)
              .testTag("gate_card_$gateNum")
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
                  text = "GATE $gateNum",
                  color = if (cleared) BrightGold else TextDim,
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold
                )
                if (cleared) {
                  Icon(
                    Icons.Default.Check,
                    contentDescription = "Cleared",
                    tint = BrightGold,
                    modifier = Modifier.size(12.dp)
                  )
                }
              }

              Spacer(modifier = Modifier.height(4.dp))

              Text(
                text = gateZodiac.glyph,
                color = gateZodiac.accentColor,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
              )

              Text(
                text = "${gateZodiac.signName}–${realmAnimal.animalName}",
                color = TextSoft,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                maxLines = 1
              )

              if (isRecovery) {
                Text(
                  text = "Recovery Beat",
                  color = AmberGold,
                  fontSize = 8.sp,
                  modifier = Modifier.padding(top = 2.dp)
                )
              }
            }
          }
        }

        // Gate 13: Year Chimera Boss
        item {
          val chimeraCleared = isGateCleared(selectedRealm, 13)
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(16.dp))
              .background(SurfaceDark)
              .border(
                width = 1.5.dp,
                color = if (chimeraCleared) BrightGold else ShadowViolet,
                shape = RoundedCornerShape(16.dp)
              )
              .clickable {
                GameSoundPlayer.playTechnique()
                val enemy = EnemyFusion(
                  zodiac = MonthZodiac.LEO,
                  animal = realmAnimal,
                  realmIndex = selectedRealm,
                  gateIndex = 13,
                  isChimeraBoss = true
                )
                onSelectGate(selectedRealm, 13, enemy)
              }
              .padding(10.dp)
              .testTag("gate_chimera_boss")
          ) {
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              modifier = Modifier.fillMaxWidth()
            ) {
              Text(
                text = "BOSS GATE",
                color = ShadowViolet,
                fontSize = 9.sp,
                fontWeight = FontWeight.Black
              )
              Text(
                text = "👑",
                fontSize = 22.sp
              )
              Text(
                text = "Year Chimera",
                color = BrightGold,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }
    }
  }
}
