package com.example.musicplayer;

import static com.example.musicplayer.PlayerActivity.musicListPA;
import static com.example.musicplayer.PlayerActivity.musicService;
import static com.example.musicplayer.PlayerActivity.songPosition;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.io.IOException;

public class MusicService extends Service {

    public IBinder MyBinder = new MyBinder();

    public MediaPlayer mediaPlayer = null;

    public static MediaSessionCompat mediaSession;

    private Runnable runnable;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        mediaSession = new MediaSessionCompat(getBaseContext(), "My Music");
        return MyBinder;
    }

    public class MyBinder extends Binder{
        public MusicService currentService(){
            return MusicService.this;
        }
    }

    public void showNotification(int playPauseBtn){

        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        PendingIntent contextIntent = PendingIntent.getActivity(this, 0, intent, 0);

        //Thực hiện việc tạo các intent để gửi đến cho Broadcast receiver để thực hiện các action cho notification

        Intent prevIntent = new Intent(getBaseContext(), NotificationReceiver.class).setAction(ApplicationClass.PREVIOUS);
        PendingIntent prevPendingIntent = PendingIntent.getBroadcast(getBaseContext(), 0, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent playIntent = new Intent(getBaseContext(), NotificationReceiver.class).setAction(ApplicationClass.PLAY);
        PendingIntent playPendingIntent = PendingIntent.getBroadcast(getBaseContext(), 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent nextIntent = new Intent(getBaseContext(), NotificationReceiver.class).setAction(ApplicationClass.NEXT);
        PendingIntent nextPendingIntent = PendingIntent.getBroadcast(getBaseContext(), 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent exitIntent = new Intent(getBaseContext(), NotificationReceiver.class).setAction(ApplicationClass.EXIT);
        PendingIntent exitPendingIntent = PendingIntent.getBroadcast(getBaseContext(), 0, exitIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        byte[] imgArt = Musics.getImgArt(PlayerActivity.musicListPA.get(PlayerActivity.songPosition).getPath());

        Bitmap image;
        if(imgArt != null){
            image = BitmapFactory.decodeByteArray(imgArt, 0, imgArt.length);
        }else {
            image = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_splash_screen);
        }

        Notification notification = new NotificationCompat.Builder(getBaseContext(), ApplicationClass.CHANNEL_ID)
                .setContentIntent(contextIntent)
                .setContentTitle(PlayerActivity.musicListPA.get(PlayerActivity.songPosition).getTitle())
                .setContentText(PlayerActivity.musicListPA.get(PlayerActivity.songPosition).getArtist())
                .setSmallIcon(R.drawable.music_note_icon)
                .setLargeIcon(image)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(mediaSession.getSessionToken()))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setOnlyAlertOnce(true)
                .addAction(R.drawable.back_icon, "Previous", prevPendingIntent)
                .addAction(playPauseBtn, "Play", playPendingIntent)
                .addAction(R.drawable.arrow_forward_icon, "Next", nextPendingIntent)
                .addAction(R.drawable.exit_icon, "Exit", exitPendingIntent)
                .build();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            double playbackSpeed;
            if (PlayerActivity.isPLaying){
                playbackSpeed = 1.0;
            } else {
                playbackSpeed = 0.0;
            }
            mediaSession.setMetadata(new MediaMetadataCompat.Builder()
                    .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, (long)mediaPlayer.getDuration()).build());
            PlaybackStateCompat playBackState = new PlaybackStateCompat.Builder()
                    .setState(PlaybackStateCompat.STATE_PLAYING, (long) mediaPlayer.getCurrentPosition(), (float) playbackSpeed)
                    .setActions(PlaybackStateCompat.ACTION_SEEK_TO)
                    .build();
            mediaSession.setPlaybackState(playBackState);
            mediaSession.setCallback(new MediaSessionCompat.Callback() {
                @Override
                public void onSeekTo(long pos) {
                    super.onSeekTo(pos);
                    mediaPlayer.seekTo((int) pos);
                    PlaybackStateCompat playBackStateNew = new PlaybackStateCompat.Builder()
                            .setState(PlaybackStateCompat.STATE_PLAYING, (long) mediaPlayer.getCurrentPosition(), (float) playbackSpeed)
                            .setActions(PlaybackStateCompat.ACTION_SEEK_TO)
                            .build();
                    mediaSession.setPlaybackState(playBackStateNew);
                }
            });
        }

        startForeground(13, notification);

    }

    public void createMediaPlayerInNotification(Context context){
        try {
            if(musicService.mediaPlayer == null){
                musicService.mediaPlayer = new MediaPlayer();
            }
            musicService.mediaPlayer.reset();

            try {
                musicService.mediaPlayer.setDataSource(musicListPA.get(songPosition).getPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            try {
                musicService.mediaPlayer.prepare();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            PlayerActivity.binding.playPauseBtn.setIconResource(R.drawable.pause_icon);
            PlayerActivity.musicService.showNotification(R.drawable.pause_icon);
            PlayerActivity.binding.seekbarPAStart.setText(Musics.formatDuration(Long.valueOf(musicService.mediaPlayer.getCurrentPosition())));
            PlayerActivity.binding.seekbarPAEnd.setText(Musics.formatDuration(Long.valueOf(musicService.mediaPlayer.getDuration())));
            PlayerActivity.binding.seekbarPA.setProgress(0);
            PlayerActivity.binding.seekbarPA.setMax(musicService.mediaPlayer.getDuration());
            PlayerActivity.nowPlayingId = PlayerActivity.musicListPA.get(PlayerActivity.songPosition).getId();
        }catch (Exception e){
            Toast.makeText(context, "ERROR", Toast.LENGTH_SHORT).show();
        }
    }

    public void seekbarSetup(){
        runnable = () -> {
            try{
                PlayerActivity.binding.seekbarPAStart.setText(Musics.formatDuration(Long.valueOf(musicService.mediaPlayer.getCurrentPosition())));
                PlayerActivity.binding.seekbarPA.setProgress(mediaPlayer.getCurrentPosition());
                new Handler(Looper.getMainLooper()).postDelayed(runnable, 200);
            } catch (Exception e){
                Toast.makeText(this, "ERROR: " + e, Toast.LENGTH_SHORT).show();
            }

        };
        new Handler(Looper.getMainLooper()).postDelayed(runnable, 0);
    }

}
