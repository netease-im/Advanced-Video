package com.netease.nmc.nertcsample.sensetime.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.sensetime.stmobile.STMobileAuthentificationNative;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by leiwang on 2016/12/2.
 */

public class STLicenseUtils {
    private final static String TAG = "STLicenseUtils";

    private final static String PREF_ACTIVATE_CODE_FILE = "activate_code_file";
    private final static String PREF_ACTIVATE_CODE = "activate_code";

    private static final String LOCAL_LICENSE_NAME = "SenseME.lic";//离线generateActiveCode使用

    //鉴权
    public static boolean checkLicense(final Context context) {
        return checkLicenseFromLocal(context);
    }

    /**
     * 检查activeCode合法性
     *
     * @return true, 成功 false,失败
     */
    public static boolean checkLicenseFromAssetFile(Context context, String fileName, boolean isOnlineLicense) {
        StringBuilder sb = new StringBuilder();
        InputStreamReader isr = null;
        BufferedReader br = null;
        // 读取license文件内容
        try {
            isr = new InputStreamReader(context.getResources().getAssets().open(fileName));
            br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (isr != null) {
                try {
                    isr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // license文件为空,则直接返回
        if (sb.toString().length() == 0) {
            Log.e(TAG, "read license data error");
            return false;
        }

        String licenseBuffer = sb.toString();
        /**
         * 以下逻辑为：
         * 1. 获取本地保存的激活码
         * 2. 如果没有则生成一个激活码
         * 3. 如果有, 则直接调用checkActiveCode*检查激活码
         * 4. 如果检查失败，则重新生成一个activeCode
         * 5. 如果生成失败，则返回失败，成功则保存新的activeCode，并返回成功
         */
        SharedPreferences sp = context.getApplicationContext().getSharedPreferences(PREF_ACTIVATE_CODE_FILE, Context.MODE_PRIVATE);
        String activateCode = sp.getString(PREF_ACTIVATE_CODE, null);
        int error = -1;
        if (activateCode == null || (STMobileAuthentificationNative.checkActiveCodeFromBuffer(context, licenseBuffer, licenseBuffer.length(), activateCode, activateCode.length()) != 0)) {
            Log.e(TAG, "activeCode: " + (activateCode == null));
            if (isOnlineLicense) {
                activateCode = STMobileAuthentificationNative.generateActiveCodeFromBufferOnline(context, licenseBuffer, licenseBuffer.length());
            } else {
                activateCode = STMobileAuthentificationNative.generateActiveCodeFromBuffer(context, licenseBuffer, licenseBuffer.length());
            }
            if (activateCode != null && activateCode.length() > 0) {
                SharedPreferences.Editor editor = sp.edit();
                editor.putString(PREF_ACTIVATE_CODE, activateCode);
                editor.commit();
                return true;
            }
            Log.e(TAG, "generate license error: " + error);
            return false;
        }

        Log.e(TAG, "activeCode: " + activateCode);
        return true;
    }


    //从本地读取License文件，并检查授权
    public static boolean checkLicenseFromLocal(final Context context) {
        return STLicenseUtils.checkLicenseFromAssetFile(context, LOCAL_LICENSE_NAME, false);
    }
}
