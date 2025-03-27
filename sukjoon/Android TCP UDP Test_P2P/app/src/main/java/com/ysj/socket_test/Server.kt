package com.ysj.socket_test

import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.time.Instant
import java.time.format.DateTimeFormatter

class Server {
    fun openServer() {
        // 예외를 처리하기 위해 runCatching 사용
        runCatching {
            val server = ServerSocket(3000, 50, InetAddress.getByName("0.0.0.0"))
            while(true) {
                println("서버 열기")
//            server.bind(InetSocketAddress("49.173.62.69", 3000))
                val client = server.accept()
                var num = -1000
                val list = ArrayList<packetData>()
                var cnt = 0
                while (true) {

                    val request = ByteArray(1024)
                    val inputStream = client.getInputStream()
                    val outputStream = client.getOutputStream()

                    inputStream.read(request)
                    if (request.decodeToString().contains("END")) {
                        client.close()
                        break
                    }

                    list.add(
                        packetData(
                            cnt,
                            System.currentTimeMillis(),
                            String(request, Charsets.UTF_8).slice(0..4)
                        )
                    )


                    if ((1..2).random() == 1) {
                        num += 1
                    } else {
                        num -= 1
                    }
                    val content = num.toString()

                    outputStream.write(content.toByteArray())
                    outputStream.flush()
                    cnt++
                }
                println("SERVER : " + list)
            }


        }.onFailure {
            // 예외가 발생한 경우 스택 트레이스 출력
            it.printStackTrace()
        }
    }
}