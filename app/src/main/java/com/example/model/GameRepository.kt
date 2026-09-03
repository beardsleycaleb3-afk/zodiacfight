package com.example.model

import android.content.Context
import android.content.SharedPreferences

class GameRepository(context: Context) {
  private val prefs: SharedPreferences =
    context.getSharedPreferences("zodiac_guardian_prefs", Context.MODE_PRIVATE)

  var playerFusion: GuardianFusion? = null
    private set

  var currentRealmIndex: Int = 0
    private set

  var currentGateIndex: Int = 1
    private set

  var shardsCount: Int = 0
    private set

  private val clearedGates = mutableSetOf<String>()
  private val unlockedCodex = mutableSetOf<String>()

  init {
    loadSavedData()
  }

  private fun loadSavedData() {
    val month = prefs.getInt("birth_month", -1)
    val year = prefs.getInt("birth_year", -1)
    if (month in 1..12 && year in 1900..2099) {
      val zodiac = MonthZodiac.fromMonth(month)
      val animal = ChineseAnimal.fromYear(year)
      playerFusion = GuardianFusion(zodiac, animal, month, year)
    }

    currentRealmIndex = prefs.getInt("current_realm", 0)
    currentGateIndex = prefs.getInt("current_gate", 1)
    shardsCount = prefs.getInt("shards_count", 0)

    val gatesSet = prefs.getStringSet("cleared_gates", emptySet()) ?: emptySet()
    clearedGates.addAll(gatesSet)

    val codexSet = prefs.getStringSet("unlocked_codex", emptySet()) ?: emptySet()
    unlockedCodex.addAll(codexSet)

    // Player's own fusion is automatically unlocked in codex
    playerFusion?.let {
      unlockCodex(it.zodiac, it.animal)
    }
  }

  fun setPlayer(month: Int, year: Int): GuardianFusion {
    val zodiac = MonthZodiac.fromMonth(month)
    val animal = ChineseAnimal.fromYear(year)
    val fusion = GuardianFusion(zodiac, animal, month, year)
    playerFusion = fusion

    // Realm starts with player's animal realm
    currentRealmIndex = animal.index
    currentGateIndex = 1

    prefs.edit()
      .putInt("birth_month", month)
      .putInt("birth_year", year)
      .putInt("current_realm", currentRealmIndex)
      .putInt("current_gate", currentGateIndex)
      .apply()

    unlockCodex(zodiac, animal)
    return fusion
  }

  fun setRealmAndGate(realmIndex: Int, gateIndex: Int) {
    currentRealmIndex = realmIndex.coerceIn(0, 11)
    currentGateIndex = gateIndex.coerceIn(1, 13)
    prefs.edit()
      .putInt("current_realm", currentRealmIndex)
      .putInt("current_gate", currentGateIndex)
      .apply()
  }

  fun markGateCleared(realmIndex: Int, gateIndex: Int, zodiac: MonthZodiac, animal: ChineseAnimal) {
    val key = "R${realmIndex}_G${gateIndex}"
    clearedGates.add(key)
    shardsCount++
    unlockCodex(zodiac, animal)

    // Advance to next gate
    if (gateIndex < 13) {
      currentGateIndex = gateIndex + 1
    } else {
      // Advance to next realm
      currentRealmIndex = (realmIndex + 1) % 12
      currentGateIndex = 1
    }

    prefs.edit()
      .putStringSet("cleared_gates", clearedGates)
      .putStringSet("unlocked_codex", unlockedCodex)
      .putInt("shards_count", shardsCount)
      .putInt("current_realm", currentRealmIndex)
      .putInt("current_gate", currentGateIndex)
      .apply()
  }

  fun isGateCleared(realmIndex: Int, gateIndex: Int): Boolean {
    return clearedGates.contains("R${realmIndex}_G${gateIndex}")
  }

  fun unlockCodex(zodiac: MonthZodiac, animal: ChineseAnimal) {
    val key = "${zodiac.name}_${animal.name}"
    unlockedCodex.add(key)
    prefs.edit().putStringSet("unlocked_codex", unlockedCodex).apply()
  }

  fun isCodexUnlocked(zodiac: MonthZodiac, animal: ChineseAnimal): Boolean {
    return unlockedCodex.contains("${zodiac.name}_${animal.name}")
  }

  fun totalCodexUnlocked(): Int = unlockedCodex.size

  fun totalGatesCleared(): Int = clearedGates.size

  fun getRealmDamageBonus(): Float {
    val bonusTiers = shardsCount / 3
    return (bonusTiers * 0.05f).coerceAtMost(0.50f)
  }

  fun resetAll() {
    prefs.edit().clear().apply()
    clearedGates.clear()
    unlockedCodex.clear()
    shardsCount = 0
    currentRealmIndex = 0
    currentGateIndex = 1
    playerFusion = null
  }
}
