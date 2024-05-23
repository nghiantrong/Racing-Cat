package com.example.miniproject;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    TextView money;
    CheckBox chBox1, chBox2, chBox3;
    SeekBar bar1, bar2, bar3;
    EditText ed1, ed2, ed3;
    Button start, restart;
    private int increment1, increment2, increment3;

    private MediaPlayer winSoundEffect;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Random random = new Random();
    private boolean isRaceFinished = false;

    private final Runnable progressRunnable = new Runnable() {
        @Override
        public void run() {
            bar1.setProgress(bar1.getProgress() + increment1);
            bar2.setProgress(bar2.getProgress() + increment2);
            bar3.setProgress(bar3.getProgress() + increment3);

            if (bar1.getProgress() < 100 || bar2.getProgress() < 100 || bar3.getProgress() < 100) {
                handler.postDelayed(this, 1000); // Update progress every 1 second
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        money = (TextView) findViewById(R.id.txtMoney);
        chBox1 = (CheckBox) findViewById(R.id.checkBox1);
        chBox2 = (CheckBox) findViewById(R.id.checkBox2);
        chBox3 = (CheckBox) findViewById(R.id.checkBox3);
        bar1 = (SeekBar) findViewById(R.id.crab1);
        bar2 = (SeekBar) findViewById(R.id.crab2);
        bar3 = (SeekBar) findViewById(R.id.crab3);
        ed1 = (EditText) findViewById(R.id.bet1);
        ed2 = (EditText) findViewById(R.id.bet2);
        ed3 = (EditText) findViewById(R.id.bet3);
        start = (Button) findViewById(R.id.btnStart);
        restart = (Button) findViewById(R.id.btnRestart);
        winSoundEffect = MediaPlayer.create(this, R.raw.win_sound);
        start.setOnClickListener(this);
        restart.setOnClickListener(this);

        increment1 = random.nextInt(10) + 1;
        increment2 = random.nextInt(10) + 1;
        increment3 = random.nextInt(10) + 1;

        chBox1.setOnCheckedChangeListener(((buttonView, isChecked) -> {
            if(isChecked){
                ed1.setVisibility(View.VISIBLE);
            }else{
                ed1.setVisibility(View.INVISIBLE);
            }
        }));
        chBox2.setOnCheckedChangeListener(((buttonView, isChecked) -> {
            if(isChecked){
                ed2.setVisibility(View.VISIBLE);
            }else{
                ed2.setVisibility(View.INVISIBLE);
            }
        }));
        chBox3.setOnCheckedChangeListener(((buttonView, isChecked) -> {
            if(isChecked){
                ed3.setVisibility(View.VISIBLE);
            }else{
                ed3.setVisibility(View.INVISIBLE);
            }
        }));
        if (savedInstanceState != null) {
            int savedMoney = savedInstanceState.getInt("money");
            int savedCrab1Progress = savedInstanceState.getInt("crab1Progress");
            int savedCrab2Progress = savedInstanceState.getInt("crab2Progress");
            int savedCrab3Progress = savedInstanceState.getInt("crab3Progress");

            money.setText(String.valueOf(savedMoney));
            bar1.setProgress(savedCrab1Progress);
            bar2.setProgress(savedCrab2Progress);
            bar3.setProgress(savedCrab3Progress);
        }
    }

    @Override
    public void onClick(View v){
        if(v.getId() == R.id.btnStart){
            int val = Integer.parseInt(money.getText().toString());
            int bet1 = TextUtils.isEmpty(ed1.getText()) ? 0 : Integer.parseInt(ed1.getText().toString());
            int bet2 = TextUtils.isEmpty(ed2.getText()) ? 0 : Integer.parseInt(ed2.getText().toString());
            int bet3 = TextUtils.isEmpty(ed3.getText()) ? 0 : Integer.parseInt(ed3.getText().toString());

            int totalBet = bet1 + bet2 + bet3;

            if(totalBet <= val){
                startProgress();
                checkWinner();
            }else{
                Toast.makeText(this, "You don't have enough money", Toast.LENGTH_SHORT).show();
            }
        }
        if(v.getId() == R.id.btnRestart){
            resetGame();
        }
    }

    private void resetGame(){
        bar1.setProgress(0);
        bar2.setProgress(0);
        bar3.setProgress(0);

        ed1.setText("");
        ed2.setText("");
        ed3.setText("");

        chBox1.setChecked(false);
        chBox2.setChecked(false);
        chBox3.setChecked(false);
    }

    private void checkWinner() {
        if (isRaceFinished) {
            return;
        }

        if (bar1.getProgress() >= 100 || bar2.getProgress() >= 100 || bar3.getProgress() >= 100) {
            isRaceFinished = true;
            handler.removeCallbacks(progressRunnable);

            bar1.setProgress(bar1.getProgress());
            bar2.setProgress(bar2.getProgress());
            bar3.setProgress(bar3.getProgress());

            winSoundEffect.start();

            int winningBar = 0;
            if (bar1.getProgress() >= 100) {
                winningBar = 1;
            } else if (bar2.getProgress() >= 100) {
                winningBar = 2;
            } else if (bar3.getProgress() >= 100) {
                winningBar = 3;
            }

            int winningAmount = 0;
            if (winningBar == 1 && chBox1.isChecked()) {
                winningAmount = Integer.parseInt(ed1.getText().toString());
            } else if (winningBar == 2 && chBox2.isChecked()) {
                winningAmount = Integer.parseInt(ed2.getText().toString());
            } else if (winningBar == 3 && chBox3.isChecked()) {
                winningAmount = Integer.parseInt(ed3.getText().toString());
            }

            if (winningAmount > 0) {
                int currentMoney = Integer.parseInt(money.getText().toString());
                int updatedMoney = currentMoney + winningAmount;
                money.setText(String.valueOf(updatedMoney));
                Intent intent = new Intent(MainActivity.this, ResultActivity.class);
                intent.putExtra("winningAmount", winningAmount);
                intent.putExtra("updatedMoney", updatedMoney);
                intent.putExtra("crab1Progress", bar1.getProgress());
                intent.putExtra("crab2Progress", bar2.getProgress());
                intent.putExtra("crab3Progress", bar3.getProgress());
                startActivity(intent);
                //finish();
            } else {
                Intent intent = new Intent(MainActivity.this, ResultActivity.class);
                intent.putExtra("winningAmount", 0);
                intent.putExtra("updatedMoney", Integer.parseInt(money.getText().toString()));
                intent.putExtra("crab1Progress", bar1.getProgress());
                intent.putExtra("crab2Progress", bar2.getProgress());
                intent.putExtra("crab3Progress", bar3.getProgress());
                startActivity(intent);
                //finish();
            }
        } else {
            handler.postDelayed(this::checkWinner, 100);
        }
    }

    private void startProgress(){
        isRaceFinished = false;
        handler.post(progressRunnable);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(progressRunnable);
        if (winSoundEffect != null) {
            winSoundEffect.release();
            winSoundEffect = null;
        }
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("money", Integer.parseInt(money.getText().toString()));
        outState.putInt("crab1Progress", bar1.getProgress());
        outState.putInt("crab2Progress", bar2.getProgress());
        outState.putInt("crab3Progress", bar3.getProgress());
    }
}