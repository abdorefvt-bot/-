package com.abdorefvt.mishkat;
import android.Manifest;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.content.pm.PackageManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
public class MainActivity extends Activity {
 private WebView webView;
 @Override public void onCreate(Bundle state) {
  super.onCreate(state); webView=new WebView(this); setContentView(webView);
  WebSettings s=webView.getSettings(); s.setJavaScriptEnabled(true); s.setDomStorageEnabled(true); s.setMediaPlaybackRequiresUserGesture(false);
  webView.setWebViewClient(new WebViewClient()); webView.setWebChromeClient(new WebChromeClient());
  if(state==null) webView.loadUrl("https://mishkatt.framer.website"); else webView.restoreState(state);
  AdhkarScheduler.schedule(this);
  if(Build.VERSION.SDK_INT>=33 && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)!=PackageManager.PERMISSION_GRANTED)
   requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS},100);
 }
 @Override protected void onSaveInstanceState(Bundle out){super.onSaveInstanceState(out);webView.saveState(out);}
 @Override public void onBackPressed(){if(webView!=null&&webView.canGoBack())webView.goBack();else super.onBackPressed();}
 @Override protected void onDestroy(){if(isFinishing()&&webView!=null)webView.destroy();super.onDestroy();}
}
