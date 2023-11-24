#!/bin/bash

src="/Users/artur/AndroidStudioProjects/NoteDelight/iosApp/iosApp/Assets.xcassets/AppIcon.appiconset/1024.png"

iconset="icon.iconset"
mkdir -p $iconset

sizes=(16 32 128 256 512)
for size in "${sizes[@]}"; do
    convert $src -resize ${size}x${size} $iconset/icon_${size}x${size}.png
    convert $src -resize $(($size * 2))x$(($size * 2)) $iconset/icon_${size}x${size}@2x.png
done

iconutil -c icns $iconset

#mv $iconset/../icon.icns .
#rm -rf $iconset
