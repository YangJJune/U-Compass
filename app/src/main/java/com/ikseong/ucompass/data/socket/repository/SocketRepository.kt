package com.ikseong.ucompass.data.socket.repository

import android.util.Log
import com.ikseong.ucompass.BuildConfig
import com.ikseong.ucompass.data.network.socket.receive.SocketLocationReceiveDto
import com.ikseong.ucompass.data.network.socket.write.SocketLocationWriteDto
import com.ikseong.ucompass.data.network.socket.write.SocketLoginDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.net.Socket
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SocketRepository @Inject constructor() {
    private var socket: Socket? = null
    private var writer: PrintWriter? = null
    private var reader: BufferedReader? = null
    private var receiveJob: Job? = null
    private val json = Json { encodeDefaults = true }

    private val _locationDto = MutableSharedFlow<SocketLocationReceiveDto>()
    val locationDto: SharedFlow<SocketLocationReceiveDto> = _locationDto

    // 연결하는 로직
    // 데이터 받는 로직
    fun connect() {
        try {
            socket = Socket(BuildConfig.HOST, BuildConfig.PORT)
            writer = PrintWriter(OutputStreamWriter(socket!!.getOutputStream()), true)
            reader = BufferedReader(InputStreamReader(socket!!.getInputStream()))

            // 수신 루프 시작
            receiveJob = CoroutineScope(Dispatchers.IO).launch {
                while (isActive) {
                    val line = reader?.readLine() ?: break
                    try {
                        val _json = JSONObject(line)
                        if (_json.has("type")) {
                            when (_json.getString("type")) {
                                "location_broadcast" -> {
                                    val dto = SocketLocationReceiveDto(
                                        deviceId = _json.getString("user_id"),
                                        lat = _json.getDouble("lat"),
                                        lng = _json.getDouble("lng")
                                    )
                                    _locationDto.emit(dto)
                                }
                                // 다른 타입들 처리
                            }
                        }
                        //type에 따라 처리
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

    fun flowConnect() = flow {
//        mLocationDto = SocketLocationReceiveDto()
        try {
            socket = Socket(BuildConfig.HOST, BuildConfig.PORT)
            writer = PrintWriter(OutputStreamWriter(socket!!.getOutputStream()), true)
            reader = BufferedReader(InputStreamReader(socket!!.getInputStream()))

            // 수신 루프 시작
            val line = reader?.readLine() ?: return@flow
            try {
                val _json = JSONObject(line)
                if (_json.has("type")) {
                    when (_json.getString("type")) {
                        "location_broadcast" -> {
                            val dto = SocketLocationReceiveDto(
                                deviceId = _json.getString("user_id"),
                                lat = _json.getDouble("lat"),
                                lng = _json.getDouble("lng")
                            )
                            _locationDto.emit(dto)
                            emit(SocketLocationReceiveDto(deviceId = "1", lat = 1.0, lng = 1.0))
                        }
                        // 다른 타입들 처리
                    }
                }
                //type에 따라 처리
            } catch (e: Exception) {
                Log.e("Socket", "JSON 파싱 오류: ${e.message}")
            }
        } catch (e: Exception) {
            Log.e("Socket", "서버 연결 실패: ${e.message}")
            disconnect()
        }
    }
        .flowOn(Dispatchers.IO)

    // 디스커넥트 로직
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

    suspend fun login(userId: String, roomId: Int) {
        val loginDto = SocketLoginDto(
            userId = userId,
            roomId = roomId
        )
        val loginData = JSONObject(
            json.encodeToString(loginDto)
        )

        send(loginData)
    }

    suspend fun sendLocation(lat: Double, lng: Double) {

        val locationWriteDto = SocketLocationWriteDto(
            lat = lat,
            lng = lng
        )
        val locationData = JSONObject(
            json.encodeToString(locationWriteDto)
        )
        send(locationData)
    }

    private suspend fun send(json: JSONObject) = withContext(Dispatchers.IO) {
        writer?.println(json.toString())
    }


}