package com.app.yoo.baiducdn;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.webkit.CookieManager;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private TextView tv_machines,tv_speeds,tv_todayout,tv_yesterdayout,tv_allout,tv_detail;
    private ListView lv_machines;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findView();
        new GetDataTask().execute((Void) null);
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                new GetDataTask().execute((Void) null);
                handler.postDelayed(this,1000);
            }
        };
        handler.postDelayed(runnable,1000);
    }

    public void findView(){
        tv_machines = findViewById(R.id.tv_machines);
        tv_speeds = findViewById(R.id.tv_speeds);
        tv_todayout = findViewById(R.id.tv_todayout);
        tv_yesterdayout = findViewById(R.id.tv_yesterdayout);
        tv_allout = findViewById(R.id.tv_allout);
        tv_detail = findViewById(R.id.tv_detail);
        lv_machines = findViewById(R.id.lv_machines);
    }
    public class GetDataTask extends AsyncTask<Void,Void,String>{

        private String strURL = "http://baijin.baidu.com/site/mining";
        private String strURL2 = "http://baijin.baidu.com/site/account";
        private Call call,call2;

        GetDataTask(){
            CookieManager cookieManager = CookieManager.getInstance();
            String cookieStr = cookieManager.getCookie(LoginActivity.homeURL);
            OkHttpClient okHttpClient = new OkHttpClient();
            Request request = new Request.Builder().url(strURL).header("Cookie",cookieStr).build();
            call = okHttpClient.newCall(request);
            Request request2 = new Request.Builder().url(strURL2).header("Cookie",cookieStr).build();
            call2 = okHttpClient.newCall(request2);
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Document document = Jsoup.parse(s);
            Elements spans = document.getElementsByClass("item-value");
            Elements item_unit = document.getElementsByClass("item-unit");
            if(spans.size()>7 && item_unit.size()>7){
                tv_machines.setText("机器数:" + spans.get(0).text() + item_unit.get(0).text());
                tv_speeds.setText("总上传速度:" + spans.get(1).text() + item_unit.get(1).text());
                tv_todayout.setText("今日产出:" + spans.get(2).text() + item_unit.get(2).text());
                tv_yesterdayout.setText("昨日产出:" + spans.get(3).text() + item_unit.get(3).text());
                tv_allout.setText("历史产出:" + spans.get(4).text() + item_unit.get(4).text());
            }
            Elements spans2 = document.getElementsByClass("item-detail");
            if(spans2.size()>3){
                tv_detail.setText("收入(税前):" + spans.get(6).text() + "元\n" + spans2.get(1).text());
            }
            List<Map<String,String>> list = new ArrayList<Map<String, String>>();
            //Elements jss = document.getElementsByTag("script");

            SimpleAdapter adapter = new SimpleAdapter(MainActivity.this,list,R.layout.lv_machines,
                    new String[]{"xh","ip","speed","disk","to","yo","ao","time"},
                    new int[]{R.id.lv_xh,R.id.lv_ip,R.id.lv_speed,R.id.lv_disk,R.id.lv_to,R.id.lv_yo,R.id.lv_ao,R.id.lv_time} );
        }

        @Override
        protected String doInBackground(Void... voids) {
            try{
                Response response = call.execute();
                Response response2 = call2.execute();
                String result = response.body().string() + response2.body().string();
                return result;
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }
}
