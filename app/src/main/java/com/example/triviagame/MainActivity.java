package com.example.triviagame;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    TextView question, errorCheck, score;
    private Button choice1, choice2, choice3, choice4;
    private Button btn_nextQn;
    private Button btn_lifeline_switch, btn_lifeline_askHost, btn_lifeline_fifty50;

    private List<Questions> questionsList;
    private Questions currentQn;
    private int qnCounter;
    private boolean checked, isCorrect;
    private String btn_selected;

    private static final long COUNTDOWN_IN_MS = 30000;
    private CountDownTimer countDownTimer;
    private long timeLeft;
    TextView timer;

    String[] moneyTree = new String[12];
    private int moneyTreeCount;

    public static final String EXTRA_NAME = "com.example.quiz.quiz.EXTRA_NAME";
    public static final String EXTRA_SCORE = "com.example.quiz.quiz.EXTRA_SCORE";

    private boolean gameOver;
    private String result;
    AlertDialog alert;
    Dialog moneyPopup;

    /**
     *
     * @Override methods
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        question = findViewById(R.id.txt_question);
        choice1 = findViewById(R.id.btn_choice1);
        choice1.setOnClickListener(this);
        choice2 = findViewById(R.id.btn_choice2);
        choice2.setOnClickListener(this);
        choice3 = findViewById(R.id.btn_choice3);
        choice3.setOnClickListener(this);
        choice4 = findViewById(R.id.btn_choice4);
        choice4.setOnClickListener(this);
        btn_nextQn = findViewById(R.id.btn_nextQn);
        score = findViewById(R.id.score);
        timer = findViewById(R.id.timer);
        moneyTreeCount = 0;
        qnCounter = 0;
        //errorCheck = findViewById(R.id.debug);
        btn_selected = "";

        btn_lifeline_switch = findViewById(R.id.btn_lifeline_switch);
        btn_lifeline_askHost = findViewById(R.id.btn_lifeline_askHost);
        btn_lifeline_fifty50 = findViewById(R.id.btn_lifeline_fifty50);

        DbAccess dbAccess = DbAccess.getInstance(this);
        dbAccess.open();
        questionsList = dbAccess.getAllQuestions();
        dbAccess.close();

        Collections.shuffle(questionsList);
        checked = false;
        isCorrect = false;
        showNextQn();

        gameOver = false;
        result = "Game Over";

        moneyPopup = new Dialog(this);

        moneyTree = new String[]{getString(R.string.startMoney),
                getString(R.string.round1Prize), getString(R.string.round2Prize),
                getString(R.string.round3Prize),getString(R.string.round4Prize),
                getString(R.string.round5Prize),getString(R.string.round6Prize),
                getString(R.string.round7Prize), getString(R.string.round8Prize),
                getString(R.string.round9Prize),getString(R.string.round10Prize),
                getString(R.string.round11Prize),getString(R.string.round12Prize)};

        btn_nextQn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checked) {
                    checkAnswer();
                } else {
                    showButtons();
                    if (isCorrect) {
                        moneyTreeCount++;
                        showMoneyPopup();
                        //showNextQn();
                        checked = false;
                        isCorrect = false;
                        clearSelection();
                    }

                }

            }
        });

        /**
        Button btn_show = (Button) findViewById(R.id.btn_showPopup);

        btn_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
         **/

        btn_lifeline_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNextQn();
                btn_lifeline_switch.setVisibility(View.INVISIBLE);
            }
        });

        btn_lifeline_askHost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Host says: " +
                        currentQn.getAnswer(), Toast.LENGTH_SHORT).show();
                btn_lifeline_askHost.setVisibility(View.INVISIBLE);

            }
        });

        btn_lifeline_fifty50.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button choices[] = {choice1, choice2, choice3, choice4};
                int removedButtons = 0;
                for (int i = 0; i < 4; i++) {
                    String currentBtn = choices[i].getText().toString();
                    if (!currentBtn.equals(currentQn.getAnswer())) {
                        choices[i].setVisibility(View.INVISIBLE);
                        removedButtons++;
                    }
                    if (removedButtons == 2) {
                        break;
                    }
                }
                btn_lifeline_fifty50.setVisibility(View.INVISIBLE);
            }
        });

        AlertDialog.Builder saveScoreDialog = new AlertDialog.Builder(this);
        saveScoreDialog.setTitle(result);
        saveScoreDialog.setMessage(R.string.saveScoreConfirm);

        saveScoreDialog.setPositiveButton(R.string.ansYes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent saveScore = new Intent(MainActivity.this, SaveScore.class);
                saveScore.putExtra(EXTRA_SCORE, moneyTree[moneyTreeCount]);
                startActivity(saveScore);
            }
        });

        saveScoreDialog.setNegativeButton(R.string.ansNo, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                /**
                Intent backToMenu = new Intent(MainActivity.this, MainMenu.class);
                startActivity(backToMenu);**/
                MainActivity.super.finish();
            }
        });

        alert = saveScoreDialog.create();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        Drawable btnClicked = getResources().getDrawable(R.drawable.button_clicked);
        switch (v.getId()) {
            case R.id.btn_choice1:
                choice1.setBackground(btnClicked);
                clearSelection(choice1);
                //errorCheck.setText("choice 1");
                btn_selected = choice1.getText().toString();
                break;
            case R.id.btn_choice2:
                choice2.setBackground(btnClicked);
                clearSelection(choice2);
                //errorCheck.setText("choice 2");
                btn_selected = choice2.getText().toString();
                break;
            case R.id.btn_choice3:
                choice3.setBackground(btnClicked);
                clearSelection(choice3);
                //errorCheck.setText("choice 3");
                btn_selected = choice3.getText().toString();
                break;
            case R.id.btn_choice4:
                choice4.setBackground(btnClicked);
                clearSelection(choice4);
                //errorCheck.setText("choice 4");
                btn_selected = choice4.getText().toString();
                break;
            default:
                break;
        }
    }

    private void showButtons() {
        Button choices[] = {choice1, choice2, choice3, choice4};

        for (int i = 0; i < 4; i++) {
            choices[i].setVisibility(View.VISIBLE);
        }
    }

    private void clearSelection(Button btnClicked) {
        Drawable btnUnclicked = getResources().getDrawable(R.drawable.button_unclicked);
        Button choices[] = {choice1, choice2, choice3, choice4};

        for (int i = 0; i < 4; i++) {
            if (!btnClicked.equals(choices[i])) {
                choices[i].setBackground(btnUnclicked);
            }
        }
    }

    private void clearSelection() {
        Drawable btnUnclicked = getResources().getDrawable(R.drawable.button_unclicked);
        Button choices[] = {choice1, choice2, choice3, choice4};

        for (int i = 0; i < 4; i++) choices[i].setBackground(btnUnclicked);

    }

    private void showNextQn() {
        score.setText(moneyTree[moneyTreeCount]);
        currentQn = questionsList.get(qnCounter);
        question.setText(currentQn.getQuestion());
        choice1.setText(currentQn.getChoice1());
        choice2.setText(currentQn.getChoice2());
        choice3.setText(currentQn.getChoice3());
        choice4.setText(currentQn.getChoice4());

        qnCounter++;
        btn_nextQn.setText("Final Answer");
        timeLeft = COUNTDOWN_IN_MS;
        startCountdown();
    }

    private void startCountdown() {
        countDownTimer = new CountDownTimer(timeLeft, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeft = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                timeLeft = 0;
                updateCountDownText();
                checkAnswer();
            }
        }.start();
    }

    private void updateCountDownText() {
        int seconds = (int) (timeLeft/1000);

        timer.setText(String.valueOf(seconds));

    }

    private void checkAnswer() {
        checked = true;

        countDownTimer.cancel();

        if (btn_selected.equalsIgnoreCase(currentQn.getAnswer())) {
            //errorCheck.setText("Correct Answer: " + currentQn.getAnswer());
            btn_nextQn.setText("Next Question");
            isCorrect = true;
            if (moneyTreeCount == 12) {
                gameOver = true;
                result = getString(R.string.winMsg);
            }
        } else {
            //errorCheck.setText("Wrong Answer; Correct Ans: " + currentQn.getAnswer());
            isCorrect = false;
            gameOver = true;
            result = getString(R.string.loseMsg);
        }

        checkGameState();
    }

    private void checkGameState() {
        if (gameOver) {
            alert.show();
        }

    }


    public void showMoneyPopup() {
        moneyPopup.setContentView(R.layout.money_tree_popup);
        Button btn_close_popup;

        int bgColor = getResources().getColor(R.color.yellow);
        TextView roundDet[] = new TextView[12];
        btn_close_popup = moneyPopup.findViewById(R.id.btn_close_popup);
        int i;
        for (i = 1; i < 12; i++) {
            String txt_lbl = "round" + i + "Money";
            int rId = getResources().getIdentifier(txt_lbl, "id", getPackageName());
            roundDet[i] = moneyPopup.findViewById(rId);
        }

        roundDet[moneyTreeCount].setBackgroundColor(bgColor);


        btn_close_popup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moneyPopup.dismiss();
                showNextQn();
            }
        });

        moneyPopup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        moneyPopup.show();
    }


    private void toastMsg(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


}
