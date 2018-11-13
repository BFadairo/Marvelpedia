package com.example.android.marvelpedia.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.marvelpedia.Adapters.CharacterDetailAdapter;
import com.example.android.marvelpedia.BuildConfig;
import com.example.android.marvelpedia.R;
import com.example.android.marvelpedia.Utils.Network.GetMarvelData;
import com.example.android.marvelpedia.Utils.Network.RetrofitInstance;
import com.example.android.marvelpedia.model.BaseJsonResponse;
import com.example.android.marvelpedia.model.Character;
import com.example.android.marvelpedia.model.Comic;
import com.example.android.marvelpedia.model.Data;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CharacterFragment extends Fragment implements CharacterDetailAdapter.CharacterOnClick {

    private final static String LOG_TAG = CharacterFragment.class.getSimpleName();
    private RecyclerView characterRecyclerView;
    private Comic retrievedComic;
    private CharacterDetailAdapter mCharacterAdapter;
    private TextView characterDescription;
    private Data<Character> characterData;
    private List<Character> mCharacters = new ArrayList<>();

    public CharacterFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail_character_list, container, false);

        //Get the reference to the character recycler view
        characterRecyclerView = rootView.findViewById(R.id.character_recycler_view);
        TextView characterDetailHeader = rootView.findViewById(R.id.character_header);

        //Retrieve the passed arguments from the parent activity
        Bundle passedArgs = getArguments();

        //Retrieve the parcelable argument with key "character_extras"
        retrievedComic = passedArgs.getParcelable("comic_extras");

        retrieveCharacters();

        //return the rootView
        return rootView;
    }

    private void retrieveCharacters() {
        populateUi();
        GetMarvelData marvelData = new RetrofitInstance().getRetrofitInstance().create(GetMarvelData.class);
        String apiKey = BuildConfig.MARVEL_API_KEY;
        String privateKey = BuildConfig.MARVEL_HASH_KEY;
        Call<BaseJsonResponse<Character>> characterCall = marvelData.getComicCharacters(retrievedComic.getId(), "1", apiKey, privateKey);
        Log.v(LOG_TAG, "" +
                characterCall.request().url());

        characterCall.enqueue(new Callback<BaseJsonResponse<Character>>() {
            @Override
            public void onResponse(Call<BaseJsonResponse<Character>> call, Response<BaseJsonResponse<Character>> response) {
                if (response.isSuccessful()) {
                    mCharacters.clear();
                    characterData = response.body().getData();
                    mCharacters = characterData.getResults();
                    //Log.v(LOG_TAG, fetchedData.getCharacterData().getCount().toString());
                    //mCharacters = fetchedData.getCharacterData().getCharacters();
                    mCharacterAdapter.setCharacterData(mCharacters);
                    /*for (int i = 0; i < 10; i++){
                        Log.v(LOG_TAG, mCharacters.get(i).getName());
                    }*/
                    Log.v(LOG_TAG, "Retrofit Call Successful");
                }
            }

            @Override
            public void onFailure(Call<BaseJsonResponse<Character>> call, Throwable t) {
                Log.v(LOG_TAG, t.getMessage());
                Log.v(LOG_TAG, "Cause: " + t.getCause());
            }
        });
    }

    private void populateUi() {
        //Create a new Character Adapter
        // This adapter takes in an empty list of Characters as well as a context
        mCharacterAdapter = new CharacterDetailAdapter(getContext(), mCharacters, this);

        //Set the adapter on the RecyclerView
        characterRecyclerView.setAdapter(mCharacterAdapter);

        //Create a Horizontal Linear Layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        characterRecyclerView.setLayoutManager(layoutManager);
    }


    @Override
    public void onClick(Character character) {

    }
}
