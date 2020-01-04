package com.lab.epfl.reactiongame;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ScoreGame2Fragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ScoreGame2Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScoreGame2Fragment extends Fragment {
    private final String TAG = this.getClass().getSimpleName();
    private final List<String> nameList2 = new ArrayList<String>();
    private final List<String> scoresList2 = new ArrayList<String>();
    private static final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static final DatabaseReference highScoresGetRef = database.getReference("highscores");
    private static DatabaseReference highScoresRef = highScoresGetRef.push();
    private View fragmentView;











    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ScoreGame1Fragment.OnFragmentInteractionListener mListener;

    public ScoreGame2Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ScoreGame1Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ScoreGame1Fragment newInstance(String param1, String param2) {
        ScoreGame1Fragment fragment = new ScoreGame1Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.fragment_score_game2, container, false);
        getScores();
//        TextView third = fragmentView.findViewById(R.id.ThirdPlace);
//        third.setText(nameList4.get(2));
//        third.setText("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
//        Log.i(TAG, "THIS IS THE aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");

//        return inflater.inflate(R.layout.fragment_score_game1, container, false);

        return fragmentView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ScoreGame1Fragment.OnFragmentInteractionListener) {
            mListener = (ScoreGame1Fragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    private void getScores() {


        DatabaseReference rootref = FirebaseDatabase.getInstance().getReference();
        DatabaseReference highScoresRef = rootref.child("highscores");
        DatabaseReference namesRef = highScoresRef.child("Game2");

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()){
                    String tmpName = userSnapshot.child("username").getValue(String.class);
                    nameList2.add(tmpName);
                    String tmpScore = userSnapshot.child("score").getValue(String.class);
                    scoresList2.add(tmpScore);
                }
                Log.i(TAG, "THIS IS THE TAGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG" + nameList2.toString());
                Log.i(TAG, "THIS IS THE TAGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG" + scoresList2.toString());

//                TextView title = fragmentView.findViewById(R.id.textViewGameID);
//                title.setText("Game 2");
                TextView first = fragmentView.findViewById(R.id.firstPlace);
                first.setText(nameList2.get(0));
                TextView firstScore = fragmentView.findViewById(R.id.firstPlaceScore);
                firstScore.setText(formatTimeMilliseconds(Long.parseLong(scoresList2.get(0))));

                TextView second = fragmentView.findViewById(R.id.SecondPlace);
                second.setText(nameList2.get(1));
                TextView secondScore = fragmentView.findViewById(R.id.SecondPlaceScore);
                secondScore.setText(formatTimeMilliseconds(Long.parseLong(scoresList2.get(1))));

                TextView third = fragmentView.findViewById(R.id.ThirdPlace);
                third.setText(nameList2.get(2));
                TextView thirdScore = fragmentView.findViewById(R.id.ThirdPlaceScore);
                thirdScore.setText(formatTimeMilliseconds(Long.parseLong(scoresList2.get(2))));

                TextView forth = fragmentView.findViewById(R.id.forthPlace);
                forth.setText(nameList2.get(3));
                TextView forthScore = fragmentView.findViewById(R.id.forthPlaceScore);
                forthScore.setText(formatTimeMilliseconds(Long.parseLong(scoresList2.get(3))));

                TextView fifth = fragmentView.findViewById(R.id.fifthPlace);
                fifth.setText(nameList2.get(4));
                TextView fifthScore = fragmentView.findViewById(R.id.fifthPlaceScore);
                fifthScore.setText(formatTimeMilliseconds(Long.parseLong(scoresList2.get(4))));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        namesRef.addListenerForSingleValueEvent(valueEventListener);

    }


    public String formatTimeMilliseconds(long time) {
        DecimalFormat df = new DecimalFormat("0");
        DecimalFormat two = new DecimalFormat("00");
        DecimalFormat mf = new DecimalFormat("000");

        int hours = (int)(time / (3600 * 1000));
        int remaining = (int)(time % (3600 * 1000));

        int minutes = (int)(remaining / (60 * 1000));
        remaining = (int)(remaining % (60 * 1000));

        int seconds = (int)(remaining / 1000);
        remaining = (int)(remaining % (1000));

        int milliseconds = (int)((int)time % 1000);

        String text = "";

        if (hours > 0) {
            text += df.format(hours) + ":";
        }
        if (minutes > 0) {
            text += df.format(minutes) + ":";
            text += two.format(seconds) + ".";
        } else {
            text += df.format(seconds) + ".";
        }
        text += mf.format(milliseconds);
        return text;
    }
}

