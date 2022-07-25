package es.uji.geotec.backgroundsensors.collection;

import android.content.Context;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import es.uji.geotec.backgroundsensors.record.callback.RecordCallback;
import es.uji.geotec.backgroundsensors.sensor.Sensor;

public abstract class CollectorManager {

    private Context context;

    protected List<Sensor> sensors;
    protected HashMap<Sensor, SensorEventListener> listeners;
    protected SensorManager sensorManager;

    public CollectorManager(Context context, List<Sensor> sensors) {
        this.context = context;
        this.sensors = sensors;
        this.listeners = new HashMap<>();
        this.sensorManager = (android.hardware.SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    }

    public List<Sensor> availableSensors() {
        List<Sensor> availableSensors = new ArrayList<>();
        for (Sensor sensor : sensors) {
            if (isSensorAvailable(sensor))
                availableSensors.add(sensor);
        }

        return availableSensors;
    }

    public boolean isSensorAvailable(Sensor sensor) {
        boolean hasFeature = context.getPackageManager().hasSystemFeature(sensor.getSystemFeature());

        // Heart rate appears to be not available as system feature in the emulator
        // but it is available from the sensor manager. This is probably due to the
        // recent inclusion of this sensor in the emulator.
        // TODO: check in the future if the heart rate is available as system feature to undo this patch
        int sensorType = sensor.getType();
        if (sensorType == -1)
            return hasFeature;

        boolean hasSensor = sensorManager.getDefaultSensor(sensor.getType()) != null;

        return hasFeature || hasSensor;
    }

    protected android.hardware.Sensor getAndroidSensor(Sensor sensor) {
        return sensorManager.getDefaultSensor(sensor.getType());
    }

    public abstract boolean startCollectingFrom(
            CollectionConfiguration collectionConfiguration,
            RecordCallback callback
    );
    public abstract void stopCollectingFrom(Sensor sensor);
    public abstract void ensureStopCollecting();
}
