package com.ikseong.ucompass.data.socket.repository

import android.util.Log
import com.ikseong.ucompass.BuildConfig
import com.ikseong.ucompass.data.network.socket.UserLocation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.absoluteValue

@Singleton
class SocketRepository @Inject constructor() {
    private val host: String = BuildConfig.HOST
    private val port: Int = BuildConfig.PORT
    private var socket: Socket? = null
    private var writer: PrintWriter? = null
    private var reader: BufferedReader? = null
    private var receiveJob: Job? = null
    private var sendJob: Job? = null

    // 위치 정보 Flow로 관리
    private val _locationDataFlow = MutableStateFlow<Map<String, UserLocation>>(emptyMap())
    val locationDataFlow: StateFlow<Map<String, UserLocation>> = _locationDataFlow

    init {
        CoroutineScope(Dispatchers.IO).launch {
            locationDataFlow.collect {
                Log.d("Socket", "위치 데이터 업데이트: $it")
            }
        }
    }

//    suspend fun connect() = withContext(Dispatchers.IO) {
//        try {
//            socket = Socket(host, port)
//            writer = PrintWriter(OutputStreamWriter(socket!!.getOutputStream()), true)
//            reader = BufferedReader(InputStreamReader(socket!!.getInputStream()))
//
//            Log.d("Socket", "reader : ${reader}")
//            // 수신 루프 시작
//            receiveJob = CoroutineScope(Dispatchers.IO).launch {
//                try {
//                    while (isActive) {
//                        Log.d("Socket", isConnected().toString())
//                        val line = reader?.readLine() ?: break
//                        try {
//                            val json = JSONObject(line)
//                            when (json.optString("type")) {
//                                "location_broadcast" -> {
//                                    val userId = json.optString("user_id")
//                                    val lat = json.optDouble("lat")
//                                    val lng = json.optDouble("lng")
//                                    val userName = json.optString("name")
//                                    val profileImg = json.optString("profileImg")
//
//                                    Log.d("Socket", "위치 수신: $userName -> $userId at ($lat, $lng)")
//                                    updateLocationData(userId, lat, lng, userName, profileImg)
//                                }
//
//                                "status" -> {
//                                    val status = json.optString("status")
//                                    Log.d("Socket", "로그인 결과: $status")
//                                }
//
//                                "disconnect_broadcast" -> {
//                                    val disconnectedUserId = json.optString("user_id")
//                                    Log.d("Socket", "Disconnection 발생 : $disconnectedUserId")
//                                    removeLocationData(disconnectedUserId)
//                                }
//
//                                else -> {
//                                    Log.w("Socket", "알 수 없는 메시지: $json")
//                                }
//                            }
//                        } catch (e: Exception) {
//                            Log.e("Socket", "JSON 파싱 오류: ${e.message}")
//                        }
//                    }
//                } catch (e: Exception) {
//                    Log.e("Socket", "수신 루프 오류: ${e.message}")
//                } finally {
//                    disconnect()
//                }
//            }
//
//        } catch (e: Exception) {
//            Log.e("Socket", "서버 연결 실패: ${e.message}")
//            disconnect()
//        }
//    }

    suspend fun connect() = withContext(Dispatchers.IO) {
        try {
            socket = Socket(host, port)
            writer = PrintWriter(OutputStreamWriter(socket!!.getOutputStream()), true)
            reader = BufferedReader(InputStreamReader(socket!!.getInputStream()))

            Log.d("Socket", "reader : $reader")

            receiveJob = CoroutineScope(Dispatchers.IO).launch {
                Log.d("Socket", "11")
                try {
                    Log.d("Socket", "22")
                    while (isActive) {
                        Log.d("Socket", "33")
                        // ① 소켓 연결 상태 체크
                        if (!isConnected()) {
                            Log.w("Socket", "수신 중단: 소켓이 연결되어 있지 않습니다.")
                            break
                        }
                        Log.d("Socket", "44")

                        // ② readLine() 호출 전후에 예외를 잡음
                        var line = ""
                        try {
                            Log.d("Socket", "55")
                            Log.d("Socket", "reader : ${reader?.ready()}")
                            line = reader?.readLine().toString()
                            Log.d("Socket", "66 $line")
                            line
                        } catch (e: Exception) {
                            Log.e("Socket", "readLine 예외 발생: ${e.toString()}")
                            null
                        }
                        Log.d("Socket", "33 $line")

                        // ③ readLine()이 null이 되면 루프 탈출
                        if (line == null) {
                            Log.w("Socket", "서버에서 null 수신됨 또는 예외 발생. 연결 종료로 간주합니다.")
                            continue
                        }
                        Log.d("Socket", "77")

                        // ④ JSON 파싱 및 처리
                        try {
                            val json = JSONObject(line)
                            Log.d("Socket", "수신된 JSON: $json")
                            when (json.optString("type")) {
                                "location_broadcast" -> {
                                    val userId = json.optString("user_id")
                                    val lat = json.optDouble("lat")
                                    val lng = json.optDouble("lng")
                                    val userName = json.optString("name")
                                    Log.d("Socket", "위치 수신: $userName -> $userId at ($lat, $lng)")
                                    updateLocationData(userId, lat, lng, userName)
                                }

                                "status" -> {
                                    Log.d("Socket", "로그인 결과: ${json.optString("status")}")
                                }

                                "disconnect_broadcast" -> {
                                    val disconnectedUserId = json.optString("user_id")
                                    Log.d("Socket", "Disconnection 발생: $disconnectedUserId")
                                    removeLocationData(disconnectedUserId)
                                }

                                else -> {
                                    Log.w("Socket", "알 수 없는 메시지: $json")
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("Socket", "JSON 파싱 오류: ${e.toString()}")
                        }
                    }
                } catch (e: Exception) {
                    Log.e("Socket", "수신 루프 전체 오류: ${e.message}")
                } finally {
                    disconnect()
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
        Log.d("Socket", "로그인 데이터: $loginData")
        send(loginData)
    }

    suspend fun sendLocation(lat: Double, lng: Double) = withContext(Dispatchers.IO) {
        if (socket == null || !isConnected()) {
            Log.e("Socket", "소켓이 연결되어 있지 않습니다.")
            return@withContext
        }
        sendJob = CoroutineScope(Dispatchers.IO).launch {
            try {
                while (isActive) {
                    val locationData = JSONObject()
                        .put("type", "location_update")
                        .put("lat", lat)
                        .put("lng", lng.absoluteValue)
                    Log.d("Socket", "위치 전송 : $locationData")
                    send(locationData)
                    delay(3000L) // 2초마다 위치 전송
                }
            } catch (e: Exception) {
                Log.e("Socket", "위치 전송 실패: ${e.message}")
            }
        }
    }

    private suspend fun send(json: JSONObject) = withContext(Dispatchers.IO) {
        socket ?: run {
            socket = Socket(host, port)
            writer = PrintWriter(OutputStreamWriter(socket!!.getOutputStream()), true)
            reader = BufferedReader(InputStreamReader(socket!!.getInputStream()))
        }
        writer?.println(json.toString())
    }

    private fun updateLocationData(
        userId: String,
        lat: Double,
        lng: Double,
        name: String,
    ) {
        val current = _locationDataFlow.value.toMutableMap()
        Log.d("Socket", "updateLocationData: $userId, $lat, $lng, $name")
        current[userId] = UserLocation(lat, lng, name)
        _locationDataFlow.value = current
    }

    private fun removeLocationData(userId: String) {
        val current = _locationDataFlow.value.toMutableMap()
        current.remove(userId)
        _locationDataFlow.value = current
    }

    fun disconnect() {
        receiveJob?.cancel()
        sendJob?.cancel()
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