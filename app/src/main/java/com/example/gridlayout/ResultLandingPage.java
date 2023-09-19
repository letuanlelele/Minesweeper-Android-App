package com.example.gridlayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ResultLandingPage extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_landing_page);

        initializeActionBar();

        TextView result = findViewById(R.id.result);
        Button play_again = findViewById(R.id.play_again);

        boolean won = getIntent().getBooleanExtra("won", false);
        int curr_clock = getIntent().getIntExtra("curr_clock", 0);

        String resultMessage;
        if(won == true) resultMessage = "Used " + curr_clock + " seconds.\nYou won.\nGood job!";
        else resultMessage = "You lost.\nReplay?";
        result.setText(resultMessage);

        play_again.setOnClickListener(view -> {
            Intent intent = new Intent(ResultLandingPage.this, MainActivity.class);
            startActivity(intent);
        });
    }
    private void initializeActionBar() {
        // Inflate the custom action bar layout
        View customActionBar = getLayoutInflater().inflate(R.layout.custom_actionbar, null);

        // Set the custom action bar layout as the action bar view
        getSupportActionBar().setCustomView(customActionBar);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false); // Hide the default title
    }
}

