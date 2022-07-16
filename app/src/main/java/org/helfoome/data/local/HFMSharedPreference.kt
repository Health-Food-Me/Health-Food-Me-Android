package org.helfoome.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import org.helfoome.BuildConfig
import javax.inject.Inject

class HFMSharedPreference @Inject constructor(context: Context) {
    private val masterKey = MasterKey.Builder(context, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val dataStore: SharedPreferences =
        if (BuildConfig.DEBUG) context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        else EncryptedSharedPreferences.create(
            context,
            FILE_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

    var accessToken: String
        set(value) = dataStore.edit { putString("ACCESS_TOKEN", value) }
        get() = dataStore.getString("ACCESS_TOKEN", "") ?: ""

    var refreshToken: String
        set(value) = dataStore.edit { putString("REFRESH_TOKEN", value) }
        get() = dataStore.getString("REFRESH_TOKEN", "") ?: ""

    companion object {
        const val FILE_NAME = "HFM"
    }
}