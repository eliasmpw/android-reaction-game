package com.lab.epfl.reactiongame;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class GameHighscores {
    public SingleScore user1;
    public SingleScore user2;
    public SingleScore user3;
    public SingleScore user4;
    public SingleScore user5;

    public GameHighscores() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public GameHighscores(SingleScore user1, SingleScore user2, SingleScore user3, SingleScore user4, SingleScore user5) {
        this.user1 = user1;
        this.user2 = user2;
        this.user3 = user3;
        this.user4 = user4;
        this.user5 = user5;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("user1", user1);
        result.put("user2", user2);
        result.put("user3", user3);
        result.put("user4", user4);
        result.put("user5", user5);
        return result;
    }
}
