package com.yuruneji.cameratraining2.data.local

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
    fun getAll(): List<Log>

    @Insert
    fun insert(log: Log)
}
