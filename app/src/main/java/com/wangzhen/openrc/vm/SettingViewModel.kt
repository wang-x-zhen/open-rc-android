package com.wangzhen.openrc.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SettingViewModel : ViewModel() {
    var modelListChange = MutableLiveData<Boolean>()
}