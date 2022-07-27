package es.uji.geotec.backgroundsensors.collection;

import android.content.Context;
import android.hardware.SensorEventListener;

import java.util.Arrays;

import es.uji.geotec.backgroundsensors.listener.TriAxialSensorListener;
import es.uji.geotec.backgroundsensors.record.accumulator.RecordAccumulator;
import es.uji.geotec.backgroundsensors.record.callback.RecordCallback;
import es.uji.geotec.backgroundsensors.sensor.BaseSensor;
import es.uji.geotec.backgroundsensors.sensor.Sensor;

public class BaseCollectorManager extends CollectorManager {

    public BaseCollectorManager(Context context) {
        super(context, Arrays.asList(BaseSensor.values()));
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

        TriAxialSensorListener listener = new TriAxialSensorListener(sensor, accumulator);
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
