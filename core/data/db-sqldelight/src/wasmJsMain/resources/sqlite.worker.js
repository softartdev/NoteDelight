let sqlite3Available = false;
let useSqlJs = false;

try {
  importScripts("sqlite3.js");
  sqlite3Available = typeof sqlite3InitModule === "function";
} catch (error) {
  sqlite3Available = false;
}

let db = null;

async function createDatabase() {
  if (sqlite3Available) {
    const sqlite3 = await sqlite3InitModule();

    // This is the key part for OPFS support
    // It instructs SQLite to use the OPFS VFS.
    try {
      db = new sqlite3.oo1.DB("file:database.db?vfs=opfs", "c");
      return;
    } catch (error) {
      try {
        // Fallback for environments without OPFS support (e.g., headless browsers).
        db = new sqlite3.oo1.DB(":memory:", "c");
        return;
      } catch (fallbackError) {
        sqlite3Available = false;
      }
    }
  }

  if (typeof initSqlJs !== "function") {
    importScripts("sql-wasm.js");
  }

  const SQL = await initSqlJs({
    locateFile: (file) => file,
  });
  db = new SQL.Database();
  useSqlJs = true;
}

function execSqlJs(sql, params) {
  const stmt = db.prepare(sql);
  if (params && params.length) {
    stmt.bind(params);
  }
  const rows = [];
  while (stmt.step()) {
    rows.push(stmt.get());
  }
  stmt.free();
  return rows;
}

function handleMessage() {
  const data = this.data;

  switch (data && data.action) {
    case "exec":
      if (!data["sql"]) {
        throw new Error("exec: Missing query string");
      }

      if (useSqlJs) {
        return postMessage({
          id: data.id,
          results: { values: execSqlJs(data.sql, data.params) },
        });
      }

      return postMessage({
        id: data.id,
        results: { values: db.exec({ sql: data.sql, bind: data.params, returnValue: "resultRows" }) },
      });
    case "begin_transaction":
      if (useSqlJs) {
        db.exec("BEGIN TRANSACTION;");
        return postMessage({ id: data.id, results: [] });
      }
      return postMessage({ id: data.id, results: db.exec("BEGIN TRANSACTION;") });
    case "end_transaction":
      if (useSqlJs) {
        db.exec("END TRANSACTION;");
        return postMessage({ id: data.id, results: [] });
      }
      return postMessage({ id: data.id, results: db.exec("END TRANSACTION;") });
    case "rollback_transaction":
      if (useSqlJs) {
        db.exec("ROLLBACK TRANSACTION;");
        return postMessage({ id: data.id, results: [] });
      }
      return postMessage({ id: data.id, results: db.exec("ROLLBACK TRANSACTION;") });
    default:
      throw new Error(`Unsupported action: ${data && data.action}`);
  }
}

function handleError(err) {
  return postMessage({
    id: this.data.id,
    error: err,
  });
}

if (typeof importScripts === "function") {
  db = null;
  const sqlModuleReady = createDatabase();
  self.onmessage = (event) => {
    return sqlModuleReady.then(handleMessage.bind(event))
    .catch(handleError.bind(event));
  }
}
