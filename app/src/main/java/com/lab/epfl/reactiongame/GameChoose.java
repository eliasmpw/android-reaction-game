package com.lab.epfl.reactiongame;

import android.nfc.Tag;
import android.util.Log;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class GameChoose {
    public String username1;
    public String username2;
    public int points1;
    public int points2;
    public long time1;
    public long time2;
    public String question;
    public long questionCreatedOn;
    public long questionTimePlayer1;
    public long questionTimePlayer2;
    public Map<String, GameOption> answers = new HashMap<>();
    public int correctAnswer;
    public int lastWinner;

    public GameChoose() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public GameChoose(String username1, String username2, int points1, int points2, long time1, long time2, String question, long questionCreatedOn, long questionTimePlayer1, long questionTimePlayer2, Map<String, GameOption> answers, int correctAnswer, int lastWinner) {
        this.username1 = username1;
        this.username2 = username2;
        this.points1 = points1;
        this.points2 = points2;
        this.time1 = time1;
        this.time2 = time2;
        this.question = question;
        this.questionCreatedOn = questionCreatedOn;
        this.questionTimePlayer1 = questionTimePlayer1;
        this.questionTimePlayer2 = questionTimePlayer2;
        this.answers = answers;
        this.correctAnswer = correctAnswer;
        this.lastWinner = lastWinner;
    }


    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("username1", username1);
        result.put("username2", username2);
        result.put("points1", points1);
        result.put("time1", time1);
        result.put("time2", time2);
        result.put("question", question);
        result.put("questionCreatedOn", questionCreatedOn);
        result.put("questionTimePlayer1", questionTimePlayer1);
        result.put("questionTimePlayer2", questionTimePlayer2);
        result.put("answers", answers);
        result.put("correctAnswer", correctAnswer);
        result.put("lastWinner", lastWinner);

        return result;
    }
}
