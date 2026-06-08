# CreateAtomic Reactor — Gameplay Reference

## Structure

The reactor is a multiblock built from **Reactor Casing** blocks (square base, any height). Rod Assemblies sit on top. The controller is the bottom-left corner casing block.

- **Min size**: 2×2×2 (hull capacity = 0 — needs at least 4 blocks)
- **Max size**: 6×6×5
- **Hull capacity** = width² × height (one per casing block), reduced by low water (see below)
- **Water tank** = 1 bucket per casing block, filled via fluid pipe into any casing face

---

## Rod Assemblies

Placed on the **top face** of the reactor. Each slot accepts one rod item. Rods are **locked** while the reactor is hot (temperature > 25°C) — control rods are always swappable.

| Rod | Effect |
|---|---|
| Uranium Fuel Rod | +1 fuel unit per rod. Depletes over ~120 min → becomes Depleted Rod |
| Depleted Fuel Rod | No effect, occupies slot |
| Small Control Rod | -2 control units |
| Large Control Rod | -5 control units |
| Neutron Reflector | Boosts reactivity of adjacent fuel rods |

**Reactivity bonus**: each fuel rod gains +50% effective power per orthogonal neighbour (fuel rod or reflector). Example: 4 rods in a 2×2 = 8 effective power.

**Rod animation**: rods physically lower into the reactor when active/inserted, raise when lifted.

---

## Redstone Interface

Placed on any **face of a reactor casing**. Controls whether the reactor can run.

| Interface state | Reactor behaviour | Control rod animation |
|---|---|---|
| No interface present | Always SCRAM — reactor cannot run | Rods down |
| Interface attached, **no signal** | SCRAM — reactor inserts control rods | Rods down |
| Interface attached, **signal ON** | Reactor runs; control rods lifted (ignored) | Rods up |

**"Reactor SCRAMed!"** appears in the goggle tooltip whenever no signal is active.

---

## Power & Heat

Every ~1 second (lazy tick) the reactor calculates:

1. **Effective power** = sum of fuel rod power with reactivity bonuses
2. **Net power** = effective power (control rods are **ignored** when armed — they only matter when not armed and the reactor is physically stopped)
3. **Temperature** = 25°C at idle → 315°C at hull capacity → 895°C at 3× overload

---

## Hull Capacity & Damage

Hull capacity is reduced by low water level:

| Water level | Capacity multiplier |
|---|---|
| ≥ 50% | ×1.0 (full) |
| 25–50% | ×0.8 (−20%) |
| 15–25% | ×0.6 (−40%) |
| < 15% | ×0.4 (−60%) |

Hull takes damage only when **net power > hull capacity** while active. At 0% hull integrity:
- **Meltdowns enabled** (default): explosion + debris
- **Meltdowns disabled**: reactor forces shutdown until hull self-repairs

Hull **regenerates** at 0.5%/tick (~160 s full regen) only when temperature = 25°C (fully idle).

---

## Turbines

Steam Turbines attach to the **sides** of the reactor and chain inline. Each turbine requires **8 mB of water per lazy tick** from the reactor's tank. If the tank runs dry all turbines stop.

- **RPM** = `MAX_RPM × min(1, netPower / turbineCount)` — scales with net power and turbine count
- **No water** → turbines produce 0 RPM regardless of power

---

## Active vs Inactive

The reactor is **active** when temperature > 25°C. This controls:
- Fuel rod lock (active = locked, cannot insert/remove)
- Hull regen (active = no regen)
- Looping ambient sound
