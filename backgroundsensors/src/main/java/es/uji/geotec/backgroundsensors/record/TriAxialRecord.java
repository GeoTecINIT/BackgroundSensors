package es.uji.geotec.backgroundsensors.record;

import es.uji.geotec.backgroundsensors.sensor.Sensor;

public class TriAxialRecord extends Record {

    private float x, y, z;

    public TriAxialRecord(Sensor sensor, long timestamp, float x, float y, float z) {
        super(sensor, timestamp);
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    @Override
    public String toString() {
        return "TriAxialRecord{" +
                "sensor=" + getSensor() +
                ", timestamp=" + getTimestamp() +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
