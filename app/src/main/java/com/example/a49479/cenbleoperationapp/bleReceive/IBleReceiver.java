package com.example.a49479.cenbleoperationapp.bleReceive;

import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;

/**
 * Created by 49479 on 2018/5/2.
 */

public interface IBleReceiver {
    void init(Context context, Object lock);
    void receive(BleReceiveResponse response);
}
