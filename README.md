# MuzzChat — Assumptions & Key Decisions

This repo implements the Muzz chat exercise using **Kotlin 2.x**, **Compose Material 3**, **Hilt**, **Room**, and **Paging** in a **multi‑module** layout.

## Modules
- `app`: Android entry point, theme, and DI bootstrap.
- `feature-chat`: Chat UI (Compose) + `ChatViewModel`.
- `core-domain`: Entities (`Message`, `DeliveryStatus`), policies and use‑cases.
- `core-data`: Room DB + repository implementation.
- `core-ui`: Small reusable UI pieces (chips, spacing, etc.).

## Dependency flow (DIP)

```
:feature-chat  --->  :core-domain  <---  :core-data
      ^                                   (implements repo IF)
      |         
     used by
      |
     :app  (theme + Hilt entry; depends on feature + data for DI aggregation)
```

## Assumptions
- **Single conversation, two fixed users**: `"me"` and `"other"`; no auth.
- **No networking**; all data is local. Optional seeding on first launch.
- **Time**: section header format follows the requirement _“{day} {HH:mm}”_ via `DateFormats.CHAT_HEADER_PATTERN`; device **timezone** and **locale** are used.
- **Sectioning**: a header is inserted when the gap to the previous message exceeds **1 hour** (`MessagePolicies.SECTION_GAP`).
- **Compact spacing**: consecutive messages from the same sender within **< 20s** reduce vertical spacing.
- **Status ticks**: mine-only, minimal simulation (e.g., `SENT → DELIVERED/READ`); other user bubbles don’t show ticks.
- **Auto-replies**: a small list of canned responses, delivered after a **600–1200ms** delay to exercise the pipeline.
- **Persistence & observability**: Room exposes `Flow<List<Message>>`; UI observes a `Flow<PagingData<UiItem>>` generated with `PagingData.from(list)`.
- **Keyboard/insets**: `WindowCompat.setDecorFitsSystemWindows(window, false)` + `adjustNothing` at Activity level; `imePadding()`/`navigationBarsPadding()` in the bottom bar.
- **Min SDK 24** with **core library desugaring** for `java.time` APIs.
- **Navigation**: single-activity; partner name passed into `ChatRoute`.

## Key Decisions
- **Compose Material3**: modern UI toolkit with concise theming and layout.
- **Clean-ish modularisation**: domain separated from data/UI; DI via Hilt.
- **KAPT** retained for Hilt/Room to avoid churn; both support KSP and can be migrated later.
- **Paging bridge**: `PagingData.from(list)` keeps code small while leaving a clear path to `RoomPagingSource`.
- **Deterministic tests**: `kotlinx-coroutines-test` + Turbine; a `MainDispatcherRule` drives virtual time.
- **java.time** everywhere for clarity/immutability (desugared).

## Build & Run
- Android Studio **Ladybug**/AGP **8.9.x**.
- JDK **17**; Kotlin compiler set to **17** toolchain.
- Run `:app` on API 24+.
- Unit tests: `./gradlew :feature-chat:testDebugUnitTest`.
- UI tests (minimal): `./gradlew :feature-chat:connectedDebugAndroidTest`.

## Demo
https://github.com/user-attachments/assets/3a5d1fcf-4a5b-43b8-a730-5e75c849ebde


## Folder Hints
- `feature-chat/presentation` — `ChatScreen`, `MessageRow`, `DateChip`, `MessageInputBar`.
- `core-domain/usecase` — `GetMessages`, `SendMessage`, `ReceiveMessage`, `SeedMessages`.
- `core-data` — Room `Dao`, `Database`, and repository.
- `feature-chat/presentation/model` — `UiItem` mapping.

## Notes
- The UI aims to match the screenshots (right/left bubbles, compact grouping, section headers). 
- Strictly KISS: minimal code to satisfy the brief while leaving an obvious path to production hardening.
