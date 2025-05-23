package com.ikseong.ucompass.data.repositoryImpl

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.ikseong.ucompass.data.repository.DeviceIdRepository
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DeviceIdRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : DeviceIdRepository {

    private companion object {
        private val DEVICE_ID = stringPreferencesKey("device_id")
    }

    override suspend fun getDeviceId() =
        dataStore.data.map { preferences ->
            preferences[DEVICE_ID] ?: ""
        }

    override suspend fun setDeviceId(deviceId: String) {
        dataStore.edit { preferences ->
            preferences[DEVICE_ID] = deviceId
        }
    }
}