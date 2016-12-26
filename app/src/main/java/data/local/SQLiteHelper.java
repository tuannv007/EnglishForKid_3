package data.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Nhahv on 12/26/2016.
 * <></>
 */
public class SQLiteHelper extends SQLiteOpenHelper {
    public static final String TABLE_SONGS = "songs";
    public static final String TABLE_SHORT_STORIES = "shortStories";
    public static final String FIELD_COLUMN_ID = "id";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_IMAGE_URL = "imageUrl";
    public static final String FIELD_PATH_RENDER = "pathRender";
    public static final String FIELD_URL_MP4 = "urlMp4";
    private static final String DATABASE_NAME = "EnglishForKids.db";
    private static final int DATABASE_VERSION = 1;
    private final String DATABASE_CREATE_SONGS = "Create table "
        + TABLE_SONGS + "( "
        + FIELD_COLUMN_ID + " integer primary key autoincrement, "
        + FIELD_NAME + " text not null,"
        + FIELD_IMAGE_URL + " text,"
        + FIELD_PATH_RENDER + " text,"
        + FIELD_URL_MP4 + " text" + ");";
    private final String DATABASE_CREATE_SHORT_STORIES = "Create table "
        + TABLE_SHORT_STORIES + "( "
        + FIELD_COLUMN_ID + " integer primary key autoincrement, "
        + FIELD_NAME + " text not null,"
        + FIELD_IMAGE_URL + " text,"
        + FIELD_PATH_RENDER + " text,"
        + FIELD_URL_MP4 + " text" + ");";

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DATABASE_CREATE_SONGS);
        sqLiteDatabase.execSQL(DATABASE_CREATE_SHORT_STORIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_SONGS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_SHORT_STORIES);
        onCreate(sqLiteDatabase);
    }
}
