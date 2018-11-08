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

import com.example.android.marvelpedia.Adapters.TeamAdapter;
import com.example.android.marvelpedia.R;
import com.example.android.marvelpedia.model.Character;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TeamFragment extends Fragment {

    private final String LOG_TAG = TeamFragment.class.getSimpleName();
    @BindView(R.id.team_recycler_view)
    RecyclerView teamRecyclerView;
    private List<Character> teamMembers = new ArrayList<>();
    private TeamAdapter mTeamAdapter;

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
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Character currentCharacter = postSnapshot.getValue(Character.class);
                    Log.v(LOG_TAG, currentCharacter.getName());
                    teamMembers.add(currentCharacter);
                }
                mTeamAdapter.setTeamData(teamMembers);

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
}
