package com.example.a49479.cenbleoperationapp.bleReceive;

/**
 * Created by 49479 on 2018/5/2.
 */

public interface IBleReceiver {
    void init(Object lock);
    void receive();
}
