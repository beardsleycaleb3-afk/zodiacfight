# Zodiac Fusion Arena (SWA & PWA)

A cel-shaded mythic arena-action quest where your guardian is forged from your birth month monthly zodiac and Chinese birth year animal. Journey through 12 celestial realms, conquer 144 normal and shadow gates, defeat Year Chimeras, and confront the Final Celestial Shadow Chimera.

## Features

- **Birthdate Fusion Engine**:
  - **Month Zodiac (12 Signs)**: Determines active Celestial Constellation Technique (e.g., Leo Solar Roar, Aquarius Water Fan, Capricorn Stone Barrier, Sagittarius Star Volley).
  - **Chinese Year Animal (12 Beasts)**: Anchored to 2020 = Rat. Determines movement traits, dash effects, and combat passives (e.g., Dragon Flame Dash Trail, Tiger 3rd-Hit Combo Surge, Rabbit Double Dash).
  - **5-Second Mythic Assembly**: Cinematic 3-beat guardian forge sequence (Animal Silhouette & Mask $\rightarrow$ Constellation Starlight Armor $\rightarrow$ Luminous Overhead Zodiac Glyph).
- **Artistic Flair Design Theme**:
  - Deep midnight indigo background (`#0F111A`), rich mythic gold accents (`#D4AF37` / `#FFD700`), celestial slate panels (`#16181D`), crimson warning highlights (`#FF4B4B`), and shadow ultraviolet (`#9B51E0`).
- **Interactive Step-by-Step Tutorial**:
  - Step 1: Birthdate fusion mechanics overview.
  - Step 2: Virtual analog stick & keyboard navigation (WASD/Arrows).
  - Step 3: 3-hit basic weapon chain (Light $\rightarrow$ Medium $\rightarrow$ Heavy with crescent hit-stop FX).
  - Step 4: Dash mechanics with invulnerability frames and animal trails.
  - Step 5: Active constellation sign techniques.
  - Step 6: Live duel with an Astral Combat Automaton.
- **Dynamic Dual-Phase Combat**:
  - Elevated 3/4 perspective arena with circular celestial clock floor and engraved zodiac markers.
  - **Phase 1**: Normal Guardian combat with animal movement patterns and telegraphed attacks.
  - **Phase 2 (Shadow Transformation)**: Defeated normal guardian transforms in-place (screen dimming, violet fracture rings) with accelerated attacks, black-violet telegraphs, and persistent arena hazards.
  - **Constellation Shards**: Every 3 shards refill 1 healing charge and grant +5% damage in that realm.
- **Visual Codex (12x12 Matrix - 144 Fusions)**:
  - Tracks all 144 fusion combinations across 12 Month Signs and 12 Year Realms.
  - Unlocked entries feature cel-shaded avatar portraits, fusion titles, ability details, and encounter timestamps.
- **Shareable Victory Screen & Social Card Generator**:
  - High-resolution 1200x630 social card generator rendered directly to PNG.
  - Features player's guardian name (e.g., *Leo–Dragon Guardian Prime*), campaign time, defeats, and clear achievements.
  - "Play Now!" invitation link with direct birthdate sharing.
- **PWA & Standalone SWA Architecture**:
  - Progressive Web App with `manifest.json`, offline caching via `sw.js`, and all-inline standalone `index.html`.
  - Manifest link at line 6 of `index.html`.
  - Service Worker registration at the very end of the main `<script>` tag.
  - Built-in Web Audio API sound synthesizer for responsive retro-mythic sound effects and celestial combat melodies.

## Controls

- **Movement**: Virtual Joystick (touch) or `W`, `A`, `S`, `D` / Arrow Keys.
- **Basic Strike**: Strike Button or `J` / Left Click (3-hit chain).
- **Constellation Technique**: Sign Button or `K` / Right Click (6s cooldown).
- **Dash**: Dash Button or `Space` (0.3s dash with 0.18s invulnerability window).
- **Heal**: Heal Button or `E` (Restores 40 HP, charges refilled by constellation shards).
