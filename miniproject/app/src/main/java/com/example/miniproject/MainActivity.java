package com.example.miniproject;

import android.animation.ObjectAnimator;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    TextView tvMoney;
    CheckBox cb1, cb2, cb3;
    SeekBar sb1, sb2, sb3;
    EditText etBetMoney1, etBetMoney2, etBetMoney3;
    Button btnStart, btnReset;
    ImageView cat1, cat2;
    MediaPlayer backgroundMusicPlayer, startSoundPlayer, endSoundPlayer;
    private static final int MAX_PROGRESS = 1000;
    private static final int UPDATE_INTERVAL = 100;
    Handler handler = new Handler();
    Random random = new Random();
    int progress1 = 0, progress2 = 0, progress3 = 0;
    boolean raceFinished = false;
    int totalMoney = 1000;

    // Tree ImageViews
    ImageView tree11, tree12, tree13, tree14, tree21, tree22, tree23, tree24;
    boolean treeAnimationRunning = false;

    // Placeholder arrays for tree positions
    float[] tree1X = new float[4];
    float[] tree1Y = new float[4];
    float[] tree2X = new float[4];
    float[] tree2Y = new float[4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Existing initialization code
        tvMoney = findViewById(R.id.tvMoney);
        cb1 = findViewById(R.id.cb1);
        cb2 = findViewById(R.id.cb2);
        cb3 = findViewById(R.id.cb3);
        sb1 = findViewById(R.id.sb1);
        sb2 = findViewById(R.id.sb2);
        sb3 = findViewById(R.id.sb3);
        etBetMoney1 = findViewById(R.id.etBetMoney1);
        etBetMoney2 = findViewById(R.id.etBetMoney2);
        etBetMoney3 = findViewById(R.id.etBetMoney3);
        btnStart = findViewById(R.id.btnStart);
        btnReset = findViewById(R.id.btnReset);
        cat1 = findViewById(R.id.cat1);
        cat2 = findViewById(R.id.cat2);

        // Initialize tree ImageViews
        tree11 = findViewById(R.id.tree11);
        tree12 = findViewById(R.id.tree12);
        tree13 = findViewById(R.id.tree13);
        tree14 = findViewById(R.id.tree14);
        tree21 = findViewById(R.id.tree21);
        tree22 = findViewById(R.id.tree22);
        tree23 = findViewById(R.id.tree23);
        tree24 = findViewById(R.id.tree24);

        initializeMediaPlayers();

        etBetMoney1.setEnabled(false);
        etBetMoney2.setEnabled(false);
        etBetMoney3.setEnabled(false);

        cb1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                etBetMoney1.setEnabled(isChecked);
            }
        });
        cb2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                etBetMoney2.setEnabled(isChecked);
            }
        });
        cb3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                etBetMoney3.setEnabled(isChecked);
            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBets()) {
                    playStartSound();
                    startRace();
                    startCatFlipAnimation();
                    startTreeAnimation();
                }
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetRace();
                initializeMediaPlayers();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMediaPlayers();
    }

    private void initializeMediaPlayers() {
        backgroundMusicPlayer = MediaPlayer.create(this, R.raw.background);
        startSoundPlayer = MediaPlayer.create(this, R.raw.start);
        endSoundPlayer = MediaPlayer.create(this, R.raw.end);
    }

    private void releaseMediaPlayers() {
        if (backgroundMusicPlayer != null) {
            backgroundMusicPlayer.stop();
            backgroundMusicPlayer.release();
            backgroundMusicPlayer = null;
        }
        if (startSoundPlayer != null) {
            startSoundPlayer.release();
            startSoundPlayer = null;
        }
        if (endSoundPlayer != null) {
            endSoundPlayer.release();
            endSoundPlayer = null;
        }
    }

    private void playStartSound() {
        if (startSoundPlayer != null) {
            startSoundPlayer.start();
        }
        if (backgroundMusicPlayer != null) {
            backgroundMusicPlayer.setLooping(true);
            backgroundMusicPlayer.start();
        }
    }

    private void playEndSound() {
        if (endSoundPlayer != null) {
            endSoundPlayer.start();
        }
        if (backgroundMusicPlayer != null) {
            backgroundMusicPlayer.stop();
        }
    }

    private int[] validateCheckboxAndEdittext() {
        int bet1 = 0, bet2 = 0, bet3 = 0;

        if (cb1.isChecked() && !etBetMoney1.getText().toString().isEmpty()) {
            bet1 = Integer.parseInt(etBetMoney1.getText().toString());
        }
        if (cb2.isChecked() && !etBetMoney2.getText().toString().isEmpty()) {
            bet2 = Integer.parseInt(etBetMoney2.getText().toString());
        }
        if (cb3.isChecked() && !etBetMoney3.getText().toString().isEmpty()) {
            bet3 = Integer.parseInt(etBetMoney3.getText().toString());
        }

        return new int[]{bet1, bet2, bet3};
    }

    private boolean checkBets() {
        int[] bets = validateCheckboxAndEdittext();
        int bet1 = bets[0];
        int bet2 = bets[1];
        int bet3 = bets[2];

        int totalBet = bet1 + bet2 + bet3;

        if (totalBet > totalMoney) {
            Toast.makeText(this, "Combined bet exceeds total money available!", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    private void startRace() {
        sb1.setMax(MAX_PROGRESS);
        sb2.setMax(MAX_PROGRESS);
        sb3.setMax(MAX_PROGRESS);

        raceFinished = false;
        progress1 = 0;
        progress2 = 0;
        progress3 = 0;
        sb1.setProgress(0);
        sb2.setProgress(0);
        sb3.setProgress(0);

        // Extract initial positions of trees
        tree1X[0] = tree11.getX();
        tree1X[1] = tree12.getX();
        tree1X[2] = tree13.getX();
        tree1X[3] = tree14.getX();
        tree1Y[0] = tree11.getY();
        tree1Y[1] = tree12.getY();
        tree1Y[2] = tree13.getY();
        tree1Y[3] = tree14.getY();

        tree2X[0] = tree21.getX();
        tree2X[1] = tree22.getX();
        tree2X[2] = tree23.getX();
        tree2X[3] = tree24.getX();
        tree2Y[0] = tree21.getY();
        tree2Y[1] = tree22.getY();
        tree2Y[2] = tree23.getY();
        tree2Y[3] = tree24.getY();

        updateSeekBars();
        btnStart.setEnabled(false);
    }

    private void resetRace() {
        raceFinished = true;
        btnStart.setEnabled(true);

        progress1 = 0;
        progress2 = 0;
        progress3 = 0;
        sb1.setProgress(0);
        sb2.setProgress(0);
        sb3.setProgress(0);

        cb1.setChecked(false);
        cb2.setChecked(false);
        cb3.setChecked(false);

        etBetMoney1.setText("");
        etBetMoney2.setText("");
        etBetMoney3.setText("");

        handler.removeCallbacksAndMessages(null); // Stop all animations

        // Reset cat positions
        cat1.setTranslationX(0);
        cat2.setTranslationX(0);

        stopTreeAnimation();
    }

    private void updateSeekBars() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!raceFinished) {
                    progress1 += random.nextInt(20) + 1;
                    progress2 += random.nextInt(20) + 1;
                    progress3 += random.nextInt(20) + 1;

                    if (progress1 > MAX_PROGRESS) progress1 = MAX_PROGRESS;
                    if (progress2 > MAX_PROGRESS) progress2 = MAX_PROGRESS;
                    if (progress3 > MAX_PROGRESS) progress3 = MAX_PROGRESS;

                    sb1.setProgress(progress1);
                    sb2.setProgress(progress2);
                    sb3.setProgress(progress3);

                    if (progress1 >= MAX_PROGRESS || progress2 >= MAX_PROGRESS || progress3 >= MAX_PROGRESS) {
                        raceFinished = true;
                        calculateBetMoney();
                        playEndSound();
                        stopTreeAnimation();
                    }

                    if (!raceFinished) {
                        handler.postDelayed(this, UPDATE_INTERVAL);
                    }
                }
            }
        }, UPDATE_INTERVAL);
    }

    private void calculateBetMoney() {
        int[] bets = validateCheckboxAndEdittext();
        int bet1 = bets[0], bet2 = bets[1], bet3 = bets[2];

        if (progress1 >= MAX_PROGRESS) {
            totalMoney += bet1;
        } else {
            totalMoney -= bet1;
        }

        if (progress2 >= MAX_PROGRESS) {
            totalMoney += bet2;
        } else {
            totalMoney -= bet2;
        }

        if (progress3 >= MAX_PROGRESS) {
            totalMoney += bet3;
        } else {
            totalMoney -= bet3;
        }

        tvMoney.setText("$" + totalMoney);
    }

    private void startCatFlipAnimation() {
        long duration = 500; // 0.5 seconds for each flip

        // Create ObjectAnimators for cat1
        ObjectAnimator cat1Flip = ObjectAnimator.ofFloat(cat1, "scaleX", 1f, -1f);
        cat1Flip.setDuration(duration);
        cat1Flip.setRepeatCount(ObjectAnimator.INFINITE);
        cat1Flip.setRepeatMode(ObjectAnimator.REVERSE);

        // Create ObjectAnimators for cat2
        ObjectAnimator cat2Flip = ObjectAnimator.ofFloat(cat2, "scaleX", 1f, -1f);
        cat2Flip.setDuration(duration);
        cat2Flip.setRepeatCount(ObjectAnimator.INFINITE);
        cat2Flip.setRepeatMode(ObjectAnimator.REVERSE);

        // Start the animations
        cat1Flip.start();
        cat2Flip.start();
    }

    private void startTreeAnimation() {
        treeAnimationRunning = true;
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (treeAnimationRunning) {
                    swapTreePositions();
                    handler.postDelayed(this, 500); // Delay for the loop
                }
            }
        });
    }

    private void stopTreeAnimation() {
        treeAnimationRunning = false;
    }

    private void swapTreePositions() {
        // Swap positions for the first row of trees (faster speed)
        float tempX = tree1X[0];
        float tempY = tree1Y[0];
        for (int i = 0; i < 3; i++) {
            tree1X[i] = tree1X[i + 1];
            tree1Y[i] = tree1Y[i + 1];
        }
        tree1X[3] = tempX;
        tree1Y[3] = tempY;

        tree11.setX(tree1X[0]);
        tree11.setY(tree1Y[0]);
        tree12.setX(tree1X[1]);
        tree12.setY(tree1Y[1]);
        tree13.setX(tree1X[2]);
        tree13.setY(tree1Y[2]);
        tree14.setX(tree1X[3]);
        tree14.setY(tree1Y[3]);

        // Swap positions for the second row of trees (slower speed)
        tempX = tree2X[0];
        tempY = tree2Y[0];
        for (int i = 0; i < 3; i++) {
            tree2X[i] = tree2X[i + 1];
            tree2Y[i] = tree2Y[i + 1];
        }
        tree2X[3] = tempX;
        tree2Y[3] = tempY;

        tree21.setX(tree2X[0]);
        tree21.setY(tree2Y[0]);
        tree22.setX(tree2X[1]);
        tree22.setY(tree2Y[1]);
        tree23.setX(tree2X[2]);
        tree23.setY(tree2Y[2]);
        tree24.setX(tree2X[3]);
        tree24.setY(tree2Y[3]);
    }
}
