# Web Development Server Workflow

Quick reference guide for running and testing the web application with AI agents and built-in browsers.

## Overview

The webpack-dev-server automatically opens a browser by default. For AI agent workflows, we need to:
1. Disable automatic browser opening
2. Use LAN URL (not localhost) for built-in browser access
3. Properly stop the server when done

**Note**: The web app now includes OPFS (Origin-Private FileSystem) support for persistent database storage. This requires specific Cross-Origin headers which are automatically configured.

## Configuration

### Disable Automatic Browser Opening

Edit `app/web/build.gradle.kts`:

```kotlin
devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
    open = false  // Disable automatic browser opening
    // ... other config
}
```

## Workflow

### 1. Start the Development Server

```bash
# From project root
./gradlew wasmJsBrowserDevelopmentRun
```

### 2. Find the Server URL

Look for this output in the terminal:

```
<i> [webpack-dev-server] Project is running at:
<i> [webpack-dev-server] Loopback: http://localhost:8081/, http://[::1]:8081/
<i> [webpack-dev-server] On Your Network (IPv4): http://192.168.0.103:8081/
<i> [webpack-dev-server] On Your Network (IPv6): http://[fd8c:...]:8081/
```

**Important:** Use the **LAN URL (IPv4)** for built-in browser tools, NOT localhost.

### 3. Access with Built-in Browser

**✅ Works:**
- `http://192.168.0.103:8081/` (LAN IPv4 URL from server output)
- `http://127.0.0.1:8081/` (might work, but LAN URL is more reliable)

**❌ Doesn't Work:**
- `http://localhost:8081/` (connection refused in Playwright/isolated browser contexts)

### 4. Stop the Server

**Always stop the server when done!**

```bash
# Kill Gradle/webpack processes
ps aux | grep -E "(gradle.*wasmJs|webpack)" | grep -v grep | awk '{print $2}' | xargs kill

# Or free the ports directly
lsof -ti:8080,8081 | xargs kill

# Or use Ctrl+C if running in foreground
```

## AI Agent Checklist

When working with the web app:

- [ ] Start server: `./gradlew wasmJsBrowserDevelopmentRun`
- [ ] Wait for compilation (look for "compiled successfully")
- [ ] Extract LAN URL from output (IPv4 network address)
- [ ] Navigate browser to LAN URL (not localhost)
- [ ] Test/verify application in browser
- [ ] **Stop server before ending session** (kill processes/free ports)
