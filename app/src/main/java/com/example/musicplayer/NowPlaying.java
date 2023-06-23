package com.example.musicplayer;

import static com.example.musicplayer.PlayerActivity.isPLaying;
import static com.example.musicplayer.PlayerActivity.musicListPA;
import static com.example.musicplayer.PlayerActivity.musicService;
import static com.example.musicplayer.PlayerActivity.songPosition;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.musicplayer.databinding.FragmentNowPlayingBinding;

public class NowPlaying extends Fragment {

    public static FragmentNowPlayingBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_now_playing, container, false);
        binding = FragmentNowPlayingBinding.bind(view);
        binding.getRoot().setVisibility(View.INVISIBLE);

        @SuppressLint("UseCompatLoadingForColorStateLists") ColorStateList colorStateList = getResources().getColorStateList(R.color.white);
        binding.playPauseBtnNP.setImageTintList(colorStateList);
        binding.nextBtnNP.setImageTintList(colorStateList);

        binding.playPauseBtnNP.setOnClickListener(view1 -> {
            if(isPLaying){
                pauseMusic();
            }else playMusic();
        });

        binding.nextBtnNP.setOnClickListener(view1 -> {
            Musics.setSongPosition(true);
            PlayerActivity.musicService.createMediaPlayerInNotification(getContext());
            //for Player Activity
            PlayerActivity.binding.songNamePA.setText(musicListPA.get(songPosition).getTitle());
            //for Now Playing fragment
            Glide.with(this)
                    .asBitmap()
                    .load(musicListPA.get(songPosition).getArtUri())
                    .apply(RequestOptions.placeholderOf(R.drawable.ic_launcher_splash_screen).centerCrop())
                    .into(binding.songImgNP);
            binding.songNameNP.setText(musicListPA.get(songPosition).getTitle());
            musicService.showNotification(R.drawable.pause_icon);
            playMusic();
        });

        binding.getRoot().setOnClickListener(view1 -> {
            Intent intent = new Intent(getContext(), PlayerActivity.class);
            intent.putExtra("index", songPosition);
            intent.putExtra("class", "NowPlaying");
            ContextCompat.startActivity(getContext(), intent, null);
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(PlayerActivity.musicService != null){
            binding.getRoot().setVisibility(View.VISIBLE);
            binding.songNameNP.setSelected(true);
            ViewGroup.LayoutParams layoutParams = MainActivity.binding.musicRV.getLayoutParams();
            int dpValue = 530; // Giá trị dp mong muốn
            float density = getResources().getDisplayMetrics().density;
            int pixelValue = (int) (dpValue * density + 0.5f);
            layoutParams.height = pixelValue;
            MainActivity.binding.musicRV.setLayoutParams(layoutParams);
            Glide.with(this)
                    .asBitmap()
                    .load(musicListPA.get(songPosition).getArtUri())
                    .apply(RequestOptions.placeholderOf(R.drawable.ic_launcher_splash_screen).centerCrop())
                    .into(binding.songImgNP);
            binding.songNameNP.setText(musicListPA.get(songPosition).getTitle());
            if(PlayerActivity.isPLaying){
                binding.playPauseBtnNP.setImageResource(R.drawable.pause_icon);
            }else{
                binding.playPauseBtnNP.setImageResource(R.drawable.play_icon);
            }
        }
    }

    private void playMusic(){
        isPLaying = true;
        musicService.mediaPlayer.start();
        binding.playPauseBtnNP.setImageResource(R.drawable.pause_icon);
        musicService.showNotification(R.drawable.pause_icon);
        PlayerActivity.binding.nextBtnPA.setIconResource(R.drawable.pause_icon);
    }

    private void pauseMusic(){
        isPLaying = false;
        musicService.mediaPlayer.pause();
        binding.playPauseBtnNP.setImageResource(R.drawable.play_icon);
        musicService.showNotification(R.drawable.play_icon);
        PlayerActivity.binding.nextBtnPA.setIconResource(R.drawable.play_icon);
    }
}