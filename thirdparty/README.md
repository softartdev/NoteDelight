# Third-Party Module

## Overview

The `thirdparty` directory contains **vendored** and **forked third-party libraries** that have been integrated into the project for customization, fixes, or features not available in official releases. These modules are maintained within the project repository.

## Purpose

- Include modified versions of third-party libraries
- Apply custom patches and fixes
- Support multiplatform features not in official releases
- Ensure specific versions and behavior
- Maintain compatibility with project requirements

## Structure

```
thirdparty/ (Vendored Third-Party Libraries)
    ├── androidx/
    │   └── paging/
    │       └── compose/           # Paging Compose for KMP
    └── app/
        └── cash/
            └── sqldelight/
                └── paging3/        # SQLDelight Paging3 extension
```

## Modules

### androidx/paging/compose

**Purpose**: Multiplatform Paging library for Compose

**Why vendored**: Official AndroidX Paging doesn't fully support Kotlin Multiplatform Compose yet. This module provides Compose Multiplatform support for pagination.

**Original source**: AndroidX Paging library (Google)

**Modifications**:
- Adapted for Kotlin Multiplatform
- Removed Android-specific dependencies
- Added support for iOS, Desktop, Web

**Platforms**:
- ✅ Android
- ✅ iOS
- ✅ Desktop JVM
- ✅ Web (Wasm)

**Dependencies**:
- `androidx.paging.common` - Core paging
- `compose.runtime` - Compose runtime
- `compose.foundation` - Foundation components

**Usage**:
```kotlin
dependencies {
    implementation(project(":thirdparty:androidx:paging:compose"))
}
```

**Key Components**:
- `LazyPagingItems.kt` - Paging state holder for Compose
- `collectAsLazyPagingItems()` - Extension to collect paging data
- Platform-specific implementations for each target

---

### app/cash/sqldelight/paging3

**Purpose**: SQLDelight integration with Paging3 library

**Why vendored**: Official SQLDelight Paging3 extension was discontinued. This fork maintains the functionality for the project.

**Original source**: [Cash App SQLDelight Paging3 Extension](https://github.com/cashapp/sqldelight/tree/master/extensions/androidx-paging3)

**Modifications**:
- Updated for latest SQLDelight version
- Fixed multiplatform compatibility issues
- Maintained Paging3 integration

**Platforms**:
- ✅ Android
- ✅ iOS
- ✅ Desktop JVM
- ✅ Web (Wasm)

**Dependencies**:
- `sqlDelight.runtime` - SQLDelight runtime
- `androidx.paging.common` - Core paging
- `kotlinx.coroutines` - Coroutines support

**Usage**:
```kotlin
dependencies {
    implementation(project(":thirdparty:app:cash:sqldelight:paging3"))
}
```

**Key Components**:
- `QueryPagingSource.kt` - PagingSource for SQLDelight queries
- `Pager extensions` - Extensions for creating pagers from queries

**Code Example**:
```kotlin
// In DAO implementation
override val pagingDataFlow: Flow<PagingData<Note>> = Pager(
    config = PagingConfig(pageSize = 20),
    pagingSourceFactory = {
        QueryPagingSource(
            countQuery = noteQueries.count(),
            transacter = noteQueries,
            context = Dispatchers.IO,
            queryProvider = { limit, offset ->
                noteQueries.selectAll(limit, offset)
            }
        )
    }
).flow
```

## Maintenance

### Updating Vendored Libraries

When updating vendored libraries:

1. **Check upstream**: Review changes in original repository
2. **Apply patches**: Re-apply custom modifications
3. **Test thoroughly**: Test on all platforms
4. **Document changes**: Update this README with changes
5. **Version tracking**: Note original version in module README

### Adding New Vendored Library

To add a new vendored library:

1. **Create directory**: `thirdparty/<owner>/<library>/`
2. **Add source code**: Copy relevant source files
3. **Add build.gradle.kts**: Configure as Gradle module
4. **Update settings.gradle.kts**: Include module
5. **Document**: Create README in module directory
6. **License**: Include original license file

### Example Build Configuration

```kotlin
// thirdparty/example/build.gradle.kts
plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
}

kotlin {
    jvm()
    android {
        compileSdk = libs.versions.compileSdk.get().toInt()
        minSdk = libs.versions.minSdk.get().toInt()
    }
    iosArm64()
    iosSimulatorArm64()
    
    sourceSets {
        commonMain.dependencies {
            // Dependencies
        }
    }
}
```

## Licensing

### License Compliance

All vendored libraries must:
- Include original license file
- Preserve copyright notices
- Comply with license terms (Apache 2.0, MIT, etc.)
- Document modifications

### License Files

Each vendored library should have:
- `LICENSE` or `LICENSE.txt` - Original license
- `README.md` - Documentation including source attribution
- `NOTICE` (if required) - Attribution notices

## Best Practices

### When to Vendor

✅ **Good reasons to vendor**:
- Needed features not in official release
- Critical bug fixes not yet merged upstream
- Library abandoned but still needed
- Heavy modifications required
- Multiplatform support not official

❌ **Avoid vendoring if**:
- Official library works fine
- Changes can be contributed upstream
- Stable version available via Maven
- Maintenance burden too high

### Maintaining Vendored Code

1. **Track upstream**: Monitor original repository
2. **Minimal changes**: Keep modifications minimal
3. **Document patches**: Document all changes
4. **Test thoroughly**: Test on all platforms
5. **Consider upstreaming**: Contribute back when possible

## Contributing Back

If you make improvements to vendored libraries:

1. **Extract generic changes**: Separate project-specific from generic
2. **Prepare patches**: Create clean patch series
3. **Open PR**: Submit to upstream repository
4. **Document**: Note contribution in README
5. **Replace**: Use official version once merged

## Alternatives to Vendoring

Before vendoring, consider:

1. **Fork on GitHub**: Fork library and depend on fork
2. **Gradle patch**: Use Gradle resolution strategies
3. **Shade/relocate**: Shade dependencies to avoid conflicts
4. **Contribute upstream**: Fix issues in original library

## AI Agent Guidelines

When working with vendored libraries:

1. **Respect licenses**: Ensure license compliance
2. **Document changes**: Document all modifications clearly
3. **Minimal changes**: Minimize custom modifications
4. **Test thoroughly**: Test on all supported platforms
5. **Track versions**: Keep track of original library versions
6. **Consider upstream**: Always consider contributing back
7. **Avoid unnecessary vendoring**: Use official versions when possible
8. **Update cautiously**: Be careful when updating vendored code
9. **Security**: Monitor for security issues in original libraries
10. **Dependencies**: Keep vendored library dependencies minimal

## Module READMEs

Each vendored library should have its own `README.md`:

```markdown
# Library Name

## Original Source
- Repository: https://github.com/original/repo
- Version: X.Y.Z
- License: Apache 2.0

## Modifications
- Added multiplatform support
- Fixed issue #123
- Custom feature XYZ

## Usage
...

## Maintenance
Last updated: 2024-01-01
Original version: X.Y.Z
```

## Troubleshooting

### Build Issues

1. **Dependency conflicts**: Check for version mismatches
2. **Platform issues**: Verify platform-specific code
3. **API changes**: Check for breaking changes in dependencies

### Update Issues

1. **Merge conflicts**: Carefully merge upstream changes
2. **Breaking changes**: Test thoroughly after updates
3. **Lost patches**: Ensure custom patches are preserved

## Related Documentation

- See individual module READMEs for specific documentation
- Check LICENSE files for licensing information
- Review NOTICE files for attribution requirements

## Resources

- [Gradle Multi-Project Builds](https://docs.gradle.org/current/userguide/multi_project_builds.html)
- [Open Source Licensing](https://opensource.org/licenses)
- [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html)
