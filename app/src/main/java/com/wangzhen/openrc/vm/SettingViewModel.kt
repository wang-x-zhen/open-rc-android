package com.wangzhen.openrc.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket

const val TAG = "SettingViewModel"

class SettingViewModel : ViewModel() {
    var modelListChange = MutableLiveData<Long>()
}