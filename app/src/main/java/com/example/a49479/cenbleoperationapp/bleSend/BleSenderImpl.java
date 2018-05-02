package com.example.a49479.cenbleoperationapp.bleSend;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.example.a49479.cenbleoperationapp.BleCode;
import com.example.a49479.cenbleoperationapp.bleConnect.BleWriteResponse;
import com.example.a49479.cenbleoperationapp.bleConnect.IBleConnector;

import java.util.UUID;

import static android.os.Process.THREAD_PRIORITY_BACKGROUND;

/**
 * Created by 49479 on 2018/5/2.
 */

public class BleSenderImpl implements IBleSender {

    private static final String TAG = "BleSenderImpl";

    IBleConnector mConnector;

    Object mLock;

    private HandlerThread mHandlerThread;  //后台线程
    private Handler mSendHandler;          //后台线程Handler

    private boolean mRequestCompleted;

    private static final int MSG_SEND_PACKAGE = 0x3001;

    public BleSenderImpl() {
        mHandlerThread = new HandlerThread("event handler thread", THREAD_PRIORITY_BACKGROUND);
        mHandlerThread.start();
        mSendHandler = new SendHandler(mHandlerThread.getLooper()) {
        };
    }

    public class SendHandler extends Handler {

        public SendHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_SEND_PACKAGE) {
                BleDataPackage bleDataPackage = (BleDataPackage) msg.obj;
                //发送数据.递归
                sendBleData(bleDataPackage);

                //等待回调
                try {
                    synchronized (mLock) {
                        while ((!mRequestCompleted && mConnector.isConnectGatt(bleDataPackage.mac))) {
                            mLock.wait();
                        }
                    }
                } catch (final InterruptedException e) {
                    Log.i(TAG, "sendBleDataAndWaitAck Sleeping interrupted", e);
                }

                //ToDo   发送ack
            }
        }
    }

    @Override
    public void init(IBleConnector connector, Object lock) {
        mConnector = connector;
        mLock = lock;
    }

    @Override
    public void sendPackageData(BleDataPackage bleDataPackage) {
        Message msg = new Message();
        msg.what = MSG_SEND_PACKAGE;

    }

    /**
     * 发送蓝牙数据包.递归方法
     *
     * @param bleDataPackage
     */
    private void sendBleData(final BleDataPackage bleDataPackage) {
        final byte[] data = bleDataPackage.data;
        final int mtuSize = bleDataPackage.mtu;
        final int offSet = bleDataPackage.offSet;

        mRequestCompleted = false;
        byte[] tempData = null;
        int restLength = data.length - mtuSize;

        if (restLength <= 0) {
            mRequestCompleted = true;
            return;
        }

        if (restLength > mtuSize) {
            tempData = new byte[mtuSize];
            System.arraycopy(data, offSet, tempData, 0, mtuSize);
        } else {
            tempData = new byte[restLength];
            System.arraycopy(data, offSet, tempData, 0, restLength);
        }

        final int dataLength = tempData.length;

        mConnector.writeNoRsp(bleDataPackage.mac, bleDataPackage.serviceId, bleDataPackage.characterId, tempData, new BleWriteResponse() {
            @Override
            public void onResponse(int result) {
                if (result == BleCode.REQUEST_SUCCESS) {
                    bleDataPackage.offSet = offSet + dataLength;
                    sendBleData(bleDataPackage);
                } else {

                }
            }
        });
    }
}
