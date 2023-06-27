package com.example.musicplayer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.musicplayer.databinding.ActivityFavoriteBinding;

import java.util.ArrayList;

public class FavoriteActivity extends AppCompatActivity {

    private ActivityFavoriteBinding binding;
    private static FavoriteAdapter favoriteAdapter;

    public static ArrayList<Musics> favoriteSongs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.coolPink);
        binding = ActivityFavoriteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.backBtnFA.setOnClickListener(view -> {
            finish();
        });
        binding.favoriteRV.setHasFixedSize(true);
        binding.favoriteRV.setItemViewCacheSize(13);
        binding.favoriteRV.setLayoutManager(new GridLayoutManager(this, 4));
        favoriteAdapter = new FavoriteAdapter(FavoriteActivity.this, favoriteSongs);
        binding.favoriteRV.setAdapter(favoriteAdapter);
        if(favoriteSongs.size() < 1){
            binding.shuffleBtnFA.setVisibility(View.INVISIBLE);
        }
        binding.shuffleBtnFA.setOnClickListener(view -> {
            Intent intent = new Intent(this, PlayerActivity.class);
            intent.putExtra("index",0);
            intent.putExtra("class", "FavoriteShuffle");
            startActivity(intent);
        });
    }
}