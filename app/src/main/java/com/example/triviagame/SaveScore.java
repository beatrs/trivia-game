package com.example.triviagame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class SaveScore extends AppCompatActivity {
    private EditText txt_player_name;
    private TextView txt_player_score;
    private Button saveConfirm;
    QuizDBHelper scoredb;
    HiScores playerDet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_score);

        scoredb = new QuizDBHelper(this);
        txt_player_name = findViewById(R.id.name);
        txt_player_score = findViewById(R.id.showScore);
        saveConfirm = findViewById(R.id.btn_saveConfirm);

        final Intent saveScore = getIntent();
        final String pScore = saveScore.getStringExtra(MainActivity.EXTRA_SCORE);
        txt_player_score.setText(pScore);

        saveConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backToMenu = new Intent(SaveScore.this, MainMenu.class);

                String player_name = txt_player_name.getText().toString();

                playerDet = new HiScores(player_name, pScore);
                insertData(playerDet);
                startActivity(backToMenu);

            }
        });
    }

    public void insertData(HiScores details) {
        scoredb.open();
        boolean addSuccess = scoredb.addScore(details);

        if (addSuccess) {
            toastMsg("added successfully");
        } else {
            toastMsg("something went wrong");
        }
        scoredb.close();
    }

    private void toastMsg(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
