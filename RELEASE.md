# Android Release Guide

This project uses an automated GitHub Actions workflow (`.github/workflows/release.yml`) to gracefully handle version bumping, building, signing, and releasing the Android application to both GitHub Releases and the Google Play Store.

## How It Works

When you manually trigger the **Create Android Release (APK & AAB)** workflow from the GitHub `Actions` tab, it completes the following sequence of operations:

1. **Auto-Versioning**: Parses `app/build.gradle.kts` and dynamically bumps the `versionName` (Patch, Minor, or Major based on your input), whilst incrementing the `versionCode` by 1.
2. **Commit and Tag**: The modified gradle files are committed via a `github-actions[bot]` user and tagged with the new release version.
3. **Build Step**: An unsigned `.apk` (useful for quick side-loading installs) and an unsigned Android App Bundle `.aab` (required by Play Store) are compiled.
4. **App Signing**: Takes those unsigned builds, downloads your encrypted Keystore (provided as a base64 GitHub Secret), and effectively signs your app through the `r0adkll/sign-android-release` action.
5. **GitHub Release**: Uses that specific tag name to automatically draft a GitHub Release containing both the signed `.apk` and signed `.aab`. It dynamically generates release notes based on the commits since the last tag.
6. **Play Store Publication**: Pushes the newly signed `.aab` directly to your desired Google Play Console release track.

---

## 🔒 Configuration Requirements

To successfully run this pipeline without any missing keys or Play Store errors, you **must populate the following items** inside your GitHub Repository's **Settings -> Secrets and variables -> Actions** as "Repository secrets":

| Secret Name | Description |
| :--- | :--- |
| `KEYSTORE_FILE_BASE64` | The Base64 encoded string of your Android Keystore (`.jks` or `.keystore`) file. You can generate this by running `base64 -w 0 my-release-key.jks > encoded.txt` locally (or without `-w 0` on Mac). |
| `KEYSTORE_PASSWORD` | The password used to lock your entire Keystore file. |
| `KEY_ALIAS` | The specific alias name given to your App Signing Key inside the keystore. |
| `KEY_PASSWORD` | The password configured specifically for your key alias. |
| `PLAY_STORE_CREDENTIALS` | The raw JSON payload of your Google Cloud Service Account that has explicitly been granted "Release Manager" permissions on your Google Play Console configuration. |

---

## 🚀 Triggering a Release

Follow these steps to ship a new version:
1. Navigate to the **Actions** tab located under your repository's top navigation bar.
2. On the left sidebar list of Workflows, select **Create Android Release (APK & AAB)**.
3. On the right side of the screen, click the **Run workflow** dropdown button.
4. Choose the specific **Version bump type** (e.g. `minor` if introducing a new feature, `patch` for a bugfix).
5. Choose your target **Play Store Track** (Internal, Alpha, Beta, or Production).
6. Click **Run workflow**.

The workflow typically takes 3-10 minutes. Once you see a completely green checkmark, your latest code is securely released!
