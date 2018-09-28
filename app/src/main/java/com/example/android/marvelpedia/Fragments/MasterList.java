package com.example.android.marvelpedia.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.marvelpedia.Adapters.MasterListCharacterAdapter;
import com.example.android.marvelpedia.R;
import com.example.android.marvelpedia.Utils.Network.GetMarvelData;
import com.example.android.marvelpedia.Utils.Network.RetrofitInstance;
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
public class MasterList extends Fragment {

    private final static String LOG_TAG = MasterList.class.getSimpleName();
    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    private RecyclerView characterRecyclerView;
    private MasterListCharacterAdapter mCharacterAdapter;
    private List<Character> mCharacters = new ArrayList<>();
    private RecyclerView.LayoutManager layoutManager;
    private Data fetchedData;
    // TODO: Customize parameters
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

        retrieveCharacters();

        // Return the root view
        return rootView;
    }

    private void retrieveCharacters() {
        populateUi();
        GetMarvelData marvelData = new RetrofitInstance().getRetrofitInstance().create(GetMarvelData.class);

        Call<Data> characterCall = marvelData.getCharacters("1", "cd962dcb13cefe34366b4076f38d5653", "323614045909f81499b1549b610f76fd", "Spider");
        Log.v(LOG_TAG, "" +
                characterCall.request().url());

        characterCall.enqueue(new Callback<Data>() {
            @Override
            public void onResponse(Call<Data> call, Response<Data> response) {
                if (response.isSuccessful()) {
                    Log.v(LOG_TAG, response.body().getCount().toString());
                    fetchedData = response.body();
                    Log.v(LOG_TAG, fetchedData.getCount().toString());
                    /*mCharacters.clear();
                    mCharacters = fetchedData.getResults();
                    for (int i = 0; i < 10; i++){
                        Log.v(LOG_TAG, mCharacters.get(i).getName());
                    }
                    Log.v(LOG_TAG, "Retrofit Call Successful");
                    */
                }
            }

            @Override
            public void onFailure(Call<Data> call, Throwable t) {
                Log.v(LOG_TAG, t.getMessage());
            }
        });


    }


    private void populateUi() {
        //Create a new Character Adapter
        // This adapter takes in an empty list of characters as well as a context
        mCharacterAdapter = new MasterListCharacterAdapter(getContext(), mCharacters);

        //Set the adapter on the RecyclerView
        characterRecyclerView.setAdapter(mCharacterAdapter);

        //Create a GridLayoutManager
        layoutManager = new GridLayoutManager(getContext(), mColumnCount);
        characterRecyclerView.setLayoutManager(layoutManager);
    }
}
