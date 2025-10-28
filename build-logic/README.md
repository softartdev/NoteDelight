# Build Logic Module

## Overview

The `build-logic` module contains **Gradle convention plugins** and shared build configuration for the NoteDelight project. It centralizes common build logic to reduce duplication across module build scripts and enforce consistent configuration.

## Purpose

- Define reusable Gradle convention plugins
- Centralize common build configuration
- Enforce consistent build settings across modules
- Simplify module `build.gradle.kts` files
- Provide custom build tasks and extensions

## Architecture

```
build-logic (Gradle Convention Plugins)
    ├── convention/
    │   ├── src/
    │   │   └── main/
    │   │       └── kotlin/
    │   │           ├── GradleConventionPlugin.kt
    │   │           └── # Other convention plugins
    │   ├── build.gradle.kts
    │   └── settings.gradle.kts (not present, uses root settings)
    ├── gradle.properties
    └── settings.gradle.kts
```

## Key Components

### GradleConventionPlugin

Main convention plugin applied to Gradle builds:

```kotlin
import org.gradle.api.Plugin
import org.gradle.api.Project

class GradleConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            // Configure Gradle-specific settings
            configureGradle()
        }
    }
    
    private fun Project.configureGradle() {
        // Gradle wrapper validation
        // Build cache configuration
        // Common repository configuration
        // etc.
    }
}
```

### Plugin Registration

Plugins are registered in `build.gradle.kts`:

```kotlin
gradlePlugin {
    plugins {
        register("gradleConvention") {
            id = "com.softartdev.notedelight.buildlogic.convention"
            implementationClass = "GradleConventionPlugin"
        }
    }
}
```

## Usage in Modules

Apply convention plugins in module `build.gradle.kts`:

```kotlin
plugins {
    alias(libs.plugins.gradle.convention)
    // Other plugins...
}
```

This automatically applies:
- Common Gradle configuration
- Repository setup
- Build cache settings
- Validation rules

## Convention Plugin Benefits

### Before (Duplicated Configuration)

```kotlin
// In each module's build.gradle.kts
repositories {
    google()
    mavenCentral()
    // ... repeated everywhere
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17"
        // ... repeated everywhere
    }
}
```

### After (Using Convention Plugin)

```kotlin
// In module's build.gradle.kts
plugins {
    alias(libs.plugins.gradle.convention)
}

// That's it! Common config applied automatically
```

## Build Configuration

### Gradle Properties

`build-logic/gradle.properties`:

```properties
# Convention plugin configuration
kotlin.code.style=official
org.gradle.jvmargs=-Xmx2048m
org.gradle.caching=true
```

### Dependency Versions

Convention plugins can access version catalog from root:

```kotlin
val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
val kotlinVersion = libs.findVersion("kotlin").get()
```

## Common Configurations

### Repository Configuration

```kotlin
fun Project.configureRepositories() {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}
```

### Kotlin Configuration

```kotlin
fun Project.configureKotlin() {
    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = libs.versions.jdk.get()
            freeCompilerArgs += listOf(
                "-Xopt-in=kotlin.RequiresOptIn",
                "-Xcontext-receivers"
            )
        }
    }
}
```

### Testing Configuration

```kotlin
fun Project.configureTests() {
    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }
}
```

## Build Cache

Enable build cache for faster builds:

```kotlin
fun Project.configureBuildCache() {
    gradle.sharedServices.registerIfAbsent("buildCache", BuildCacheService::class.java) {
        // Build cache configuration
    }
}
```

## Custom Tasks

Define reusable tasks:

```kotlin
abstract class ValidateCodeTask : DefaultTask() {
    @TaskAction
    fun validate() {
        // Custom validation logic
    }
}

// Register task
tasks.register<ValidateCodeTask>("validateCode")
```

## Plugin Dependencies

```kotlin
dependencies {
    compileOnly(kotlin("gradle-plugin", version = libs.versions.kotlin.get()))
}
```

This allows convention plugins to configure Kotlin plugin.

## Building

### Build Convention Plugins

Convention plugins are built automatically when building the project:

```bash
# Build all (includes build-logic)
./gradlew build

# Build only build-logic
./gradlew :build-logic:convention:build
```

### Testing Convention Plugins

```bash
./gradlew :build-logic:convention:test
```

## Validation

### Plugin Validation

Gradle validates plugins at build time:

```kotlin
tasks {
    validatePlugins {
        enableStricterValidation = true
        failOnWarning = true
    }
}
```

This ensures:
- Correct plugin implementation
- Proper annotations
- Valid plugin IDs
- No warnings

## Advanced Usage

### Conditional Configuration

```kotlin
fun Project.configureForPlatform() {
    when {
        pluginManager.hasPlugin("com.android.application") -> {
            // Android-specific config
        }
        pluginManager.hasPlugin("org.jetbrains.kotlin.jvm") -> {
            // JVM-specific config
        }
    }
}
```

### Extension Functions

```kotlin
// Define project extension
fun Project.customProperty(): String {
    return findProperty("customProp") as? String ?: "default"
}

// Use in build scripts
val myProp = project.customProperty()
```

### Shared Extensions

```kotlin
interface NoteDelightExtension {
    val enableFeatureX: Property<Boolean>
}

// Register extension
project.extensions.create<NoteDelightExtension>("noteDelight")

// Use in build script
noteDelight {
    enableFeatureX.set(true)
}
```

## Best Practices

### Convention Plugin Design

1. **Single responsibility**: One plugin per concern
2. **Composable**: Plugins should work together
3. **Configurable**: Allow overrides when needed
4. **Documented**: Document plugin behavior
5. **Tested**: Write tests for plugins

### Build Script Simplification

```kotlin
// Good: Concise with convention plugin
plugins {
    alias(libs.plugins.gradle.convention)
    alias(libs.plugins.kotlin.multiplatform)
}

// Bad: Lots of repeated configuration
plugins {
    kotlin("multiplatform")
}
repositories { /* ... */ }
tasks.withType<KotlinCompile> { /* ... */ }
// etc.
```

## AI Agent Guidelines

When working with this module:

1. **DRY principle**: Extract repeated configuration into conventions
2. **Backwards compatibility**: Don't break existing modules
3. **Testing**: Test convention plugins thoroughly
4. **Documentation**: Document plugin behavior clearly
5. **Versioning**: Be careful with breaking changes
6. **Performance**: Keep plugins lightweight
7. **Validation**: Enable strict validation
8. **Composition**: Prefer small, composable plugins
9. **Type safety**: Use Kotlin DSL features
10. **Error messages**: Provide helpful error messages

## Troubleshooting

### Plugin Not Found

1. **Check plugin ID**: Verify ID in `libs.versions.toml`
2. **Rebuild**: `./gradlew clean build`
3. **Check classpath**: Ensure plugin is on buildscript classpath

### Configuration Issues

1. **Check order**: Plugin order matters
2. **Check conflicts**: Look for conflicting configuration
3. **Debug**: Use `--stacktrace` and `--info` flags

### Build Performance

1. **Profile builds**: `./gradlew build --profile`
2. **Enable caching**: Configure build cache
3. **Parallel execution**: Use `--parallel` flag

## Gradle Configuration Options

### gradle.properties

```properties
# Gradle daemon
org.gradle.daemon=true
org.gradle.jvmargs=-Xmx4g -XX:+HeapDumpOnOutOfMemoryError

# Build cache
org.gradle.caching=true

# Parallel builds
org.gradle.parallel=true

# Configuration cache
org.gradle.configuration-cache=true
```

## Future Enhancements

Potential convention plugins to add:

1. **Android Convention**: Common Android configuration
2. **Compose Convention**: Compose-specific settings
3. **Multiplatform Convention**: KMP defaults
4. **Publishing Convention**: Maven publishing setup
5. **Documentation Convention**: Dokka configuration
6. **Code Quality Convention**: Detekt, ktlint integration

## Related Modules

- **Used by**: All modules (via `libs.plugins.gradle.convention`)
- **Scope**: Build-time only

## Resources

- [Gradle Plugin Development](https://docs.gradle.org/current/userguide/custom_plugins.html)
- [Gradle Best Practices](https://docs.gradle.org/current/userguide/authoring_maintainable_build_scripts.html)
- [Kotlin DSL](https://docs.gradle.org/current/userguide/kotlin_dsl.html)
- [Convention Plugins](https://docs.gradle.org/current/samples/sample_convention_plugins.html)

