# Screenshot Generation

NoteDelight screenshots are generated from dedicated Compose Preview functions
with the Android CLI and Android Studio. The generated assets are a common
curated screenshot set for README and store metadata preparation. They are not
yet vendor-specific Google Play or App Store upload assets.

## Prerequisites

- Open this repository in Android Studio.
- Wait until Android Studio shows the `NoteDelight` project as ready.
- Ensure the `android` CLI is available on `PATH`.
- Run the command on macOS, because the script validates PNG dimensions with
  `sips`.

Check Studio status manually:

```bash
android studio check
```

## Generate Screenshots

Run from the repository root:

```bash
.github/scripts/generate_screenshots.sh
```

The script renders `ScreenshootPreview.kt` through:

```bash
android studio render-compose-preview --project=NoteDelight
```

Generated files are written to:

```text
docs/screenshoots/store/{phone,tablet}/{light,dark}/NN-scenario.png
```

Current scenarios:

- phone: `01-notes`, `02-note-detail`, `03-sign-in`, `04-security-settings`
- tablet: `01-notes`, `02-sign-in`, `03-security-settings`

## Preview Design

Marketing screenshots live in
`core/test/ui/src/androidMain/kotlin/com/softartdev/notedelight/screenshot_preview/ScreenshootPreview.kt`.
Keep these previews separate from everyday development previews so the output is
stable and intentional.

Use one top-level preview function per output file. Do not use MultiPreview for
generated screenshots, because `android studio render-compose-preview` renders a
named composable and does not expose a stable selector for individual
MultiPreview variants.

Preview functions should receive curated demo state from
`ScreenshotPreviewProvider`, using presentation state types such as
`SignInResult`, `NoteListResult`, `NoteResult`, `SettingsCategoriesResult`, and
`SettingsResult`. For previews that depend on Koin-backed UI helpers, wrap the
content with `PreviewKoin`.

The first version intentionally generates one shared scenario set for stores.
Google Play and App Store upload-ready resizing and fastlane integration should
be handled as a later step.
