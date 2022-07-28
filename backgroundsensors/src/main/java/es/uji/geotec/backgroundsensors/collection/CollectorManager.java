package es.uji.geotec.backgroundsensors.collection;

import android.content.Context;
import android.hardware.SensorEventListener;

import java.util.HashMap;

import es.uji.geotec.backgroundsensors.record.callback.RecordCallback;
import es.uji.geotec.backgroundsensors.sensor.Sensor;
import es.uji.geotec.backgroundsensors.sensor.SensorManager;
import es.uji.geotec.backgroundsensors.time.TimeProvider;

public abstract class CollectorManager {

    protected Context context;
    protected TimeProvider timeProvider;
    protected HashMap<Sensor, SensorEventListener> listeners;
    protected SensorManager sensorManager;
    protected android.hardware.SensorManager androidSensorManager;

    public CollectorManager(Context context, TimeProvider timeProvider) {
        this.context = context;
        this.timeProvider = timeProvider;
        this.listeners = new HashMap<>();
        this.sensorManager = new SensorManager(context);
        this.androidSensorManager = (android.hardware.SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    }

    protected android.hardware.Sensor getAndroidSensor(Sensor sensor) {
        return androidSensorManager.getDefaultSensor(sensor.getType());
    }

    public abstract boolean startCollectingFrom(
            CollectionConfiguration collectionConfiguration,
            RecordCallback callback
    );
    public abstract void stopCollectingFrom(Sensor sensor);
    public abstract void ensureStopCollecting();
}
