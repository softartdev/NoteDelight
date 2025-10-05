#!/bin/sh

# Decrypt the file
# --batch to prevent interactive command --yes to assume "yes" for questions

gpg --quiet --batch --yes --decrypt --passphrase="$LARGE_SECRET_PASSPHRASE" \
--output ./app/android/keystore.properties ./.github/secrets/keystore.properties.gpg

gpg --quiet --batch --yes --decrypt --passphrase="$LARGE_SECRET_PASSPHRASE" \
--output ./app/android/note_room_key_store.jks ./.github/secrets/note_room_key_store.jks.gpg

gpg --quiet --batch --yes --decrypt --passphrase="$LARGE_SECRET_PASSPHRASE" \
--output ./app/android/fastlane/api-7350020584032910214-328107-d8d3807d1e1a.json ./.github/secrets/api-7350020584032910214-328107-d8d3807d1e1a.json.gpg

gpg --quiet --batch --yes --decrypt --passphrase="$LARGE_SECRET_PASSPHRASE" \
--output ./app/iosApp/fastlane/28F5CB4337.json ./.github/secrets/28F5CB4337.json.gpg

gpg --quiet --batch --yes --decrypt --passphrase="$LARGE_SECRET_PASSPHRASE" \
--output ./app/iosApp/fastlane/ios_distribution.p12 ./.github/secrets/ios_distribution.p12.gpg

gpg --quiet --batch --yes --decrypt --passphrase="$LARGE_SECRET_PASSPHRASE" \
--output ./app/iosApp/fastlane/NoteDelight_Distribution_Profile.mobileprovision ./.github/secrets/NoteDelight_Distribution_Profile.mobileprovision.gpg
