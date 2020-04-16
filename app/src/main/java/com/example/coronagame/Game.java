package com.example.coronagame;

public class Game {

    int Lives;
    int Hits;
    int points;


    Game(){
        this.Lives = 3;
        this.Hits = 0;
        this.points = 0;
    }

    public int getLives() {
        return Lives;
    }

    public void setLives(int lives) {
        Lives = lives;
    }

    public int getHits() {
        return Hits;
    }

    public void setHits(int hits) {
        Hits = hits;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
