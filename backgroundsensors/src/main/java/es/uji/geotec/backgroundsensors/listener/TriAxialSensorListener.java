package es.uji.geotec.backgroundsensors.listener;

import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import es.uji.geotec.backgroundsensors.record.TriAxialRecord;
import es.uji.geotec.backgroundsensors.record.accumulator.RecordAccumulator;
import es.uji.geotec.backgroundsensors.sensor.Sensor;
import es.uji.geotec.backgroundsensors.time.TimeProvider;

public class TriAxialSensorListener implements SensorEventListener {

    private Sensor sensor;
    private RecordAccumulator accumulator;
    private TimeProvider timeProvider;

    public TriAxialSensorListener(Sensor sensor, RecordAccumulator recordAccumulator, TimeProvider timeProvider) {
        this.sensor = sensor;
        this.accumulator = recordAccumulator;
        this.timeProvider = timeProvider;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() != sensor.getType())
            return;

        float xValue = event.values[0];
        float yValue = event.values[1];
        float zValue = event.values[2];

        TriAxialRecord record = new TriAxialRecord(
                sensor,
                timeProvider.getTimestampFromElapsedNanos(event.timestamp),
                xValue,
                yValue,
                zValue
        );

        accumulator.accumulateRecord(record);
    }

    @Override
    public void onAccuracyChanged(android.hardware.Sensor sensor, int accuracy) {

    }
}
