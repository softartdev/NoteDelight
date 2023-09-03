export SRCROOT=/Users/artur/IdeaProjects/NoteDelight/iosApp
export PLATFORM_NAME=iphonesimulator
export CONFIGURATION=Debug
export BUILT_PRODUCTS_DIR=/Users/artur/Library/Developer/Xcode/DerivedData/iosApp-diwfdzdpjmasfsgbrkjkgzbtpdch/Build/Products/Debug-iphonesimulator
export CONTENTS_FOLDER_PATH=Note\ Delight.app
export ARCHS=x86_64
export OTHER_CFLAGS=-DSQLITE_HAS_CODEC
export HEADER_SEARCH_PATHS=/Users/artur/Library/Developer/Xcode/DerivedData/iosApp-diwfdzdpjmasfsgbrkjkgzbtpdch/Build/Products/Debug-iphonesimulator/include\
export FRAMEWORK_SEARCH_PATHS=/Users/artur/Library/Developer/Xcode/DerivedData/iosApp-diwfdzdpjmasfsgbrkjkgzbtpdch/Build/Products/Debug-iphonesimulator\ \ \"/Users/artur/IdeaProjects/NoteDelight/iosApp/Pods/../../shared/build/cocoapods/framework\"\ \"/Users/artur/IdeaProjects/NoteDelight/iosApp/Pods/../../shared/build/cocoapods/framework\"

"$SRCROOT/../gradlew" -p "$SRCROOT/../" :shared:copyFrameworkResourcesToApp \
    -Pmoko.resources.PLATFORM_NAME=$PLATFORM_NAME \
    -Pmoko.resources.CONFIGURATION=$CONFIGURATION \
    -Pmoko.resources.BUILT_PRODUCTS_DIR=$BUILT_PRODUCTS_DIR \
    -Pmoko.resources.CONTENTS_FOLDER_PATH="$CONTENTS_FOLDER_PATH"\
    -Pkotlin.native.cocoapods.platform=$PLATFORM_NAME \
    -Pkotlin.native.cocoapods.archs="$ARCHS" \
    -Pkotlin.native.cocoapods.configuration=$CONFIGURATION \
    -Pkotlin.native.cocoapods.cflags="$OTHER_CFLAGS" \
    -Pkotlin.native.cocoapods.paths.headers="$HEADER_SEARCH_PATHS" \
    -Pkotlin.native.cocoapods.paths.frameworks="$FRAMEWORK_SEARCH_PATHS"
