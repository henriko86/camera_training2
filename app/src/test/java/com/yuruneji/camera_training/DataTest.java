package com.yuruneji.camera_training;

import com.yuruneji.camera_training.common.CommonUtil;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author toru
 * @version 1.0
 */
public class DataTest {

    @Test
    public void test1() {

        List<Byte> list = new ArrayList<Byte>();
        list.add((byte) 0x02); // ヘッダ
        list.add((byte) 0x00); // シーケンス番号
        list.add((byte) 0xA4); // コマンド
        list.add((byte) 0x31); // データ長
        list.add((byte) 0x31); // データ長
        list.add((byte) 0x41); // データ
        list.add((byte) 0x41); // データ
        list.add((byte) 0x30); // データ
        list.add((byte) 0x30); // データ
        list.add((byte) 0x20); // データ
        list.add((byte) 0x28); // データ
        list.add((byte) 0x44); // データ
        list.add((byte) 0x56); // データ
        list.add((byte) 0x32); // データ
        list.add((byte) 0x34); // データ
        list.add((byte) 0x2F); // データ
        list.add((byte) 0x30); // データ
        list.add((byte) 0x35); // データ
        list.add((byte) 0x2F); // データ
        list.add((byte) 0x31); // データ
        list.add((byte) 0x37); // データ
        list.add((byte) 0x29); // データ
        list.add((byte) 0x37); // Sum
        list.add((byte) 0x43); // Sum
        list.add((byte) 0x03); // フッタ

        List<String> hexList = new ArrayList<String>();
        for (byte b : list) {
            //			String hex = StringUtils.defaultString(Integer.toHexString(b), "0");
            String hex = String.format("%02x", b);
            int intValue = Integer.parseInt(hex, 16);
            //			System.out.println(b + "," + hex + "," + intValue);

            //			sum += intValue;
            hexList.add(hex);
        }

        List<Byte> dataSeqList = list.subList(1, list.size() - 3);
        String dataSeq = CommonUtil.INSTANCE.getDataChecksum(dataSeqList);
        System.out.println("データのシーケンス番号=" + dataSeq);

        List<Byte> dataSeqList2 = list.subList(5, list.size() - 3);
        String dataSeq2 = CommonUtil.INSTANCE.getDataChecksum((byte) 0x00, (byte) 0xA4, dataSeqList2);
        System.out.println("データのシーケンス番号2=" + dataSeq2);

        List<Byte> seqList = list.subList(list.size() - 3, list.size() - 1);
        String seq = CommonUtil.INSTANCE.getChecksum(seqList, "");
        System.out.println("シーケンス番号=" + seq);

        if (dataSeq.equals(seq)) {
            System.out.println("シーケンス番号一致");
        } else {
            System.out.println("シーケンス番号不一致");
        }

    }
    //
    // private  String ascii2String(List<Byte> list) {
    //     byte[] data = new byte[list.size()];
    //     if (list != null && !list.isEmpty()) {
    //         for (int i = 0; i < list.size(); i++) {
    //             data[i] = list.get(i);
    //         }
    //     }
    //     return ascii2String(data);
    // }
    //
    // private  String ascii2String(byte[] data) {
    //     try {
    //         return new String(data, "US-ASCII");
    //     } catch (UnsupportedEncodingException e) {
    //         e.printStackTrace();
    //         return null;
    //     }
    // }
}
