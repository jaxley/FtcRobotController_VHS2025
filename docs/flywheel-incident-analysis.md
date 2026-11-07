# Flywheel Runaway — What Happened (1-pager)

**The short version:** One day the flywheel motor got stuck at full speed in one direction (−2100 ticks/sec) even though the code was asking for a small speed in the *opposite* direction (+238). D-pad presses changed the number on the driver station but not the motor. We couldn't make it happen again. Our best explanation, backed by the evidence: the motor's speed sensor briefly reported the wrong direction, so the motor's built-in "cruise control" pushed full power the wrong way and stayed there. This looks like a wiring/electrical problem, not a code bug — and the drive team's older "flywheel spun backwards" reports are probably the same problem showing up differently.

## How the speed control works (30-second background)

- The flywheel motor has an **encoder**: a small sensor that counts how fast, and which way, the shaft is spinning.
- The REV hub runs a **cruise control** (a PID controller): our code sends a target speed, the hub compares the encoder's measurement to that target, and adjusts motor power until they match.
- Our targets are in *ticks per second*: full power works out to about −2060 ticks/sec (based on a 28-count encoder and a 4414 RPM top speed).

## What we observed

1. Motor pegged at ≈ −2100 ticks/sec (that's full speed) while the target was +238. It never settled.
2. The D-pad **was** working in software — the power value and target on telemetry changed on every press. Only the motor ignored them.
3. The odd +238 target has an explanation: dpad-down changes flywheel power by 0.025 with **no limit**, so ~37 presses takes it from 0.8 through zero to negative (≈ −0.115), which flips the target's sign.
4. −2100 is almost exactly full power (−2060 to −2111 depending on the last few presses) — the motor wasn't at some random speed, it was maxed out.
5. Today the same code works perfectly (target −1648 → actual −1640), and recent code changes didn't touch the speed control.
6. Separately, in the past, the drive team reported the flywheel **spinning backwards** at full speed — also never reproducible.
7. Nobody changed the PID numbers on the dashboard; they were confirmed at 22 / 3 / 0 / 0.

## Best explanation: the encoder lied about direction

Imagine a car where the speedometer reads −60 mph while you're actually driving forward. If you set cruise control to +5 mph, it would floor the engine backwards forever — because every "correction" makes the error worse. That's called **positive feedback**: the fix pushes the wrong way, so the controller slams to full power and stays pinned there. When this happens:

- the motor sits at full speed in the direction *opposite* the target,
- changing the target does nothing (the controller is already maxed out),
- the measurement shows a big number with the opposite sign of the target.

That matches every observation — and it unifies our two "different" problems into one:

| Target sign that day | Motor rails… | What people saw |
|---|---|---|
| Negative (normal driving) | positive → **backwards** | "Flywheel spinning the wrong way!" |
| Positive (+238, D-pad past zero) | negative → full speed | "Stuck at −2100, D-pad dead" |

Why it never reproduces: a loose connection or electrical glitch depends on motor vibration, speed, humidity, and luck. It behaves on the bench and fails mid-session.

## Why it's not a software bug

1. **Code is a machine.** Same inputs → same outputs. Nothing in our code ever changes the encoder's direction, and the same code runs perfectly now. A bug can't appear once, show up later as a *different* symptom, and then heal itself.
2. **The math doesn't allow it.** With a truthful encoder, our controller settles at whatever target you give it — even a weird target like +238 just means slowing down through zero and spinning slowly the other way. "Stuck at full power opposite the target" is only possible if the measurement itself has the wrong sign.
3. **Every software suspect was checked and cleared.** Dashboard-tuned PID values? Never changed. The hot-reload library? Resets values, doesn't corrupt them, and only touches dashboard-tuned numbers. D-pad handling? Worked. Lost commands to the hub? That would crash visibly, and it can't cause backwards spinning. A telemetry display glitch (number wraparound)? It would need 16× our maximum speed to trigger.

## Primer: EMI and ESD (the electronics backstory)

- **EMI (electromagnetic interference)** = electrical noise getting into signal wires and garbling them. **ESD (electrostatic discharge)** = a static shock — a brief, intense burst of EMI.
- Our robot produces plenty of both: brushed motors spark internally thousands of times per second, motor power wires switch large currents on and off very fast, and the spinning flywheel rubbing plastic rings builds static charge on the robot (worst in dry air — that's why this stuff happens more in winter).
- The victim is the **encoder cable**: it carries small, sensitive counting signals. A noise spike — or a half-loose wire — creates **fake counts**, so the hub thinks the wheel moved when it didn't, and can get the direction wrong. A flaky connector vibrating loose at 4400 RPM causes the same thing mechanically. Either way, the feedback lies, and the cruise control drives the wrong way.
- It never reproduces when you're watching because it depends on humidity, cable position, vibration, and the exact moment of a spark.

## What we're doing about it

- ✅ Hub firmware is already **1.8.2** — it includes fixes for occasional encoder-count glitches and better recovery from static-shock faults, so that possible cause is covered.
- **Replace the encoder cable** (about $3, REV-31-1406/1407), tug-test the crimps, zip-tie strain relief at both ends, and route it away from the motor power wires.
- Add a snap-on **ferrite choke** on the encoder cable and a REV **grounding strap** (legal under RG01); keep connectors away from the metal frame.
- **Code safety net:** clamp flywheel power to [0.1, 1.0]; send the PID numbers once at startup instead of every loop; and add a **guard** — if the motor's direction disagrees with the target's direction for more than ~0.3 seconds, cut motor power completely and show an alert. (Just commanding "zero speed" would *not* stop this failure — the controller would keep fighting.)
- **Bench test to catch it:** robot on blocks, flywheel at full speed, gently wiggle the encoder cable while watching the velocity sign on telemetry. If the sign flickers — mystery solved.

## Appendix: The Numbers

The hub's controller computes: **power = P·(target − measured) + I·(accumulated error) + F·target**, then clamps it to full power. Internally the hub measures power from −32,767 to +32,767 (that range *is* −100% to +100%). With our P = 22, hitting 100% power only takes an error of **32,767 ÷ 22 ≈ 1,489 ticks/sec**.

**Our incident vs. a normal day:**

| Target | Measured | Error | P × error | Power out |
|---|---|---|---|---|
| −1648 | −1640 (normal day) | −8 | −176 | −0.5% ✅ |
| **+238** | **−2100 (incident)** | **+2338** | **51,436** | **clamped to 100%** 🔴 |

**With a truthful encoder, a peg at 100% is always temporary.** Simulated: motor at −2060, target flips to +238:

```
t=0.0s   vel -2060   power 100%   ← pegged
t=0.8s   vel   -43   power 100%   ← crossed zero
t=2.0s   vel  +787   power   2%
t=3.0s   vel  +243   power  -5%   ← settled at target
```

One ugly second, then it recovers. **Code alone cannot hold the peg** — this is the numeric version of "it's not a software bug."

**With a lying encoder, the rail is a trap with no exit.** Simulated with measured = −actual:

```
t=0.0  target +238   measured +2060   power -100%   motor: normal direction, MAXED
t=3.0  target +309   measured +2060   power -100%   ← D-pad change: nothing
t=5.0  target -1648  measured +2060   power -100%   ← D-pad all the way back: nothing
```

Why no escape: to *actually reverse* the motor, the error must cross zero, which needs a target beyond full speed (over ±2060). The D-pad can only move the target between about −2120 and +309 — never past a rail. **No sequence of D-pad presses can flip the motor out of the trap.**

**Which trap you land in is chance** (decided by the error's sign at the instant the feedback corrupts):

- **Backwards trap** (power +100%, motor physically backwards, telemetry ≈ −2060): exactly our incident — measured −2100 vs. target +238 → error +2338 → 100% power (full speed on a fresh battery runs ~2% over nominal → −2100 ✓). Also matches the "spinning backwards" reports.
- **Forward trap** (power −100%, motor maxed the usual way, telemetry ≈ +2060 vs. negative target): "screaming at full speed, won't slow down."

Same disease, two faces, chosen at random — which is why every report sounds different and none of them reproduce.
