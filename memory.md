# MEMORY.md

## Snapshot
- Project: REGFood
- Stack: Android Native Java + XML
- Architecture: Feature-first Clean Architecture
- UI direction: health/clean, green + orange palette
- Locale: EN + VI

## Current Navigation
- Bottom tabs: Home, Foods, Journal, Goals, Insights
- Insights currently uses placeholder screen (`ComingSoonFragment`).
- Reselect behavior:
  - Home tab reselect resets Home to main state.
  - Foods tab reselect resets Foods to list state.

## Home Feature State Machine
- `homeStateRoot`: dashboard + scan/manual entry
- `historyStateRoot`: manual log history quick list
- `accountStateRoot`: account/profile quick access UI inside Home
- `scanStateRoot`: recognition mode (camera/upload choices)
- `resultStateRoot`: recognition result + favorite actions

Key behavior:
- Manual Log opens `historyStateRoot`.
- Profile chips in Home/History/Scan/Result open `accountStateRoot`.
- Result actions:
  - `Scan another` -> back to scan state.
  - `Add to favorites` -> toast only (no persistence yet).

## Foods Feature State Machine
- `foodsListRoot`: 3 static dishes (Banh Mi, Mi Quang, Bun Cha Ca)
- `foodDetailRoot`: selected dish details + favorite action

Key behavior:
- Card click opens detail.
- Favorite button currently toast only (no persistence yet).

## Important Files
- `app/src/main/java/com/finalterm/regfood/MainActivity.java`
- `app/src/main/java/com/finalterm/regfood/app/navigation/BottomTab.java`
- `app/src/main/java/com/finalterm/regfood/features/home/view/ui/HomeFragment.java`
- `app/src/main/java/com/finalterm/regfood/features/foods/view/ui/FoodsFragment.java`
- `app/src/main/res/layout/activity_main.xml`
- `app/src/main/res/layout/fragment_home.xml`
- `app/src/main/res/layout/fragment_foods.xml`
- `app/src/main/res/values/strings.xml`
- `app/src/main/res/values-vi/strings.xml`

## Environment Notes
- AGP requires Java 11+.
- Known blocker: local Gradle may run on Java 8 and fail build.
- First fix when build fails: set JAVA_HOME / Gradle JVM to 11+.

## Next Priority
1. Implement real Insights screen with charts (weekly/monthly calories and trend).
2. Persist favorites (SharedPreferences or Room).
3. Connect Home history to real stored logs.
4. Move static Foods cards to RecyclerView + model.
5. Replace placeholder profile with real auth/user data.
