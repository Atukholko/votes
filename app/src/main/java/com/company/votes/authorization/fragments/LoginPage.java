package com.company.votes.authorization.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.company.votes.R;
import com.company.votes.app.AppActivity;
import com.company.votes.authorization.validator.AuthValidator;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.rengwuxian.materialedittext.MaterialEditText;

public class LoginPage extends Fragment implements View.OnClickListener {
    private static final String TAG = "AUTHORIZATION";

    private RelativeLayout authorizationLayout;
    private MaterialEditText emailField;
    private MaterialEditText passwordField;

    private FirebaseAuth auth;
    private ProgressBar progressBar;

    private AuthValidator validator;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.login, container,false);

        rootView.findViewById(R.id.signInButton).setOnClickListener(this);

        progressBar = rootView.findViewById(R.id.progressBarLogin);
        //FireBase
        auth = FirebaseAuth.getInstance();

        //Form
        emailField = rootView.findViewById(R.id.emailField);
        passwordField = rootView.findViewById(R.id.passwordField);

        authorizationLayout = rootView.findViewById(R.id.login_layout);
        validator = new AuthValidator();

        return rootView;
    }

    private void signIn() {
        emailField = getActivity().findViewById(R.id.emailField);
        passwordField = getActivity().findViewById(R.id.passwordField);
        if(!validateForm(emailField, passwordField)){
            return;
        }

        Log.d(TAG, "signInAccount:" + String.valueOf(emailField.getText()));
        progressBar.setVisibility(ProgressBar.VISIBLE);
        auth.signInWithEmailAndPassword(String.valueOf(emailField.getText()), String.valueOf(passwordField.getText()))
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Log.d(TAG, "signInWithEmail:success");
                        FirebaseUser user = auth.getCurrentUser();
                        updateUI(user);
                        progressBar.setVisibility(ProgressBar.INVISIBLE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "signInUserWithEmail:failure(Не существует такой учетной записи)");
                        emailField.setText("");
                        passwordField.setText("");

                        Snackbar.make(authorizationLayout, "Не существует такой учетной записи", Snackbar.LENGTH_SHORT).show();
                        progressBar.setVisibility(ProgressBar.INVISIBLE);
                    }
                });
    }

    private boolean validateForm(MaterialEditText emailField, MaterialEditText passwordField){
        boolean result = true;
        String email = String.valueOf(emailField.getText());
        String password = String.valueOf(passwordField.getText());

        if(!validator.emailValid(email)){
            emailField.setError("Неверная почта");
            result = false;
        }
        if(!validator.passwordValid(password)){
            passwordField.setError("Неверный пароль");
            result = false;
        }
        return result;
    }

    private void updateUI(FirebaseUser user){
        if(user != null){
            startActivity(new Intent(getActivity(), AppActivity.class));
        }
        else{
            return;
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        switch (i){
            case R.id.signInButton:
                signIn();
                break;
        }
    }

}
