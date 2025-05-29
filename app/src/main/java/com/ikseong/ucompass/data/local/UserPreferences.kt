package com.ikseong.ucompass.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// DataStore 인스턴스를 위한 확장 속성
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

/**
 * DataStore를 사용하여 사용자 정보를 저장하고 불러오는 클래스
 */
@Singleton
class UserPreferences @Inject constructor(
    private val context: Context
) {
    // 키 정의
    companion object {
        private val DEVICE_ID_KEY = stringPreferencesKey("device_id")
        private val USER_NAME_KEY = stringPreferencesKey("user_name")
    }

    /**
     * 디바이스 ID를 저장합니다.
     */
    suspend fun saveDeviceId(deviceId: String) {
        context.dataStore.edit { preferences ->
            preferences[DEVICE_ID_KEY] = deviceId
        }
    }

    /**
     * 디바이스 ID를 가져옵니다.
     */
    fun getDeviceId(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[DEVICE_ID_KEY]
        }
    }

    /**
     * 사용자 이름을 저장합니다.
     */
    suspend fun saveUserName(name: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_NAME_KEY] = name
        }
    }

    /**
     * 사용자 이름을 가져옵니다.
     */
    fun getUserName(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[USER_NAME_KEY]
        }
    }
} 