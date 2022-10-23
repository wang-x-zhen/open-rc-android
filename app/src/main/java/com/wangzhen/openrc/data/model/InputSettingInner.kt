package com.wangzhen.openrc.data.model

import com.wangzhen.openrc.data.InputSetting

object InputSettingInner {
    var OpenRcMini = InputSetting().apply {
        name = "OpenRc Mini"
        time = 0
        gpio2InputList = arrayListOf(2, 2, 0, 0, 3, 4, 4, 4, 3, 4)//2,2,0,0,3,4,4,4,3,4
        gpio2PwmList = arrayListOf(2, 3, 2, 3, 0, 0, 0, 0, 0, 0)//2,3,2,3,0,0,0,0,0,0
        gpio2DirectionList = arrayListOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0)//0,0,0,0,0,0,0,0,0,0
        autoResetList = arrayListOf(1, 1, 1, 1) // 1,1,1,1
        inputScaleList = arrayListOf(100, 100, 100, 100) //100,100,100,100
        switchWithGpioList = arrayListOf(9, -1, -1, -1, -1, -1) //,9,-1,-1,-1,-1,-1
        switchShowList = arrayListOf(1, 0, 0, 0, 0, 1)//,1,0,0,0,0,1
        switchActiveList = arrayListOf(1, 0, 0, 0, 0, 0)//1,0,0,0,0,0
        switch1ValueList = arrayListOf(0, 0, 0, 0, 0, 0)//0,0,0,0,0,0
        switch2ValueList = arrayListOf(90, 90, 90, 90, 90, 90)//90,90,90,90,90,90
        switch3ValueList = arrayListOf(180, 180, 180, 180, 180, 180)//180,180,180,180,180,180
    }
}

