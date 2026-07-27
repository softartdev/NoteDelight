const path = require("path");
const dist = path.resolve("../../node_modules/sql.js/dist/")
const wasm = path.join(dist, "sql-wasm.wasm")

config.files.push({
    pattern: wasm,
    served: true,
    watched: false,
    included: false,
    nocache: false,
});

config.proxies["/sql-wasm.wasm"] = path.join("/absolute/", wasm)

const sqliteAssets = [
  'sqlite.worker.js',
  'sqlite3.js',
  'sqlite3.wasm',
  'sqlite3-opfs-async-proxy.js',
];

for (const asset of sqliteAssets) {
  const assetPath = path.resolve(config.basePath, 'kotlin', asset);
  config.files.push({
    pattern: assetPath,
    served: true,
    watched: false,
    included: false,
    nocache: false,
  });
  config.proxies[`/${asset}`] = `/absolute/${assetPath}`;
}

config.set({
  customHeaders: [{
    match: '.*',
    name: 'Cross-Origin-Opener-Policy',
    value: 'same-origin',
  }, {
    match: '.*',
    name: 'Cross-Origin-Embedder-Policy',
    value: 'require-corp',
  }]
});
