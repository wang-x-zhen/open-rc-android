package com.wangzhen.openrc.data

import android.app.Application
import androidx.room.Room
import com.wangzhen.openrc.model.*

const val NULL_STR = "无"

object Data {
    const val DB_NAME = "database-esp-tx"
    var mInputSetting: InputSetting? = null
    var rxDeviceList = ArrayList<RxDevice>()
    var gpioList: ArrayList<GPIO> = ArrayList()
    var gpioStringListEsp8266: ArrayList<String> = arrayListOf(
        "GPIO 16",
        "GPIO 14",
        "GPIO 12",
        "GPIO 13",
        "GPIO 15",
        "GPIO 1",
        "GPIO 3",
        "GPIO 5",
        "GPIO 4",
        "GPIO 2",
    )
    val switchStringList: ArrayList<String> = arrayListOf(
        "SW1",
        "SW2",
        "SW3",
        "SW4",
        "SW5",
        "SW6",
    )
    var switchWithGpioList: ArrayList<Int> = arrayListOf()
    var switchOffOnList: ArrayList<String> = arrayListOf(
        "关",
        "开"
    )
    var switchShowList: ArrayList<Int> = arrayListOf(
        1,
        1,
        0,
        0,
        0,
        0,
    )
    var switchActiveList: ArrayList<Int> = arrayListOf(
        0,
        0,
        0,
        0,
        0,
        0,
    )
    var switch1ValueList: ArrayList<Int> = arrayListOf(
        0,
        0,
        0,
        0,
        0,
        0,
    )
    var switch2ValueList: ArrayList<Int> = arrayListOf(
        90,
        90,
        90,
        90,
        90,
        90,
    )
    var switch3ValueList: ArrayList<Int> = arrayListOf(
        180,
        180,
        180,
        180,
        180,
        180,
    )
    var inputList: ArrayList<Input> = arrayListOf()
    var inputInValidList: ArrayList<Input> = arrayListOf()
    var pwmList: ArrayList<Pwm> = ArrayList()
    var directionList: ArrayList<Direction> = ArrayList()
    var gpio2InputList: ArrayList<Int> = ArrayList()
    var gpio2PwmList: ArrayList<Int> = ArrayList()
    var gpio2DirectionList: ArrayList<Int> = ArrayList()
    var autoResetList: ArrayList<Int> = ArrayList()
    var inputScaleList: ArrayList<Int> = arrayListOf()

    init {
        inputInValidList.apply {
            add(Input().apply { name = NULL_STR })
        }
        autoResetList.apply {
            add(0)
            add(1)
            add(1)
            add(1)
        }
        inputList.apply {
            add(Input().apply { name = "Left  V" })
            add(Input().apply { name = "Left  H" })
            add(Input().apply { name = "Right V" })
            add(Input().apply { name = "Right H" })
        }.also {
            it.addAll(inputInValidList)
        }
        inputScaleList.apply {
            add(100)
            add(100)
            add(100)
            add(100)
        }

        gpioStringListEsp8266.forEachIndexed { pos, s ->
            gpioList.apply {
                add(GPIO().apply {
                    name = s
                    index = pos
                })
            }
        }
        gpioList.forEachIndexed { index, gpio ->
            val pos = if (index <= (inputList.size - 1)) {
                index
            } else {
                inputList.size - 1
            }
            gpio2InputList.add(pos)
        }

        pwmList.apply {
            add(Pwm().apply { name = "PWM 舵机" })
            add(Pwm().apply { name = "PWM 马达" })
            add(Pwm().apply { name = "双向 A" })
            add(Pwm().apply { name = "双向 B" })
        }
        directionList.apply {
            add(Direction().apply { name = DIRECT.R.v })
            add(Direction().apply { name = DIRECT.L.v })
        }

        gpioList.forEachIndexed { index, gpio ->
            if (index == 0) {
                gpio2PwmList.add(1) // 马达
            } else {
                gpio2PwmList.add(0) // 舵机
            }
        }
        gpioList.forEachIndexed { _, _ ->
            gpio2DirectionList.add(0)
        }
        switchStringList.forEachIndexed { _, _ ->
            switchWithGpioList.add(-1)
        }
    }

    fun loadSetting(inputSetting: InputSetting) {
        mInputSetting = inputSetting
        gpio2InputList = inputSetting.gpio2InputList
        gpio2PwmList = inputSetting.gpio2PwmList
        gpio2DirectionList = inputSetting.gpio2DirectionList
        autoResetList = inputSetting.autoResetList
        inputScaleList = inputSetting.inputScaleList
        switchWithGpioList = inputSetting.switchWithGpioList
        switchShowList = inputSetting.switchShowList
        switchActiveList = inputSetting.switchActiveList
        switch1ValueList = inputSetting.switch1ValueList
        switch2ValueList = inputSetting.switch2ValueList
        switch3ValueList = inputSetting.switch3ValueList
    }

    lateinit var db: AppDatabase
    fun dbInit(applicationContext: Application): AppDatabase {
        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, DB_NAME
        ).fallbackToDestructiveMigration().build()
        return db
    }

}