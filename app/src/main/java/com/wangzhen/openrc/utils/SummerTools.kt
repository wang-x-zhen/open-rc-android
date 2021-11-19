package com.wangzhen.openrc.utils

import java.util.concurrent.Executors

object SummerTools {
    private val fixedThreadPool = lazy { Executors.newFixedThreadPool(5) }
    fun runOnIo(run: () -> Unit) {
        fixedThreadPool.value.execute {
            run.invoke()
        }
    }
}