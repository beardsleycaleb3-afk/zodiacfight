const ZODIAC_MONTHS = [
  { id: 1, name: "Capricorn", glyph: "\u2651", element: "Earth", tech: "Stone Charge & Barrier", desc: "Dashes forward clad in granite, gaining a 2s damage shield." },
  { id: 2, name: "Aquarius", glyph: "\u2652", element: "Air", tech: "Pushing Water Fan", desc: "Releases a 90\xB0 wave blast that knocks back and slows foes." },
  { id: 3, name: "Pisces", glyph: "\u2653", element: "Water", tech: "Orbiting Twin Bolts", desc: "Summons twin astral orbs that seek and pierce enemies." },
  { id: 4, name: "Aries", glyph: "\u2648", element: "Fire", tech: "Piercing Horn Rush", desc: "A rapid lance charge dealing heavy critical puncture damage." },
  { id: 5, name: "Taurus", glyph: "\u2649", element: "Earth", tech: "Radial Ground Slam", desc: "Slams the celestial floor, causing a seismic shockwave." },
  { id: 6, name: "Gemini", glyph: "\u264A", element: "Air", tech: "Echo Strike", desc: "Empowers next strike to instantly repeat itself as a starlight phantom." },
  { id: 7, name: "Cancer", glyph: "\u264B", element: "Water", tech: "Timed Shell Counter", desc: "Enters parry stance: incoming hits trigger a lethal counter-burst." },
  { id: 8, name: "Leo", glyph: "\u264C", element: "Fire", tech: "Solar Roar", desc: "Unleashes a blinding cone of solar energy that burns enemies." },
  { id: 9, name: "Virgo", glyph: "\u264D", element: "Earth", tech: "Cleansing Heal Field", desc: "Sprouts a celestial glyph zone that restores health over time." },
  { id: 10, name: "Libra", glyph: "\u264E", element: "Air", tech: "Balance Burst", desc: "Places dual karmic seals that detonate when hit." },
  { id: 11, name: "Scorpio", glyph: "\u264F", element: "Water", tech: "Poison Tether", desc: "Hooks enemy with a venomous starlight tether dealing tick damage." },
  { id: 12, name: "Sagittarius", glyph: "\u2650", element: "Fire", tech: "Star Volley", desc: "Fires a rapid spread of celestial piercing starlight arrows." }
];
const CHINESE_ANIMALS = [
  { index: 0, name: "Rat", icon: "\u{1F400}", trait: "Quick sidestep & item magnet", effect: "rat_dodge" },
  { index: 1, name: "Ox", icon: "\u{1F402}", trait: "15% damage resistance & heavy dash", effect: "ox_guard" },
  { index: 2, name: "Tiger", icon: "\u{1F405}", trait: "3rd weapon combo hit deals +100% damage", effect: "tiger_fury" },
  { index: 3, name: "Rabbit", icon: "\u{1F407}", trait: "Grants a second rapid dash charge", effect: "rabbit_dash" },
  { index: 4, name: "Dragon", icon: "\u{1F409}", trait: "Dashes leave a trail of burning astral fire", effect: "dragon_fire" },
  { index: 5, name: "Snake", icon: "\u{1F40D}", trait: "Poison immunity & evasive slither dash", effect: "snake_evade" },
  { index: 6, name: "Horse", icon: "\u{1F40E}", trait: "+25% sustained movement speed", effect: "horse_speed" },
  { index: 7, name: "Goat", icon: "\u{1F410}", trait: "Restores 20 HP after clearing each phase", effect: "goat_heal" },
  { index: 8, name: "Monkey", icon: "\u{1F412}", trait: "Leaves a starlight decoy upon dodging", effect: "monkey_decoy" },
  { index: 9, name: "Rooster", icon: "\u{1F413}", trait: "Sign techniques deal +30% critical damage", effect: "rooster_power" },
  { index: 10, name: "Dog", icon: "\u{1F415}", trait: "Divine Aegis: absorbs first hit in each phase", effect: "dog_shield" },
  { index: 11, name: "Pig", icon: "\u{1F416}", trait: "Landing full 3-hit combo heals 6 HP", effect: "pig_lifesteal" }
];
class AudioController {
  ctx = null;
  muted = false;
  init() {
    if (!this.ctx && typeof window !== "undefined") {
      const AudioCtx = window.AudioContext || window.webkitAudioContext;
      if (AudioCtx) {
        this.ctx = new AudioCtx();
      }
    }
  }
  playTone(freq, type, duration, vol = 0.2) {
    if (this.muted) return;
    this.init();
    if (!this.ctx) return;
    try {
      const osc = this.ctx.createOscillator();
      const gain = this.ctx.createGain();
      osc.type = type;
      osc.frequency.setValueAtTime(freq, this.ctx.currentTime);
      gain.gain.setValueAtTime(vol, this.ctx.currentTime);
      gain.gain.exponentialRampToValueAtTime(1e-3, this.ctx.currentTime + duration);
      osc.connect(gain);
      gain.connect(this.ctx.destination);
      osc.start();
      osc.stop(this.ctx.currentTime + duration);
    } catch {
    }
  }
  playStrike(comboIndex) {
    const freqs = [320, 440, 580];
    this.playTone(freqs[comboIndex % 3], "sawtooth", 0.12, 0.25);
  }
  playDash() {
    this.playTone(220, "sine", 0.15, 0.15);
  }
  playSign() {
    this.playTone(660, "triangle", 0.35, 0.3);
  }
  playHit() {
    this.playTone(140, "square", 0.12, 0.25);
  }
  playShadowTransform() {
    this.playTone(80, "sawtooth", 0.7, 0.4);
  }
  playHeal() {
    this.playTone(523.25, "sine", 0.2, 0.25);
    setTimeout(() => this.playTone(659.25, "sine", 0.3, 0.25), 100);
  }
  playFanfare() {
    [440, 554, 659, 880].forEach((f, i) => {
      setTimeout(() => this.playTone(f, "triangle", 0.4, 0.3), i * 140);
    });
  }
  playRealmUnlock() {
    const arpeggio = [261.63, 329.63, 392, 523.25, 659.25, 783.99, 1046.5];
    arpeggio.forEach((freq, idx) => {
      setTimeout(() => this.playTone(freq, "triangle", 0.5, 0.28), idx * 110);
    });
  }
}
const audio = new AudioController();
const state = {
  playerMonth: 8,
  playerYear: 2e3,
  playerAnimalIndex: 4,
  // Dragon
  guardianTitle: "Leo\u2013Dragon Guardian Prime",
  currentRealmIndex: 2,
  // Tiger Realm
  currentGate: 1,
  conqueredRealms: /* @__PURE__ */ new Set([0, 1]),
  unlockedRealms: /* @__PURE__ */ new Set([0, 1, 2]),
  totalDefeats: 0,
  campaignStartTime: Date.now(),
  constellationShards: 0,
  healingCharges: 1,
  unlockedCodex: /* @__PURE__ */ new Set(),
  player: {
    x: 0,
    y: 60,
    radius: 18,
    hp: 100,
    maxHp: 100,
    speed: 3.4,
    isDashing: false,
    dashTimer: 0,
    dashCooldown: 0,
    signCooldown: 0,
    comboStep: 0,
    comboTimer: 0,
    isAttacking: false,
    attackCooldown: 0,
    invulnerable: false,
    facingAngle: -Math.PI / 2
  },
  enemy: null,
  hazards: [],
  particles: [],
  dashTrails: [],
  joystick: { active: false, startX: 0, startY: 0, curX: 0, curY: 0, dx: 0, dy: 0 },
  isPractice: false,
  codexDates: {},
  viewWidth: 350,
  viewHeight: 720
};
function getAnimalIndexForYear(year) {
  return ((year - 2020) % 12 + 12) % 12;
}
function getZodiacMonth(monthId) {
  return ZODIAC_MONTHS[((monthId - 1) % 12 + 12) % 12];
}
function addParticle(x, y, color, size = 3, vx = 0, vy = 0) {
  state.particles.push({ x, y, color, size, vx, vy, life: 1 });
}
function addFloatingText(x, y, text, color = "#FFF") {
  const container = document.getElementById("game-container");
  if (!container) return;
  const centerX = (state.viewWidth || 350) / 2;
  const centerY = state.viewHeight ? (state.viewHeight - 70) / 2 : 320;
  const screenX = centerX + x;
  const screenY = centerY + y;
  const el = document.createElement("div");
  el.className = "floating-text";
  el.textContent = text;
  el.style.color = color;
  el.style.left = `${screenX}px`;
  el.style.top = `${screenY}px`;
  container.appendChild(el);
  setTimeout(() => el.remove(), 650);
}
function bindTouchAction(target, actionCallback) {
  const el = typeof target === "string" ? document.getElementById(target) : target;
  if (!el) return;
  const trigger = (e) => {
    e.preventDefault();
    e.stopPropagation();
    if (navigator.vibrate) {
      try {
        navigator.vibrate(14);
      } catch {
      }
    }
    el.classList.add("touch-active");
    if (actionCallback) actionCallback(e);
  };
  const release = () => {
    el.classList.remove("touch-active");
  };
  el.addEventListener("touchstart", trigger, { passive: false });
  el.addEventListener("touchend", release, { passive: false });
  el.addEventListener("touchcancel", release);
  el.addEventListener("click", (e) => {
    if (!e.defaultPrevented && actionCallback) actionCallback(e);
  });
}
let canvas = null;
let ctx = null;
let lastTime = performance.now();
let realmFxRaf = null;
let realmStars = [];
let realmSparks = [];
let realmBeamProgress = 0;
let realmBeamActive = false;
let realmBeamFrom = { x: 0, y: 0 };
let realmBeamTo = { x: 0, y: 0 };
function resizeCanvas() {
  if (!canvas || !ctx) return;
  const container = document.getElementById("game-container") || document.body;
  const rect = container.getBoundingClientRect();
  const dpr = Math.max(window.devicePixelRatio || 2, 2);
  state.viewWidth = rect.width || 350;
  state.viewHeight = rect.height || 720;
  canvas.width = Math.round(state.viewWidth * dpr);
  canvas.height = Math.round(state.viewHeight * dpr);
  canvas.style.width = `${state.viewWidth}px`;
  canvas.style.height = `${state.viewHeight}px`;
  ctx.resetTransform();
  ctx.scale(dpr, dpr);
}
function spawnGateEnemy() {
  const realmAnimal = CHINESE_ANIMALS[state.currentRealmIndex % 12];
  if (state.isPractice) {
    state.enemy = {
      name: "Astral Training Dummy",
      month: { id: 0, name: "Astral", glyph: "\u2726", color: "#FFD700", tech: "Light Spark" },
      animal: { name: "Dummy", emoji: "\u{1F3AF}", icon: "\u{1F3AF}", trait: "Stationary Practice Target", speedMult: 0, index: 0 },
      x: 0,
      y: -85,
      radius: 22,
      hp: 90,
      maxHp: 90,
      isShadow: false,
      isDummy: true,
      isChimera: false,
      transforming: false,
      speed: 0.2,
      attackTimer: 0,
      attackTelegraph: 0,
      telegraphType: "circle",
      facingAngle: Math.PI / 2,
      wobble: 0
    };
    const bossHud2 = document.getElementById("hud-boss");
    if (bossHud2) bossHud2.style.display = "flex";
    const bossName2 = document.getElementById("hud-boss-name");
    if (bossName2) bossName2.textContent = "Astral Training Puppet (Practice)";
    const bossPhase2 = document.getElementById("hud-boss-phase");
    if (bossPhase2) {
      bossPhase2.textContent = "Practice Target";
      bossPhase2.className = "boss-phase-tag";
    }
    const bossHp2 = document.getElementById("hud-boss-hp");
    if (bossHp2) {
      bossHp2.className = "bar-boss-fill";
      bossHp2.style.width = "100%";
    }
    const realmNameEl2 = document.getElementById("hud-realm-name");
    if (realmNameEl2) realmNameEl2.textContent = "Sparring Sanctum";
    const gateNumEl2 = document.getElementById("hud-gate-num");
    if (gateNumEl2) gateNumEl2.textContent = "TUT";
    return;
  }
  if (state.currentGate >= 13) {
    const isFinalTitan = state.currentRealmIndex === 11;
    const chimeraTitle = isFinalTitan ? "Chronos All-Zodiac Chimera Shadow Titan" : `${realmAnimal.name} Realm Chimera Boss`;
    state.enemy = {
      name: chimeraTitle,
      month: { id: 99, name: "All-Signs", glyph: "\u2742", color: "#FFD700", tech: "Omni-Burst" },
      animal: realmAnimal,
      x: 0,
      y: -105,
      radius: 30,
      hp: isFinalTitan ? 420 : 280,
      maxHp: isFinalTitan ? 420 : 280,
      isShadow: false,
      isChimera: true,
      isFinalBoss: isFinalTitan,
      transforming: false,
      speed: 2.2,
      attackTimer: 0,
      attackTelegraph: 0,
      telegraphType: "cone",
      facingAngle: Math.PI / 2,
      wobble: 0
    };
    const bossHud2 = document.getElementById("hud-boss");
    if (bossHud2) bossHud2.style.display = "flex";
    const bossName2 = document.getElementById("hud-boss-name");
    if (bossName2) bossName2.textContent = state.enemy.name;
    const bossPhase2 = document.getElementById("hud-boss-phase");
    if (bossPhase2) {
      bossPhase2.textContent = isFinalTitan ? "FINAL TITAN PHASE" : "CHIMERA BOSS";
      bossPhase2.className = "boss-phase-tag";
    }
    const bossHp2 = document.getElementById("hud-boss-hp");
    if (bossHp2) {
      bossHp2.className = "bar-boss-fill";
      bossHp2.style.width = "100%";
    }
    const realmNameEl2 = document.getElementById("hud-realm-name");
    if (realmNameEl2) realmNameEl2.textContent = isFinalTitan ? "The Astral Void" : `Apex of ${realmAnimal.name}`;
    const gateNumEl2 = document.getElementById("hud-gate-num");
    if (gateNumEl2) gateNumEl2.textContent = isFinalTitan ? "MAX" : "CHM";
    return;
  }
  const gateMonth = ZODIAC_MONTHS[(state.currentGate - 1) % 12];
  state.enemy = {
    name: `${gateMonth.name}\u2013${realmAnimal.name}`,
    month: gateMonth,
    animal: realmAnimal,
    x: 0,
    y: -100,
    radius: 24,
    hp: 150 + state.currentRealmIndex * 12,
    maxHp: 150 + state.currentRealmIndex * 12,
    isShadow: false,
    isChimera: false,
    transforming: false,
    speed: 1.8 + state.currentRealmIndex * 0.05,
    attackTimer: 0,
    attackTelegraph: 0,
    telegraphType: "cone",
    facingAngle: Math.PI / 2,
    wobble: 0
  };
  const bossHud = document.getElementById("hud-boss");
  if (bossHud) bossHud.style.display = "flex";
  const bossName = document.getElementById("hud-boss-name");
  if (bossName) bossName.textContent = state.enemy.name;
  const bossPhase = document.getElementById("hud-boss-phase");
  if (bossPhase) {
    bossPhase.textContent = "Guardian Phase";
    bossPhase.className = "boss-phase-tag";
  }
  const bossHp = document.getElementById("hud-boss-hp");
  if (bossHp) {
    bossHp.className = "bar-boss-fill";
    bossHp.style.width = "100%";
  }
  const realmNameEl = document.getElementById("hud-realm-name");
  if (realmNameEl) realmNameEl.textContent = `Realm of the ${realmAnimal.name} \u{1F5FA}\uFE0F`;
  const gateNumEl = document.getElementById("hud-gate-num");
  if (gateNumEl) gateNumEl.textContent = String(state.currentGate).padStart(2, "0");
}
function triggerShadowPhase() {
  if (!state.enemy || state.enemy.isShadow) return;
  state.enemy.transforming = true;
  state.enemy.hp = 0;
  const flash = document.getElementById("shadow-flash");
  if (flash) flash.style.opacity = "1";
  audio.playShadowTransform();
  addFloatingText(state.enemy.x, state.enemy.y - 20, state.enemy.isChimera ? "SHADOW CHIMERA ERUPTION!" : "SHADOW AWAKENING!", "#9B51E0");
  setTimeout(() => {
    if (!state.enemy) return;
    state.enemy.isShadow = true;
    state.enemy.transforming = false;
    const shadowHp = state.enemy.isChimera ? Math.round(state.enemy.maxHp * 1.3) : 220;
    state.enemy.hp = shadowHp;
    state.enemy.maxHp = shadowHp;
    state.enemy.speed = 2.6;
    if (flash) flash.style.opacity = "0";
    const bossPhase = document.getElementById("hud-boss-phase");
    if (bossPhase) {
      bossPhase.textContent = state.enemy.isChimera ? "Shadow Chimera Phase" : "Shadow Phase";
      bossPhase.className = "boss-phase-tag shadow";
    }
    const bossHp = document.getElementById("hud-boss-hp");
    if (bossHp) {
      bossHp.className = "bar-boss-fill shadow";
      bossHp.style.width = "100%";
    }
    state.hazards.push({
      x: (Math.random() - 0.5) * 140,
      y: (Math.random() - 0.5) * 140,
      radius: 36,
      pulse: 0
    });
  }, 1400);
}
function recordCodexEntry(monthId, animalIdx) {
  if (monthId < 1 || monthId > 12) return;
  const code = `${monthId}_${animalIdx}`;
  state.unlockedCodex.add(code);
  state.codexDates[code] = (/* @__PURE__ */ new Date()).toLocaleDateString(void 0, { month: "short", day: "numeric", year: "numeric" });
  try {
    localStorage.setItem("zodiac_fusion_codex", JSON.stringify(Array.from(state.unlockedCodex)));
    localStorage.setItem("zodiac_fusion_dates", JSON.stringify(state.codexDates));
  } catch {
  }
  updateCodexCounter();
}
function updateCodexCounter() {
  const counterEl = document.getElementById("codex-counter");
  if (counterEl) counterEl.textContent = `${state.unlockedCodex.size}/144`;
  const labelEl = document.getElementById("codex-progress-label");
  if (labelEl) labelEl.textContent = `${state.unlockedCodex.size} / 144 Fusions Defeated`;
}
function performStrike() {
  const p = state.player;
  if (p.attackCooldown > 0 || p.isDashing) return;
  audio.playStrike(p.comboStep);
  p.isAttacking = true;
  p.attackCooldown = 0.28;
  p.comboTimer = 0.7;
  const reach = 48;
  const hitX = p.x + Math.cos(p.facingAngle) * reach;
  const hitY = p.y + Math.sin(p.facingAngle) * reach;
  for (let i = 0; i < 6; i++) {
    addParticle(hitX, hitY, "#FFD700", 3, (Math.random() - 0.5) * 3, (Math.random() - 0.5) * 3);
  }
  if (state.enemy && !state.enemy.transforming && state.enemy.hp > 0) {
    const dist = Math.hypot(hitX - state.enemy.x, hitY - state.enemy.y);
    if (dist < state.enemy.radius + 32) {
      let dmg = 24 + p.comboStep * 12;
      if (state.playerAnimalIndex === 2 && p.comboStep === 2) {
        dmg *= 2;
        addFloatingText(state.enemy.x, state.enemy.y - 10, "TIGER CRIT!", "#FFD700");
      }
      if (state.playerAnimalIndex === 11 && p.comboStep === 2) {
        p.hp = Math.min(p.maxHp, p.hp + 6);
        updatePlayerHpUI();
      }
      damageEnemy(dmg);
    }
  }
  p.comboStep = (p.comboStep + 1) % 3;
}
function performDash() {
  const p = state.player;
  if (p.dashCooldown > 0) return;
  audio.playDash();
  p.isDashing = true;
  p.dashTimer = 0.3;
  p.dashCooldown = state.playerAnimalIndex === 3 ? 0.7 : 1.2;
  p.invulnerable = true;
  state.dashTrails.push({
    x: p.x,
    y: p.y,
    angle: p.facingAngle,
    isDragon: state.playerAnimalIndex === 4,
    life: 0.6
  });
}
function performSignTechnique() {
  const p = state.player;
  if (p.signCooldown > 0) return;
  audio.playSign();
  p.signCooldown = 6;
  const overlay = document.getElementById("sign-cooldown-overlay");
  if (overlay) {
    overlay.style.clipPath = "polygon(50% 50%, 50% 0, 100% 0, 100% 100%, 0 100%, 0 0, 50% 0)";
  }
  const month = ZODIAC_MONTHS[(state.playerMonth - 1) % 12];
  addFloatingText(p.x, p.y - 30, month.tech, "#FFD700");
  if (month.id === 8) {
    if (state.enemy && state.enemy.hp > 0) {
      const dist = Math.hypot(state.enemy.x - p.x, state.enemy.y - p.y);
      if (dist < 140) {
        damageEnemy(45);
        addFloatingText(state.enemy.x, state.enemy.y, "SOLAR BURN!", "#FF4444");
      }
    }
  } else if (month.id === 1) {
    p.invulnerable = true;
    setTimeout(() => {
      p.invulnerable = false;
    }, 2e3);
  } else if (month.id === 9) {
    p.hp = Math.min(p.maxHp, p.hp + 35);
    updatePlayerHpUI();
    addFloatingText(p.x, p.y - 20, "+35 HEAL", "#4EBA6F");
  } else {
    if (state.enemy && state.enemy.hp > 0) {
      damageEnemy(40);
    }
  }
}
function performHeal() {
  if (state.healingCharges > 0 && state.player.hp < state.player.maxHp) {
    state.healingCharges--;
    state.player.hp = Math.min(state.player.maxHp, state.player.hp + 40);
    const healCountEl = document.getElementById("heal-charges-count");
    if (healCountEl) healCountEl.textContent = String(state.healingCharges);
    updatePlayerHpUI();
    audio.playHeal();
    addFloatingText(state.player.x, state.player.y - 20, "+40 HEAL", "#4EBA6F");
  }
}
function damageEnemy(amount) {
  if (!state.enemy || state.enemy.hp <= 0) return;
  state.enemy.hp -= amount;
  audio.playHit();
  addFloatingText(state.enemy.x, state.enemy.y - 15, `-${amount}`, state.enemy.isShadow ? "#9B51E0" : "#FFD700");
  const pct = Math.max(0, state.enemy.hp / state.enemy.maxHp * 100);
  const bossHpEl = document.getElementById("hud-boss-hp");
  if (bossHpEl) bossHpEl.style.width = `${pct}%`;
  if (state.enemy.hp <= 0) {
    if (state.enemy.isDummy) {
      audio.playFanfare();
      addFloatingText(0, 0, "PRACTICE DUMMY DEFEATED!", "#FFD700");
      state.enemy = null;
      const bossHud = document.getElementById("hud-boss");
      if (bossHud) bossHud.style.display = "none";
      setTimeout(() => {
        state.isPractice = false;
        state.currentGate = 1;
        state.player.hp = state.player.maxHp;
        updatePlayerHpUI();
        spawnGateEnemy();
      }, 1500);
      return;
    }
    if (!state.enemy.isShadow) {
      triggerShadowPhase();
    } else {
      onGateVictory();
    }
  }
}
function onGateVictory() {
  audio.playFanfare();
  if (state.enemy && state.enemy.month && state.enemy.animal) {
    recordCodexEntry(state.enemy.month.id, state.enemy.animal.index);
  }
  state.constellationShards++;
  updateShardsUI();
  if (state.currentGate >= 12) {
    addFloatingText(0, 0, "\u2726 ALL 12 GATES CONQUERED! \u2726", "#FFD700");
    setTimeout(() => {
      const cur = state.currentRealmIndex;
      const next = (state.currentRealmIndex + 1) % 12;
      triggerRealmUnlockSequence(cur, next);
    }, 1500);
  } else {
    addFloatingText(0, 0, "GATE CLEARED!", "#FFD700");
    setTimeout(() => {
      state.currentGate++;
      spawnGateEnemy();
    }, 1600);
  }
}
function updateShardsUI() {
  const s1 = document.getElementById("shard-1");
  const s2 = document.getElementById("shard-2");
  const s3 = document.getElementById("shard-3");
  const count = state.constellationShards % 3;
  if (s1) s1.className = count >= 1 ? "shard-gem active" : "shard-gem";
  if (s2) s2.className = count >= 2 ? "shard-gem active" : "shard-gem";
  if (s3) s3.className = "shard-gem";
  if (state.constellationShards > 0 && state.constellationShards % 3 === 0) {
    state.healingCharges = Math.min(3, state.healingCharges + 1);
    const healCountEl = document.getElementById("heal-charges-count");
    if (healCountEl) healCountEl.textContent = String(state.healingCharges);
    addFloatingText(state.player.x, state.player.y - 25, "+1 HEAL CHARGE!", "#4EBA6F");
  }
}
function updatePlayerHpUI() {
  const pct = Math.max(0, state.player.hp / state.player.maxHp * 100);
  const hpEl = document.getElementById("hud-player-hp");
  if (hpEl) hpEl.style.width = `${pct}%`;
}
function damagePlayer(dmg) {
  const p = state.player;
  if (p.invulnerable) return;
  if (state.playerAnimalIndex === 1) {
    dmg = Math.round(dmg * 0.85);
  }
  p.hp = Math.max(0, p.hp - dmg);
  updatePlayerHpUI();
  audio.playHit();
  addFloatingText(p.x, p.y - 15, `-${dmg}`, "#FF4B4B");
  if (p.hp <= 0) {
    state.totalDefeats++;
    addFloatingText(p.x, p.y, "DEFEATED - RESTARTING GATE", "#FF4444");
    setTimeout(() => {
      p.hp = p.maxHp;
      updatePlayerHpUI();
      spawnGateEnemy();
    }, 1200);
  }
}
function update(dt) {
  const p = state.player;
  let moveX = 0;
  let moveY = 0;
  if (state.joystick.active) {
    moveX = state.joystick.dx;
    moveY = state.joystick.dy;
  }
  const baseSpeed = state.playerAnimalIndex === 6 ? p.speed * 1.25 : p.speed;
  if (p.isDashing) {
    p.dashTimer -= dt;
    p.x += Math.cos(p.facingAngle) * baseSpeed * 2.8;
    p.y += Math.sin(p.facingAngle) * baseSpeed * 2.8;
    if (p.dashTimer <= 0) {
      p.isDashing = false;
      p.invulnerable = false;
    }
  } else if (Math.hypot(moveX, moveY) > 0.05) {
    p.facingAngle = Math.atan2(moveY, moveX);
    p.x += moveX * baseSpeed;
    p.y += moveY * baseSpeed;
  }
  const arenaRadius = 180;
  const playerDist = Math.hypot(p.x, p.y);
  if (playerDist > arenaRadius - p.radius) {
    const ang = Math.atan2(p.y, p.x);
    p.x = Math.cos(ang) * (arenaRadius - p.radius);
    p.y = Math.sin(ang) * (arenaRadius - p.radius);
  }
  if (p.dashCooldown > 0) p.dashCooldown -= dt;
  if (p.attackCooldown > 0) p.attackCooldown -= dt;
  if (p.comboTimer > 0) {
    p.comboTimer -= dt;
    if (p.comboTimer <= 0) p.comboStep = 0;
  }
  if (p.signCooldown > 0) {
    p.signCooldown -= dt;
    const pct = Math.max(0, p.signCooldown / 6);
    const deg = Math.round(pct * 360);
    const overlay = document.getElementById("sign-cooldown-overlay");
    if (overlay) {
      overlay.style.clipPath = `polygon(50% 50%, 50% 0, ${deg > 180 ? "100% 0, 100% 100%, 0 100%, 0 0" : "100% 0, 100% 50%"}, 50% 0)`;
    }
  }
  const e = state.enemy;
  if (e && !e.transforming && e.hp > 0) {
    e.wobble += dt * 4;
    const toPlayerX = p.x - e.x;
    const toPlayerY = p.y - e.y;
    const dist = Math.hypot(toPlayerX, toPlayerY);
    if (dist > 45) {
      e.facingAngle = Math.atan2(toPlayerY, toPlayerX);
      e.x += toPlayerX / dist * e.speed;
      e.y += toPlayerY / dist * e.speed;
    }
    e.attackTimer += dt;
    if (e.attackTimer > (e.isShadow ? 1.8 : 2.5)) {
      e.attackTelegraph += dt;
      if (e.attackTelegraph > (e.isShadow ? 0.7 : 0.95)) {
        if (dist < 65) {
          damagePlayer(e.isShadow ? 28 : 18);
        }
        e.attackTimer = 0;
        e.attackTelegraph = 0;
      }
    }
  }
  state.hazards.forEach((h) => {
    h.pulse += dt * 3;
    const d = Math.hypot(p.x - h.x, p.y - h.y);
    if (d < h.radius) {
      damagePlayer(1);
    }
  });
  state.dashTrails.forEach((t) => {
    t.life -= dt;
    if (t.isDragon && e && e.hp > 0) {
      const d = Math.hypot(e.x - t.x, e.y - t.y);
      if (d < 30) damageEnemy(2);
    }
  });
  state.dashTrails = state.dashTrails.filter((t) => t.life > 0);
  state.particles.forEach((pt) => {
    pt.x += pt.vx;
    pt.y += pt.vy;
    pt.life -= dt * 1.5;
  });
  state.particles = state.particles.filter((pt) => pt.life > 0);
}
function render() {
  if (!canvas || !ctx) return;
  ctx.clearRect(0, 0, canvas.width, canvas.height);
  const cx = canvas.width / 2;
  const cy = canvas.height / 2;
  ctx.save();
  ctx.translate(cx, cy);
  const arenaR = 190;
  const grad = ctx.createRadialGradient(0, 0, arenaR * 0.4, 0, 0, arenaR);
  grad.addColorStop(0, "#151822");
  grad.addColorStop(0.85, "#10121A");
  grad.addColorStop(1, "#0F111A");
  ctx.fillStyle = grad;
  ctx.beginPath();
  ctx.arc(0, 0, arenaR, 0, Math.PI * 2);
  ctx.fill();
  ctx.strokeStyle = "#333741";
  ctx.lineWidth = 1.5;
  ctx.setLineDash([6, 6]);
  ctx.beginPath();
  ctx.arc(0, 0, arenaR * 0.9, 0, Math.PI * 2);
  ctx.stroke();
  ctx.strokeStyle = "#232733";
  ctx.setLineDash([]);
  ctx.beginPath();
  ctx.arc(0, 0, arenaR * 0.65, 0, Math.PI * 2);
  ctx.stroke();
  for (let i = 0; i < 12; i++) {
    const ang = i / 12 * Math.PI * 2 - Math.PI / 2;
    const mx = Math.cos(ang) * (arenaR - 16);
    const my = Math.sin(ang) * (arenaR - 16);
    ctx.fillStyle = "rgba(212,175,55,0.35)";
    ctx.font = "11px sans-serif";
    ctx.textAlign = "center";
    ctx.textBaseline = "middle";
    ctx.fillText(ZODIAC_MONTHS[i].glyph, mx, my);
  }
  state.hazards.forEach((h) => {
    if (!ctx) return;
    ctx.fillStyle = "rgba(155,81,224,0.18)";
    ctx.beginPath();
    ctx.arc(h.x, h.y, h.radius + Math.sin(h.pulse) * 4, 0, Math.PI * 2);
    ctx.fill();
    ctx.strokeStyle = "#9B51E0";
    ctx.lineWidth = 1.5;
    ctx.stroke();
  });
  state.dashTrails.forEach((t) => {
    if (!ctx) return;
    ctx.fillStyle = t.isDragon ? "rgba(255,100,50,0.35)" : "rgba(212,175,55,0.2)";
    ctx.beginPath();
    ctx.arc(t.x, t.y, 16 * t.life, 0, Math.PI * 2);
    ctx.fill();
  });
  const e = state.enemy;
  if (e && e.attackTelegraph > 0) {
    ctx.fillStyle = e.isShadow ? "rgba(155,81,224,0.3)" : "rgba(255,75,75,0.25)";
    ctx.strokeStyle = e.isShadow ? "#9B51E0" : "#FF4B4B";
    ctx.lineWidth = 2;
    ctx.beginPath();
    ctx.arc(e.x, e.y, 60, e.facingAngle - 0.7, e.facingAngle + 0.7);
    ctx.lineTo(e.x, e.y);
    ctx.closePath();
    ctx.fill();
    ctx.stroke();
  }
  if (e && e.hp > 0) {
    ctx.save();
    ctx.translate(e.x, e.y);
    ctx.fillStyle = e.isShadow ? "rgba(155,81,224,0.25)" : "rgba(0,0,0,0.4)";
    ctx.beginPath();
    ctx.ellipse(0, 16, e.radius, 8, 0, 0, Math.PI * 2);
    ctx.fill();
    ctx.fillStyle = e.isShadow ? "rgba(155,81,224,0.15)" : "rgba(212,175,55,0.12)";
    ctx.beginPath();
    ctx.arc(0, 0, e.radius + 8, 0, Math.PI * 2);
    ctx.fill();
    ctx.fillStyle = e.isShadow ? "#1C1526" : "#1A1C22";
    ctx.strokeStyle = e.isShadow ? "#9B51E0" : "#D4AF37";
    ctx.lineWidth = 2;
    ctx.beginPath();
    ctx.arc(0, 0, e.radius, 0, Math.PI * 2);
    ctx.fill();
    ctx.stroke();
    ctx.font = "16px sans-serif";
    ctx.textAlign = "center";
    ctx.textBaseline = "middle";
    ctx.fillText(e.animal.icon, 0, 2);
    ctx.fillStyle = e.isShadow ? "#FF4444" : "#FFD700";
    ctx.font = "14px sans-serif";
    ctx.fillText(e.month.glyph, 0, -26);
    ctx.restore();
  }
  const p = state.player;
  ctx.save();
  ctx.translate(p.x, p.y);
  ctx.fillStyle = "rgba(212,175,55,0.2)";
  ctx.beginPath();
  ctx.ellipse(0, 14, p.radius, 7, 0, 0, Math.PI * 2);
  ctx.fill();
  ctx.fillStyle = "rgba(212,175,55,0.15)";
  ctx.beginPath();
  ctx.arc(0, 0, p.radius + 6, 0, Math.PI * 2);
  ctx.fill();
  ctx.fillStyle = "#1A1C22";
  ctx.strokeStyle = p.invulnerable ? "#00E5FF" : "#FFD700";
  ctx.lineWidth = 2.5;
  ctx.beginPath();
  ctx.arc(0, 0, p.radius, 0, Math.PI * 2);
  ctx.fill();
  ctx.stroke();
  const wx = Math.cos(p.facingAngle) * 22;
  const wy = Math.sin(p.facingAngle) * 22;
  ctx.fillStyle = "#FFD700";
  ctx.beginPath();
  ctx.arc(wx, wy, 4, 0, Math.PI * 2);
  ctx.fill();
  ctx.fillStyle = "#FFD700";
  ctx.font = "16px sans-serif";
  ctx.textAlign = "center";
  ctx.textBaseline = "middle";
  const pMonth = ZODIAC_MONTHS[(state.playerMonth - 1) % 12];
  ctx.fillText(pMonth.glyph, 0, -22);
  const pAnimal = CHINESE_ANIMALS[state.playerAnimalIndex % 12];
  ctx.font = "14px sans-serif";
  ctx.fillText(pAnimal.icon, 0, 1);
  ctx.restore();
  state.particles.forEach((pt) => {
    if (!ctx) return;
    ctx.fillStyle = pt.color;
    ctx.globalAlpha = pt.life;
    ctx.beginPath();
    ctx.arc(pt.x, pt.y, pt.size, 0, Math.PI * 2);
    ctx.fill();
  });
  ctx.globalAlpha = 1;
  ctx.restore();
}
function gameLoop(now) {
  const dt = Math.min(0.1, (now - lastTime) / 1e3);
  lastTime = now;
  update(dt);
  render();
  requestAnimationFrame(gameLoop);
}
function renderRealmMapGrid(illuminatingIdx = -1) {
  const grid = document.getElementById("realm-map-grid");
  if (!grid) return;
  grid.innerHTML = "";
  CHINESE_ANIMALS.forEach((animal, idx) => {
    const node = document.createElement("div");
    const isConquered = state.conqueredRealms.has(idx);
    const isUnlocked = state.unlockedRealms.has(idx);
    const isCurrent = idx === state.currentRealmIndex;
    const isIlluminating = idx === illuminatingIdx;
    node.className = "realm-node";
    node.id = `realm-node-${idx}`;
    if (isIlluminating) {
      node.classList.add("illuminating");
    } else if (isConquered) {
      node.classList.add("conquered");
    } else if (isCurrent || isUnlocked) {
      node.classList.add("active-current");
    } else {
      node.classList.add("locked");
    }
    let statusText = isConquered ? "Cleared" : isUnlocked ? "Unlocked" : "Locked";
    if (isIlluminating) statusText = "UNLOCKED!";
    node.innerHTML = `
      ${isConquered ? '<span class="realm-conquered-star">\u2713</span>' : ""}
      <div class="realm-node-icon">${animal.icon}</div>
      <div class="realm-node-name">${animal.name}</div>
      <div class="realm-node-status">${statusText}</div>
    `;
    bindTouchAction(node, () => {
      showRealmInfoPreview(idx);
    });
    grid.appendChild(node);
  });
}
function showRealmInfoPreview(idx) {
  const animal = CHINESE_ANIMALS[idx];
  const isConquered = state.conqueredRealms.has(idx);
  const isUnlocked = state.unlockedRealms.has(idx);
  const bigIconEl = document.getElementById("new-realm-big-icon");
  if (bigIconEl) bigIconEl.textContent = animal.icon;
  const nameEl = document.getElementById("new-realm-name");
  if (nameEl) nameEl.textContent = `Realm of the ${animal.name}`;
  const tagEl = document.getElementById("new-realm-tag");
  if (tagEl) {
    tagEl.textContent = isConquered ? "\u2726 REALM MASTERED \u2726" : isUnlocked ? "\u2726 REALM ACCESSIBLE \u2726" : "\u2726 REALM SEALED \u{1F512} \u2726";
  }
  const descEl = document.getElementById("new-realm-desc");
  if (descEl) {
    descEl.textContent = isConquered ? `All 12 monthly gates mastered! Passive trait: ${animal.trait}.` : isUnlocked ? `12 monthly gates & shadow chimera challenge. Trait: ${animal.trait}.` : `Defeat earlier realm gates to shatter this celestial seal.`;
  }
}
function getNodeCenter(idx) {
  const fxCanvas = document.getElementById("realm-fx-canvas");
  const node = document.getElementById(`realm-node-${idx}`);
  if (!fxCanvas || !node) return { x: 180, y: 150 };
  const cRect = fxCanvas.getBoundingClientRect();
  const nRect = node.getBoundingClientRect();
  return {
    x: nRect.left + nRect.width / 2 - cRect.left,
    y: nRect.top + nRect.height / 2 - cRect.top
  };
}
function initRealmFxCanvas(fromIdx, toIdx) {
  const fxCanvas = document.getElementById("realm-fx-canvas");
  if (!fxCanvas) return;
  const fxCtx = fxCanvas.getContext("2d");
  if (!fxCtx) return;
  if (realmFxRaf) cancelAnimationFrame(realmFxRaf);
  realmStars = [];
  for (let i = 0; i < 40; i++) {
    realmStars.push({
      x: Math.random() * fxCanvas.width,
      y: Math.random() * fxCanvas.height,
      radius: Math.random() * 1.5 + 0.5,
      alpha: Math.random() * 0.8 + 0.2,
      speed: Math.random() * 0.02 + 0.01
    });
  }
  realmSparks = [];
  realmBeamProgress = 0;
  realmBeamActive = true;
  realmBeamFrom = getNodeCenter(fromIdx);
  realmBeamTo = getNodeCenter(toIdx);
  function loop() {
    if (!fxCanvas || !fxCtx) return;
    fxCtx.clearRect(0, 0, fxCanvas.width, fxCanvas.height);
    realmStars.forEach((s) => {
      s.alpha += s.speed;
      if (s.alpha > 1 || s.alpha < 0.2) s.speed = -s.speed;
      fxCtx.fillStyle = `rgba(255, 215, 0, ${Math.max(0, Math.min(1, s.alpha))})`;
      fxCtx.beginPath();
      fxCtx.arc(s.x, s.y, s.radius, 0, Math.PI * 2);
      fxCtx.fill();
    });
    if (realmBeamActive) {
      realmBeamProgress = Math.min(1, realmBeamProgress + 0.025);
      const curX = realmBeamFrom.x + (realmBeamTo.x - realmBeamFrom.x) * realmBeamProgress;
      const curY = realmBeamFrom.y + (realmBeamTo.y - realmBeamFrom.y) * realmBeamProgress;
      fxCtx.save();
      fxCtx.strokeStyle = "rgba(255, 215, 0, 0.75)";
      fxCtx.lineWidth = 2.5;
      fxCtx.shadowColor = "#FFD700";
      fxCtx.shadowBlur = 10;
      fxCtx.beginPath();
      fxCtx.moveTo(realmBeamFrom.x, realmBeamFrom.y);
      fxCtx.lineTo(curX, curY);
      fxCtx.stroke();
      fxCtx.fillStyle = "#00E5FF";
      fxCtx.shadowColor = "#00E5FF";
      fxCtx.shadowBlur = 14;
      fxCtx.beginPath();
      fxCtx.arc(curX, curY, 4, 0, Math.PI * 2);
      fxCtx.fill();
      fxCtx.restore();
      if (realmBeamProgress < 1 && Math.random() < 0.6) {
        realmSparks.push({
          x: curX,
          y: curY,
          vx: (Math.random() - 0.5) * 3,
          vy: (Math.random() - 0.5) * 3,
          life: 1,
          color: Math.random() < 0.5 ? "#FFD700" : "#00E5FF"
        });
      }
    }
    for (let i = realmSparks.length - 1; i >= 0; i--) {
      const sp = realmSparks[i];
      sp.x += sp.vx;
      sp.y += sp.vy;
      sp.life -= 0.035;
      if (sp.life <= 0) {
        realmSparks.splice(i, 1);
        continue;
      }
      fxCtx.fillStyle = sp.color;
      fxCtx.globalAlpha = sp.life;
      fxCtx.beginPath();
      fxCtx.arc(sp.x, sp.y, 2, 0, Math.PI * 2);
      fxCtx.fill();
    }
    fxCtx.globalAlpha = 1;
    realmFxRaf = requestAnimationFrame(loop);
  }
  loop();
}
function spawnRealmBurstParticles(nodeIdx) {
  const center = getNodeCenter(nodeIdx);
  for (let i = 0; i < 35; i++) {
    const angle = Math.random() * Math.PI * 2;
    const speed = Math.random() * 4.5 + 1.5;
    realmSparks.push({
      x: center.x,
      y: center.y,
      vx: Math.cos(angle) * speed,
      vy: Math.sin(angle) * speed,
      life: 1,
      color: Math.random() < 0.4 ? "#00E5FF" : Math.random() < 0.7 ? "#FFD700" : "#FFF"
    });
  }
}
function triggerRealmUnlockSequence(fromRealm = state.currentRealmIndex, toRealm = (state.currentRealmIndex + 1) % 12) {
  state.conqueredRealms.add(fromRealm);
  state.unlockedRealms.add(toRealm);
  const fromAnimal = CHINESE_ANIMALS[fromRealm];
  const toAnimal = CHINESE_ANIMALS[toRealm];
  const modal = document.getElementById("modal-realm-unlock");
  if (modal) modal.classList.add("active");
  const badgeEl = document.getElementById("realm-unlock-badge");
  if (badgeEl) badgeEl.textContent = `\u2726 REALM XII CONQUERED \u2726`;
  const titleEl = document.getElementById("realm-unlock-title");
  if (titleEl) titleEl.textContent = `Realm of the ${fromAnimal.name} Mastered!`;
  const subEl = document.getElementById("realm-unlock-subtitle");
  if (subEl) subEl.textContent = `All 12 gates cleared! Celestial starlight flows to the next realm...`;
  const closeBtn = document.getElementById("btn-close-realm-map");
  if (closeBtn) closeBtn.style.display = "none";
  const banner = document.getElementById("new-realm-banner");
  if (banner) banner.classList.remove("illuminated");
  renderRealmMapGrid(-1);
  showRealmInfoPreview(fromRealm);
  setTimeout(() => {
    initRealmFxCanvas(fromRealm, toRealm);
  }, 50);
  setTimeout(() => {
    if (subEl) subEl.textContent = `Constellation rays illuminate the Earthly Branch of the ${toAnimal.name}...`;
    audio.playTone(330, "sine", 0.35, 0.25);
  }, 800);
  setTimeout(() => {
    renderRealmMapGrid(toRealm);
    if (banner) banner.classList.add("illuminated");
    const bigIcon = document.getElementById("new-realm-big-icon");
    if (bigIcon) bigIcon.textContent = toAnimal.icon;
    const realmName = document.getElementById("new-realm-name");
    if (realmName) realmName.textContent = `Realm of the ${toAnimal.name}`;
    const realmTag = document.getElementById("new-realm-tag");
    if (realmTag) realmTag.textContent = `\u2726 REALM ILLUMINATED & UNLOCKED \u2726`;
    const realmDesc = document.getElementById("new-realm-desc");
    if (realmDesc) realmDesc.textContent = `Gate 01 is open! 12 new monthly zodiac combos await your guardian.`;
    const enterBtn = document.getElementById("btn-enter-new-realm");
    if (enterBtn) enterBtn.textContent = `\u2694\uFE0F Enter Realm of the ${toAnimal.name} \u2192`;
    spawnRealmBurstParticles(toRealm);
    audio.playRealmUnlock();
    if (navigator.vibrate) {
      try {
        navigator.vibrate([40, 80, 120]);
      } catch {
      }
    }
  }, 1800);
}
function openRealmMapModal() {
  renderRealmMapGrid(-1);
  const curAnimal = CHINESE_ANIMALS[state.currentRealmIndex];
  showRealmInfoPreview(state.currentRealmIndex);
  const badgeEl = document.getElementById("realm-unlock-badge");
  if (badgeEl) badgeEl.textContent = `\u2726 12-REALM CELESTIAL MAP \u2726`;
  const titleEl = document.getElementById("realm-unlock-title");
  if (titleEl) titleEl.textContent = `Realm of the ${curAnimal.name}`;
  const subEl = document.getElementById("realm-unlock-subtitle");
  if (subEl) subEl.textContent = `Gate ${state.currentGate}/12 in progress. Tap any realm node to inspect traits.`;
  const closeBtn = document.getElementById("btn-close-realm-map");
  if (closeBtn) closeBtn.style.display = "block";
  const enterBtn = document.getElementById("btn-enter-new-realm");
  if (enterBtn) enterBtn.textContent = `\u2694\uFE0F Resume Gate ${state.currentGate}`;
  const modal = document.getElementById("modal-realm-unlock");
  if (modal) modal.classList.add("active");
}
function enterUnlockedRealm() {
  const nextRealm = (state.currentRealmIndex + 1) % 12;
  state.currentRealmIndex = nextRealm;
  state.currentGate = 1;
  state.hazards = [];
  state.player.hp = state.player.maxHp;
  updatePlayerHpUI();
  const modal = document.getElementById("modal-realm-unlock");
  if (modal) modal.classList.remove("active");
  if (realmFxRaf) cancelAnimationFrame(realmFxRaf);
  spawnGateEnemy();
  audio.playFanfare();
  addFloatingText(0, 0, `ENTERED REALM OF THE ${CHINESE_ANIMALS[nextRealm].name.toUpperCase()}!`, "#FFD700");
}
function showVictoryScreen(isFinalTitan = false) {
  const elapsedSec = Math.floor((Date.now() - state.campaignStartTime) / 1e3);
  const mins = String(Math.floor(elapsedSec / 60)).padStart(2, "0");
  const secs = String(elapsedSec % 60).padStart(2, "0");
  const vicBadge = document.getElementById("vic-badge");
  const vicTitle = document.querySelector("#modal-victory .modal-title");
  const vicSub = document.getElementById("vic-sub");
  const btnNextRealm = document.getElementById("btn-next-realm");
  if (isFinalTitan) {
    if (vicBadge) vicBadge.textContent = "\u{1F451}";
    if (vicTitle) vicTitle.textContent = "Final Shadow Titan Overthrown!";
    if (vicSub) vicSub.textContent = "You conquered all 12 Yearly Realms and conquered the Chronos All-Zodiac Chimera!";
    if (btnNextRealm) btnNextRealm.textContent = "Ascend Again (New NG+ Cycle)";
  } else {
    if (vicBadge) vicBadge.textContent = "\u{1F3C6}";
    if (vicTitle) vicTitle.textContent = `${CHINESE_ANIMALS[state.currentRealmIndex].name} Realm Conquered!`;
    if (vicSub) vicSub.textContent = "Yearly Chimera Boss defeated! Advance into the next celestial realm.";
    if (btnNextRealm) btnNextRealm.textContent = `Advance to ${CHINESE_ANIMALS[(state.currentRealmIndex + 1) % 12].name} Realm`;
  }
  const nameEl = document.getElementById("vic-guardian-name");
  if (nameEl) nameEl.textContent = state.guardianTitle.replace(" Prime", "");
  const timeEl = document.getElementById("vic-time");
  if (timeEl) timeEl.textContent = `${mins}:${secs}`;
  const defeatsEl = document.getElementById("vic-defeats");
  if (defeatsEl) defeatsEl.textContent = String(state.totalDefeats);
  const gatesEl = document.getElementById("vic-gates-cleared");
  if (gatesEl) gatesEl.textContent = isFinalTitan ? "12 / 12 Realms" : `${state.currentGate} / 12 Gates`;
  const modal = document.getElementById("modal-victory");
  if (modal) modal.classList.add("active");
}
function populateCodexGrid() {
  const codexGrid = document.getElementById("codex-grid");
  if (!codexGrid) return;
  codexGrid.innerHTML = "";
  for (let m = 1; m <= 12; m++) {
    for (let a = 0; a < 12; a++) {
      const month = ZODIAC_MONTHS[m - 1];
      const animal = CHINESE_ANIMALS[a];
      const code = `${m}_${a}`;
      const isUnlocked = state.unlockedCodex.has(code);
      const slot = document.createElement("div");
      slot.className = `codex-slot ${isUnlocked ? "unlocked" : "locked"}`;
      slot.innerHTML = `
        <div class="codex-slot-icon">${isUnlocked ? month.glyph + animal.icon : "\u{1F512}"}</div>
        <div class="codex-slot-label">${isUnlocked ? `${month.name.slice(0, 3)}-${animal.name.slice(0, 3)}` : "???"}</div>
      `;
      bindTouchAction(slot, () => {
        showCodexDetail(month, animal, isUnlocked);
      });
      codexGrid.appendChild(slot);
    }
  }
}
function showCodexDetail(month, animal, isUnlocked) {
  const code = `${month.id}_${animal.index}`;
  const encDate = state.codexDates[code] || "Recorded in Campaign";
  const emblem = document.getElementById("detail-emblem");
  if (emblem) emblem.textContent = isUnlocked ? month.glyph + " " + animal.icon : "\u{1F512}";
  const title = document.getElementById("detail-title");
  if (title) title.textContent = isUnlocked ? `${month.name}\u2013${animal.name} Guardian` : "Undiscovered Mythic Fusion";
  const status = document.getElementById("detail-status");
  if (status) status.textContent = isUnlocked ? `Defeated \u2022 ${encDate}` : "Locked in Gate Realm";
  const lore = document.getElementById("detail-lore");
  if (lore) {
    lore.innerHTML = isUnlocked ? `<strong>Active Technique:</strong> ${month.tech}<br><strong>Animal Trait:</strong> ${animal.trait}<br><br>A celestial guardian channeling the starry aura of ${month.name} fused with the ${animal.name} spirit.` : `Conquer this guardian in the ${animal.name} realm or monthly gate to unlock its cel-shaded codex archives and recorded attributes.`;
  }
  const modal = document.getElementById("modal-codex-detail");
  if (modal) modal.classList.add("active");
}
function generateShareCard() {
  const cardCanvas = document.getElementById("share-card-canvas");
  if (!cardCanvas) return;
  const sctx = cardCanvas.getContext("2d");
  if (!sctx) return;
  const bgGrad = sctx.createLinearGradient(0, 0, 1200, 630);
  bgGrad.addColorStop(0, "#0F111A");
  bgGrad.addColorStop(0.5, "#161822");
  bgGrad.addColorStop(1, "#0F111A");
  sctx.fillStyle = bgGrad;
  sctx.fillRect(0, 0, 1200, 630);
  sctx.strokeStyle = "#D4AF37";
  sctx.lineWidth = 8;
  sctx.strokeRect(24, 24, 1152, 582);
  sctx.strokeStyle = "rgba(212,175,55,0.25)";
  sctx.lineWidth = 2;
  sctx.beginPath();
  sctx.arc(600, 315, 260, 0, Math.PI * 2);
  sctx.stroke();
  sctx.fillStyle = "#FFD700";
  sctx.font = "bold 44px sans-serif";
  sctx.textAlign = "center";
  sctx.fillText("ZODIAC FUSION ARENA", 600, 110);
  const m = ZODIAC_MONTHS[state.playerMonth - 1];
  const a = CHINESE_ANIMALS[state.playerAnimalIndex];
  sctx.font = "72px sans-serif";
  sctx.fillText(`${m.glyph} ${a.icon}`, 600, 220);
  sctx.fillStyle = "#FFFFFF";
  sctx.font = "bold 36px sans-serif";
  sctx.fillText(state.guardianTitle, 600, 290);
  sctx.fillStyle = "#1A1C22";
  sctx.fillRect(200, 340, 800, 140);
  sctx.strokeStyle = "#333741";
  sctx.lineWidth = 2;
  sctx.strokeRect(200, 340, 800, 140);
  const elapsedSec = Math.floor((Date.now() - state.campaignStartTime) / 1e3);
  const mins = String(Math.floor(elapsedSec / 60)).padStart(2, "0");
  const secs = String(elapsedSec % 60).padStart(2, "0");
  sctx.fillStyle = "#9EA1A8";
  sctx.font = "18px sans-serif";
  sctx.fillText("CAMPAIGN TIME", 350, 390);
  sctx.fillText("DEFEATS", 600, 390);
  sctx.fillText("GATES CONQUERED", 850, 390);
  sctx.fillStyle = "#FFD700";
  sctx.font = "bold 34px sans-serif";
  sctx.fillText(`${mins}:${secs}`, 350, 440);
  sctx.fillText(`${state.totalDefeats}`, 600, 440);
  sctx.fillText(`${state.currentGate} / 12`, 850, 440);
  sctx.fillStyle = "#D4AF37";
  sctx.font = "bold 22px sans-serif";
  sctx.fillText("\u25B6 PLAY NOW & FORGE YOUR BIRTHDATE GUARDIAN", 600, 540);
  const link = document.createElement("a");
  link.download = `zodiac-guardian-${Date.now()}.png`;
  link.href = cardCanvas.toDataURL("image/png");
  link.click();
}
function shareSocialResults() {
  const shareData = {
    title: "Zodiac Fusion Arena",
    text: `I conquered the celestial realm as ${state.guardianTitle}! Defeats: ${state.totalDefeats}. Forge your guardian from your birthdate and play now:`,
    url: window.location.href
  };
  if (navigator.share) {
    navigator.share(shareData).catch(() => {
    });
  } else {
    try {
      navigator.clipboard.writeText(`${shareData.text} ${shareData.url}`);
      alert("Victory announcement & Play Now link copied to clipboard!");
    } catch {
      alert(`Guardian Victory: ${state.guardianTitle}`);
    }
  }
}
function initGame() {
  canvas = document.getElementById("arena-canvas");
  if (canvas) {
    ctx = canvas.getContext("2d");
  }
  try {
    const savedCodex = localStorage.getItem("zodiac_fusion_codex");
    if (savedCodex) {
      JSON.parse(savedCodex).forEach((id) => state.unlockedCodex.add(id));
    }
    const savedDates = localStorage.getItem("zodiac_fusion_dates");
    if (savedDates) {
      state.codexDates = JSON.parse(savedDates);
    }
  } catch {
  }
  window.addEventListener("resize", resizeCanvas);
  resizeCanvas();
  const stickZone = document.getElementById("touch-stick-zone");
  const stickKnob = document.getElementById("touch-stick-knob");
  if (stickZone && stickKnob) {
    const handleStickMove = (clientX, clientY) => {
      const rect = stickZone.getBoundingClientRect();
      const centerX = rect.left + rect.width / 2;
      const centerY = rect.top + rect.height / 2;
      const dx = clientX - centerX;
      const dy = clientY - centerY;
      const maxDist = rect.width / 2 - 10;
      const dist = Math.hypot(dx, dy);
      const angle = Math.atan2(dy, dx);
      const clampedDist = Math.min(dist, maxDist);
      const knobX = Math.cos(angle) * clampedDist;
      const knobY = Math.sin(angle) * clampedDist;
      stickKnob.style.transform = `translate(${knobX}px, ${knobY}px)`;
      state.joystick.active = true;
      state.joystick.dx = Math.cos(angle) * (clampedDist / maxDist);
      state.joystick.dy = Math.sin(angle) * (clampedDist / maxDist);
    };
    const resetStick = () => {
      stickKnob.style.transform = "translate(0px, 0px)";
      state.joystick.active = false;
      state.joystick.dx = 0;
      state.joystick.dy = 0;
    };
    stickZone.addEventListener("touchstart", (e) => {
      e.preventDefault();
      handleStickMove(e.touches[0].clientX, e.touches[0].clientY);
    });
    stickZone.addEventListener("touchmove", (e) => {
      e.preventDefault();
      handleStickMove(e.touches[0].clientX, e.touches[0].clientY);
    });
    stickZone.addEventListener("touchend", resetStick);
    stickZone.addEventListener("touchcancel", resetStick);
  }
  window.addEventListener("keydown", (e) => {
    if (e.key === "w" || e.key === "ArrowUp") {
      state.joystick.active = true;
      state.joystick.dy = -1;
    }
    if (e.key === "s" || e.key === "ArrowDown") {
      state.joystick.active = true;
      state.joystick.dy = 1;
    }
    if (e.key === "a" || e.key === "ArrowLeft") {
      state.joystick.active = true;
      state.joystick.dx = -1;
    }
    if (e.key === "d" || e.key === "ArrowRight") {
      state.joystick.active = true;
      state.joystick.dx = 1;
    }
    if (e.key === " " || e.key === "j" || e.key === "J") performStrike();
    if (e.key === "Shift" || e.key === "k" || e.key === "K") performDash();
    if (e.key === "e" || e.key === "l" || e.key === "L") performSignTechnique();
    if (e.key === "q" || e.key === "h" || e.key === "H") performHeal();
  });
  window.addEventListener("keyup", (e) => {
    if (["w", "s", "ArrowUp", "ArrowDown"].includes(e.key)) state.joystick.dy = 0;
    if (["a", "d", "ArrowLeft", "ArrowRight"].includes(e.key)) state.joystick.dx = 0;
    if (state.joystick.dx === 0 && state.joystick.dy === 0) state.joystick.active = false;
  });
  bindTouchAction("btn-strike", performStrike);
  bindTouchAction("btn-dash", performDash);
  bindTouchAction("btn-sign", performSignTechnique);
  bindTouchAction("btn-heal", performHeal);
  const monthSelect = document.getElementById("select-month");
  const yearInput = document.getElementById("input-year");
  const updateCreationPreview = () => {
    if (!monthSelect || !yearInput) return;
    const mVal = parseInt(monthSelect.value);
    const yVal = parseInt(yearInput.value) || 2e3;
    const month = ZODIAC_MONTHS[mVal - 1];
    const aIdx = getAnimalIndexForYear(yVal);
    const animal = CHINESE_ANIMALS[aIdx];
    const previewEmblem = document.getElementById("preview-emblem");
    if (previewEmblem) previewEmblem.textContent = month.glyph;
    const previewTitle = document.getElementById("preview-fusion-title");
    if (previewTitle) previewTitle.textContent = `${month.name}\u2013${animal.name} Guardian`;
    const previewDesc = document.getElementById("preview-fusion-desc");
    if (previewDesc) previewDesc.textContent = `Active: ${month.tech} \u2022 Passive: ${animal.trait}`;
  };
  if (monthSelect) monthSelect.addEventListener("change", updateCreationPreview);
  if (yearInput) yearInput.addEventListener("input", updateCreationPreview);
  updateCreationPreview();
  bindTouchAction("btn-confirm-forge", () => {
    if (!monthSelect || !yearInput) return;
    state.playerMonth = parseInt(monthSelect.value);
    state.playerYear = parseInt(yearInput.value) || 2e3;
    state.playerAnimalIndex = getAnimalIndexForYear(state.playerYear);
    const m = ZODIAC_MONTHS[state.playerMonth - 1];
    const a = CHINESE_ANIMALS[state.playerAnimalIndex];
    state.guardianTitle = `${m.name}\u2013${a.name} Guardian Prime`;
    const modalCreation = document.getElementById("modal-creation");
    if (modalCreation) modalCreation.classList.remove("active");
    const overlay = document.getElementById("assembly-overlay");
    if (overlay) overlay.classList.add("active");
    const stepLabel = document.getElementById("assembly-step-label");
    const glyphDisplay = document.getElementById("assembly-glyph-display");
    const animalDisplay = document.getElementById("assembly-animal-display");
    const titleDisplay = document.getElementById("assembly-title-display");
    const traitDisplay = document.getElementById("assembly-trait-display");
    if (glyphDisplay) glyphDisplay.textContent = "";
    if (animalDisplay) animalDisplay.textContent = a.icon;
    if (titleDisplay) titleDisplay.textContent = `${a.name} Mask Formed`;
    if (stepLabel) stepLabel.textContent = "Beat 1: Animal Silhouette & Mask";
    if (traitDisplay) traitDisplay.textContent = `Imbued with ${a.name} trait: ${a.trait}`;
    audio.playTone(180, "sine", 0.5, 0.3);
    setTimeout(() => {
      if (stepLabel) stepLabel.textContent = "Beat 2: Constellation Armor Forged";
      if (titleDisplay) titleDisplay.textContent = "Starlight Armor Assembled";
      if (traitDisplay) traitDisplay.textContent = `Tempered by cosmic starlight resistance.`;
      audio.playTone(360, "triangle", 0.5, 0.3);
    }, 1600);
    setTimeout(() => {
      if (stepLabel) stepLabel.textContent = "Beat 3: Zodiac Glyph Awakened";
      if (glyphDisplay) glyphDisplay.textContent = m.glyph;
      if (titleDisplay) titleDisplay.textContent = state.guardianTitle;
      if (traitDisplay) traitDisplay.textContent = `Empowered with active constellation technique: ${m.tech}!`;
      audio.playTone(540, "sawtooth", 0.7, 0.35);
    }, 3200);
    setTimeout(() => {
      if (overlay) overlay.classList.remove("active");
      const hudTitle = document.getElementById("hud-fusion-title");
      if (hudTitle) hudTitle.textContent = `${m.name}\u2013${a.name}`;
      const hudName = document.getElementById("hud-guardian-name");
      if (hudName) hudName.textContent = "Guardian Prime";
      const btnSignSpan = document.getElementById("btn-sign-icon")?.querySelector("span");
      if (btnSignSpan) btnSignSpan.textContent = m.glyph;
      const signLabel = document.getElementById("sign-tech-name");
      if (signLabel) signLabel.textContent = m.name;
      spawnGateEnemy();
      audio.playFanfare();
    }, 5e3);
  });
  bindTouchAction("btn-reforge", () => {
    const modal = document.getElementById("modal-creation");
    if (modal) modal.classList.add("active");
  });
  const tutorialSteps = [
    {
      title: "1. Birthdate Fusion Mechanics",
      icon: "\u2728",
      desc: "Your <strong>Birth Month</strong> bestows your active Constellation Sign Technique (e.g. Leo Roar, Aquarius Fan).<br><br>Your <strong>Birth Year Animal</strong> grants your movement silhouette, dash trait (Dragon fire, Rabbit double dash), and combat passives!"
    },
    {
      title: "2. Touch Movement (Virtual Stick)",
      icon: "\u{1F579}\uFE0F",
      desc: "Glide your guardian smoothly across the circular celestial arena using the <strong>Virtual Analog Stick</strong> on the bottom-left of your phone screen.<br><br>Thumb drag direction governs movement and weapon facing orientation."
    },
    {
      title: "3. 3-Hit Strike Combo",
      icon: "\u2694\uFE0F",
      desc: "Tap the \u2694\uFE0F <strong>Strike Button</strong> on the right to unleash a rhythmic 3-hit weapon combo.<br><br>Each consecutive hit deals escalating damage with starlight crescent hit-stops and sparks!"
    },
    {
      title: "4. Dash & Sign Technique",
      icon: "\u2726",
      desc: "Tap the \u2726 <strong>Dash Button</strong> for an instant burst with 0.18s invulnerability.<br><br>Tap the glowing <strong>Zodiac Sign Button</strong> to unleash your birth month active power on a 6s cooldown!"
    },
    {
      title: "5. Practice Sparring Session",
      icon: "\u{1F3AF}",
      desc: "Ready to test your touch controls? Tap below to summon a stationary <strong>Astral Training Dummy</strong> into the arena for practice striking, dodging, and testing techniques before entering Realm 1!"
    }
  ];
  let currentTutStep = 0;
  const renderTutStep = () => {
    const step = tutorialSteps[currentTutStep];
    const tutTitle = document.getElementById("tut-title");
    if (tutTitle) tutTitle.textContent = step.title;
    const tutIcon = document.getElementById("tut-icon");
    if (tutIcon) tutIcon.innerHTML = step.icon;
    const tutDesc = document.getElementById("tut-desc");
    if (tutDesc) tutDesc.innerHTML = step.desc;
    for (let i = 1; i <= 5; i++) {
      const dot = document.getElementById(`tut-dot-${i}`);
      if (dot) dot.className = i - 1 === currentTutStep ? "tut-dot active" : "tut-dot";
    }
    const prevBtn = document.getElementById("tut-btn-prev");
    if (prevBtn) prevBtn.style.display = currentTutStep > 0 ? "block" : "none";
    const nextBtn = document.getElementById("tut-btn-next");
    if (nextBtn) nextBtn.textContent = currentTutStep === 4 ? "\u2694\uFE0F Start Practice Fight!" : "Next Step";
  };
  bindTouchAction("btn-open-tutorial", () => {
    currentTutStep = 0;
    renderTutStep();
    const modal = document.getElementById("modal-tutorial");
    if (modal) modal.classList.add("active");
  });
  bindTouchAction("tut-btn-prev", () => {
    if (currentTutStep > 0) {
      currentTutStep--;
      renderTutStep();
    }
  });
  bindTouchAction("tut-btn-next", () => {
    if (currentTutStep < 4) {
      currentTutStep++;
      renderTutStep();
    } else {
      const modal = document.getElementById("modal-tutorial");
      if (modal) modal.classList.remove("active");
      state.isPractice = true;
      spawnGateEnemy();
      addFloatingText(0, 0, "PRACTICE SPARRING STARTED!", "#FFD700");
    }
  });
  bindTouchAction("btn-open-codex", () => {
    populateCodexGrid();
    const modal = document.getElementById("modal-codex");
    if (modal) modal.classList.add("active");
  });
  bindTouchAction("btn-close-codex", () => {
    const modal = document.getElementById("modal-codex");
    if (modal) modal.classList.remove("active");
  });
  bindTouchAction("btn-close-detail", () => {
    const modal = document.getElementById("modal-codex-detail");
    if (modal) modal.classList.remove("active");
  });
  bindTouchAction("btn-next-realm", () => {
    const modal = document.getElementById("modal-victory");
    if (modal) modal.classList.remove("active");
    const cur = state.currentRealmIndex;
    const next = (state.currentRealmIndex + 1) % 12;
    triggerRealmUnlockSequence(cur, next);
  });
  bindTouchAction("btn-play-now", () => {
    const modal = document.getElementById("modal-victory");
    if (modal) modal.classList.remove("active");
    const createModal = document.getElementById("modal-creation");
    if (createModal) createModal.classList.add("active");
  });
  bindTouchAction("btn-enter-new-realm", enterUnlockedRealm);
  bindTouchAction("btn-replay-realm-seq", () => {
    const cur = state.currentRealmIndex;
    const next = (state.currentRealmIndex + 1) % 12;
    triggerRealmUnlockSequence(cur, next);
  });
  bindTouchAction("btn-view-realm-stats", () => {
    const modal = document.getElementById("modal-realm-unlock");
    if (modal) modal.classList.remove("active");
    if (realmFxRaf) cancelAnimationFrame(realmFxRaf);
    showVictoryScreen(state.currentRealmIndex === 11);
  });
  bindTouchAction("btn-close-realm-map", () => {
    const modal = document.getElementById("modal-realm-unlock");
    if (modal) modal.classList.remove("active");
    if (realmFxRaf) cancelAnimationFrame(realmFxRaf);
  });
  bindTouchAction("hud-realm-badge", openRealmMapModal);
  bindTouchAction("btn-download-share-card", generateShareCard);
  bindTouchAction("btn-share-social", shareSocialResults);
  requestAnimationFrame(gameLoop);
  updateCodexCounter();
  if ("serviceWorker" in navigator) {
    window.addEventListener("load", () => {
      navigator.serviceWorker.register("./sw.js").then((reg) => console.log("ServiceWorker registered with scope:", reg.scope)).catch((err) => console.log("ServiceWorker registration failed:", err));
    });
  }
}
if (typeof window !== "undefined") {
  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", () => initGame());
  } else {
    initGame();
  }
}
export {
  AudioController,
  CHINESE_ANIMALS,
  ZODIAC_MONTHS,
  addFloatingText,
  addParticle,
  audio,
  bindTouchAction,
  damageEnemy,
  damagePlayer,
  enterUnlockedRealm,
  gameLoop,
  generateShareCard,
  getAnimalIndexForYear,
  getNodeCenter,
  getZodiacMonth,
  initGame,
  initRealmFxCanvas,
  onGateVictory,
  openRealmMapModal,
  performDash,
  performHeal,
  performSignTechnique,
  performStrike,
  populateCodexGrid,
  recordCodexEntry,
  render,
  renderRealmMapGrid,
  resizeCanvas,
  shareSocialResults,
  showCodexDetail,
  showRealmInfoPreview,
  showVictoryScreen,
  spawnGateEnemy,
  spawnRealmBurstParticles,
  state,
  triggerRealmUnlockSequence,
  triggerShadowPhase,
  update,
  updateCodexCounter,
  updatePlayerHpUI,
  updateShardsUI
};
