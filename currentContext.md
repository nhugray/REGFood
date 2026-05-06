# currentContext.md

## Why This File Exists
Fast context handoff for new agents so they can continue work immediately.

## Product Direction (Current)
- Keep bottom tabs focused on high-utility tasks:
  - Home
  - Foods
  - Journal
  - Goals
  - Insights
- Account is surfaced inside Home as a quick-access state, not bottom tab.

## Implemented In This Phase
1. Replaced bottom tab `Account` with `Insights`.
2. Added Home internal `accountStateRoot`.
3. Replaced static guest labels with clickable profile chips (icon + text) in Home states.
4. Wired profile chips to open Home Account state.
5. Added Account UI scaffold in Home with profile card + preference cards.
6. Updated EN/VI strings for Insights and Account state content.

## Important UX Behavior
- Home reselect from bottom nav resets to `homeStateRoot`.
- Foods reselect resets to `foodsListRoot`.
- Favorite actions currently show toast only (no storage yet).

## Known Technical Limits
- Build may fail if Java runtime is below 11.
- Insights/Journal/Goals are still placeholder views.
- Account inside Home is UI-only; no auth/profile backend yet.

## Suggested Next Step
- Build real Insights page first (charts + weekly/monthly trend), then connect Journal and Goals data to close the data loop.
