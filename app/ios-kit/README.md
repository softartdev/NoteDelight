# iOS Kit Module

`app:ios-kit` produces the dynamic `iosComposeKit` framework consumed by the native iOS application. It exports the shared domain and Koin APIs required by Swift and depends transitively on the repository's shared Compose UI.

## Framework Configuration

The framework is configured directly with Kotlin Multiplatform:

```kotlin
listOf(iosArm64(), iosSimulatorArm64()).forEach { iosTarget ->
    iosTarget.binaries.framework {
        baseName = "iosComposeKit"
        isStatic = false
        export(projects.core.domain)
        export(project.dependencies.platform(libs.koin.bom))
        export(libs.koin.core)
    }
}

swiftPMDependencies {
    iosMinimumDeploymentTarget = "14.1"
}
```

SQLCipher is declared only by the direct database owner, `core:data:db-sqldelight`. Kotlin's SwiftPM metadata propagates that dependency into the linkage package used by `iosComposeKit` and the Xcode application.

## Xcode Integration

The iOS application is integrated once with:

```bash
XCODEPROJ_PATH=app/iosApp/iosApp.xcodeproj \
GRADLE_PROJECT_PATH=:app:ios-kit \
./gradlew :app:ios-kit:integrateEmbedAndSign :app:ios-kit:integrateLinkagePackage
```

This adds the Gradle embed/sign build phase to the Xcode project and generates `app/iosApp/KotlinMultiplatformLinkedPackage/`. Re-run `integrateLinkagePackage` after changing Swift package products.

## Build Commands

```bash
# Compile Kotlin for the simulator
./gradlew :app:ios-kit:compileKotlinIosSimulatorArm64

# Link simulator framework
./gradlew :app:ios-kit:linkDebugFrameworkIosSimulatorArm64

# Link device release framework
./gradlew :app:ios-kit:linkReleaseFrameworkIosArm64
```

Framework output is written under `app/ios-kit/build/bin/<target>/<configuration>Framework/`.

## Swift Usage

Swift application sources import the framework by its configured base name:

```swift
import iosComposeKit
```

The framework remains dynamic by project policy. Any change to linkage must be validated by launching the iOS application because link-only builds do not detect all framework embedding and dyld failures.

## Verification

After changes, compile and link the framework, build `app/iosApp/iosApp.xcodeproj`, run the Xcode SQLCipher unit test, and launch the application on a simulator. Then run the repository verification sequence documented in `AGENTS.md`.
