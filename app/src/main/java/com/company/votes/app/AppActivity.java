package com.company.votes.app;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;

import com.company.votes.R;
import com.company.votes.app.fragments.ProfileFragment;
import com.company.votes.app.fragments.VoteBuilderFragment;
import com.company.votes.app.fragments.VotesFragment;
import com.company.votes.authorization.MainActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class AppActivity extends AppCompatActivity {

    private VotesFragment votesFragment;
    private VoteBuilderFragment voteBuilderFragment;
    private ProfileFragment profileFragment;

    private FirebaseAuth auth;

    BottomNavigationView bottomNavigationView;
    //@RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);
        getSupportActionBar().setTitle("votes");
        getSupportActionBar().setSubtitle("Все доступные голосования");


        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        auth = FirebaseAuth.getInstance();
        votesFragment = new VotesFragment();
        voteBuilderFragment = new VoteBuilderFragment();
        profileFragment = new ProfileFragment();

        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container,
                votesFragment).commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    switch (menuItem.getItemId()){
                        case R.id.nav_votes:
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.fragment_container, votesFragment).commit();
                            getSupportActionBar().setSubtitle("Доступные голосования");
                            break;
                        case R.id.nav_vote_builder:
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.fragment_container, voteBuilderFragment).commit();
                            getSupportActionBar().setSubtitle("Новое голосование");
                            break;
                        case R.id.nav_profile:
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.fragment_container, profileFragment).commit();
                            getSupportActionBar().setSubtitle("Профиль");
                            break;
                    }

                    return true;
                }
            };

    public void signOut() {
        auth.signOut();
        startActivity(new Intent(AppActivity.this, MainActivity.class));
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
