package jiyouliang.com.gridview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import jiyouliang.com.gridview.model.ImageBean;

/**
 * 自定义相册
 * @author YouLiang.Ji
 */
public class ActivityGallery extends ActionBarActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    private GridView gridview;
    private DisplayImageOptions imageOptions;
    private List<ImageBean> pathList;
    public static final String EXTRA_KEY_IMAGE_URI = "jiyouliang.com.gridview_extra_key_image_uri";
    public static final String EXTRA_KEY_IMAGE_LIST = "jiyouliang.com.gridview_extra_key_image_uri_list";
    private GridViewAdapter mAdapter;
    private Button btnSelect;
    private List<String> selectedList;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_layout);
        gridview = (GridView) findViewById(R.id.gridview);
        btnSelect = (Button)findViewById(R.id.btnPositive);
        btnSelect.setOnClickListener(this);
        selectedList = (ArrayList<String>)getIntent().getSerializableExtra(MainActivity.EXTRA_KEY_IMAGE_LIST);
        pathList = new ArrayList<ImageBean>();
        imageOptions = ImageUtil.initImageLoader(this, 0, 0, true, true);
        // 读取图片路径是耗时操作，在子线程中处理
        new GalleryTask().execute();
        gridview.setOnItemClickListener(this);
        setBtnStatus();
    }

    @Override public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        ImageBean image = pathList.get(position);
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
        image.setChecked(!image.isChecked());
        checkBox.setChecked(image.isChecked());
        String uri = image.getUrl();
        setBtnStatus();
    }

    /**
     * 设置按钮背景
     */
    private void setBtnStatus(){
        List<String> checkedImageUri = getCheckedImageUri();
        boolean disalble = checkedImageUri != null && checkedImageUri.size() > 0;
        btnSelect.setBackgroundResource(disalble ? R.drawable.btn_selector : R.drawable.btn_disable);
        if(checkedImageUri != null && checkedImageUri.size() > 0){
            btnSelect.setText("确定("+checkedImageUri.size()+")");
        }else{
            btnSelect.setText("确定");
        }
    }

    /**
     * 获取选中的图片
     * @return
     */
    private List<String> getCheckedImageUri() {
        List<String> checkedList = new ArrayList<String>();
        for (int i = 0; i < pathList.size(); i++) {
            ImageBean image = pathList.get(i);
            if (image.isChecked())
                checkedList.add(image.getUrl());
        }
        return checkedList;
    }

    @Override public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnPositive:
                if(getCheckedImageUri() == null || getCheckedImageUri().size() == 0) return;
                Intent intent = new Intent();
                List<String> imageList = getCheckedImageUri();
                setResult(Activity.RESULT_OK, intent.putStringArrayListExtra(EXTRA_KEY_IMAGE_LIST, (ArrayList<String>) imageList));
                finish();
                break;
        }
    }

    class GridViewAdapter extends ArrayAdapter<ImageBean> {
        public GridViewAdapter(Context context, List<ImageBean> objects) {
            super(context, 0, objects);
        }

        @Override public View getView(int position, View convertView, ViewGroup parent) {
            BaseViewHolder holder = BaseViewHolder.getViewHolder(getContext(), convertView, parent, R.layout.grid_item, position);
            ImageView imageView = (ImageView) holder.getView(R.id.image_item);
            CheckBox checkbox = (CheckBox) holder.getView(R.id.checkbox);
            ImageBean item = getItem(position);
            checkbox.setChecked(item.isChecked());
            ImageLoader.getInstance().displayImage("file://" + item.getUrl(), imageView, imageOptions);
            return holder.getConvertView();
        }
    }

    /**
     * 异步任务，读取图片路径
     */
    class GalleryTask extends AsyncTask<Void, Void, List<ImageBean>> {
        public GalleryTask() {
            super();
        }

        @Override protected List<ImageBean> doInBackground(Void... voids) {
            // 获取图片路径
            return ImageUtil.getImagePaths(ActivityGallery.this, selectedList);
        }

        @Override protected void onPostExecute(List<ImageBean> imageList) {
            pathList.addAll(imageList);
            setBtnStatus();
            mAdapter = new GridViewAdapter(ActivityGallery.this, imageList);
            gridview.setAdapter(mAdapter);
            super.onPostExecute(imageList);
        }
    }

}
