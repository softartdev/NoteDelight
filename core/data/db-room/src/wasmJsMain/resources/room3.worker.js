let sqlite3 = null;
let initializationError = null;
let multipleCiphersVfsReady = false;

const databases = new Map();
const databaseCache = new Map();
const statements = new Map();
let nextDatabaseId = 0;
let nextStatementId = 0;

async function initializeSqlite() {
  if (sqlite3) return sqlite3;
  importScripts("sqlite3.js");
  sqlite3 = await sqlite3InitModule();
  return sqlite3;
}

function parseDatabaseName(value) {
  const keyMarker = "?key=";
  const markerIndex = value.indexOf(keyMarker);
  if (markerIndex < 0) return { fileName: value, key: null };
  return {
    fileName: value.substring(0, markerIndex),
    key: decodeURIComponent(value.substring(markerIndex + keyMarker.length)),
  };
}

function configureKey(database, key) {
  if (!key) return;
  const escapedKey = key.replaceAll("'", "''");
  database.exec("PRAGMA cipher = 'sqlcipher'");
  database.exec("PRAGMA legacy = 4");
  database.exec(`PRAGMA key = '${escapedKey}'`);
}

function createDatabase(fileName) {
  try {
    if (!multipleCiphersVfsReady) {
      multipleCiphersVfsReady = sqlite3.capi.sqlite3mc_vfs_create("opfs", 0) === 0;
    }
    if (multipleCiphersVfsReady) {
      return new sqlite3.oo1.DB(`file:${fileName}?vfs=multipleciphers-opfs`, "c");
    }
  } catch (_) {
    multipleCiphersVfsReady = false;
  }

  try {
    return new sqlite3.oo1.OpfsDb(fileName);
  } catch (_) {
    return new sqlite3.oo1.DB(":memory:", "c");
  }
}

function openRequest(id, requestData) {
  try {
    const { fileName, key } = parseDatabaseName(requestData.fileName);
    const databaseId = nextDatabaseId++;
    let database = databaseCache.get(fileName);
    if (!database) {
      database = createDatabase(fileName);
      configureKey(database, key);
      databaseCache.set(fileName, database);
    }
    databases.set(databaseId, database);
    postMessage({ id, data: { databaseId } });
  } catch (error) {
    postMessage({ id, error: error.message });
  }
}

function prepareRequest(id, requestData) {
  try {
    const database = databases.get(requestData.databaseId);
    if (!database) throw new Error(`Invalid database ID: ${requestData.databaseId}`);

    const statementId = nextStatementId++;
    const statement = database.prepare(requestData.sql);
    statements.set(statementId, { statement, databaseId: requestData.databaseId });

    const columnNames = [];
    for (let index = 0; index < statement.columnCount; index++) {
      columnNames.push(sqlite3.capi.sqlite3_column_name(statement, index));
    }
    postMessage({
      id,
      data: {
        statementId,
        parameterCount: sqlite3.capi.sqlite3_bind_parameter_count(statement),
        columnNames,
      },
    });
  } catch (error) {
    postMessage({ id, error: error.message });
  }
}

function stepRequest(id, requestData) {
  try {
    const statementRecord = statements.get(requestData.statementId);
    if (!statementRecord) throw new Error(`Invalid statement ID: ${requestData.statementId}`);
    const statement = statementRecord.statement;

    statement.reset();
    statement.clearBindings();
    for (let index = 0; index < requestData.bindings.length; index++) {
      statement.bind(index + 1, requestData.bindings[index]);
    }

    const rows = [];
    const columnTypes = [];
    while (statement.step()) {
      if (columnTypes.length === 0) {
        for (let index = 0; index < statement.columnCount; index++) {
          columnTypes.push(sqlite3.capi.sqlite3_column_type(statement, index));
        }
      }
      rows.push(statement.get([]));
    }
    postMessage({ id, data: { rows, columnTypes } });
  } catch (error) {
    postMessage({ id, error: error.message });
  }
}

function closeRequest(id, requestData) {
  try {
    if (requestData.statementId !== undefined && requestData.statementId !== null) {
      const statementRecord = statements.get(requestData.statementId);
      if (!statementRecord) throw new Error(`Invalid statement ID: ${requestData.statementId}`);
      statementRecord.statement.finalize();
      statements.delete(requestData.statementId);
    }
    if (requestData.databaseId !== undefined && requestData.databaseId !== null) {
      const database = databases.get(requestData.databaseId);
      if (!database) throw new Error(`Invalid database ID: ${requestData.databaseId}`);
      for (const [statementId, statementRecord] of statements) {
        if (statementRecord.databaseId === requestData.databaseId) {
          statementRecord.statement.finalize();
          statements.delete(statementId);
        }
      }
      try {
        database.exec("ROLLBACK");
      } catch (_) {
        // The connection is normally already in autocommit mode.
      }
      database.exec("DROP TRIGGER IF EXISTS `room_table_modification_trigger_note_INSERT`");
      database.exec("DROP TRIGGER IF EXISTS `room_table_modification_trigger_note_UPDATE`");
      database.exec("DROP TRIGGER IF EXISTS `room_table_modification_trigger_note_DELETE`");
      database.exec("DROP TABLE IF EXISTS temp.room_table_modification_log");
      databases.delete(requestData.databaseId);
    }
  } catch (error) {
    postMessage({ id, error: error.message });
  }
}

function pingRequest(id) {
  postMessage({ id, data: {} });
}

const commandMap = {
  open: openRequest,
  prepare: prepareRequest,
  step: stepRequest,
  close: closeRequest,
  ping: pingRequest,
};

function handleMessage(event) {
  const request = event.data;
  if (!request || !request.data || !request.data.cmd) {
    postMessage({ id: request && request.id, error: "Invalid Room SQLite worker request" });
    return;
  }
  const handler = commandMap[request.data.cmd];
  if (!handler) {
    postMessage({ id: request.id, error: `Unknown command: ${request.data.cmd}` });
    return;
  }
  handler(request.id, request.data);
}

const queuedMessages = [];
onmessage = (event) => {
  if (initializationError) {
    postMessage({
      id: event.data && event.data.id,
      error: initializationError.message || String(initializationError),
    });
  } else if (!sqlite3) {
    queuedMessages.push(event);
  } else {
    handleMessage(event);
  }
};

initializeSqlite()
  .then(() => {
    while (queuedMessages.length > 0) handleMessage(queuedMessages.shift());
  })
  .catch((error) => {
    initializationError = error;
    while (queuedMessages.length > 0) {
      const event = queuedMessages.shift();
      postMessage({
        id: event.data && event.data.id,
        error: error.message || String(error),
      });
    }
  });
