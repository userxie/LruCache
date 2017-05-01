package com.networkstudy.phone.asynctest;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView mListView;
    private static String URL ="http://www.imooc.com/api/teacher?type=4&num=30";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListView= (ListView) findViewById(R.id.list);
        List<NewsBean> list;
        new NewsAsyncTask().execute(URL);


    }

    private List<NewsBean> getJsonData(String url){
        List<NewsBean> newsBeanList =new ArrayList<NewsBean>();
        try {
            String jsonString =readStream(new URL(url).openStream());

            JSONObject jsonObject;
            NewsBean newsBean;
            jsonObject =new JSONObject(jsonString);

            JSONArray jsonArray =jsonObject.getJSONArray("data");
            for(int i=0;i<jsonArray.length();i++){
                jsonObject =jsonArray.getJSONObject(i);
                newsBean =new NewsBean();
                newsBean.newsIconUrl=jsonObject.getString("picSmall");
                newsBean.newsTitle=jsonObject.getString("name");
                newsBean.newsContent =jsonObject.getString("description");
                newsBeanList.add(newsBean);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return newsBeanList;

    }

    private String readStream(InputStream is){
        InputStreamReader isr;
        StringBuilder result=new StringBuilder("");
        try{
            String line="";
            isr=new InputStreamReader(is,"utf-8");
            BufferedReader br =new BufferedReader(isr);
            while((line=br.readLine())!=null){
                result.append(line);
            }
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.toString();
    }


    class NewsAsyncTask extends AsyncTask<String,Void,List<NewsBean>> {

        @Override
        protected List<NewsBean> doInBackground(String... strings) {
            return getJsonData(strings[0]);
        }
        @Override
        protected void onPostExecute(List<NewsBean> newsBeen) {
            super.onPostExecute(newsBeen);
            newsAdapter adapter =new newsAdapter(MainActivity.this,newsBeen,mListView);
            mListView.setAdapter(adapter);
        }
    }
}
