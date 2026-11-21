package com.abdorefvt.mishkat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import androidx.core.app.NotificationCompat;

public final class AudioKeepAliveService extends Service {
    private static final String CHANNEL_ID = "mishkat_audio_playback";
    private static final int NOTIFICATION_ID = 2401;
    private PowerManager.WakeLock wakeLock;

    @Override public void onCreate() {
        super.onCreate();
        createChannel();
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_mishkat_notification)
            // Keep only the service description; Android already shows the app name.
            .setContentText("التلاوة مستمرة في الخلفية")
            .setCategory(NotificationCompat.CATEGORY_TRANSPORT)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build();
        if (Build.VERSION.SDK_INT >= 29) {
            startForeground(NOTIFICATION_ID, notification,
                android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK);
        } else {
            startForeground(NOTIFICATION_ID, notification);
        }

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        if (powerManager != null) {
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "Mishkat:AudioPlayback");
            wakeLock.setReferenceCounted(false);
            wakeLock.acquire(6L * 60L * 60L * 1000L);
        }
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID, "تشغيل الصوت", NotificationManager.IMPORTANCE_LOW);
            channel.setDescription("إبقاء تشغيل التلاوة متاحًا عند الانتقال إلى تطبيق آخر");
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) manager.createNotificationChannel(channel);
        }
    }

    @Override public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override public void onDestroy() {
        if (wakeLock != null && wakeLock.isHeld()) wakeLock.release();
        super.onDestroy();
    }

    @Override public IBinder onBind(Intent intent) { return null; }
}
