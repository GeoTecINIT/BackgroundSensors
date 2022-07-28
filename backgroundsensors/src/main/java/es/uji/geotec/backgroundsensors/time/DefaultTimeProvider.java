package es.uji.geotec.backgroundsensors.time;

public class DefaultTimeProvider extends TimeProvider {

    @Override
    public long getTimestamp() {
        return System.currentTimeMillis();
    }
}
