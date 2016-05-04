package com.example.asus.mygson;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.example.asus.mygson.adpter.MyAdpter;
import com.example.asus.mygson.bean.News;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ListView mListView;
    private Context mContext;
    static final String URLS="http://op.juhe.cn/onebox/news/words?key=b1cccdf6af5cd58b7ed712a9ccb3af46";
    Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==1){
                List<String> list= (List<String>) msg.obj;
                if(list!=null){
                    MyAdpter adpter=new MyAdpter(list,mContext);
                    mListView.setAdapter(adpter);
                }
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListView=(ListView)findViewById(R.id.list);
        mContext=this;
        MyThread myThread=new MyThread();
        myThread.start();
    }
    class MyThread extends Thread{
        @Override
        public void run() {
            try {
                URL url=new URL(URLS);
                URLConnection connection=url.openConnection();
                HttpURLConnection httpURLConnection= (HttpURLConnection) connection;
                httpURLConnection.setRequestMethod("GET");//设置网络请求的一个方式
                httpURLConnection.setRequestProperty("Accept-Charset","UTF-8");//编码格式
                int code=httpURLConnection.getResponseCode();//获得网络连接的返回值
                if(code==HttpURLConnection.HTTP_OK){
                    InputStream inputStream=httpURLConnection.getInputStream();
                    BufferedReader reader=new BufferedReader(new InputStreamReader(inputStream));
                    String str=reader.readLine();
                    while(reader.readLine()!=null){
                        str=str+reader.readLine();
                    }
                    Log.d("返回值",str);
                    Gson gson=new Gson();
                    News news=gson.fromJson(str,News.class);
                    List<String> list=news.getResult();
                    Message msg=mHandler.obtainMessage();
                    msg.obj=list;
                    msg.what=1;
                    mHandler.sendMessage(msg);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
