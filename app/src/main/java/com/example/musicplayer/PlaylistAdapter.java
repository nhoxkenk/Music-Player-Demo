package com.example.musicplayer;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.musicplayer.databinding.FavoriteViewBinding;
import com.example.musicplayer.databinding.PlaylistViewBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.MyHolder> {

    private Context context;
    private ArrayList<Musics.Playlist> arrayList;

    public PlaylistAdapter(Context context, ArrayList<Musics.Playlist> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public PlaylistAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PlaylistViewBinding playlistViewBinding = PlaylistViewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyHolder(playlistViewBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistAdapter.MyHolder holder, int position) {
        holder.name.setText(arrayList.get(position).name);
        holder.name.setSelected(true);
        holder.delete.setOnClickListener(view -> {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
            builder.setTitle(arrayList.get(position).name)
                    .setMessage("Do you want to delete playlist?")
                    .setPositiveButton("Yes", (dialogInterface, i) -> {
                        PlaylistActivity.musicPlaylist.ref.remove(position);
                        refreshPlaylist();
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
        holder.root.setOnClickListener(view -> {
            Intent intent = new Intent(context, PlaylistDetails.class);
            intent.putExtra("index", position);
            ContextCompat.startActivity(context, intent, null);
        });
        if(PlaylistActivity.musicPlaylist.ref.get(position).playlist.size() > 0){
            Glide.with(context)
                    .asBitmap()
                    .load(PlaylistActivity.musicPlaylist.ref.get(position).playlist.get(0).getArtUri())
                    .apply(RequestOptions.placeholderOf(R.drawable.ic_launcher_splash_screen).centerCrop())
                    .into(holder.image);
        }
    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public void refreshPlaylist(){
        arrayList = new ArrayList<>();
        arrayList.addAll(PlaylistActivity.musicPlaylist.ref);
        notifyDataSetChanged();
    }

    public class MyHolder extends RecyclerView.ViewHolder{
        PlaylistViewBinding binding;
        CardView root;
        ShapeableImageView image;
        TextView name;
        ImageButton delete;
        public MyHolder(@NonNull PlaylistViewBinding playlistViewBinding) {
            super(playlistViewBinding.getRoot());
            binding = playlistViewBinding;
            root = binding.getRoot();
            image = binding.playListImg;
            name = binding.playListName;
            delete = binding.playListDeleteBtn;
        }
    }
}