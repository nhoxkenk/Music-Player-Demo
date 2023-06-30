package com.example.musicplayer;

import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.musicplayer.databinding.ActivityPlaylistBinding;
import com.example.musicplayer.databinding.AddPlaylistDialogBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;

public class PlaylistActivity extends AppCompatActivity {

    private ActivityPlaylistBinding binding;
    private static PlaylistAdapter playlistAdapter;

    public static Musics.MusicPlaylist musicPlaylist = new Musics.MusicPlaylist();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.coolPink);
        binding = ActivityPlaylistBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.backBtnPL.setOnClickListener(view -> {
            finish();
        });

        //load music
        binding.playlistRV.setHasFixedSize(true);
        binding.playlistRV.setItemViewCacheSize(13);
        binding.playlistRV.setLayoutManager(new GridLayoutManager(this,2));
        playlistAdapter = new PlaylistAdapter(this, musicPlaylist.ref);
        binding.playlistRV.setAdapter(playlistAdapter);
        binding.addPlaylistBtn.setOnClickListener(view -> {
            customDialog();
        });
    }

    private void customDialog(){
        View customDialog = LayoutInflater.from(this).inflate(R.layout.add_playlist_dialog, binding.getRoot(), false);
        AddPlaylistDialogBinding binder = AddPlaylistDialogBinding.bind(customDialog);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setView(customDialog)
                .setTitle("Playlist details")
                .setPositiveButton("Add", (dialogInterface, i) -> {
                    Editable playlistName = binder.playListName.getText();
                    Editable createBy = binder.yourName.getText();
                    if(playlistName.length()!= 0 && createBy.length()!= 0){
                        addPlaylist(playlistName.toString(), createBy.toString());
                    }
                    dialogInterface.dismiss();
                }).show();
    }

    private void addPlaylist(String name, String createBy){
        boolean playListExist = false;
        for(int i = 0; i < musicPlaylist.ref.size(); i++){
            if(name.equals(musicPlaylist.ref.get(i).name)){
                playListExist = true;
                break;
            }
        }
        if(playListExist) Toast.makeText(this, "Playlist Exist!!!", Toast.LENGTH_SHORT).show();
        else{
            Musics.Playlist tempPlayList = new Musics.Playlist();
            tempPlayList.name = name;
            tempPlayList.playlist = new ArrayList<>();
            tempPlayList.createBy = createBy;
            musicPlaylist.ref.add(tempPlayList);
            playlistAdapter.refreshPlaylist();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        playlistAdapter.notifyDataSetChanged();
    }
}