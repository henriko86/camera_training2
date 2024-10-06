package com.yuruneji.camera_training.common

import org.apache.commons.net.ntp.NTPUDPClient
import timber.log.Timber
import java.net.InetAddress

/**
 * @author toru
 * @version 1.0
 */
class TimeService {

    companion object {
        /** NTPサーバ */
        private const val NTP_SERVER = "ntp.nict.jp"

        /** 比較時間(ミリ秒) */
        private const val CHECK_TIME = 60_000L

        // /** フォーマット */
        // private val formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS", Locale.JAPAN)
        // private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS", Locale.JAPAN)
        // private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.JAPAN)

        /**
         * NTPサーバと時刻がズレていないか確認する
         * @param checkTime 比較時間(ミリ秒)
         * @param ntpServer NTPサーバ
         * @return true:時刻がズレていない, false:時刻がズレている
         */
        fun isTimeSync(checkTime: Long = CHECK_TIME, ntpServer: String = NTP_SERVER): Boolean {
            try {
                val client = NTPUDPClient()
                client.use {
                    client.open()

                    val host = InetAddress.getByName(ntpServer)
                    val info = client.getTime(host)

                    info.computeDetails()
                    // val exactTime = Date(System.currentTimeMillis() + info.offset)
                    // Timber.d("正しい時刻 ${formatDate(exactTime)}")

                    // val packet = info.message
                    // Timber.d("[t1] クライアントがパケットを送信した時刻（Originate TimeStamp）${formatDate(packet.originateTimeStamp.date)}")
                    // Timber.d("[t2] NTPサーバーがパケットを受信した時刻（Receive TimeStamp）${formatDate(packet.receiveTimeStamp.date)}")
                    // Timber.d("[t3] NTPサーバーがパケットを送信した時刻（Transmit TimeStamp）${formatDate(packet.transmitTimeStamp.date)}")
                    // Timber.d("[t4] クライアントがパケットを受信した時刻（Return TimeStamp）${formatDate(Date(info.returnTime))}")
                    // Timber.d("往復にかかった時間（Roundtrip Time）${info.delay}ms")
                    // Timber.d("クライアントの時刻差（Offset）${info.offset}ms")

                    var offset = info.offset
                    if (0 > info.offset) {
                        offset = -offset
                    }

                    return offset < checkTime
                }
            } catch (e: Exception) {
                Timber.e(e)
                return false
            }
        }

        // private fun formatDate(date: Date): String {
        //     return formatDate(date2DateTime(date))
        // }

        // private fun date2DateTime(date: Date): LocalDateTime {
        //     return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
        // }

        // private fun formatDate(dateTime: LocalDateTime): String {
        //     return formatter.format(dateTime)
        // }
    }
}
