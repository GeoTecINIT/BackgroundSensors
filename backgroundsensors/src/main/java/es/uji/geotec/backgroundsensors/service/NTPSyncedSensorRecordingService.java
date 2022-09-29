package es.uji.geotec.backgroundsensors.service;

import android.util.Log;

import es.uji.geotec.backgroundsensors.collection.BaseCollectorManager;
import es.uji.geotec.backgroundsensors.collection.CollectorManager;
import es.uji.geotec.backgroundsensors.time.NTPTimeProvider;

public class NTPSyncedSensorRecordingService extends SensorRecordingService {

    protected NTPTimeProvider ntpTimeProvider;
    protected boolean ntpSynced;

    @Override
    public void onCreate() {
        super.onCreate();

        ntpTimeProvider = NTPTimeProvider.getInstance();
        ntpSynced = ntpTimeProvider.isSynced();

        if (!ntpSynced) {
            ntpSynced = ntpTimeProvider.sync();
            if (!ntpSynced) {
                Log.d("SyncedRecordingService", "ntp syncing failed, using system clock");
            }
        }
    }

    @Override
    public CollectorManager getCollectorManager() {
        return new BaseCollectorManager(this, NTPTimeProvider.getInstance());
    }
}
