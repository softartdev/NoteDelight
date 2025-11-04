importScripts("sqlite3.js");

let db = null;

async function createDatabase() {
  const sqlite3 = await sqlite3InitModule();

  // This is the key part for OPFS support
  // It instructs SQLite to use the OPFS VFS.
  db = new sqlite3.oo1.DB("file:database.db?vfs=opfs", "c");
}

function handleMessage() {
  const data = this.data;

  switch (data && data.action) {
    case "exec":
      if (!data["sql"]) {
        throw new Error("exec: Missing query string");
      }

      return postMessage({
        id: data.id,
        results: { values: db.exec({ sql: data.sql, bind: data.params, returnValue: "resultRows" }) },
      })
    case "begin_transaction":
      return postMessage({
        id: data.id,
        results: db.exec("BEGIN TRANSACTION;"),
      })
    case "end_transaction":
      return postMessage({
        id: data.id,
        results: db.exec("END TRANSACTION;"),
      })
    case "rollback_transaction":
      return postMessage({
        id: data.id,
        results: db.exec("ROLLBACK TRANSACTION;"),
      })
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
