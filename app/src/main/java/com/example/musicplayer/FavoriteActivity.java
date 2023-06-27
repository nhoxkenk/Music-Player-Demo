package com.example.musicplayer;

import android.os.Bundle;

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
    }
}