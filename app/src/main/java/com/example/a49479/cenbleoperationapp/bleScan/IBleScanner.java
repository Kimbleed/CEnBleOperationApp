package com.example.a49479.cenbleoperationapp.bleScan;

import android.bluetooth.BluetoothAdapter;
import android.support.annotation.NonNull;

/**
 * Created by 49479 on 2018/4/23.
 *
 * 蓝牙扫描器接口
 * 处理扫描工作
 *
 */

public interface IBleScanner  {
    void init(BluetoothAdapter bleAdapter);
    void startLeScan(long mills,@NonNull BleScannerCallback callback);
    void cancelLeScan();

}
