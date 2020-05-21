package com.company.votes.authorization.fragments;

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
import androidx.fragment.app.FragmentManager;

import com.company.votes.R;
import com.company.votes.authorization.MainActivity;
import com.company.votes.authorization.validator.AuthValidator;
import com.company.votes.entity.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

public class RegisterPage extends Fragment implements View.OnClickListener {
    private static final String TAG = "REGISTRATION";

    private RelativeLayout authorizationLayout;
    private MaterialEditText emailField;
    private MaterialEditText passwordField;
    private MaterialEditText nameField;
    private MaterialEditText phoneField;
    private MaterialEditText emailFieldLogin;

    private FirebaseAuth auth;
    private FirebaseDatabase db;
    private DatabaseReference users;

    private AuthValidator validator;
    private ProgressBar progressBar;

    private FragmentManager fragmentManager;
    private Fragment fragment;
    private View loginView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.register, container,false);
        rootView.findViewById(R.id.registerButton).setOnClickListener(this);

        progressBar = rootView.findViewById(R.id.progressBar);

        fragmentManager = getFragmentManager();
        fragment =  fragmentManager.getFragments().get(0);
        loginView = fragment.getView();

        //FireBase
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        users = db.getReference("Users");

        //Form
        emailField = rootView.findViewById(R.id.emailFieldRegister);
        passwordField = rootView.findViewById(R.id.passwordFieldRegister);
        nameField = rootView.findViewById(R.id.nameFieldRegister);
        phoneField = rootView.findViewById(R.id.phoneFieldRegister);
        emailFieldLogin = loginView.findViewById(R.id.emailField);

        authorizationLayout = rootView.findViewById(R.id.register_layout);
        validator = new AuthValidator();

        return rootView;
    }



    private void createAccount() {
        emailField = getActivity().findViewById(R.id.emailFieldRegister);
        passwordField = getActivity().findViewById(R.id.passwordFieldRegister);
        nameField = getActivity().findViewById(R.id.nameFieldRegister);
        phoneField = getActivity().findViewById(R.id.phoneFieldRegister);
        if(!validateForm(emailField, passwordField, nameField, phoneField)){
            return;
        }

        Log.d(TAG, "createAccount:" + String.valueOf(emailField.getText()));

        progressBar.setVisibility(ProgressBar.VISIBLE);

        auth.createUserWithEmailAndPassword(String.valueOf(emailField.getText()), String.valueOf(passwordField.getText()))
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Log.d(TAG, "createUserWithEmail:success");

                        addToDataBase(String.valueOf(emailField.getText()), String.valueOf(passwordField.getText()), String.valueOf(nameField.getText()), String.valueOf(phoneField.getText()));
                        emailFieldLogin.setText(String.valueOf(emailField.getText()));

                        emailField.setText("");
                        passwordField.setText("");
                        nameField.setText("");
                        phoneField.setText("");

                        Snackbar.make(authorizationLayout, "Регистрация завершена", Snackbar.LENGTH_SHORT).show();
                        progressBar.setVisibility(ProgressBar.INVISIBLE);
                        ((MainActivity)getActivity()).login_fragment();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "createUserWithEmail:failure(Указана неверная/зарегистрированная почта)");

                        emailField.setText("");

                        Snackbar.make(authorizationLayout, "Указана неверная/зарегистрированная почта", Snackbar.LENGTH_SHORT).show();
                        progressBar.setVisibility(ProgressBar.INVISIBLE);
                    }
                });


    }

    private void addToDataBase(String email, String password, String name, String phone){

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setPhone(phone);
        users.child(auth.getCurrentUser().getUid())
                        .setValue(user);
    }

    private boolean validateForm(MaterialEditText emailField, MaterialEditText passwordField, MaterialEditText nameField, MaterialEditText phoneField){
        boolean result = true;
        String email = String.valueOf(emailField.getText());
        String password = String.valueOf(passwordField.getText());
        String name = String.valueOf(nameField.getText());
        String phone = String.valueOf(phoneField.getText());

        if(!validator.emailValid(email)){
            emailField.setError("Неверная почта");
            result = false;
        }
        if(!validator.passwordValid(password)){
            passwordField.setError("Пароль должен содержать более 5 символов");
            result = false;
        }
        if(!validator.nameValid(name)){
            nameField.setError("Заполните имя и фамилию");
            result = false;
        }
        if(!validator.phoneValid(phone)){
            phoneField.setError("Неверный номер");
            result = false;
        }
        return result;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        switch (i){
            case R.id.registerButton:
                createAccount();
                break;
        }
    }
}
