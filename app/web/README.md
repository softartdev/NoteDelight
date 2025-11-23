# Web App Module

## Overview

The `app:web` module is the **Web application** for NoteDelight, built with Compose Multiplatform for WebAssembly (Wasm). It provides a browser-based note-taking experience with the same UI and features as the native apps.

⚠️ **Status**: Experimental - Web support is in development preview.

## Purpose

- Provide web application entry point
- Deploy as static website (GitHub Pages, Vercel, etc.)
- Support progressive web app (PWA) features
- Demonstrate Compose Multiplatform Web capabilities
- Enable browser-based note-taking without installation

## Architecture

```
app:web (Web Application - Wasm)
    ├── src/
    │   ├── wasmJsMain/
    │   │   ├── kotlin/
    │   │   │   └── com/softartdev/notedelight/
    │   │   │       └── main.kt              # Web entry point
    │   │   └── resources/
    │   │       ├── index.html               # HTML page
    │   │       └── styles.css               # Custom styles
    │   └── wasmJsTest/
    │       └── kotlin/                      # Web tests (future)
    ├── build.gradle.kts                     # Build configuration
    └── webpack.config.d/
        └── sqljs-config.js                  # SQL.js webpack config
```

## Key Components

### main.kt

Web application entry point:

```kotlin
fun main() {
    // Initialize Koin
    startKoin {
        modules(webModule)
    }
    
    // Render Compose app to HTML canvas
    CanvasBasedWindow(canvasElementId = "ComposeTarget") {
        App() // Shared Compose UI
    }
}
```

### index.html

HTML entry point:

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Note Delight</title>
    <link rel="stylesheet" href="styles.css">
</head>
<body>
    <canvas id="ComposeTarget"></canvas>
    <script src="web.js"></script>
</body>
</html>
```

## Technology Stack

### WebAssembly (Wasm)

- **Kotlin/Wasm**: Compiles Kotlin to WebAssembly
- **Performance**: Near-native performance in browsers
- **Size**: Compact binary format
- **Security**: Sandboxed execution environment

### Database

- **Official SQLite WASM**: Native SQLite compiled to WebAssembly
- **OPFS Storage**: Origin-Private FileSystem for persistent database storage
- **Web Worker**: Database operations run off the main thread
- **No encryption**: SQLCipher not available in browsers

### Webpack

- **Module bundler**: Bundles Wasm, JS, and resources
- **Development server**: Hot reload during development
- **Production build**: Optimized bundles

## Building & Running

### Development Server

```bash
# Run development server with hot reload
./gradlew :app:web:wasmJsBrowserDevelopmentRun --continuous
```

This starts a webpack dev server at `http://localhost:8080`

### Production Build

```bash
# Build optimized production bundle
./gradlew :app:web:wasmJsBrowserProductionWebpack
```

Output: `app/web/build/dist/wasmJs/productionExecutable/`

### Build Output

```
build/dist/wasmJs/productionExecutable/
├── index.html                    # Entry HTML
├── composeApp.js                 # JavaScript loader
├── composeApp.wasm              # Application WebAssembly binary
├── skiko.wasm                   # Skia graphics engine
├── sqlite3.js                   # SQLite JavaScript
├── sqlite3.wasm                 # Official SQLite WASM
├── sqlite.worker.js             # Custom OPFS worker
├── coi-serviceworker.js         # Service worker for headers
└── sql-wasm.wasm               # Legacy SQL.js (fallback)
```

## Deployment

### Static Hosting

The web app is a static site that can be hosted on:

#### GitHub Pages

```bash
# Build production
./gradlew :app:web:wasmJsBrowserProductionWebpack

# Deploy to gh-pages branch
cd build/dist/wasmJs/productionExecutable
git init
git add .
git commit -m "Deploy"
git push -f origin gh-pages
```

Hosted at: `https://username.github.io/NoteDelight/`

#### Vercel

```bash
# Install Vercel CLI
npm i -g vercel

# Build
./gradlew :app:web:wasmJsBrowserProductionWebpack

# Deploy
cd build/dist/wasmJs/productionExecutable
vercel --prod
```

#### Netlify

```bash
# Build
./gradlew :app:web:wasmJsBrowserProductionWebpack

# Deploy via Netlify CLI or drag-and-drop
netlify deploy --prod --dir=build/dist/wasmJs/productionExecutable
```

### CI/CD

Automated deployment via GitHub Actions:

```yaml
name: Deploy Web App
on:
  push:
    branches: [main]

jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Setup JDK
        uses: actions/setup-java@v3
        with:
          java-version: '17'
      - name: Build Web App
        run: ./gradlew :app:web:wasmJsBrowserProductionWebpack
      - name: Deploy to GitHub Pages
        uses: peaceiris/actions-gh-pages@v3
        with:
          github_token: ${{ secrets.GITHUB_TOKEN }}
          publish_dir: ./app/web/build/dist/wasmJs/productionExecutable
```

## Configuration

### Webpack Config

Custom webpack configuration in `webpack.config.d/sqljs-config.js`:

```javascript
config.resolve = {
    fallback: {
        fs: false,
        path: false,
        crypto: false,
    }
};

// Configure SQL.js
config.plugins.push(
    new CopyWebpackPlugin({
        patterns: [
            {
                from: '../sql-wasm.wasm',
                to: '.'
            }
        ]
    })
);
```

### Build Configuration

```kotlin
kotlin {
    wasmJs {
        moduleName = "web"
        browser {
            commonWebpackConfig {
                outputFileName = "web.js"
            }
        }
        binaries.executable()
    }
}

compose.experimental {
    web.application {}
}
```

## Browser Support

### Minimum Requirements

- **Chrome/Edge**: 119+ (Wasm GC support)
- **Firefox**: 120+ (Wasm GC support)
- **Safari**: 17.4+ (Wasm GC support)

### Feature Detection

```kotlin
fun isWasmSupported(): Boolean = js("""
    (function() {
        try {
            if (typeof WebAssembly === "object" &&
                typeof WebAssembly.instantiate === "function") {
                return true;
            }
        } catch (e) {}
        return false;
    })()
""") as Boolean
```

## Limitations

### Current Limitations

1. ❌ **No encryption**: SQLCipher not available in browsers
2. ✅ **Storage**: OPFS provides persistent database storage
3. ⚠️ **File access**: Restricted browser file API
4. ⚠️ **Performance**: Slower than native (improving)
5. ⚠️ **Binary size**: Larger initial download than native apps
6. ⚠️ **Experimental**: Wasm support still evolving

### Workarounds

- **Files**: Use File System Access API when available
- **Performance**: Lazy loading, code splitting
- **Size**: Compression, caching, CDN

## OPFS Database Storage

The web app now uses OPFS (Origin-Private FileSystem) for persistent database storage, providing better performance and reliability than IndexedDB.

### Requirements

1. **Secure context**: HTTPS or localhost
2. **Cross-Origin headers**: Automatically configured
   - `Cross-Origin-Embedder-Policy: require-corp`
   - `Cross-Origin-Opener-Policy: same-origin`

### Benefits

- ✅ **Persistent storage**: Survives browser sessions
- ✅ **Better performance**: Direct file system access  
- ✅ **Larger capacity**: Not limited by IndexedDB quotas
- ✅ **Real SQLite**: Uses official SQLite WASM build

### Browser Support

- **Chrome/Edge**: 86+ (OPFS support)
- **Firefox**: 111+ (OPFS support)
- **Safari**: 15.2+ (OPFS support)

For deployment on static hosts like GitHub Pages, a service worker (`coi-serviceworker.js`) automatically enables the required headers.

## Locale Management

The web app supports dynamic locale switching through Compose Multiplatform resources:

- **Locale Override**: JavaScript code in `index.html` overrides `navigator.languages` to support custom locale selection
- **Locale Storage**: The `LocaleInteractor.wasmJs` implementation uses `window.__customLocale` to persist locale preferences
- **Supported Languages**: English and Russian

The locale override script must be loaded before `composeApp.js` to ensure proper initialization. The script checks for `window.__customLocale` and returns it when set, otherwise falls back to the default `navigator.languages` behavior.

## Progressive Web App (PWA)

### Service Worker

Enable offline support:

```javascript
// sw.js
self.addEventListener('install', (event) => {
    event.waitUntil(
        caches.open('note-delight-v1').then((cache) => {
            return cache.addAll([
                '/',
                '/index.html',
                '/web.js',
                '/web.wasm',
                '/styles.css'
            ]);
        })
    );
});
```

### Web Manifest

```json
{
    "name": "Note Delight",
    "short_name": "NoteDelight",
    "start_url": "/",
    "display": "standalone",
    "background_color": "#ffffff",
    "theme_color": "#6200ee",
    "icons": [
        {
            "src": "/icon-192.png",
            "sizes": "192x192",
            "type": "image/png"
        },
        {
            "src": "/icon-512.png",
            "sizes": "512x512",
            "type": "image/png"
        }
    ]
}
```

## Dependencies

### Core
- `core:domain` - Domain layer
- `core:data:db-sqldelight` - Data layer (with sql.js)
- `core:presentation` - ViewModels
- `ui:shared` - Shared UI

### Compose Web
- `compose.web.application` - Compose for Web
- `compose.runtime` - Compose runtime

### Browser APIs
- `kotlinx.browser` - Browser API bindings

### Database
- `sqlDelight.web` - SQLDelight Web driver
- NPM: `sql.js` - SQLite for browsers
- NPM: `@cashapp/sqldelight-sqljs-worker` - Web Worker support

## Testing

### Unit Tests

Web-specific tests:

```kotlin
// In wasmJsTest/
@Test
fun testWebInitialization() {
    // Test web-specific initialization
}
```

### Running Tests

```bash
./gradlew :app:web:wasmJsTest
```

### Browser Testing

Manual testing in browsers:

```bash
# Start dev server
./gradlew :app:web:wasmJsBrowserDevelopmentRun --continuous

# Open in browser
open http://localhost:8080
```

## Performance Optimization

### Code Splitting

```kotlin
// Lazy load heavy features
val heavyFeature by lazy {
    loadHeavyFeature()
}
```

### Compression

Enable compression in server config:

```nginx
# Nginx
gzip on;
gzip_types application/wasm application/javascript;
```

### Caching

Configure cache headers:

```
Cache-Control: public, max-age=31536000, immutable
```

## AI Agent Guidelines

When working with this module:

1. **Browser APIs**: Use `kotlinx.browser` for DOM access
2. **No native code**: Everything must work in browser sandbox
3. **Size matters**: Minimize bundle size
4. **Progressive enhancement**: Detect and use modern APIs gracefully
5. **Testing**: Test in multiple browsers
6. **Security**: No sensitive data without encryption
7. **Performance**: Profile and optimize load time
8. **Responsive**: Support mobile and desktop browsers
9. **Accessibility**: Follow WCAG guidelines
10. **PWA**: Consider offline functionality

## Best Practices

### Resource Loading

```kotlin
// Lazy load resources
val image by lazy {
    loadResource("image.png")
}
```

### State Persistence

```kotlin
// Use localStorage for settings
fun saveSettings(settings: Settings) {
    window.localStorage.setItem(
        "settings",
        JSON.stringify(settings)
    )
}
```

### Error Handling

```kotlin
try {
    // Browser operation
} catch (e: dynamic) {
    console.error("Browser error:", e)
    showUserFriendlyError()
}
```

## Troubleshooting

### Build Issues

1. **Wasm not found**: Update Kotlin and Compose plugins
2. **Webpack errors**: Check `webpack.config.d/` configuration
3. **Missing dependencies**: Run `./gradlew --refresh-dependencies`

### Runtime Issues

1. **White screen**: Check browser console for errors
2. **Database errors**: Verify sql.js is loaded
3. **Slow performance**: Profile with browser DevTools

### Browser Compatibility

1. **Check Wasm GC support**: Use modern browser versions
2. **Polyfills**: Not available for Wasm GC
3. **Fallback**: Provide message for unsupported browsers

## Related Modules

- **Depends on**: `ui:shared`, `core:presentation`, `core:data:db-sqldelight`, `core:domain`
- **Alternative apps**: `app:android`, `app:desktop`, `app:iosApp`

## Resources

- [Kotlin/Wasm](https://kotlinlang.org/docs/wasm-overview.html)
- [Compose Multiplatform Web](https://github.com/JetBrains/compose-jb)
- [SQL.js](https://sql.js.org/)
- [WebAssembly](https://webassembly.org/)
- [PWA Documentation](https://web.dev/progressive-web-apps/)

