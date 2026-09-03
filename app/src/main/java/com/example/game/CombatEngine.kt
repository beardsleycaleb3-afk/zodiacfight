package com.example.game

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.example.audio.GameSoundPlayer
import com.example.model.ChineseAnimal
import com.example.model.EnemyFusion
import com.example.model.GuardianFusion
import com.example.model.MonthZodiac
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.BrightGold
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.ShadowViolet
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

enum class BattlePhase {
  NORMAL,
  TRANSFORMING_TO_SHADOW,
  SHADOW,
  VICTORY,
  DEFEAT
}

data class CombatParticle(
  var x: Float,
  var y: Float,
  var vx: Float,
  var vy: Float,
  var life: Float, // 1.0 down to 0.0
  val maxLife: Float,
  val color: Color,
  val size: Float
)

data class DamageText(
  val text: String,
  var x: Float,
  var y: Float,
  var life: Float,
  val color: Color
)

data class Projectile(
  var x: Float,
  var y: Float,
  var vx: Float,
  var vy: Float,
  val radius: Float,
  val damage: Float,
  val isPlayer: Boolean,
  val color: Color,
  var duration: Float
)

data class GroundHazard(
  val x: Float,
  val y: Float,
  val radius: Float,
  var duration: Float,
  val damagePerSec: Float,
  val color: Color,
  val isPlayer: Boolean
)

data class TelegraphWarning(
  val x: Float,
  val y: Float,
  val radius: Float,
  var progress: Float, // 0 to 1
  val duration: Float,
  val isShadow: Boolean
)

class BattleState(
  val playerFusion: GuardianFusion,
  val enemyFusion: EnemyFusion,
  val realmDamageBonus: Float = 0f
) {
  // Arena radius
  val arenaRadius: Float = 220f

  // Player state
  var playerX: Float = 0f
  var playerY: Float = 120f
  var playerVx: Float = 0f
  var playerVy: Float = 0f
  var playerFacingX: Float = 0f
  var playerFacingY: Float = -1f

  var playerHp: Float = 100f
  val playerMaxHp: Float = 100f
  var healCharges: Int = 1

  // Attack combo
  var comboStep: Int = 0
  var comboTimer: Float = 0f
  var attackCooldown: Float = 0f
  var attackSlashAngle: Float = 0f
  var attackSlashActiveTime: Float = 0f

  // Dash
  var dashCooldown: Float = 0f
  var dashDuration: Float = 0f
  var isInvulnerable: Boolean = false
  var rabbitExtraDash: Int = if (playerFusion.animal == ChineseAnimal.RABBIT) 1 else 0

  // Sign technique
  var techniqueCooldown: Float = 0f
  val techniqueMaxCooldown: Float = 6.0f
  var techniqueActiveTime: Float = 0f

  // Dog shield
  var dogShieldActive: Boolean = playerFusion.animal == ChineseAnimal.DOG

  // Enemy state
  var enemyX: Float = 0f
  var enemyY: Float = -90f
  var enemyHp: Float = enemyFusion.maxHp
  var enemyMaxHp: Float = enemyFusion.maxHp
  var enemyPhase: BattlePhase = BattlePhase.NORMAL

  // Enemy AI
  var enemyAiTimer: Float = 1.0f
  var enemyTelegraph: TelegraphWarning? = null
  var transformTimer: Float = 0f

  // Arena collections
  val particles = mutableListOf<CombatParticle>()
  val damageTexts = mutableListOf<DamageText>()
  val projectiles = mutableListOf<Projectile>()
  val hazards = mutableListOf<GroundHazard>()

  // Screen shake & hit stop
  var screenShake: Float = 0f
  var hitStopTimer: Float = 0f

  init {
    // Reset Dog shield
    dogShieldActive = playerFusion.animal == ChineseAnimal.DOG
  }

  fun update(dt: Float) {
    if (hitStopTimer > 0f) {
      hitStopTimer -= dt
      return
    }

    if (screenShake > 0f) {
      screenShake = (screenShake - dt * 15f).coerceAtLeast(0f)
    }

    // Update timers
    if (attackCooldown > 0f) attackCooldown -= dt
    if (attackSlashActiveTime > 0f) attackSlashActiveTime -= dt
    if (comboTimer > 0f) {
      comboTimer -= dt
      if (comboTimer <= 0f) comboStep = 0
    }
    if (dashCooldown > 0f) dashCooldown -= dt
    if (dashDuration > 0f) {
      dashDuration -= dt
      isInvulnerable = dashDuration in 0.06f..0.24f
    } else {
      isInvulnerable = false
    }
    if (techniqueCooldown > 0f) techniqueCooldown -= dt
    if (techniqueActiveTime > 0f) techniqueActiveTime -= dt

    // Phase transitions
    if (enemyPhase == BattlePhase.TRANSFORMING_TO_SHADOW) {
      transformTimer -= dt
      // Spawn dark purple transformation particles
      for (i in 0..2) {
        val angle = Math.random().toFloat() * 6.28f
        val dist = 10f + Math.random().toFloat() * 35f
        particles.add(
          CombatParticle(
            x = enemyX + cos(angle) * dist,
            y = enemyY + sin(angle) * dist,
            vx = cos(angle) * 30f,
            vy = sin(angle) * 30f,
            life = 0.6f,
            maxLife = 0.6f,
            color = ShadowViolet,
            size = 5f
          )
        )
      }
      if (transformTimer <= 0f) {
        enemyPhase = BattlePhase.SHADOW
        enemyMaxHp = enemyFusion.shadowMaxHp
        enemyHp = enemyMaxHp
        dogShieldActive = playerFusion.animal == ChineseAnimal.DOG
        if (playerFusion.animal == ChineseAnimal.GOAT) {
          playerHp = (playerHp + 15f).coerceAtMost(playerMaxHp)
          addDamageText("+15 HP", playerX, playerY - 30f, AntiqueGold)
          GameSoundPlayer.playHeal()
        }
      }
      return
    }

    if (enemyPhase == BattlePhase.VICTORY || enemyPhase == BattlePhase.DEFEAT) {
      return
    }

    // Move player
    var speed = 120f
    if (playerFusion.animal == ChineseAnimal.HORSE) speed *= 1.25f
    if (dashDuration > 0f) speed *= 2.8f

    playerX += playerVx * speed * dt
    playerY += playerVy * speed * dt

    // Arena boundary clamp
    val pDist = sqrt(playerX * playerX + playerY * playerY)
    if (pDist > arenaRadius - 16f) {
      val angle = atan2(playerY, playerX)
      playerX = cos(angle) * (arenaRadius - 16f)
      playerY = sin(angle) * (arenaRadius - 16f)
    }

    // Dragon burning dash trail
    if (dashDuration > 0f && playerFusion.animal == ChineseAnimal.DRAGON) {
      hazards.add(
        GroundHazard(
          x = playerX,
          y = playerY,
          radius = 24f,
          duration = 1.4f,
          damagePerSec = 22f,
          color = CrimsonRed,
          isPlayer = true
        )
      )
    }

    // Update projectiles
    val projIterator = projectiles.iterator()
    while (projIterator.hasNext()) {
      val p = projIterator.next()
      p.x += p.vx * dt
      p.y += p.vy * dt
      p.duration -= dt

      // Collision with enemy
      if (p.isPlayer) {
        val dx = p.x - enemyX
        val dy = p.y - enemyY
        if (sqrt(dx * dx + dy * dy) < p.radius + 28f) {
          damageEnemy(p.damage)
          projIterator.remove()
          continue
        }
      } else {
        // Enemy projectile hits player
        val dx = p.x - playerX
        val dy = p.y - playerY
        if (sqrt(dx * dx + dy * dy) < p.radius + 18f && !isInvulnerable) {
          damagePlayer(p.damage)
          projIterator.remove()
          continue
        }
      }

      if (p.duration <= 0f || sqrt(p.x * p.x + p.y * p.y) > arenaRadius + 20f) {
        projIterator.remove()
      }
    }

    // Update hazards
    val hazardIterator = hazards.iterator()
    while (hazardIterator.hasNext()) {
      val h = hazardIterator.next()
      h.duration -= dt
      if (h.isPlayer) {
        val dx = h.x - enemyX
        val dy = h.y - enemyY
        if (sqrt(dx * dx + dy * dy) < h.radius + 22f) {
          damageEnemy(h.damagePerSec * dt)
        }
      } else {
        val dx = h.x - playerX
        val dy = h.y - playerY
        if (sqrt(dx * dx + dy * dy) < h.radius + 16f && !isInvulnerable) {
          damagePlayer(h.damagePerSec * dt)
        }
      }
      if (h.duration <= 0f) hazardIterator.remove()
    }

    // Update Enemy AI
    updateEnemyAi(dt)

    // Update particles & damage text
    val partIterator = particles.iterator()
    while (partIterator.hasNext()) {
      val pt = partIterator.next()
      pt.x += pt.vx * dt
      pt.y += pt.vy * dt
      pt.life -= dt
      if (pt.life <= 0f) partIterator.remove()
    }

    val textIterator = damageTexts.iterator()
    while (textIterator.hasNext()) {
      val txt = textIterator.next()
      txt.y -= 35f * dt
      txt.life -= dt
      if (txt.life <= 0f) textIterator.remove()
    }
  }

  private fun updateEnemyAi(dt: Float) {
    val dx = playerX - enemyX
    val dy = playerY - enemyY
    val dist = sqrt(dx * dx + dy * dy)
    val isShadow = enemyPhase == BattlePhase.SHADOW
    val speed = if (isShadow) 75f else 55f

    // Face player
    if (dist > 1f) {
      val nx = dx / dist
      val ny = dy / dist
      // If not telegraphing, move towards player
      if (enemyTelegraph == null) {
        if (dist > 50f) {
          enemyX += nx * speed * dt
          enemyY += ny * speed * dt
        }
      }
    }

    // Clamp enemy in arena
    val eDist = sqrt(enemyX * enemyX + enemyY * enemyY)
    if (eDist > arenaRadius - 25f) {
      val angle = atan2(enemyY, enemyX)
      enemyX = cos(angle) * (arenaRadius - 25f)
      enemyY = sin(angle) * (arenaRadius - 25f)
    }

    // Telegraph update
    val tg = enemyTelegraph
    if (tg != null) {
      tg.progress += dt / tg.duration
      if (tg.progress >= 1.0f) {
        // Trigger attack
        executeEnemyAttack(tg)
        enemyTelegraph = null
        enemyAiTimer = if (isShadow) 1.2f else 1.8f
      }
    } else {
      enemyAiTimer -= dt
      if (enemyAiTimer <= 0f) {
        // Start telegraph
        val telegraphDuration = if (isShadow) 0.75f else 0.95f
        enemyTelegraph = TelegraphWarning(
          x = if (Math.random() > 0.4) playerX else enemyX,
          y = if (Math.random() > 0.4) playerY else enemyY,
          radius = if (isShadow) 58f else 46f,
          progress = 0f,
          duration = telegraphDuration,
          isShadow = isShadow
        )
      }
    }

    // Shadow phase ambient hazard: spawn occasional shadow rift
    if (isShadow && Math.random() < 0.015) {
      hazards.add(
        GroundHazard(
          x = (Math.random().toFloat() * 200f - 100f),
          y = (Math.random().toFloat() * 200f - 100f),
          radius = 35f,
          duration = 4.0f,
          damagePerSec = 12f,
          color = ShadowViolet,
          isPlayer = false
        )
      )
    }
  }

  private fun executeEnemyAttack(tg: TelegraphWarning) {
    val isShadow = tg.isShadow
    val attackColor = if (isShadow) ShadowViolet else CrimsonRed
    // Explosion in radius
    val dx = playerX - tg.x
    val dy = playerY - tg.y
    val dist = sqrt(dx * dx + dy * dy)
    if (dist < tg.radius && !isInvulnerable) {
      val dmg = if (isShadow) 26f else 18f
      damagePlayer(dmg)
    }

    // Shockwave particles
    for (i in 0..16) {
      val angle = i * (6.28f / 16f)
      particles.add(
        CombatParticle(
          x = tg.x,
          y = tg.y,
          vx = cos(angle) * 90f,
          vy = sin(angle) * 90f,
          life = 0.4f,
          maxLife = 0.4f,
          color = attackColor,
          size = 6f
        )
      )
    }
    screenShake = 6f
    GameSoundPlayer.playHit()
  }

  fun performBasicAttack() {
    if (attackCooldown > 0f) return

    comboStep = if (comboStep in 1..2 && comboTimer > 0f) comboStep + 1 else 1
    val stepDurations = floatArrayOf(0.28f, 0.32f, 0.48f)
    attackCooldown = stepDurations[comboStep - 1]
    comboTimer = 0.9f
    attackSlashActiveTime = 0.22f

    // Aim toward enemy or facing direction
    val dx = enemyX - playerX
    val dy = enemyY - playerY
    val dist = sqrt(dx * dx + dy * dy)
    attackSlashAngle = if (dist > 1f) atan2(dy, dx) else atan2(playerFacingY, playerFacingX)
    playerFacingX = cos(attackSlashAngle)
    playerFacingY = sin(attackSlashAngle)

    GameSoundPlayer.playSlash(comboStep)

    // Check hit
    val attackRange = 75f
    if (dist <= attackRange) {
      var baseDamage = when (comboStep) {
        1 -> 16f
        2 -> 22f
        else -> 36f
      }

      // Tiger passive: +35% on 3rd combo hit
      if (comboStep == 3 && playerFusion.animal == ChineseAnimal.TIGER) {
        baseDamage *= 1.35f
      }
      // Realm damage bonus
      baseDamage *= (1.0f + realmDamageBonus)

      damageEnemy(baseDamage)

      // Pig passive: heal on combo finisher
      if (comboStep == 3 && playerFusion.animal == ChineseAnimal.PIG) {
        playerHp = (playerHp + 6f).coerceAtMost(playerMaxHp)
        addDamageText("+6 HP", playerX, playerY - 30f, AntiqueGold)
        GameSoundPlayer.playHeal()
      }

      // Gemini passive / technique echo
      if (playerFusion.zodiac == MonthZodiac.GEMINI && comboStep == 3) {
        particles.add(
          CombatParticle(
            x = enemyX,
            y = enemyY,
            vx = 0f,
            vy = -20f,
            life = 0.5f,
            maxLife = 0.5f,
            color = BrightGold,
            size = 14f
          )
        )
        damageEnemy(baseDamage * 0.5f)
      }
    }
  }

  fun performDash(dirX: Float, dirY: Float) {
    if (dashCooldown > 0f) return

    var nx = dirX
    var ny = dirY
    val len = sqrt(nx * nx + ny * ny)
    if (len > 0.1f) {
      nx /= len
      ny /= len
    } else {
      nx = playerFacingX
      ny = playerFacingY
    }

    playerFacingX = nx
    playerFacingY = ny
    playerVx = nx
    playerVy = ny

    dashDuration = 0.30f
    isInvulnerable = true
    dashCooldown = 1.2f

    // Monkey passive: decoy on dodge
    if (playerFusion.animal == ChineseAnimal.MONKEY) {
      particles.add(
        CombatParticle(
          x = playerX,
          y = playerY,
          vx = 0f,
          vy = 0f,
          life = 1.5f,
          maxLife = 1.5f,
          color = AntiqueGold,
          size = 18f
        )
      )
    }

    GameSoundPlayer.playDash()
  }

  fun performTechnique() {
    if (techniqueCooldown > 0f) return
    techniqueCooldown = techniqueMaxCooldown
    techniqueActiveTime = 0.45f
    GameSoundPlayer.playTechnique()

    var bonus = 1.0f + realmDamageBonus
    if (playerFusion.animal == ChineseAnimal.ROOSTER) bonus *= 1.30f

    val sign = playerFusion.zodiac
    when (sign) {
      MonthZodiac.CAPRICORN -> { // Stone Barrier Charge
        isInvulnerable = true
        dashDuration = 0.35f
        damageEnemy(45f * bonus)
        screenShake = 7f
      }
      MonthZodiac.AQUARIUS -> { // Water fan wave
        for (i in -2..2) {
          val angle = atan2(enemyY - playerY, enemyX - playerX) + i * 0.25f
          projectiles.add(
            Projectile(
              x = playerX,
              y = playerY,
              vx = cos(angle) * 220f,
              vy = sin(angle) * 220f,
              radius = 16f,
              damage = 18f * bonus,
              isPlayer = true,
              color = NeonCyan,
              duration = 1.5f
            )
          )
        }
      }
      MonthZodiac.PISCES -> { // Orbiting twin bolts
        for (i in 0..1) {
          val angle = i * 3.14f
          projectiles.add(
            Projectile(
              x = playerX + cos(angle) * 30f,
              y = playerY + sin(angle) * 30f,
              vx = cos(angle + 1.2f) * 200f,
              vy = sin(angle + 1.2f) * 200f,
              radius = 18f,
              damage = 25f * bonus,
              isPlayer = true,
              color = Color(0xFF4FC3F7),
              duration = 2.0f
            )
          )
        }
      }
      MonthZodiac.ARIES -> { // Piercing horn rush
        val angle = atan2(enemyY - playerY, enemyX - playerX)
        playerX += cos(angle) * 60f
        playerY += sin(angle) * 60f
        damageEnemy(52f * bonus)
        screenShake = 8f
      }
      MonthZodiac.TAURUS -> { // Radial ground slam
        hazards.add(
          GroundHazard(
            x = playerX,
            y = playerY,
            radius = 80f,
            duration = 1.2f,
            damagePerSec = 40f * bonus,
            color = Color(0xFFA1887F),
            isPlayer = true
          )
        )
        screenShake = 9f
      }
      MonthZodiac.GEMINI -> { // Echo strike
        damageEnemy(28f * bonus)
        damageEnemy(28f * bonus)
      }
      MonthZodiac.CANCER -> { // Shell counter
        isInvulnerable = true
        dashDuration = 0.4f
        damageEnemy(45f * bonus)
      }
      MonthZodiac.LEO -> { // Solar roar
        screenShake = 12f
        damageEnemy(50f * bonus)
        for (i in 0..20) {
          val angle = i * (6.28f / 20f)
          particles.add(
            CombatParticle(
              x = playerX,
              y = playerY,
              vx = cos(angle) * 140f,
              vy = sin(angle) * 140f,
              life = 0.4f,
              maxLife = 0.4f,
              color = BrightGold,
              size = 8f
            )
          )
        }
      }
      MonthZodiac.VIRGO -> { // Cleansing heal field
        playerHp = (playerHp + 25f).coerceAtMost(playerMaxHp)
        addDamageText("+25 HP", playerX, playerY - 35f, BrightGold)
        hazards.add(
          GroundHazard(
            x = playerX,
            y = playerY,
            radius = 65f,
            duration = 2.5f,
            damagePerSec = 18f * bonus,
            color = Color(0xFF81C784),
            isPlayer = true
          )
        )
      }
      MonthZodiac.LIBRA -> { // Equinox dual marks
        damageEnemy(46f * bonus)
      }
      MonthZodiac.SCORPIO -> { // Venom tether
        hazards.add(
          GroundHazard(
            x = enemyX,
            y = enemyY,
            radius = 45f,
            duration = 3.0f,
            damagePerSec = 18f * bonus,
            color = ShadowViolet,
            isPlayer = true
          )
        )
      }
      MonthZodiac.SAGITTARIUS -> { // Astral star volley (5 arrows)
        val baseAngle = atan2(enemyY - playerY, enemyX - playerX)
        for (i in -2..2) {
          val angle = baseAngle + i * 0.12f
          projectiles.add(
            Projectile(
              x = playerX,
              y = playerY,
              vx = cos(angle) * 260f,
              vy = sin(angle) * 260f,
              radius = 12f,
              damage = 12f * bonus,
              isPlayer = true,
              color = BrightGold,
              duration = 1.4f
            )
          )
        }
      }
    }
  }

  fun useHeal() {
    if (healCharges <= 0 || playerHp >= playerMaxHp) return
    healCharges--
    playerHp = (playerHp + 40f).coerceAtMost(playerMaxHp)
    addDamageText("+40 HP", playerX, playerY - 35f, AntiqueGold)
    GameSoundPlayer.playHeal()
  }

  fun damagePlayer(amount: Float) {
    if (isInvulnerable) return

    // Dog passive: shield first hit
    if (dogShieldActive) {
      dogShieldActive = false
      addDamageText("SHIELDED!", playerX, playerY - 30f, BrightGold)
      GameSoundPlayer.playHit()
      return
    }

    var dmg = amount
    // Ox passive: -20% incoming damage
    if (playerFusion.animal == ChineseAnimal.OX) {
      dmg *= 0.8f
    }

    playerHp = (playerHp - dmg).coerceAtLeast(0f)
    addDamageText("-${dmg.toInt()}", playerX, playerY - 20f, CrimsonRed)
    screenShake = 8f
    hitStopTimer = 0.06f
    GameSoundPlayer.playHit()

    if (playerHp <= 0f) {
      enemyPhase = BattlePhase.DEFEAT
      GameSoundPlayer.playDefeat()
    }
  }

  fun damageEnemy(amount: Float) {
    if (enemyPhase != BattlePhase.NORMAL && enemyPhase != BattlePhase.SHADOW) return

    enemyHp = (enemyHp - amount).coerceAtLeast(0f)
    addDamageText("-${amount.toInt()}", enemyX + (Math.random().toFloat() * 20f - 10f), enemyY - 25f, BrightGold)
    hitStopTimer = 0.05f

    // Hit sparks
    for (i in 0..4) {
      val angle = Math.random().toFloat() * 6.28f
      particles.add(
        CombatParticle(
          x = enemyX,
          y = enemyY,
          vx = cos(angle) * 60f,
          vy = sin(angle) * 60f,
          life = 0.25f,
          maxLife = 0.25f,
          color = BrightGold,
          size = 4f
        )
      )
    }

    if (enemyHp <= 0f) {
      if (enemyPhase == BattlePhase.NORMAL) {
        // Trigger Shadow Phase transformation!
        enemyPhase = BattlePhase.TRANSFORMING_TO_SHADOW
        transformTimer = 1.5f
        screenShake = 10f
        GameSoundPlayer.playShadowTransform()
      } else if (enemyPhase == BattlePhase.SHADOW) {
        enemyPhase = BattlePhase.VICTORY
        screenShake = 14f
        GameSoundPlayer.playVictory()
      }
    }
  }

  private fun addDamageText(text: String, x: Float, y: Float, color: Color) {
    damageTexts.add(DamageText(text, x, y, 0.7f, color))
  }
}
