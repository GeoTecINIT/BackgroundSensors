package es.uji.geotec.backgroundsensors.service.manager;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;

import es.uji.geotec.backgroundsensors.collection.CollectionConfiguration;
import es.uji.geotec.backgroundsensors.record.callback.RecordCallback;
import es.uji.geotec.backgroundsensors.sensor.Sensor;
import es.uji.geotec.backgroundsensors.service.SensorRecordingService;

public class ServiceManager {

    private Context context;
    private Class<?> service;

    public ServiceManager(Context context, Class<?> service) {
        this.context = context;
        this.service = service;
    }

    public void startCollection(CollectionConfiguration configuration, RecordCallback recordCallback) {
        Intent intent = new Intent(context, service);
        context.bindService(
            intent,
            new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName componentName, IBinder collectionService) {
                    SensorRecordingService.SensorRecordingBinder binder =
                            (SensorRecordingService.SensorRecordingBinder) collectionService;
                    binder.startRecordingFor(configuration, recordCallback);
                    context.unbindService(this);
                }

                @Override
                public void onServiceDisconnected(ComponentName componentName) { }
            },
            0
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        }
    }

    public void stopCollection(Sensor sensor) {
        Intent intent = new Intent(context, service);
        context.bindService(
                intent,
                new ServiceConnection() {
                    @Override
                    public void onServiceConnected(ComponentName componentName, IBinder collectionService) {
                        SensorRecordingService.SensorRecordingBinder binder =
                                (SensorRecordingService.SensorRecordingBinder) collectionService;
                        binder.stopRecordingFor(sensor);
                        context.unbindService(this);
                    }

                    @Override
                    public void onServiceDisconnected(ComponentName componentName) { }
                },
                0
        );
    }
}
