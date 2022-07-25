package es.uji.geotec.backgroundsensors.sensor;

import android.content.pm.PackageManager;

public enum BaseSensor implements Sensor {
    ACCELEROMETER(android.hardware.Sensor.TYPE_ACCELEROMETER, PackageManager.FEATURE_SENSOR_ACCELEROMETER),
    GYROSCOPE(android.hardware.Sensor.TYPE_GYROSCOPE, PackageManager.FEATURE_SENSOR_GYROSCOPE),
    MAGNETOMETER(android.hardware.Sensor.TYPE_MAGNETIC_FIELD, PackageManager.FEATURE_SENSOR_COMPASS);

    private int sensorType;
    private String feature;
    BaseSensor(int sensorType, String feature) {
        this.sensorType = sensorType;
        this.feature = feature;
    }

    @Override
    public int getType() {
        return sensorType;
    }

    @Override
    public String getSystemFeature() {
        return feature;
    }
}
