package com.lab.epfl.reactiongame;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class SingleScore {
    public String score;
    public String userID;
    public String username;

    public SingleScore() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public SingleScore(String score, String userID, String username) {
        this.score = score;
        this.userID = userID;
        this.username = username;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("score", score);
        result.put("userID", userID);
        result.put("username", username);
        return result;
    }
}
