package com.example.triviagame;

public class HiScores {
    private String player_name;
    private String score;

    public HiScores() {}

    public HiScores(String player_name, String score) {
        this.player_name = player_name;
        this.score = score;
    }

    public String getPlayer_name() {
        return player_name;
    }

    public void setPlayer_name(String player_name) {
        this.player_name = player_name;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }
}
