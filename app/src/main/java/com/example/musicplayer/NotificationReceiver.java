package com.example.musicplayer;

import static com.example.musicplayer.PlayerActivity.binding;
import static com.example.musicplayer.PlayerActivity.musicListPA;
import static com.example.musicplayer.PlayerActivity.musicService;
import static com.example.musicplayer.PlayerActivity.songPosition;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()){
            case "previous":
                prevNextSong(false, context);
                break;

            case "next":
                prevNextSong(true, context);
                break;

            case "play":
                if(PlayerActivity.isPLaying){
                    pauseMusic();
                }else{
                    playMusic();
                }
                break;

            case "exit":
                if(PlayerActivity.musicService != null){
                    PlayerActivity.musicService.stopForeground(true);
                    PlayerActivity.musicService.mediaPlayer.release();
                    PlayerActivity.musicService = null;
                }
                System.exit(1);
                break;

            default:
                break;
        }
    }

    private void playMusic(){
        PlayerActivity.isPLaying = true;
        musicService.mediaPlayer.start();
        musicService.showNotification(R.drawable.pause_icon);
        binding.playPauseBtn.setIconResource(R.drawable.pause_icon);
        NowPlaying.binding.playPauseBtnNP.setImageResource(R.drawable.pause_icon);
    }

    private void pauseMusic(){
        PlayerActivity.isPLaying = false;
        musicService.mediaPlayer.pause();
        musicService.showNotification(R.drawable.play_icon);
        binding.playPauseBtn.setIconResource(R.drawable.play_icon);
        NowPlaying.binding.playPauseBtnNP.setImageResource(R.drawable.play_icon);
    }

    private void prevNextSong(boolean increment, Context context){
        Musics.setSongPosition(increment);
        PlayerActivity.musicService.createMediaPlayerInNotification(context);
        //for Player Activity
        Glide.with(context)
                .asBitmap()
                .load(musicListPA.get(songPosition).getArtUri())
                .apply(RequestOptions.placeholderOf(R.drawable.ic_launcher_splash_screen).centerCrop())
                .into(binding.songImgPA);
        PlayerActivity.binding.songNamePA.setText(musicListPA.get(songPosition).getTitle());
        //for Now Playing fragment
        Glide.with(context)
                .asBitmap()
                .load(musicListPA.get(songPosition).getArtUri())
                .apply(RequestOptions.placeholderOf(R.drawable.ic_launcher_splash_screen).centerCrop())
                .into(NowPlaying.binding.songImgNP);
        NowPlaying.binding.songNameNP.setText(musicListPA.get(songPosition).getTitle());
        playMusic();
        PlayerActivity.fIndex = Musics.favoriteChecked(musicListPA.get(songPosition).getId());
        if(PlayerActivity.isFavorite) binding.favoriteBtnPA.setImageResource(R.drawable.favorite_icon);
        else binding.favoriteBtnPA.setImageResource(R.drawable.favorite_empty_icon);
    }



}
