package com.company.votes.entity;

import java.util.ArrayList;
import java.util.List;

public class Vote {
    private String id;
    private String ownerEmail;
    private String question;
    private List<Answer> answers;
    private List<String> answeredUID;
    private int count;

    {
        id = "";
        ownerEmail = "";
        question = "";
        answers = new ArrayList<>(2);
        answeredUID = new ArrayList<>();
    }

    public Vote() {
    }

    public Vote(String question) {
        this.question = question;
    }

    public Vote(String question, List<Answer> answers) {
        this.question = question;
        this.answers = answers;
    }

    public Vote(String ownerId, String question, List<Answer> answers) {
        this.ownerEmail = ownerId;
        this.question = question;
        this.answers = answers;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public List<String> getAnsweredUID() {
        return answeredUID;
    }

    public void setAnsweredUID(List<String> answeredUID) {
        this.answeredUID = answeredUID;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Vote{");
        sb.append("id='").append(id).append('\'');
        sb.append(", ownerEmail='").append(ownerEmail).append('\'');
        sb.append(", question='").append(question).append('\'');
        sb.append(", answers=").append(answers);
        sb.append(", answeredUID=").append(answeredUID);
        sb.append(", count=").append(count);
        sb.append('}');
        return sb.toString();
    }
}
