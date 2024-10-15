package com.yuruneji.camera_training.domain.usecase

import android.content.Context
import com.yuruneji.camera_training.data.local.db.LogDao
import com.yuruneji.camera_training.data.local.db.LogEntity
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject

/**
 * @author toru
 * @version 1.0
 */
class LogFile @Inject constructor(
    private val logDao: LogDao
) {

    companion object {
        private val executor: ExecutorService = Executors.newSingleThreadExecutor()
        const val LOG_EXPIRED_DAY = 13
        private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
        private val fileNameFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    }

    fun postLog(context: Context, priority: Int, tag: String?, message: String, t: Throwable?) {
        createLogLine(priority, tag, message, t).let { msg ->
            executor.takeUnless { it.isShutdown }?.execute {
                flush(context, msg)
                insertTable(priority, tag, message, t)
            }
        }
    }

    private fun createLogLine(
        priority: Int,
        tag: String?,
        message: String,
        t: Throwable?,
        date: LocalDateTime = LocalDateTime.now()
    ): String {
        val dateStr = date.format(formatter)
        // val stackTraceStr = Log.getStackTraceString(t)
        return "$dateStr\t$priority\t$tag\t$message"
    }

    private fun insertTable(priority: Int, tag: String?, message: String, t: Throwable?) {
        logDao.insert(
            LogEntity(
                date = LocalDateTime.now(),
                priority = priority,
                tag = tag,
                message = message
            )
        )
    }

    private fun flush(context: Context, log: String) {
        val today = LocalDateTime.now()
        val fileName = "${today.format(fileNameFormatter)}.log"
        val file = File(context.filesDir, fileName)

        // まだ当日分のファイルが作成されていなかったら
        // 14日前までに作成されたファイルを削除する
        if (!file.exists()) deleteExpiredFiles(context)

        BufferedWriter(FileWriter(file, true)).use { writer ->
            writer.write(log)
            writer.newLine()
            writer.flush()
        }
    }

    private fun deleteExpiredFiles(context: Context) {
        context.filesDir
            .listFiles()
            ?.toList()
            ?.filter { file -> file.name.endsWith(".log") }
            ?.let { fileList ->
                fileList.map { file ->
                    if (expired(file) && file.exists()) file.delete()
                }
            }
    }

    private fun expired(file: File): Boolean {
        return toLocalDate(file.lastModified())
            .isBefore(LocalDate.now().minusDays(LOG_EXPIRED_DAY.toLong()))
    }

    private fun toLocalDate(lastModified: Long): LocalDate {
        return Instant
            .ofEpochMilli(lastModified)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
    }
}
