package com.wangzhen.openrc.utils

import android.content.Context
import android.net.DhcpInfo
import android.net.wifi.WifiManager
import java.lang.reflect.InvocationTargetException
import java.net.Inet4Address
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.SocketException

/**
 * @author w小z
 * @since : 2021/4/9
 */
object NetUtils {
    fun getIP(context: Context?): String? {
        try {
            val en = NetworkInterface.getNetworkInterfaces()
            while (en.hasMoreElements()) {
                val intf = en.nextElement()
                val enumIpAddr = intf.inetAddresses
                while (enumIpAddr.hasMoreElements()) {
                    val inetAddress = enumIpAddr.nextElement()
                    if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                        return inetAddress.getHostAddress().toString()
                    }
                }
            }
        } catch (ex: SocketException) {
            ex.printStackTrace()
        }
        return null
    }
    fun getBroadcastAddress(context: Context): InetAddress {
        if (isWifiApEnabled(context)) { //判断wifi热点是否打开
            return InetAddress.getByName("192.168.43.255")  //直接返回
        }
        val wifi: WifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val dhcp: DhcpInfo = wifi.dhcpInfo ?: return InetAddress.getByName("255.255.255.255")
        val broadcast = (dhcp.ipAddress and dhcp.netmask) or dhcp.netmask.inv()
        val quads = ByteArray(4)
        for (k in 0..3) {
            quads[k] = ((broadcast shr k * 8) and 0xFF).toByte()
        }
        return InetAddress.getByAddress(quads)
    }

    /**
     * check whether the wifiAp is Enable
     */
    private fun isWifiApEnabled(context: Context): Boolean {
        try {
            val manager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val method = manager.javaClass.getMethod("isWifiApEnabled")
            return method.invoke(manager) as Boolean
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        }
        return false
    }

}