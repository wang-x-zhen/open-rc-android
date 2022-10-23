package com.wangzhen.openrc.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wangzhen.openrc.data.Data
import com.wangzhen.openrc.data.InputSetting
import com.wangzhen.openrc.utils.SummerTools

const val TAG = "SettingViewModel"

class SettingViewModel : ViewModel() {
    var modelListChange = MutableLiveData<Long>()
    fun saveModel() {
        SummerTools.runOnIo {
            var modelName = ""
            Data.mInputSetting?.let {
                modelName = it.name
            }
            val inputSetting = InputSetting().apply {
                name = modelName
                time = System.currentTimeMillis()
                gpio2InputList = Data.gpio2InputList
                gpio2PwmList = Data.gpio2PwmList
                gpio2DirectionList = Data.gpio2DirectionList
                autoResetList = Data.autoResetList
                inputScaleList = Data.inputScaleList
                switchWithGpioList = Data.switchWithGpioList
                switchShowList = Data.switchShowList
                switchActiveList = Data.switchActiveList
                switch1ValueList = Data.switch1ValueList
                switch2ValueList = Data.switch2ValueList
                switch3ValueList = Data.switch3ValueList
            }
            Data.db.inputSettingDao().insertAll(inputSetting)
        }
    }
}