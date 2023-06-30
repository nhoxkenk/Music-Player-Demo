package com.example.musicplayer;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.musicplayer.databinding.ActivityPlaylistDetailsBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class PlaylistDetails extends AppCompatActivity {
    public static ActivityPlaylistDetailsBinding binding;
    public static MusicAdapter adapter;
    public static int currentPlaylistPosition = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.coolPink);
        binding = ActivityPlaylistDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent = getIntent();
        currentPlaylistPosition = intent.getIntExtra("index",0);
        binding.playlistDetailsRV.setItemViewCacheSize(16);
        binding.playlistDetailsRV.setHasFixedSize(true);
        binding.playlistDetailsRV.setLayoutManager(new LinearLayoutManager(this));

        adapter = new MusicAdapter(this, PlaylistActivity.musicPlaylist.ref.get(currentPlaylistPosition).playlist, true);
        binding.playlistDetailsRV.setAdapter(adapter);
        binding.backBtnPD.setOnClickListener(view -> finish());
        binding.addBtnPD.setOnClickListener(view -> {
            startActivity(new Intent(this, SelectionActivity.class));
        });
        binding.removeAllPD.setOnClickListener(view -> {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
            builder.setTitle("Remove")
                    .setMessage("Do you want to remove all songs?")
                    .setPositiveButton("Yes", (dialogInterface, i) -> {
                        PlaylistActivity.musicPlaylist.ref.get(currentPlaylistPosition).playlist.clear();
                        adapter.refreshPlaylist();
                        dialogInterface.dismiss();
                    })
                    .setNegativeButton("No", (dialogInterface, i) -> {
                        dialogInterface.dismiss();
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.playlistNamePD.setText(PlaylistActivity.musicPlaylist.ref.get(currentPlaylistPosition).name);
        binding.moreInfoPD.setText("Total " + adapter.getItemCount()+ " Songs.");
        if(adapter.getItemCount() > 0){
            Glide.with(this)
                    .asBitmap()
                    .load(PlaylistActivity.musicPlaylist.ref.get(currentPlaylistPosition).playlist.get(0).getArtUri())
                    .apply(RequestOptions.placeholderOf(R.drawable.ic_launcher_splash_screen).centerCrop())
                    .into(binding.playlistImgPD);
        }
        adapter.notifyDataSetChanged();
    }
}