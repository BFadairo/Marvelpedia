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
import com.example.android.marvelpedia.model.Character;
import com.example.android.marvelpedia.model.Thumbnail;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CharacterDetailAdapter extends RecyclerView.Adapter<CharacterDetailAdapter.ViewHolder> {

    private final static String LOG_TAG = CharacterDetailAdapter.class.getSimpleName();
    private final CharacterOnClick characterClick;
    private Context mContext;
    private List<Character> mCharacters;

    public CharacterDetailAdapter(Context context, List<Character> characters, CharacterOnClick onClick) {
        mContext = context;
        mCharacters = characters;
        characterClick = onClick;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.detail_frag_list, parent, false);

        return new CharacterDetailAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Character currentCharacter = mCharacters.get(position);

        ImageView comicImage = viewHolder.mCharacterImage;
        //TextView comicTitle = viewHolder.mComicName;

        Thumbnail currentComicThumbnail = currentCharacter.getThumbnail();

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
            String PORTRAIT_UNCANNY = "portrait_uncanny";
            String combinedPath = thumbnailPath + "/" + PORTRAIT_UNCANNY + "." + thumbnailExtension;
            currentCharacter.setImageUrl(combinedPath);
            Log.v(LOG_TAG, currentCharacter.getImageUrl());
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
        return mCharacters.size();
    }

    public void setCharacterData(List<Character> characters) {
        this.mCharacters = characters;
        notifyDataSetChanged();
    }

    public interface CharacterOnClick {
        void onClick(Character character);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final View mView;
        final ImageView mCharacterImage;
        //public final TextView mComicName;

        ViewHolder(View view) {
            super(view);
            mView = view;
            mCharacterImage = view.findViewById(R.id.item_image);
            mView.setOnClickListener(this);
            //mComicName = view.findViewById(R.id.comic_title);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            Character character = mCharacters.get(adapterPosition);
            characterClick.onClick(character);
        }
    }
}
