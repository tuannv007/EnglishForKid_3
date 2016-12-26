package data.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.util.ArrayList;
import java.util.List;

import data.model.DataModel;

/**
 * Created by Nhahv on 12/26/2016.
 * <.
 */
public class SQLiteCommon extends SQLiteHelper {
    public static final int TYPE_TABLE_SONGS = 1;
    public static final int TYPE_TABLE_SHORT_STORIES = 2;
    public static String[] mAllColumn =
        {SQLiteHelper.FIELD_COLUMN_ID, SQLiteHelper.FIELD_NAME, SQLiteHelper.FIELD_IMAGE_URL,
            SQLiteHelper.FIELD_PATH_RENDER, SQLiteHelper.FIELD_URL_MP4};
    private SQLiteDatabase mSqLiteDatabase;

    public SQLiteCommon(Context context) {
        super(context);
    }

    public void saveListDataModel(List<DataModel> dataModels, int type) {
        if (dataModels == null) return;
        try {
            mSqLiteDatabase = getWritableDatabase();
            deleteDataModel(type);
            for (DataModel dataModel : dataModels) saveDataModel(dataModel, type);
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            mSqLiteDatabase.close();
        }
    }

    private void saveDataModel(DataModel dataModel, int type) {
        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.FIELD_NAME, dataModel.getName());
        values.put(SQLiteHelper.FIELD_IMAGE_URL, dataModel.getImageUrl());
        values.put(SQLiteHelper.FIELD_PATH_RENDER, dataModel.getPathRender());
        values.put(SQLiteHelper.FIELD_URL_MP4, dataModel.getUrlMp4());
        mSqLiteDatabase.insert(getTableName(type), null, values);
    }

    public void updateDataModel(DataModel dataModel, int type) {
        try {
            mSqLiteDatabase = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(SQLiteHelper.FIELD_COLUMN_ID, dataModel.getId());
            values.put(SQLiteHelper.FIELD_NAME, dataModel.getName());
            values.put(SQLiteHelper.FIELD_IMAGE_URL, dataModel.getImageUrl());
            values.put(SQLiteHelper.FIELD_PATH_RENDER, dataModel.getPathRender());
            values.put(SQLiteHelper.FIELD_URL_MP4, dataModel.getUrlMp4());
            mSqLiteDatabase.update(getTableName(type), values,
                SQLiteHelper.FIELD_COLUMN_ID + " = " + dataModel.getId(), null);
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            mSqLiteDatabase.close();
        }
    }

    private void deleteDataModel(int type) {
        mSqLiteDatabase.delete(getTableName(type), null, null);
    }

    public List<DataModel> getListDataModel(int type) {
        List<DataModel> listDataModel = new ArrayList<>();
        try {
            mSqLiteDatabase = getReadableDatabase();
            String table = getTableName(type);
            Cursor cursor = mSqLiteDatabase.query(table, mAllColumn, null, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    DataModel dataModel = new DataModel(cursor);
                    listDataModel.add(dataModel);
                    cursor.moveToNext();
                }
                cursor.close();
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            mSqLiteDatabase.close();
        }
        return listDataModel;
    }

    private String getTableName(int type) {
        return (type == TYPE_TABLE_SONGS) ? SQLiteHelper.TABLE_SONGS : SQLiteHelper
            .TABLE_SHORT_STORIES;
    }
}
