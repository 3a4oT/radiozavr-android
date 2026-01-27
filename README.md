## Слухай Радіо Люкс ФМ Львів на своєму Android TV

 📺 Дуже простий додаток який запускає інтернет стрім **Радіо Люкс ФМ Львів 104.7 FM** на вашому AndroidTV. Має динамічне тло та годинник. Може використовуватись як демо режим для телевізора.


![screenStreamerLuXFM](/playstore/tv-demo-radio-streamer.png)



⚙️👨🏻‍🔧 [Налаштування підпису аплікації](/signing/README.MD)

---

## Web Admin

Веб-панель для керування конфігурацією додатку через Firebase Remote Config.

### Функціонал

- **Ticker** — керування біжучими повідомленнями
- **Predictions** — тексти передбачень (рандомно на день)
- **Settings** — URL стріму, контакти, feature flags, оновлення
- **Deploy** — публікація змін у production

### Локальна розробка

```bash
cd web
cp .env.example .env      # Заповнити Firebase credentials
npm install
npm run dev               # http://localhost:5173
```

### Команди

```bash
npm run dev       # Dev server
npm run build     # Production build (auto-lint)
npm run lint      # Перевірка коду
npm run lint:fix  # Автовиправлення
npm run format    # Форматування
```

### CI/CD Secrets

Для автоматичного деплою web admin на Firebase Hosting потрібні GitHub Secrets:

| Secret | Опис |
|--------|------|
| `FIREBASE_API_KEY` | Firebase Web API Key |
| `FIREBASE_AUTH_DOMAIN` | `{project-id}.firebaseapp.com` |
| `FIREBASE_PROJECT_ID` | Firebase Project ID |
| `FIREBASE_STORAGE_BUCKET` | `{project-id}.appspot.com` |
| `FIREBASE_MESSAGING_SENDER_ID` | Sender ID з Firebase Console |
| `FIREBASE_APP_ID` | App ID з Firebase Console |
| `FIREBASE_SERVICE_ACCOUNT` | JSON ключ service account |
| `ADMIN_EMAIL` | Email адміністратора (Google Sign-In whitelist) |

### Отримання Service Account

1. Firebase Console → Project Settings → Service Accounts
2. Generate new private key
3. Скопіювати весь JSON у secret `FIREBASE_SERVICE_ACCOUNT`
