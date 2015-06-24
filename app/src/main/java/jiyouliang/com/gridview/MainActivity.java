package jiyouliang.com.gridview;

import android.app.Activity;
import android.app.ActivityGroup;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author YouLiang.Ji
 */
public class MainActivity extends ActionBarActivity {

    private GridView gridView;
    private Context context;
    private List<String> urlList;
    private MyGridViewAdapter mAdapter;
    private static final int REQUEST_CODE_ALUMB = 1;
    private static final String TAG = "MainActivity";
    public static final String EXTRA_KEY_IMAGE_LIST = "jiyouliang.com.gridview.MainActivity_iamge_list";
    private DisplayImageOptions options;
    private String ADD_IMG = "add_img";

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        initImageLoader();
        urlList = new ArrayList<String>();
        gridView = (GridView)findViewById(R.id.gridView);
        mAdapter = new MyGridViewAdapter(context, urlList);
        gridView.setAdapter(mAdapter);
        mAdapter.insert(ADD_IMG, 0);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (position == urlList.size() - 1) {//点击最后一张图片，打开相册
                    Intent intent = new Intent(context, ActivityGallery.class);
                    if(urlList.size() > 1){
                        intent.putStringArrayListExtra(EXTRA_KEY_IMAGE_LIST, (ArrayList<String>) urlList);
                    }
                    startActivityForResult(intent, REQUEST_CODE_ALUMB);
                }
            }
        });
    }

    class MyGridViewAdapter extends ArrayAdapter<String>{

        private final RelativeLayout.LayoutParams params;

        public MyGridViewAdapter(Context context, List<String> objects) {
            super(context, 0, objects);
            int screenWidth = ImageUtil.getScreenWidth(getContext());// 屏幕宽度px
            int itemPadding = (int) context.getResources().getDimension(R.dimen.gridPadding); // px
            // 计算每张图片大小：屏幕宽度 - GridView右边距 - GridView每个item的边距 * 4，结果除以4， 就是每张图片的大小（px）
            int imageSize = (screenWidth - itemPadding - itemPadding * 4) / 4;
            params = new RelativeLayout.LayoutParams(imageSize, imageSize);
        }

        @Override public View getView(int position, View convertView, ViewGroup parent) {
            BaseViewHolder holder = BaseViewHolder.getViewHolder(getContext(), convertView, parent,R.layout.gridview_item_layout, position);
            ImageView image = (ImageView) holder.getView(R.id.image);
            ImageView addImg = (ImageView) holder.getView(R.id.addImg);
            image.setLayoutParams(params);// 设置图片大小
            addImg.setLayoutParams(params);// 设置图片大小
            if(position == getCount() - 1){// 最后一张图片显示添加图片
                image.setVisibility(View.GONE);
                addImg.setVisibility(View.VISIBLE);// 显示添加图片
            }else{
                image.setVisibility(View.VISIBLE);
                addImg.setVisibility(View.GONE);
                ImageLoader.getInstance().displayImage("file://" + getItem(position), image);
            }
            return holder.getConvertView();
        }

        @Override public void registerDataSetObserver(DataSetObserver observer) {
            super.registerDataSetObserver(observer);
        }
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_ALUMB){
            String path = data.getStringExtra(ActivityGallery.EXTRA_KEY_IMAGE_URI);
            List<String>imageList = (List<String>) data.getSerializableExtra(ActivityGallery.EXTRA_KEY_IMAGE_LIST);
            urlList.clear();
            urlList.addAll(imageList);
            urlList.add(ADD_IMG);
            mAdapter.notifyDataSetChanged();
            printList();
        }
    }

    private void initImageLoader(){
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(context));
        DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
        builder.resetViewBeforeLoading(true).cacheOnDisk(true).cacheInMemory(true).imageScaleType(ImageScaleType.EXACTLY).bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true).displayer(new FadeInBitmapDisplayer(30));
        options = builder.build();
    }

    private void printList(){
        for(String path : urlList){
            Log.e(TAG, "path = " + path);
        }
    }
}
