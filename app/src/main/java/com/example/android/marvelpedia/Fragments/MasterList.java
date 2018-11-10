package com.example.android.marvelpedia.Fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.marvelpedia.Adapters.MasterListCharacterAdapter;
import com.example.android.marvelpedia.BuildConfig;
import com.example.android.marvelpedia.DetailActivity;
import com.example.android.marvelpedia.R;
import com.example.android.marvelpedia.Utils.Network.GetMarvelData;
import com.example.android.marvelpedia.Utils.Network.RetrofitInstance;
import com.example.android.marvelpedia.model.BaseJsonResponse;
import com.example.android.marvelpedia.model.Character;
import com.example.android.marvelpedia.model.Data;

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
    private final String CHARACTER_EXTRAS = "character_extras";
    private final String SAVED_CHARACTERS = "characters";
    private String ATTRIBUTION_TEXT;
    private RecyclerView characterRecyclerView;
    private android.support.v7.widget.SearchView marvelSearchView;
    private ImageView dataImage;
    private CharSequence marvelSearchTerm;
    private MasterListCharacterAdapter mCharacterAdapter;
    private Data<Character> characterData;
    private static List<Character> mCharacters = new ArrayList<>();
    private RecyclerView.LayoutManager layoutManager;
    private static Bundle savedCharacters;
    private int mColumnCount = 3;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MasterList() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.character_list, container, false);

        // Get a reference to the RecyclerView in the fragment_master_list xml layout file
        characterRecyclerView = rootView.findViewById(R.id.master_character_recycler_view);
        marvelSearchView = rootView.findViewById(R.id.search_view_text);

        savedCharacters = new Bundle();

        if (savedInstanceState != null) {
            mCharacters = savedInstanceState.getParcelableArrayList(SAVED_CHARACTERS);
            populateUi();
            mCharacterAdapter.setCharacterData(mCharacters);
        } else {
            //Used to retrieve the query and update the search results from the SearchView
            getQueryFromSearchBar();
        }

        // Return the root view
        return rootView;
    }

    /**
     * Thie method is used to search for characters
     *
     * @param searchTerm for this case (the hero's name)
     */
    private void retrieveCharacters(String searchTerm) {
        populateUi();
        GetMarvelData marvelData = new RetrofitInstance().getRetrofitInstance().create(GetMarvelData.class);
        String apiKey = BuildConfig.MARVEL_API_KEY;
        String privateKey = BuildConfig.MARVEL_HASH_KEY;
        Call<BaseJsonResponse<Character>> characterCall = marvelData.getCharacters("1", apiKey, privateKey, searchTerm, 100);
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

    /*@Override
    public void onClick(Character character) {
        Intent characterActivity = new Intent(getContext(), DetailActivity.class);
        characterActivity.putExtra(CHARACTER_EXTRAS, character);
        startActivity(characterActivity);
    }*/

    private void getQueryFromSearchBar() {
        marvelSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String searchTerm) {
                marvelSearchTerm = searchTerm;
                Log.v(LOG_TAG, "Search Term: " + marvelSearchTerm);
                retrieveCharacters(searchTerm);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }


    @Override
    public void onClick(Character character, ImageView view) {
        Intent characterActivity = new Intent(getContext(), DetailActivity.class);
        characterActivity.putExtra(CHARACTER_EXTRAS, character);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Log.v(LOG_TAG, view.getTransitionName());
            characterActivity.putExtra("transition_name", view.getTransitionName());
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), view, view.getTransitionName());
            startActivity(characterActivity, options.toBundle());
        } else {
            startActivity(characterActivity);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.v(LOG_TAG, "Saving Character List");
        outState.putParcelableArrayList(SAVED_CHARACTERS, (ArrayList<Character>) mCharacters);
    }

    /*@Override
    public void onResume() {
        super.onResume();
        Log.v(LOG_TAG, "On Resume Called");
        if (!(mCharacters.isEmpty())) {
            mCharacters = savedCharacters.getParcelable(SAVED_CHARACTERS);
            populateUi();
            mCharacterAdapter.setCharacterData(mCharacters);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v(LOG_TAG, "On Pause Called");
        savedCharacters.putParcelableArrayList(SAVED_CHARACTERS, (ArrayList<Character>) mCharacters);
        for (int i = 0; i < mCharacters.size(); i++){
            Log.v(LOG_TAG, mCharacters.get(i).getName());
        }
    }*/
}
