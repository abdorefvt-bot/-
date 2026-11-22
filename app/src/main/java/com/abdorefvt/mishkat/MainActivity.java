package com.abdorefvt.mishkat;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity {
    private WebView webView;
    private boolean stopAudioReceiverRegistered = false;

    private final BroadcastReceiver stopAudioReceiver = new BroadcastReceiver() {
        @Override public void onReceive(Context context, Intent intent) {
            pauseWebAudio();
        }
    };

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        webView = new WebView(this);
        setContentView(webView);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setMediaPlaybackRequiresUserGesture(false);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.addJavascriptInterface(new AudioBridge(), "MishkatAudio");
        webView.setWebViewClient(new WebViewClient() {
            @Override public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                installAudioTracking();
            }
        });
        webView.setWebChromeClient(new WebChromeClient());

        if (state == null) webView.loadUrl("https://mishkatt.framer.website");
        else webView.restoreState(state);

        registerStopAudioReceiver();
        requestNotificationPermission();
        requestExactAlarmPermissionIfNeeded();
        AdhkarScheduler.schedule(this);
    }

    private void registerStopAudioReceiver() {
        IntentFilter filter = new IntentFilter(AudioKeepAliveService.ACTION_STOP_WEB_AUDIO);
        if (Build.VERSION.SDK_INT >= 33) {
            registerReceiver(stopAudioReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(stopAudioReceiver, filter);
        }
        stopAudioReceiverRegistered = true;
    }

    private void installAudioTracking() {
        String script = "(function(){"
            + "if(window.__mishkatAudioTracking)return;window.__mishkatAudioTracking=true;"
            + "function report(){try{var a=[].slice.call(document.querySelectorAll('audio,video'));"
            + "var playing=a.some(function(x){return !x.paused&&!x.ended;});"
            + "window.MishkatAudio.onPlaybackChanged(playing);}catch(e){}}"
            + "function bind(el){if(el.__mishkatBound)return;el.__mishkatBound=true;"
            + "['play','playing','pause','ended','emptied'].forEach(function(ev){el.addEventListener(ev,report);});}"
            + "function scan(){document.querySelectorAll('audio,video').forEach(bind);report();}"
            + "scan();new MutationObserver(scan).observe(document.documentElement,{childList:true,subtree:true});"
            + "setInterval(scan,2000);})();";
        webView.evaluateJavascript(script, null);
    }

    private void pauseWebAudio() {
        if (webView != null) {
            webView.evaluateJavascript(
                "document.querySelectorAll('audio,video').forEach(function(m){m.pause();});", null);
        }
    }

    private final class AudioBridge {
        @JavascriptInterface public void onPlaybackChanged(boolean playing) {
            runOnUiThread(() -> {
                if (isFinishing()) return;
                Intent service = new Intent(MainActivity.this, AudioKeepAliveService.class);
                if (playing) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) startForegroundService(service);
                    else startService(service);
                } else {
                    stopService(service);
                }
            });
        }
    }

    @Override protected void onResume() {
        super.onResume();
        AdhkarScheduler.schedule(this);
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= 33 &&
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 100);
        }
    }

    private void requestExactAlarmPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            if (alarmManager != null && !alarmManager.canScheduleExactAlarms()) {
                try {
                    Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                        Uri.parse("package:" + getPackageName()));
                    startActivity(intent);
                } catch (Exception ignored) {
                    // Scheduler uses an inexact idle-allowed fallback when exact alarms are not granted.
                }
            }
        }
    }

    @Override protected void onSaveInstanceState(Bundle out) {
        if (webView != null) webView.saveState(out);
        super.onSaveInstanceState(out);
    }

    @Override public void onBackPressed() {
        if (webView != null && webView.canGoBack()) webView.goBack();
        else moveTaskToBack(true);
    }

    @Override protected void onDestroy() {
        if (isFinishing()) {
            pauseWebAudio();
            stopService(new Intent(this, AudioKeepAliveService.class));
        }
        if (stopAudioReceiverRegistered) {
            try { unregisterReceiver(stopAudioReceiver); } catch (Exception ignored) {}
        }
        if (isFinishing() && webView != null) webView.destroy();
        super.onDestroy();
    }
}