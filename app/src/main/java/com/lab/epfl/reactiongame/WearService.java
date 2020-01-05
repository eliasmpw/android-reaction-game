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
        EXAMPLE_SEND_STRING, EXAMPLE_SEND_BITMAP, OPEN_LOADING, OPEN_HIGHSCORES,
        CLOSE_HIGHSCORES, OPEN_GAMECHOOSE, CLOSE_LOADING, CLOSE_GAMERESULT,
        CLOSE_GAMECHOOSE, UPDATE_GAMECHOOSE, OPEN_GAMERESULT, OPEN_GAME4, GAME4_CHANGEIMAGE,CLOSE_GAME4,GAME4_RESULT,GAME4_RESULTSHOW,CLOSE_GAME4RESULT
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        // If no action defined, return
        if (intent.getAction() == null) return Service.START_NOT_STICKY;

        // Match against the given action
        ACTION_SEND action = ACTION_SEND.valueOf(intent.getAction());

        switch (action) {
            case EXAMPLE_SEND_STRING:
                // Example action of sending a String received from the MainActivity
                String message_to_send = intent.getStringExtra(MainActivity
                        .EXAMPLE_INTENT_STRING_NAME_ACTIVITY_TO_SERVICE);
                sendMessage(message_to_send, BuildConfig.W_path_example_message);
                break;
            case EXAMPLE_SEND_BITMAP:
                // Example action of sending the app icon, resized to 128 pixels
                Bitmap iconBitmap = BitmapFactory.decodeResource(
                        getApplicationContext().getResources(), R.drawable.esl_logo);
                Asset iconAsset = createAssetFromBitmap(iconBitmap, 128);
                // Create the datamap
                PutDataMapRequest putDataMapRequest = PutDataMapRequest
                        .create(BuildConfig.W_path_example_datamap);
                putDataMapRequest
                        .getDataMap()
                        .putAsset(BuildConfig.W_datamap_example_image, iconAsset);
                sendPutDataMapRequest(putDataMapRequest);
                break;
            case OPEN_LOADING:
                sendMessage("loading", BuildConfig.W_path_message_main);
                break;
            case OPEN_HIGHSCORES:
                sendMessage("highscores", BuildConfig.W_path_message_main);
                break;
            case CLOSE_HIGHSCORES:
                sendMessage("close", BuildConfig.W_path_message_highscore);
                break;
            case OPEN_GAMECHOOSE:
                sendMessage("choose", BuildConfig.W_path_message_loading);
                break;
            case CLOSE_LOADING:
                sendMessage("close", BuildConfig.W_path_message_loading);
                break;
            case CLOSE_GAMERESULT:
                sendMessage("close", BuildConfig.W_path_message_gameresult);
                break;
            case CLOSE_GAME4:
                sendMessage("close", BuildConfig.W_path_message_game4);
            case OPEN_GAMERESULT:
                String isWinner = intent.getStringExtra("isWinner");
                sendMessage(isWinner, BuildConfig.W_path_message_gamechoose);
                break;
            case CLOSE_GAMECHOOSE:
                sendMessage("close", BuildConfig.W_path_message_gamechoose);
                break;
            case UPDATE_GAMECHOOSE:
                ArrayList<String> newData = new ArrayList<>();
                // Create the datamap
                for (int i = 0; i<9; i++) {
                    newData.add(intent.getStringExtra("color" + i));
                    newData.add(intent.getStringExtra("text" + i));
                    newData.add(intent.getStringExtra("image" + i));
                }
//                Log.e("TEST++", newData.toString());
                PutDataMapRequest auxDataMapRequest = PutDataMapRequest
                        .create(BuildConfig.W_path_data_gamechoose);
                auxDataMapRequest
                        .getDataMap().putStringArrayList("data", newData);
                sendPutDataMapRequest(auxDataMapRequest);
                break;
            case OPEN_GAME4:
                sendMessage("startgame4",BuildConfig.W_path_message_game4);
                break;
            case GAME4_CHANGEIMAGE:
                String correctNum = intent.getStringExtra("correctNum");
                sendMessage(correctNum,BuildConfig.W_path_message_game4_changeimage);
                break;
            case GAME4_RESULT:
                sendMessage("go2result",BuildConfig.W_path_message_game4);
                break;
            case GAME4_RESULTSHOW:
                String result = intent.getStringExtra("result");
                sendMessage(result,BuildConfig.W_path_message_game4_result);
                break;
            case CLOSE_GAME4RESULT:
                sendMessage("close",BuildConfig.W_path_message_game4_resultclose);
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
            case BuildConfig.W_path_message_selectoption:
//                Log.e("WEAR1++", data);
                Intent intentSelect = new Intent(GameChooseActivity
                        .BROADCAST_SELECTOPTION);
                intentSelect.putExtra("indexOption", data);
                LocalBroadcastManager.getInstance(this).sendBroadcast(intentSelect);
            case BuildConfig.W_path_message_game4:
                Intent intentReact = new Intent(GameFourActivity
                        .BROADCAST_GAME4_REACT);
                LocalBroadcastManager.getInstance(this).sendBroadcast(intentReact);
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
