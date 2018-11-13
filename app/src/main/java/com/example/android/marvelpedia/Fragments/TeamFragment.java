package com.example.android.marvelpedia.Fragments;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.marvelpedia.Adapters.DetailExtrasAdapter;
import com.example.android.marvelpedia.Adapters.TeamAdapter;
import com.example.android.marvelpedia.BuildConfig;
import com.example.android.marvelpedia.R;
import com.example.android.marvelpedia.Utils.Network.GetMarvelData;
import com.example.android.marvelpedia.Utils.Network.RetrofitInstance;
import com.example.android.marvelpedia.model.BaseJsonResponse;
import com.example.android.marvelpedia.model.Character;
import com.example.android.marvelpedia.model.Comic;
import com.example.android.marvelpedia.model.Data;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TeamFragment extends Fragment implements DetailExtrasAdapter.ItemOnClick<Comic>,
        SharedPreferences.OnSharedPreferenceChangeListener, TeamAdapter.startMemberActivity {

    private final String LOG_TAG = TeamFragment.class.getSimpleName();
    @BindView(R.id.team_recycler_view)
    RecyclerView teamRecyclerView;
    @BindView(R.id.team_comic_recycler_view)
    RecyclerView teamComicRecyclerView;
    private List<Character> teamMembers = new ArrayList<>();
    private List<Comic> teamComics = new ArrayList<>();
    private Data<Comic> comicData;
    private TeamAdapter mTeamAdapter;
    @BindView(R.id.team_name)
    TextView mTeamName;
    private DetailExtrasAdapter<Comic> mTeamComicAdapter;
    private DatabaseReference teamMember;
    private String userId;
    private FirebaseDatabase mDatabase;


    public TeamFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_team, container, false);

        ButterKnife.bind(this, rootView);

        //Check if the user is logged in and save their id
        checkIfUserLoggedIn();
        //Get the Firebase Instance
        mDatabase = FirebaseDatabase.getInstance();

        //getTeamMemberReference(mDatabase);
        populateUi();

        if (savedInstanceState != null) {
            teamComics = savedInstanceState.getParcelableArrayList("Comics");
            teamMembers = savedInstanceState.getParcelableArrayList("Members");
            populateComics();
            mTeamComicAdapter.setItemData(teamComics);
        } else {
            queryDatabase();
        }

        registerPreferenceChangeListener();
        setTeamName();

        return rootView;
    }

    private void checkIfUserLoggedIn() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            //Retrieve the user's email address
            userId = user.getUid();
        }
    }

    private void getTeamMemberReference(FirebaseDatabase database) {
        teamMember = database.getReference("users").child(userId).child("team");
    }

    private void populateUi() {
        //Create a new Comic Adapter
        // This adapter takes in an empty list of comics as well as a context
        mTeamAdapter = new TeamAdapter(getContext(), teamMembers, this);

        //Set the adapter on the RecyclerView
        teamRecyclerView.setAdapter(mTeamAdapter);

        //Create a Horizontal Linear Layout manager
        int numOfColumns = 3;
        RecyclerView.LayoutManager layoutManager;
        layoutManager = new StaggeredGridLayoutManager(numOfColumns, StaggeredGridLayoutManager.VERTICAL);
        teamRecyclerView.setLayoutManager(layoutManager);
    }

    private void populateComics() {
        mTeamComicAdapter = new DetailExtrasAdapter<>(getContext(), teamComics, this);
        teamComicRecyclerView.setAdapter(mTeamComicAdapter);

        RecyclerView.LayoutManager layoutManager;
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        teamComicRecyclerView.setLayoutManager(layoutManager);
    }

    private void getTeamComics(List<Character> characters) {
        populateComics();
        GetMarvelData marvelData = new RetrofitInstance().getRetrofitInstance().create(GetMarvelData.class);
        String apiKey = BuildConfig.MARVEL_API_KEY;
        String privateKey = BuildConfig.MARVEL_HASH_KEY;
        Call<BaseJsonResponse<Comic>> characterCall = marvelData.getTeamComics("1", apiKey, privateKey, buildCharacterIdList(characters));
        Log.v(LOG_TAG, "" +
                characterCall.request().url());

        characterCall.enqueue(new Callback<BaseJsonResponse<Comic>>() {
            @Override
            public void onResponse(Call<BaseJsonResponse<Comic>> call, Response<BaseJsonResponse<Comic>> response) {
                if (response.isSuccessful()) {
                    teamComics.clear();
                    comicData = response.body().getData();
                    teamComics = comicData.getResults();
                    //Log.v(LOG_TAG, fetchedData.getCharacterData().getCount().toString());
                    //mCharacters = fetchedData.getCharacterData().getCharacters();
                    mTeamComicAdapter.setItemData(teamComics);
                    /*for (int i = 0; i < 10; i++){
                        Log.v(LOG_TAG, mCharacters.get(i).getName());
                    }*/
                    Log.v(LOG_TAG, "Retrofit Call Successful");
                }
            }

            @Override
            public void onFailure(Call<BaseJsonResponse<Comic>> call, Throwable t) {
                Log.v(LOG_TAG, t.getMessage());
            }
        });
    }

    private String buildCharacterIdList(List<Character> characters) {
        String divider = ",";
        String queryString;
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < characters.size(); i++) {
            stringBuilder.append(characters.get(i).getId()).append(divider);
        }
        queryString = stringBuilder.toString();
        Log.v(LOG_TAG, queryString);
        return queryString;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("Comics", (ArrayList<Comic>) teamComics);
        outState.putParcelableArrayList("Members", (ArrayList<Character>) teamMembers);
    }

    private void queryDatabase() {
        DatabaseReference root = mDatabase.getReference();
        teamMember = root.child("users").child(userId).child("team");
        teamMember.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                teamMembers.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Character currentCharacter = postSnapshot.getValue(Character.class);
                    Log.v(LOG_TAG, currentCharacter.getName());
                    teamMembers.add(currentCharacter);
                }
                mTeamAdapter.setTeamData(teamMembers);
                getTeamComics(teamMembers);
                updateAppWidget();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Failed to read value
                Log.w(LOG_TAG, "Failed to Read Database Value.", databaseError.toException());
                Log.v(LOG_TAG, databaseError.getMessage());
            }
        });
    }

    private void updateAppWidget() {
        Intent widgetIntent = new Intent();
        widgetIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        getActivity().sendBroadcast(widgetIntent);
    }

    private void setTeamName() {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(getContext());

        String retrievedTeamName = preferences.getString(getResources().getString(R.string.team_name_key), "");
        mTeamName.setText(retrievedTeamName);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        setTeamName();
    }

    private void registerPreferenceChangeListener() {
        /*
         * Register TeamFragment as an OnPreferenceChangedListener to receive a callback when a
         * SharedPreference has changed. Please note that we must unregister TeamFragment as an
         * OnSharedPreferenceChanged listener in onDestroy to avoid any memory leaks.
         */
        PreferenceManager.getDefaultSharedPreferences(getContext())
                .registerOnSharedPreferenceChangeListener(this);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        /*
         * Register TeamFragment as an OnPreferenceChangedListener to receive a callback when a
         * SharedPreference has changed. Please note that we must unregister TeamFragment as an
         * OnSharedPreferenceChanged listener in onDestroy to avoid any memory leaks.
         */
        PreferenceManager.getDefaultSharedPreferences(getContext())
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onClick(int adapterPosition, Comic item, ImageView transitionView) {
        Log.v(LOG_TAG, item.getTitle());
        Toast.makeText(getContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(int adapterPosition, Character character, ImageView transitionView) {
        Log.v(LOG_TAG, character.getName());
        Toast.makeText(getContext(), character.getName(), Toast.LENGTH_SHORT).show();
    }
}
