package com.example.android.marvelpedia.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.marvelpedia.Adapters.MasterListCharacterAdapter;
import com.example.android.marvelpedia.DetailActivity;
import com.example.android.marvelpedia.R;
import com.example.android.marvelpedia.Service.MasterIntentService;
import com.example.android.marvelpedia.Service.MyReceiver;
import com.example.android.marvelpedia.model.Character;
import com.example.android.marvelpedia.model.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 */
public class MasterList extends Fragment implements MasterListCharacterAdapter.CharacterAdapterOnClick {

    private final static String LOG_TAG = MasterList.class.getSimpleName();
    private final String SAVED_CHARACTERS = "characters";
    private final String CHARACTER_TRANSITION_NAME = "character_transition";
    private static final String ACTION_CHARS = "com.example.android.marvelpedia.action.CHARS";
    private static final String EXTRA_PARAM1 = "com.example.android.marvelpedia.extra.PARAM1";
    private String ATTRIBUTION_TEXT;
    private RecyclerView characterRecyclerView;
    private android.support.v7.widget.SearchView marvelSearchView;
    private ImageView dataImage;
    private CharSequence marvelSearchTerm;
    private MasterListCharacterAdapter mCharacterAdapter;
    private Data<Character> characterData;
    private static List<Character> mCharacters = new ArrayList<>();
    private MyReceiver broadcastReceiver;
    private Boolean isConnected;
    private ProgressBar loadingBar;
    private TextView emptyView;


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

        //Find the views related to errors
        emptyView = rootView.findViewById(R.id.master_error_text_view);
        loadingBar = rootView.findViewById(R.id.master_progress_bar);
        //set default visibility for the loading bar
        loadingBar.setVisibility(View.GONE);


        checkNetworkStatus();

        if (savedInstanceState != null) {
            mCharacters = savedInstanceState.getParcelableArrayList(SAVED_CHARACTERS);
            populateUi();
            mCharacterAdapter.setCharacterData(mCharacters);
        }

        if (isConnected) {
            //Used to retrieve the query and update the search results from the SearchView
            getQueryFromSearchBar();
        } else {
            //Set the empty view to No Connection when no internet connection is found
            emptyView.setText(R.string.no_connection);
            // Sets the loading bar to gone when loading is finished
            loadingBar.setVisibility(View.GONE);
        }

        setupBroadcastReceiver();

        // Return the root view
        return rootView;
    }

    private void populateUi() {
        //Create a new Character Adapter
        // This adapter takes in an empty list of characters as well as a context
        mCharacterAdapter = new MasterListCharacterAdapter(getContext(), mCharacters, this);

        //Set the adapter on the RecyclerView
        characterRecyclerView.setAdapter(mCharacterAdapter);

        //Create a GridLayoutManager
        int mColumnCount = 3;
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), mColumnCount);
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
                loadingBar.setVisibility(View.VISIBLE);
                emptyView.setText("");
                prepareSearchIntent(searchTerm);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }

    private void checkNetworkStatus() {
        //Check the current internet connection of the phone
        ConnectivityManager cm =
                (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }


    @Override
    public void onClick(int adapterPosition, Character character, ImageView view) {
        Intent characterActivity = new Intent(getContext(), DetailActivity.class);
        String CHARACTER_EXTRAS = "character_extras";
        characterActivity.putExtra(CHARACTER_EXTRAS, character);
        characterActivity.putExtra(CHARACTER_TRANSITION_NAME, ViewCompat.getTransitionName(view));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Log.v(LOG_TAG, view.getTransitionName());
            characterActivity.putExtra("transition_name", view.getTransitionName());
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), view, ViewCompat.getTransitionName(view));
            startActivity(characterActivity, options.toBundle());
        } else {
            startActivity(characterActivity);
        }
    }

    private void prepareSearchIntent(String searchTerm) {
        MasterIntentService.startActionPopulateCharacters(getContext(), searchTerm);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.v(LOG_TAG, "Saving Character List");
        outState.putParcelableArrayList(SAVED_CHARACTERS, (ArrayList<Character>) mCharacters);
    }

    private void setupBroadcastReceiver() {
        if (getContext() != null) {
            broadcastReceiver = new MyReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent.getAction().equals(ACTION_CHARS)) {
                        Log.v(LOG_TAG, "Inside onReceive");
                        Bundle receivedArgs = intent.getBundleExtra(EXTRA_PARAM1);
                        mCharacters = receivedArgs.getParcelableArrayList(EXTRA_PARAM1);
                        populateUi();
                        mCharacterAdapter.setCharacterData(mCharacters);
                        loadingBar.setVisibility(View.GONE);
                        if (mCharacters.isEmpty()) {
                            emptyView.setText(R.string.no_results);
                        }
                        //throw new UnsupportedOperationException("Not yet implemented");
                    }
                }
            };
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(LOG_TAG, "On Resume Called");
        if (getContext() != null) {
            IntentFilter filter = new IntentFilter(ACTION_CHARS);
            filter.addCategory(Intent.CATEGORY_DEFAULT);
            getContext().registerReceiver(broadcastReceiver, filter);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v(LOG_TAG, "On Pause Called");
        if (getContext() != null) {
            getContext().unregisterReceiver(broadcastReceiver);
        }
    }
}
