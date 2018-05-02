package com.example.a49479.cenbleoperationapp.bleSend;

import com.example.a49479.cenbleoperationapp.bleConnect.IBleConnector;

import java.util.UUID;

/**
 * Created by 49479 on 2018/5/2.
 */

public interface IBleSender {
    void init(IBleConnector connector,Object lock);
    void sendPackageData(BleDataPackage bleDataPackage);
}
