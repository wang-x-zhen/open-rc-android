package com.wangzhen.openrc.activity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.wangzhen.openrc.R
import com.wangzhen.openrc.utils.NetUtils
import com.wangzhen.openrc.utils.UdpUtils
import kotlinx.android.synthetic.main.activity_tcp.*
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.ServerSocket
import java.net.Socket
import java.nio.charset.Charset
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class TcpActivity : AppCompatActivity() {
    var serverSocket: ServerSocket? = null
    var serverSocketPort: Int = 5566
    var socket: Socket? = null
    var inputStream: InputStream? = null
    var outputStream: OutputStream? = null
    var clientInputStream: InputStream? = null
    var clientOutputStream: OutputStream? = null
    var threadUdpListen: Thread? = null
    var udpListenPort: Int = 5678
    var clientSocket: Socket? = null
    var _serverIp = ""
    var isGetRTT = false
    var isTcpSendBack = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tcp)
        openServer.setOnClickListener {
            runIo {
                openServer {ip, port, msg ->
                    runOnUiThread {
                        tcp_receive_content.text =  "${tcp_receive_content.text} ip:$ip port:$port msg:$msg \n"
                    }
                }
            }
        }
        get_tcp_rtt.setOnCheckedChangeListener { _, b ->
            isGetRTT = b
        }
        tcp_send_back.setOnCheckedChangeListener { _, b ->
            isTcpSendBack = b
        }
        val ip = NetUtils.getIP(this)
        tcp_ip_tv.text = "ip:$ip BroadcastAddress:${NetUtils.getBroadcastAddress(this).hostAddress}"
        send.setOnClickListener {
            runIo {
                if (isGetRTT) {
                    clientOutputStream?.write(System.currentTimeMillis().toString().toByteArray())
                } else {
                    clientOutputStream?.write(tcp_send_msg.text.toString().toByteArray())
                }
            }
        }
        connectTcp.setOnClickListener {
            val ip = tcp_ip_et.text.toString()
            val port = tcp_port_et.text.toString().toInt()
            runIo {
                connectServer(ip, port) {
                    runOnUiThread {
                        tcp_receive_content.text = it
                        if (isGetRTT && it.startsWith("16")) {
                            tcp_rtt_tv.text = "耗时：${(System.currentTimeMillis() - it.toLong())}ms"
                        }
                    }
                }
            }
        }
        serverIp.setOnClickListener {
            tcp_ip_et.setText(_serverIp)
        }
        sendIpUdp.setOnClickListener {
            runIo {
                UdpUtils.send(
                    NetUtils.getBroadcastAddress(this).hostAddress,
                    udpListenPort,
                    serverSocketPort.toString()
                )
            }
        }
        threadUdpListen = Thread {
            while (true) {
                UdpUtils.receive(udpListenPort) { _ip, _port, msg ->
                    _serverIp = _ip
                    runOnUiThread {
                        serverIp.text = _serverIp
                    }
                }
            }
        }
        threadUdpListen?.start()

    }

    var mThreadPool: ExecutorService? = null
    private fun runIo(block: () -> Unit) {
        if (mThreadPool == null) {
            // 初始化线程池
            mThreadPool = Executors.newCachedThreadPool();
        }
        mThreadPool?.execute {
            block.invoke()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        threadUdpListen?.interrupt()
    }

    private fun openServer(onGetMsg: (ip: String?, port: Int?, msg: String) -> Unit) {
        try {
            //开启服务、指定端口号
            serverSocket = ServerSocket(5566)
            //等待客户端的连接，Accept会阻塞，直到建立连接，
            //所以需要放在子线程中运行。
            Log.e("openServer", "serverSocket?.accept()-------1-----")
            socket = serverSocket?.accept()
            Log.e("openServer", "serverSocket?.accept()-------2-----")
            //获取输入流
            inputStream = socket?.getInputStream()

            Log.e("openServer", "socket?.getInputStream()------------")
            //获取输出流
            outputStream = socket?.getOutputStream()
            Log.e("openServer", "socket?.getOutputStream()------------")
            outputStream = socket?.getOutputStream()

            val buffer = ByteArray(1024)
            // 循环执行read，用来接收数据。
            // 数据存在buffer中，count为读取到的数据长度。
            // 循环执行read，用来接收数据。
            // 数据存在buffer中，count为读取到的数据长度。

            while (true) {
                Log.e("openServer", "inputStream?.read(buffer)------1------")
                val count: Int? = inputStream?.read(buffer)
                Log.e("openServer", "inputStream?.read--2-----count:$count")
                if (count == null || count < 1) {
                    continue
                }
                var dataS = String(buffer, 0, count, Charset.forName("utf-8"))
                Log.e("openServer", "inputStream?.read--2-----buffer:$dataS")
                onGetMsg.invoke(socket?.inetAddress?.hostAddress, socket?.port, dataS)
                if (isTcpSendBack) {
                    outputStream?.write(dataS.toByteArray())
                }
            }

        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "服务开启失败", Toast.LENGTH_SHORT).show()
            return
        }
    }

    private fun connectServer(severIp: String, port: Int, onGetMsg: (String) -> Unit) {
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
            clientOutputStream?.write(("hello Server" + "\n").toByteArray())
            val buffer = ByteArray(1024)
            // 循环执行read，用来接收数据。
            // 数据存在buffer中，count为读取到的数据长度。
            // 循环执行read，用来接收数据。
            // 数据存在buffer中，count为读取到的数据长度。

            while (true) {
                Log.e("connectServer", "inputStream?.read(buffer)------1------")
                val count: Int? = clientInputStream?.read(buffer)
                if (count == null || count < 1) {
                    continue
                }
                val r = String(buffer, 0, count, Charset.forName("utf-8"))
                Log.e("connectServer", "inputStream?.read--2-----count:$count")
                Log.e("connectServer", "inputStream?.read--2-----buffer:$r")
                onGetMsg.invoke(r)
            }

        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "connectServer 失败", Toast.LENGTH_SHORT).show()
            return
        }
    }


}