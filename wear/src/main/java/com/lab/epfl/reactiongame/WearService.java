package com.lab.epfl.reactiongame;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class WearService extends WearableListenerService {

    // Tag for Logcat
    private static final String TAG = "WearService";

    // Actions defined for the onStartCommand(...)
    public enum ACTION_SEND {
        SELECT_OPTION,GAME4_REACT
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        // If no action defined, return
        if (intent.getAction() == null) return Service.START_NOT_STICKY;

        // Match against the given action
        ACTION_SEND action = ACTION_SEND.valueOf(intent.getAction());

        switch (action) {
            case SELECT_OPTION:
                String indexOption = intent.getStringExtra("indexOption");

//                Log.e("WEAR2++", indexOption);
                sendMessage(indexOption, BuildConfig.W_path_message_selectoption);
                break;
            case GAME4_REACT:
                sendMessage("react", BuildConfig.W_path_message_selectoption);
                break;
            default:
                Log.w(TAG, "Unknown action");
                break;
        }

        return Service.START_NOT_STICKY;
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        // A message has been received from the Wear API
        // Get the URI of the event
        String path = messageEvent.getPath();
        String data = new String(messageEvent.getData());
        Log.v(TAG, "Received a message for path " + path
                + " : \"" + data
                + "\", from node " + messageEvent.getSourceNodeId());

        switch (path) {
            case BuildConfig.W_path_message_main:
                switch (data) {
                    case "loading":
                        Intent intentLoading = new Intent(MainActivity
                                .BROADCAST_LOADING);
                        LocalBroadcastManager.getInstance(this).sendBroadcast(intentLoading);
                        break;
                    case "highscores":
                        Intent intentHighscores = new Intent(MainActivity
                                .BROADCAST_HIGHSCORES);
                        LocalBroadcastManager.getInstance(this).sendBroadcast(intentHighscores);
                        break;
                    default:
                        break;
                }
                break;
            case BuildConfig.W_path_message_highscore:
                switch (data) {
                    case "close":
                        Intent intentClose = new Intent(HighscoresActivity
                                .BROADCAST_CLOSE_HIGHSCORE);
                        LocalBroadcastManager.getInstance(this).sendBroadcast(intentClose);
                        break;
                    default:
                        break;
                }
                break;
            case BuildConfig.W_path_message_loading:
                switch (data) {
                    case "choose":
                        Intent intentLoading = new Intent(LoadingActivity
                                .BROADCAST_CHOOSE);
                        LocalBroadcastManager.getInstance(this).sendBroadcast(intentLoading);
                        Intent intentLoading2 = new Intent(MainActivity
                                .BROADCAST_CHOOSEFROMMAIN);
                        LocalBroadcastManager.getInstance(this).sendBroadcast(intentLoading2);
                        break;
                    case "close":
                        Intent intentHighscores = new Intent(LoadingActivity
                                .BROADCAST_CLOSE_LOADING);
                        LocalBroadcastManager.getInstance(this).sendBroadcast(intentHighscores);
                        break;
                    default:
                        break;
                }
                break;
            case BuildConfig.W_path_message_gameresult:
                switch (data) {
                    case "close":
                        Intent intentClose = new Intent(GameResult
                                .BROADCAST_CLOSE_GAMERESULT);
                        LocalBroadcastManager.getInstance(this).sendBroadcast(intentClose);
                        break;
                    default:
                        break;
                }
                break;
            case BuildConfig.W_path_message_gamechoose:
                switch (data) {
                    case "true":
                        Intent intentWinner = new Intent(GameChooseActivity
                                .BROADCAST_GAMERESULT);
                        intentWinner.putExtra("isWinner", true);
                        LocalBroadcastManager.getInstance(this).sendBroadcast(intentWinner);
                        break;
                    case "false":
                        Intent intentLoser = new Intent(GameChooseActivity
                                .BROADCAST_GAMERESULT);
                        intentLoser.putExtra("isWinner", false);
                        LocalBroadcastManager.getInstance(this).sendBroadcast(intentLoser);
                        break;
                    case "close":
                        Intent intentCloseGameChoose = new Intent(GameChooseActivity
                                .BROADCAST_CLOSEGAMECHOOSE);
                        LocalBroadcastManager.getInstance(this).sendBroadcast(intentCloseGameChoose);
                        break;
                    default:
                        break;
                }
                break;
            case BuildConfig.W_path_example_message:
                // The message received is already extracted in the `data` variable
                Intent intent = new Intent(MainActivity
                        .EXAMPLE_BROADCAST_NAME_FOR_NOTIFICATION_MESSAGE_STRING_RECEIVED);
                intent.putExtra(MainActivity
                        .EXAMPLE_INTENT_STRING_NAME_WHEN_BROADCAST, data);
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                break;
            case BuildConfig.W_path_message_game4:
                switch(data){
                    case "startgame4":
                        Intent intentGame4 = new Intent(MainActivity
                                .BROADCAST_GAME4);
                        LocalBroadcastManager.getInstance(this).sendBroadcast(intentGame4);
                        break;
                    case "close":
                        Intent closeGame4 = new Intent(MainActivity
                                .BROADCAST_GAME4_CLOSE);
                        LocalBroadcastManager.getInstance(this).sendBroadcast(closeGame4);
                        break;
                    case "go2result":
                        Intent go2result = new Intent(MainActivity
                                .BROADCAST_GAME4_RESULT);
                        LocalBroadcastManager.getInstance(this).sendBroadcast(go2result);
                        break;
                    default:
                        break;
                }
                break;
            case BuildConfig.W_path_message_game4_changeimage:
                Intent intentGame4changeimage = new Intent(MainActivity
                        .BROADCAST_GAME4_CHANGEIMAGE);
                intentGame4changeimage.putExtra("correctNum",data);
                LocalBroadcastManager.getInstance(this).sendBroadcast(intentGame4changeimage);
                break;
            case BuildConfig.W_path_message_game4_result:
                Intent intentGame4result = new Intent(MainActivity
                        .BROADCAST_GAME4_RESULTSHOW);
                intentGame4result.putExtra("result", data);
                LocalBroadcastManager.getInstance(this).sendBroadcast(intentGame4result);
                break;
            case BuildConfig.W_path_message_game4_resultclose:
                Intent closeGame4result = new Intent(MainActivity
                            .BROADCAST_GAME4_RESULTCLOSE);
                LocalBroadcastManager.getInstance(this).sendBroadcast(closeGame4result);
            break;
            default:
                Log.w(TAG, "Received a message for unknown path " + path + " : " + data);
        }
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.v(TAG, "onDataChanged: " + dataEvents);

        for (DataEvent event : dataEvents) {
            // Get the URI of the event
            Uri uri = event.getDataItem().getUri();

            // Test if data has changed or has been removed
            if (event.getType() == DataEvent.TYPE_CHANGED) {

                // Extract the dataMap from the event
                DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());

                Log.v(TAG, "Received DataItem\n"
                        + "\tDataItem: " + event.getDataItem().toString() + "\n"
                        + "\tPath: " + uri
                        + "\tDatamap: " + dataMapItem.getDataMap());

                Intent intent;

                assert uri.getPath() != null;
                switch (uri.getPath()) {
                    case BuildConfig.W_path_example_datamap:
                        // Extract the data behind the key you know contains data
                        Asset asset = dataMapItem
                                .getDataMap()
                                .getAsset(BuildConfig.W_datamap_example_image);
                        intent = new Intent(MainActivity
                                .EXAMPLE_BROADCAST_NAME_FOR_NOTIFICATION_IMAGE_DATAMAP_RECEIVED);
                        decodeAndBroadcastBitmapFromAsset(asset, intent, MainActivity
                                .EXAMPLE_INTENT_IMAGE_NAME_WHEN_BROADCAST);
                        break;
                    case BuildConfig.W_path_data_gamechoose:
                        // Extract the data behind the key you know contains data
                        ArrayList<String> newData = dataMapItem
                                .getDataMap()
                                .getStringArrayList("data");
                        Intent intentUpdate = new Intent(GameChooseActivity
                                .BROADCAST_UPDATEDATA);
                        for (int i = 0; i<9; i++) {
                            intentUpdate.putExtra("color" + i, newData.get(3 * i));
                            intentUpdate.putExtra("text" + i, newData.get(3 * i + 1));
                            intentUpdate.putExtra("image" + i, newData.get(3 * i + 2));
                        }
                        LocalBroadcastManager.getInstance(WearService.this).sendBroadcast(intentUpdate);
                        break;
                    default:
                        Log.v(TAG, "Data changed for unhandled path: " + uri);
                        break;
                }
            } else if (event.getType() == DataEvent.TYPE_DELETED) {
                Log.w(TAG, "DataItem deleted: " + event.getDataItem().toString());
            }
        }
    }




















    //////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////
    ///////                                                                            ///////
    ///////                      NO NEED TO EDIT BELOW THIS POINT                      ///////
    ///////                                                                            ///////
    ///////        This is mostly glue-code for a convenient Wear communication.       ///////
    ///////                                                                            ///////
    //////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////

    private void sendMessage(String message, String path, final String nodeId) {
        // Sends a message through the Wear API
        Wearable.getMessageClient(this)
                .sendMessage(nodeId, path, message.getBytes())
                .addOnSuccessListener(new OnSuccessListener<Integer>() {
                    @Override
                    public void onSuccess(Integer integer) {
                        Log.v(TAG, "Sent message to " + nodeId + ". Result = " + integer);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Message not sent. " + e.getMessage());
                    }
                });
    }

    private void sendMessage(String message, String path) {
        // Send message to ALL connected nodes
        sendMessageToNodes(message, path);
    }

    void sendMessageToNodes(final String message, final String path) {
        Log.v(TAG, "Sending message " + message);
        // Lists all the nodes (devices) connected to the Wear API
        Wearable.getNodeClient(this).getConnectedNodes().addOnCompleteListener(new OnCompleteListener<List<Node>>() {
            @Override
            public void onComplete(@NonNull Task<List<Node>> listTask) {
                List<Node> nodes = listTask.getResult();
                for (Node node : nodes) {
                    Log.v(TAG, "Try to send message to a specific node");
                    WearService.this.sendMessage(message, path, node.getId());
                }
            }
        });
    }

    void sendPutDataMapRequest(PutDataMapRequest putDataMapRequest) {
        putDataMapRequest.getDataMap().putLong("time", System.nanoTime());
        PutDataRequest request = putDataMapRequest.asPutDataRequest();
        request.setUrgent();
        Wearable.getDataClient(this)
                .putDataItem(request)
                .addOnSuccessListener(new OnSuccessListener<DataItem>() {
                    @Override
                    public void onSuccess(DataItem dataItem) {
                        Log.v(TAG, "Sent datamap.");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Datamap not sent. " + e.getMessage());
                    }
                });
    }

    private void decodeAndBroadcastBitmapFromAsset(Asset asset, final Intent intent, final String extraName) {
        // Reads an asset from the Wear API and parse it as an image
        if (asset == null) {
            throw new IllegalArgumentException("Asset must be non-null");
        }

        // Convert asset and convert it back to an image
        Wearable.getDataClient(this).getFdForAsset(asset)
                .addOnCompleteListener(new OnCompleteListener<DataClient.GetFdForAssetResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<DataClient.GetFdForAssetResponse> runnable) {
                        Log.v(TAG, "Got bitmap from asset");
                        InputStream assetInputStream = runnable.getResult().getInputStream();
                        Bitmap bmp = BitmapFactory.decodeStream(assetInputStream);

                        final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                        bmp.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
                        byte[] bytes = byteStream.toByteArray();
                        intent.putExtra(extraName, bytes);
                        LocalBroadcastManager.getInstance(WearService.this).sendBroadcast(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception runnable) {
                        Log.e(TAG, "Failed to get bitmap from asset");
                    }
                });
    }


    public static Asset createAssetFromBitmap(Bitmap bitmap, int size) {
        bitmap = resizeImage(bitmap, size);

        if (bitmap != null) {
            final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
            return Asset.createFromBytes(byteStream.toByteArray());
        }
        return null;
    }

    private static Bitmap resizeImage(Bitmap bitmap, int newSize) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        // Image smaller, return it as is!
        if (width <= newSize && height <= newSize) return bitmap;

        int newWidth;
        int newHeight;

        if (width > height) {
            newWidth = newSize;
            newHeight = (newSize * height) / width;
        } else if (width < height) {
            newHeight = newSize;
            newWidth = (newSize * width) / height;
        } else {
            newHeight = newSize;
            newWidth = newSize;
        }

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        return Bitmap.createBitmap(bitmap, 0, 0,
                width, height, matrix, true);
    }
}
