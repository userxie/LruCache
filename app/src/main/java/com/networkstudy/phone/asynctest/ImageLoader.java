package com.networkstudy.phone.asynctest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.LruCache;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
/**
 * Created by Muzhou on 4/30/2017.
 */
public class ImageLoader {
    private ImageView mImageView;
    private String murl;

    private LruCache<String,Bitmap> mCache;

    private ListView mListView;
    private Set<NewsAsyncTask> mTask;
    public ImageLoader(ListView listView){
        //获取最大可用内存

        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize =maxMemory/4;
        mCache =new LruCache<String,Bitmap>(cacheSize){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                //每次存入缓存的时候调用
                return value.getByteCount();
            }
        };
        mListView=listView;
        mTask =new HashSet<>();
    }
    public void addBitmapToCache(String url,Bitmap bitmap){

        if(getBitmapFromCache(url)==null){
            mCache.put(url,bitmap);
        }
    }
    public Bitmap getBitmapFromCache(String url){
        return  mCache.get(url);
    }
    private Handler mHandler =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(mImageView.getTag().equals(murl))
            mImageView.setImageBitmap((Bitmap) msg.obj);
        }
    };
    public void showImageByThread(ImageView imageView, final String url){
        mImageView =imageView;
        murl=url;
        new Thread(){

            @Override
            public void run() {
                super.run();
                Bitmap bitmap =getBitmapFromURL(url);
                Message message =Message.obtain();
                message.obj =bitmap;
                mHandler.sendMessage(message);
            }
        }.start();
    }

    public Bitmap getBitmapFromURL(String urlString){
        Bitmap bitmap;
        InputStream is = null;
        try{
            URL url =new URL(urlString);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            is =new BufferedInputStream(httpURLConnection.getInputStream());
            bitmap = BitmapFactory.decodeStream(is);
            httpURLConnection.disconnect();
            return bitmap;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void showImageByAsncTask(ImageView imageView,String url){
        Bitmap bitmap=getBitmapFromCache(url);
        if(bitmap==null){
            imageView.setImageResource(R.mipmap.ic_launcher);
        }else{
            imageView.setImageBitmap(bitmap);
        }
    }
    private class NewsAsyncTask extends AsyncTask<String ,Void,Bitmap>{

//        private  ImageView mImageView;
        private String murl;
        public NewsAsyncTask(String url) {
//            mImageView=imageView;
            murl=url;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            return getBitmapFromURL(strings[0]);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if(bitmap!=null){
                addBitmapToCache(murl,bitmap);
            }
            ImageView imageView = (ImageView) mListView.findViewWithTag(murl);
            if(imageView!=null&&bitmap!=null)
                imageView.setImageBitmap(bitmap);
            mTask.remove(this);
        }
    }

    public void loadImage(int start,int end){
        for(int i=start;i<end;i++){
            String url =newsAdapter.URLS[i];
            Bitmap bitmap =getBitmapFromCache(url);
            if(bitmap==null){
                NewsAsyncTask task =new NewsAsyncTask(url);
                task.execute(url);
                mTask.add(task);
            }else{
                ImageView imageView = (ImageView) mListView.findViewWithTag(url);
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    public void cancelAllTasks(){
        if(mTask!=null){
            for(NewsAsyncTask task:mTask){
                task.cancel(false);
            }
        }
    }
}
