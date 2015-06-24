package jiyouliang.com.gridview.model;

import java.io.Serializable;
/**
 * ͼƬ
 */
public class ImageBean implements Serializable{
    private boolean checked;
    private String url;

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override public String toString() {
        return "ImageBean{" +
                "checked=" + checked +
                ", url='" + url + '\'' +
                '}';
    }
}
