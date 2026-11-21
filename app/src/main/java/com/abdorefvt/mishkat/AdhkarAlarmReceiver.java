package com.abdorefvt.mishkat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.provider.Settings;
import android.os.Build;
import androidx.core.app.NotificationCompat;

public class AdhkarAlarmReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "adhkar_sound_v2";

    private static final String[] VERSES = {
        "فَمَن تَبِعَ هُدَايَ فَلَا خَوْفٌ عَلَيْهِمْ وَلَا هُمْ يَحْزَنُونَ",
        "فَلَوْلَا فَضْلُ اللَّهِ عَلَيْكُمْ وَرَحْمَتُهُ لَكُنتُم مِّنَ الْخَاسِرِينَ",
        "أَوَلَا يَعْلَمُونَ أَنَّ اللَّهَ يَعْلَمُ مَا يُسِرُّونَ وَمَا يُعْلِنُونَ",
        "وَاللَّهُ يَخْتَصُّ بِرَحْمَتِهِ مَن يَشَاءُ وَاللَّهُ ذُو الْفَضْلِ الْعَظِيمِ",
        "وَعَسَىٰ أَن تَكْرَهُوا شَيْئًا وَهُوَ خَيْرٌ لَّكُمْ",
        "وَاتَّقُوا يَوْمًا تُرْجَعُونَ فِيهِ إِلَى اللَّهِ",
        "إِذَا قَضَىٰ أَمْرًا فَإِنَّمَا يَقُولُ لَهُ كُن فَيَكُونُ",
        "وَسَارِعُوا إِلَىٰ مَغْفِرَةٍ مِّن رَّبِّكُمْ وَجَنَّةٍ عَرْضُهَا السَّمَاوَاتُ وَالْأَرْضُ",
        "سَيَجْعَلُ اللَّهُ بَعْدَ عُسْرٍ يُسْرًا",
        "وَمَن يَتَّقِ اللَّهَ يَجْعَل لَّهُ مِنْ أَمْرِهِ يُسْرًا",
        "وَفِي السَّمَاءِ رِزْقُكُمْ وَمَا تُوعَدُونَ",
        "يَا أَيُّهَا الَّذِينَ آمَنُوا اجْتَنِبُوا كَثِيرًا مِّنَ الظَّنِّ إِنَّ بَعْضَ الظَّنِّ إِثْمٌ",
        "لِّيَغْفِرَ لَكَ اللَّهُ مَا تَقَدَّمَ مِن ذَنبِكَ وَمَا تَأَخَّرَ",
        "وَمَا ظَلَمْنَاهُمْ وَلَٰكِن كَانُوا هُمُ الظَّالِمِينَ",
        "وَلَوْ بَسَطَ اللَّهُ الرِّزْقَ لِعِبَادِهِ لَبَغَوْا فِي الْأَرْضِ",
        "وَهُوَ الَّذِي يَقْبَلُ التَّوْبَةَ عَنْ عِبَادِهِ وَيَعْفُو عَنِ السَّيِّئَاتِ",
        "إِنَّ اللَّهَ لَذُو فَضْلٍ عَلَى النَّاسِ وَلَٰكِنَّ أَكْثَرَ النَّاسِ لَا يَشْكُرُونَ",
        "فَاصْبِرْ إِنَّ وَعْدَ اللَّهِ حَقٌّ",
        "وَيُنَجِّي اللَّهُ الَّذِينَ اتَّقَوْا بِمَفَازَتِهِمْ لَا يَمَسُّهُمُ السُّوءُ وَلَا هُمْ يَحْزَنُونَ",
        "وَأَنِيبُوا إِلَىٰ رَبِّكُمْ وَأَسْلِمُوا لَهُ",
        "أَوَلَمْ يَعْلَمُوا أَنَّ اللَّهَ يَبْسُطُ الرِّزْقَ لِمَن يَشَاءُ وَيَقْدِرُ"
    };

    private static final String[] ADHKAR = {
        "سُبْحَانَ اللَّهِ",
        "أَسْتَغْفِرُ اللَّهَ الْعَظِيمَ وَأَتُوبُ إِلَيْهِ",
        "لَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ",
        "اللَّهُمَّ صَلِّ وَسَلِّمْ عَلَىٰ نَبِيِّنَا مُحَمَّدٍ",
        "لَا إِلَٰهَ إِلَّا اللَّهُ",
        "الْحَمْدُ لِلَّهِ حَمْدًا كَثِيرًا طَيِّبًا مُبَارَكًا فِيهِ",
        "اللَّهُمَّ اغْفِرْ لِي وَارْحَمْنِي"
    };

    @Override public void onReceive(Context context, Intent intent) {
        long scheduledAt = intent != null ? intent.getLongExtra("scheduled_at", 0L) : 0L;
        if (scheduledAt <= 0L) scheduledAt = System.currentTimeMillis();

        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTimeInMillis(scheduledAt);
        int minute = cal.get(java.util.Calendar.MINUTE);
        int hour = cal.get(java.util.Calendar.HOUR_OF_DAY);
        boolean isVerseTime = minute == 0 && hour % 2 == 0;

        android.content.SharedPreferences prefs = context.getSharedPreferences("mishkat", Context.MODE_PRIVATE);
        String message;
        if (isVerseTime) {
            int index = prefs.getInt("verse_index", 0) % VERSES.length;
            message = "﴿" + VERSES[index] + "﴾";
            prefs.edit().putInt("verse_index", (index + 1) % VERSES.length).apply();
        } else {
            int index = prefs.getInt("dhikr_index", 0) % ADHKAR.length;
            message = ADHKAR[index];
            prefs.edit().putInt("dhikr_index", (index + 1) % ADHKAR.length).apply();
        }

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID, "إشعارات مشكاة", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("تنبيهات الآيات والأذكار مع صوت الإشعار الافتراضي");
            channel.setSound(Settings.System.DEFAULT_NOTIFICATION_URI,
                new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build());
            manager.createNotificationChannel(channel);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
            new Intent(context, MainActivity.class),
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder notification = new NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_mishkat_notification)
            .setContentTitle("Mishkat")
            .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
            .setDefaults(NotificationCompat.DEFAULT_SOUND | NotificationCompat.DEFAULT_VIBRATE)
            .setContentText(message)
            .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent);

        int notificationId = 4100 + (int)((scheduledAt / (30L * 60L * 1000L)) % 1000L);
        manager.notify(notificationId, notification.build());

        AdhkarScheduler.schedule(context);
    }

}
