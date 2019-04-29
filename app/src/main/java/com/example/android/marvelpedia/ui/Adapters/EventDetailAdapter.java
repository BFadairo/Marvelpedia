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
import com.example.android.marvelpedia.model.Event;
import com.example.android.marvelpedia.model.Thumbnail;
import com.squareup.picasso.Picasso;

import java.util.List;

public class EventDetailAdapter extends RecyclerView.Adapter<EventDetailAdapter.ViewHolder> {

    private final static String LOG_TAG = EventOnClick.class.getSimpleName();
    private final EventOnClick eventClick;
    private List<Event> mEvents;

    public EventDetailAdapter(Context context, List<Event> events, EventOnClick onClick) {
        //noinspection UnnecessaryLocalVariable,UnnecessaryLocalVariable,UnnecessaryLocalVariable
        @SuppressWarnings("UnnecessaryLocalVariable") Context mContext = context;
        mEvents = events;
        eventClick = onClick;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.detail_frag_list, parent, false);

        return new EventDetailAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Event currentEvent = mEvents.get(position);

        ImageView comicImage = viewHolder.mEventImage;
        //TextView comicTitle = viewHolder.mComicName;

        Thumbnail currentEventThumbnail = currentEvent.getThumbnails();

        String NO_IMAGE = "no_image_available";
        if (currentEventThumbnail.getPath().endsWith(NO_IMAGE)) {
            //If there's no Image marvel Image will be used
            Picasso.get().load(R.mipmap.ic_launcher).into(comicImage);
            comicImage.setScaleType(ImageView.ScaleType.FIT_XY);
            //TODO: Get a Marvel Image for this
        } else {
            comicImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            String thumbnailExtension = currentEventThumbnail.getExtension();
            String thumbnailPath = currentEventThumbnail.getPath();
            //String combinedPath = thumbnailPath + "." + thumbnailExtension;
            String portrait_uncanny = "portrait_uncanny";
            String combinedPath = thumbnailPath + "/" + portrait_uncanny + "." + thumbnailExtension;
            currentEvent.setImageUrl(combinedPath);
            Log.v(LOG_TAG, currentEvent.getImageUrl());
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
        return mEvents.size();
    }

    public void setEventData(List<Event> events) {
        this.mEvents = events;
        notifyDataSetChanged();
    }

    public interface EventOnClick {
        void onClick(Event event);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final View mView;
        final ImageView mEventImage;
        //public final TextView mComicName;

        ViewHolder(View view) {
            super(view);
            mView = view;
            mEventImage = view.findViewById(R.id.item_image);
            mView.setOnClickListener(this);
            //mComicName = view.findViewById(R.id.comic_title);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            Event event = mEvents.get(adapterPosition);
            eventClick.onClick(event);
        }
    }
}
