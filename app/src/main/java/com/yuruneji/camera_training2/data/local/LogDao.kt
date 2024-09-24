package com.yuruneji.camera_training2.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

/**
 * @author toru
 * @version 1.0
 */
@Dao
interface LogDao {
    @Query("SELECT * FROM log")
    fun getAll(): List<LogEntity>

    @Insert
    fun insert(log: LogEntity)
}
