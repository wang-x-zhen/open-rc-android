package com.wangzhen.openrc.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface InputSettingDao {
    @Query("SELECT * FROM InputSettingTab ORDER BY time DESC")
    fun getAll(): List<InputSetting>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg inputSetting: InputSetting)

    @Query("DELETE FROM InputSettingTab WHERE name = :name")
    fun deleteByName(name: String)
}