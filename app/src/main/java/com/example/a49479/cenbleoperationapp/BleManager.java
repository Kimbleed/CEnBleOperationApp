package com.example.a49479.cenbleoperationapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.a49479.cenbleoperationapp.bleConnect.BleConnectorResponse;
import com.example.a49479.cenbleoperationapp.bleConnect.BleWriteResponse;
import com.example.a49479.cenbleoperationapp.bleConnect.IBleConnector;
import com.example.a49479.cenbleoperationapp.bleReceive.BleReceiveResponse;
import com.example.a49479.cenbleoperationapp.bleReceive.IBleReceiver;
import com.example.a49479.cenbleoperationapp.bleScan.BleScannerCallback;
import com.example.a49479.cenbleoperationapp.bleScan.IBleScanner;
import com.example.a49479.cenbleoperationapp.bleSend.BleDataPackage;
import com.example.a49479.cenbleoperationapp.bleSend.IBleSender;

import java.util.UUID;

/**
 * Created by 49479 on 2018/4/23.
 */

public class BleManager {

    private static final String TAG = "BleManager";

    private Context mContext;

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BleManagerConfig mBleManagerConfig;

    private IBleScanner mBleScanner;
    private IBleConnector mBleConnector;
    private IBleSender mBleSender;
    private IBleReceiver mBleReceiver;

    private boolean isInit;//是否已经初始化

    private BleScannerCallback mBleScannerCallback;
    private boolean isInScanning;

    private boolean mRequestCompleted;

    private BleManager() {

    }

    private static class BleManagerHolder {
        private static final BleManager sInstance = new BleManager();
    }

    public static BleManager getInstance() {
        return BleManagerHolder.sInstance;
    }

    public void init(BleManagerConfig config) {
        mBleManagerConfig = config;
        mContext = mBleManagerConfig.mContext;

        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                return;
            }
        }
        mBluetoothAdapter = config.mBleAdapter;

        if(mBleManagerConfig.isRequestOpenBle)
            mBluetoothAdapter.isEnabled();

        mBleScanner = config.mBleScanner;

        mBleConnector = config.mBleConnector;

        mBleSender = config.mBleSender;

        mBleReceiver = config.mBleReceiver;

        isInit = true;
    }

    /**
     * 开启Ble 扫描
     *
     * @param callback
     */
    public void startLeScan(int mills, @NonNull BleScannerCallback callback) {
        mBleScanner.startLeScan(mills,callback);
    }

    /**
     * 取消Ble扫描
     */
    public void cancelLeScan() {
        mBleScanner.cancelLeScan();
    }

    /**
     * 请求蓝牙连接
     * @param mac
     * @param response
     */
    public void connectGatt(String mac, BleConnectorResponse response){
        mBleConnector.connectGatt(mac,response);
    }

    /**
     * 发送蓝牙请求
     * @param bleDataPackage
     * @param response
     */
    public void sendBleData(BleDataPackage bleDataPackage, BleReceiveResponse response){
        mBleSender.sendPackageData(bleDataPackage);
        mBleReceiver.receive(response);
    }

    /**
     * 发送蓝牙请求
     * @param serviceId     已连接设备的service Id
     * @param characterId   characteristic Id
     * @param data          字节流数据
     * @param mtu           mBleConnector 中 write 的 长度
     * @param response      接收数据的回调
     */
    public void sendBleData(UUID serviceId,UUID characterId,byte[] data,int mtu,BleReceiveResponse response){
        BleDataPackage bleDataPackage = new BleDataPackage(mBleConnector.getConnectGattMac(),serviceId,characterId,data,mtu);
        mBleSender.sendPackageData(bleDataPackage);
        mBleReceiver.receive(response);
    }

}