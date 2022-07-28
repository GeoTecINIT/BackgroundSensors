package es.uji.geotec.backgroundsensors.collection;

import android.content.Context;
import android.hardware.SensorEventListener;

import java.util.Arrays;

import es.uji.geotec.backgroundsensors.listener.TriAxialSensorListener;
import es.uji.geotec.backgroundsensors.record.accumulator.RecordAccumulator;
import es.uji.geotec.backgroundsensors.record.callback.RecordCallback;
import es.uji.geotec.backgroundsensors.sensor.BaseSensor;
import es.uji.geotec.backgroundsensors.sensor.Sensor;
import es.uji.geotec.backgroundsensors.time.DefaultTimeProvider;
import es.uji.geotec.backgroundsensors.time.TimeProvider;

public class BaseCollectorManager extends CollectorManager {

    public BaseCollectorManager(Context context) {
        this(context, BaseSensor.values(), new DefaultTimeProvider());
    }

    public BaseCollectorManager(Context context, TimeProvider timeProvider) {
        this(context, BaseSensor.values(), timeProvider);
    }

    public BaseCollectorManager(Context context, Sensor[] sensors, TimeProvider timeProvider) {
        super(context, Arrays.asList(sensors), timeProvider);
    }

    @Override
    public boolean startCollectingFrom(
            CollectionConfiguration collectionConfiguration,
            RecordCallback callback
    ) {
        Sensor sensor = collectionConfiguration.getSensor();

        if (!sensorManager.isSensorAvailable(sensor))
            return false;

        RecordAccumulator accumulator = new RecordAccumulator(
                callback,
                collectionConfiguration.getBatchSize()
        );

        TriAxialSensorListener listener = new TriAxialSensorListener(sensor, accumulator, timeProvider);
        listeners.put(sensor, listener);


        android.hardware.Sensor androidSensor = this.getAndroidSensor(sensor);
        return androidSensorManager.registerListener(
                listener,
                androidSensor,
                collectionConfiguration.getSensorDelay()
        );
    }

    @Override
    public void stopCollectingFrom(Sensor sensor) {
        SensorEventListener listener = listeners.get(sensor);
        if (listener == null)
            return;

        listeners.remove(sensor);

        android.hardware.Sensor androidSensor = this.getAndroidSensor(sensor);
        androidSensorManager.unregisterListener(listener, androidSensor);
    }

    @Override
    public void ensureStopCollecting() {
        for (Sensor sensor : listeners.keySet()) {
            stopCollectingFrom(sensor);
        }
    }
}
