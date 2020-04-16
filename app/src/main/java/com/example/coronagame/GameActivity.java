package com.example.coronagame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Random;

import pl.droidsonroids.gif.GifImageView;

public class GameActivity extends AppCompatActivity {

    RelativeLayout mainLayout;
    private Handler mainHandler = new Handler();
    TextView txtPoints;
    final Animation[] CloudAnimation = new Animation[4];
    final Animation[] BatAnimation = new Animation[4];
    final Animation[] DropsAnimation = new Animation[4];

    public enum Direction {
        RIGHT(1),
        LEFT(2),
        MIDDLE(3);

        private final int DirectionNm;

        Direction(int directionNm) {
            this.DirectionNm = directionNm;
        }
    }

    Direction D;
    BatmanThread batman = new BatmanThread();
    GifImageView Batman;
    ImageView imgTree1;
    GifImageView gifTree1;
    Animation animCorona;

    int Score = 0;
    View[] coronaHits = new View[5];
    ImageView[] clouds = new ImageView[4];
    GifImageView[] bats = new GifImageView[4];
    ImageView[] IV = new ImageView[4];

    Game Game = new Game();
    int Lives;
    float batX;
    Boolean isGameOver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        this.Lives = 3;
        txtPoints = findViewById(R.id.txtPoints);
        mainLayout = findViewById(R.id.mainLayout);
        Batman = findViewById(R.id.batman);

        BatAnimation[0] = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bat_right1);
        BatAnimation[1] = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bat_right2);
        BatAnimation[2] = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bat_left1);
        BatAnimation[3] = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bat_left2);

        CloudAnimation[0] = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.cloud_anim1);
        CloudAnimation[1] = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.cloud_anim2);
        CloudAnimation[2] = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.cloud_anim3);
        CloudAnimation[3] = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.cloud_anim4);

        DropsAnimation[0] = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.drop1_anim);
        DropsAnimation[1] = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.drop1_anim);
        DropsAnimation[2] = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.drop1_anim);
        DropsAnimation[3] = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.drop1_anim);

        coronaHits[0] = findViewById(R.id.CorHit1);
        coronaHits[1] = findViewById(R.id.CorHit2);
        coronaHits[2] = findViewById(R.id.CorHit3);
        coronaHits[3] = findViewById(R.id.CorHit4);
        coronaHits[4] = findViewById(R.id.CorHit5);


        IV[0] = findViewById(R.id.Cor1);
        IV[1] = findViewById(R.id.Cor2);
        IV[2] = findViewById(R.id.Cor3);
        IV[3] = findViewById(R.id.Cor4);

        bats[0] = findViewById(R.id.bat1);
        bats[1] = findViewById(R.id.bat2);
        bats[2] = findViewById(R.id.bat3);
        bats[3] = findViewById(R.id.bat4);

        imgTree1 = findViewById(R.id.tree3);
        gifTree1 = findViewById(R.id.tree1);

        clouds[0] = findViewById(R.id.cloud0);
        clouds[1] = findViewById(R.id.cloud1);
        clouds[2] = findViewById(R.id.cloud2);
        clouds[3] = findViewById(R.id.cloud3);

        animCorona = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.corana);
        animCorona.setFillAfter(true);

        isGameOver = false;
    }

    public void setScore() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                while (!isGameOver) {
                    Score++;
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            txtPoints.setText(Integer.toString(Score));
                        }
                    });
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }


    @Override
    public boolean onTouchEvent(MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                float posX = e.getX();
                batX = Batman.getX();

                if (posX > getBatManCenter()) {

                    batman.setBatRunningTo(Direction.RIGHT);

                } else {
                    batman.setBatRunningTo(Direction.LEFT);
                }

                if (isGameOver){
                    Intent i = new Intent(this, MainActivity.class);
                    startActivity(i);
                }
                break;
            case MotionEvent.ACTION_UP:
                batman.setBatRunningTo(Direction.MIDDLE);
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

//        setScore();
        for (int i = 0; i < 4; i++) {
            clouds[i].startAnimation(CloudAnimation[i]);
        }

        Thread RainLoop = new Thread() {
            public void run() {
                while (!isGameOver) {
                    new RainThread().start();
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        RainLoop.start();


        new treesMoveThread().start();

        batman.start();
    }

    public float getBatManCenter() {

        if (batman.getBatmanFacing() == 1) {
            return Batman.getX() + 320;
        } else {
            return Batman.getX() + 380;
        }
    }

    public void SetGameOver() {
        final TextView txtGameOver = new TextView(getApplicationContext());
        txtGameOver.setText("GAME OVER");
        txtGameOver.setTextColor(Color.RED);
        txtGameOver.setTextSize(50);

        mainLayout.addView(txtGameOver, 0);
        txtGameOver.setX(200);
        txtGameOver.setY(500);

        isGameOver = true;
    }

    class batsThread extends Thread {
        GifImageView[] Bats;

        batsThread(GifImageView[] bats) {
            this.Bats = bats;
        }

        @Override
        public void run() {
            for (int i = 0; i < 4; i++) {
                Bats[i].startAnimation(BatAnimation[i]);
                new coronaThread(i).start();
            }
        }
    }

    class RainThread extends Thread {
        ImageView[] drops = new ImageView[4];
        View RainView = new View(getApplicationContext());

        RainThread() {
            createRainLayout();

            drops[0] = RainView.findViewById(R.id.drop1);
            drops[1] = RainView.findViewById(R.id.drop2);
            drops[2] = RainView.findViewById(R.id.drop3);
            drops[3] = RainView.findViewById(R.id.drop4);
        }

        private void removeRainLayout(){
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    mainLayout.removeView(RainView);
                }
            });
        }

        @Override
        public void run() {
            for (int i = 0; i < 4; i++) {
                drops[i].startAnimation(DropsAnimation[i]);
            }

            final ValueAnimator ValA = new ValueAnimator();

            ValA.setDuration(3000);
            ValA.setFloatValues(-400f, 3000f);
            ValA.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float currVal = (float) ValA.getAnimatedValue();

                    RainView.setY(currVal);
                    if ((400f + currVal) > 2050 && (400f + currVal) < 2150) {
                        float midBat = getBatManCenter();

                        if ((midBat + 110 > RainView.getX() + 75) && (midBat - 110 < RainView.getX() + 75)) {
                            if (Game.getHits() > 0) {
                                Game.setHits(Game.getHits() - 1);
                                coronaHits[Game.getHits()].setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.border));
                            }
                            ValA.cancel();
                        }
                    }
                }
            });

            ValA.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    removeRainLayout();
                }

                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    removeRainLayout();
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }

            });


            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    mainLayout.addView(RainView, 10);
                    ValA.start();
                }
            });
        }

        private void createRainLayout() {
            final RelativeLayout.LayoutParams LP;
            Random ran = new Random();

            this.RainView = getLayoutInflater().inflate(R.layout.view_rain, null);

            LP = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            LP.leftMargin = ran.nextInt(700);
            LP.topMargin = 100;

            this.RainView.setLayoutParams(LP);
        }
    }

    class coronaThread extends Thread {
        Random r = new Random();
        final int Speed = r.nextInt(400) + 1800;
        final int Delay = r.nextInt(3000);
        final float posX = (r.nextFloat() * (1100)) + 150;
        boolean isChecked = false;
        final int ImgNm;

        coronaThread(int imgNm) {
            this.ImgNm = imgNm;
        }

        private Boolean isCoronaTouch(ValueAnimator VA) {

            float midBat = getBatManCenter();

            if (midBat + 110 > (posX + 20) && midBat - 110 < (posX + 20)) {
                Game.setLives(Game.getLives() - 1);
                Game.setHits(Game.getHits() + 1);

                if (Game.getHits() <= 5) {
                    coronaHits[Game.getHits() - 1].setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.hit_bg));
                }

                if (Game.getHits() == 5) {
                    SetGameOver();
                }

                VA.cancel();
                return true;
            } else {
                return false;
            }
        }

        private void deleteImageCorona() {
            IV[this.ImgNm].setY(-130f);
            IV[this.ImgNm].setVisibility(View.INVISIBLE);
        }

        private void startCoronaFalling() {
            IV[this.ImgNm].setX(posX);
            IV[this.ImgNm].setY(600);
            IV[this.ImgNm].setVisibility(View.VISIBLE);
        }

        @Override
        public void run() {
            final ValueAnimator VA = new ValueAnimator();

            VA.setFloatValues(0f, 1800f);
            VA.setDuration(Speed);
            VA.setStartDelay(Delay);

            VA.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float tempVal = (float) VA.getAnimatedValue();
                    IV[ImgNm].setY(600f + tempVal);

                    if (1430 <= (int) tempVal && 1480 >= (int) tempVal && !isChecked) {
                        isCoronaTouch(VA);
                        isChecked = true;
                    }
                }
            });

            VA.addListener(new Animator.AnimatorListener() {

                @Override
                public void onAnimationEnd(Animator animation) {
                    deleteImageCorona();
                }

                @Override
                public void onAnimationStart(Animator animation) {
                    startCoronaFalling();
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    deleteImageCorona();
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });

            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    VA.start();
                }
            });

        }
    }

    class treesMoveThread extends Thread {
        Runnable R;
        int counter = 0;

        @Override
        public void run() {

            while (!isGameOver) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mainHandler.post(R = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            imgTree1.setVisibility(View.INVISIBLE);
                            gifTree1.setVisibility(View.VISIBLE);
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                    }
                });

                batsThread bt = new batsThread(bats);
                bt.start();

                R = null;

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                mainHandler.post(R = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            imgTree1.setVisibility(View.VISIBLE);
                            gifTree1.setVisibility(View.INVISIBLE);
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }

                    }

                });

                R = null;
            }
        }
    }

    class BatmanThread extends Thread {

        private Direction BatRunningTo = Direction.MIDDLE;
        private int batmanFacing = 1;

        private void setBatRunningTo(Direction d) {
            BatRunningTo = d;
        }

        private Direction getBatRunningTo() {
            return this.BatRunningTo;
        }

        private void updateBatX(float f) {
            Batman.setX(f);
        }

        private int getBatmanFacing() {
            return this.batmanFacing;
        }

        @Override
        public void run() {
            while (!isGameOver) {
                float BX = Batman.getX();
                switch (getBatRunningTo()) {
                    case RIGHT:
                        this.batmanFacing = 1;
                        Batman.setScaleX(1);
                        while (Direction.RIGHT == getBatRunningTo() && BX < 1030) {
                            BX = BX + 0.004f;
                            updateBatX(BX);
                        }
                        break;

                    case LEFT:
                        this.batmanFacing = 2;
                        Batman.setScaleX(-1);
                        while (Direction.LEFT == getBatRunningTo() && BX > (-200)) {
                            BX = BX - 0.004f;
                            updateBatX(BX);
                        }
                        break;

                    case MIDDLE:
                        break;
                }
            }
        }
    }

    class Points extends Thread {

        int points = 0;

        @Override
        public void run() {
            while (!isGameOver) {
                points++;
                txtPoints.setText(Integer.toString(points));
            }


        }

    }
}
