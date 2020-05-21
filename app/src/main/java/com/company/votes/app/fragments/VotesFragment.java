package com.company.votes.app.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.company.votes.R;
import com.company.votes.app.fragments.listadapter.VoteAdapter;
import com.company.votes.entity.Vote;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class VotesFragment extends Fragment {
    private static final String TAG = "ALL_VOTES";


    private FirebaseAuth auth;
    private VoteAdapter voteAdapter;
    private FirebaseDatabase db;
    private DatabaseReference ref;
    private String ownerUid;

    private ListView listOfVotes;

    ArrayList<Vote> votes = new ArrayList<>();
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        //setRetainInstance(true);
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        ref = db.getReference("Votes");
        ownerUid = auth.getCurrentUser().getUid();


        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_votes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        listOfVotes = getView().findViewById(R.id.listOfVotes);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                votes.clear();
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    Vote vote = singleSnapshot.getValue(Vote.class);
                    votes.add(vote);
                }
                Collections.reverse(votes);
                displayVotes();
                Log.d(TAG, votes.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "The read failed: " + databaseError.getCode());
            }
        });
        super.onViewCreated(view, savedInstanceState);
    }

    private void displayVotes(){
        if (getActivity()!=null) {
            voteAdapter = new VoteAdapter(getActivity(), votes);
            listOfVotes.setAdapter(voteAdapter);
        }
    }

}