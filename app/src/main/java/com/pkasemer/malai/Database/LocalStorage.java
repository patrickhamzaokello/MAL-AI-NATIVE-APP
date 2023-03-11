package com.pkasemer.malai.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LocalStorage extends SQLiteOpenHelper {

    private static final String DB_NAME = "malAI";
    private static final int DB_VERSION = 3;
    private static final String TABLE_TRACK = "track";
    private static final String TABLE_ArtistFollowing = "ArtistFollowing";
    private static final String TABLE_LIKED_TRACK = "liked_tracks";
    public static final String COLUMN_id = "_id";
    public static final String COLUMN_trackId = "trackId";
    public static final String COLUMN_trackTitle = "trackTitle";
    public static final String COLUMN_trackArtist = "trackArtist";
    public static final String COLUMN_trackArtistID = "trackArtistID";
    public static final String COLUMN_trackAlbum = "trackAlbum";
    public static final String COLUMN_trackArtworkPath = "trackArtworkPath";
    public static final String COLUMN_trackGenre = "trackGenre";
    public static final String COLUMN_trackGenreID = "trackGenreID";
    public static final String COLUMN_trackDuration = "trackDuration";
    public static final String COLUMN_trackPath = "trackPath";
    public static final String COLUMN_trackTotalPlays = "trackTotalPlays";
    public static final String COLUMN_trackWeeklyPlays = "trackWeeklyPlays";
    public static final String COLUMN_track_status = "track_status";
    public static final String COLUMN_track_lastPlayed = "track_lastPlayed";
    public static final String COLUMN_track_user_plays = "track_user_plays";
    public static final String COLUMN_artistFollow_status = "status";
    public static final String COLUMN_UserID = "userID";
    public static final String COLUMN_ArtistID = "artistID";
    public static final String COLUMN_FollowDate = "followDate";
    public static final int NOT_SYNCED_WITH_SERVER = 0;
    public static final int SYNCED_WITH_SERVER = 1;



    public LocalStorage(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        //create CART table
        String sql = "CREATE TABLE " + TABLE_LIKED_TRACK + " (" +
                COLUMN_id + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_trackId + " INTEGER," +
                COLUMN_track_status + " INTEGER DEFAULT " + NOT_SYNCED_WITH_SERVER + " )";

        db.execSQL(sql);

        //create Track table
        String recently_played_sql = "CREATE TABLE " + TABLE_TRACK + " (" +
                COLUMN_id + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_trackId + " INTEGER," +
                COLUMN_trackTitle + " TEXT," +
                COLUMN_trackArtist + " TEXT," +
                COLUMN_trackArtistID + " INTEGER," +
                COLUMN_trackAlbum + " TEXT," +
                COLUMN_trackArtworkPath + " TEXT," +
                COLUMN_trackGenre + " TEXT," +
                COLUMN_trackGenreID + " INTEGER," +
                COLUMN_trackDuration + " TEXT," +
                COLUMN_trackPath + " TEXT," +
                COLUMN_trackTotalPlays + " INTEGER," +
                COLUMN_trackWeeklyPlays + " TEXT," +
                COLUMN_track_lastPlayed + " DATETIME DEFAULT (datetime('now','localtime'))," +
                COLUMN_track_user_plays + " INTEGER DEFAULT 0 ," +
                COLUMN_track_status + " INTEGER DEFAULT " + NOT_SYNCED_WITH_SERVER + " )";

        db.execSQL(recently_played_sql);

        //artistFollowing
        String SQL_CREATE_ARTIST_FOLLOW_TABLE = "CREATE TABLE " + TABLE_ArtistFollowing + " ("
                + COLUMN_id + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_UserID + " TEXT NOT NULL, "
                + COLUMN_ArtistID + " TEXT NOT NULL, "
                + COLUMN_FollowDate + " DATETIME DEFAULT (datetime('now','localtime')),"
                + COLUMN_artistFollow_status + " INTEGER DEFAULT " + NOT_SYNCED_WITH_SERVER + ");";

        db.execSQL(SQL_CREATE_ARTIST_FOLLOW_TABLE);
    }

    //upgrading the database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRACK);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LIKED_TRACK);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ArtistFollowing);
        onCreate(db);
    }















    public boolean checkTrackInDb(String id_str) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TRACK,
                new String[]{COLUMN_trackId},
                COLUMN_trackId + " = ?",
                new String[]{id_str},
                null, null, null, null);
        if (cursor.moveToFirst()) {
            //recordexist
            cursor.close();
            return false;
        } else {
            //record not existing
            cursor.close();

            return true;
        }
    }



    public void addLikedTrack(String trackID, int track_status) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_trackId, trackID);
        values.put(COLUMN_track_status, track_status);
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_LIKED_TRACK, null, values);
    }

    public void removeLikedTrack(String trackID, int track_status) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_LIKED_TRACK, COLUMN_trackId + " = ?", new String[]{trackID});
    }

    public boolean checkLikedTrackID(String id_str) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_LIKED_TRACK,
                new String[]{COLUMN_trackId},
                COLUMN_trackId + " = ?",
                new String[]{id_str},
                null, null, null, null);
        if (cursor.moveToFirst()) {
            //recordexist
            cursor.close();
            return false;
        } else {
            //record not existing
            cursor.close();

            return true;
        }
    }


    //update Database
    public void updateDataRecentlyPlayed(String trackID, int syncStatus) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_trackId, trackID);
        values.put(COLUMN_track_status, syncStatus);
        SQLiteDatabase db = this.getWritableDatabase();
        db.update(TABLE_TRACK, values, COLUMN_trackId + " = ?", new String[]{trackID});
    }

    //update LikedSong Database
    public void updateDataLikedSongPlayed(String trackID, int syncStatus) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_trackId, trackID);
        values.put(COLUMN_track_status, syncStatus);
        SQLiteDatabase db = this.getWritableDatabase();
        db.update(TABLE_LIKED_TRACK, values, COLUMN_trackId + " = ?", new String[]{trackID});
    }

    public void clearDatabases() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_LIKED_TRACK, null, null);
        db.delete(TABLE_TRACK, null, null);
        db.close();
    }


    //follow_not_followArtist
    public void followUnfollowArtist(String userId, String artistId, int status, boolean follow) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_UserID, userId);
        values.put(COLUMN_ArtistID, artistId);
        values.put(COLUMN_artistFollow_status, status);

        if (follow) {
            // Insert the new row, returning the primary key value of the new row
            long newRowId = db.insert(TABLE_ArtistFollowing, null, values);
        } else {
            // Delete the artist from the list
            String selection = COLUMN_UserID + " = ? AND " + COLUMN_ArtistID + " = ?";
            String[] selectionArgs = {userId, artistId};
            db.delete(TABLE_ArtistFollowing, selection, selectionArgs);
        }
    }

    public boolean isFollowingArtist(String userId, String artistId) {
        SQLiteDatabase db = this.getReadableDatabase();

        // projection to get the Artist ID from the DB
        String[] projection = {
                COLUMN_id
        };

        String selection = COLUMN_UserID + " = ? AND " +
                COLUMN_ArtistID + " = ?";
        String[] selectionArgs = {userId, artistId};

        Cursor cursor = db.query(
                TABLE_ArtistFollowing,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );

        if(cursor.getCount() > 0){
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }


}
