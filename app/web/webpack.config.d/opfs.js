// Configure HTTP headers for OPFS (Origin-Private FileSystem) support
// OPFS requires specific Cross-Origin headers to function properly

config.devServer = {
  ...config.devServer,
  headers: {
    ...config.devServer?.headers,
    "Cross-Origin-Embedder-Policy": "require-corp",
    "Cross-Origin-Opener-Policy": "same-origin",
  }
}
