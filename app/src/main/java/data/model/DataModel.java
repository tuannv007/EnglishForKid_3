package data.model;

import android.database.Cursor;

import java.io.Serializable;

import data.local.SQLiteHelper;

/**
 * Created by Nhahv on 12/21/2016.
 * <></>
 */
public class DataModel implements Serializable {
    private int mId;
    private String mName;
    private String mImageUrl;
    private String mPathRender;
    private String mUrlMp4;

    public DataModel() {
    }

    public DataModel(Cursor cursor) {
        mId =
            Integer.parseInt(cursor.getString(cursor.getColumnIndex(SQLiteHelper.FIELD_COLUMN_ID)));
        mName = cursor.getString(cursor.getColumnIndex(SQLiteHelper.FIELD_NAME));
        mImageUrl = cursor.getString(cursor.getColumnIndex(SQLiteHelper.FIELD_IMAGE_URL));
        mPathRender = cursor.getString(cursor.getColumnIndex(SQLiteHelper.FIELD_PATH_RENDER));
        mUrlMp4 = cursor.getString(cursor.getColumnIndex(SQLiteHelper.FIELD_URL_MP4));
    }

    public DataModel(String name, String image, String pathRender) {
        this.mName = name;
        this.mImageUrl = image;
        this.mPathRender = pathRender;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String image) {
        this.mImageUrl = image;
    }

    public String getPathRender() {
        return mPathRender;
    }

    public void setPathRender(String pathRender) {
        this.mPathRender = pathRender;
    }

    public String getUrlMp4() {
        return mUrlMp4;
    }

    public void setUrlMp4(String urlMp4) {
        this.mUrlMp4 = urlMp4;
    }

    public void setId(int id) {
        mId = id;
    }

    public int getId() {
        return mId;
    }
}
