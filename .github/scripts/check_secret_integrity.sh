#!/bin/sh

echo "28d0921e0d134ac1655c3b93e675ed50aa77233d02966130beff857fb60cf4f6  ./app/android/keystore.properties" | sha256sum -c -

echo "cc018f4fb00ec66cf3b8d918ce4db107945502ab7c47dd0c33fc10e56d79d1a2  ./app/android/note_room_key_store.jks" | sha256sum -c -

echo "58e5bdf33538df1638858d0f8a8bba8161f29b4846a7b6d0aaba3279650aa04a  ./app/android/fastlane/api-7350020584032910214-328107-d8d3807d1e1a.json" | sha256sum -c -

echo "7b9f841129997ddb098e03dd7099a3341dbfa012c31e4cb465b92ddd476cca7a  ./app/iosApp/fastlane/28F5CB4337.json" | sha256sum -c -

echo "8c18885545c026cda51805e3b8eb86f5639c87ddcd695be2bf8a649db6cbe153  ./app/iosApp/fastlane/ios_distribution.p12" | sha256sum -c -

echo "2c1cec349be9d1ac6f85c2f6f3c10d8efa109e3348f6615fcddac67a34b8f46f  ./app/iosApp/fastlane/NoteDelight_Distribution_Profile.mobileprovision" | sha256sum -c -
