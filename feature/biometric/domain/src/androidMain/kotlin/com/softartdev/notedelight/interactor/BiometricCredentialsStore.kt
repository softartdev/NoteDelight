package com.softartdev.notedelight.interactor

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import kotlinx.coroutines.flow.first

/**
 * Stores and retrieves the biometric-encrypted credential pair (Base64-encoded AES-GCM ciphertext
 * and IV) using [DataStore] Preferences.
 *
 * **Is the DataStore itself encrypted?** No. DataStore stores data in a plain binary Protobuf file
 * on disk. However, the *values* stored here are already opaque ciphertext — the user's database
 * password is encrypted with a hardware-backed Android Keystore key (AES-256-GCM) that can only
 * be used after a successful biometric authentication. An attacker with raw file-system access
 * would see Base64-encoded bytes but could not decrypt them without the Keystore key, which never
 * leaves the secure hardware.
 */
internal class BiometricCredentialsStore(context: Context) {

    private val dataStore: DataStore<Preferences> = PreferenceDataStoreFactory.create(
        produceFile = { context.applicationContext.preferencesDataStoreFile(PREFS_NAME) }
    )

    /** Returns `true` when both the ciphertext and IV entries are present. */
    suspend fun hasCredentials(): Boolean {
        val prefs: Preferences = dataStore.data.first()
        return prefs.contains(KEY_CIPHERTEXT) && prefs.contains(KEY_IV)
    }

    /**
     * Returns the stored (ciphertext, iv) pair, or `null` if either entry is missing.
     * Both values are Base64-encoded strings (no padding, [android.util.Base64.NO_WRAP]).
     */
    suspend fun load(): Pair<String, String>? {
        val prefs: Preferences = dataStore.data.first()
        val ciphertext: String = prefs[KEY_CIPHERTEXT] ?: return null
        val iv: String = prefs[KEY_IV] ?: return null
        return ciphertext to iv
    }

    /** Persists the encrypted credential pair. Overwrites any existing values. */
    suspend fun save(ciphertext: String, iv: String) = dataStore.edit { prefs ->
        prefs[KEY_CIPHERTEXT] = ciphertext
        prefs[KEY_IV] = iv
    }

    /** Removes all stored credentials. */
    suspend fun clear() = dataStore.edit { prefs ->
        prefs.remove(KEY_CIPHERTEXT)
        prefs.remove(KEY_IV)
    }

    companion object {
        private const val PREFS_NAME = "notedelight_biometric_prefs"
        private val KEY_CIPHERTEXT: Preferences.Key<String> = stringPreferencesKey("ciphertext")
        private val KEY_IV: Preferences.Key<String> = stringPreferencesKey("iv")
    }
}
