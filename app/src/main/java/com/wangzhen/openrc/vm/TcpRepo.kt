package com.wangzhen.openrc.vm

import android.util.Log
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket

class TcpRepo {
    companion object {
        const val PORT = 13026
    }

    var clientInputStream: InputStream? = null
    var clientOutputStream: OutputStream? = null
    var clientSocket: Socket? = null

    fun connectServer(severIp: String, port: Int = PORT) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                // 建立连接到远程服务器的Socket
                Log.e("connectServer", "Socket(severIp, port)-------1-----")
                clientSocket = Socket(severIp, port)
                Log.e("connectServer", "Socket(severIp, port)-------2-----")

                Log.i("本机地址", clientSocket?.localAddress.toString())

                //获取输入流
                clientInputStream = clientSocket?.getInputStream()
                //获取输出流
                clientOutputStream = clientSocket?.getOutputStream()
            } catch (e: IOException) {
                e.printStackTrace()
                Log.e(TAG, "connectServer 失败")
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun send(byteArray: ByteArray) {
        GlobalScope.launch(Dispatchers.IO) {
            Log.e(TAG, "send ${byteArray.toIntString()}")
            clientOutputStream?.write(byteArray)
        }
    }
}
fun ByteArray.toIntString() = this.joinToString("") {
    (it.toInt() and 0xFF).toString(10) + " "
}