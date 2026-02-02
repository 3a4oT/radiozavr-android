# Архітектура

## Огляд

Android radio streaming app для **Lux FM Lviv**. Підтримує TV (Leanback) та Phone. **TV First** підхід.

## Структура модулів

```
radio-lux-fm/
├── core/
│   ├── models/           ← Data classes (pure Kotlin)
│   ├── ui-theme/         ← Colors, Typography, Theme
│   ├── ui-components/    ← Shared Compose UI
│   ├── network/          ← API clients (Open-Meteo, NOAA)
│   └── config/           ← Remote Config, serialization
└── app/                  ← Main Radio app (Android)
```

### Залежності модулів

```
:core:models          ← Без залежностей (pure Kotlin)
:core:ui-theme        ← Compose
:core:ui-components   ← :core:models, :core:ui-theme
:core:network         ← :core:models, Retrofit
:core:config          ← :core:models, Serialization
:app                  ← Всі core модулі
```

### Де розміщувати код

| Тип | Модуль | Приклади |
|-----|--------|----------|
| Data models, enums | `:core:models` | ThemeMode, DeviceType, LanguageMode |
| Theme, colors, typography | `:core:ui-theme` | ColorPalette, Dimensions |
| Reusable UI components | `:core:ui-components` | Buttons, Settings rows, Cards |
| Feature implementation | `:app` | Screens, ViewModels, Repositories |

## Clean Architecture

```
domain/    ← Бізнес-логіка (interfaces, use cases)
data/      ← Реалізації (repositories, data sources)
ui/        ← Presentation (screens, components)
viewmodel/ ← ViewModel для кожного екрану
```

Детальніше: [Android App Architecture](https://developer.android.com/topic/architecture)

## Reactive State

### Flow Types по шарах

| Шар | Тип | Причина |
|-----|-----|---------|
| **Services** | `MutableStateFlow` + `asStateFlow()` | Hot state, imperative updates |
| **DataSources** | `StateFlow` від services | Proxy |
| **Repositories** | `Flow<T>` | Cold streams, transformations |
| **UseCases** | `Flow<T>` / `suspend fun` | Делегування |
| **ViewModels** | `combine()` + `stateIn()` | UI state composition |

### ViewModel Pattern

```kotlin
// Рекомендовано: combine + stateIn
val uiState: StateFlow<UiState> = combine(
    useCase1(),
    useCase2(),
) { a, b -> UiState(a, b) }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState())

// Простий випадок: single flow
val theme: StateFlow<ThemeMode> = repository.themeMode
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ThemeMode.AUTO)
```

### Чому `WhileSubscribed(5_000)`

- Офіційна рекомендація Google
- Тримає flow активним 5с після останнього subscriber (переживає config changes)
- Скасовує upstream коли немає subscribers (економить ресурси)

### Уникати

- ❌ `MutableStateFlow` + `collect` в `init`
- ❌ `StateFlow` в repositories (використовуй cold `Flow`)
- ❌ `LiveData` (legacy)

## Dependency Injection

**Hilt — єдиний DI механізм.** Не вводити альтернативні патерни.

### ViewModel + Hilt (не CompositionLocal)

| Критерій | ViewModel + Hilt | CompositionLocal |
|----------|------------------|------------------|
| Консистентність | ✅ Вже використовується | ❌ Новий патерн |
| Тестування | ✅ Легко мокати | ⚠️ Потребує setup |
| Google рекомендація | ✅ Official guide | ⚠️ Тільки для UI theming |
| Lifecycle | ✅ Переживає config changes | ❌ Resets on recomposition |

### Коли CompositionLocal допустимий

- Theme values (colors, dimensions) — pure UI
- Android locals (`LocalContext`, `LocalLifecycleOwner`)
- UI-only state без бізнес-логіки

### Коли НЕ використовувати CompositionLocal

- App services (analytics, repositories)
- Що завгодно з бізнес-логікою
- Залежності для тестування

### Правила

```kotlin
// ✅ Правильно: ViewModel з Hilt
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val analyticsTracker: AnalyticsTracker,
) : ViewModel()

// ❌ Неправильно: CompositionLocal для app dependencies
val LocalAnalyticsTracker = compositionLocalOf { ... }

// ❌ Неправильно: Parameter drilling через navigation
fun AppNavigation(analyticsTracker: AnalyticsTracker)
```

| Потреба | Рішення |
|---------|---------|
| Cross-cutting concerns (analytics) | Inject через Hilt в ViewModel |
| Theme/Dimensions | `LocalDimensions` (UI-only) |
| Context в Composable | `LocalContext` |
| App dependencies | **Завжди Hilt** через ViewModel |

## Navigation

Jetpack Compose Navigation з type-safe routes (Navigation 2.8+).

```kotlin
// Routes
@Serializable data object RadioPlayer
@Serializable data object Settings
@Serializable data class ArticleDetail(val id: String)

// NavHost
NavHost(navController, startDestination = RadioPlayer) {
    composable<RadioPlayer> {
        RadioPlayerScreen(onSettings = { navController.navigate(Settings) })
    }
    composable<Settings> {
        SettingsScreen(onBack = { navController.popBackStack() })
    }
}
```

**Правила:**
- Routes в `:app/navigation/`
- `@Serializable` для type-safe params
- NavController залишається в `MainActivity`

## Anti-Patterns

### Критичні

| Anti-Pattern | Проблема | Правильно |
|--------------|----------|-----------|
| `mutableStateOf` в Activity | Не прив'язано до Compose lifecycle | `remember { mutableStateOf() }` в `@Composable` |
| Static Service references | Memory leaks | WeakReference або proper cleanup |
| Network callback без unregister | Memory leak | Unregister в onDestroy |
| Hardcoded API URLs | Security risk | BuildConfig або Remote Config |

### Середні

| Anti-Pattern | Проблема | Правильно |
|--------------|----------|-----------|
| `while(true)` в LaunchedEffect | Неефективно | `animate*AsState()` або `snapshotFlow` |
| SimpleDateFormat | Not thread-safe | `java.time` API |
| Без error handling в coroutines | Silent failures | try-catch або `CoroutineExceptionHandler` |

### Низькі

| Anti-Pattern | Проблема | Правильно |
|--------------|----------|-----------|
| Hardcoded strings | Без локалізації | `stringResource(R.string.xxx)` |
| Magic numbers | Hard to maintain | Constants |
| Без contentDescription | Accessibility | Завжди додавати |

## Code Quality

Проект дотримується політики **0 warnings / 0 errors**.

```bash
./gradlew detekt                  # Static analysis
./gradlew detekt --auto-correct   # Auto-fix
./gradlew :app:lintDebug          # Android Lint
```

### Типові warnings

| Warning | Причина | Fix |
|---------|---------|-----|
| KT-73255 annotation target | `@Qualifier` на constructor param | `@param:Qualifier` |
| UnsafeOptInUsageError | `@UnstableApi` | `@OptIn(UnstableApi::class)` |
| Unused parameter | Параметр не використовується | Видалити або `_prefix` |
