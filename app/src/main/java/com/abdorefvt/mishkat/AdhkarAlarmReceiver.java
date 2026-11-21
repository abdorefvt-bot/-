package com.abdorefvt.mishkat;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
public class AdhkarAlarmReceiver extends BroadcastReceiver {
 private static final String[] A={"سبحان الله","لا إله إلا الله","اللهم صل وسلم على نبينا محمد","لا حول ولا قوة إلا بالله","رب اغفر لي وارحمني","لا إله إلا الله"};
 @Override public void onReceive(Context c,Intent i){
  int x=c.getSharedPreferences("mishkat",0).getInt("dhikr_index",0);
  NotificationManager nm=(NotificationManager)c.getSystemService(Context.NOTIFICATION_SERVICE);
  if(Build.VERSION.SDK_INT>=26)nm.createNotificationChannel(new NotificationChannel("adhkar","الأذكار",NotificationManager.IMPORTANCE_DEFAULT));
  PendingIntent pi=PendingIntent.getActivity(c,0,new Intent(c,MainActivity.class),PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
  NotificationCompat.Builder n=new NotificationCompat.Builder(c,"adhkar").setSmallIcon(android.R.drawable.ic_dialog_info).setContentTitle("Mishkat").setContentText(A[x]).setStyle(new NotificationCompat.BigTextStyle().bigText(A[x])).setAutoCancel(true).setContentIntent(pi);
  nm.notify(4100,n.build()); c.getSharedPreferences("mishkat",0).edit().putInt("dhikr_index",(x+1)%A.length).apply();
 }
}
