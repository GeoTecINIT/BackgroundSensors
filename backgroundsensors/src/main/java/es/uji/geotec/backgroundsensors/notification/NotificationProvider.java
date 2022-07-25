package es.uji.geotec.backgroundsensors.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import es.uji.geotec.backgroundsensors.R;

public class NotificationProvider {
    private static final String SENSOR_RECORDING_CHANNEL = "SENSOR_RECORDING_CHANNEL";
    private static final int SENSOR_RECORDING_DESCRIPTION = R.string.sensor_recording_channel_description;
    private static final int SENSOR_RECORDING_NOTIFICATION_ID = 24;

    private Context context;
    private NotificationManagerCompat notificationManager;

    public NotificationProvider(Context context) {
        this.context = context;
        this.notificationManager = NotificationManagerCompat.from(context);
    }

    public Notification getNotificationForRecordingService() {
        setupNotificationChannelIfNeeded(
                SENSOR_RECORDING_CHANNEL,
                context.getString(SENSOR_RECORDING_DESCRIPTION)
        );

        return buildNotification(
                SENSOR_RECORDING_CHANNEL,
                context.getString(R.string.sensorization_notification_title),
                context.getString(R.string.sensorization_notification_text),
                null
        );
    }

    public int getRecordingServiceNotificationId() {
        return SENSOR_RECORDING_NOTIFICATION_ID;
    }

    private void setupNotificationChannelIfNeeded(String id, String name) {
        if (notificationManager.getNotificationChannel(id) != null) {
            return;
        }

        NotificationChannel channel = null;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            channel = new NotificationChannel(
                    id,
                    name,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.enableVibration(true);
        }

        notificationManager.createNotificationChannel(channel);
    }

    private Notification buildNotification(
            String channelId,
            String title,
            String text,
            PendingIntent pendingIntent
    ) {
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, channelId)
                        .setContentTitle(title)
                        .setContentText(text)
                        .setSmallIcon(R.drawable.ic_sensor_service)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});

        if (pendingIntent != null) {
            notificationBuilder.setContentIntent(pendingIntent);
            notificationBuilder.setAutoCancel(true);
        }

        return notificationBuilder.build();
    }
}
