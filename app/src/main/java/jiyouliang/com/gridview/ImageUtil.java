package jiyouliang.com.gridview;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jiyouliang.com.gridview.model.ImageBean;
/**
 * @author YouLiang.Ji
 */
public class ImageUtil {

    private static final String TAG = "ImageUtil";
    private static File mFileTemp;
    public static final String TEMP_PHOTO_FILE_NAME = "temp_photo.jpg";

    /**
     * 获取图片路径
     * @param context
     * @return
     */
    public static List<String> getGalleryImagePaths(Context context) {
        List<String> imagePaths = new ArrayList<String>();
        ContentResolver resolver = context.getContentResolver();
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = new String[]{MediaStore.Images.ImageColumns.MIME_TYPE, MediaStore.Images.ImageColumns.DATA};
        String order = MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC"; // 按时间排序
        Cursor cursor = resolver.query(uri, projection, null, null, order);
        //        Log.e("ImageUtil", "count = " + cursor.getCount());
        while (cursor.moveToNext()) {
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
            //            Log.e("ImageUtil", "path = " + path);
            if (!TextUtils.isEmpty(path))
                imagePaths.add(path);
        }
        return imagePaths;
    }

    /**
     * 获取图片路径，封装成需要的Bean对象
     * @param context
     * @param selectedList 已经选中的图片
     * @return
     */
    public static List<ImageBean> getImagePaths(Context context, List<String> selectedList) {
        List<ImageBean> imagePaths = new ArrayList<ImageBean>();
        ContentResolver resolver = context.getContentResolver();
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = new String[]{MediaStore.Images.ImageColumns.MIME_TYPE, MediaStore.Images.ImageColumns.DATA};
        String order = MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC"; // 按时间排序
        Cursor cursor = resolver.query(uri, projection, null, null, order);
        //        Log.e("ImageUtil", "count = " + cursor.getCount());
        while (cursor.moveToNext()) {
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
            if (path != null) {
                ImageBean imageBean = new ImageBean();
                imageBean.setUrl(path);
                imagePaths.add(imageBean);
                imageBean.setChecked(false);
                setCheckBoxStatus(imageBean, selectedList);
            }
        }
        return imagePaths;
    }

    public static void setCheckBoxStatus(ImageBean imageBean, List<String> selectedList) {
        if(selectedList != null && selectedList.size() > 0){
            for(int i = 0; i < selectedList.size(); i ++){
                if(imageBean.getUrl().equals(selectedList.get(i))){
                    imageBean.setChecked(true);
                    return;
                }
                imageBean.setChecked(false);
            }
        }else{
            imageBean.setChecked(false);
        }
    }

    /**
     * ImageLoader
     * @param context
     * @param drawableId
     * @param failDrawableId
     * @param cacheOnDisk
     * @param cacheInMemory
     * @return
     */
    public static DisplayImageOptions initImageLoader(Context context, int drawableId, int failDrawableId, boolean cacheOnDisk, boolean cacheInMemory) {
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(context));
        DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
        if (drawableId != 0)
            builder.showImageForEmptyUri(drawableId);
        if (failDrawableId != 0)
            builder.showImageOnFail(failDrawableId);
        builder.resetViewBeforeLoading(true).cacheOnDisk(cacheOnDisk).cacheInMemory(cacheInMemory).imageScaleType(ImageScaleType.EXACTLY).bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true).displayer(new FadeInBitmapDisplayer(300));
        return builder.build();
    }

    /** 获取屏幕宽度 px */
    public static int getScreenWidth(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

}
