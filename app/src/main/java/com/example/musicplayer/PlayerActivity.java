package com.example.musicplayer;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.media.session.PlaybackStateCompat;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.musicplayer.databinding.ActivityPlayerBinding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class PlayerActivity extends AppCompatActivity implements ServiceConnection, MediaPlayer.OnCompletionListener {

    public static ActivityPlayerBinding binding;

    public static ArrayList<Musics> musicListPA = new ArrayList<>();

    public static int songPosition = 0;

    public static MusicService musicService;
    public static boolean isPLaying = false;

    public static boolean isRepeat = false;

    public static boolean isShuffle = false;

   // @NonNull public static MediaPlayer mediaPlayer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.coolPink);
        binding = ActivityPlayerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        initializeLayout(intent);
        binding.backBtnPA.setOnClickListener(view -> {
            finish();
        });
        //Play & Pause button
        binding.playPauseBtn.setOnClickListener(view -> {
            if (isPLaying){
                Toast.makeText(this, "Pause", Toast.LENGTH_SHORT).show();
                pauseMusic();
            }else {
                Toast.makeText(this, "Playing", Toast.LENGTH_SHORT).show();
                playMusic();
            }
        });

        //Previous and Next button
        binding.previousBtnPA.setOnClickListener(view -> {
            prevNextSong(false);
        });

        binding.nextBtnPA.setOnClickListener(view -> {
            prevNextSong(true);
        });

        binding.repeatBtnPA.setOnClickListener(view -> {
            if(isShuffle){
                binding.shuffleBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.cool_pink));
                isShuffle = false;
            }
            if (!isRepeat){
                isRepeat = true;
                binding.repeatBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.purple_200));
            }else{
                isRepeat = false;
                binding.repeatBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.cool_pink));
            }
        });

        binding.shuffleBtnPA.setOnClickListener(view -> {
            if(isRepeat){
                binding.repeatBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.cool_pink));
                isRepeat = false;
            }
                if (!isShuffle){
                    isShuffle = true;
                    binding.shuffleBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.purple_200));
                }else{
                    isShuffle = false;
                    binding.shuffleBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.cool_pink));
                }
        });

        //SeekBar part
        binding.seekbarPA.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(b){
                    musicService.mediaPlayer.seekTo(i);
                    PlaybackStateCompat playBackState = new PlaybackStateCompat.Builder()
                            .setState(PlaybackStateCompat.STATE_PLAYING, (long) i, (float) 1.0)
                            .setActions(PlaybackStateCompat.ACTION_SEEK_TO)
                            .build();
                    MusicService.mediaSession.setPlaybackState(playBackState);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    private void setLayout(){
        Glide.with(this)
                .asBitmap()
                .load(musicListPA.get(songPosition).getArtUri())
                .apply(RequestOptions.placeholderOf(R.drawable.ic_launcher_splash_screen).centerCrop())
                .into(PlayerActivity.binding.songImgPA);
        PlayerActivity.binding.songNamePA.setText(musicListPA.get(songPosition).getTitle());
        PlayerActivity.binding.songArtistNamePA.setText(musicListPA.get(songPosition).getArtist());

        //vẫn giữ tính năng repeat khi kết thúc bài hát
        if(isRepeat){
            PlayerActivity.binding.repeatBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.purple_200));
        }

        if(isShuffle){
            PlayerActivity.binding.shuffleBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.purple_200));
        }
    }

    private void createMediaPlayer(){
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

            musicService.mediaPlayer.start();
            isPLaying = true;
            binding.playPauseBtn.setIconResource(R.drawable.pause_icon);
            //Thực hiện việc gọi đến Music Service cho bài hát đang được implement.
            musicService.showNotification(R.drawable.pause_icon);
            binding.seekbarPAStart.setText(Musics.formatDuration(Long.valueOf(musicService.mediaPlayer.getCurrentPosition())));
            binding.seekbarPAEnd.setText(Musics.formatDuration(Long.valueOf(musicService.mediaPlayer.getDuration())));
            binding.seekbarPA.setProgress(0);
            binding.seekbarPA.setMax(musicService.mediaPlayer.getDuration());
            //khi mediaPlayer chạy hết bài hát
            musicService.mediaPlayer.setOnCompletionListener(this);

        } catch (Exception e){
            Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show();
        }
    }

    private void initializeLayout(Intent intent){
        songPosition = intent.getIntExtra("index", 0);
        Intent intentService;
        switch (intent.getStringExtra("class")){
            case "MusicAdapter":
                //Starting Background Service
                intentService = new Intent(this,MusicService.class);
                bindService(intentService,this, BIND_AUTO_CREATE);
                startService(intentService);

                musicListPA.addAll(MainActivity.musics);
                setLayout();
                break;
            case "MainActivity":
                //Starting Background Service
                intentService = new Intent(this,MusicService.class);
                bindService(intentService,this, BIND_AUTO_CREATE);
                startService(intentService);

                musicListPA.addAll(MainActivity.musics);
                Collections.shuffle(musicListPA);
                setLayout();
                break;
            case "NowPlaying":
                setLayout();
                binding.seekbarPAStart.setText(Musics.formatDuration(Long.valueOf(musicService.mediaPlayer.getCurrentPosition())));
                binding.seekbarPAEnd.setText(Musics.formatDuration(Long.valueOf(musicService.mediaPlayer.getDuration())));
                binding.seekbarPA.setProgress(musicService.mediaPlayer.getCurrentPosition());
                binding.seekbarPA.setMax(musicService.mediaPlayer.getDuration());
                break;
            default:
                break;
        }
    }

    private void pauseMusic(){
        isPLaying = false;
        musicService.mediaPlayer.pause();
        musicService.showNotification(R.drawable.play_icon);
        binding.playPauseBtn.setIconResource(R.drawable.play_icon);
    }

    private void playMusic(){
        isPLaying = true;
        musicService.mediaPlayer.start();
        musicService.showNotification(R.drawable.pause_icon);
        binding.playPauseBtn.setIconResource(R.drawable.pause_icon);
    }

    private void prevNextSong(boolean increment){
        if(increment){
            Musics.setSongPosition(true); // fix lại vị trí của từng bài hát khi out of range của arrayList với đầu vào boolean.
        }
        else {
            Musics.setSongPosition(false);
        }
        setLayout(); // hàm thực hiện việc load ảnh và tên của từng bài hát ra layoyt player
        createMediaPlayer(); // tạo mới 1 mediaPlayer với path của bài hát đã được lấy ra
    }



    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        MusicService.MyBinder binder = (MusicService.MyBinder) iBinder;
        musicService = binder.currentService();
        createMediaPlayer();
        musicService.seekbarSetup();
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        musicService = null;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        Musics.setSongPositionForFuncButton(true);
        setLayout();
        createMediaPlayer();

        NowPlaying.binding.songNameNP.setSelected(true);
        Glide.with(this)
                .asBitmap()
                .load(musicListPA.get(songPosition).getArtUri())
                .apply(RequestOptions.placeholderOf(R.drawable.ic_launcher_splash_screen).centerCrop())
                .into(NowPlaying.binding.songImgNP);
        NowPlaying.binding.songNameNP.setText(musicListPA.get(songPosition).getTitle());
    }

}