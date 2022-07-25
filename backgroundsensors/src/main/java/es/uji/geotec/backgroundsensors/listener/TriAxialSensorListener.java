package es.uji.geotec.backgroundsensors.listener;

import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import es.uji.geotec.backgroundsensors.record.TriAxialRecord;
import es.uji.geotec.backgroundsensors.record.accumulator.RecordAccumulator;
import es.uji.geotec.backgroundsensors.sensor.Sensor;

public class TriAxialSensorListener implements SensorEventListener {

    private Sensor sensor;
    private RecordAccumulator accumulator;

    public TriAxialSensorListener(Sensor sensor, RecordAccumulator recordAccumulator) {
        this.sensor = sensor;
        this.accumulator = recordAccumulator;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() != sensor.getType())
            return;

        float xValue = event.values[0];
        float yValue = event.values[1];
        float zValue = event.values[2];

        TriAxialRecord record = new TriAxialRecord(sensor, System.currentTimeMillis(), xValue, yValue, zValue);
        accumulator.accumulateRecord(record);
    }

    @Override
    public void onAccuracyChanged(android.hardware.Sensor sensor, int accuracy) {

    }
}
