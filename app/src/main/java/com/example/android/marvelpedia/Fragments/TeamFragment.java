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
import android.widget.ImageView;

import com.example.android.marvelpedia.Adapters.TeamAdapter;
import com.example.android.marvelpedia.Adapters.TestAdapter;
import com.example.android.marvelpedia.BuildConfig;
import com.example.android.marvelpedia.R;
import com.example.android.marvelpedia.Utils.Network.GetMarvelData;
import com.example.android.marvelpedia.Utils.Network.RetrofitInstance;
import com.example.android.marvelpedia.model.BaseJsonResponse;
import com.example.android.marvelpedia.model.Character;
import com.example.android.marvelpedia.model.Comic;
import com.example.android.marvelpedia.model.Data;
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

public class TeamFragment extends Fragment implements TestAdapter.ItemOnClick<Comic> {

    private final String LOG_TAG = TeamFragment.class.getSimpleName();
    @BindView(R.id.team_recycler_view)
    RecyclerView teamRecyclerView;
    @BindView(R.id.team_comic_recycler_view)
    RecyclerView teamComicRecyclerView;
    private List<Character> teamMembers = new ArrayList<>();
    private List<Comic> teamComics = new ArrayList<>();
    private Data<Comic> comicData;
    private TeamAdapter mTeamAdapter;
    private TestAdapter<Comic> mTeamComicAdapter;

    public TeamFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_team, container, false);

        ButterKnife.bind(this, rootView);

        //Get the Firebase Instance
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference teamMember = database.getReference();
        populateUi();

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

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Failed to read value
                Log.w(LOG_TAG, "Failed to Read Database Value.", databaseError.toException());
            }
        });
        return rootView;
    }

    private void populateUi() {
        //Create a new Comic Adapter
        // This adapter takes in an empty list of comics as well as a context
        mTeamAdapter = new TeamAdapter(getContext(), teamMembers);

        //Set the adapter on the RecyclerView
        teamRecyclerView.setAdapter(mTeamAdapter);

        //Create a Horizontal Linear Layout manager
        RecyclerView.LayoutManager layoutManager;
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        teamRecyclerView.setLayoutManager(layoutManager);
    }

    private void populateComics() {
        mTeamComicAdapter = new TestAdapter<>(getContext(), teamComics, this);
        teamComicRecyclerView.setAdapter(mTeamComicAdapter);

        RecyclerView.LayoutManager layoutManager;
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        teamComicRecyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public void onClick(Comic item, ImageView transitionView) {

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
}
