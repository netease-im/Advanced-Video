package com.netease.nmc.nertcsample.beauty.utils;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * @author MatrixCV
 * <p>
 * Accelerometer 用于开启重力传感器，以获得当前手机朝向
 */
public class Accelerometer {
    /**
     * @author MatrixCV
     * <p>
     * CLOCKWISE_ANGLE为手机旋转角度
     * 其Deg0定义如下图所示
     * ___________________
     * | +--------------+  |
     * | |              |  |
     * | |              |  |
     * | |              | O|
     * | |              |  |
     * | |______________|  |
     * ---------------------
     * 顺时针旋转后得到Deg90，即手机竖屏向上，如下图所示
     * ___________
     * |           |
     * |+---------+|
     * ||         ||
     * ||         ||
     * ||         ||
     * ||         ||
     * ||         ||
     * |+---------+|
     * |_____O_____|
     */
    // Deg0:横屏  Deg90:正向竖屏  Deg180:反向横屏  Deg270:反向竖屏
    public enum CLOCKWISE_ANGLE {
        Deg0(0, "横屏"), Deg90(1, "竖屏"), Deg180(2, "反向横屏"), Deg270(3, "反向竖屏");
        private int value;
        private String des;

        CLOCKWISE_ANGLE(int value, String des) {
            this.value = value;
            this.des = des;
        }

        public int getValue() {
            return value;
        }

        public String getDes() {
            return des;
        }
    }

    private SensorManager sensorManager;

    private boolean hasStarted = false;

    private static CLOCKWISE_ANGLE rotation;

    /**
     * @param ctx 用Activity初始化获得传感器
     */
    public Accelerometer(Context ctx) {
        sensorManager = (SensorManager) ctx
                .getSystemService(Context.SENSOR_SERVICE);
        rotation = CLOCKWISE_ANGLE.Deg90;
    }

    /**
     * 开始对传感器的监听
     */
    public void start() {
        if (hasStarted) return;
        hasStarted = true;
        rotation = CLOCKWISE_ANGLE.Deg90;
        sensorManager.registerListener(accListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    /**
     * 结束对传感器的监听
     */
    public void stop() {
        if (!hasStarted) return;
        hasStarted = false;
        sensorManager.unregisterListener(accListener);
    }

    /**
     * @return 返回当前手机转向
     */
    static public int getDirection() {
        return rotation.getValue();
    }

    /**
     * 传感器与手机转向之间的逻辑
     */
    private SensorEventListener accListener = new SensorEventListener() {

        @Override
        public void onAccuracyChanged(Sensor arg0, int arg1) {
        }

        @Override
        public void onSensorChanged(SensorEvent arg0) {
            if (arg0.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                float x = arg0.values[0];
                float y = arg0.values[1];
                float z = arg0.values[2];
                if (Math.abs(x) > 3 || Math.abs(y) > 3) {
                    if (Math.abs(x) > Math.abs(y)) {
                        if (x > 0) {
                            rotation = CLOCKWISE_ANGLE.Deg0;
                            //Log.d("ROTATION","CLOCKWISE_ANGLE: Deg0");
                        } else {
                            rotation = CLOCKWISE_ANGLE.Deg180;
                            //Log.d("ROTATION","CLOCKWISE_ANGLE: Deg180");
                        }
                    } else {
                        if (y > 0) {
                            rotation = CLOCKWISE_ANGLE.Deg90;
                            //Log.d("ROTATION","CLOCKWISE_ANGLE: Deg90");
                        } else {
                            rotation = CLOCKWISE_ANGLE.Deg270;
                            //Log.d("ROTATION","CLOCKWISE_ANGLE: Deg270");
                        }
                    }
                }
                if (mListener != null) {
                    mListener.onOrientationChanged(rotation);
                }
            }
        }
    };

    private Listener mListener;

    public interface Listener {
        void onOrientationChanged(CLOCKWISE_ANGLE orientation);
    }

    public void setListener(Listener listener) {
        this.mListener = listener;
    }
}
