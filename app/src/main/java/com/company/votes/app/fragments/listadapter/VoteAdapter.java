package com.company.votes.app.fragments.listadapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.company.votes.R;
import com.company.votes.entity.Vote;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class VoteAdapter extends BaseAdapter {
    private final String TAG = "ADAPTER";
    private Context ctx;
    private LayoutInflater lInflater;
    private ArrayList<Vote> objects;
    private Vote vote;

    private FirebaseAuth auth;
    private FirebaseDatabase db;
    private DatabaseReference ref;

    public VoteAdapter(Context context, ArrayList<Vote> votes) {
        ctx = context;
        objects = votes;
        lInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        db = FirebaseDatabase.getInstance();
        ref = db.getReference("Votes");
        auth = FirebaseAuth.getInstance();
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.vote_list_item, parent, false);
        }

        vote = getVote(position);

        ((TextView) view.findViewById(R.id.itemOwner)).setText(vote.getOwnerEmail());
        final TextView question = view.findViewById(R.id.itemQuestion);
        question.setText(vote.getQuestion());
        final Button deleteVoteButton = view.findViewById(R.id.delete_vote_button);
        if(auth.getCurrentUser().getEmail().equals(vote.getOwnerEmail())){
            deleteVoteButton.setVisibility(View.VISIBLE);
            deleteVoteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ref.child(getVote(position).getId()).removeValue();
                }
            });
        }
        final LinearLayout answersList = view.findViewById(R.id.answers_list);
        if(answersList.getChildCount() != 0){
            answersList.removeAllViews();
        }
        for (int i = 0; i < vote.getAnswers().size(); i++) {
            final View answer = lInflater.inflate(R.layout.answers_list_item, null);
            final TextView text = answer.findViewById(R.id.answerItem);
            final TextView count = answer.findViewById(R.id.count);
            final TextView percent= answer.findViewById(R.id.percent);

            answer.setTag(i);
            if(vote.getAnsweredUID().contains(auth.getCurrentUser().getEmail())){
                text.setText(vote.getAnswers().get(i).getAnswer());
                count.setText("" + vote.getAnswers().get(i).getCount());
                percent.setText(vote.getAnswers().get(i).getCount() * 100 / (vote.getCount()==0?1:vote.getCount()) + "%");
                answer.setClickable(false);
            }
            else {
                text.setText(vote.getAnswers().get(i).getAnswer());
                answer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e(TAG, "" + v.getTag());
                        Vote current = getVote(position);
                        current.getAnsweredUID().add(auth.getCurrentUser().getEmail());
                        int j = Integer.valueOf(v.getTag() + "");
                        ref.child(current.getId()).child("answeredUID").setValue(current.getAnsweredUID());
                        ref.child(current.getId()).child("count").setValue(current.getCount() + 1);
                        ref.child(current.getId()).child("answers").child("" + j).child("count")
                                .setValue(current.getAnswers().get(j).getCount() + 1);
                        Log.d(TAG, "" + current.getAnswers().get(j).getCount());
                    }
                });

            }
            answersList.addView(answer);
        }

        return view;
    }
//@Override
//public View getView(final int position, View convertView, ViewGroup parent) {
//    View view = convertView;
//    if (view == null) {
//        view = lInflater.inflate(R.layout.vote_list_item, parent, false);
//    }
//
//    vote = getVote(position);
//    final TextView itemOwner = view.findViewById(R.id.itemOwner);
//    final TextView question = view.findViewById(R.id.itemQuestion);
//    final Button deleteVoteButton = view.findViewById(R.id.delete_vote_button);
//
//    itemOwner.setText(vote.getOwnerEmail());
//    question.setText(vote.getQuestion());
//
//    if(auth.getCurrentUser().getEmail().equals(vote.getOwnerEmail())){
//        deleteVoteButton.setVisibility(View.VISIBLE);
//        deleteVoteButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ref.child(getVote(position).getId()).removeValue();
//            }
//        });
//    }
//
//    final LinearLayout answersList = view.findViewById(R.id.answers_list);
//    if(answersList.getChildCount() != 0){
//        answersList.removeAllViews();
//    }
//    final List<Answer> answers = new ArrayList<>();
//    ref.child(getVote(position).getId()).child("answers").addValueEventListener(new ValueEventListener() {
//        @Override
//        public void onDataChange(DataSnapshot dataSnapshot) {
//            for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
//                answers.add( singleSnapshot.getValue(Answer.class));
//            }
//        }
//
//        @Override
//        public void onCancelled(DatabaseError databaseError) {
//            Log.e(TAG, "The read failed: " + databaseError.getCode());
//        }
//    });
//    for (int i = 0; i < vote.getAnswers().size(); i++) {
//        final View answer = lInflater.inflate(R.layout.answers_list_item, null);
//        final TextView text = answer.findViewById(R.id.answerItem);
//        final TextView count = answer.findViewById(R.id.count);
//        final TextView percent= answer.findViewById(R.id.percent);
//
//        answer.setTag(i);
//        if(vote.getAnsweredUID().contains(auth.getCurrentUser().getEmail())){
//            text.setText(vote.getAnswers().get(i).getAnswer());
//            count.setText("" + vote.getAnswers().get(i).getCount());
//            percent.setText(vote.getAnswers().get(i).getCount() * 100 / (vote.getCount()==0?1:vote.getCount()) + "%");
//            answer.setClickable(false);
//        }
//        else {
//            text.setText(vote.getAnswers().get(i).getAnswer());
//            answer.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Log.e(TAG, "" + v.getTag());
//                    Vote current = getVote(position);
//                    current.getAnsweredUID().add(auth.getCurrentUser().getEmail());
//                    int j = Integer.valueOf(v.getTag() + "");
//                    ref.child(current.getId()).child("answeredUID").setValue(current.getAnsweredUID());
//                    ref.child(current.getId()).child("count").setValue(current.getCount() + 1);
//                    ref.child(current.getId()).child("answers").child("" + j).child("count")
//                            .setValue(current.getAnswers().get(j).getCount() + 1);
//                    Log.d(TAG, "" + current.getAnswers().get(j).getCount());
//                }
//            });
//
//        }
//        answersList.addView(answer);
//    }
//
//    return view;
//}

    Vote getVote(int position) {
        return ((Vote) getItem(position));
    }

}