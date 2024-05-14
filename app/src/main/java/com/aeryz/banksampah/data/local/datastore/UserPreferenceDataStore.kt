package com.aeryz.banksampah.data.local.datastore

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.aeryz.banksampah.utils.PreferenceDataStoreHelper
import kotlinx.coroutines.flow.Flow

interface UserPreferenceDataSource {
    suspend fun getUserDarkModePref(): Boolean
    fun getUserDarkModePrefFlow(): Flow<Boolean>
    suspend fun setUserDarkModePref(isUsingDarkMode: Boolean)
    fun getUserLayoutModePrefFlow(): Flow<Int>
    suspend fun setUserLayoutModePref(layoutMode: Int)
    fun getUserAddressPrefFlow(): Flow<String>
    suspend fun setUserAddressPref(address: String)
}

class UserPreferenceDataSourceImpl(private val preferenceHelper: PreferenceDataStoreHelper) :
    UserPreferenceDataSource {

    override suspend fun getUserDarkModePref(): Boolean {
        return preferenceHelper.getFirstPreference(PREF_USER_DARK_MODE, false)
    }

    override fun getUserDarkModePrefFlow(): Flow<Boolean> {
        return preferenceHelper.getPreference(PREF_USER_DARK_MODE, false)
    }

    override suspend fun setUserDarkModePref(isUsingDarkMode: Boolean) {
        return preferenceHelper.putPreference(PREF_USER_DARK_MODE, isUsingDarkMode)
    }

    override fun getUserLayoutModePrefFlow(): Flow<Int> {
        return preferenceHelper.getPreference(PREF_USER_LAYOUT_MODE, 1)
    }

    override suspend fun setUserLayoutModePref(layoutMode: Int) {
        return preferenceHelper.putPreference(PREF_USER_LAYOUT_MODE, layoutMode)
    }

    override fun getUserAddressPrefFlow(): Flow<String> {
        return preferenceHelper.getPreference(PREF_USER_ADDRESS, "Harap isi alamat di halaman profil")
    }

    override suspend fun setUserAddressPref(address: String) {
        return preferenceHelper.putPreference(PREF_USER_ADDRESS, address)
    }

    companion object {
        val PREF_USER_DARK_MODE = booleanPreferencesKey("PREF_USER_DARK_MODE")
        val PREF_USER_LAYOUT_MODE = intPreferencesKey("PREF_USER_LAYOUT_MODE")
        val PREF_USER_ADDRESS = stringPreferencesKey("PREF_USER_ADDRESS")
    }
}
