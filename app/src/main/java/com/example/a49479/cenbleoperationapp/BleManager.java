package com.example.a49479.cenbleoperationapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.support.annotation.NonNull;

import com.example.a49479.cenbleoperationapp.bleConnect.BleConnectorImpl;
import com.example.a49479.cenbleoperationapp.bleConnect.IBleConnector;
import com.example.a49479.cenbleoperationapp.bleScan.BleScannerCallback;
import com.example.a49479.cenbleoperationapp.bleScan.BleScannerImpl;
import com.example.a49479.cenbleoperationapp.bleScan.IBleScanner;

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

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice bluetoothDevice, int i, byte[] bytes) {
            if (mBleScannerCallback != null)
                mBleScannerCallback.onDeviceFound(bluetoothDevice);

        }
    };


    private static final int EVENT_HANDLER_BLE_SCAN_OVERTIME = 0x1000;

    private boolean isInit;//是否已经初始化

    private BleScannerCallback mBleScannerCallback;
    private boolean isInScanning;

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

}