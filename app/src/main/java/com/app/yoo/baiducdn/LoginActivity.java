package com.app.yoo.baiducdn;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    public final static String homeURL = "http://baijin.baidu.com/";

    private WebView wv_login;
    private OkHttpClient okHttpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        wv_login = findViewById(R.id.wv_login);

        configWebView(wv_login, false);
        synCookies(this, homeURL, "uid=11");
        synCookies(this, homeURL, "name=xyw");
        synCookies(this, homeURL, "agent=android");
        synCookies(this, homeURL, "age=20;sex=1;time=today");

        wv_login.loadUrl(homeURL);

        new CheckLoginStatus().execute((Void) null);

    }

    public class CheckLoginStatus extends AsyncTask<Void,Void,Boolean>{
        @Override
        protected void onPostExecute(Boolean has) {
            super.onPostExecute(has);
            if(has){
                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                startActivity(intent);
            }
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            CookieManager cookieManager = CookieManager.getInstance();
            String cookieStr = cookieManager.getCookie(homeURL);
            okHttpClient = new OkHttpClient();
            Request request = new Request.Builder().url(homeURL).header("Cookie",cookieStr).build();
            Call call = okHttpClient.newCall(request);
            try{
                Response response = call.execute();
                String result = response.body().string();
                if(result.contains("moismeQQ")){
                    return true;
                }else{
                    return false;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return false;
        }
    }


    protected void configWebView(WebView webView, boolean needCache) {

        if (!needCache) {
            webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        }

        webView.getSettings().setJavaScriptEnabled(true);
        //启用数据库
        webView.getSettings().setDatabaseEnabled(true);
        //设置定位的数据库路径
        String dir = this.getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath();
        webView.getSettings().setGeolocationDatabasePath(dir);
        //启用地理定位
        webView.getSettings().setGeolocationEnabled(true);
        //开启DomStorage缓存
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.setWebViewClient(new WebViewClient(){});
        webView.setWebChromeClient(new WebChromeClient());
        //支持javascript
        webView.getSettings().setJavaScriptEnabled(true);
        // 设置可以支持缩放
        webView.getSettings().setSupportZoom(true);
        // 设置出现缩放工具
        webView.getSettings().setBuiltInZoomControls(true);
        //扩大比例的缩放
        webView.getSettings().setUseWideViewPort(true);
        /*//自适应屏幕
        web.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
        web.getSettings().setLoadWithOverviewMode(true);*/
    }

    /**
     * 设置Cookie
     *
     * @param context
     * @param url
     * @param cookie  格式：uid=21233 如需设置多个，需要多次调用
     */
    public void synCookies(Context context, String url, String cookie) {
        CookieSyncManager.createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setCookie(url, cookie);//cookies是在HttpClient中获得的cookie
        CookieSyncManager.getInstance().sync();
    }

    /**
     * 清除Cookie
     *
     * @param context
     */
    public static void removeCookie(Context context) {
        CookieSyncManager.createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
        CookieSyncManager.getInstance().sync();
    }

}
