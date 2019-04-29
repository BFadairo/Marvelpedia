package com.example.android.marvelpedia.ui.Adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.marvelpedia.R;
import com.example.android.marvelpedia.model.Comic;
import com.example.android.marvelpedia.model.Thumbnail;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ComicDetailAdapter extends RecyclerView.Adapter<ComicDetailAdapter.ViewHolder> {

    private final static String LOG_TAG = ComicDetailAdapter.class.getSimpleName();
    private final ComicOnClick comicClick;
    private final String PORTRAIT_UNCANNY = "portrait_uncanny";
    private List<Comic> mComics;

    public ComicDetailAdapter(Context context, List<Comic> comics, ComicOnClick onClick) {
        Context mContext = context;
        mComics = comics;
        comicClick = onClick;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.detail_frag_list, parent, false);

        return new ComicDetailAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Comic currentComic = mComics.get(position);

        ImageView comicImage = viewHolder.mComicImage;
        //TextView comicTitle = viewHolder.mComicName;

        Thumbnail currentComicThumbnail = currentComic.getThumbnail();

        String NO_IMAGE = "no_image_available";
        if (currentComicThumbnail.getPath().endsWith(NO_IMAGE)) {
            //If there's no Image marvel Image will be used
            Picasso.get().load(R.mipmap.ic_launcher).into(comicImage);
            comicImage.setScaleType(ImageView.ScaleType.FIT_XY);
            //TODO: Get a Marvel Image for this
        } else {
            comicImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            String thumbnailExtension = currentComicThumbnail.getExtension();
            String thumbnailPath = currentComicThumbnail.getPath();
            //String combinedPath = thumbnailPath + "." + thumbnailExtension;
            String portrait_uncanny = "portrait_uncanny";
            String combinedPath = thumbnailPath + "/" + portrait_uncanny + "." + thumbnailExtension;
            currentComic.setImageUrl(combinedPath);
            Log.v(LOG_TAG, currentComic.getImageUrl());
            Log.v(LOG_TAG, combinedPath);
            try {
                Picasso.get().load(combinedPath).into(comicImage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //comicTitle.setText(currentComic.getTitle());
    }

    @Override
    public int getItemCount() {
        return mComics.size();
    }

    public void setComicData(List<Comic> comics) {
        this.mComics = comics;
        notifyDataSetChanged();
    }

    public interface ComicOnClick {
        void onClick(Comic comic);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final View mView;
        final ImageView mComicImage;
        //public final TextView mComicName;

        ViewHolder(View view) {
            super(view);
            mView = view;
            mComicImage = view.findViewById(R.id.item_image);
            mView.setOnClickListener(this);
            //mComicName = view.findViewById(R.id.comic_title);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            Comic comic = mComics.get(adapterPosition);
            comicClick.onClick(comic);
        }
    }
}
