// Karma configuration for Compose WASM UI tests
//
// Key issues this config solves:
// 1. WASM file serving: webpack bundles JS but not .wasm files. The bundled code
//    tries to fetch .wasm relative to import.meta.url (a webpack temp path).
//    We add a middleware to redirect .wasm requests to the correct location.
// 2. Timeouts: WasmGC module (~30 MB) compilation blocks the main thread during init.
// 3. Source-map performance: skipping source-map-loader for large WASM glue files.

const path = require('path');
const fs = require('fs');

function findRepoRoot(startDir) {
    let current = startDir;
    while (current !== path.dirname(current)) {
        if (
            fs.existsSync(path.join(current, 'settings.gradle.kts')) ||
            fs.existsSync(path.join(current, 'settings.gradle'))
        ) {
            return current;
        }
        current = path.dirname(current);
    }
    return startDir;
}

const repoRoot = findRepoRoot(process.cwd());
const composeResourcesAbsolutePath = path.resolve(
    repoRoot,
    'app',
    'web',
    'build',
    'processedResources',
    'wasmJs',
    'main',
    'composeResources'
).replace(/\\/g, '/');

// Middleware to serve .wasm files from the correct location
// Webpack sets import.meta.url to a temp directory, so fetch('x.wasm') goes to the
// wrong path. This middleware intercepts .wasm requests and serves them from kotlin/.
function wasmFileMiddleware(config) {
    const basePath = config.basePath;
    const composeResourcesRoot = path.resolve(
        repoRoot,
        'app',
        'web',
        'build',
        'processedResources',
        'wasmJs',
        'main'
    );
    return function(req, res, next) {
        const cleanUrl = req.url.split('?')[0];

        if (cleanUrl.includes('/composeResources/')) {
            const composeIndex = cleanUrl.indexOf('/composeResources/');
            const relativePath = cleanUrl.substring(composeIndex + 1);
            const resourcePath = path.join(composeResourcesRoot, relativePath);
            if (fs.existsSync(resourcePath)) {
                res.setHeader('Content-Type', 'application/octet-stream');
                fs.createReadStream(resourcePath).pipe(res);
                return;
            }
        }
        if (cleanUrl.endsWith('.wasm') && !cleanUrl.endsWith('sql-wasm.wasm')) {
            const filename = path.basename(cleanUrl);
            const wasmPath = path.join(basePath, 'kotlin', filename);
            if (fs.existsSync(wasmPath)) {
                res.setHeader('Content-Type', 'application/wasm');
                res.setHeader('Cross-Origin-Embedder-Policy', 'require-corp');
                res.setHeader('Cross-Origin-Opener-Policy', 'same-origin');
                fs.createReadStream(wasmPath).pipe(res);
                return;
            }
        }
        next();
    };
}

// Register middleware as an inline Karma plugin
config.plugins = config.plugins || [];
config.plugins.push({
    'middleware:wasmFileServer': ['factory', function() {
        return wasmFileMiddleware(config);
    }]
});

config.set({
    beforeMiddleware: ['wasmFileServer'],
    middleware: ['wasmFileServer'],
    proxies: Object.assign({}, config.proxies, {
        '/composeResources/': `/absolute/${composeResourcesAbsolutePath}/`
    }),

    // WasmGC compilation blocks the main thread for ~15-18 min on the first run.
    // pingTimeout must exceed compilation time so the socket stays alive.
    pingTimeout: 1200000, // 20 min
    browserSocketTimeout: 1200000, // 20 min
    browserNoActivityTimeout: 1500000, // 25 min
    browserDisconnectTimeout: 60000, // 1 min
    browserDisconnectTolerance: 1,
    captureTimeout: 60000, // 1 min
    processKillTimeout: 5000, // 5 sec

    client: {
        mocha: {
            timeout: 60000 // 1 min
        }
    },

    customLaunchers: {
        ChromeHeadlessForWasm: {
            base: 'ChromeHeadless',
            flags: [
                '--no-sandbox',
                '--enable-features=SharedArrayBuffer'
            ]
        }
    },
    browsers: ['ChromeHeadlessForWasm']
});

// Speed up webpack compilation by skipping source-map-loader for large WASM glue files.
if (config.webpack && config.webpack.module && config.webpack.module.rules) {
    config.webpack.module.rules.forEach(function(rule) {
        if (rule.use && rule.use.indexOf('source-map-loader') !== -1) {
            rule.exclude = [/skiko\.m?js$/, /\.uninstantiated\.m?js$/];
        }
    });
}
