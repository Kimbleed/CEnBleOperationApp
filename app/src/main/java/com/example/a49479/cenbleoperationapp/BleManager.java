package com.example.a49479.cenbleoperationapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;

import com.example.a49479.cenbleoperationapp.bleConnect.BleWriteResponse;
import com.example.a49479.cenbleoperationapp.bleConnect.IBleConnector;
import com.example.a49479.cenbleoperationapp.bleScan.BleScannerCallback;
import com.example.a49479.cenbleoperationapp.bleScan.IBleScanner;

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

    private boolean isInit;//是否已经初始化

    private BleScannerCallback mBleScannerCallback;
    private boolean isInScanning;

    private HandlerThread mHandlerThread;
    private static Handler mEventHandler;          //后台线程Handler

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


    /**
     * 发送蓝牙数据
     * @param mac
     * @param serviceId
     * @param characterId
     * @param data
     * @param mtuSize
     * @param offSet
     */
    public void sendBleData(final String mac, final UUID serviceId, final UUID characterId, final byte[] data, final int mtuSize, final int offSet){
        byte[] tempData = null;
       int restLength = data.length-mtuSize;

        if (restLength <= 0) {
            return;
        }

        if(restLength>mtuSize) {
            tempData = new byte[mtuSize];
            System.arraycopy(data, offSet, tempData, 0, mtuSize);
        }
        else{
            tempData = new byte[restLength];
            System.arraycopy(data, offSet, tempData, 0, restLength);
        }
        final int dataLength = tempData.length;
        mBleConnector.writeNoRsp(mac, serviceId, characterId, tempData, new BleWriteResponse() {
            @Override
            public void onResponse(int result) {
                if(result == BleCode.REQUEST_SUCCESS){
                    int newOffSet = offSet+dataLength;
                    sendBleData(mac,serviceId,characterId,data,mtuSize,newOffSet);
                }
                else{

                }
            }
        });
    }

}