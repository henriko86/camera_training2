package com.yuruneji.camera_training2.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

/**
 * @author toru
 * @version 1.0
 */
@Dao
interface LogDao {
    @Query("SELECT * FROM log")
    fun getAll(): Flow<List<LogEntity>>

    @Query("SELECT * FROM log WHERE date(date) = date(:date)")
    fun get(date: LocalDateTime): Flow<List<LogEntity>>

    // @Query("SELECT * FROM log WHERE date(date) >= date(:from) AND date(date) <= date(:to)")
    // fun get(from: LocalDateTime, to: LocalDateTime): Flow<List<LogEntity>>

    @Insert
    fun insert(log: LogEntity)

    @Delete
    fun delete(log: LogEntity)

    @Update
    fun update(log: LogEntity)
}
