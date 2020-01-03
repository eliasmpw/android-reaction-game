package com.lab.epfl.reactiongame;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class GameOption {
    public String color;
    public String text;
    public String image;

    public GameOption() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public GameOption(String color, String text, String image) {
        this.color = color;
        this.text = text;
        this.image = image;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("color", color);
        result.put("text", text);
        result.put("image", image);
        return result;
    }
}
