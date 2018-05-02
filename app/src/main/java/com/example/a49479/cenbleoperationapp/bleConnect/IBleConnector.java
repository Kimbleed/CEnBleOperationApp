package com.example.a49479.cenbleoperationapp.bleConnect;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;

import java.util.UUID;

/**
 * Created by 49479 on 2018/4/24.
 *
 * 蓝牙连接器接口
 * 处理连接工作和写数据工作
 */

public interface IBleConnector {

    void init(Context context, BluetoothAdapter bleAdapter);

    boolean isConnectGatt(String mac);

    void connectGatt(String mac,BleConnectorResponse callback);

    void write(String mac,UUID serviceId,UUID characterId,byte[] data,BleWriteResponse response);

    void writeNoRsp(String mac,UUID serviceId,UUID characterId,byte[] data,BleWriteResponse response);

    void notify(String mac, UUID serviceId, UUID characterId, final BleNotifyResponse response);

    String getConnectGattMac();
}
