package com.example.a49479.cenbleoperationapp.bleSend;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by 49479 on 2018/5/2.
 */

public class BleDataPackage implements Serializable{
    String mac;         // 蓝牙设备地址
    UUID serviceId;     // service id
    UUID characterId;   // characteristic id
    byte[] data;        // 蓝牙数据包
    int mtu;            // 一次写入的数组长度
    int offSet;         // 偏移量

    public BleDataPackage(String mac, UUID serviceId, UUID characterId, byte[] data, int mtu, int offSet) {
        this.mac = mac;
        this.serviceId = serviceId;
        this.characterId = characterId;
        this.data = data;
        this.mtu = mtu;
        this.offSet = offSet;
    }
}
