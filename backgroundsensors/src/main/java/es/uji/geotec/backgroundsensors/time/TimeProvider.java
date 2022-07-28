package es.uji.geotec.backgroundsensors.time;

import android.os.SystemClock;

public abstract class TimeProvider {

    public long getTimestampFromElapsedNanos(long elapsedNanos) {
        return getTimestamp() + (elapsedNanos - SystemClock.elapsedRealtimeNanos()) / 1000000L;
    }

    abstract long getTimestamp();
}
