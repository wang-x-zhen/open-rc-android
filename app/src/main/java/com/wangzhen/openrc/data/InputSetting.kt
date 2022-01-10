package com.wangzhen.openrc.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

const val TABLE_SETTING = "InputSettingTab"

@Entity(tableName = TABLE_SETTING)
class InputSetting {
    @PrimaryKey
    var name = ""
    var time: Long = 0
    var gpio2InputList: ArrayList<Int> = ArrayList()
    var gpio2PwmList: ArrayList<Int> = ArrayList()
    var gpio2DirectionList: ArrayList<Int> = ArrayList()
    var autoResetList: ArrayList<Int> = ArrayList() // 自动会中
    var inputScaleList: ArrayList<Int> = ArrayList()
}