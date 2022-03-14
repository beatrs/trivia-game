package com.example.triviagame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainMenu extends AppCompatActivity {
    private Button btn_start, btn_scores;
    QuizDBHelper scoredb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        scoredb = new QuizDBHelper(this);

        btn_start = findViewById(R.id.btn_start);
        btn_scores = findViewById(R.id.btn_scores);

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gameProper = new Intent(MainMenu.this, MainActivity.class);
                startActivity(gameProper);
            }
        });

        btn_scores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent hi_scores = new Intent(MainMenu.this, Leaderboard.class);
                startActivity(hi_scores);
            }
        });
    }
}
