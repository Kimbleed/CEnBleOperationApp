package com.example.a49479.cenbleoperationapp.bleConnect;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.example.a49479.cenbleoperationapp.BleCode;

import java.util.List;
import java.util.UUID;


/**
 * Created by 49479 on 2018/4/24.
 */

public class BleConnectorImpl implements IBleConnector {

    private static final String TAG = "BleConnectorImpl";

    private Context mContext;
    private BluetoothAdapter mBleAdapter;

    private String mMac="";
    private BluetoothGatt mBleGatt;
    private List<BluetoothGattService> mGattServicesList;
    private BluetoothGattService mNotifyBleService;
    private BluetoothGattCharacteristic mNotifyBleCharacteristic;
    private BluetoothGattService mWriteBleSerivce;
    private BluetoothGattCharacteristic mWriteBleCharacteristic;

    private int mBleConnectState = BluetoothProfile.STATE_DISCONNECTED;

    private BleConnectorCallback mBleConnectCallback;
    private BleNotifyResponse mBleNotifyResponse;

    private BluetoothGattCallback mBleGattCallback = new BluetoothGattCallback() {

        /**
         * 连接回调
         * @param gatt
         * @param status
         * @param newState
         */
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            Log.i(TAG, "onConnectionStateChange status:" + status + " newState:" + newState);
            if (status == BluetoothProfile.STATE_CONNECTED && newState == 0) {
                Log.i(TAG, "onConnectionStateChange  connected");
                mBleGatt = gatt;
                changeBleConnectState(BluetoothProfile.STATE_CONNECTED);
                gatt.discoverServices();
            } else if (status == BluetoothProfile.STATE_DISCONNECTED) {
                Log.i(TAG, "onConnectionStateChange  disconnected ");
                gatt.close();
                //蓝牙连接断开 且 断开的蓝牙就是当前操作的蓝牙时，则 mMac 和 mBleGatt 赋空
                if (mMac.equals(gatt.getDevice().getAddress())) {
                    changeBleConnectState(BluetoothProfile.STATE_DISCONNECTED);
                    reset();
                }
                //蓝牙连接断开 且 断开的蓝牙不是当前操作的蓝牙时，则
                else {

                }
            } else if (status == BluetoothProfile.STATE_DISCONNECTING) {
                Log.i(TAG, "onConnectionStateChange  disconnecting ");
                changeBleConnectState(BluetoothProfile.STATE_DISCONNECTING);
            } else if (status == BluetoothProfile.STATE_CONNECTING) {
                Log.i(TAG, "onConnectionStateChange  disconnected ");
                changeBleConnectState(BluetoothProfile.STATE_CONNECTING);
            }
        }

        /**
         * 发现服务回调
         * @param gatt
         * @param status
         */
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                mGattServicesList = gatt.getServices();
                if (mGattServicesList.size() <= 0) {
                    Log.i(TAG, "onServicesDiscovered services size 0");
                }

            } else {
                gatt.disconnect();
            }
        }

        /**
         * 写描述符回调
         * @param gatt
         * @param descriptor
         * @param status
         */
        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            String character = descriptor.getCharacteristic().getUuid().toString();
            String service = descriptor.getCharacteristic().getService().getUuid().toString();
            Log.i(TAG, "onDescriptorWrite " + "   service:" + service + "   character:" + character);
            if (character.equals(mNotifyBleCharacteristic.getUuid().toString()) && service.equals(mNotifyBleService.getUuid().toString())) {
                Log.i(TAG, "onDescriptorWrite character and service match the params of the method \"notify\" ");
                notifyResponse(BleCode.REQUEST_SUCCESS);
            } else {
                Log.i(TAG, "onDescriptorWrite character and service don't match the params of the method \"notify\" ");
                notifyResponse(BleCode.BLE_NOTIFY_FAILURE);
            }
        }

        /**
         * 特征值改变回调
         * @param gatt
         * @param characteristic
         */
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
        }

        /**
         * 写特征值监听
         * @param gatt
         * @param characteristic
         * @param status
         */
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
        }

        /**
         * 读特征值回调
         * @param gatt
         * @param characteristic
         * @param status
         */
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
        }

    };

    public BleConnectorImpl() {

    }

    @Override
    public void init(Context context, BluetoothAdapter bluetoothAdapter) {
        mBleAdapter = bluetoothAdapter;
        mContext = context;
    }

    public void connectGatt(BluetoothDevice device, @NonNull BleConnectorCallback callback) {
        connectGatt(device.getAddress(), callback);
    }

    @Override
    public void connectGatt(String mac, @NonNull BleConnectorCallback callback) {
        //若 mMac 为空，代表当前没有正在操作(非以下三种状态：已连接、连接中、正在断开)的设备
        if (TextUtils.isEmpty(mMac)) {
            mMac = mac;
        }
        //mMac 不为空，代表当前有正在操作(以下三种状态：已连接、连接中、正在断开)的设备
        else {
            // 若请求连接的地址 是当前正在操作的设备的地址
            if (mMac.equals(mac)) {
                mBleConnectCallback = callback;

                if (mBleConnectState == BluetoothProfile.STATE_CONNECTED) {
                    changeBleConnectState(BluetoothProfile.STATE_CONNECTED);
                    return;
                } else {
                    Log.i(TAG, "connectGatt has request ble connect , waiting callback");
                }
            }
            //若请求连接的地址 不是当前正在操作的设备的地址，断开连接
            else {
                mBleGatt.disconnect();
                reset();
                changeBleConnectState(BluetoothProfile.STATE_DISCONNECTED);
                mMac = mac;
            }
        }
        mBleConnectCallback = callback;
        changeBleConnectState(BluetoothProfile.STATE_CONNECTING);
        BluetoothDevice device = mBleAdapter.getRemoteDevice(mac);
        device.connectGatt(mContext, false, mBleGattCallback);
    }

    @Override
    public void notify(String mac, UUID serviceId, UUID characterId, @NonNull BleNotifyResponse response) {
        mBleNotifyResponse = response;
        if (mMac.equals(mac)) {
            if (mBleConnectState == BluetoothProfile.STATE_CONNECTED) {
                for (BluetoothGattService gattService : mGattServicesList) {
                    if (gattService.getUuid().toString().equals(serviceId.toString())) {
                        BluetoothGattCharacteristic gattCharacteristic = gattService.getCharacteristic(characterId);
                        if (setCharacteristicNotification(mBleGatt, gattCharacteristic, true)) {
                            mNotifyBleService = gattService;
                            mNotifyBleCharacteristic = gattCharacteristic;
                        }
                    }
                }
            } else {
                Log.i(TAG, "notify ble connect state isn't connected --" + BleCode.toString(mBleConnectState));
                notifyResponse(mBleConnectState);
            }
        } else {
            Log.i(TAG, "notify mac not match the mac address of current connected device");
            notifyResponse(BleCode.REQUEST_MAC_NO_MATCH);
        }
    }

    @Override
    public void write(String mac, UUID serviceId, UUID characterId, byte[] data, BleWriteResponse response) {
        if (mac.equals(mMac) && mBleConnectState == BluetoothProfile.STATE_CONNECTED) {
            writePrepare(serviceId, characterId, BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
            writeData(data);
        } else {
            if (!mac.equals(mMac)) {
                Log.i(TAG, "write params mac doesn't match the mMac");
            }
            Log.i(TAG, "write ble connect state is " + BleCode.toString(mBleConnectState));
        }
    }

    @Override
    public void writeNoRsp(String mac, UUID serviceId, UUID characterId, byte[] data, BleWriteResponse response) {
        if (mac.equals(mMac) && mBleConnectState == BluetoothProfile.STATE_CONNECTED) {
            writePrepare(serviceId, characterId, BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
            writeData(data);
        } else {

        }
    }

    /**
     * private String mMac;
     * private BluetoothGatt mBleGatt;
     * private List<BluetoothGattService> mGattServicesList;
     * private BluetoothGattService mNotifyBleService;
     * private BluetoothGattCharacteristic mNotifyBleCharacteristic;
     */
    private void reset() {
        mMac = null;
        mBleGatt = null;
        mGattServicesList = null;
        mNotifyBleService = null;
        mNotifyBleCharacteristic = null;
    }

    /**
     * 设置 Character 通知
     *
     * @param gatt
     * @param characteristicRead
     * @param enable
     * @return
     */
    private boolean setCharacteristicNotification(BluetoothGatt gatt, BluetoothGattCharacteristic characteristicRead, boolean enable) {
        if (gatt == null) {
            Log.i(TAG, "setCharacteristicNotification gatt is null");
            return false;
        }

        gatt.setCharacteristicNotification(characteristicRead, enable);

        List<BluetoothGattDescriptor> descriptorList = characteristicRead.getDescriptors();
        Log.i(TAG, "setCharacteristicNotification descriptorList size = " + descriptorList.size());
        for (BluetoothGattDescriptor descriptor : descriptorList) {
            boolean ret;
            ret = descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            Log.i(TAG, "setCharacteristicNotification descriptor set value result:" + ret);
            gatt.writeDescriptor(descriptor);
            Log.i(TAG, "setCharacteristicNotification gatt write descriptor result:" + ret);
        }

        return true;
    }

    /**
     * @param state 蓝牙连接状态
     *              1.BluetoothProfile.STATE_CONNECTED         变为已连接，调用bleConnectCallback.onResponse 后，callback置空
     *              2.BluetoothProfile.STATE_DISCONNECTED      变为断开连接，调用bleConnectCallback.onResponse 后，callback置空
     *              3.BluetoothProfile.STATE_CONNECTING
     *              4.BluetoothProfile.STATE_DISCONNECTING
     */
    private void changeBleConnectState(int state) {
        Log.i(TAG, "changeBleConnectState :" + BleCode.toString(state));
        mBleConnectState = state;
        mBleConnectCallback.onResponse(mBleConnectState);
        if (mBleConnectState == BluetoothProfile.STATE_CONNECTED || mBleConnectState == BluetoothProfile.STATE_DISCONNECTED)
            mBleConnectCallback = null;
    }

    /**
     * @param response notify结果
     *                 1.BleNotifyResponse.BLE_NOTIFY_SUCCESS
     *                 2.BleNotifyResponse.BLE_NOTIFY_FAILURE
     */
    private void notifyResponse(int response) {
        Log.i(TAG, "notifyResponse :" + BleCode.toString(response));
        mBleNotifyResponse.onResponse(response);
        mBleNotifyResponse = null;
    }

    /**
     * 获取写数据的  service 和 characteristic ，并声明写类型为 WRITE_TYPE_NO_RESPONSE
     *
     * @param serviceId
     * @param characterId
     * @param writeType   写的类型
     *                    1.BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE
     *                    2.BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
     */
    private void writePrepare(UUID serviceId, UUID characterId, int writeType) {
        if (mWriteBleSerivce.getUuid().toString().equals(serviceId) && mWriteBleCharacteristic.getUuid().toString().equals(characterId)) {
            return;
        }

        mWriteBleSerivce = mBleGatt.getService(serviceId);
        if (null == mWriteBleSerivce) {
            Log.e(TAG, "writePrepare get mUartService null");
            return;
        }
        mWriteBleCharacteristic = mWriteBleSerivce.getCharacteristic(characterId);
        if (null == mWriteBleCharacteristic) {
            Log.e(TAG, "writePrepare get mUartROCharatoristic null");
            return;
        }
        mWriteBleCharacteristic.setWriteType(writeType);
    }


    private void writeData(byte[] data) {
        mWriteBleCharacteristic.setValue(data);
        mBleGatt.writeCharacteristic(mWriteBleCharacteristic);
    }

}