package com.example.a49479.cenbleoperationapp.bleScan;

import android.bluetooth.BluetoothDevice;

/**
 * Created by 49479 on 2018/4/23.
 */

public interface BleScannerCallback {
    void onStartScan();
    void onDeviceFound(BluetoothDevice device);
    void onStopScan();
    void onCancelScan();
}
