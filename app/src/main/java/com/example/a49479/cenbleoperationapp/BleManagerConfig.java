package com.example.a49479.cenbleoperationapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;

import com.example.a49479.cenbleoperationapp.bleConnect.BleConnectorImpl;
import com.example.a49479.cenbleoperationapp.bleConnect.IBleConnector;
import com.example.a49479.cenbleoperationapp.bleReceive.IBleReceiver;
import com.example.a49479.cenbleoperationapp.bleScan.BleScannerImpl;
import com.example.a49479.cenbleoperationapp.bleScan.IBleScanner;
import com.example.a49479.cenbleoperationapp.bleSend.BleSenderImpl;
import com.example.a49479.cenbleoperationapp.bleSend.IBleSender;

/**
 * Created by 49479 on 2018/4/23.
 */

public class BleManagerConfig {
    Context mContext;
    Object mLock;
    BluetoothManager mBleManager;
    BluetoothAdapter mBleAdapter;
    IBleScanner mBleScanner;
    IBleConnector mBleConnector;
    IBleSender mBleSender;
    IBleReceiver mBleReceiver;
    boolean isRequestOpenBle = true;

    public BleManagerConfig(Context context,Object lock) {
        this.mContext = context;
        mLock = lock;

        mBleManager= (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        mBleAdapter = mBleManager.getAdapter();

        //蓝牙扫描管理类(蓝牙扫描器)
        mBleScanner = new BleScannerImpl();
        mBleScanner.init(mBleAdapter);

        //蓝牙连接管理类(蓝牙连接器)
        mBleConnector = new BleConnectorImpl();
        mBleConnector.init(mContext,mBleAdapter);

        //蓝牙数据发送管理类(蓝牙数据发送器)
        mBleSender = new BleSenderImpl();
        mBleSender .init(mBleConnector,mLock);

        //蓝牙数据接收管理类(蓝牙数据接收器)


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
