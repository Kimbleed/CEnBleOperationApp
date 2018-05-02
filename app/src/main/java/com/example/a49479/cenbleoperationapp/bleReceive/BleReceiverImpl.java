package com.example.a49479.cenbleoperationapp.bleReceive;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * Created by 49479 on 2018/5/2.
 */

public class BleReceiverImpl implements IBleReceiver {

    BleDataReceiver mBleDataReceiver;

    public static final String ACTION_AVAILABLE_DATA = "available_data";
    public static final String EXTRA_DATA = "extra_data";

    BleReceiveResponse mBleReceiveResponse;


    public BleReceiverImpl() {
        mBleDataReceiver = new BleDataReceiver();
    }

    @Override
    public void init(Context context, Object lock) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_AVAILABLE_DATA);
        context.registerReceiver(mBleDataReceiver, intentFilter);
    }

    @Override
    public void receive(BleReceiveResponse response) {
        mBleReceiveResponse = response;
    }

    public class BleDataReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(ACTION_AVAILABLE_DATA)) {
                BluetoothGattCharacteristic characteristic = (BluetoothGattCharacteristic) intent.getSerializableExtra(EXTRA_DATA);
                mBleReceiveResponse.onResponse(characteristic);
            }
        }
    }
}
