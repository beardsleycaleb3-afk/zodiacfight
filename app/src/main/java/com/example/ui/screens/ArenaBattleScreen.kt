package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.GameSoundPlayer
import com.example.game.BattlePhase
import com.example.game.BattleState
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
import com.example.ui.theme.DangerRed
import com.example.ui.theme.DarkGold
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
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun ArenaBattleScreen(
  playerFusion: GuardianFusion,
  enemyFusion: EnemyFusion,
  realmIndex: Int,
  gateIndex: Int,
  shardsCount: Int,
  unlockedCodexCount: Int,
  realmDamageBonus: Float,
  onBattleVictory: () -> Unit,
  onOpenCodex: () -> Unit,
  onReturnToMap: () -> Unit
) {
  val battleState = remember(playerFusion, enemyFusion) {
    BattleState(playerFusion, enemyFusion, realmDamageBonus)
  }

  // Virtual joystick state
  var joystickOffset by remember { mutableStateOf(Offset.Zero) }
  val maxStickRadius = 40f

  // Game loop (60 fps tick)
  var lastFrameNanos by remember { mutableFloatStateOf(0f) }
  var isPaused by remember { mutableStateOf(false) }

  LaunchedEffect(isPaused) {
    if (isPaused) return@LaunchedEffect
    var prevNanos = 0L
    while (true) {
      withFrameNanos { frameNanos ->
        if (prevNanos != 0L) {
          val dt = ((frameNanos - prevNanos) / 1_000_000_000f).coerceIn(0.001f, 0.05f)
          battleState.update(dt)
        }
        prevNanos = frameNanos
      }
    }
  }

  val isShadowPhase = battleState.enemyPhase == BattlePhase.SHADOW
  val isTransforming = battleState.enemyPhase == BattlePhase.TRANSFORMING_TO_SHADOW
  val isDefeated = battleState.enemyPhase == BattlePhase.DEFEAT
  val isVictory = battleState.enemyPhase == BattlePhase.VICTORY

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(ObsidianBg)
      .testTag("arena_battle_screen")
  ) {
    Column(
      modifier = Modifier.fillMaxSize()
    ) {
      // 1. Top HUD: Realm and Vital Info (matching Artistic Flair)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(
            Brush.verticalGradient(
              listOf(SurfacePanel, Color.Transparent)
            )
          )
          .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        // Player Vitals (Left)
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            // Level badge (circular gold with glow)
            Box(
              modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(AntiqueGold)
                .border(2.dp, BrightGold, CircleShape)
                .shadow(elevation = 10.dp, shape = CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "LV ${20 + realmIndex * 2}",
                color = Color.Black,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }

            Column {
              Text(
                text = "${playerFusion.zodiac.signName}-${playerFusion.animal.animalName}".uppercase(),
                color = TextMuted,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp
              )
              Text(
                text = "Guardian Prime",
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
              )
            }
          }

          // Player Health Bar
          val hpPercent = (battleState.playerHp / battleState.playerMaxHp).coerceIn(0f, 1f)
          Box(
            modifier = Modifier
              .width(130.dp)
              .height(8.dp)
              .clip(RoundedCornerShape(4.dp))
              .background(SurfaceHighlight)
          ) {
            Box(
              modifier = Modifier
                .fillMaxWidth(hpPercent)
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(CrimsonRed)
            )
          }
        }

        // Realm Badge & Gate Number (Right)
        Column(horizontalAlignment = Alignment.End) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(SurfacePanel)
                .border(1.dp, BorderSlate, RoundedCornerShape(12.dp))
                .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
              Text(
                text = "Realm of the ${ChineseAnimal.entries[realmIndex].animalName}",
                color = AntiqueGold,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
              )
            }

            Spacer(modifier = Modifier.width(6.dp))

            IconButton(
              onClick = { isPaused = !isPaused },
              modifier = Modifier.size(32.dp).testTag("pause_button")
            ) {
              Icon(Icons.Default.Pause, contentDescription = "Pause", tint = TextMuted)
            }
          }

          Text(
            text = "Gate ${if (gateIndex < 10) "0$gateIndex" else "$gateIndex"}/12",
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Light,
            modifier = Modifier.padding(top = 2.dp)
          )
        }
      }

      // 2. Main Arena Viewport (Simulated 3D Space)
      Box(
        modifier = Modifier
          .weight(1f)
          .fillMaxWidth()
          .padding(horizontal = 8.dp)
          .testTag("combat_arena_viewport"),
        contentAlignment = Alignment.Center
      ) {
        // Boss Visual Indicator (Top Center of arena)
        Column(
          modifier = Modifier
            .align(Alignment.TopCenter)
            .padding(top = 4.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = if (isShadowPhase || isTransforming) "SHADOW PHASE" else "NORMAL GUARDIAN",
            color = if (isShadowPhase || isTransforming) DangerRed else AntiqueGold,
            fontSize = 10.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 2.sp
          )

          Text(
            text = enemyFusion.name,
            color = TextPrimary,
            fontSize = 17.sp,
            fontFamily = FontFamily.Serif,
            fontStyle = FontStyle.Italic,
            fontWeight = FontWeight.Medium
          )

          // Boss Health Bar
          val bossHpPercent = (battleState.enemyHp / battleState.enemyMaxHp).coerceIn(0f, 1f)
          Box(
            modifier = Modifier
              .width(180.dp)
              .height(6.dp)
              .clip(RoundedCornerShape(3.dp))
              .background(SurfacePanel)
          ) {
            Box(
              modifier = Modifier
                .fillMaxWidth(bossHpPercent)
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(if (isShadowPhase || isTransforming) ShadowViolet else CrimsonRed)
            )
          }
        }

        // Custom Canvas Arena rendering
        Canvas(
          modifier = Modifier
            .fillMaxSize()
        ) {
          val centerX = size.width / 2f
          val centerY = size.height / 2f
          val arenaRadius = battleState.arenaRadius

          // Screen shake offset
          val shakeX = if (battleState.screenShake > 0f) (Math.random().toFloat() - 0.5f) * battleState.screenShake * 2f else 0f
          val shakeY = if (battleState.screenShake > 0f) (Math.random().toFloat() - 0.5f) * battleState.screenShake * 2f else 0f

          val origin = Offset(centerX + shakeX, centerY + shakeY)

          // 1. Arena Circular Floor Rings
          // Outer dashed ring
          drawCircle(
            color = if (isShadowPhase) ShadowViolet.copy(alpha = 0.25f) else BorderSlate.copy(alpha = 0.4f),
            radius = arenaRadius,
            center = origin,
            style = Stroke(
              width = 1.5f,
              pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
            )
          )

          // Inner solid boundary
          drawCircle(
            color = Color(0xFF1E2129),
            radius = arenaRadius * 0.85f,
            center = origin,
            style = Stroke(width = 1f)
          )

          // Center celestial clock marks (12 zodiac points on rim)
          for (i in 0..11) {
            val angle = i * (6.283185f / 12f)
            val px = origin.x + cos(angle) * (arenaRadius * 0.92f)
            val py = origin.y + sin(angle) * (arenaRadius * 0.92f)
            drawCircle(
              color = if (isShadowPhase) ShadowViolet.copy(alpha = 0.5f) else AntiqueGold.copy(alpha = 0.5f),
              radius = 3.5f,
              center = Offset(px, py)
            )
          }

          // 2. Telegraph Warnings
          battleState.enemyTelegraph?.let { tg ->
            val tgCenter = Offset(origin.x + tg.x, origin.y + tg.y)
            val color = if (tg.isShadow) ShadowViolet else CrimsonRed
            // Pulsing fill
            drawCircle(
              color = color.copy(alpha = (tg.progress * 0.35f).coerceIn(0.1f, 0.45f)),
              radius = tg.radius * tg.progress,
              center = tgCenter
            )
            // Warning border
            drawCircle(
              color = color.copy(alpha = 0.8f),
              radius = tg.radius,
              center = tgCenter,
              style = Stroke(width = 2f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f))
            )
          }

          // 3. Ground Hazards (Fire trails, shadow rifts)
          battleState.hazards.forEach { h ->
            drawCircle(
              color = h.color.copy(alpha = (h.duration.coerceAtMost(1f) * 0.35f)),
              radius = h.radius,
              center = Offset(origin.x + h.x, origin.y + h.y)
            )
            drawCircle(
              color = h.color.copy(alpha = 0.7f),
              radius = h.radius,
              center = Offset(origin.x + h.x, origin.y + h.y),
              style = Stroke(width = 1.5f)
            )
          }

          // 4. Projectiles
          battleState.projectiles.forEach { p ->
            drawCircle(
              color = p.color,
              radius = p.radius,
              center = Offset(origin.x + p.x, origin.y + p.y)
            )
            drawCircle(
              color = Color.White.copy(alpha = 0.8f),
              radius = p.radius * 0.5f,
              center = Offset(origin.x + p.x, origin.y + p.y)
            )
          }

          // 5. Basic attack crescent slash visual
          if (battleState.attackSlashActiveTime > 0f) {
            val pPos = Offset(origin.x + battleState.playerX, origin.y + battleState.playerY)
            val angle = battleState.attackSlashAngle
            val arcRadius = 45f
            val sweep = 1.6f

            val slashPath = Path().apply {
              val startAngle = angle - sweep / 2f
              val endAngle = angle + sweep / 2f
              moveTo(pPos.x + cos(startAngle) * (arcRadius * 0.4f), pPos.y + sin(startAngle) * (arcRadius * 0.4f))
              quadraticTo(
                pPos.x + cos(angle) * (arcRadius * 1.3f),
                pPos.y + sin(angle) * (arcRadius * 1.3f),
                pPos.x + cos(endAngle) * (arcRadius * 0.4f),
                pPos.y + sin(endAngle) * (arcRadius * 0.4f)
              )
            }
            drawPath(
              path = slashPath,
              color = if (battleState.comboStep == 3) BrightGold else Color.White,
              style = Stroke(width = 5f, cap = StrokeCap.Round)
            )
          }

          // 6. Enemy Guardian rendering
          val ePos = Offset(origin.x + battleState.enemyX, origin.y + battleState.enemyY)
          val enemyAuraColor = if (isShadowPhase || isTransforming) ShadowViolet else enemyFusion.zodiac.accentColor

          // Enemy shadow / ground aura
          drawCircle(
            color = enemyAuraColor.copy(alpha = 0.25f),
            radius = 34f,
            center = ePos
          )
          drawCircle(
            color = enemyAuraColor.copy(alpha = 0.8f),
            radius = 26f,
            center = ePos,
            style = Stroke(width = 2f)
          )

          // Native text draw for enemy glyph & emoji
          drawContext.canvas.nativeCanvas.apply {
            val paint = android.graphics.Paint().apply {
              textSize = 58f
              textAlign = android.graphics.Paint.Align.CENTER
              isAntiAlias = true
            }
            drawText(enemyFusion.animal.emoji, ePos.x, ePos.y + 20f, paint)

            val glyphPaint = android.graphics.Paint().apply {
              textSize = 42f
              textAlign = android.graphics.Paint.Align.CENTER
              color = android.graphics.Color.argb(
                220,
                if (isShadowPhase) 180 else 255,
                if (isShadowPhase) 120 else 215,
                if (isShadowPhase) 255 else 0
              )
              isAntiAlias = true
            }
            drawText(enemyFusion.zodiac.glyph, ePos.x, ePos.y - 32f, glyphPaint)
          }

          // 7. Player Guardian rendering (matches Artistic Flair character representation)
          val pPos = Offset(origin.x + battleState.playerX, origin.y + battleState.playerY)
          val playerAlpha = if (battleState.isInvulnerable) 0.6f else 1.0f

          // Ground ring
          drawOval(
            color = AntiqueGold.copy(alpha = 0.3f * playerAlpha),
            topLeft = Offset(pPos.x - 28f, pPos.y + 16f),
            size = Size(56f, 16f)
          )

          // Zodiac aura
          drawCircle(
            color = AntiqueGold.copy(alpha = 0.18f * playerAlpha),
            radius = 36f,
            center = pPos
          )

          // Dog shield bubble
          if (battleState.dogShieldActive) {
            drawCircle(
              color = BrightGold.copy(alpha = 0.7f),
              radius = 42f,
              center = pPos,
              style = Stroke(width = 2f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 4f), 0f))
            )
          }

          // Cel-shaded guardian base
          drawCircle(
            color = SurfaceHighlight,
            radius = 24f,
            center = pPos
          )
          drawCircle(
            color = AntiqueGold,
            radius = 24f,
            center = pPos,
            style = Stroke(width = 2f)
          )

          // Player glyph and animal mask
          drawContext.canvas.nativeCanvas.apply {
            val animalPaint = android.graphics.Paint().apply {
              textSize = 50f
              textAlign = android.graphics.Paint.Align.CENTER
              isAntiAlias = true
            }
            drawText(playerFusion.animal.emoji, pPos.x, pPos.y + 16f, animalPaint)

            // Mythic Glyph overhead
            val glyphPaint = android.graphics.Paint().apply {
              textSize = 40f
              textAlign = android.graphics.Paint.Align.CENTER
              color = android.graphics.Color.argb(255, 212, 175, 55)
              isAntiAlias = true
            }
            drawText(playerFusion.zodiac.glyph, pPos.x, pPos.y - 28f, glyphPaint)
          }

          // 8. Particles
          battleState.particles.forEach { pt ->
            drawCircle(
              color = pt.color.copy(alpha = (pt.life / pt.maxLife).coerceIn(0f, 1f)),
              radius = pt.size,
              center = Offset(origin.x + pt.x, origin.y + pt.y)
            )
          }

          // 9. Floating Damage Text
          battleState.damageTexts.forEach { dt ->
            drawContext.canvas.nativeCanvas.apply {
              val textPaint = android.graphics.Paint().apply {
                textSize = 34f
                color = android.graphics.Color.argb(
                  (dt.life * 255f).toInt().coerceIn(0, 255),
                  (dt.color.red * 255).toInt(),
                  (dt.color.green * 255).toInt(),
                  (dt.color.blue * 255).toInt()
                )
                textAlign = android.graphics.Paint.Align.CENTER
                isFakeBoldText = true
                isAntiAlias = true
              }
              drawText(dt.text, origin.x + dt.x, origin.y + dt.y, textPaint)
            }
          }
        }
      }

      // 3. Combat Controls Footer (Matching Artistic Flair)
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
          .background(SurfaceDark)
          .border(1.dp, BorderSlate.copy(alpha = 0.5f), RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
          .padding(horizontal = 20.dp, vertical = 16.dp)
      ) {
        Column {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .height(96.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Virtual Stick Area (Left)
            Box(
              modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(SurfaceElevated)
                .border(1.dp, SurfaceHighlight, CircleShape)
                .pointerInput(Unit) {
                  detectDragGestures(
                    onDragStart = { offset ->
                      val dx = offset.x - 40f
                      val dy = offset.y - 40f
                      val dist = sqrt(dx * dx + dy * dy)
                      val clampedDist = dist.coerceAtMost(maxStickRadius)
                      val angle = atan2(dy, dx)
                      joystickOffset = Offset(cos(angle) * clampedDist, sin(angle) * clampedDist)
                      battleState.playerVx = joystickOffset.x / maxStickRadius
                      battleState.playerVy = joystickOffset.y / maxStickRadius
                    },
                    onDrag = { change, dragAmount ->
                      change.consume()
                      val newOffset = joystickOffset + dragAmount
                      val dist = sqrt(newOffset.x * newOffset.x + newOffset.y * newOffset.y)
                      val clampedDist = dist.coerceAtMost(maxStickRadius)
                      val angle = atan2(newOffset.y, newOffset.x)
                      joystickOffset = Offset(cos(angle) * clampedDist, sin(angle) * clampedDist)
                      battleState.playerVx = joystickOffset.x / maxStickRadius
                      battleState.playerVy = joystickOffset.y / maxStickRadius
                    },
                    onDragEnd = {
                      joystickOffset = Offset.Zero
                      battleState.playerVx = 0f
                      battleState.playerVy = 0f
                    },
                    onDragCancel = {
                      joystickOffset = Offset.Zero
                      battleState.playerVx = 0f
                      battleState.playerVy = 0f
                    }
                  )
                },
              contentAlignment = Alignment.Center
            ) {
              // Subtle gold decorative ring
              Box(
                modifier = Modifier
                  .size(64.dp)
                  .border(1.dp, AntiqueGold.copy(alpha = 0.15f), CircleShape)
              )
              // Inner draggable thumb knob
              Box(
                modifier = Modifier
                  .offset { IntOffset(joystickOffset.x.roundToInt(), joystickOffset.y.roundToInt()) }
                  .size(40.dp)
                  .clip(CircleShape)
                  .background(SurfaceHighlight)
                  .border(1.dp, BorderLight, CircleShape)
              )
            }

            // Action Buttons Area (Right)
            Row(
              horizontalArrangement = Arrangement.spacedBy(14.dp),
              verticalAlignment = Alignment.Bottom
            ) {
              // Heal Charge Button
              Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
              ) {
                Box(
                  modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(if (battleState.healCharges > 0) SurfaceElevated else SurfaceDark)
                    .border(1.dp, if (battleState.healCharges > 0) AntiqueGold else BorderLight, CircleShape)
                    .clickable(enabled = battleState.healCharges > 0) {
                      battleState.useHeal()
                    }
                    .testTag("heal_button"),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    Icons.Default.Favorite,
                    contentDescription = "Heal",
                    tint = if (battleState.healCharges > 0) CrimsonRed else TextDim,
                    modifier = Modifier.size(20.dp)
                  )
                }
                Text(
                  text = "HEAL (${battleState.healCharges})",
                  color = TextDim,
                  fontSize = 8.sp,
                  fontWeight = FontWeight.Bold
                )
              }

              // Dash Button (✦)
              Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
              ) {
                val canDash = battleState.dashCooldown <= 0f
                Box(
                  modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(SurfaceHighlight)
                    .border(1.dp, BorderLight, CircleShape)
                    .clickable(enabled = canDash) {
                      battleState.performDash(battleState.playerVx, battleState.playerVy)
                    }
                    .testTag("dash_button"),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = if (canDash) "✦" else "${battleState.dashCooldown.toInt() + 1}",
                    color = if (canDash) TextPrimary else TextDim,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                  )
                }
                Text(
                  text = "DASH",
                  color = TextDim,
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold
                )
              }

              // Primary Month Technique Button (Leo Roar / Zodiac Sign)
              Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
              ) {
                val canUseTechnique = battleState.techniqueCooldown <= 0f
                Box(
                  modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape)
                    .background(
                      Brush.linearGradient(
                        listOf(AntiqueGold, DarkGold)
                      )
                    )
                    .border(2.dp, BrightGold, CircleShape)
                    .shadow(12.dp, CircleShape)
                    .clickable(enabled = canUseTechnique) {
                      battleState.performTechnique()
                    }
                    .padding(2.dp)
                    .testTag("technique_button"),
                  contentAlignment = Alignment.Center
                ) {
                  Box(
                    modifier = Modifier
                      .fillMaxSize()
                      .clip(CircleShape)
                      .background(ObsidianBg),
                    contentAlignment = Alignment.Center
                  ) {
                    if (canUseTechnique) {
                      Text(
                        text = playerFusion.zodiac.glyph,
                        color = BrightGold,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                      )
                    } else {
                      Text(
                        text = "${battleState.techniqueCooldown.toInt() + 1}s",
                        color = TextDim,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                      )
                    }
                  }
                }
                Text(
                  text = playerFusion.zodiac.techniqueName.split(" ").last().uppercase(),
                  color = AntiqueGold,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Black,
                  letterSpacing = 1.sp
                )
              }

              // Basic Attack Button (⚔️ Strike)
              Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
              ) {
                Box(
                  modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .clickable {
                      battleState.performBasicAttack()
                    }
                    .testTag("strike_button"),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = "⚔️",
                    fontSize = 26.sp
                  )
                }
                Text(
                  text = "STRIKE",
                  color = TextDim,
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }

          // Bottom Meta Nav
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(top = 10.dp, start = 4.dp, end = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Shards Tracker (3 pips)
            Column {
              Text(
                text = "SHARDS (${shardsCount})",
                color = TextDim,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
              )
              Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(top = 2.dp)
              ) {
                val pipProgress = shardsCount % 3
                for (i in 0..2) {
                  val filled = i < pipProgress
                  Box(
                    modifier = Modifier
                      .size(width = 12.dp, height = 8.dp)
                      .clip(RoundedCornerShape(2.dp))
                      .background(if (filled) AntiqueGold else SurfaceHighlight)
                  )
                }
              }
            }

            // Codex Access Button
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(SurfaceElevated)
                .border(1.dp, BorderSlate, RoundedCornerShape(16.dp))
                .clickable {
                  GameSoundPlayer.playClick()
                  onOpenCodex()
                }
                .padding(horizontal = 14.dp, vertical = 6.dp)
                .testTag("codex_badge_button")
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Text(
                  text = "Codex",
                  color = AmberGold,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold
                )
                Text(
                  text = "$unlockedCodexCount/144",
                  color = TextDim,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.SemiBold
                )
              }
            }
          }
        }
      }
    }

    // Victory Dialog Overlay
    AnimatedVisibility(
      visible = isVictory,
      enter = fadeIn(),
      exit = fadeOut()
    ) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(Color.Black.copy(alpha = 0.85f))
          .padding(24.dp),
        contentAlignment = Alignment.Center
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(SurfacePanel)
            .border(2.dp, AntiqueGold, RoundedCornerShape(24.dp))
            .padding(24.dp)
        ) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
          ) {
            Text(
              text = "CELESTIAL VICTORY",
              color = AntiqueGold,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
              text = "Shadow Overcome!",
              color = TextPrimary,
              fontSize = 24.sp,
              fontWeight = FontWeight.Bold
            )

            Text(
              text = "${enemyFusion.name} inscribed in the 12×12 Codex.",
              color = TextMuted,
              fontSize = 13.sp,
              textAlign = TextAlign.Center,
              modifier = Modifier.padding(vertical = 8.dp)
            )

            Box(
              modifier = Modifier
                .padding(vertical = 12.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(SurfaceDark)
                .border(1.dp, BrightGold.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
              Text(
                text = "+1 Constellation Shard Awarded ✦",
                color = BrightGold,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
              )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
              onClick = onBattleVictory,
              modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("victory_next_button"),
              shape = RoundedCornerShape(25.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = AntiqueGold,
                contentColor = ObsidianBg
              )
            ) {
              Text(
                text = "PROCEED TO NEXT GATE",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
              )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
              onClick = onReturnToMap,
              modifier = Modifier
                .fillMaxWidth()
                .height(44.dp),
              shape = RoundedCornerShape(22.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = SurfaceElevated,
                contentColor = TextSoft
              )
            ) {
              Text("Return to Realm Wheel", fontSize = 13.sp)
            }
          }
        }
      }
    }

    // Defeat Dialog Overlay
    AnimatedVisibility(
      visible = isDefeated,
      enter = fadeIn(),
      exit = fadeOut()
    ) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(Color.Black.copy(alpha = 0.85f))
          .padding(24.dp),
        contentAlignment = Alignment.Center
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(SurfacePanel)
            .border(2.dp, DangerRed, RoundedCornerShape(24.dp))
            .padding(24.dp)
        ) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
          ) {
            Text(
              text = "SHADOW CONSUMED",
              color = DangerRed,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
              text = "Guardian Slain",
              color = TextPrimary,
              fontSize = 24.sp,
              fontWeight = FontWeight.Bold
            )

            Text(
              text = "Losing restarts only this gate with full health and fresh cooldowns.",
              color = TextMuted,
              fontSize = 13.sp,
              textAlign = TextAlign.Center,
              modifier = Modifier.padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
              onClick = {
                // Reset gate combat
                battleState.playerHp = battleState.playerMaxHp
                battleState.enemyHp = battleState.enemyFusion.maxHp
                battleState.enemyMaxHp = battleState.enemyFusion.maxHp
                battleState.enemyPhase = BattlePhase.NORMAL
                battleState.healCharges = 1
                battleState.techniqueCooldown = 0f
                battleState.dashCooldown = 0f
                battleState.playerX = 0f
                battleState.playerY = 120f
                battleState.enemyX = 0f
                battleState.enemyY = -90f
              },
              modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("retry_gate_button"),
              shape = RoundedCornerShape(25.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = DangerRed,
                contentColor = TextPrimary
              )
            ) {
              Text("RETRY GATE", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
              onClick = onReturnToMap,
              modifier = Modifier
                .fillMaxWidth()
                .height(44.dp),
              shape = RoundedCornerShape(22.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = SurfaceElevated,
                contentColor = TextSoft
              )
            ) {
              Text("Return to Realm Wheel", fontSize = 13.sp)
            }
          }
        }
      }
    }
  }
}
