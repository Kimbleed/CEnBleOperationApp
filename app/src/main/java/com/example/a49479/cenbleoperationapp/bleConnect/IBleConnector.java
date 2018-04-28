package com.example.a49479.cenbleoperationapp.bleConnect;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.annotation.NonNull;

import java.util.UUID;

/**
 * Created by 49479 on 2018/4/24.
 */

public interface IBleConnector {

    void init(Context context, BluetoothAdapter bleAdapter);

    void connectGatt(String mac,BleConnectorCallback callback);

    void write(String mac,UUID serviceId,UUID characterId,byte[] data,BleWriteResponse response);

    void writeNoRsp(String mac,UUID serviceId,UUID characterId,byte[] data,BleWriteResponse response);

    void notify(String mac, UUID serviceId, UUID characterId, final BleNotifyResponse response);
}