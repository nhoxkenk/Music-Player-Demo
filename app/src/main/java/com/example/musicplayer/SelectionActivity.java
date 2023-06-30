package com.example.musicplayer;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.musicplayer.databinding.ActivitySelectionBinding;
import java.util.ArrayList;

public class SelectionActivity extends AppCompatActivity {

    private ActivitySelectionBinding binding;
    private MusicAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySelectionBinding.inflate(getLayoutInflater());
        setTheme(R.style.coolPink);
        setContentView(binding.getRoot());
        binding.selectionRV.setItemViewCacheSize(30);
        binding.selectionRV.setHasFixedSize(true);
        binding.selectionRV.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MusicAdapter(this, true, MainActivity.musics);
        binding.selectionRV.setAdapter(adapter);
        binding.backBtnSA.setOnClickListener(view -> finish());

        // For SearchView
        binding.searchViewSA.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                MainActivity.musicsSearch = new ArrayList<>();
                if (newText != null) {
                    String userInput = newText.toLowerCase();
                    for (Musics song : MainActivity.musics) {
                        if (song.getTitle().toLowerCase().contains(userInput)) {
                            MainActivity.musicsSearch.add(song);
                        }
                    }
                    MainActivity.search = true;
                    adapter.updateMusicList(MainActivity.musicsSearch);
                }
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // For black theme checking
//        if (MainActivity.themeIndex == 4) {
//            binding.searchViewSA.setBackgroundTintList(
//                    ContextCompat.getColorStateList(this, R.color.white)
//            );
//        }
    }
}