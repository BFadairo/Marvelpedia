package com.example.android.marvelpedia.Fragments;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.marvelpedia.R;
import com.example.android.marvelpedia.model.Character;
import com.example.android.marvelpedia.model.Comic;
import com.example.android.marvelpedia.model.Event;
import com.example.android.marvelpedia.model.Series;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class DetailFragment extends Fragment {

    private final String LOG_TAG = DetailFragment.class.getSimpleName();
    private String CHARACTER_EXTRAS, COMIC_EXTRAS, EVENT_EXTRAS, SERIES_EXTRAS;
    private String imagePath;
    private Character passedCharacter;
    private Comic passedComic;
    private Event passedEvent;
    private Series passedSeries;
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
        String CHARACTER_TRANSITION_NAME = "character_transition";
        String transitionName = passedArgs.getString(CHARACTER_TRANSITION_NAME);
        retrieveStrings();
        postponeEnterTransition();

        //Get a reference to the description view
        detailDescription = rootView.findViewById(R.id.detail_description);

        //Get a reference to the imageView in the layout
        detailImage = rootView.findViewById(R.id.image_detail);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setSharedElementEnterTransition(TransitionInflater.from(getContext()).inflateTransition(R.transition.change_image_transform));
            detailImage.setTransitionName(transitionName);
            Log.v(LOG_TAG, detailImage.getTransitionName());
        }



        if (passedArgs != null) {
            if (passedArgs.getParcelable(CHARACTER_EXTRAS) != null) {
                passedCharacter = passedArgs.getParcelable(CHARACTER_EXTRAS);
            } else if (passedArgs.getParcelable(COMIC_EXTRAS) != null) {
                passedComic = passedArgs.getParcelable(COMIC_EXTRAS);
                //Log.v(LOG_TAG, passedComic.getTitle());
            } else if (passedArgs.getParcelable(EVENT_EXTRAS) != null) {
                passedEvent = passedArgs.getParcelable(EVENT_EXTRAS);
            } else if (passedArgs.getParcelable(SERIES_EXTRAS) != null) {
                passedSeries = passedArgs.getParcelable(SERIES_EXTRAS);
            }
        }
        //Retrieve the character details
        //This is information passed from the MasterList fragment
        //Is only used to retrieve and set the image on the detailActivity
        setExtraDetailInformation();
        setDetailImage();

        //Return the rootView
        return rootView;
    }

    private void setDetailImage() {
        if (passedCharacter != null) {
            imagePath = passedCharacter.getImageUrl();
        } else if (passedComic != null) {
            imagePath = passedComic.getImageUrl();
        } else if (passedEvent != null) {
            imagePath = passedEvent.getImageUrl();
        } else if (passedSeries != null) {
            imagePath = passedEvent.getImageUrl();
        }

        Picasso
                .get()
                .load(imagePath)
                .fit()
                .into(detailImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        startPostponedEnterTransition();
                    }

                    @Override
                    public void onError(Exception e) {
                        startPostponedEnterTransition();
                    }
                });
    }

    private void setExtraDetailInformation() {
        if (passedCharacter != null) {
            String characterBio = passedCharacter.getDescription();
            //Log.v(LOG_TAG, characterBio);
            detailDescription.setText(characterBio);
        } else if (passedComic != null) {
            String comicDescription = passedComic.getDescription();
            detailDescription.setText(comicDescription);
        }
    }

    private void retrieveStrings() {
        //Retrieve the strings for the extras
        CHARACTER_EXTRAS = getResources().getString(R.string.character_extras);
        COMIC_EXTRAS = getResources().getString(R.string.comic_extras);
        EVENT_EXTRAS = getResources().getString(R.string.event_extras);
        SERIES_EXTRAS = getResources().getString(R.string.series_extras);
    }
}
