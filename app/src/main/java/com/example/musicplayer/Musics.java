package com.example.musicplayer;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Musics {
    private String id;
    private String title;
    private String album;
    private String artist;
    private long duration = 0;
    private String path;
    private Bitmap artUri;

    public Musics(String id, String title, String album, String artist, long duration, String path, Bitmap artUri) {
        this.id = id;
        this.title = title;
        this.album = album;
        this.artist = artist;
        this.duration = duration;
        this.path = path;
        this.artUri = artUri;
    }

    public static String formatDuration(long duration){
        long minutes = TimeUnit.MINUTES.convert(duration, TimeUnit.MILLISECONDS);
        long seconds = (TimeUnit.SECONDS.convert(duration, TimeUnit.MILLISECONDS)) - minutes*TimeUnit.SECONDS.convert(1,TimeUnit.MINUTES);
        return String.format("%02d:%02d",minutes,seconds);
    }

    public static void setSongPosition(boolean increment){
        if(increment){
            if(PlayerActivity.musicListPA.size() - 1 == PlayerActivity.songPosition){
                PlayerActivity.songPosition = 0;
            }else {
                ++PlayerActivity.songPosition;
            }
        }else{
            if(0 == PlayerActivity.songPosition){
                PlayerActivity.songPosition = PlayerActivity.musicListPA.size() - 1;
            }else {
                --PlayerActivity.songPosition;
            }
        }
    }

    public static void setSongPositionForFuncButton(boolean increment){
        if(!PlayerActivity.isRepeat){
            if(increment){
                if(PlayerActivity.musicListPA.size() - 1 == PlayerActivity.songPosition){
                    PlayerActivity.songPosition = 0;
                }else {
                    ++PlayerActivity.songPosition;
                }
            }else{
                if(0 == PlayerActivity.songPosition){
                    PlayerActivity.songPosition = PlayerActivity.musicListPA.size() - 1;
                }else {
                    --PlayerActivity.songPosition;
                }
            }
        }

        if(PlayerActivity.isShuffle && PlayerActivity.isRepeat == false){
            Random random = new Random();
            int randomPosition = random.nextInt(PlayerActivity.musicListPA.size());
            if(increment){
                if(randomPosition == PlayerActivity.songPosition){
                    ++PlayerActivity.songPosition;
                }else {
                    PlayerActivity.songPosition = randomPosition;
                }
            }else{
                if(randomPosition == PlayerActivity.songPosition){
                    --PlayerActivity.songPosition;
                }else {
                    PlayerActivity.songPosition = randomPosition;
                }
            }
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Bitmap getArtUri() {
        return artUri;
    }

    public void setArtUri(Bitmap artUri) {
        this.artUri = artUri;
    }

    public static byte[] getImgArt(String path){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(path);
        return retriever.getEmbeddedPicture();
    }
}
