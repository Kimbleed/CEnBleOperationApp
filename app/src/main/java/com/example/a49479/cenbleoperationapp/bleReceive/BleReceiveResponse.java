package com.example.a49479.cenbleoperationapp.bleReceive;

import android.bluetooth.BluetoothGattCharacteristic;

/**
 * Created by 49479 on 2018/5/2.
 */

public interface BleReceiveResponse {
    void onResponse(BluetoothGattCharacteristic characteristic);
}
