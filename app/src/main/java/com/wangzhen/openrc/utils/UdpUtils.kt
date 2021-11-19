package com.wangzhen.openrc.utils

import android.util.Log
import com.wangzhen.openrc.activity.UdpSetting
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.UnknownHostException
import kotlin.jvm.Throws

/**
 *  @author :summerlabs
 *  @since : 2021/4/13 xxx
 */
object UdpUtils {

    @JvmStatic
    fun receive(port: Int, onReceive: (ip: String, port: Int, data: String) -> Unit) {
        //创建数据包传输对象DatagramSocket 绑定端口号
        val ds = DatagramSocket(port)
        //创建字节数组
        val data = ByteArray(1024)
        //创建数据包对象，传递字节数组
        val dp = DatagramPacket(data, data.size)
        //调用ds对象的方法receive传递数据包
        Log.e("com.sunmmerlab.openrc.utils.UdpUtils", "receive------------")
        ds.receive(dp)
        Log.e("com.sunmmerlab.openrc.utils.UdpUtils", "receive=============")
        //获取发送端的IP地址对象
        val ip = dp.address.hostAddress

        //获取发送的端口号
        val port = dp.port

        //获取接收到的字节数
        val length = dp.length
        val msg = String(data, 0, length)
        Log.e("receive", "msg:$msg $ip:$port")
        onReceive.invoke(ip, port, msg)
        ds.close()
    }

    @Throws(IOException::class)
    @JvmStatic
    fun receive(port: Int, onReceive: (data: String) -> Unit) {
        //创建数据包传输对象DatagramSocket 绑定端口号
        val ds = DatagramSocket(port)
        //创建字节数组
        val data = ByteArray(1024)
        //创建数据包对象，传递字节数组
        val dp = DatagramPacket(data, data.size)
        //调用ds对象的方法receive传递数据包
        Log.e("receive", "receive------------")
        ds.receive(dp)
        Log.e("receive", "receive=============")
        //获取发送端的IP地址对象
        val ip = dp.address.hostAddress

        //获取发送的端口号
        val port = dp.port

        //获取接收到的字节数
        val length = dp.length
        val msg = String(data, 0, length)
        Log.e("receive", "msg:$msg $ip:$port")
        onReceive.invoke(msg)
        ds.close()
        if (UdpSetting.sendBack) {
            send(ip, UdpSetting.sendBackPort, msg)
        }
        if (UdpSetting.getRTT) {
            try {
                if (msg.length == System.currentTimeMillis().toString().length) {
                    val time = System.currentTimeMillis() - msg.toLong()
                    UdpSetting.activity?.runOnUiThread {
                        UdpSetting.rttTv?.text = "来回耗时 $time ms"
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @JvmStatic
    fun send(ip: String, port: Int, data: String) {
        try {
            var sendData = data
            val mSocket = DatagramSocket()
            //发送
            val address: InetAddress = InetAddress.getByName(ip)
            val packet = DatagramPacket(
                sendData.toByteArray(),
                sendData.toByteArray().size,
                address,
                port
            )
            mSocket.send(packet)
            Log.e("send", "send success ip:$ip  port:$port sendData:$sendData")
        } catch (e: UnknownHostException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    @Throws(IOException::class)
    @JvmStatic
    fun receiveBroadcast(port: Int, onReceive: (data: String, ip: String) -> Unit) {
        //创建数据包传输对象DatagramSocket 绑定端口号
        val ds = DatagramSocket(port)
        //创建字节数组
        val data = ByteArray(1024)
        //创建数据包对象，传递字节数组
        val dp = DatagramPacket(data, data.size)
        //调用ds对象的方法receive传递数据包
        Log.e("receiveBroadcast", "receive------------")
        ds.receive(dp)
        Log.e("receiveBroadcast", "receive=============")
        //获取发送端的IP地址对象
        val ip = dp.address.hostAddress

        //获取发送的端口号
        val port = dp.port

        //获取接收到的字节数
        val length = dp.length
        val msg = String(data, 0, length)
        Log.e("receiveBroadcast", "msg:$msg $ip:$port")
        onReceive.invoke(msg, ip)
        ds.close()

    }
    const val debug = true
    fun log(msg: String) {
        if (!debug) return
        log("log", msg)
    }

    fun log(tag: String, msg: String) {
        if (!debug) return
        Log.e(tag, msg)
    }

}

