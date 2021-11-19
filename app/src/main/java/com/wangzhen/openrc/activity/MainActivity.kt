package com.wangzhen.openrc.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.wangzhen.openrc.R
import com.wangzhen.openrc.utils.NetUtils
import com.wangzhen.openrc.utils.UdpUtils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.msg_et
import kotlinx.android.synthetic.main.activity_main.port_et
import kotlinx.android.synthetic.main.activity_main.send
import kotlinx.android.synthetic.main.activity_main.send_back
import kotlinx.android.synthetic.main.activity_tcp.*
import org.json.JSONObject
import java.net.DatagramPacket
import java.net.DatagramSocket

object Command {
    var udp_address: String = "10.2.8.163"
    var udp_port: Int = 60000
}

object UdpSetting {
    // 收到信息自动原文回复
    var sendBack: Boolean = false
    var sendBackPort: Int = 22222

    // 计算RTT
    var getRTT: Boolean = false
    var activity: Activity? = null
    var rttTv: TextView? = null
}

object UdpSocket {
    lateinit var mSocket: DatagramSocket
    val sendPacket: DatagramPacket? = null
    val receivePacket: DatagramPacket? = null
    val messageQueue = ArrayList<String>()
}

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val ip = NetUtils.getIP(this)
        ip_tv.text = "ip:$ip BroadcastAddress:${NetUtils.getBroadcastAddress(this).hostAddress}"
        ip_et.setText(ip)
        UdpSetting.activity = this
        UdpSetting.rttTv = rtt_tv
        goTcp.setOnClickListener {
            startActivity(Intent(this, TcpActivity::class.java))
        }

        send.setOnClickListener {
            val ip = ip_et.text.toString()
            val port = port_et.text.toString().toInt()
            val msg = msg_et.text.toString()
            Thread {
                UdpUtils.send(ip, port, msg)
            }.start()
        }
        sendBroadcast.setOnClickListener {
            val ip = NetUtils.getBroadcastAddress(this@MainActivity).hostAddress
            val port = port_et.text.toString().toInt()
            val msg = msg_et.text.toString()
            Thread {
                UdpUtils.send(ip, port, msg)
            }.start()
        }
        get_rtt.setOnCheckedChangeListener { _, b ->
            UdpSetting.getRTT = b
        }
        sb_servo.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val ip = NetUtils.getBroadcastAddress(this@MainActivity).hostAddress
                val port = port_et.text.toString().toInt()
                val msg = msg_et.text.toString()
                val j = JSONObject()
                j.put("servo", progress)
                j.put("led", sb_led.progress)
                j.put("msg", "haha")
                Thread {
                    UdpUtils.send(ip, port, j.toString())
                }.start()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })

        sb_led.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val ip = NetUtils.getBroadcastAddress(this@MainActivity).hostAddress
                val port = port_et.text.toString().toInt()
                val msg = msg_et.text.toString()
                val j = JSONObject()
                j.put("servo", sb_servo.progress)
                j.put("led", progress)
                j.put("msg", "haha")
                Thread {
                    UdpUtils.send(ip, port, j.toString())
                }.start()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })
        broadcast_rx_bt.setOnClickListener {
            if (!broadcast_rx_bt.text.contains(".")) {
                return@setOnClickListener
            }
            ip_et.setText(broadcast_rx_bt.text)
        }

        bt_led_on.setOnClickListener {
            val ip = ip_et.text.toString()
            val port = port_et.text.toString().toInt()
            val msg = "Turn on"
            Thread {
                UdpUtils.send(ip, port, msg)
            }.start()
        }
        bt_led_off.setOnClickListener {
            val ip = ip_et.text.toString()
            val port = port_et.text.toString().toInt()
            val msg = "Turn off"
            Thread {
                UdpUtils.send(ip, port, msg)
            }.start()
        }
        sendBroadcast.setOnClickListener {
            val ip = NetUtils.getBroadcastAddress(this@MainActivity).hostAddress
            val port = port_et.text.toString().toInt()
            val msg = msg_et.text.toString()
            Thread {
                UdpUtils.send(ip, port, msg)
            }.start()
        }
        send_back.setOnCheckedChangeListener { _, b ->
            UdpSetting.sendBack = b
        }

        recv.setOnClickListener {
            val port = rec_port_et.text.toString().toInt()
            UdpSetting.sendBackPort = port
            receive(port)
            recv.isEnabled = false
        }
        receiveBroadcast(18888)
    }

    override fun onDestroy() {
        super.onDestroy()
        UdpSetting.activity = null
        UdpSetting.rttTv = null
        UdpSetting.getRTT = false
        UdpSetting.sendBack = false
    }

    var threadReceive: Thread? = null
    private fun receive(port: Int) {
        if (threadReceive != null) {
            threadReceive?.interrupt()
            threadReceive?.start()
        } else {
            threadReceive = Thread {
                while (true) {
                    UdpUtils.receive(port) { msg ->
                        runOnUiThread {
                            rcv_tv.text = msg
                        }
                    }
                }
            }
            threadReceive?.start()
        }
    }

    var threadReceiveBroadcast: Thread? = null
    private fun receiveBroadcast(port: Int) {
        if (threadReceiveBroadcast != null) {
            threadReceiveBroadcast?.interrupt()
            threadReceiveBroadcast?.start()
        } else {
            threadReceiveBroadcast = Thread {
                while (true) {
                    UdpUtils.receiveBroadcast(port) { msg, _ ->
                        runOnUiThread {
                            broadcast_rx_bt.text = msg
                        }
                    }
                }
            }
            threadReceiveBroadcast?.start()
        }
    }

}




