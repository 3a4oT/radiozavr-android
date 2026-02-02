## Слухай Радіо Люкс ФМ Львів на своєму Android TV

 📺 Дуже простий додаток який запускає інтернет стрім **Радіо Люкс ФМ Львів 104.7 FM** на вашому AndroidTV. Має динамічне тло та годинник. Може використовуватись як демо режим для телевізора.


![screenStreamerLuXFM](https://github.com/3a4oT/radio-lux-fm-lviv-android-streamer/releases/download/0.1.1/tv-demo-radio-streamer.png)



⚙️👨🏻‍🔧 [Налаштування підпису аплікації](/signing/README.MD)

---

## Development

> **Для контриб'юторів:** див. [docs/DEVELOPMENT.md](docs/DEVELOPMENT.md) — налаштування Firebase та CI.

### Tech Stack

| Компонент | Технологія |
|-----------|------------|
| **Мова** | Kotlin |
| **UI** | Jetpack Compose |
| **Архітектура** | Clean Architecture, Repository Pattern |
| **DI** | Hilt |
| **Async** | Kotlin Coroutines, Flow |
| **Static Analysis** | Detekt (formatting + style + bugs) |
| **Android Lint** | Built-in |

### Команди

```bash
./gradlew assembleDebug           # Build
./gradlew detekt                  # Static analysis
./gradlew detekt --auto-correct   # Auto-fix formatting
./gradlew lintDebug               # Android Lint
./gradlew test                    # Tests
```
