Like "app.cash.sqldelight:androidx-paging3-extensions", but with async-driver support for projects that enable SQLDelight `generateAsync = true`.

SQLDelight `2.3.2` already publishes `androidx-paging3-extensions` against `androidx.paging`, but the upstream implementation still calls synchronous `executeAsList`/`executeAsOne`. This local fork keeps the same API surface and adds support for SQLDelight's async driver. When your database is generated with `generateAsync = true`, this module will use the `app.cash.sqldelight:async-extensions` suspend helpers (e.g. `awaitAsList`, `awaitAsOne`) when a `SuspendingTransacter` is used, avoiding IllegalStateException from mixing sync calls with an async driver.

Features
- Matches the published SQLDelight paging API that already integrates with `androidx.paging`.
- Uses async-extensions (suspending query helpers) automatically when a `SuspendingTransacter` is provided.
- Falls back to synchronous calls for a regular `Transacter` for backward compatibility.

Requirements
- SQLDelight configured with `generateAsync = true` if you use an async (suspending) driver.
- Add `app.cash.sqldelight:async-extensions` on platforms where you need suspend helpers.
- Add `androidx.paging` dependencies appropriate for your target (paging-runtime, paging-compose, etc.).

Usage (example)

In your DAO/Repository you can provide a `PagingSource` using `QueryPagingSource`:

```kotlin
val pagingSource: PagingSource<Int, YourRowType> = QueryPagingSource(
  countQuery = yourQueries.count(),
  transacter = yourQueries, // can be a SuspendingTransacter
  context = Dispatchers.IO,
  queryProvider = { limit, offset -> yourQueries.paged(limit, offset) }
)
```

Notes
- If you configure SQLDelight with `generateAsync = true`, make sure you include and initialize the async driver and `async-extensions` dependency on the target platforms that require it. This module automatically uses suspend helpers when appropriate, but mixing synchronous execute* calls with an async driver will still throw the usual SQLDelight IllegalStateException.
- This module is intended as a simple local replacement of the upstream paging integration until upstream also supports async query execution in the paging source.

License: same as original upstream (Apache 2.0).
