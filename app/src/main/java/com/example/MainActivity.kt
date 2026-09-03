package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.model.ChineseAnimal
import com.example.model.EnemyFusion
import com.example.model.GameRepository
import com.example.model.GuardianFusion
import com.example.model.MonthZodiac
import com.example.ui.screens.ArenaBattleScreen
import com.example.ui.screens.CharacterCreationScreen
import com.example.ui.screens.CodexScreen
import com.example.ui.screens.FusionRevealScreen
import com.example.ui.screens.RealmMapScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.ObsidianBg

enum class ScreenState {
  CREATION,
  FUSION_REVEAL,
  REALM_MAP,
  BATTLE,
  CODEX
}

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBg)
            .safeDrawingPadding()
        ) {
          ZodiacFusionApp()
        }
      }
    }
  }
}

@Composable
fun ZodiacFusionApp() {
  val context = LocalContext.current
  val repository = remember { GameRepository(context) }

  var currentScreen by remember {
    mutableStateOf(
      if (repository.playerFusion != null) ScreenState.REALM_MAP else ScreenState.CREATION
    )
  }

  var playerFusion by remember { mutableStateOf(repository.playerFusion) }

  var activeRealmIndex by remember { mutableIntStateOf(repository.currentRealmIndex) }
  var activeGateIndex by remember { mutableIntStateOf(repository.currentGateIndex) }
  var activeEnemyFusion by remember {
    mutableStateOf(
      EnemyFusion(
        zodiac = MonthZodiac.fromMonth(1),
        animal = ChineseAnimal.fromYear(2020),
        realmIndex = 0,
        gateIndex = 1
      )
    )
  }

  var shardsCount by remember { mutableIntStateOf(repository.shardsCount) }
  var unlockedCodexCount by remember { mutableIntStateOf(repository.totalCodexUnlocked()) }

  // Handle system back navigation
  BackHandler(enabled = currentScreen != ScreenState.CREATION && currentScreen != ScreenState.REALM_MAP) {
    when (currentScreen) {
      ScreenState.CODEX -> currentScreen = ScreenState.REALM_MAP
      ScreenState.BATTLE -> currentScreen = ScreenState.REALM_MAP
      ScreenState.FUSION_REVEAL -> currentScreen = ScreenState.REALM_MAP
      else -> {}
    }
  }

  when (currentScreen) {
    ScreenState.CREATION -> {
      CharacterCreationScreen(
        initialMonth = playerFusion?.birthMonth ?: 8,
        initialYear = playerFusion?.birthYear ?: 2000,
        onAwakenGuardian = { month, year ->
          val newFusion = repository.setPlayer(month, year)
          playerFusion = newFusion
          activeRealmIndex = repository.currentRealmIndex
          activeGateIndex = repository.currentGateIndex
          shardsCount = repository.shardsCount
          unlockedCodexCount = repository.totalCodexUnlocked()
          currentScreen = ScreenState.FUSION_REVEAL
        }
      )
    }

    ScreenState.FUSION_REVEAL -> {
      playerFusion?.let { fusion ->
        FusionRevealScreen(
          fusion = fusion,
          onProceedToCampaign = {
            currentScreen = ScreenState.REALM_MAP
          }
        )
      } ?: run {
        currentScreen = ScreenState.CREATION
      }
    }

    ScreenState.REALM_MAP -> {
      playerFusion?.let { fusion ->
        RealmMapScreen(
          playerFusion = fusion,
          currentRealmIndex = activeRealmIndex,
          currentGateIndex = activeGateIndex,
          shardsCount = shardsCount,
          unlockedCodexCount = unlockedCodexCount,
          isGateCleared = { r, g -> repository.isGateCleared(r, g) },
          onSelectGate = { realm, gate, enemy ->
            activeRealmIndex = realm
            activeGateIndex = gate
            activeEnemyFusion = enemy
            currentScreen = ScreenState.BATTLE
          },
          onOpenCodex = {
            currentScreen = ScreenState.CODEX
          },
          onReforgeCharacter = {
            currentScreen = ScreenState.CREATION
          }
        )
      } ?: run {
        currentScreen = ScreenState.CREATION
      }
    }

    ScreenState.BATTLE -> {
      playerFusion?.let { fusion ->
        ArenaBattleScreen(
          playerFusion = fusion,
          enemyFusion = activeEnemyFusion,
          realmIndex = activeRealmIndex,
          gateIndex = activeGateIndex,
          shardsCount = shardsCount,
          unlockedCodexCount = unlockedCodexCount,
          realmDamageBonus = repository.getRealmDamageBonus(),
          onBattleVictory = {
            repository.markGateCleared(
              activeRealmIndex,
              activeGateIndex,
              activeEnemyFusion.zodiac,
              activeEnemyFusion.animal
            )
            shardsCount = repository.shardsCount
            unlockedCodexCount = repository.totalCodexUnlocked()
            activeRealmIndex = repository.currentRealmIndex
            activeGateIndex = repository.currentGateIndex

            // Automatically set up next gate
            val nextZodiac = MonthZodiac.entries.getOrElse(activeGateIndex - 1) { MonthZodiac.LEO }
            val nextAnimal = ChineseAnimal.entries[activeRealmIndex]
            activeEnemyFusion = EnemyFusion(
              zodiac = nextZodiac,
              animal = nextAnimal,
              realmIndex = activeRealmIndex,
              gateIndex = activeGateIndex,
              isChimeraBoss = activeGateIndex == 13
            )
          },
          onOpenCodex = {
            currentScreen = ScreenState.CODEX
          },
          onReturnToMap = {
            currentScreen = ScreenState.REALM_MAP
          }
        )
      } ?: run {
        currentScreen = ScreenState.CREATION
      }
    }

    ScreenState.CODEX -> {
      CodexScreen(
        isCodexUnlocked = { z, a -> repository.isCodexUnlocked(z, a) },
        onClose = {
          currentScreen = ScreenState.REALM_MAP
        }
      )
    }
  }
}
