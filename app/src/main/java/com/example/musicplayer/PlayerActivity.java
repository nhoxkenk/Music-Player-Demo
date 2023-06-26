package com.example.musicplayer;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.audiofx.AudioEffect;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.media.session.PlaybackStateCompat;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.musicplayer.databinding.ActivityPlayerBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

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

    public static boolean Min15 = false;
    public static boolean Min30 = false;
    public static boolean Min60 = false;
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

        binding.equalizerBtnPA.setOnClickListener(view -> {
            try {
                Intent eqIntent = new Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL);
                eqIntent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, musicService.mediaPlayer.getAudioSessionId());
                eqIntent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, getBaseContext().getPackageName());
                eqIntent.putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC);
                startActivityForResult(eqIntent, 13);
            }catch (Exception e){
                Toast.makeText(this, "Equalizer Feature not Supported!", Toast.LENGTH_SHORT).show();
            }
        });

        binding.timerBtnPA.setOnClickListener(view -> {
            boolean timer = Min15 || Min30 || Min60;
            if(!timer){
                showBottomSheetDialog();
            }else{
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
                builder.setTitle("Stop timer")
                        .setMessage("Do you want to stop timer?")
                        .setPositiveButton("Yes", (dialogInterface, i) -> {
                            Min15 = false;
                            Min30 = false;
                            Min60 = false;
                            binding.timerBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.cool_pink));
                        })
                        .setNegativeButton("No", (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED);
            }

        });

        binding.shareBtnPA.setOnClickListener(view -> {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setType("audio/*");
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(musicListPA.get(songPosition).getPath()));
            startActivity(Intent.createChooser(shareIntent, "Sharing music file!"));
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
        Glide.with(getBaseContext())
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

        if(Min15 || Min30||Min60){
            binding.timerBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.purple_500));
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
            case "MusicAdapterSearch":
                //Starting Background Service
                intentService = new Intent(this,MusicService.class);
                bindService(intentService,this, BIND_AUTO_CREATE);
                startService(intentService);
                musicListPA = new ArrayList<>();
                musicListPA.addAll(MainActivity.musicsSearch);
                setLayout();
                break;
            case "MusicAdapter":
                //Starting Background Service
                intentService = new Intent(this,MusicService.class);
                bindService(intentService,this, BIND_AUTO_CREATE);
                startService(intentService);
                musicListPA = new ArrayList<>();
                musicListPA.addAll(MainActivity.musics);
                setLayout();
                break;
            case "MainActivity":
                //Starting Background Service
                intentService = new Intent(this,MusicService.class);
                bindService(intentService,this, BIND_AUTO_CREATE);
                startService(intentService);
                musicListPA = new ArrayList<>();
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
        Glide.with(getBaseContext())
                .asBitmap()
                .load(musicListPA.get(songPosition).getArtUri())
                .apply(RequestOptions.placeholderOf(R.drawable.ic_launcher_splash_screen).centerCrop())
                .into(NowPlaying.binding.songImgNP);
        NowPlaying.binding.songNameNP.setText(musicListPA.get(songPosition).getTitle());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 13 || requestCode == RESULT_OK){
            return;
        }
    }

    private void showBottomSheetDialog(){
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(R.layout.bottom_sheet_dialog);
        dialog.show();
        dialog.findViewById(R.id.min15).setOnClickListener(view -> {
            Toast.makeText(this, "Music will stop after 15 minutes", Toast.LENGTH_SHORT).show();
            binding.timerBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.purple_500));
            Min15 = true;
            Thread thread = new Thread(() -> {
                try {
                    Thread.sleep(5000);
                    if (Min15) {
                        PlayerActivity.musicService.stopForeground(true);
                        PlayerActivity.musicService.mediaPlayer.release();
                        PlayerActivity.musicService = null;
                        System.exit(1);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            thread.start();
            dialog.dismiss();
        });
        dialog.findViewById(R.id.min30).setOnClickListener(view -> {
            Toast.makeText(this, "Music will stop after 30 minutes", Toast.LENGTH_SHORT).show();
            binding.timerBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.purple_500));
            Min30 = true;
            Thread thread = new Thread(() -> {
                try {
                    Thread.sleep(5000);
                    if (Min30) {
                        PlayerActivity.musicService.stopForeground(true);
                        PlayerActivity.musicService.mediaPlayer.release();
                        PlayerActivity.musicService = null;
                        System.exit(1);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            thread.start();
            dialog.dismiss();
        });
        dialog.findViewById(R.id.min60).setOnClickListener(view -> {
            Toast.makeText(this, "Music will stop after 60 minutes", Toast.LENGTH_SHORT).show();
            binding.timerBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.purple_500));
            Min60= true;
            Thread thread = new Thread(() -> {
                try {
                    Thread.sleep(5000);
                    if (Min60) {
                        PlayerActivity.musicService.stopForeground(true);
                        PlayerActivity.musicService.mediaPlayer.release();
                        PlayerActivity.musicService = null;
                        System.exit(1);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            thread.start();
            dialog.dismiss();
        });
    }
}