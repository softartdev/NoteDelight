# Opening an Exported Encrypted Database in DB Browser for SQLite

NoteDelight encrypts its database using [SQLite3 Multiple Ciphers](https://utelle.github.io/SQLite3MultipleCiphers/) (sqlite3mc) in **SQLCipher v4 compatibility mode**. Exported `.db` files can be inspected with any tool that supports SQLCipher 4.

## Prerequisites

| Tool | Minimum version | Notes |
|------|----------------|-------|
| [DB Browser for SQLite](https://sqlitebrowser.org/dl/) | 3.12.x | Must be a build **with SQLCipher support**. The default Linux package (`apt install sqlitebrowser`) usually ships without it — use the official AppImage or macOS / Windows installer instead. |
| **or** `sqlcipher` CLI | 4.x | `apt install sqlcipher` (Linux) / `brew install sqlcipher` (macOS) |

> **Tip:** Run `sqlitebrowser --version` — the output should mention **SQLCipher**. If it only shows a plain SQLite version the build does not support encrypted databases.

## Export the database from NoteDelight

1. Open the desktop app and sign in with your password.
2. Go to **Settings → Backup → Export database**.
3. Choose a destination path and click **Save**.

The exported file is an exact copy of the encrypted database.

## Open in DB Browser for SQLite

1. Launch **DB Browser for SQLite** (the SQLCipher-enabled build).
2. **File → Open Database…** and select the exported `.db` file.
3. A **"SQLCipher encryption"** dialog appears.
4. Configure the dialog:

| Field | Value |
|-------|-------|
| **Password** | Your NoteDelight password |
| **Encryption settings** | **SQLCipher 4 defaults** |
| Page size | 4096 |
| KDF iterations | 256000 |
| HMAC algorithm | SHA512 |
| KDF algorithm | SHA512 |
| Plaintext Header Size | 0 |

5. Click **OK**. The database structure and data should now be visible.

## Open with the `sqlcipher` CLI

```bash
sqlcipher /path/to/notes.db
sqlite> PRAGMA key = 'your_password';
sqlite> .tables
note  sqlite_sequence
sqlite> SELECT * FROM note;
```

## Troubleshooting

| Symptom | Fix |
|---------|-----|
| "file is not a database" after entering password | Verify you selected **SQLCipher 4 defaults** (not SQLCipher 3 or Custom). |
| DB Browser doesn't show an encryption dialog | You are running a build without SQLCipher. Download the AppImage from the [official releases](https://github.com/sqlitebrowser/sqlitebrowser/releases). |
| `sqlcipher` CLI says "Error: file is not a database" | Make sure `PRAGMA key` is the **first** statement executed on the connection. |

## Platform differences

| Platform | Encryption library | `PRAGMA cipher_version` result |
|----------|-------------------|-------------------------------|
| Android | SQLCipher (Zetetic) | e.g. `4.6.1 community` |
| iOS | SQLCipher (CocoaPod) | e.g. `4.5.5 community` |
| Desktop (JVM) | SQLite3 Multiple Ciphers | e.g. `SQLite3 Multiple Ciphers 2.2.7` |
| Web | sql.js (no encryption) | N/A |

All platforms that support encryption produce databases compatible with the **SQLCipher 4** format.
