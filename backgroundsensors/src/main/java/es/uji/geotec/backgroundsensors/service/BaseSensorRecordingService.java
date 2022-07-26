package es.uji.geotec.backgroundsensors.service;

import es.uji.geotec.backgroundsensors.collection.CollectorManager;
import es.uji.geotec.backgroundsensors.collection.BaseCollectorManager;

public class BaseSensorRecordingService extends SensorRecordingService {

    @Override
    public CollectorManager getCollectorManager() {
        return new BaseCollectorManager(this);
    }
}
