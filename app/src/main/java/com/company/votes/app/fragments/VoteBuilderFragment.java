package com.company.votes.app.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.company.votes.R;
import com.company.votes.entity.Answer;
import com.company.votes.entity.Vote;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class VoteBuilderFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "VOTE_BUILDER";

    private TextInputEditText question;
    private TextInputEditText answer1;
    private ProgressBar progressBar;

    private FirebaseAuth auth;
    private FirebaseDatabase db;
    private DatabaseReference ref;
    private String ownerEmail;

    private Vote vote;
    private List<View> allAnswers;
    private LinearLayout answerContainer;
    private ScrollView scrollView;
    private Runnable scroll;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        setRetainInstance(true);
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        ref = db.getReference("Votes");
        ownerEmail = auth.getCurrentUser().getEmail();

        scroll = new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        };
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_votebuilder, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //Кнопки
        getView().findViewById(R.id.addAnswer).setOnClickListener(this);
        getView().findViewById(R.id.addVote).setOnClickListener(this);
        scrollView = ((ScrollView) getView().findViewById(R.id.scrollBuilder));

        //View
        answerContainer = getView().findViewById(R.id.newAnswers);
        question = getView().findViewById(R.id.question);
        answer1 = getView().findViewById(R.id.answerText);
        progressBar = getView().findViewById(R.id.progressBarBuilder);

        //Массив Ответов
        allAnswers = new ArrayList<>();
        allAnswers.add(answer1);

        //сразу добавляю второй вопрос
        final View answer = getLayoutInflater().inflate(R.layout.custom_edit_text_layout, null);
        answer.findViewById(R.id.delete_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    allAnswers.remove(answer);
                    ((LinearLayout) answer.getParent()).removeView(answer);
                } catch(IndexOutOfBoundsException ex) {
                    ex.printStackTrace();
                }
            }
        });
        allAnswers.add(answer);
        answerContainer.addView(answer);

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        switch (i){
            case R.id.addAnswer:
                if(allAnswers.size() <= 10) {
                    final View answer = getLayoutInflater().inflate(R.layout.custom_edit_text_layout, null);
                    final TextInputEditText text = answer.findViewById(R.id.answerText);
                    answer.findViewById(R.id.delete_button).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                // при удалении фокус на предыдущий вопрос
//                                int k = allAnswers.indexOf(answer) - 1;
//                                final TextInputEditText textFocus = allAnswers.get(k).findViewById(R.id.answerText);
//                                textFocus.post(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        textFocus.requestFocus();
//                                    }
//                                });
                                allAnswers.remove(answer);
                                ((LinearLayout) answer.getParent()).removeView(answer);
                            } catch (IndexOutOfBoundsException ex) {
                                ex.printStackTrace();
                            }
                        }
                    });
                    allAnswers.add(answer);
                    answerContainer.addView(answer);
                    scrollView.post(scroll);
                    text.post(new Runnable() {
                        @Override
                        public void run() {
                            text.requestFocus();
                        }
                    });
                }else{
                    Snackbar.make(getView().findViewById(R.id.voteBuilderLayout),
                            "Достигнуто максимальное количество ответов",
                            Snackbar.LENGTH_SHORT).show();
                }
                break;
            case R.id.addVote:
                if(voteValidator()) {
                    progressBar.setVisibility(View.VISIBLE);
                    String title = question.getText().toString().trim();
                    String tmpanswer;
                    //Массив ответов в формате Answer
                    List<Answer> answers = new ArrayList<>();
                    for (int j = 0; j < allAnswers.size(); j++) {
                        tmpanswer = ((TextInputEditText) allAnswers.get(j).findViewById(R.id.answerText))
                                .getText().toString().trim();
                        answers.add(new Answer(tmpanswer));
                    }

                    //Создаю объект Vote
                    String id = ref.push().getKey();
                    vote = new Vote(title, answers);
                    vote.setOwnerEmail(ownerEmail);
                    vote.setId(id);
                    Log.d(TAG, vote.toString());

                    //Запись в бд
                    ref.child(id).setValue(vote).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "Добавлено в базу");
                            progressBar.setVisibility(View.GONE);

                            //Очистка полей
                            question.setText("");
                            answer1.setText("");
                            answerContainer.removeAllViews();
                            allAnswers.clear();

                            //Возврат статического варианта ответа
                            allAnswers.add(answer1);

                            //Снятие автоматических фокусов
                            question.clearFocus();
                            answer1.clearFocus();

                            Snackbar.make(getView().findViewById(R.id.voteBuilderLayout),
                                    "Готово",
                                    Snackbar.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "Не добавлено в базу");
                            progressBar.setVisibility(View.GONE);
                            Snackbar.make(getView().findViewById(R.id.voteBuilderLayout),
                                    "Ошибка подключения к базе данных",
                                    Snackbar.LENGTH_SHORT).show();
                        }
                    });



                    //Закрытие клавиатуры
                    ((InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE)).
                            hideSoftInputFromWindow(v.getWindowToken(), 0);
                }else{
                    Snackbar.make(getView().findViewById(R.id.voteBuilderLayout),
                            "Голосование не заполнено",
                            Snackbar.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private boolean voteValidator(){
        for(int i =0; i < allAnswers.size(); i++) {
            if(((TextInputEditText) allAnswers.get(i).findViewById(R.id.answerText))
                    .getText().toString().trim().length() == 0){
                return false;
            }
        }

        if(question.getText().toString().trim().length() == 0){
            return false;
        }

        return true;
    }
}
