package com.example.musicplayer;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.ArrayList;

//Room Database






































































































































































































































































public class FavoriteSongDB extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "MusicPLayer_database.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "favorite";
    private static final String COLUMN_ID = "id";

    private static final String COLUMN_SONG_ID = "song_id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_ALBUM = "album";
    private static final String COLUMN_ARTIST = "artist";
    private static final String COLUMN_DURATION = "duration";
    private static final String COLUMN_PATH = "path";
    private static final String COLUMN_ART = "art";

    public FavoriteSongDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createTableQuery = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_SONG_ID + " TEXT, "+
                COLUMN_TITLE + " TEXT, "+
                COLUMN_ALBUM + " TEXT, "+
                COLUMN_ARTIST + " TEXT, "+
                COLUMN_DURATION + " INTEGER, "+
                COLUMN_PATH + " TEXT, "+
                COLUMN_ART + " BLOB)";
        sqLiteDatabase.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
    public void  deleteMusic (String columnValue) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_SONG_ID + "=?", new String[]{columnValue});
    }
    public void addMusic(Musics music) {
        SQLiteDatabase db = getWritableDatabase();
            byte[] imageData = Musics.getImgArt(music.getPath());

            ContentValues values = new ContentValues();
            values.put(COLUMN_SONG_ID, music.getId());
            values.put(COLUMN_TITLE, music.getTitle());
            values.put(COLUMN_ALBUM, music.getAlbum());
            values.put(COLUMN_ARTIST, music.getArtist());
            values.put(COLUMN_DURATION, music.getDuration());
            values.put(COLUMN_PATH, music.getPath());
            values.put(COLUMN_ART, imageData);
            db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public void addMusics(ArrayList<Musics> musics) {
        SQLiteDatabase db = getWritableDatabase();
        for (Musics music : musics) {
            byte[] imageData = Musics.getImgArt(music.getPath());

            ContentValues values = new ContentValues();
            values.put(COLUMN_SONG_ID, music.getId());
            values.put(COLUMN_TITLE, music.getTitle());
            values.put(COLUMN_ALBUM, music.getAlbum());
            values.put(COLUMN_ARTIST, music.getArtist());
            values.put(COLUMN_DURATION, music.getDuration());
            values.put(COLUMN_PATH, music.getPath());
            values.put(COLUMN_ART, imageData);
            db.insert(TABLE_NAME, null, values);
        }
        db.close();
    }

    public ArrayList<Musics> getAllMusics() {
        ArrayList<Musics> musics = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        cursor.moveToFirst();
        while(cursor.isAfterLast() == false){

                @SuppressLint("Range") String id = cursor.getString(cursor.getColumnIndex(COLUMN_SONG_ID));
                @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE));
                @SuppressLint("Range") String album = cursor.getString(cursor.getColumnIndex(COLUMN_ALBUM));
                @SuppressLint("Range") String artist = cursor.getString(cursor.getColumnIndex(COLUMN_ARTIST));
                @SuppressLint("Range") Long duration = cursor.getLong(cursor.getColumnIndex(COLUMN_DURATION));
                @SuppressLint("Range") String path = cursor.getString(cursor.getColumnIndex(COLUMN_PATH));
                @SuppressLint("Range") byte[] imageData = cursor.getBlob(cursor.getColumnIndex(COLUMN_ART));
                Bitmap image;
                if(imageData != null){
                    image = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                }else {
                    image = BitmapFactory.decodeResource(Resources.getSystem(), R.drawable.ic_launcher_splash_screen);
                }
                Musics music = new Musics(id, title, album, artist, duration, path, image);

                musics.add(music);
                cursor.moveToNext();
        }

        cursor.close();
        db.close();

        return musics;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, TABLE_NAME);
        return numRows;
    }
}
