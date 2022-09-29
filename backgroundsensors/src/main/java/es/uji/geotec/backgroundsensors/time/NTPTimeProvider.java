package es.uji.geotec.backgroundsensors.time;

import android.util.Log;

import es.uji.geotec.backgroundsensors.time.ntp.GoodClock;

public class NTPTimeProvider extends TimeProvider {

    private static NTPTimeProvider instance;

    private GoodClock clock;
    private long lastSyncTimestamp;

    private NTPTimeProvider() {
        clock = new GoodClock(5, GoodClock.DEFAULT_UPDATE_INTERVAL);
    }

    public static NTPTimeProvider getInstance() {
        if (instance == null) {
            instance = new NTPTimeProvider();
        }
        return instance;
    }

    public boolean sync() {
        boolean success = clock.singleSync();
        lastSyncTimestamp = getTimestamp();
        return success;
    }

    public boolean isSynced() {
        long now = getTimestamp();

        return now - lastSyncTimestamp < 60000 && clock.SntpSuceeded;
    }

    @Override
    public long getTimestamp() {
        return clock.SntpSuceeded ? clock.Now() : System.currentTimeMillis();
    }
}
