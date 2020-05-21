package com.company.votes.app.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.company.votes.R;
import com.company.votes.app.AppActivity;
import com.company.votes.entity.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "PROFILE";

    private TextView email;
    private TextView name;
    private TextView phone;
    ProgressBar progressBar;

    private FirebaseAuth auth;
    private FirebaseDatabase db;
    private DatabaseReference ref;

    private User user;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setRetainInstance(true);
        user =  new User();
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        Log.d(TAG, auth.getCurrentUser().getUid());
        ref = db.getReference("Users/" + auth.getCurrentUser().getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                email.setText(user.getEmail());
                name.setText(user.getName());
                phone.setText(user.getPhone());
                progressBar.setVisibility(progressBar.GONE);
                getView().findViewById(R.id.profileInfo).setVisibility(View.VISIBLE);
                Log.d(TAG, user.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "The read failed: " + databaseError.getCode());
            }
        });
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            progressBar = getView().findViewById(R.id.progressBarProfile);

            if (user.getEmail().length() == 0) {
                progressBar.setVisibility(progressBar.VISIBLE);
                getView().findViewById(R.id.profileInfo).setVisibility(View.GONE);
            }

            email = getView().findViewById(R.id.profileEmail);
            name = getView().findViewById(R.id.profileName);
            phone = getView().findViewById(R.id.profilePhone);

            getView().findViewById(R.id.signOutP).setOnClickListener(this);
            //progressBar.setVisibility(progressBar.VISIBLE);

            email.setText(user.getEmail());
            name.setText(user.getName());
            phone.setText(user.getPhone());
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        switch (i){
            case R.id.signOutP:
                ((AppActivity)getActivity()).signOut();
                Log.d(TAG, "signed out successfully");
                break;
        }
    }
}
