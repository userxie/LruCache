package com.networkstudy.phone.asynctest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Muzhou on 4/30/2017.
 */
public class newsAdapter extends BaseAdapter implements AbsListView.OnScrollListener {
    private List<NewsBean> mlist;
    private LayoutInflater mInflater;

    private int mStart,mEnd;
    public   static String [] URLS;//
    ImageLoader mImageLoader;
    private boolean mFirstIn;
    public newsAdapter(Context context, List<NewsBean>data, ListView listView){
        mlist=data;
        mInflater =LayoutInflater.from(context);
        mImageLoader =new ImageLoader(listView);
        URLS =new String[data.size()];
        for(int i=0;i<data.size();i++){
            URLS[i]=data.get(i).newsIconUrl;
        }
        listView.setOnScrollListener(this); //注册事件
        mFirstIn =true;
    }
    @Override
    public int getCount() {
        return mlist.size();
    }

    @Override
    public NewsBean getItem(int i) {
        return mlist.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder =null;
        if(view==null){
            viewHolder =new ViewHolder();
            view =mInflater.inflate(R.layout.item_layout,null);
            viewHolder.ivIcon= (ImageView) view.findViewById(R.id.image);
            viewHolder.title = (TextView) view.findViewById(R.id.tv_title);
            viewHolder.tvContent = (TextView) view.findViewById(R.id.tv_content);
            view.setTag(viewHolder);
        }else{
            viewHolder= (ViewHolder) view.getTag();
        }
       // viewHolder.ivIcon.setImageResource(R.mipmap.ic_launcher);

        String url =mlist.get(i).newsIconUrl;
        viewHolder.ivIcon.setTag(url);
      //  new ImageLoader().showImageByThread(viewHolder.ivIcon,url);

        mImageLoader.showImageByAsncTask(viewHolder.ivIcon,url);
        viewHolder.title.setText(mlist.get(i).newsTitle);
        viewHolder.tvContent.setText(mlist.get(i).newsContent);
        return view;
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int scrollState) {
        if(scrollState==SCROLL_STATE_IDLE){
            //加载可见项
            mImageLoader.loadImage(mStart,mEnd);
        }else{
            //停止任务
            mImageLoader.cancelAllTasks();
        }
    }

    @Override
    public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItem) {
        mStart =firstVisibleItem;
        mEnd =firstVisibleItem+visibleItemCount;
        if(mFirstIn && visibleItemCount>0){
            mImageLoader.loadImage(mStart,mEnd);
            mFirstIn=false;
        }
    }

    class ViewHolder{
        public TextView title;
        public TextView tvContent;
        public ImageView ivIcon;

    }
}
