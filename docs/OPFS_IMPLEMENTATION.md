# OPFS database implementation

The WasmJS application stores the active ORM database in the browser's Origin-Private File
System (OPFS). SQLDelight and Room 3 use the same SQLite3MultipleCiphers WASM runtime, the same
`notes.db` file, and the same v2 `note` schema.

## Architecture

```text
Compose Web / domain contracts
              |
     CORE_DATA_DB_MODULE
       /              \
SQLDelight worker   Room 3 worker
sqlite.worker.js    room3.worker.js
       \              /
 SQLite3MultipleCiphers + multipleciphers-opfs
              |
         OPFS notes.db
```

`CORE_DATA_DB_MODULE` in `gradle.properties` selects one implementation. Only that module is
included by `settings.gradle.kts`, preventing duplicate data-layer classes and resources.

## Shared storage contract

- database filename: `notes.db`;
- VFS: `multipleciphers-opfs` when available, regular `opfs` as a fallback;
- SQLite schema version: `PRAGMA user_version = 2`;
- dates: epoch milliseconds in INTEGER columns;
- encryption: SQLCipher-compatible key and rekey pragmas supplied by
  SQLite3MultipleCiphers.

Because OPFS is origin-private, databases from different schemes, hosts, or ports are separate.
Import/export code must use `SafeRepo.dbPath`, not a hard-coded filename.

## SQLDelight worker

`sqlite.worker.js` implements SQLDelight's worker protocol (`exec`, transactions, and close).
The worker opens `file:notes.db?vfs=multipleciphers-opfs`. `WebSafeRepo` applies the key before
schema creation or any readability probe.

The resource exists in the app and Web UI-test source sets because each executable packages its
own worker assets. Keep these copies synchronized when the protocol or filename changes.

## Room 3 worker

`core/data/db-room/src/wasmJsMain/resources/room3.worker.js` implements the AndroidX SQLite Web
worker protocol used by `WebWorkerSQLiteDriver`:

- `open` maps Room's logical connection to `notes.db`;
- `prepare`, `step`, and `close` manage statements and database handles;
- bindings and column types use the official Room/SQLite wire format;
- the encryption key is URL-encoded in the logical database name and applied before Room reads
  the schema.

Room uses its in-memory builder only to request a single-connection pool. A small driver wrapper
replaces the logical `:memory:` name with the persistent OPFS filename.

`WebWorkerSQLiteDriver` does not bundle a worker implementation. AndroidX provides an
unencrypted SQLite WASM example, while this project needs SQLite3MultipleCiphers for compatible
SQLCipher encryption; therefore the project keeps its worker and implements the official protocol.

Each `WebDatabaseHolder` owns one worker. Closing the holder terminates the worker and waits one
browser task before another holder is created. This releases the exclusive OPFS handle and avoids
blocking the UI thread in synchronous `RoomDatabase.close()` while still preserving all writes
whose DAO calls have completed.

## Build resources

`configureWebSqlite3mcWasmResources()` stores the pinned SQLite3MultipleCiphers archive in
`${GRADLE_USER_HOME}/caches/notedelight/sqlite3mc/<version>/` and extracts it into the relevant
source-set resources. Once this archive has been downloaded, `clean` and offline builds reuse it
without contacting GitHub. A new machine or an empty cache still requires one online download.
Webpack and Karma must serve:

- `sqlite3.js`;
- `sqlite3.wasm`;
- `sqlite3-opfs-async-proxy.js`;
- the selected ORM worker.

When Room is selected, `app:web` adds the active database module's Wasm resources to the
executable, including `room3.worker.js`. A missing or failed worker is a startup error; only SQLite
`NOTADB` from the readability probe is treated as an encrypted database.

OPFS requires cross-origin isolation. Development and test servers set:

```text
Cross-Origin-Opener-Policy: same-origin
Cross-Origin-Embedder-Policy: require-corp
```

The production host must provide equivalent headers (or the project's isolation service worker).

## Testing

Focused Room coverage:

```bash
./gradlew :core:data:db-room:wasmJsBrowserTest
```

SQLDelight and application Web coverage:

```bash
./gradlew :core:data:db-sqldelight:wasmJsBrowserTest
./gradlew :app:web:wasmJsBrowserTest
```

After switching `CORE_DATA_DB_MODULE`, always run the complete workflow in `AGENTS.md` so native,
JVM, Web, and application wiring are checked together.

## Troubleshooting

- `SharedArrayBuffer` or OPFS errors: verify the isolation headers.
- Missing worker/WASM 404: inspect the browser distribution and Karma proxy configuration.
- Database missing after a switch: confirm the origin and the exact `notes.db` filename.
- Wrong-password failures: the key must be applied before any schema query.
- `Pagesize cannot be changed`: the repositories normalize an unencrypted database to 4096-byte
  pages with `VACUUM` before its first SQLCipher `rekey`.
- Reopen hangs: verify the previous ORM worker was closed or terminated before opening OPFS again.
