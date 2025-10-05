#!/bin/sh

# Encrypt the files
# --batch to prevent interactive command --yes to assume "yes" for questions

gpg --symmetric --cipher-algo AES256 --batch --yes --passphrase="$LARGE_SECRET_PASSPHRASE" \
--output ./.github/secrets/keystore.properties.gpg ./app/android/keystore.properties

gpg --symmetric --cipher-algo AES256 --batch --yes --passphrase="$LARGE_SECRET_PASSPHRASE" \
--output ./.github/secrets/note_room_key_store.jks.gpg ./app/android/note_room_key_store.jks

gpg --symmetric --cipher-algo AES256 --batch --yes --passphrase="$LARGE_SECRET_PASSPHRASE" \
--output ./.github/secrets/api-7350020584032910214-328107-d8d3807d1e1a.json.gpg ./app/android/fastlane/api-7350020584032910214-328107-d8d3807d1e1a.json

gpg --symmetric --cipher-algo AES256 --batch --yes --passphrase="$LARGE_SECRET_PASSPHRASE" \
--output ./.github/secrets/28F5CB4337.json.gpg ./app/iosApp/fastlane/28F5CB4337.json

gpg --symmetric --cipher-algo AES256 --batch --yes --passphrase="$LARGE_SECRET_PASSPHRASE" \
--output ./.github/secrets/ios_distribution.p12.gpg ./app/iosApp/fastlane/ios_distribution.p12

gpg --symmetric --cipher-algo AES256 --batch --yes --passphrase="$LARGE_SECRET_PASSPHRASE" \
--output ./.github/secrets/NoteDelight_Distribution_Profile.mobileprovision.gpg ./app/iosApp/fastlane/NoteDelight_Distribution_Profile.mobileprovision
