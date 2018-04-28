package com.example.a49479.cenbleoperationapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;

import com.example.a49479.cenbleoperationapp.bleConnect.BleConnectorImpl;
import com.example.a49479.cenbleoperationapp.bleConnect.IBleConnector;
import com.example.a49479.cenbleoperationapp.bleScan.BleScannerImpl;
import com.example.a49479.cenbleoperationapp.bleScan.IBleScanner;

/**
 * Created by 49479 on 2018/4/23.
 */

public class BleManagerConfig {
    Context mContext;
    BluetoothManager mBleManager;
    BluetoothAdapter mBleAdapter;
    IBleScanner mBleScanner;
    IBleConnector mBleConnector;
    boolean isRequestOpenBle = true;

    public BleManagerConfig(Context context) {
        this.mContext = context;
        mBleManager= (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        mBleAdapter = mBleManager.getAdapter();
        mBleScanner = new BleScannerImpl();
        mBleScanner.init(mBleAdapter);

        mBleConnector = new BleConnectorImpl();
        mBleConnector.init(mContext,mBleAdapter);
    }

    /**
     * 配置 蓝牙扫描器
     * @param scanner
     */
    public void setBleScanner(IBleScanner scanner){
        mBleScanner = scanner;
        mBleScanner.init(mBleAdapter);
    }

    /**
     * 配置 蓝牙连接器
     * @param connector
     */
    public void setBleConnector(IBleConnector connector){
        mBleConnector = connector;
        mBleConnector.init(mContext,mBleAdapter);
    }

    public void setRequestOpenBle(boolean flag){
        isRequestOpenBle = true;
    }


}
