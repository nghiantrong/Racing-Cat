package com.example.racing_game;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;
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
    private static final int MAX_PROGRESS = 1000;
    private static final int UPDATE_INTERVAL = 100;
    Handler handler = new Handler();
    Random random = new Random();
    int progress1 = 0, progress2 = 0, progress3 = 0;
    boolean raceFinished = false;
    int totalMoney = 1000;

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
                    startRace();
                }
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetRace();
            }
        });
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
}