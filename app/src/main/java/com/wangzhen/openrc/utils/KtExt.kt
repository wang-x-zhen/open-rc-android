package com.wangzhen.openrc

import android.content.Context
import android.view.Gravity
import android.widget.Toast

fun Context?.toast(msg: String) {
    if (this == null) return
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).apply {
        setGravity(Gravity.CENTER, 0, 0)
    }.show()
}