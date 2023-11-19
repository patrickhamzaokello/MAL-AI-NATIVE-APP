package com.pkasemer.malai.Adapters;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.pkasemer.malai.ImageDisplayActivity;
import com.pkasemer.malai.Models.MalSavedImage;
import com.pkasemer.malai.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ItemViewHolder> {

    class ItemViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleView, artistname;
        private final ImageView artworkView;
        View view;

        public ItemViewHolder(View itemView) {
            super(itemView);
            titleView = itemView.findViewById(R.id.titleView);
            artistname = itemView.findViewById(R.id.artistname);
            artworkView = itemView.findViewById(R.id.artworkView);
            view = itemView;
        }
    }

    private final Context context;
    private final DrawableCrossFadeFactory factory;
    List<MalSavedImage> savedImages;
    public GalleryAdapter(Context context, List<MalSavedImage> malSavedImageList) {
        this.context = context;
        this.factory = new DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build();
        this.savedImages = malSavedImageList;
    }


    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_image, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, @SuppressLint("RecyclerView") int position) {
        final MalSavedImage malSavedImage = savedImages.get(position);
        holder.titleView.setText("Slide ID  #: "+malSavedImage.getSlide_id());
        holder.artistname.setText("Name: "+malSavedImage.getSite_name());
        Glide.with(context)
                .applyDefaultRequestOptions(new RequestOptions()
                        .placeholder(R.drawable.malimagesize_default)
                        .error(R.drawable.malimagesize_default))
                .load(malSavedImage.getImage_path())
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)     // cache both original & resized image
                .centerCrop()
                .into(holder.artworkView);

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImage(malSavedImage.getImage_path(), malSavedImage.getId(), malSavedImage.getAge(), malSavedImage.getSlide_id(), malSavedImage.getGender(), malSavedImage.getSite_name());
            }
        });




    }


    @Override
    public int getItemCount() {
        return savedImages.size();
    }



    private void showImage(String imageFile, int id, int age, String slide_id, String gender, String site_name) {
        Intent intent = new Intent(context, ImageDisplayActivity.class);
        intent.putExtra("IMAGE_PATH", imageFile);
        intent.putExtra("IMAGE_ID", id);
        intent.putExtra("AGE", age);
        intent.putExtra("SLIDE_ID", slide_id);
        intent.putExtra("GENDER", gender);
        intent.putExtra("SITE_NAME", site_name);
        context.startActivity(intent);
    }


}



