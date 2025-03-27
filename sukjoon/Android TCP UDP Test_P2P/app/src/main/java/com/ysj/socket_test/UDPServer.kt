package com.ysj.socket_test

import android.provider.ContactsContract.Data
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.SocketTimeoutException

class UDPServer {
    fun openServer() {
        // 예외를 처리하기 위해 runCatching 사용

        runCatching {
            while(true) {
                val socket = DatagramSocket(3000)
                socket.soTimeout = 100
                var num = -1000
                val list = ArrayList<packetData>()
                var cnt = 0
                while (true) {
                    val request = ByteArray(1024)
                    val requestPacket = DatagramPacket(request, request.size)
                    try {
                        socket.receive(requestPacket)
                    }catch(e: SocketTimeoutException){
                        println("타임아웃..")
                    }

                    if (request.decodeToString().contains("END")) {
                        socket.close()
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
                    val responseData = content.toByteArray()
                    val responsePacket = DatagramPacket(
                        responseData,
                        responseData.size,
                        InetAddress.getByName("192.168.0.5"),
                        3000
                    )
                    socket.send(responsePacket)

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