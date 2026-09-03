package com.example.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.ShadowViolet

enum class MonthZodiac(
  val monthNumber: Int,
  val monthName: String,
  val signName: String,
  val glyph: String,
  val element: String,
  val techniqueName: String,
  val techniqueDescription: String,
  val accentColor: Color
) {
  CAPRICORN(1, "January", "Capricorn", "♑", "Earth", "Stone Barrier Charge", "Surrounds guardian in terrestrial armor and charges forward with crushing force", Color(0xFF8D6E63)),
  AQUARIUS(2, "February", "Aquarius", "♒", "Air", "Cascade Water Fan", "Unleashes a broad tidal shockwave that knocks back and stuns foes", NeonCyan),
  PISCES(3, "March", "Pisces", "♓", "Water", "Orbiting Twin Bolts", "Spawns dual celestial hydro-orbs that spiral outwards piercing through targets", Color(0xFF4FC3F7)),
  ARIES(4, "April", "Aries", "♈", "Fire", "Piercing Horn Rush", "Dashes at high velocity with fiery ram horns delivering a critical piercing blow", CrimsonRed),
  TAURUS(5, "May", "Taurus", "♉", "Earth", "Radial Ground Slam", "Slams the celestial floor, creating radial stone pillars in a 360° tremor", Color(0xFFA1887F)),
  GEMINI(6, "June", "Gemini", "♊", "Air", "Echo Strike", "Calls forth a spectral mirror duplicate that repeats your weapon slash for double impact", Color(0xFFFFD54F)),
  CANCER(7, "July", "Cancer", "♋", "Water", "Tide Shell Counter", "Deploys a crystal carapaced ward that reflects incoming damage as aquatic spears", Color(0xFF80DEEA)),
  LEO(8, "August", "Leo", "♌", "Fire", "Solar Roar", "Emits an incandescent celestial burst that scorches all surrounding enemies in a sun halo", AntiqueGold),
  VIRGO(9, "September", "Virgo", "♍", "Earth", "Cleansing Bloom Field", "Manifests a sanctuary glyph restoring vital health and purging debuffs", Color(0xFF81C784)),
  LIBRA(10, "October", "Libra", "♎", "Air", "Equinox Dual Marks", "Applies twin celestial marks to foes that detonate in harmonious resonance", Color(0xFFBA68C8)),
  SCORPIO(11, "November", "Scorpio", "♏", "Water", "Venom Tether", "Shoots a stinger tether draining enemy vitality over time while slowing movement", ShadowViolet),
  SAGITTARIUS(12, "December", "Sagittarius", "♐", "Fire", "Astral Star Volley", "Fires a rapid succession of 5 seeking starlight arrows across the arena", Color(0xFFFF8A65));

  companion object {
    fun fromMonth(month: Int): MonthZodiac {
      val clamped = ((month - 1) % 12 + 12) % 12 + 1
      return entries.firstOrNull { it.monthNumber == clamped } ?: LEO
    }
  }
}

enum class ChineseAnimal(
  val index: Int,
  val animalName: String,
  val emoji: String,
  val traitName: String,
  val traitDescription: String
) {
  RAT(0, "Rat", "🐀", "Swift Magnet", "Attracts fallen shards rapidly and grants +15% evasive speed"),
  OX(1, "Ox", "🐂", "Stone Bastion", "Passively reduces all incoming damage by 20% and imparts heavy knockback"),
  TIGER(2, "Tiger", "🐅", "Ferocious Finisher", "Third combo strike inflicts devastating +35% bonus damage"),
  RABBIT(3, "Rabbit", "🐇", "Dual Fleetfoot", "Grants 2 consecutive dash charges with reduced recharge time"),
  DRAGON(4, "Dragon", "🐉", "Blazing Wake", "Dashes leave a trail of celestial fire that scorches traversing enemies"),
  SNAKE(5, "Snake", "🐍", "Serpentine Slip", "Immune to toxic hazards and gains a razor-narrow precision dodge"),
  HORSE(6, "Horse", "🐎", "Gale Gallop", "Passively increases base movement speed by +25% in combat"),
  GOAT(7, "Goat", "🐐", "Mountain Vitality", "Restores +15 HP automatically upon entering each new battle phase"),
  MONKEY(8, "Monkey", "🐒", "Phantom Decoy", "A perfect dodge leaves an illusion that taunts and confuses enemies for 2s"),
  ROOSTER(9, "Rooster", "🐓", "Sun Herald", "Technique and ranged attacks deal +30% amplified damage"),
  DOG(10, "Dog", "🐕", "Aegis Guard", "Shields against the very first strike taken in each battle phase"),
  PIG(11, "Pig", "🐖", "Bountiful Strike", "Landing a full 3-hit basic combo restores +6 HP to the guardian");

  companion object {
    // Standard repeating cycle anchored to 2020 = Rat
    fun fromYear(year: Int): ChineseAnimal {
      val idx = ((year - 2020) % 12 + 12) % 12
      return entries.firstOrNull { it.index == idx } ?: DRAGON
    }
  }
}

data class GuardianFusion(
  val zodiac: MonthZodiac,
  val animal: ChineseAnimal,
  val birthMonth: Int,
  val birthYear: Int
) {
  val title: String
    get() = "${zodiac.signName}–${animal.animalName} Guardian"

  val comboTitle: String
    get() = "${zodiac.signName}-${animal.animalName}"
}

data class EnemyFusion(
  val zodiac: MonthZodiac,
  val animal: ChineseAnimal,
  val realmIndex: Int,
  val gateIndex: Int,
  val isChimeraBoss: Boolean = false,
  val isFinalBoss: Boolean = false
) {
  val name: String
    get() = when {
      isFinalBoss -> "Celestial Chimera Void Prime"
      isChimeraBoss -> "Year ${animal.animalName} Chimera"
      else -> "${zodiac.signName}–${animal.animalName}"
    }

  val maxHp: Float
    get() = when {
      isFinalBoss -> 480f
      isChimeraBoss -> 320f
      gateIndex % 4 == 0 -> 130f // lighter recovery gate
      else -> 160f
    }

  val shadowMaxHp: Float
    get() = when {
      isFinalBoss -> 580f
      isChimeraBoss -> 400f
      else -> 230f
    }
}
