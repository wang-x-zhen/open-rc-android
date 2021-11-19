package com.wangzhen.openrc.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [InputSetting::class], version = 2, exportSchema = false)
@TypeConverters(GenreConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun inputSettingDao(): InputSettingDao
}