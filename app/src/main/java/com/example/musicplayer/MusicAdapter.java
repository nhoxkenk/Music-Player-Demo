package com.example.musicplayer;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.musicplayer.databinding.MusicViewBinding;

import java.util.ArrayList;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MyHolder> {

    private Context context;
    private ArrayList<Musics> arrayList = new ArrayList<>();

    public MusicAdapter(Context context, ArrayList<Musics> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
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

        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PlayerActivity.class);
                intent.putExtra("index",holder.getAdapterPosition());
                intent.putExtra("class", "MusicAdapter");
                ContextCompat.startActivity(context, intent, null);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder{
        MusicViewBinding binding;
        RelativeLayout root;
        TextView title;
        TextView artist;
        ImageView image;
        TextView duration;
        public MyHolder(@NonNull MusicViewBinding musicViewBinding) {
            super(musicViewBinding.getRoot());
            this.binding = musicViewBinding;
            title = binding.songName;
            artist = binding.songArtist;
            image = binding.imageMV;
            duration = binding.songDuration;
            root = binding.getRoot();
        }
    }
}
