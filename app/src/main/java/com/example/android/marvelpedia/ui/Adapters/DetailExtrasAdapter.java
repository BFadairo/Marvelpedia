package com.example.android.marvelpedia.ui.Adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.marvelpedia.R;
import com.example.android.marvelpedia.model.Character;
import com.example.android.marvelpedia.model.Comic;
import com.example.android.marvelpedia.model.Event;
import com.example.android.marvelpedia.model.Series;
import com.example.android.marvelpedia.model.Thumbnail;
import com.squareup.picasso.Picasso;

import java.util.List;

public class DetailExtrasAdapter<T> extends RecyclerView.Adapter<DetailExtrasAdapter<T>.ViewHolder> {

    private final static String LOG_TAG = DetailExtrasAdapter.class.getSimpleName();
    private List<T> mItems;
    private final ItemOnClick<T> itemOnClick;
    private Thumbnail currentItemThumbnail;

    public DetailExtrasAdapter(Context context, List<T> items, ItemOnClick<T> onClick) {
        //noinspection UnnecessaryLocalVariable
        Context mContext = context;
        mItems = items;
        itemOnClick = onClick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.detail_frag_list, viewGroup, false);

        return new DetailExtrasAdapter<T>.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        T currentItem = mItems.get(position);

        if (currentItem instanceof Character) {
            currentItemThumbnail = ((Character) currentItem).getThumbnail();
        } else if (currentItem instanceof Comic) {
            currentItemThumbnail = ((Comic) currentItem).getThumbnail();
        } else if (currentItem instanceof Event) {
            currentItemThumbnail = ((Event) currentItem).getThumbnails();
        } else if (currentItem instanceof Series) {
            currentItemThumbnail = ((Series) currentItem).getThumbnail();
        }

        ImageView itemImage = viewHolder.mItemImage;
        //TextView comicTitle = viewHolder.mComicName;


        String NO_IMAGE = "no_image_available";
        if (currentItemThumbnail.getPath().endsWith(NO_IMAGE)) {
            //If there's no Image marvel Image will be used
            Picasso.get().load(R.mipmap.ic_launcher).into(itemImage);
            itemImage.setScaleType(ImageView.ScaleType.FIT_XY);
            //TODO: Get a Marvel Image for this
        } else {
            itemImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            String thumbnailExtension = currentItemThumbnail.getExtension();
            String thumbnailPath = currentItemThumbnail.getPath();
            //String combinedPath = thumbnailPath + "." + thumbnailExtension;
            String portrait_uncanny = "portrait_uncanny";
            String combinedPath = thumbnailPath + "/" + portrait_uncanny + "." + thumbnailExtension;
            if (currentItem instanceof Character) {
                ViewCompat.setTransitionName(itemImage, ((Character) currentItem).getName());
                ((Character) currentItem).setImageUrl(combinedPath);
                Log.v(LOG_TAG, ((Character) currentItem).getImageUrl());
            } else if (currentItem instanceof Comic) {
                ViewCompat.setTransitionName(itemImage, (((Comic) currentItem).getTitle()));
                ((Comic) currentItem).setImageUrl(combinedPath);
                Log.v(LOG_TAG, ((Comic) currentItem).getImageUrl());
            } else if (currentItem instanceof Event) {
                ViewCompat.setTransitionName(itemImage, (((Event) currentItem).getEventTitle()));
                ((Event) currentItem).setImageUrl(combinedPath);
                Log.v(LOG_TAG, ((Event) currentItem).getImageUrl());
            } else if (currentItem instanceof Series) {
                ViewCompat.setTransitionName(itemImage, (((Series) currentItem).getSeriesTitle()));
                ((Series) currentItem).setImageUrl(combinedPath);

            }

            Log.v(LOG_TAG, combinedPath);
            try {
                Picasso.get().load(combinedPath).into(itemImage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //comicTitle.setText(currentComic.getTitle());
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void setItemData(List<T> items) {
        this.mItems = items;
        Log.v(LOG_TAG, "Items Count:" + mItems.size());
        notifyDataSetChanged();
    }

    public interface ItemOnClick<T> {
        void onClick(int adapterPosition, T item, ImageView transitionView);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final View mView;
        final ImageView mItemImage;
        //public final TextView mComicName;

        ViewHolder(View view) {
            super(view);
            mView = view;
            mItemImage = view.findViewById(R.id.item_image);
            mView.setOnClickListener(this);
            //mComicName = view.findViewById(R.id.comic_title);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            T item = mItems.get(adapterPosition);
            itemOnClick.onClick(adapterPosition, item, mItemImage);
        }
    }
}

