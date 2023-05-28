package com.pkasemer.malai.Database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.pkasemer.malai.Models.MalSavedImage;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "MalDatabase.db";
    private static final String TABLE_NAME = "ImageData";

    // Column names
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_SLIDE_ID = "slide_id";
    private static final String COLUMN_TIMESTAMP = "timestamp";
    private static final String COLUMN_IMAGE_PATH = "image_path";
    private static final String COLUMN_SITE_NAME = "site_name";
    private static final String COLUMN_AGE = "age";
    private static final String COLUMN_GENDER = "gender";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_SLIDE_ID + " TEXT, " +
                COLUMN_TIMESTAMP + " TEXT, " +
                COLUMN_IMAGE_PATH + " TEXT, " +
                COLUMN_SITE_NAME + " TEXT, " +
                COLUMN_AGE + " INTEGER, " +
                COLUMN_GENDER + " TEXT" +
                ")";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void saveImageData(String slideId, String timestamp, String imagePath, String siteName, int age, String gender) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SLIDE_ID, slideId);
        values.put(COLUMN_TIMESTAMP, timestamp);
        values.put(COLUMN_IMAGE_PATH, imagePath);
        values.put(COLUMN_SITE_NAME, siteName);
        values.put(COLUMN_AGE, age);
        values.put(COLUMN_GENDER, gender);
        db.insert(TABLE_NAME, null, values);
        db.close();
    }



    List<MalSavedImage> savedImageList;
    public List<MalSavedImage> getSavedImageList(Integer number) {
        String sql = "select * from " + TABLE_NAME + " order by " + COLUMN_ID + " DESC";
        if (number != null) {
            sql += " limit " + number;
        }
        SQLiteDatabase db = this.getReadableDatabase();
        savedImageList = new ArrayList<>();
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                @SuppressLint("Range") String slide_id = cursor.getString(cursor.getColumnIndex(COLUMN_SLIDE_ID));
                @SuppressLint("Range") String timestamp = cursor.getString(cursor.getColumnIndex(COLUMN_TIMESTAMP));
                @SuppressLint("Range") String image_path = cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE_PATH));
                @SuppressLint("Range") String site_name = cursor.getString(cursor.getColumnIndex(COLUMN_SITE_NAME));
                @SuppressLint("Range") int age = cursor.getInt(cursor.getColumnIndex(COLUMN_AGE));
                @SuppressLint("Range") String gender = cursor.getString(cursor.getColumnIndex(COLUMN_GENDER));
                savedImageList.add(new MalSavedImage(id,gender,slide_id, age, timestamp, image_path, site_name));
                cursor.moveToNext();
            }
        }
        cursor.close();
        return savedImageList;
    }

    public void deleteImageData(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

}
