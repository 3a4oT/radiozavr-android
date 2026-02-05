# Radiozavr

Слухай радіо рідного міста — 104.7 FM Львів — на Android TV. Простий додаток без реклами і компромісів. Все буде ЛЮКС :)

Як бонус — режим віджетів з загадками для всієї родини.

<!-- TODO: Replace with new screenshot after release -->
![Radiozavr TV Interface](https://github.com/3a4oT/radiozavr-android/releases/download/0.1.1/tv-demo-radio-streamer.png)

## Можливості

| | |
|---|---|
| 📻 **Пряма трансляція** | Львівське радіо 104.7 у кришталевій якості |
| 📺 **TV First** | Оптимізовано для пульта та великого екрану |
| 🎯 **Загадки** | Сімейна гра під час прослуховування |
| 🎨 **Візуалізатор** | Аудіо-бари танцюють в ритм музики |
| 🌗 **Теми** | Світла, темна, автоматична |
| 📱 **Телефон** | Працює і на мобільних пристроях |

## Встановлення

**Google Play** — *скоро*

**APK** — [Releases](https://github.com/3a4oT/radiozavr-android/releases)

---

## Development

> Для контриб'юторів: [docs/DEVELOPMENT.md](docs/DEVELOPMENT.md)

### Tech Stack

Kotlin 2.3 • Jetpack Compose • Material 3 • Hilt • Media3 • Fastlane

### Команди

```bash
# Fastlane
bundle exec fastlane build_debug    # Build
bundle exec fastlane lint           # Detekt + Lint
bundle exec fastlane test           # Tests

# Gradle
./gradlew assembleDebug             # Build
./gradlew detekt                    # Static analysis
./gradlew detekt --auto-correct     # Auto-fix formatting
./gradlew lintDebug                 # Android Lint
./gradlew test                      # Tests
```

[Підпис релізу](signing/README.md)

## Ліцензія

MIT
