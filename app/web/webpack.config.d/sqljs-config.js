// SQLDelight web worker configuration for SQL.js
// Based on: https://sqldelight.github.io/sqldelight/2.1.0/js_sqlite/sqljs_worker/

// Configure resolve fallbacks for Node.js modules
config.resolve = {
    ...config.resolve,
    fallback: {
        ...config.resolve?.fallback,
        fs: false,
        path: false,
        crypto: false,
    }
};

// Add CopyWebpackPlugin to copy SQL.js WebAssembly binary
const CopyWebpackPlugin = require('copy-webpack-plugin');
config.plugins.push(
    new CopyWebpackPlugin({
        patterns: [
            {
                from: '../../node_modules/sql.js/dist/sql-wasm.wasm',
                to: 'sql-wasm.wasm'
            }
        ]
    })
);
