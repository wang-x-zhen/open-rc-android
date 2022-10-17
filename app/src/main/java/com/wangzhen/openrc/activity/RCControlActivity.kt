package com.wangzhen.openrc.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.joanzapata.iconify.fonts.SimpleLineIconsIcons
import com.wangzhen.openrc.R
import com.wangzhen.openrc.activity.ControlPWM.offSetCoefficient
import com.wangzhen.openrc.activity.ControlPWM.offSetLeftH
import com.wangzhen.openrc.activity.ControlPWM.offSetLeftV
import com.wangzhen.openrc.activity.ControlPWM.offSetRightH
import com.wangzhen.openrc.activity.ControlPWM.offSetRightV
import com.wangzhen.openrc.data.Data
import com.wangzhen.openrc.model.CmdData
import com.wangzhen.openrc.model.DIRECT
import com.wangzhen.openrc.model.Input
import com.wangzhen.openrc.model.RxDevice
import com.wangzhen.openrc.setting.SettingActivity
import com.wangzhen.openrc.utils.UdpUtils
import com.wangzhen.openrc.view.JoystickView
import com.wangzhen.openrc.view.OffSetView
import com.wangzhen.openrc.view.OffSetViewV
import com.wangzhen.openrc.vm.TcpRepo
import kotlinx.android.synthetic.main.activity_r_c_control.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONArray
import org.json.JSONObject
import org.koin.android.ext.android.inject
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.concurrent.thread
import kotlin.math.max
import kotlin.math.min


/**
 *
 * unsigned char motorPin = 16;
 * unsigned char servo1Pin = 14;
 * unsigned char servo2Pin = 12;
 * unsigned char servo3Pin = 13;
 */
object ControlPWM {
    var leftV = 0
    var leftH = 90
    var rightH = 90
    var rightV = 90

    var leftVRaw = 0
    var leftHRaw = 1000
    var rightVRaw = 1000
    var rightHRaw = 1000
    var offSetLeftH = 0
    var offSetLeftV = 0
    var offSetRightH = 0
    var offSetRightV = 0
    var offSetCoefficient = 5
    var isValid = false
}

object Rx {
    var port = 12345
    var ipSendBroadcastPort = 18888
}

class RCControlActivity : AppCompatActivity() {
    var threadUdpListen: Thread? = null
    var udpReceiverIpListenPort: Int = 5678
    var receiverIp = ""
    private val tcpRepo: TcpRepo by inject()
    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: RxDevice) {
        updateDevice()
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_r_c_control)
        threadUdpListen = Thread {
            while (true) {
                try {
                    UdpUtils.receive(udpReceiverIpListenPort) { ip, _, _ ->
                        receiverIp = ip
                    }
                } catch (e: java.lang.Exception) {

                }
            }
        }
        threadUdpListen?.start()
        joystickViewLeft.setAutoBackX(true)
        joystickViewLeft.setXYChange(object : JoystickView.XYChange {
            override fun onXYChange(x: Int, y: Int) {
                ControlPWM.leftVRaw = y
                ControlPWM.leftHRaw = x
                ControlPWM.isValid = true
            }
        })
        joystickViewRight.setAutoBackX(true)
        joystickViewRight.setAutoBackY(true)
        joystickViewRight.setXYChange(object : JoystickView.XYChange {
            override fun onXYChange(x: Int, y: Int) {
                ControlPWM.rightHRaw = x
                ControlPWM.rightVRaw = y
                ControlPWM.isValid = true
            }
        })
        offSetViewLeftH.setOffsetChange(object : OffSetView.OffsetChange {
            override fun onOffsetChange(x: Int) {
                offSetViewLeftHTv.text = offSetLeftH.toString()
                offSetLeftH += x / offSetCoefficient
                ControlPWM.isValid = true
            }
        })
        offSetViewLeftV.setOffsetChange(object : OffSetViewV.OffsetChange {
            override fun onOffsetChange(y: Int) {
                offSetViewLeftVTv.text = offSetLeftV.toString()
                offSetLeftV += y / offSetCoefficient
                ControlPWM.isValid = true
            }
        })
        offSetViewRightH.setOffsetChange(object : OffSetView.OffsetChange {
            override fun onOffsetChange(x: Int) {
                offSetViewRightHTv.text = offSetRightH.toString()
                offSetRightH += x / offSetCoefficient
                ControlPWM.isValid = true
            }
        })
        offSetViewRightV.setOffsetChange(object : OffSetViewV.OffsetChange {
            override fun onOffsetChange(y: Int) {
                offSetViewRightVTv.text = offSetRightV.toString()
                offSetRightV += y / offSetCoefficient
                ControlPWM.isValid = true
            }
        })
        try {
            receiveBroadcast(Rx.ipSendBroadcastPort)
        } catch (e: Exception) {
            e.printStackTrace()
        }


        settingIcon.also { iconTextView ->
            iconTextView.text = "{${SimpleLineIconsIcons.icon_settings.key()}}"
        }.setOnClickListener {
            startActivity(Intent(this, SettingActivity::class.java))
        }
        device_tv.setOnClickListener {
            startActivity(Intent(this, SettingActivity::class.java))
        }
        sendThread()
    }

    private fun sendThread() {
        thread {
            while (true) {
                Thread.sleep(30)
                send()
            }
        }
    }

    private fun setAutoReset() {
        joystickViewLeft.setAutoBackY(Data.autoResetList[0] == 1)
        joystickViewLeft.setAutoBackX(Data.autoResetList[1] == 1)
        joystickViewRight.setAutoBackY(Data.autoResetList[2] == 1)
        joystickViewRight.setAutoBackX(Data.autoResetList[3] == 1)
    }

    override fun onResume() {
        super.onResume()
        updateDevice()
        setAutoReset()
    }

    private fun updateDevice() {
        Data.rxDeviceList.filter { it.isSelect }.takeIf { it.isNullOrEmpty() }?.let {
            device_tv.text = getString(R.string.recv_not_select)
            return
        }
        Data.rxDeviceList.filter { it.isSelect }.let { list ->
            list.joinToString(separator = "\n") {
                it.name + " 电量:${it.adc} mV"
            }.takeIf { it.isNotEmpty() }?.let {
                device_tv.text = it
            }
        }
    }


    var oldData = ""
    var newData = ""
    var resendCount = 0
    var resendCountMax = 1

    private fun send() {
        val port = Rx.port
        if (Data.rxDeviceList.filter { it.isSelect }.isNullOrEmpty()) {
            ControlPWM.toData()
            return
        }
        if (!ControlPWM.isValid) {
            return
        }
        newData = ControlPWM.toData()
        if (oldData == newData) {
            if (resendCount > resendCountMax) {
                return
            }
            resendCount++
        } else {
            resendCount = 0
        }
        oldData = newData
        Data.rxDeviceList.filter { it.isSelect }.forEach { device ->
            Log.e("--send json", "sendData:$newData")
            try {
//                    UdpUtils.send(device.ip, port, ControlPWM.toByteData())
                tcpRepo.send(ControlPWM.toByteData())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    var threadReceiveBroadcast: Thread? = null
    private fun receiveBroadcast(port: Int) {
        if (threadReceiveBroadcast != null) {
            threadReceiveBroadcast?.interrupt()
            threadReceiveBroadcast?.start()
        } else {
            threadReceiveBroadcast = Thread {
                try {
                    while (true) {
                        UdpUtils.receiveBroadcast(port) { msg, _ip ->
                            // 从接收机接收到的格式 EspRcRx9453113,ADC:4110,Version:1.0.3
                            msg.split(",")
                            RxDevice().apply {
                                ip = _ip
                                name = msg.split(",")[0]
                                adc = msg.split(",")[1].split("ADC:")[1].toInt()
                                version = msg.split(",")[2].split("Version:")[1]
                                refreshTimeMs = System.currentTimeMillis()
                            }.also {
                                if (Data.rxDeviceList.contains(it)) {
                                    Data.rxDeviceList.firstOrNull { old -> old == it }
                                        ?.let { old -> it.isSelect = old.isSelect }
                                    Data.rxDeviceList.remove(it)
                                }
                                Data.rxDeviceList.add(it)
                                EventBus.getDefault().post(it)
                                UdpUtils.log("-----rxDevice:${Data.rxDeviceList}")
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            threadReceiveBroadcast?.start()
        }
    }
}

fun ControlPWM.toData(): String {
    if (!isValid) {
        return ""
    }
    leftV = (leftVRaw + offSetLeftV) * 180 / 2000
    leftH = (leftHRaw + offSetLeftH) * 180 / 2000
    rightV = (rightVRaw + offSetRightV) * 180 / 2000
    rightH = 180 - (rightHRaw + offSetRightH) * 180 / 2000
    leftV = max(0, leftV)
    leftV = min(leftV, 180)
    rightV = max(0, rightV)
    rightV = min(rightV, 180)
    rightH = max(0, rightH)
    rightH = min(rightH, 180)
    leftH = max(0, leftH)
    leftH = min(leftH, 180)

    var inputAndValue = HashMap<Input, Int>().apply {
        put(Data.inputList[0], leftV)
        put(Data.inputList[1], leftH)
        put(Data.inputList[2], rightV)
        put(Data.inputList[3], rightH)
    }


    return JSONArray().apply {
        Data.gpioList.forEachIndexed { pos, gpio ->
            Data.gpio2InputList[pos].takeIf { it >= 0 && it < (Data.inputList.size - 1) }
                ?.let {
                    val cmdData = CmdData()
                    val direction = Data.directionList[Data.gpio2DirectionList[pos]]
                    val v = if (direction.name == DIRECT.R.v) {
                        inputAndValue[Data.inputList[it]]!!
                    } else {
                        180 - inputAndValue[Data.inputList[it]]!!
                    }
                    cmdData.gpio = gpio.name.replace("GPIO ", "").toInt()
                    cmdData.value = v
                    cmdData.pwmMode = Data.gpio2PwmList[gpio.index]
                    if (Data.pwmList[Data.gpio2PwmList[gpio.index]].name.contains("IA")) {
                        if (v - 90 < 0) {
                            cmdData.pwmMode = 2 // GND
                            cmdData.value = 0
                        } else if (v == 90) {
                            cmdData.pwmMode = 2
                            cmdData.value = 0
                        } else {
                            cmdData.pwmMode = 1 // PWM 直接驱动
                            cmdData.value = (v - 90) * 2
                        }
                    } else if (Data.pwmList[Data.gpio2PwmList[gpio.index]].name.contains("IB")) {
                        if (v - 90 < 0) {
                            cmdData.pwmMode = 1 // PWM 直接驱动
                            cmdData.value = -1 * (v - 90) * 2
                        } else if (v == 90) {
                            cmdData.pwmMode = 2
                            cmdData.value = 0
                        } else {
                            cmdData.pwmMode = 2 // GND
                            cmdData.value = 0
                        }
                    }
                    val jsonObject = JSONObject()
                    jsonObject.put("g", cmdData.gpio)
                    jsonObject.put("v", cmdData.value)
                    jsonObject.put("p", cmdData.pwmMode)
                    this.put(jsonObject)
                }
        }
    }.toString()
}

var byteList = CopyOnWriteArrayList<Byte>()
fun ControlPWM.toByteData(): ByteArray {
    if (!isValid) {
        return ByteArray(0);
    }
    leftV = (leftVRaw + offSetLeftV) * 180 / 2000
    leftH = (leftHRaw + offSetLeftH) * 180 / 2000
    rightV = (rightVRaw + offSetRightV) * 180 / 2000
    rightH = 180 - (rightHRaw + offSetRightH) * 180 / 2000
    leftV = max(0, leftV)
    leftV = min(leftV, 180)
    rightV = max(0, rightV)
    rightV = min(rightV, 180)
    rightH = max(0, rightH)
    rightH = min(rightH, 180)
    leftH = max(0, leftH)
    leftH = min(leftH, 180)

    var inputAndValue = HashMap<Input, Int>().apply {
        put(Data.inputList[0], leftV)
        put(Data.inputList[1], leftH)
        put(Data.inputList[2], rightV)
        put(Data.inputList[3], rightH)
    }
    byteList.clear()
    Data.gpioList.forEachIndexed { pos, gpio ->
        Data.gpio2InputList[pos].takeIf { it >= 0 && it < (Data.inputList.size - 1) }
            ?.let {
                val cmdData = CmdData()
                val direction = Data.directionList[Data.gpio2DirectionList[pos]]
                val v = if (direction.name == DIRECT.R.v) {
                    inputAndValue[Data.inputList[it]]!!
                } else {
                    180 - inputAndValue[Data.inputList[it]]!!
                }
                cmdData.gpio = gpio.name.replace("GPIO ", "").toInt()
                cmdData.value = v
                cmdData.pwmMode = Data.gpio2PwmList[gpio.index]
                if (Data.pwmList[Data.gpio2PwmList[gpio.index]].name.contains("IA")) {
                    if (v - 90 < 0) {
                        cmdData.pwmMode = 2 // GND
                        cmdData.value = 0
                    } else if (v == 90) {
                        cmdData.pwmMode = 2
                        cmdData.value = 0
                    } else {
                        cmdData.pwmMode = 1 // PWM 直接驱动
                        cmdData.value = (v - 90) * 2
                    }
                } else if (Data.pwmList[Data.gpio2PwmList[gpio.index]].name.contains("IB")) {
                    if (v - 90 < 0) {
                        cmdData.pwmMode = 1 // PWM 直接驱动
                        cmdData.value = -1 * (v - 90) * 2
                    } else if (v == 90) {
                        cmdData.pwmMode = 2
                        cmdData.value = 0
                    } else {
                        cmdData.pwmMode = 2 // GND
                        cmdData.value = 0
                    }
                }
                byteList.add(cmdData.gpio.toByte())
                byteList.add(cmdData.value.toByte())
                byteList.add(cmdData.pwmMode.toByte())
            }
    }
    byteList.add(200.toByte())
    byteList.add(201.toByte())
    return byteList.toByteArray()
}