package com.abdorefvt.mishkat;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
public final class AdhkarScheduler {
 private AdhkarScheduler(){}
 public static void schedule(Context c){
  AlarmManager am=(AlarmManager)c.getSystemService(Context.ALARM_SERVICE);
  Intent i=new Intent(c,AdhkarAlarmReceiver.class);
  PendingIntent pi=PendingIntent.getBroadcast(c,3001,i,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
  long first=System.currentTimeMillis()+30L*60L*1000L, interval=30L*60L*1000L;
  if(Build.VERSION.SDK_INT>=23)am.setInexactRepeating(AlarmManager.RTC_WAKEUP,first,interval,pi);else am.setRepeating(AlarmManager.RTC_WAKEUP,first,interval,pi);
 }
}
