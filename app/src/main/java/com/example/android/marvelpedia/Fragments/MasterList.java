package com.example.android.marvelpedia.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.android.marvelpedia.Adapters.MasterListCharacterAdapter;
import com.example.android.marvelpedia.BuildConfig;
import com.example.android.marvelpedia.DetailActivity;
import com.example.android.marvelpedia.R;
import com.example.android.marvelpedia.Utils.Network.GetMarvelData;
import com.example.android.marvelpedia.Utils.Network.RetrofitInstance;
import com.example.android.marvelpedia.model.Character;
import com.example.android.marvelpedia.model.MarvelResultCharacter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A fragment representing a list of Items.
 */
public class MasterList extends Fragment implements MasterListCharacterAdapter.CharacterAdapterOnClick {

    private final static String LOG_TAG = MasterList.class.getSimpleName();
    private static final String ARG_COLUMN_COUNT = "column-count";
    private final static String CHARACTER_EXTRAS = "character_extras";
    private RecyclerView characterRecyclerView;
    private android.support.v7.widget.SearchView marvelSearchView;
    private CharSequence marvelSearchTerm;
    private MasterListCharacterAdapter mCharacterAdapter;
    private List<Character> mCharacters = new ArrayList<>();
    private RecyclerView.LayoutManager layoutManager;
    private MarvelResultCharacter fetchedData;
    private int mColumnCount = 3;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MasterList() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_character_list, container, false);

        // Get a reference to the RecyclerView in the fragment_master_list xml layout file
        characterRecyclerView = rootView.findViewById(R.id.master_character_recycler_view);
        marvelSearchTerm = rootView.findViewById(R.id.search_view_text);

        retrieveCharacters();

        // Return the root view
        return rootView;
    }

    private void retrieveCharacters() {
        populateUi();
        GetMarvelData marvelData = new RetrofitInstance().getRetrofitInstance().create(GetMarvelData.class);
        String apiKey = BuildConfig.MARVEL_API_KEY;
        String privateKey = BuildConfig.MARVEL_HASH_KEY;
        Call<MarvelResultCharacter> characterCall = marvelData.getCharacters("1", apiKey, privateKey, "Spider");
        Log.v(LOG_TAG, "" +
                characterCall.request().url());

        characterCall.enqueue(new Callback<MarvelResultCharacter>() {
            @Override
            public void onResponse(Call<MarvelResultCharacter> call, Response<MarvelResultCharacter> response) {
                if (response.isSuccessful()) {
                    fetchedData = response.body();
                    Log.v(LOG_TAG, fetchedData.getData().getCount().toString());
                    mCharacters.clear();
                    mCharacters = fetchedData.getData().getResults();
                    mCharacterAdapter.setCharacterData(mCharacters);
                    for (int i = 0; i < 10; i++){
                        Log.v(LOG_TAG, mCharacters.get(i).getName());
                    }
                    Log.v(LOG_TAG, "Retrofit Call Successful");
                }
            }

            @Override
            public void onFailure(Call<MarvelResultCharacter> call, Throwable t) {
                Log.v(LOG_TAG, t.getMessage());
            }
        });
    }


    private void populateUi() {
        //Create a new Character Adapter
        // This adapter takes in an empty list of characters as well as a context
        mCharacterAdapter = new MasterListCharacterAdapter(getContext(), mCharacters, this);

        //Set the adapter on the RecyclerView
        characterRecyclerView.setAdapter(mCharacterAdapter);

        //Create a GridLayoutManager
        layoutManager = new GridLayoutManager(getContext(), mColumnCount);
        characterRecyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public void onClick(Character character) {
        Intent characterActivity = new Intent(getContext(), DetailActivity.class);
        characterActivity.putExtra(CHARACTER_EXTRAS, character);
        startActivity(characterActivity);
    }

    private void getQueryFromSearchBar(){

        marvelSearchTerm = marvelSearchView.getQuery();
    }
}
