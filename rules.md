# RULES.md

## Purpose
This file defines working rules for humans and AI agents in this repository.
Read this before editing.

## Project Constraints
- Keep Native Java + XML unless explicitly changed by product decision.
- Keep Feature-first Clean Architecture package boundaries.
- Keep EN/VI parity for all user-visible strings.
- Keep health/clean visual direction (green + orange accents, light surfaces).

## Source Of Truth Read Order
1. `memory.md`
2. `currentContext.md`
3. `app/src/main/java/com/finalterm/regfood/MainActivity.java`
4. `app/src/main/java/com/finalterm/regfood/app/navigation/BottomTab.java`
5. `app/src/main/java/com/finalterm/regfood/features/home/view/ui/HomeFragment.java`
6. `app/src/main/java/com/finalterm/regfood/features/foods/view/ui/FoodsFragment.java`
7. `app/src/main/res/layout/activity_main.xml`
8. `app/src/main/res/layout/fragment_home.xml`
9. `app/src/main/res/layout/fragment_foods.xml`
10. `app/src/main/res/values/strings.xml`
11. `app/src/main/res/values-vi/strings.xml`

## Navigation Rules
- Bottom navigation is: Home, Foods, Journal, Goals, Insights.
- Do not put Account as a bottom tab unless product explicitly changes direction.
- Home and Foods may contain internal view states switched by visibility (`VISIBLE/GONE`).

## Home Rules
- Home state machine currently includes:
  - `homeStateRoot`
  - `historyStateRoot`
  - `accountStateRoot`
  - `scanStateRoot`
  - `resultStateRoot`
- Profile entry points should open Home Account state.
- Keep quick actions short and high-frequency.

## Foods Rules
- Foods starts with 3 curated dishes.
- Detail page should keep calories/meal tags visible without deep scroll.
- Favorite action must remain available on detail.

## Code And Resource Rules
- No hardcoded UI text in XML/Java.
- Keep IDs stable; avoid renaming without need.
- Reuse existing drawables/colors before creating new resources.
- Validate changed files with editor diagnostics before handoff.

## Build Rules
- AGP requires Java 11+.
- If build fails with Java 8 runtime, fix JVM config first.

## Handoff Rules
Each handoff should include:
1. What changed
2. Why it changed
3. What remains pending
4. Which files to read next

## Temporary Project Command (Do Not Remove)
`cd d:\AndroidStudioProjects\REGFood`
`\.\ecc-bootstrap.ps1 -Target codex -Profile developer`
