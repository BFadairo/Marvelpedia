package com.example.android.marvelpedia.ui.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.marvelpedia.ui.Adapters.ComicDetailAdapter;
import com.example.android.marvelpedia.BuildConfig;
import com.example.android.marvelpedia.R;
import com.example.android.marvelpedia.data.Retrofit.GetMarvelData;
import com.example.android.marvelpedia.data.Retrofit.RetrofitInstance;
import com.example.android.marvelpedia.model.BaseJsonResponse;
import com.example.android.marvelpedia.model.Character;
import com.example.android.marvelpedia.model.Comic;
import com.example.android.marvelpedia.model.Data;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ComicFragment extends Fragment implements ComicDetailAdapter.ComicOnClick {

    private final static String LOG_TAG = ComicFragment.class.getSimpleName();
    private RecyclerView comicRecyclerView;
    private Character retrievedCharacter;
    private ComicDetailAdapter mComicAdapter;
    private TextView characterDescription, comicDetailHeader;
    private Data<Comic> comicData;
    private List<Comic> mComics = new ArrayList<>();
    private SendComicData comicInterface;

    public ComicFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail_comic_list, container, false);

        // Get a reference to the RecyclerView in the fragment_master_list xml layout file
        comicRecyclerView = rootView.findViewById(R.id.comic_recycler_view);
        comicDetailHeader = rootView.findViewById(R.id.comic_header);

        //Retrieve the passed arguments from the parent activity
        Bundle passedArgs = getArguments();

        //Retrieve the parcelable argument with key "character_extras"
        retrievedCharacter = passedArgs.getParcelable("character_extras");
        //
        comicInterface = (SendComicData) getActivity();

        retrieveComics();

        // Return the root view
        return rootView;
    }

    private void retrieveComics() {
        populateUi();
        GetMarvelData marvelData = new RetrofitInstance().getRetrofitInstance().create(GetMarvelData.class);
        String apiKey = BuildConfig.MARVEL_API_KEY;
        String privateKey = BuildConfig.MARVEL_HASH_KEY;
        Call<BaseJsonResponse<Comic>> comicCall = marvelData.getCharacterComics(retrievedCharacter.getId(), "1", apiKey, privateKey, 50);
        Log.v(LOG_TAG, "" +
                comicCall.request().url());

        comicCall.enqueue(new Callback<BaseJsonResponse<Comic>>() {
            @Override
            public void onResponse(Call<BaseJsonResponse<Comic>> call, Response<BaseJsonResponse<Comic>> response) {
                if (response.isSuccessful()) {
                    mComics.clear();
                    comicData = response.body().getData();
                    mComics = comicData.getResults();
                    //Log.v(LOG_TAG, fetchedData.getCharacterData().getCount().toString());
                    //mCharacters = fetchedData.getCharacterData().getCharacters();
                    mComicAdapter.setComicData(mComics);
                    /*for (int i = 0; i < 10; i++){
                        Log.v(LOG_TAG, mCharacters.get(i).getName());
                    }*/
                    Log.v(LOG_TAG, "Retrofit Call Successful");
                    setComicHeader();
                }
            }

            @Override
            public void onFailure(Call<BaseJsonResponse<Comic>> call, Throwable t) {
                Log.v(LOG_TAG, t.getMessage());
                Log.v(LOG_TAG, "Cause: " + t.getCause());
            }
        });
    }


    private void populateUi() {
        //Create a new Comic Adapter
        // This adapter takes in an empty list of comics as well as a context
        mComicAdapter = new ComicDetailAdapter(getContext(), mComics, this);

        //Set the adapter on the RecyclerView
        comicRecyclerView.setAdapter(mComicAdapter);

        //Create a Horizontal Linear Layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        comicRecyclerView.setLayoutManager(layoutManager);
    }

    private void setComicHeader() {
        String comicHeader = getString(R.string.comic_header);
        comicDetailHeader.setText(comicHeader);
    }

    @Override
    public void onClick(Comic comic) {
        comicInterface.sendComic(comic);
    }

    interface SendComicData {
        void sendComic(Comic comic);
    }
}
