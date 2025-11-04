# OPFS Implementation in NoteDelight Web App

## Overview

NoteDelight's web application uses **OPFS (Origin-Private FileSystem)** to provide persistent database storage that survives browser sessions. This implementation replaces the previous IndexedDB-based approach with a more performant and reliable solution using the official SQLite WebAssembly build.

## Architecture Integration

### Core Components

```
┌─────────────────────────────────────────────────────────────────┐
│                    NoteDelight Web App                         │
├─────────────────────────────────────────────────────────────────┤
│  Frontend (Compose Multiplatform)                              │
│  ├── UI Components (ui:shared)                                 │
│  ├── ViewModels (core:presentation)                            │
│  └── Domain Logic (core:domain)                                │
├─────────────────────────────────────────────────────────────────┤
│  Data Layer (core:data:db-sqldelight)                          │
│  ├── WebDatabaseHolder (OPFS-enabled)                          │
│  ├── Custom Web Worker (sqlite.worker.js)                      │
│  └── SQLDelight Queries & DAOs                                 │
├─────────────────────────────────────────────────────────────────┤
│  Browser Storage Layer                                         │
│  ├── OPFS (Origin-Private FileSystem)                          │
│  ├── Official SQLite WASM (sqlite3.wasm)                       │
│  └── Web Worker Thread                                         │
└─────────────────────────────────────────────────────────────────┘
```

### File Structure

```
app/web/
├── src/wasmJsMain/resources/
│   ├── sqlite.worker.js                 # Custom OPFS worker
│   ├── coi-serviceworker.js            # Cross-origin headers for GitHub Pages
│   └── index.html                       # Service worker integration
├── webpack.config.d/
│   ├── opfs.js                         # Development server headers
│   └── sqljs-config.js                 # SQLite file handling
└── build.gradle.kts                    # SQLite WASM download & build config

core/data/db-sqldelight/src/wasmJsMain/kotlin/
└── com/softartdev/notedelight/db/
    └── WebDatabaseHolder.kt            # OPFS-enabled database holder
```

## Implementation Details

### 1. Database Holder (`WebDatabaseHolder.kt`)

```kotlin
class WebDatabaseHolder : SqlDelightDbHolder {
    override val driver: SqlDriver = WebWorkerDriver(worker = jsWorker())
    override val noteDb: NoteDb = createQueryWrapper(driver)
    override val noteQueries: NoteQueries = noteDb.noteQueries

    override fun close() = driver.close()
}

// Create worker with custom OPFS-enabled script
private fun jsWorker(): Worker = js("new Worker(new URL('sqlite.worker.js', import.meta.url))")
```

**Key Changes:**
- Replaced `createDefaultWebWorkerDriver()` with custom `WebWorkerDriver`
- Uses custom `sqlite.worker.js` script with OPFS configuration
- Maintains same `SqlDelightDbHolder` interface for compatibility

### 2. OPFS Web Worker (`sqlite.worker.js`)

```javascript
importScripts("sqlite3.js");

async function createDatabase() {
  const sqlite3 = await sqlite3InitModule();
  
  // Key OPFS configuration - uses OPFS VFS
  db = new sqlite3.oo1.DB("file:database.db?vfs=opfs", "c");
}
```

**Key Features:**
- Uses official SQLite WASM build (`sqlite3.js`)
- Configures database with `vfs=opfs` for OPFS storage
- Handles SQLDelight worker protocol (exec, transactions)
- Runs database operations off main thread

### 3. Build System Integration (`build.gradle.kts`)

```kotlin
// Download official SQLite WASM build
val sqliteDownload = tasks.register("sqliteDownload", Download::class.java) {
    src("https://sqlite.org/2024/sqlite-wasm-3460100.zip")
    dest(layout.buildDirectory.dir("tmp"))
    onlyIfModified(true)
}

// Extract SQLite files to resources
val sqliteUnzip = tasks.register("sqliteUnzip", Copy::class.java) {
    dependsOn(sqliteDownload)
    from(zipTree("sqlite-wasm-3460100.zip")) {
        include("sqlite-wasm-3460100/jswasm/**")
        exclude("**/*worker1*") // Use our custom worker
    }
    into(layout.buildDirectory.dir("sqlite"))
}

// Hook into resource processing
tasks.named("wasmJsProcessResources").configure {
    dependsOn(sqliteUnzip)
}
```

**Automation:**
- Downloads latest SQLite WASM build from sqlite.org
- Extracts necessary files (`sqlite3.js`, `sqlite3.wasm`, etc.)
- Integrates with Gradle resource processing
- Excludes default worker (we use custom one)

### 4. Webpack Configuration

#### Development Headers (`opfs.js`)
```javascript
config.devServer = {
  ...config.devServer,
  headers: {
    "Cross-Origin-Embedder-Policy": "require-corp",
    "Cross-Origin-Opener-Policy": "same-origin",
  }
}
```

#### Asset Management (`sqljs-config.js`)
```javascript
config.plugins.push(
    new CopyWebpackPlugin({
        patterns: [
            // Official SQLite WASM files
            { from: '../../../build/sqlite/sqlite3.wasm', to: 'sqlite3.wasm' },
            { from: '../../../build/sqlite/sqlite3.js', to: 'sqlite3.js' },
            // OPFS support files
            { from: '../../../build/sqlite/sqlite3-opfs-async-proxy.js', to: 'sqlite3-opfs-async-proxy.js' },
            // Legacy fallback files
            { from: '../../node_modules/sql.js/dist/sql-wasm.wasm', to: 'sql-wasm.wasm' },
            { from: '../../node_modules/sql.js/dist/sql-wasm.js', to: 'sql-wasm.js' },
        ]
    })
);
```

### 5. Cross-Origin Header Support

#### Development Server
- Headers automatically configured in `webpack.config.d/opfs.js`
- Enables OPFS during local development

#### Production Deployment (GitHub Pages)
- Uses `coi-serviceworker.js` service worker
- Automatically adds required headers for static hosting
- Enables OPFS on platforms that don't support custom headers

## Benefits in NoteDelight

### Performance Improvements
- ✅ **Direct file system access**: Faster than IndexedDB
- ✅ **Web worker isolation**: Database operations don't block UI
- ✅ **Native SQLite**: Full SQLite feature set and performance

### Storage Advantages
- ✅ **Persistent storage**: Data survives browser sessions and crashes
- ✅ **Larger capacity**: Not limited by IndexedDB quotas
- ✅ **Reliable**: Less prone to corruption than IndexedDB

### User Experience
- ✅ **Seamless persistence**: Notes saved automatically persist
- ✅ **Faster startup**: Reduced database initialization time
- ✅ **Better reliability**: Consistent database behavior across sessions

## Browser Compatibility

### OPFS Support Matrix

| Browser | Version | OPFS Support | Notes |
|---------|---------|--------------|-------|
| Chrome  | 86+     | ✅ Full      | Best performance |
| Edge    | 86+     | ✅ Full      | Same as Chrome |
| Firefox | 111+    | ✅ Full      | Slightly slower |
| Safari  | 15.2+   | ✅ Full      | iOS 15.2+ |

### Fallback Strategy
- Legacy SQL.js files included for compatibility
- Graceful degradation if OPFS unavailable
- Automatic detection and fallback

## Deployment Configuration

### Development
```bash
# Includes OPFS headers automatically
./gradlew :app:web:wasmJsBrowserDevelopmentRun
```

### Production Build
```bash
# Includes all OPFS files and service worker
./gradlew :app:web:wasmJsBrowserProductionWebpack
```

### GitHub Pages
- `coi-serviceworker.js` enables required headers
- No server configuration needed
- Works with standard GitHub Pages setup

### Other Static Hosts
- Service worker approach works universally
- No special server configuration required
- Compatible with Vercel, Netlify, etc.

## Technical Specifications

### Database Configuration
- **Database name**: `database.db`
- **VFS**: `opfs` (Origin-Private FileSystem)
- **Location**: Browser's origin-private storage
- **Access mode**: Read/write with creation

### File Sizes (Production Build)
- `sqlite3.wasm`: ~600KB (Official SQLite)
- `sqlite3.js`: ~45KB (SQLite JavaScript interface)
- `sqlite.worker.js`: ~1.4KB (Custom OPFS worker)
- `coi-serviceworker.js`: ~6KB (Cross-origin headers)

### Memory Usage
- Web Worker: Isolated memory space
- OPFS: Direct file system access (no memory copying)
- SQLite: Efficient memory management

## Comparison with Previous Implementation

| Aspect | Previous (SQL.js + IndexedDB) | Current (SQLite WASM + OPFS) |
|--------|------------------------------|-------------------------------|
| **Performance** | Moderate | High |
| **Persistence** | Session-based | Permanent |
| **Storage Size** | Limited by quotas | Much larger capacity |
| **Reliability** | IndexedDB issues | File system reliability |
| **SQLite Version** | Older sql.js build | Latest official SQLite |
| **Browser Support** | Wider | Modern browsers (86+) |

## Future Enhancements

### Planned Improvements
- [ ] **Compression**: Enable database compression for space efficiency  
- [ ] **Backup/Export**: Direct file access enables easier backup
- [ ] **Sync**: Foundation for future cloud sync capabilities
- [ ] **Offline**: Enhanced offline capabilities with service worker

### Potential Optimizations  
- [ ] **Lazy loading**: Load SQLite WASM only when needed
- [ ] **Caching**: Improved caching strategies for SQLite files
- [ ] **Performance monitoring**: Track OPFS performance metrics

## Troubleshooting

### Common Issues

1. **OPFS not working**
   - Check browser version (need 86+)
   - Verify HTTPS or localhost context
   - Check cross-origin headers in devtools

2. **Database not persisting**
   - Verify OPFS is actually being used
   - Check browser storage settings
   - Ensure service worker is active

3. **Build failures**
   - Clear Gradle cache: `./gradlew clean`
   - Check SQLite download: `./gradlew :app:web:sqliteDownload`
   - Verify webpack configuration

### Debug Information
- Browser DevTools → Application → Storage → Origin Private File System
- Network tab: Check for `sqlite3.wasm` and `sqlite.worker.js` loads
- Console: Look for SQLite initialization messages

## Related Documentation

- [`app/web/README.md`](../app/web/README.md) - Web app module documentation
- [`docs/WEB_DEVELOPMENT_WORKFLOW.md`](WEB_DEVELOPMENT_WORKFLOW.md) - Development workflow
- [`docs/ARCHITECTURE.md`](ARCHITECTURE.md) - Overall project architecture

## Conclusion

The OPFS implementation in NoteDelight provides a significant upgrade to the web application's data persistence capabilities. By leveraging the official SQLite WebAssembly build with OPFS storage, users now have reliable, performant, and persistent note storage that matches the quality of native applications.

This implementation demonstrates how modern web APIs can provide native-like experiences while maintaining the accessibility and deployment simplicity of web applications.
