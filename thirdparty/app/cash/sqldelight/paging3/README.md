Like "app.cash.sqldelight:androidx-paging3-extensions", but using "androidx.paging" instead of "app.cash.paging".

This local fork adapts the SQLDelight paging integration to use androidx.paging and also includes support for SQLDelight's async driver. When your database is generated with `generateAsync = true`, this module will use the `app.cash.sqldelight:async-extensions` suspend helpers (e.g. `awaitAsList`, `awaitAsOne`) when a `SuspendingTransacter` is used, avoiding IllegalStateException from mixing sync calls with an async driver.

Features
- Integrates SQLDelight Query-based paging with `androidx.paging`.
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
- This module is intended as a simple local replacement of the upstream paging integration; feel free to adapt it for your project's packaging or publish it if useful.

License: same as original upstream (Apache 2.0).
