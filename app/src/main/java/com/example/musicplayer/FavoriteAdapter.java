package com.example.musicplayer;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.musicplayer.databinding.FavoriteViewBinding;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.MyHolder> {

    private Context context;
    private ArrayList<Musics> arrayList;

    public FavoriteAdapter(Context context, ArrayList<Musics> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public FavoriteAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        FavoriteViewBinding favoriteViewBinding = FavoriteViewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyHolder(favoriteViewBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteAdapter.MyHolder holder, int position) {
        holder.name.setText(arrayList.get(position).getTitle());
        Glide.with(context)
                .asBitmap()
                .load(arrayList.get(position).getArtUri())
                .apply(RequestOptions.placeholderOf(R.drawable.ic_launcher_splash_screen).centerCrop())
                .into(holder.image);
        holder.root.setOnClickListener(view -> {
            sendIntent("FavoriteAdapter", position);
        });
    }

    private void sendIntent(String ref, int pos){
        Intent intent = new Intent(context, PlayerActivity.class);
        intent.putExtra("index",pos);
        intent.putExtra("class", ref);
        ContextCompat.startActivity(context, intent, null);
    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder{
        FavoriteViewBinding binding;
        LinearLayout root;
        ShapeableImageView image;
        TextView name;
        public MyHolder(@NonNull FavoriteViewBinding musicViewBinding) {
            super(musicViewBinding.getRoot());
            this.binding = musicViewBinding;
            root = binding.getRoot();
            image = binding.songImgFV;
            name = binding.songNameFV;
        }
    }
}