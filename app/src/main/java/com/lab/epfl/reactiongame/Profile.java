package com.lab.epfl.reactiongame;

import java.io.Serializable;

class Profile implements Serializable {

    String username;
    String password;
    String photoPath;

    Profile(String username, String password) {
        // When you create a new Profile, it's good to build it based on username and password
        this.username = username;
        this.password = password;
    }

//    DataMap toDataMap() {
//        DataMap dataMap = new DataMap();
//        dataMap.putString("username", username);
//        dataMap.putString("password", password);
//        final InputStream imageStream;
//        try {
//            imageStream = new FileInputStream(photoPath);
//            final Bitmap userImage = BitmapFactory.decodeStream(imageStream);
//            Asset asset = WearService.createAssetFromBitmap(userImage);
//            dataMap.putAsset("photo", asset);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        return dataMap;
//    }
}