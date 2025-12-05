#!/bin/sh

echo "28d0921e0d134ac1655c3b93e675ed50aa77233d02966130beff857fb60cf4f6  ./app/android/keystore.properties" | sha256sum -c -
echo "cc018f4fb00ec66cf3b8d918ce4db107945502ab7c47dd0c33fc10e56d79d1a2  ./app/android/note_room_key_store.jks" | sha256sum -c -
echo "58e5bdf33538df1638858d0f8a8bba8161f29b4846a7b6d0aaba3279650aa04a  ./app/android/fastlane/api-7350020584032910214-328107-d8d3807d1e1a.json" | sha256sum -c -

echo "7b9f841129997ddb098e03dd7099a3341dbfa012c31e4cb465b92ddd476cca7a  ./app/iosApp/fastlane/28F5CB4337.json" | sha256sum -c -
echo "e36a29b3964c8bd90030f93ac986a39510185df582a783f1b946e6127a005e38  ./app/iosApp/fastlane/ios_distribution.p12" | sha256sum -c -
echo "4fb66d6fbe9fc4a544303e6e516da2ee3314187e28fc5aacb9631f9b42b511b0  ./app/iosApp/fastlane/NoteDelight_Distribution_Profile.mobileprovision" | sha256sum -c -

echo "5439f53423060953d110cb3217e089b23da20aa7559e80b32767184f2516bba0  ./app/desktop/keystore.properties" | sha256sum -c -
echo "97f56f5e6e5bcefb738333f22170be94e0196c34958d778067295697a30f5068  ./app/desktop/macOS_development.p12" | sha256sum -c -
