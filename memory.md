# MEMORY.md

## 1) Project Snapshot
- Name: REGFood
- Domain: meal recognition and calorie estimation
- Stack: Android Native Java + XML
- Architecture: Feature-first Clean Architecture
- Current UI scope: Home-first with bottom navigation shell

## 2) Current State (Last Known Good Context)
Implemented:
- Main activity hosts bottom navigation with 5 tabs.
- Home and Foods screens exist as feature fragments with internal state switching.
- Journal, Goals, Account use shared ComingSoon fragment.
- Theme + palette created for health/clean style.
- EN and VI string resources fully localized (100+ keys).
- **Home tab now has 4 states**: home (daily overview), history (manual log), scan (recognition), result (detail).
- **Foods tab now has 2 states**: list (3 Vietnamese dishes), detail (nutrition + favorites).
- Reselecting Home/Foods tab resets the active fragment to its main state.
- Guest badge headers prepared for future user auth binding.
- Favorites action (Toast) integrated into result and Foods detail screens.

### UI Changes (Latest Phase - Apr 2026)
**Modified Layout XMLs (2)**:
- app/src/main/res/layout/fragment_home.xml (added historyStateRoot, softened hero gradient)
- app/src/main/res/layout/fragment_foods.xml (Vietnamese dish details + favorite button)

**Created Drawable XMLs (5)**:
- app/src/main/res/drawable/bg_hero_card.xml (soft green/orange gradient)
- app/src/main/res/drawable/bg_scan_frame.xml (QR frame border)
- app/src/main/res/drawable/bg_result_preview.xml (food oval badge)
- app/src/main/res/drawable/bg_food_banhmi.xml
- app/src/main/res/drawable/bg_food_myquang.xml
- app/src/main/res/drawable/bg_food_bunchaca.xml

**Modified Java Fragments (2)**:
- app/src/main/java/com/finalterm/regfood/features/home/view/ui/HomeFragment.java (4-state machine + history state)
- app/src/main/java/com/finalterm/regfood/features/foods/view/ui/FoodsFragment.java (FoodEntry enum + 2-state machine)

**Supporting Changes**:
- app/src/main/java/com/finalterm/regfood/MainActivity.java (tab reset callbacks)
- app/src/main/res/values/strings.xml (EN: +25 keys for history/favorite/result states)
- app/src/main/res/values-vi/strings.xml (VI: +25 keys with Vietnamese translations)

**Key files (read order for handoff)**:
- app/src/main/java/com/finalterm/regfood/MainActivity.java
- app/src/main/java/com/finalterm/regfood/app/navigation/BottomTab.java
- app/src/main/java/com/finalterm/regfood/features/home/view/ui/HomeFragment.java
- app/src/main/java/com/finalterm/regfood/features/foods/view/ui/FoodsFragment.java
- app/src/main/res/layout/fragment_home.xml
- app/src/main/res/layout/fragment_foods.xml
- app/src/main/res/values/strings.xml
- app/src/main/res/values-vi/strings.xml

## 3) Important Decisions Log
### 2026-04-20
Decision:
- Keep Native Java + XML (no Compose migration).
Reason:
- User requirement and project direction.
Impact:
- All new UI should be implemented with XML + Fragment + Activity patterns.

### 2026-04-20
Decision:
- Use feature-first separation for UI files.
Reason:
- Keep alignment with clean architecture and future scalability.
Impact:
- New screens should be added under features/<feature>/view/ui.

### 2026-04-20
Decision:
- Guest-first UX for Home and Foods without login.
Reason:
- User role requirement.
Impact:
- Avoid blocking actions behind auth on those tabs.

## 4) Environment and Build Notes
- AGP requires Java 11+.
- Known blocker seen before: Gradle running on Java 8.
- Action when build fails early: verify JAVA_HOME and Gradle JVM.

## 5) Known Issues / Risks
- Local machine may fail build until Java 11+ is configured.
- Placeholder tabs (Journal/Goals/Account) need real feature implementation.
- Foods screen is static UI currently; data layer integration pending.

## 6) Next Priority Backlog
1. Replace static Foods cards with RecyclerView + adapter + model.
2. Replace sample Home recognition result with real camera/gallery flow.
3. Add navigation state persistence on configuration changes.
4. Wire Home/Foods to domain use cases and repository contracts.
5. Add UI tests for tab switching and locale rendering.

## 7) Agent Resume Checklist
When continuing work:
1. Read rules.md fully.
2. Read this file (memory.md) fully.
3. Open MainActivity and current feature fragment/layout files.
4. Confirm no unresolved resources in edited XML.
5. Update this file after completing any task.

## 8) Session Update Template
Use this block after each completed task:

- Date:
- Task:
- Files changed:
- Decision made:
- Validation:
- Remaining work:

## 10) UI State Machine Details (Apr 2026 Phase)

### Home Fragment (4 States)
- **homeStateRoot**: Daily greeting, hero card with softened gradient, daily stats, Scan Meal + Manual Log buttons
- **historyStateRoot**: "Recent Activities" title, 3-item manual log history with timestamps, Back button
- **scanStateRoot**: QR frame guide, "Recognition Mode" title, Camera/Gallery action buttons  
- **resultStateRoot**: Food preview (oval badge), name, confidence %, macros (kcal/carb/protein/fat), [Scan Another] [Add to Favorites] buttons

### Foods Fragment (2 States)
- **foodsListRoot**: Grid of 3 cards (Bánh mì, Mì quảng, Bún chả cá) with gradient backgrounds + brief descriptions
- **foodDetailRoot**: Food title, image/gradient, nutrition table (kcal, energy, tags), [Add to Favorites] button

### Button Routing (Listener Map)
**Home**:
- btnScanMeal → showScanState()
- btnManualLog → showHistoryState() [changed from showScanState()]
- btnBackFromHistory → showHomeState()
- btnCapturePhoto/btnUploadPhoto → showResultState()
- btnBackHomeFromResultCard → Toast "Đã thêm vào danh sách yêu thích" [changed from nav Home]
- btnScanAnother → showScanState()

**Foods**:
- 3 food cards → showFoodDetail(FoodEntry)
- btnRecognizeAgain → Toast "Đã thêm vào danh sách yêu thích"

**MainActivity**:
- Home tab reselect → HomeFragment.resetToMainHome() → showHomeState()
- Foods tab reselect → FoodsFragment.resetToFoodsList() → showFoodsList()

## 11) Latest UI Notes (Apr 2026)
- Home hero card: softened green/orange gradient (88F/CD/BF base colors)
- Supported Foods Preview: removed from Home main state
- Home recognition: modern QR-style frame with camera/gallery choices
- Home history: 3-item sample log with timestamps (data layer integration pending)
- Foods dishes: Bánh mì, Mì quảng, Bún chả cá with warm gradient backgrounds
- Foods detail: nutrition table + tags + favorite button (no persistence layer yet)
- Navigation buttons: replaced top buttons with static Guest badges in headers (ready for auth integration)
- Favorite action: Toast shown on click (no SharedPreferences/Room storage yet)

## 12) Known Blockers & Technical Debt
- Build: AGP 8.13.2 requires Java 11+ (local Java 8 may cause failure)
- Persistence: Favorites stored in Toast only; needs SharedPreferences or Room DB
- History data: 3 hardcoded items; needs query from backend/local store
- Foods images: currently gradient placeholders; can load from assets or URLs later
- Foods list: 3 static cards; could migrate to RecyclerView for scalability

## 13) Read Next (If You Have 5 Minutes)
1. app/src/main/java/com/finalterm/regfood/MainActivity.java
2. app/src/main/java/com/finalterm/regfood/features/home/view/ui/HomeFragment.java
2. app/src/main/res/layout/activity_main.xml
3. app/src/main/res/layout/fragment_home.xml
4. app/src/main/res/values/strings.xml
5. app/src/main/res/values-vi/strings.xml
