# Radiozavr

Радіо рідного міста на великому екрані.
104.7 FM — тепер на твоєму Android TV.

Без реклами. Без підписок. У Львові все буде ЛЮКС :)

*Фонова музика із саундбаром, demo-режим для телевізора, або цікаві факти, коли проходиш повз екран.*

![Radiozavr TV Interface](https://github.com/3a4oT/radiozavr-android/releases/download/0.5.0/Screenshot_20260207_152607.png)

## Для Android TV

| | |
|---|---|
| 📺 **Великий екран** | Створений для телевізора і пульта |
| 📻 **Пряма трансляція** | Львівське радіо у кришталевій якості |
| 🎯 **Віджети** | Загадки для всієї родини під музику |
| 🎨 **Атмосфера** | Візуалізатор і теми на твій смак |

## Завантажити

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

[Підпис релізу](signing/README.MD)

## Політика конфіденційності

[Privacy Policy](https://3a4ot.github.io/radiozavr-android/privacy-policy.html)

## Ліцензія

[PolyForm Noncommercial 1.0.0](LICENSE) — безкоштовно для особистого використання.
