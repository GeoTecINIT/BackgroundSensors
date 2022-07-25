package es.uji.geotec.backgroundsensors.collection;

import android.hardware.SensorManager;

import es.uji.geotec.backgroundsensors.sensor.Sensor;

public class CollectionConfiguration {

    private static final int DEFAULT_SENSOR_DELAY = SensorManager.SENSOR_DELAY_NORMAL;
    private static final int DEFAULT_BATCH_SIZE = 50;

    private Sensor sensor;
    private int sensorDelay;
    private int batchSize;

    public CollectionConfiguration(Sensor sensor, int sensorDelay, int batchSize) {
        this.sensor = sensor;
        this.sensorDelay = sensorDelay;
        this.batchSize = batchSize;
    }

    public Sensor getSensor() {
        return sensor;
    }

    public int getSensorDelay() {
        if (sensorDelay == -1)
            return DEFAULT_SENSOR_DELAY;
        return sensorDelay;
    }

    public int getBatchSize() {
        if (batchSize == -1)
            return DEFAULT_BATCH_SIZE;
        return batchSize;
    }
}
