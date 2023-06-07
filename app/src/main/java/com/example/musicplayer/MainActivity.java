package com.example.musicplayer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.musicplayer.databinding.ActivityMainBinding;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    //Create Binding Object
    private ActivityMainBinding binding;
    private ActionBarDrawerToggle toggle;

    private MusicAdapter musicAdapter;
    private static final int STORAGE_PERMISSION_CODE = 1;

    public static ArrayList<Musics> musics = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeLayout();

        if(requestRuntimePermission()){
            initializeMusics();
        }

        binding.shuffleBtn.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, PlayerActivity.class);
            intent.putExtra("index",0);
            intent.putExtra("class", "MainActivity");
            startActivity(intent);
        });

        binding.favoriteBtn.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, FavoriteActivity.class);
            startActivity(intent);
        });

        binding.playListBtn.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, PlaylistActivity.class);
            startActivity(intent);
        });

        binding.navView.setNavigationItemSelectedListener(item ->{
            switch (item.getItemId()){
                case R.id.navFeedback:
                    Toast.makeText(this, "Feedback", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.navSettings:
                    Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.navAbout:
                    Toast.makeText(this, "About", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.navExit:
                    System.exit(0);
                    break;
                default:
                    break;
            }
            return false;
        });

    }

    //Requesting permission to access to file


    private boolean requestRuntimePermission(){
        if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "already granted permission!", Toast.LENGTH_SHORT).show();
            return true;
        }else{
            ActivityCompat.requestPermissions(this,new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE }, MainActivity.STORAGE_PERMISSION_CODE);
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == STORAGE_PERMISSION_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                initializeMusics();
            }else{
                ActivityCompat.requestPermissions(this,new String[]{
                        android.Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(toggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initializeLayout(){

        setTheme(R.style.coolPinkNav);

        //Start Binding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Navigation drawer
        toggle = new ActionBarDrawerToggle(this,binding.getRoot(),R.string.open, R.string.close);
        binding.getRoot().addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void initializeMusics(){
        musics = getAllAudio();

        //load music
        binding.musicRV.setHasFixedSize(true);
        binding.musicRV.setItemViewCacheSize(13);
        binding.musicRV.setLayoutManager(new LinearLayoutManager(this));
        musicAdapter = new MusicAdapter(MainActivity.this, musics);
        binding.musicRV.setAdapter(musicAdapter);
        binding.totalSongs.setText("Total Songs: " + musicAdapter.getItemCount());
    }

    private ArrayList<Musics>  getAllAudio(){
        ArrayList<Musics> musics = new ArrayList<>();
        String selection =  MediaStore.Audio.Media.IS_MUSIC + " != 0";
        String[] projection = {MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATE_ADDED,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ALBUM_ID};
        Cursor cursor = this.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, selection, null, MediaStore.Audio.Media.DATE_ADDED + " DESC", null);
        if(cursor != null){
            if(cursor.moveToFirst()){
                do {
                    @SuppressLint("Range") String titleC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                    @SuppressLint("Range") String idC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                    @SuppressLint("Range") String albumC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                    @SuppressLint("Range") String artistC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                    @SuppressLint("Range") String pathC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                    @SuppressLint("Range") long durationC = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                    @SuppressLint("Range") String albumIdC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
                    byte[] imgArt = Musics.getImgArt(pathC);
                    Bitmap image;
                    if(imgArt != null){
                        image = BitmapFactory.decodeByteArray(imgArt, 0, imgArt.length);
                    }else {
                        image = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_splash_screen);
                    }
//                    Uri uri = Uri.parse("content://media/external/audio/albumart");
//                    String artUri = Uri.withAppendedPath(uri, albumIdC).toString();
                    Musics music = new Musics(idC, titleC, albumC, artistC, durationC, pathC, image);
                    File file = new File(music.getPath());
                    if(file.exists()){
                        musics.add(music);
                    }
                }while (cursor.moveToNext());
                cursor.close();
            }
        }
        return musics;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(!PlayerActivity.isPLaying && PlayerActivity.musicService != null){
            PlayerActivity.musicService.stopForeground(true);
            PlayerActivity.musicService.mediaPlayer.release();
            PlayerActivity.musicService = null;
            System.exit(1);
        }
    }
}