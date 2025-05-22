import android.util.Log
import kotlinx.coroutines.*
import org.json.JSONObject
import java.io.*
import java.net.Socket

class SocketRepository(
    private val host: String = "54.66.5.0",
    private val port: Int = 9000,
    private val onReceive: (JSONObject) -> Unit = {}
) {
    private var socket: Socket? = null
    private var writer: PrintWriter? = null
    private var reader: BufferedReader? = null
    private var receiveJob: Job? = null

    suspend fun connect() = withContext(Dispatchers.IO) {
        try {
            socket = Socket(host, port)
            writer = PrintWriter(OutputStreamWriter(socket!!.getOutputStream()), true)
            reader = BufferedReader(InputStreamReader(socket!!.getInputStream()))

            // 수신 루프 시작
            receiveJob = CoroutineScope(Dispatchers.IO).launch {
                while (isActive) {
                    val line = reader?.readLine() ?: break
                    try {
                        val json = JSONObject(line)
                        Log.d("Socket",json.toString())
                        onReceive(json)
                    } catch (e: Exception) {
                        Log.e("Socket","JSON 파싱 오류: ${e.message}")
                    }
                }
            }

        } catch (e: Exception) {
            Log.e("Socket","서버 연결 실패: ${e.message}")
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
        send(locationData)
    }

    private suspend fun send(json: JSONObject) = withContext(Dispatchers.IO) {
        writer?.println(json.toString())
    }

    private fun disconnect() {
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