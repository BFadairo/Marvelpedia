package com.example.android.marvelpedia.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.marvelpedia.R;
import com.example.android.marvelpedia.model.Character;
import com.example.android.marvelpedia.model.Comic;
import com.squareup.picasso.Picasso;

public class DetailFragment extends Fragment {

    private final String LOG_TAG = DetailFragment.class.getSimpleName();
    private final String CHARACTER_EXTRAS = "character_extras";
    private final String COMIC_EXTRAS = "comic_extras";
    public String imagePath;
    private Character passedCharacter;
    private Comic passedComic;
    private ImageView detailImage;
    private TextView detailDescription;


    public DetailFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        //Get the passed arguments
        Bundle passedArgs = getArguments();

        if (passedArgs.getParcelable(CHARACTER_EXTRAS) != null) {
            passedCharacter = passedArgs.getParcelable(CHARACTER_EXTRAS);
        } else if (passedArgs.getParcelable(COMIC_EXTRAS) != null) {
            passedComic = passedArgs.getParcelable(COMIC_EXTRAS);
            Log.v(LOG_TAG, passedComic.getTitle());
        }

        //Get a reference to the description view
        detailDescription = rootView.findViewById(R.id.detail_description);
        setExtraDetailInformation();

        //Get a reference to the imageView in the layout
        detailImage = rootView.findViewById(R.id.image_detail);

        //Retrieve the character details
        //This is information passed from the MasterList fragment
        //Is only used to retrieve and set the image on the detailActivity
        //TODO: Change this to account for different types
        setDetailImage();

        //Return the rootView
        return rootView;
    }

    private void setDetailImage() {
        if (passedCharacter != null) {
            imagePath = passedCharacter.getImageUrl();
        } else if (passedComic != null) {
            imagePath = passedComic.getImageUrl();
        }

        Picasso.get().load(imagePath).into(detailImage);
    }

    private void setExtraDetailInformation() {
        if (passedCharacter != null) {
            String characterBio = passedCharacter.getDescription();
            detailDescription.setText(characterBio);
        } else if (passedComic != null) {
            String comicDescription = passedComic.getDescription();
            detailDescription.setText(comicDescription);
        }
    }
}
