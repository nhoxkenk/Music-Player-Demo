package com.example.musicplayer;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.musicplayer.databinding.MusicViewBinding;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.ArrayList;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MyHolder> {

    private Context context;
    private ArrayList<Musics> arrayList;
    boolean playListDetails = false;
    boolean selectionActivity = false;

    public MusicAdapter(Context context, ArrayList<Musics> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    public MusicAdapter(Context context, ArrayList<Musics> arrayList, boolean playlistDetails) {
        this.context = context;
        this.arrayList = arrayList;
        this.playListDetails = playlistDetails;
    }

    public MusicAdapter(Context context,  boolean selectionActivity, ArrayList<Musics> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
        this.selectionActivity = selectionActivity;
    }

    @NonNull
    @Override
    public MusicAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MusicViewBinding musicViewBinding = MusicViewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyHolder(musicViewBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicAdapter.MyHolder holder, int position) {
        holder.title.setText(arrayList.get(position).getTitle());
        if (arrayList.get(position).getArtist().equals("<unknown>")){
            arrayList.get(position).setArtist("Nghệ sĩ không xác định");
        }

        holder.artist.setText(arrayList.get(position).getArtist());
        holder.duration.setText(arrayList.get(position).formatDuration(arrayList.get(position).getDuration()));
        Glide.with(context)
                .asBitmap()
                .load(arrayList.get(position).getArtUri())
                .apply(RequestOptions.placeholderOf(R.drawable.ic_launcher_splash_screen).centerCrop())
                .into(holder.image);

        if(playListDetails){
            holder.root.setOnClickListener(view -> {
                sendIntent("PlaylistDetailsAdapter",position);
            });
        } else if (selectionActivity) {
            holder.root.setOnClickListener(view -> {
                if(addSong(arrayList.get(position))){
                    holder.root.setBackgroundColor(ContextCompat.getColor(context, R.color.cool_pink));
                }else{
                    holder.root.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
                }
            });
        } else{
            holder.root.setOnClickListener(view -> {
                if(MainActivity.search){
                    sendIntent("MusicAdapterSearch", position);
                }else if(arrayList.get(position).getId() == PlayerActivity.nowPlayingId){
                    sendIntent("NowPlaying", PlayerActivity.songPosition);
                }
                else{
                    sendIntent("MusicAdapter", position);
                }
            });
        }



        holder.moreIcon.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(context, view);
            popupMenu.getMenuInflater().inflate(R.menu.popup, popupMenu.getMenu());
            popupMenu.show();
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                switch (menuItem.getItemId()){
                    case R.id.delete:
                        Toast.makeText(context, "Deleted song", Toast.LENGTH_SHORT).show();
                        deleteSong(position, view);
                        break;
                }
                return true;
            });
        });
    }

    private void sendIntent(String ref, int pos){
        Intent intent = new Intent(context, PlayerActivity.class);
        intent.putExtra("index",pos);
        intent.putExtra("class", ref);
        ContextCompat.startActivity(context, intent, null);
    }

    private void deleteSong(int position, View view){
        Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                Long.parseLong(arrayList.get(position).getId()));

        File file = new File(arrayList.get(position).getPath());
        boolean deleted = file.delete();
        //if(deleted){
        context.getContentResolver().delete(uri, null, null);
        arrayList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, arrayList.size());
        MainActivity.binding.totalSongs.setText("Total Songs: " + getItemCount());
        Snackbar.make(view, "Song deleted: ", Snackbar.LENGTH_SHORT)
                .show();
//        }
//        else{
//            //maybe in sd card
//            Snackbar.make(view, "Can't be deleted: ", Snackbar.LENGTH_SHORT)
//                    .show();
//        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public void updateMusicList(ArrayList<Musics> searchList){
        arrayList = new ArrayList<>();
        arrayList.addAll(searchList);
        MainActivity.binding.totalSongs.setText("Total Songs: " + getItemCount());
        notifyDataSetChanged();
    }

    public boolean addSong(Musics song){
        for(int i = 0; i < PlaylistActivity.musicPlaylist.ref.get(PlaylistDetails.currentPlaylistPosition).playlist.size(); i++){
            if(song.getId() == PlaylistActivity.musicPlaylist.ref.get(PlaylistDetails.currentPlaylistPosition).playlist.get(i).getId()){
                PlaylistActivity.musicPlaylist.ref.get(PlaylistDetails.currentPlaylistPosition).playlist.remove(i);
                return false;
            }
        }
        PlaylistActivity.musicPlaylist.ref.get(PlaylistDetails.currentPlaylistPosition).playlist.add(song);
        return true;
    }

    public void refreshPlaylist(){
        arrayList = new ArrayList<>();
        arrayList = PlaylistActivity.musicPlaylist.ref.get(PlaylistDetails.currentPlaylistPosition).playlist;
        notifyDataSetChanged();
    }

    public class MyHolder extends RecyclerView.ViewHolder{
        MusicViewBinding binding;
        RelativeLayout root;
        TextView title;
        TextView artist;
        ImageView image, moreIcon;
        TextView duration;
        public MyHolder(@NonNull MusicViewBinding musicViewBinding) {
            super(musicViewBinding.getRoot());
            this.binding = musicViewBinding;
            title = binding.songName;
            artist = binding.songArtist;
            image = binding.imageMV;
            duration = binding.songDuration;
            moreIcon = binding.moreIcon;
            root = binding.getRoot();
        }
    }
}
