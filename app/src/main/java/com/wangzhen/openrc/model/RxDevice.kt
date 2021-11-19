package com.wangzhen.openrc.model

class RxDevice {
    var name: String = ""
    var ip: String = ""
    var refreshTimeMs: Long = 0L
    var adc: Int = 0
    var isSelect = false
    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        return name == (other as RxDevice).name && ip == other.ip
    }

    override fun toString(): String {
        return "name:$name,ip:$ip,refreshTimeMs:$refreshTimeMs,adc:$adc,"
    }
}