package com.example.android.marvelpedia.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.marvelpedia.R;
import com.example.android.marvelpedia.model.Character;
import com.example.android.marvelpedia.model.Thumbnail;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MasterListCharacterAdapter extends RecyclerView.Adapter<MasterListCharacterAdapter.ViewHolder> {

    private final static String LOG_TAG = MasterListCharacterAdapter.class.getSimpleName();
    private final String NO_IMAGE = "no_image_available";
    private List<Character> mCharacters;
    private final CharacterAdapterOnClick charClickHandler;
    private Context mContext;

    public MasterListCharacterAdapter(Context context, List<Character> characters, CharacterAdapterOnClick adapterOnClick) {
        mContext = context;
        mCharacters = characters;
        charClickHandler = adapterOnClick;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_character, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        Character currentCharacter = mCharacters.get(position);

        ImageView characterImage = holder.mCharacterImage;

        TextView characterName = holder.mCharacterName;

        Thumbnail charThumbnail = currentCharacter.getThumbnail();

        if (charThumbnail.getPath().endsWith(NO_IMAGE)) {
            //If there's no Image marvel Image will be used
            Picasso.get().load(R.mipmap.placeholder).into(characterImage);
            characterImage.setScaleType(ImageView.ScaleType.FIT_XY);
            //TODO: Get a Marvel Image for this
        } else {
            characterImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            String thumbnailExtension = charThumbnail.getExtension();
            String thumbnailPath = charThumbnail.getPath();
            String combinedPath = thumbnailPath + "." + thumbnailExtension;
            currentCharacter.setImageUrl(combinedPath);
            Log.v(LOG_TAG, currentCharacter.getImageUrl());
            Log.v(LOG_TAG, combinedPath);
            try {
                Picasso.get().load(combinedPath).into(characterImage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        characterName.setText(currentCharacter.getName());
    }

    public void setCharacterData(List<Character> characters) {
        this.mCharacters = characters;
        notifyDataSetChanged();
    }

    public interface CharacterAdapterOnClick {
        void onClick(Character character);
    }

    @Override
    public int getItemCount() {
        return mCharacters.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final View mView;
        public final ImageView mCharacterImage;
        public final TextView mCharacterName;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mView.setOnClickListener(this);
            mCharacterImage = view.findViewById(R.id.master_list_image);
            mCharacterName = view.findViewById(R.id.master_list_character_name);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            Character character = mCharacters.get(adapterPosition);
            charClickHandler.onClick(character);
        }
    }
}
