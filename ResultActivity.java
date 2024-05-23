package com.example.miniproject;

import android.content.Intent;
import android.graphics.drawable.shapes.Shape;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.TimeUnit;

import nl.dionsegijn.konfetti.core.Party;
import nl.dionsegijn.konfetti.core.PartyFactory;
import nl.dionsegijn.konfetti.core.emitter.Emitter;
import nl.dionsegijn.konfetti.core.emitter.EmitterConfig;
import nl.dionsegijn.konfetti.core.models.Size;
import nl.dionsegijn.konfetti.xml.KonfettiView;

public class ResultActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView tvWinningAmount;
    private TextView tvCrab1Progress;
    private TextView tvCrab2Progress;
    private TextView tvCrab3Progress;
    private Button btnGoBack;
    private KonfettiView konfettiView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_race_result);

        tvWinningAmount = findViewById(R.id.tv_winning_amount);
        tvCrab1Progress = findViewById(R.id.tv_crab1_progress);
        tvCrab2Progress = findViewById(R.id.tv_crab2_progress);
        tvCrab3Progress = findViewById(R.id.tv_crab3_progress);
        btnGoBack =  findViewById(R.id.buttonBack);
        btnGoBack.setOnClickListener(this);

        konfettiView = findViewById(R.id.konfettiView);
        EmitterConfig emitterConfig = new Emitter(5L, TimeUnit.SECONDS).perSecond(50);
        Party party =
                new PartyFactory(emitterConfig)
                        .angle(270)
                        .spread(90)
                        .setSpeedBetween(1f, 5f)
                        .timeToLive(2000L)
                        .sizes(new Size(12, 5f, 0.2f))
                        .position(0.0, 0.0, 1.0, 0.0)
                        .build();
        konfettiView.start(party);

        int winningAmount = getIntent().getIntExtra("winningAmount", 0);
        int crab1Progress = getIntent().getIntExtra("crab1Progress", 0);
        int crab2Progress = getIntent().getIntExtra("crab2Progress", 0);
        int crab3Progress = getIntent().getIntExtra("crab3Progress", 0);

        tvWinningAmount.setText("-Amount Won: $" + winningAmount);
        tvCrab1Progress.setText("-Crab 1 Progress: " + crab1Progress + "%");
        tvCrab2Progress.setText("-Crab 2 Progress: " + crab2Progress + "%");
        tvCrab3Progress.setText("-Crab 3 Progress: " + crab3Progress + "%");
    }

    private void goBack(){
        finish();
    }
    @Override
    public void onClick(View v){
        int id = v.getId();
        if(id == R.id.buttonBack){
            goBack();
        }
    }
}
