package com.netease.nmc.nertcsample.beauty.utils;

import android.content.Context;

public class ContextHolder {

    static Context ApplicationContext;

    private static boolean checkLicenseSuccess;

    public static void initial(Context context) {
        ApplicationContext = context;
    }

    public static Context getContext() {
        return ApplicationContext;
    }

    public static boolean isCheckLicenseSuccess() {
        return checkLicenseSuccess;
    }

    public static void setCheckLicenseSuccess(boolean checkLicenseSuccess) {
        ContextHolder.checkLicenseSuccess = checkLicenseSuccess;
    }
}
