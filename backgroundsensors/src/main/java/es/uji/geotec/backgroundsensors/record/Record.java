package es.uji.geotec.backgroundsensors.record;

import es.uji.geotec.backgroundsensors.sensor.Sensor;

public class Record {

    private Sensor sensor;
    private long timestamp;

    public Record(Sensor sensor, long timestamp) {
        this.sensor = sensor;
        this.timestamp = timestamp;
    }

    public Sensor getSensor() {
        return sensor;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "Record{" +
                "sensor=" + sensor +
                ", timestamp=" + timestamp +
                '}';
    }
}
