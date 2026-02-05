# Налаштування для розробки

## Firebase

Проект використовує Firebase Analytics та Crashlytics. Для збірки потрібен `google-services.json`.

### Локальна розробка

1. Відкрий [Firebase Console](https://console.firebase.google.com/)
2. Вибери або створи проект
3. Додай Android app з package name: `com.rovenskyi.radiozavr`
4. Завантаж `google-services.json`
5. Розмісти в `app/google-services.json`

> **Увага:** `google-services.json` в `.gitignore` — ніколи не комітити.

### CI/CD Secret

Для GitHub Actions `google-services.json` зберігається як repository secret.

1. Закодуй в base64:
   ```bash
   base64 -i app/google-services.json | pbcopy  # macOS
   base64 -w 0 app/google-services.json         # Linux
   ```
2. Repository **Settings** → **Secrets and variables** → **Actions**
3. **New repository secret**: `GOOGLE_SERVICES_JSON`

## Команди

```bash
./gradlew assembleDebug           # Debug збірка
./gradlew assembleRelease         # Release (потрібен signing)
./gradlew detekt                  # Static analysis
./gradlew detekt --auto-correct   # Auto-fix code style
./gradlew lintDebug               # Android Lint
./gradlew test                    # Unit tests
```

## Документація

- [ARCHITECTURE.md](ARCHITECTURE.md) — модулі, Clean Architecture, State, DI
- [UI-GUIDE.md](UI-GUIDE.md) — UI дизайн, компоненти, TV/Phone
