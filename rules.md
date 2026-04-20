# RULES.md

## 1) Purpose
This file defines project rules for humans and AI agents.
Any contributor should read this file before editing code.

## 2) Project Identity
- Project: REGFood
- Platform: Android Native (Java + XML)
- Architecture: Feature-first Clean Architecture
- Primary UI direction: health/clean
- Primary colors: green + orange
- Languages: English + Vietnamese

## 3) Source of Truth (Read Order)
Read in this exact order:
1. memory.md
2. app/src/main/java/com/finalterm/regfood/MainActivity.java
3. app/src/main/java/com/finalterm/regfood/app/navigation/BottomTab.java
4. app/src/main/java/com/finalterm/regfood/features/home/view/ui/HomeFragment.java
5. app/src/main/java/com/finalterm/regfood/features/foods/view/ui/FoodsFragment.java
6. app/src/main/res/layout/fragment_home.xml
7. app/src/main/res/layout/fragment_foods.xml
8. app/src/main/res/values/strings.xml
9. app/src/main/res/values-vi/strings.xml
10. app/src/main/res/values/colors.xml
11. app/src/main/res/values/themes.xml

## 4) Architecture Rules
- Keep Feature-first package structure.
- Feature modules own UI, domain, and data boundaries.
- Shared UI utilities go under shared.
- App-level wiring (navigation, DI, startup) goes under app.

Expected package layout:
- app/di, app/navigation
- features/<feature_name>/data
- features/<feature_name>/domain
- features/<feature_name>/view
- shared/

## 5) UI/UX Rules
- Use Native Java + XML only unless explicitly approved.
- Prefer Material components for buttons/cards/text fields.
- Keep visual style health/clean: light surfaces, green accents, orange highlights.
- Bottom navigation has 5 tabs: Home, Foods, Journal, Goals, Account.
- Guest role can use Home and Foods without login.
- Keep spacing and corner radii consistent across screens.

### UI State Machines (Established Pattern)
- Home fragment uses **Fragment-scoped visibility switching** (4 internal FrameLayout states: home, history, scan, result).
  - Avoid full fragment replacement; use View.setVisibility(VISIBLE/GONE) for state transitions.
  - Each state has its own button listeners and UI layout.
  - Reselecting Home tab calls HomeFragment.resetToMainHome() to return to homeStateRoot.
- Foods fragment uses **Fragment-scoped visibility switching** (2 internal FrameLayout states: list, detail).
  - List state shows 3 static cards; detail state shows nutrition + favorites for selected food.
  - Reselecting Foods tab calls FoodsFragment.resetToFoodsList() to return to list state.
- Apply same pattern for future tab screens (Journal, Goals, Account) if multi-state UX is needed.

## 6) Localization Rules
- Never hardcode UI text in layouts/fragments.
- Add all user-visible strings to:
  - app/src/main/res/values/strings.xml (EN)
  - app/src/main/res/values-vi/strings.xml (VI)
- Keep string keys identical across locales.

## 7) Styling Rules
- Reuse palette tokens from colors.xml.
- Reuse drawables for cards/backgrounds before creating new ones.
- Prefer explicit style names that exist in current Material library.
- If a style reference is unresolved, replace with valid style or direct widget attrs.

## 8) Coding Rules (Java/XML)
- Java: clear naming, small methods, avoid unnecessary comments.
- XML: readable hierarchy, avoid deep nesting when possible.
- IDs should be stable and descriptive.
- Do not introduce framework migrations without agreement.

## 9) Quality Gates Before Handoff
- Verify no editor errors in changed files.
- Verify resources resolve (styles, colors, strings, drawables).
- If build fails, report exact blocker and next action.
- Update memory.md with new decisions and next tasks.

- Follow conventional commits:
  - `feat(home): add history state + favorite action` for feature additions
  - `style(ui): soften hero gradient + add food backgrounds` for visual changes
  - `refactor(nav): update MainActivity tab reset callbacks` for refactoring

## 12) Recent Changes Inventory (Apr 2026 Phase)

### Files Modified (9 Total)
**Layout XMLs (2)**:
- app/src/main/res/layout/fragment_home.xml (added historyStateRoot, modified hero gradient)
- app/src/main/res/layout/fragment_foods.xml (updated foods detail state)
3
**Drawable XMLs (5) - Created New**:
- app/src/main/res/drawable/bg_hero_card.xml
- app/src/main/res/drawable/bg_scan_frame.xml
- app/src/main/res/drawable/bg_result_preview.xml
- app/src/main/res/drawable/bg_food_banhmi.xml
- app/src/main/res/drawable/bg_food_myquang.xml
- app/src/main/res/drawable/bg_food_bunchaca.xml

**Java Fragments (2)**:
- app/src/main/java/com/finalterm/regfood/features/home/view/ui/HomeFragment.java (4-state machine implementation)
- app/src/main/java/com/finalterm/regfood/features/foods/view/ui/FoodsFragment.java (FoodEntry enum + 2-state machine)

**Supporting Files (2)**:
- app/src/main/java/com/finalterm/regfood/MainActivity.java (added tab reset method calls)
- app/src/main/res/values/strings.xml & app/src/main/res/values-vi/strings.xml (added 25+ keys for new states)
## 10) Build/Environment Rules
- AGP in this repo requires Java 11+.
- If build fails with Java 8, fix JAVA_HOME/Gradle JVM first.
- Typical command:
  - ./gradlew :app:assembleDebug (or gradlew.bat on Windows)

## 11) Git and Change Discipline
- Do not revert unrelated user changes.
- Keep patches minimal and focused.
- Add new files only when needed and document them in memory.md.

## 12) Agent Handoff Contract
Every agent update should include:
1. What was changed
2. Why it was changed
3. What is still pending
4. Which files to read next

If unsure, stop and ask a focused question instead of guessing.
