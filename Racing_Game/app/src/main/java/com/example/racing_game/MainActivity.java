package com.example.racing_game;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    TextView tvMoney;
    CheckBox cb1;
    CheckBox cb2;
    CheckBox cb3;
    SeekBar sb1;
    SeekBar sb2;
    SeekBar sb3;
    EditText etBetMoney1;
    EditText etBetMoney2;
    EditText etBetMoney3;
    Button btnStart;
    Button btnReset;
    private static final int MAX_PROGRESS = 100;
    private static final int UPDATE_INTERVAL = 500;
    Handler handler = new Handler();
    Random random = new Random();
    int progress1 = 0, progress2 = 0, progress3 = 0;
    boolean raceFinished = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        tvMoney = (TextView) findViewById(R.id.tvMoney);
        cb1 = (CheckBox) findViewById(R.id.cb1);
        cb2 = (CheckBox) findViewById(R.id.cb2);
        cb3 = (CheckBox) findViewById(R.id.cb3);
        sb1 = (SeekBar) findViewById(R.id.sb1);
        sb2 = (SeekBar) findViewById(R.id.sb2);
        sb3 = (SeekBar) findViewById(R.id.sb3);
        etBetMoney1 = (EditText) findViewById(R.id.etBetMoney1);
        etBetMoney2 = (EditText) findViewById(R.id.etBetMoney2);
        etBetMoney3 = (EditText) findViewById(R.id.etBetMoney3);
        btnStart = (Button) findViewById(R.id.btnStart);
        btnReset = (Button) findViewById(R.id.btnReset);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sb1.setMax(MAX_PROGRESS);
                updateSeekBar();
            }
        });


    }

    private void updateSeekBar() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!raceFinished) {
                    progress1 += random.nextInt(5) + 1;
                    progress2 += random.nextInt(5) + 1;
                    progress3 += random.nextInt(5) + 1;

                    // Make sure progress doesn't exceed max value
                    if (progress1 > MAX_PROGRESS) progress1 = MAX_PROGRESS;
                    if (progress2 > MAX_PROGRESS) progress2 = MAX_PROGRESS;
                    if (progress3 > MAX_PROGRESS) progress3 = MAX_PROGRESS;

                    sb1.setProgress(progress1);
                    sb2.setProgress(progress2);
                    sb3.setProgress(progress3);

                    if (progress1 >= MAX_PROGRESS || progress2 >= MAX_PROGRESS || progress3 >= MAX_PROGRESS) {
                        raceFinished = true;
                    }

                    if (!raceFinished) {
                        handler.postDelayed(this, UPDATE_INTERVAL);
                    }
                }
            }
        }, UPDATE_INTERVAL);
    }
}