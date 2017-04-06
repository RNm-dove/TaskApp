package com.example.ryosuke.nakagawa.taskapp;

import android.app.Application;

import io.realm.Realm;

/**
 * Created by ryosuke on 2017/01/22.
 */
public class TaskApp extends Application {
    @Override
    public void onCreate(){
        super.onCreate();
        Realm.init(this);
    }
}
