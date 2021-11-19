package com.wangzhen.openrc.data

import androidx.room.TypeConverter
import java.util.ArrayList

class GenreConverter {
    @TypeConverter
    fun gettingListFromString(genreIds: String): ArrayList<Int> {
        val list = arrayListOf<Int>()

        val array = genreIds.split(",".toRegex()).dropLastWhile {
            it.isEmpty()
        }.toTypedArray()

        for (s in array) {
            if (s.isNotEmpty()) {
                list.add(s.toInt())
            }
        }
        return list
    }

    @TypeConverter
    fun writingStringFromList(list: ArrayList<Int>): String {
        var genreIds = ""
        for (i in list) genreIds += ",$i"
        return genreIds
    }
}