package com.example.triviagame;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Leaderboard extends AppCompatActivity {
    TextView top_scorers_list;
    QuizDBHelper scoredb;
    private HiScores playerDet;
    List<HiScores> topScoresList;
    String txt_scores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        topScoresList = new ArrayList<>();
        scoredb = new QuizDBHelper(this);
        txt_scores = "";

        playerDet = new HiScores();

        top_scorers_list = findViewById(R.id.scorer_list);
        showTopScores();

    }

    public void showTopScores() {

        /**
        int counter = 0;
        if (topScoresList.size() >= 5) counter = 5;
        else counter = topScoresList.size();
         **/

        topScoresList = scoredb.getTopScores();

        HiScores showDet = new HiScores();

        for (int i = 0; i < topScoresList.size(); i++) {
            showDet = topScoresList.get(i);
            txt_scores += showDet.getPlayer_name() + "\t\t" + showDet.getScore() + "\n";
        }
        top_scorers_list.setText(txt_scores);
    }

    public void getAllScores() {

        /**
        Cursor c = scoredb.getScores();

        if (c.moveToFirst()) {
            do {
                playerDet.setPlayer_name(c.getString(c.getColumnIndex(ScoresDB.HiScoresDB.COLUMN_PLAYER_NAME)));
                playerDet.setScore(c.getString(c.getColumnIndex(ScoresDB.HiScoresDB.COLUMN_MONEY_WON)));

                topScoresList.add(playerDet);
            } while (c.moveToNext());
        } else {
            toastMsg("no record in database");
        }**/

    }

    private void toastMsg(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
