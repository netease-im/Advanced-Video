package com.netease.nertcbeautysample;

import android.app.Application;
import android.content.Context;

import com.faceunity.FURenderer;
import com.faceunity.utils.FileUtils;
import com.netease.nertcbeautysample.util.ThreadHelper;

public class BeautyApplication extends Application {
    private static Context sContext;

    public static Context getContext() {
        return sContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this;
        FURenderer.initFURenderer(BeautyApplication.this);
        ThreadHelper.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                // 异步拷贝 assets 资源
                FileUtils.copyAssetsChangeFaceTemplate(sContext);
            }
        });

    }
}
