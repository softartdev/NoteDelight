# iOS App Module

## Overview

The `app:iosApp` module is the **native iOS application** built with SwiftUI and UIKit, consuming the shared Kotlin Multiplatform code from `app:ios-kit`. It provides the iOS-specific app shell, navigation, and platform integration.

## Purpose

- Provide iOS application entry point
- Integrate shared Kotlin code via CocoaPods
- Implement iOS-specific UI adaptations
- Configure iOS app settings and capabilities
- Support App Store distribution via Fastlane
- Enable Mac Catalyst for macOS support

## Architecture

```
app/iosApp (iOS Application - Swift/SwiftUI)
    ├── iosApp/
    │   ├── iOSApp.swift                  # App entry point
    │   ├── AppDelegate.swift              # App lifecycle
    │   ├── ComposeController.swift        # Compose UI wrapper
    │   ├── RootHolder.swift               # Root view holder
    │   ├── CipherChecker.swift            # SQLCipher verification
    │   ├── Assets.xcassets/               # App icons & images
    │   ├── Info.plist                     # App configuration
    │   └── Preview Content/               # SwiftUI previews
    ├── iosApp.xcodeproj/                  # Xcode project
    ├── iosApp.xcworkspace/                # Xcode workspace (with pods)
    ├── Podfile                            # CocoaPods dependencies
    ├── Podfile.lock                       # Locked dependency versions
    ├── Pods/                              # Downloaded CocoaPods
    └── fastlane/                          # CI/CD automation
        ├── Fastfile
        ├── Appfile
        └── metadata/                      # App Store metadata
```

## Key Components

### iOSApp.swift

SwiftUI app entry point:

```swift
import SwiftUI
import iosComposePod

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var appDelegate
    
    init() {
        // Initialize Kotlin/Native code
        IosAppKt.doInitKoin()
    }
    
    var body: some Scene {
        WindowGroup {
            ComposeController()
                .ignoresSafeArea(.all)
        }
    }
}
```

### ComposeController.swift

UIKit bridge to Compose Multiplatform:

```swift
import UIKit
import SwiftUI
import iosComposePod

struct ComposeController: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        return MainKt.ComposeViewController()
    }
    
    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {
        // Updates handled by Compose
    }
}
```

### RootHolder.swift

Manages app root and lifecycle:

```swift
class RootHolder: ObservableObject {
    let root: Root
    
    init() {
        root = RootBuilder().build()
    }
    
    deinit {
        root.onDestroy()
    }
}
```

### CipherChecker.swift

Verifies SQLCipher functionality:

```swift
import SQLCipher

class CipherChecker {
    static func verifySQLCipher() -> Bool {
        let version = sqlite3_libversion_number()
        return version > 0
    }
}
```

## Xcode Configuration

### Project Structure

- **Target**: iosApp
- **Minimum iOS Version**: 14.0
- **Supported Platforms**: iOS, Mac Catalyst (macOS)
- **Languages**: Swift 5.9+
- **Frameworks**: SwiftUI, UIKit, Combine

### Build Schemes

1. **iosApp** - Debug builds for development
2. **iosApp Release** - Release builds for distribution

### Build Phases

1. **Check Pods Manifest.lock** - Verify pod installation
2. **Sources** - Compile Swift code
3. **Frameworks** - Link frameworks and libraries
4. **Embed Frameworks** - Embed Kotlin framework
5. **Copy Resources** - Bundle resources
6. **Build iosComposePod** - CocoaPods script phase

## CocoaPods Integration

### Podfile

```ruby
platform :ios, '14.0'
use_frameworks!

target 'iosApp' do
  # Kotlin Multiplatform shared code
  pod 'iosComposePod', :path => '../ios-kit'
  
  # SQLCipher for database encryption
  pod 'SQLCipher', '~> 4.5.5'
end

post_install do |installer|
  installer.pods_project.targets.each do |target|
    target.build_configurations.each do |config|
      config.build_settings['IPHONEOS_DEPLOYMENT_TARGET'] = '14.0'
    end
  end
end
```

### Pod Installation

```bash
cd app/iosApp
pod install
```

Always use `iosApp.xcworkspace` (not `.xcodeproj`) after pod install.

## Building & Running

### Development Build

#### Using Xcode
1. Open `iosApp.xcworkspace` in Xcode
2. Select simulator or device
3. Press ⌘R (Run)

#### Using Command Line
```bash
# Build for simulator
xcodebuild -workspace iosApp.xcworkspace \
           -scheme iosApp \
           -configuration Debug \
           -sdk iphonesimulator \
           -derivedDataPath build

# Build for device
xcodebuild -workspace iosApp.xcworkspace \
           -scheme iosApp \
           -configuration Release \
           -sdk iphoneos \
           -derivedDataPath build
```

### Release Build

```bash
# Build for App Store
xcodebuild -workspace iosApp.xcworkspace \
           -scheme iosApp \
           -configuration Release \
           -sdk iphoneos \
           -archivePath build/iosApp.xcarchive \
           archive
           
# Export IPA
xcodebuild -exportArchive \
           -archivePath build/iosApp.xcarchive \
           -exportPath build/ipa \
           -exportOptionsPlist ExportOptions.plist
```

## Testing

### Unit Tests

Located in `Note Delight Unit-Tests/`:

```swift
import XCTest
@testable import iosApp

class Note_Delight_Unit_Tests: XCTestCase {
    func testSQLCipher() {
        XCTAssertTrue(CipherChecker.verifySQLCipher())
    }
    
    func testKotlinInit() {
        // Test Kotlin initialization
        IosAppKt.doInitKoin()
        // Assertions...
    }
}
```

### UI Tests

Can be added in a separate UI test target:

```swift
import XCTest

class NoteDelightUITests: XCTestCase {
    func testCreateNote() {
        let app = XCUIApplication()
        app.launch()
        
        // Test note creation flow
        app.buttons["Create Note"].tap()
        // ...
    }
}
```

### Running Tests

```bash
# Run all tests
xcodebuild test \
    -workspace iosApp.xcworkspace \
    -scheme iosApp \
    -destination 'platform=iOS Simulator,name=iPhone 15'
```

## App Configuration

### Info.plist

Key configurations:

```xml
<key>CFBundleDisplayName</key>
<string>Note Delight</string>

<key>CFBundleIdentifier</key>
<string>com.softartdev.notedelight</string>

<key>CFBundleVersion</key>
<string>841</string>

<key>CFBundleShortVersionString</key>
<string>8.4.1</string>

<key>UIApplicationSceneManifest</key>
<dict>
    <!-- SwiftUI scene configuration -->
</dict>

<key>LSSupportsOpeningDocumentsInPlace</key>
<true/>
```

### Capabilities

- **App Groups**: For sharing data
- **iCloud**: For cloud storage (optional)
- **Background Modes**: If needed
- **File Provider**: For file access (optional)

## Fastlane Integration

Automates TestFlight and App Store deployment:

### Fastfile

```ruby
default_platform(:ios)

platform :ios do
  desc "Build and upload to TestFlight"
  lane :beta do
    build_app(
      workspace: "iosApp.xcworkspace",
      scheme: "iosApp",
      configuration: "Release",
      export_method: "app-store"
    )
    upload_to_testflight(
      skip_waiting_for_build_processing: true
    )
  end
  
  desc "Release to App Store"
  lane :release do
    build_app(
      workspace: "iosApp.xcworkspace",
      scheme: "iosApp",
      configuration: "Release",
      export_method: "app-store"
    )
    upload_to_app_store(
      submit_for_review: false,
      automatic_release: false
    )
  end
end
```

### Running Fastlane

```bash
cd app/iosApp
fastlane beta       # Deploy to TestFlight
fastlane release    # Deploy to App Store
```

## Mac Catalyst Support

The app supports running on macOS via Mac Catalyst:

### Enable Mac Catalyst
1. In Xcode project settings
2. Select "iosApp" target
3. Check "Mac" in "Supported Destinations"

### Mac-Specific Adaptations

```swift
#if targetEnvironment(macCatalyst)
    // Mac-specific code
    window.windowScene?.titlebar?.toolbar = NSToolbar()
#else
    // iOS-specific code
#endif
```

## App Store Distribution

### Preparation

1. **App Store Connect**: Create app listing
2. **Certificates**: Valid distribution certificate
3. **Provisioning Profile**: App Store profile
4. **App Icon**: All required sizes in Assets.xcassets
5. **Screenshots**: Required for all device sizes
6. **Privacy Policy**: Required URL

### Version Management

Update in multiple places:
1. Xcode project: Build number & version
2. `Info.plist`: CFBundleVersion & CFBundleShortVersionString
3. Fastlane metadata: Version information

### Submission

```bash
# Via Fastlane
cd app/iosApp
fastlane release

# Or manually via Xcode
# Product → Archive → Distribute App
```

## Localization

### Supported Languages
- English (default)
- Russian

### Localized Strings

```swift
// In SwiftUI
Text("note_title")
    .localized()

// Or use generated strings
Text(String(localized: "note_title"))
```

### Adding Localizations

1. In Xcode: File → New → File → Strings File
2. Name: `Localizable.strings`
3. Localize: Select languages in File Inspector
4. Add translations

## Dependencies

### CocoaPods Dependencies
- `iosComposePod` - Kotlin Multiplatform shared code
- `SQLCipher` - Database encryption

### System Frameworks
- SwiftUI - Modern UI framework
- UIKit - Traditional UI framework
- Combine - Reactive framework
- Foundation - Core functionality

## Performance Optimization

### App Size
- Enable bitcode (if supported)
- Use on-demand resources
- Compress images
- Remove unused code

### Runtime Performance
- Lazy loading
- Background processing
- Image caching
- Database optimization

## AI Agent Guidelines

When working with this module:

1. **Swift conventions**: Follow Swift API design guidelines
2. **SwiftUI**: Use SwiftUI for new iOS features
3. **Kotlin interop**: Test Swift-Kotlin bridge thoroughly
4. **Xcode**: Keep project files organized
5. **CocoaPods**: Keep Podfile.lock in version control
6. **Testing**: Write unit and UI tests
7. **Accessibility**: Support VoiceOver and Dynamic Type
8. **Privacy**: Handle sensitive data appropriately
9. **App Store**: Follow App Store Review Guidelines
10. **Version**: Keep version numbers synchronized

## Best Practices

### SwiftUI Integration

```swift
struct ContentView: View {
    @StateObject private var rootHolder = RootHolder()
    
    var body: some View {
        ComposeController()
            .environmentObject(rootHolder)
            .ignoresSafeArea()
    }
}
```

### Error Handling

```swift
do {
    try someKotlinFunction()
} catch let error as NSError {
    print("Kotlin error: \(error.localizedDescription)")
}
```

### Memory Management

```swift
class ViewManager {
    weak var delegate: Delegate?
    var kotlinObject: KotlinClass?
    
    deinit {
        // Clean up Kotlin objects
        kotlinObject?.onDestroy()
    }
}
```

## Troubleshooting

### CocoaPods Issues
1. **Pod not found**: Run `pod install`
2. **Framework not found**: Clean build folder (⇧⌘K)
3. **Version mismatch**: Run `pod update`

### Build Issues
1. **Signing errors**: Check provisioning profiles
2. **Framework errors**: Verify ios-kit is built
3. **Swift errors**: Check Swift version compatibility

### Runtime Issues
1. **Crash on launch**: Check initialization order
2. **Black screen**: Verify Compose UI setup
3. **Memory leaks**: Use Instruments to profile

## Related Modules

- **Depends on**: `app:ios-kit` (Kotlin framework)
- **Alternative apps**: `app:android`, `app:desktop`, `app:web`

## Resources

- [iOS Developer Documentation](https://developer.apple.com/documentation/)
- [SwiftUI Documentation](https://developer.apple.com/documentation/swiftui)
- [CocoaPods](https://cocoapods.org/)
- [Fastlane iOS](https://docs.fastlane.tools/getting-started/ios/setup/)
- [App Store Connect](https://appstoreconnect.apple.com/)

