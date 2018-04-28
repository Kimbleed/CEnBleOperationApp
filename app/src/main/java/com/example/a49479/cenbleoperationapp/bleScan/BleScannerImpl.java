package com.example.a49479.cenbleoperationapp.bleScan;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import static android.os.Process.THREAD_PRIORITY_BACKGROUND;

/**
 * Created by 49479 on 2018/4/23.
 */

public class BleScannerImpl implements IBleScanner, BluetoothAdapter.LeScanCallback {

    private static final String TAG = "BleScannerImpl" ;

    private BluetoothAdapter mBleAdapter;

    private BleScannerCallback mBleScannerCallback;
    private boolean isInScanning;

    private HandlerThread mHandlerThread;
    private static Handler mEventHandler;          //后台线程Handler

    public BleScannerImpl() {

    }

    @Override
    public void init(BluetoothAdapter bluetoothAdapter){
        mBleAdapter = bluetoothAdapter;
        mHandlerThread = new HandlerThread("event handler thread", THREAD_PRIORITY_BACKGROUND);
        mHandlerThread.start();
        mEventHandler = new Handler(mHandlerThread.getLooper());
    }

    @Override
    public void startLeScan(long mills,BleScannerCallback callback) {
        if (mBleAdapter == null) {
            Log.i(TAG,"bleAdapter is null");
            return;
        }

        if (mBleAdapter.getState() == BluetoothAdapter.STATE_ON) {
            Log.i(TAG,"bleAdapter state on");
            mBleAdapter.isEnabled();
            return;
        }

        if(mBleScannerCallback!=null)
            mBleScannerCallback.onCancelScan();

        mBleScannerCallback = callback;

        if(mBleScannerCallback!=null)
        mBleScannerCallback.onStartScan();

        isInScanning = true;

        mBleAdapter.startLeScan(BleScannerImpl.this);

        mEventHandler.removeMessages(EVENT_HANDLER_BLE_SCAN_OVERTIME);
        mEventHandler.sendEmptyMessageDelayed(EVENT_HANDLER_BLE_SCAN_OVERTIME,mills);

    }

    @Override
    public void cancelLeScan() {
        if(mBleScannerCallback!=null)
            mBleScannerCallback.onCancelScan();

        mEventHandler.removeMessages(EVENT_HANDLER_BLE_SCAN_OVERTIME);
        mEventHandler.sendEmptyMessage(EVENT_HANDLER_BLE_SCAN_OVERTIME);
    }

    public void stopLeScan(){
        if(mBleAdapter.isDiscovering())
            mBleAdapter.cancelDiscovery();

        if(mBleScannerCallback!=null){
            mBleScannerCallback.onStopScan();
            mBleScannerCallback = null;
        }
    }

    @Override
    public void onLeScan(BluetoothDevice bluetoothDevice, int i, byte[] bytes) {
        if (mBleScannerCallback != null)
            mBleScannerCallback.onDeviceFound(bluetoothDevice);
    }

    private static final int EVENT_HANDLER_BLE_SCAN_OVERTIME = 0x1001;

    class EventHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case EVENT_HANDLER_BLE_SCAN_OVERTIME:
                    stopLeScan();
                    break;
            }
        }
    }

}
