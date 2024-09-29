package com.yuruneji.camera_training2.domain.usecase

import okio.IOException
import org.apache.commons.net.ntp.NTPUDPClient
import timber.log.Timber
import java.net.InetAddress
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

/**
 * @author toru
 * @version 1.0
 */
class TimeSensor {

    companion object {
        /** NTPサーバ */
        private const val NTP_SERVER = "ntp.nict.jp"
        /** 比較時間（ミリ秒） */
        private const val CHECK_TIME = 60_000L
        /** フォーマット */
        private val formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS", Locale.JAPAN)
        // private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS", Locale.JAPAN)
        // private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.JAPAN)
    }

    fun checkTime(): Boolean {
        try {
            val client = NTPUDPClient()
            client.use {
                client.open()

                val host = InetAddress.getByName(NTP_SERVER)
                val info = client.getTime(host)

                info.computeDetails()
                val exactTime = Date(System.currentTimeMillis() + info.offset)
                Timber.d("正しい時刻 ${formatDate(exactTime)}")

                val packet = info.message
                Timber.d("[t1] クライアントがパケットを送信した時刻（Originate TimeStamp）${formatDate(packet.originateTimeStamp.date)}")
                Timber.d("[t2] NTPサーバーがパケットを受信した時刻（Receive TimeStamp）${formatDate(packet.receiveTimeStamp.date)}")
                Timber.d("[t3] NTPサーバーがパケットを送信した時刻（Transmit TimeStamp）${formatDate(packet.transmitTimeStamp.date)}")
                Timber.d("[t4] クライアントがパケットを受信した時刻（Return TimeStamp）${formatDate(Date(info.returnTime))}")
                Timber.d("往復にかかった時間（Roundtrip Time）${info.delay}ms")
                Timber.d("クライアントの時刻差（Offset）${info.offset}ms")

                var offset = info.offset
                if (0 > info.offset) {
                    offset = -offset
                }

                return offset < CHECK_TIME
            }
        } catch (e: Exception) {
            Timber.e(e)
            return false
        }
    }

    // private fun long2DateTime(l: Long): LocalDateTime {
    //     return LocalDateTime.ofInstant(Instant.ofEpochMilli(l), ZoneId.systemDefault())
    // }

    private fun date2DateTime(date: Date): LocalDateTime {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
    }

    // private fun formatDate(l: Long, formatter: DateTimeFormatter): String {
    //     return formatDate(long2DateTime(l), formatter)
    // }

    // private fun formatDate(date: Date, formatter: DateTimeFormatter): String {
    //     return formatDate(date2DateTime(date), formatter)
    // }

    // private fun formatDate(dateTime: LocalDateTime, formatter: DateTimeFormatter): String {
    //     return formatter.format(dateTime)
    // }

    // private fun formatDate(l: Long): String {
    //     return formatDate(long2DateTime(l))
    // }

    private fun formatDate(date: Date): String {
        return formatDate(date2DateTime(date))
    }

    private fun formatDate(dateTime: LocalDateTime): String {
        return formatter.format(dateTime)
    }
}
