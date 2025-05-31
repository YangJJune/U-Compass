package com.ikseong.ucompass.data.socket.repository

import android.util.Log
import com.ikseong.ucompass.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.net.Socket

class SocketRepositoryV1(
    private val host: String = BuildConfig.HOST,
    private val port: Int = BuildConfig.PORT,
) {
    private var socket: Socket? = null
    private var writer: PrintWriter? = null
    private var reader: BufferedReader? = null
    private var receiveJob: Job? = null

    // 위치 정보 Flow로 관리
    private val _locationDataFlow = MutableStateFlow<Map<String, UserLocation>>(emptyMap())
    val locationDataFlow: StateFlow<Map<String, UserLocation>> = _locationDataFlow

    suspend fun connect() = withContext(Dispatchers.IO) {
        try {
            socket = Socket(BuildConfig.HOST, BuildConfig.PORT)
            writer = PrintWriter(OutputStreamWriter(socket!!.getOutputStream()), true)
            reader = BufferedReader(InputStreamReader(socket!!.getInputStream()))

            // 수신 루프 시작
            receiveJob = CoroutineScope(Dispatchers.IO).launch {
                while (isActive) {
                    val line = reader?.readLine() ?: break
                    try {
                        val json = JSONObject(line)
                        Log.d("Socket", json.toString())
                        when (json.optString("type")) {
                            "location_broadcast" -> {
                                val userId = json.optString("user_id")
                                val lat = json.optDouble("lat")
                                val lng = json.optDouble("lng")
                                Log.d("Socket", "위치 수신: $userId at ($lat, $lng)")
                                updateLocationData(userId, lat, lng)
                            }

                            "status" -> {
                                val status = json.optString("status")
                                Log.d("Socket", "로그인 결과: $status")
                            }

                            else -> {
                                Log.w("Socket", "알 수 없는 메시지: $json")
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("Socket", "JSON 파싱 오류: ${e.message}")
                    }
                }
            }

        } catch (e: Exception) {
            Log.e("Socket", "서버 연결 실패: ${e.message}")
            disconnect()
        }
    }

    suspend fun login(userId: String, roomId: Int) {
        val loginData = JSONObject()
            .put("type", "login")
            .put("user_id", userId)
            .put("room_id", roomId)
        send(loginData)
    }

    suspend fun sendLocation(lat: Double, lng: Double) {
        val locationData = JSONObject()
            .put("type", "location_update")
            .put("lat", lat)
            .put("lng", lng)
        Log.d("Socket1", locationData.toString())
        send(locationData)
    }

    private suspend fun send(json: JSONObject) = withContext(Dispatchers.IO) {
        Log.d("Socket2", json.toString())
        Log.d("Socket3", writer.toString())
        writer?.println(json.toString())
    }

    private fun updateLocationData(userId: String, lat: Double, lng: Double) {
        val current = _locationDataFlow.value.toMutableMap()
        current[userId] = UserLocation(lat, lng)
        _locationDataFlow.value = current
    }

    fun disconnect() {
        receiveJob?.cancel()
        writer?.close()
        reader?.close()
        socket?.close()
        writer = null
        reader = null
        socket = null
    }

    fun isConnected(): Boolean {
        return socket?.isConnected == true && socket?.isClosed == false
    }
}