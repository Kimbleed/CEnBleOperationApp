package com.example.a49479.cenbleoperationapp;

import android.bluetooth.BluetoothProfile;

/**
 * Created by 49479 on 2018/4/28.
 */

public class BleCode {

    public static final int REQUEST_SUCCESS = 0x101;
    // 数据传输失败
    public static final int REQUEST_WRITE_FAILURE = 0x102;
    // Notify操作失败
    public static final int BLE_NOTIFY_FAILURE = 0x103;
    // 目标mac 与  当前mac不一致
    public static final int REQUEST_MAC_NO_MATCH = 0x104;

    public static String toString(int code) {
        switch (code){
            case REQUEST_SUCCESS:
                return "REQUEST_SUCCESS";
            case REQUEST_WRITE_FAILURE:
                return "REQUEST_WRITE_FAILURE";
            case BLE_NOTIFY_FAILURE:
                return "BLE_NOTIFY_FAILURE";
            case BluetoothProfile.STATE_CONNECTED:
                return "STATE_CONNECTED";
            case BluetoothProfile.STATE_CONNECTING:
                return "STATE_CONNECTING";
            case BluetoothProfile.STATE_DISCONNECTED:
                return "STATE_DISCONNECTED";
            case BluetoothProfile.STATE_DISCONNECTING:
                return "STATE_DISCONNECTING";
            case REQUEST_MAC_NO_MATCH:
                return "REQUEST_MAC_NO_MATCH";
        }
        return "nothing";
    }
}
