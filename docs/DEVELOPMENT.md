# Development Setup

## Firebase Configuration

This project uses Firebase Analytics and Crashlytics. You need `google-services.json` to build the app.

### Local Development

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Select or create a project
3. Add Android app with package name: `com.rovenskyi.radio_lux_fm_lviv_streamer`
4. Download `google-services.json`
5. Place it in `app/google-services.json`

> **Note:** `google-services.json` is in `.gitignore` and should never be committed.

### CI/CD Setup

For GitHub Actions, the `google-services.json` is stored as a repository secret.

#### Adding the Secret

1. Encode `google-services.json` to base64:
   ```bash
   base64 -i app/google-services.json | pbcopy  # macOS
   base64 -w 0 app/google-services.json         # Linux
   ```
2. Go to repository **Settings** → **Secrets and variables** → **Actions**
3. Click **New repository secret**
4. Name: `GOOGLE_SERVICES_JSON`
5. Value: paste the base64-encoded string

#### Workflow Usage

```yaml
- name: Decode google-services.json
  env:
    GOOGLE_SERVICES_JSON: ${{ secrets.GOOGLE_SERVICES_JSON }}
  run: echo "$GOOGLE_SERVICES_JSON" | base64 --decode > app/google-services.json
```

### Firebase Features Used

| Feature | Purpose |
|---------|---------|
| **Analytics** | Screen views, button clicks, player events |
| **Crashlytics** | Error tracking, non-fatal exceptions |

### Debug Mode (DebugView)

To see events in real-time during development:

```bash
adb shell setprop debug.firebase.analytics.app com.rovenskyi.radio_lux_fm_lviv_streamer
```

Then open Firebase Console → Analytics → DebugView.

To disable:

```bash
adb shell setprop debug.firebase.analytics.app .none.
```

## Build Commands

```bash
./gradlew assembleDebug           # Debug build
./gradlew assembleRelease         # Release build (requires signing)
./gradlew detekt                  # Static analysis
./gradlew detekt --auto-correct   # Auto-fix code style
./gradlew lintDebug               # Android Lint
./gradlew test                    # Unit tests
```

## Architecture

See [CLAUDE.md](/CLAUDE.md) for architecture guidelines and coding standards.
