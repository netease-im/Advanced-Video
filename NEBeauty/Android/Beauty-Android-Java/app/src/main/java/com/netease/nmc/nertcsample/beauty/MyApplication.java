package com.netease.nmc.nertcsample.beauty;

import android.app.Application;

import com.netease.nmc.nertcsample.beauty.utils.FileUtils;

//  Created by NetEase on 10/15/21.
//  Copyright (c) 2014-2021 NetEase, Inc. All rights reserved.
//
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        startCameraActivity();
    }

    private void startCameraActivity(){
        new Thread(){
            public void run() {
                FileUtils.copyAllFiles(getApplicationContext(), "2D");
                FileUtils.copyAllFiles(getApplicationContext(), "face_morph");
                FileUtils.copyAllFiles(getApplicationContext(), "particle");
                FileUtils.copyStickerFiles(getApplicationContext(), "3D");
                FileUtils.copyAllFiles(getApplicationContext(), "beauty");
                FileUtils.copyStickerFiles(getApplicationContext(), "avatar");
                FileUtils.copyStickerFiles(getApplicationContext(), "hand_action");
                FileUtils.copyStickerFiles(getApplicationContext(), "segment");
                FileUtils.copyStickerFiles(getApplicationContext(), "deformation");
            }
        }.start();
    }

}
