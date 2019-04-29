package com.example.android.marvelpedia.ui.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.core.view.ViewCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.marvelpedia.ui.Adapters.MasterListCharacterAdapter;
import com.example.android.marvelpedia.ui.DetailActivity;
import com.example.android.marvelpedia.R;
import com.example.android.marvelpedia.data.Service.MasterIntentService;
import com.example.android.marvelpedia.data.Service.MyReceiver;
import com.example.android.marvelpedia.model.Character;
import com.example.android.marvelpedia.viewmodel.HeroViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.android.marvelpedia.ui.MainActivity.isConnected;

/**
 * A fragment representing a list of Items.
 */
public class MasterListFragment extends Fragment implements MasterListCharacterAdapter.CharacterAdapterOnClick {

    private final static String LOG_TAG = MasterListFragment.class.getSimpleName();
    private final String SAVED_CHARACTERS = "characters";

    @BindView(R.id.master_character_recycler_view)
    RecyclerView characterRecyclerView;
    @BindView(R.id.search_view_text)
    androidx.appcompat.widget.SearchView marvelSearchView;
    @BindView(R.id.master_progress_bar)
    ProgressBar loadingBar;
    @BindView(R.id.master_error_text_view)
    TextView emptyView;
    private String CHARACTER_EXTRAS;
    private CharSequence marvelSearchTerm;
    private MasterListCharacterAdapter mCharacterAdapter;
    private static List<Character> mCharacters = new ArrayList<>();
    private MyReceiver broadcastReceiver;
    private HeroViewModel heroViewModel;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MasterListFragment() {
    }

    public static MasterListFragment newInstance() {
        return new MasterListFragment();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        heroViewModel = ViewModelProviders.of(getActivity()).get(HeroViewModel.class);
        heroViewModel.getHeroList().observe(this, new Observer<List<Character>>() {
            @Override
            public void onChanged(List<Character> characters) {
                Log.v(LOG_TAG, "Outside of isEmpty");
                if (!(characters.isEmpty())) {
                    mCharacterAdapter.setCharacterData(characters);
                    characterRecyclerView.setAdapter(mCharacterAdapter);
                    loadingBar.setVisibility(View.GONE);
                    Log.v(LOG_TAG, "Inside of isEmpty");
                } else {
                    //set default visibility for the loading bar
                    loadingBar.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.character_list, container, false);

        //Retrieve any String values we may need from our Strings resource
        retrieveStrings();

        ButterKnife.bind(this, rootView);


        populateUi();

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

        //setupBroadcastReceiver();

        // Return the root view
        return rootView;
    }

    private void retrieveStrings() {
        CHARACTER_EXTRAS = getResources().getString(R.string.character_extras);
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

    private void getQueryFromSearchBar() {
        marvelSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String searchTerm) {
                marvelSearchTerm = searchTerm;
                Log.v(LOG_TAG, "Search Term: " + marvelSearchTerm);
                loadingBar.setVisibility(View.VISIBLE);
                emptyView.setText("");
                //prepareSearchIntent(searchTerm);
                heroViewModel.setHeroSearchTerm(marvelSearchTerm.toString());
                heroViewModel.getHerosFromSearchTerm();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }

    @Override
    public void onClick(int adapterPosition, Character character, ImageView view) {
        Intent characterActivity = new Intent(getContext(), DetailActivity.class);
        characterActivity.putExtra(CHARACTER_EXTRAS, character);
        String CHARACTER_TRANSITION_NAME = "character_transition";
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

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.v(LOG_TAG, "Saving Character List");
        outState.putParcelableArrayList(SAVED_CHARACTERS, (ArrayList<Character>) mCharacters);
    }
}
