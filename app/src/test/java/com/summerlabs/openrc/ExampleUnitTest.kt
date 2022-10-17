package com.summerlabs.openrc

import org.junit.Test
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        var x: Int = 100;
        println("100 to byte:${x.toByte()}")
        var x2: Int = 130;
        println("130 to byte:${x2.toByte()}")
        var byteList = CopyOnWriteArrayList<Byte>()
        byteList.add(x.toByte())
        byteList.add(x2.toByte())
        println("byteList to byte:${byteList.toByteArray().toIntString()}")
    }


}
fun ByteArray.toIntString() = this.joinToString("") {
    (it.toInt() and 0xFF).toString(10) + " "
}