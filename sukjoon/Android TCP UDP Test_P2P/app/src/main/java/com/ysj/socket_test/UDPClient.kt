package com.ysj.socket_test

import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketTimeoutException

class UDPClient {
    fun openSocket(){
        kotlin.runCatching {
            val socket = DatagramSocket(3000)
            socket.soTimeout=100
            runProcess(socket)
        }.onFailure {
            // 예외가 발생한 경우 스택 트레이스 출력
            it.printStackTrace()
        }
    }
    private fun runProcess(socket: DatagramSocket){
        kotlin.runCatching {
            var cnt = 0
            val startTime = System.currentTimeMillis()

            var num = 10000
            val list = ArrayList<packetData>()
            while(cnt<=1000) {
                println(cnt)
                if ((1..2).random() == 1) {
                    num += 1
                } else {
                    num -= 1
                }
                val content = num.toString()
                val sendData = content.toByteArray()
                val sendPacket = DatagramPacket(sendData, sendData.size, InetAddress.getByName("192.168.0.15"), 3000)

                socket.send(sendPacket)
                val response = ByteArray(1024)
                val responsePacket = DatagramPacket(response, response.size)
                try {
                    socket.receive(responsePacket)
                }catch(e:SocketTimeoutException){
                    println("타임아웃..")
                }
                list.add(packetData(cnt, System.currentTimeMillis(),String(responsePacket.data, Charsets.UTF_8).slice(0..4) ))
                cnt++
            }
            val sendData = "END".toByteArray()
            val sendPacket = DatagramPacket(sendData, sendData.size, InetAddress.getByName("192.168.0.15"), 3000)
            socket.send(sendPacket)
            val endTime = System.currentTimeMillis()
            println("종료! "+ (endTime-startTime).toString()+"ms")
            println(list)
        }.onFailure {
            it.printStackTrace()
        }
    }

}