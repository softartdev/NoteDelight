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

// Add CopyWebpackPlugin to copy SQL.js WebAssembly binary, JavaScript file, and worker script
const CopyWebpackPlugin = require('copy-webpack-plugin');
const normalizeSqlJsWasmPaths = (text) => {
    if (typeof text !== 'string') {
        return text;
    }

    const locateFileArrowPattern = /locateFile:(\s*[A-Za-z_$][\w$]*\s*)=>\s*(["'])(?:\.{0,2}\/|\/)+sql-wasm\.wasm\2/g;
    const relativePattern = /(["'])(?:\.{0,2}\/|\/)+sql-wasm\.wasm\1/g;
    const barePattern = /(["'])sql-wasm\.wasm\1/g;

    let output = text.replace(locateFileArrowPattern, (_match, identifier, quote) => `locateFile:${identifier}=>${quote}./sql-wasm.wasm${quote}`);
    output = output.replace(relativePattern, (_match, quote) => `${quote}./sql-wasm.wasm${quote}`);
    output = output.replace(barePattern, (_match, quote) => `${quote}./sql-wasm.wasm${quote}`);

    return output;
};

config.plugins.push(
    new CopyWebpackPlugin({
        patterns: [
            // Use official SQLite WASM files downloaded by Gradle
            {
                from: '../../../build/sqlite/sqlite3.wasm',
                to: 'sqlite3.wasm',
                noErrorOnMissing: true
            },
            {
                from: '../../../build/sqlite/sqlite3.js',
                to: 'sqlite3.js',
                noErrorOnMissing: true
            },
            // Copy OPFS async proxy for OPFS support
            {
                from: '../../../build/sqlite/sqlite3-opfs-async-proxy.js',
                to: 'sqlite3-opfs-async-proxy.js',
                noErrorOnMissing: true
            },
            // Custom OPFS worker script is handled by Gradle resource processing
            // Keep legacy SQL.js files for backward compatibility
            {
                from: '../../node_modules/sql.js/dist/sql-wasm.wasm',
                to: 'sql-wasm.wasm',
                noErrorOnMissing: true
            },
            {
                from: '../../node_modules/sql.js/dist/sql-wasm.js',
                to: 'sql-wasm.js',
                noErrorOnMissing: true
            },
            {
                from: '../../node_modules/@cashapp/sqldelight-sqljs-worker/sqljs.worker.js',
                to: 'sqljs.worker.js',
                noErrorOnMissing: true
            }
        ]
    })
);

// Add webpack plugin to modify SQL.js WASM loading at the source level
class SQLJsWasmFixPlugin {
    apply(compiler) {
        compiler.hooks.emit.tapAsync('SQLJsWasmFixPlugin', (compilation, callback) => {
            console.log('🔧 SQLJsWasmFixPlugin running, checking', Object.keys(compilation.assets).length, 'assets');
            // Find and modify SQL.js files to fix WASM path
            Object.keys(compilation.assets).forEach(filename => {
                if (filename.endsWith('.js')) {
                    const asset = compilation.assets[filename];
                    let source = asset.source();
                    if (typeof source !== 'string') {
                        source = source?.toString();
                    }
                    
                    // Replace WASM path references in SQL.js
                    if (typeof source === 'string' && source.includes('sql-wasm.wasm')) {
                        console.log('🔧 Fixing WASM path in', filename);
                        const updatedSource = normalizeSqlJsWasmPaths(source);
                        
                        if (updatedSource !== source) {
                            compilation.assets[filename] = {
                                source: () => updatedSource,
                                size: () => updatedSource.length
                            };
                        }
                    }
                }
            });
            callback();
        });
        
        // Also run after the compilation is done to process distribution files
        compiler.hooks.afterEmit.tapAsync('SQLJsWasmFixPlugin', (compilation, callback) => {
            console.log('🔧 SQLJsWasmFixPlugin afterEmit hook running');
            const fs = require('fs');
            const path = require('path');

            // Check if outputPath is defined
            if (!compilation.outputPath) {
                console.log('🔧 No outputPath defined, skipping distribution file processing');
                callback();
                return;
            }

            // Process distribution files if they exist
            const distPath = path.join(compilation.outputPath, '..', 'dist', 'wasmJs', 'productionExecutable');
            if (fs.existsSync(distPath)) {
                console.log('🔧 Processing distribution files in', distPath);
                const files = fs.readdirSync(distPath);
                files.forEach(file => {
                    if (file.endsWith('.js')) {
                        const filePath = path.join(distPath, file);
                        let content = fs.readFileSync(filePath, 'utf8');

                        if (content.includes('sql-wasm.wasm')) {
                            console.log('🔧 Fixing WASM path in distribution file', file);

                            content = normalizeSqlJsWasmPaths(content);

                            fs.writeFileSync(filePath, content);
                            console.log('🔧 Fixed distribution file', file);
                        }
                    }
                });
            } else {
                console.log('🔧 Distribution path does not exist:', distPath);
            }

            // Also process the main output directory files
            if (fs.existsSync(compilation.outputPath)) {
                console.log('🔧 Processing main output files in', compilation.outputPath);
                const files = fs.readdirSync(compilation.outputPath);
                files.forEach(file => {
                    if (file.endsWith('.js')) {
                        const filePath = path.join(compilation.outputPath, file);
                        let content = fs.readFileSync(filePath, 'utf8');

                        if (content.includes('sql-wasm.wasm')) {
                            console.log('🔧 Fixing WASM path in main output file', file);

                            content = normalizeSqlJsWasmPaths(content);

                            fs.writeFileSync(filePath, content);
                            console.log('🔧 Fixed main output file', file);
                        }
                    }
                });
            }

            callback();
        });
    }
}

config.plugins.push(new SQLJsWasmFixPlugin());