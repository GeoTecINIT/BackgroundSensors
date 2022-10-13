package es.uji.geotec.backgroundsensors.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import es.uji.geotec.backgroundsensors.R;

public class NotificationProvider {
    private static final String SENSOR_RECORDING_CHANNEL = "SENSOR_RECORDING_CHANNEL";
    private static final int SENSOR_RECORDING_DESCRIPTION = R.string.sensor_recording_channel_description;
    private static final int SENSOR_RECORDING_NOTIFICATION_ID = 24;
    private static final long[] VIBRATION_PATTERN = new long[]{400, 400, 400, 400, 400};

    private Context context;
    private NotificationManagerCompat notificationManager;

    public NotificationProvider(Context context) {
        this.context = context;
        this.notificationManager = NotificationManagerCompat.from(context);
    }

    public Notification getNotificationForRecordingService(boolean enableVibration) {
        setupNotificationChannelIfNeeded(
                SENSOR_RECORDING_CHANNEL,
                context.getString(SENSOR_RECORDING_DESCRIPTION),
                enableVibration
        );

        Intent launchIntent = context
                .getPackageManager()
                .getLaunchIntentForPackage(context.getPackageName());

        return buildNotification(
                SENSOR_RECORDING_CHANNEL,
                context.getString(R.string.sensorization_notification_title),
                context.getString(R.string.sensorization_notification_text),
                PendingIntent.getActivity(
                        context,
                        0,
                        launchIntent,
                        Build.VERSION.SDK_INT > Build.VERSION_CODES.M
                                ? PendingIntent.FLAG_IMMUTABLE
                                : 0
                ),
                enableVibration
        );
    }

    public int getRecordingServiceNotificationId() {
        return SENSOR_RECORDING_NOTIFICATION_ID;
    }

    private void setupNotificationChannelIfNeeded(String id, String name, boolean enableVibration) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.O) {
            return;
        }

        if (notificationManager.getNotificationChannel(id) != null) {
            return;
        }

        NotificationChannel channel = new NotificationChannel(
                id,
                name,
                NotificationManager.IMPORTANCE_HIGH
        );

        if (enableVibration) {
            channel.enableVibration(true);
            channel.setVibrationPattern(VIBRATION_PATTERN);
        }

        notificationManager.createNotificationChannel(channel);
    }

    private Notification buildNotification(
            String channelId,
            String title,
            String text,
            PendingIntent pendingIntent,
            boolean enableVibration
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder builder = new Notification.Builder(context, channelId)
                .setContentTitle(title)
                .setContentText(text)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_sensor_service);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                builder.setForegroundServiceBehavior(Notification.FOREGROUND_SERVICE_IMMEDIATE);
            }

            return builder.build();
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
            .setContentTitle(title)
            .setContentText(text)
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.ic_sensor_service)
            .setPriority(NotificationCompat.PRIORITY_HIGH);

        if (enableVibration) {
            builder.setVibrate(VIBRATION_PATTERN);
        }

        return builder.build();
    }
}
