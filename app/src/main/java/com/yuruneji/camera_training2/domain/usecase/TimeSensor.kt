package com.yuruneji.camera_training2.domain.usecase

import org.apache.commons.net.ntp.NTPUDPClient
import timber.log.Timber
import java.net.InetAddress
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * @author toru
 * @version 1.0
 */
class TimeSensor {

    companion object {
        private const val NTP_SERVER = "ntp.nict.jp"
        private const val CHECK_TIME = 60_000L
        private val formater: SimpleDateFormat = SimpleDateFormat("HH:mm:ss.SSS", Locale.JAPAN)
    }

    fun checkTime(): Boolean {
        val client = NTPUDPClient()
        try {
            client.use {
                client.open()

                val host = InetAddress.getByName(NTP_SERVER)
                val info = client.getTime(host)

                info.computeDetails()
                val exactTime = Date(System.currentTimeMillis() + info.offset)
                Timber.i("正しい時刻 ${formater.format(exactTime)}")

                val packet = info.message
                Timber.i(
                    "[t1] クライアントがパケットを送信した時刻（Originate TimeStamp）${
                        formatDate(
                            packet.originateTimeStamp.date
                        )
                    }"
                )
                Timber.i(
                    "[t2] NTPサーバーがパケットを受信した時刻（Receive TimeStamp）${
                        formatDate(
                            packet.receiveTimeStamp.date
                        )
                    }"
                )
                Timber.i(
                    "[t3] NTPサーバーがパケットを送信した時刻（Transmit TimeStamp）${
                        formatDate(
                            packet.transmitTimeStamp.date
                        )
                    }"
                )
                Timber.i(
                    "[t4] クライアントがパケットを受信した時刻（Return TimeStamp）${
                        formatDate(
                            Date(
                                info.returnTime
                            )
                        )
                    }"
                )
                Timber.i("往復にかかった時間（Roundtrip Time）${info.delay}ms")
                Timber.i("クライアントの時刻差（Offset）${info.offset}ms")

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

    private fun formatDate(date: Date): String {
        return formater.format(date)
    }

}
