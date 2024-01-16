package es.uji.geotec.backgroundsensors.service.manager;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

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

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public void enableServiceNotification() {
        int result = ActivityCompat.checkSelfPermission(this.context, Manifest.permission.POST_NOTIFICATIONS);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return;
        }

        ActivityCompat.requestPermissions(
                (Activity) this.context,
                new String[]{Manifest.permission.POST_NOTIFICATIONS},
                53
        );
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
