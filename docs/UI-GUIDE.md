# UI Guide

## Дизайн концепція

Додаток використовує **player-centric design** — плеєр завжди внизу, контент зверху.

```
PHONE/TABLET:                   TV:
┌─────────────────┐             ┌──────────────┬──────────────┐
│                 │             │   Widget 1   │   Widget 2   │
│  Widget Stack   │             │   (weather)  │  (forecast)  │
│  (swipe/auto)   │             ├──────────────┴──────────────┤
│                 │             │         🕐 Clock            │
├─────────────────┤             ├─────────────────────────────┤
│   Lux FM 104.7  │             │        Lux FM 104.7         │
│  ██ ██ ▶ ██ ██  │             │    ██ ██ ██ ▶ ██ ██ ██      │
└─────────────────┘             └─────────────────────────────┘
```

## PlayerBar

**Завжди внизу** (Phone/Tablet) — зручно для великого пальця, звичний UX.

### Стани

| Стан | Кнопка | Візуалізатор |
|------|--------|--------------|
| STOPPED | ▶ Play | — (hidden) |
| BUFFERING | ◌ disabled | CircularProgressIndicator |
| PLAYING | ⏸ Pause | Pulsing bars |
| ERROR | 🔄 Retry | Error indicator |

### Принципи

1. **Branding над кнопкою** — "Lux FM 104.7"
2. **Audio Visualizer навколо кнопки** — bars зліва і справа
3. **Material3 Spinner для buffering** — не під час playback
4. **Visualizer тільки при PLAYING**

## Widget System

### Концепція

- **Widget Stack** — swipeable/auto-rotating контейнер
- **TV показує 2 widgets**, Phone — 1
- **Widget Manager** — керує visibility, order, refresh (через Remote Config)

### Widget States

```kotlin
sealed interface WidgetState<out T> {
    data object Loading : WidgetState<Nothing>
    data class Ready<T>(val data: T) : WidgetState<T>
    data class Error(val message: String?) : WidgetState<Nothing>
}
```

### Loading UX

| Компонент | Loading UI | Причина |
|-----------|------------|---------|
| **Player** | CircularProgressIndicator | Critical action |
| **Widgets** | Skeleton/Shimmer | Non-blocking |

```
Loading:              Ready:                Error:
┌───────────────┐     ┌───────────────┐     ┌───────────────┐
│ ░░░░░░░░░░░░░ │     │ 🌤️ +15°C      │     │ ⚠️ Load error │
│ ░░░░░  ░░░░░░ │     │ Partly cloudy │     │   [Retry]     │
│ ░░░░░░░░░     │     │ Wind: 5 m/s   │     │               │
└───────────────┘     └───────────────┘     └───────────────┘
   Skeleton              Content            Compact error
```

**Правила:**
- Skeleton показує структуру контенту (не generic spinner)
- Error state має compact retry button
- Widget сам керує своїм state
- WidgetStack показує widgets незалежно від їх loading state

## Компоненти

### Структура `:core:ui-components`

```
:core:ui-components/
├── background/     ← RelaxingBackground, GradientBackground
├── buttons/        ← FocusableButton, SegmentedToggle
├── containers/     ← ScreenContainer, SettingsContainer
├── cards/          ← InfoCard, WeatherCard
├── settings/       ← SettingsGroup, SettingsRow
└── focus/          ← FocusUtils, rememberFocusState
```

### Правила дизайну компонентів

1. **Reusable first** — generic компоненти, не feature-specific
   ```kotlin
   // ✅ Добре: Reusable
   SegmentedToggle(options, selectedIndex, onSelect)

   // ❌ Погано: Feature-specific
   ThemeModeToggle(currentMode, onModeChange)
   ```

2. **TV First, Phone adaptive**
   ```kotlin
   val padding = if (isTV()) 48.dp else 16.dp
   ```

3. **Composable containers**
   ```kotlin
   ScreenContainer(background, topBar) { content() }
   SettingsRow(title, subtitle, icon) { trailing() }
   ```

4. **Focus management** — завжди D-pad navigation
   ```kotlin
   FocusableButton(onClick, focusRequester) { content() }
   ```

### TV-specific компоненти

```
:core:ui-components/
├── buttons/SegmentedToggle.kt      ← Universal
├── tv/TvFocusableGrid.kt           ← TV-only
└── phone/SwipeableCard.kt          ← Phone-only (rare)
```

### Перед створенням компонента

1. Перевір чи схожий існує в `:core:ui-components`
2. Якщо ні — створи generic версію там (не в `:app`)
3. Використовуй Modifier параметри
4. Додай `contentDescription` для accessibility

## Checklist для компонентів

Кожен reusable компонент в `:core:ui-components` ПОВИНЕН мати:

| Вимога | Реалізація |
|--------|------------|
| **Локалізація** | `stringResource(R.string.xxx)`, без hardcoded strings |
| **Accessibility** | `contentDescription`, `semantics {}`, логічний focus order |
| **TV/Phone** | `LocalDimensions`, D-pad focus для TV |
| **Modifier** | Приймає `Modifier` параметр |
| **Preview** | `@Preview` для light/dark, TV/phone |

## TV First підхід

### Ключові правила

- **D-pad navigation** — `FocusRequester` для play button
- **Accessibility** — `contentDescription` на всіх інтерактивних елементах
- **Великі touch targets** — мінімум 48dp
- **Focus states** — чіткі візуальні індикатори

### Focus Management

```kotlin
val focusRequester = remember { FocusRequester() }

Button(
    onClick = { /* action */ },
    modifier = Modifier
        .focusRequester(focusRequester)
        .focusable()
) {
    Text("Play")
}

LaunchedEffect(Unit) {
    focusRequester.requestFocus()
}
```

## Локалізація

- **UA** — default
- **EN** — альтернатива

Всі strings в `strings.xml`:
```
app/src/main/res/values/strings.xml      ← Default (Ukrainian)
app/src/main/res/values-uk/strings.xml   ← Ukrainian (explicit)
app/src/main/res/values-en/strings.xml   ← English
```

**Правило:** Ніколи hardcoded strings в Composables — тільки `stringResource()`.
