package com.ysj.socket_test

import java.net.InetSocketAddress
import java.net.Socket
import java.time.Instant
import java.time.format.DateTimeFormatter

class Client {
    fun openSocket(){
        kotlin.runCatching {
            val socket = Socket()
            println("연결 시작!")
            socket.connect(InetSocketAddress("192.168.0.15", 3000))
            println("연결 성공!")
            runProcess(socket)
        }.onFailure {
            // 예외가 발생한 경우 스택 트레이스 출력
            it.printStackTrace()
        }
    }
    private fun runProcess(socket: Socket){
        kotlin.runCatching {
            val inputStream = socket.getInputStream()
            val outputStream = socket.getOutputStream()
            var cnt = 0

            val startTime = System.currentTimeMillis()

            var num = 10000
            val list = ArrayList<packetData>()
            while(cnt<= 1000) {
                if ((1..2).random() == 1) {
                    num += 1
                } else {
                    num -= 1
                }
                val content = num.toString()
                outputStream.write(content.toByteArray())
                outputStream.flush()
                val response = ByteArray(1024)
                inputStream.read(response)
                list.add(packetData(cnt, System.currentTimeMillis(),String(response, Charsets.UTF_8).slice(0..4) ))
                println(cnt)
                cnt++
            }
            outputStream.write("END".toByteArray())
            outputStream.flush()
            inputStream.close()
            outputStream.close()
            val endTime = System.currentTimeMillis()
            println("종료! "+ (endTime-startTime).toString()+"ms")
            println(list)
        }.onFailure {
            it.printStackTrace()
        }
    }
}