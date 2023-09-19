package com.example.gridlayout;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ResultLandingPage extends AppCompatActivity{
    // NEED CHANGE
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_landing_page);

        TextView resultMessage = findViewById(R.id.resultMessage);
        Button playAgain = findViewById(R.id.playAgain);

        // get the value from MainActivity
        boolean won = getIntent().getBooleanExtra("won", false);
        int curr_clock = getIntent().getIntExtra("curr_clock", 0);

        // have different output depends on the result
        String result;
        if(won){
            result = "Used " + curr_clock + " seconds.\nYou won.\nGood job!";
        }
        else{
            result = "You lost.\nTry again!";
        }
        resultMessage.setText(result);

        // allow the user to run the program again
        playAgain.setOnClickListener(view -> {
            Intent intent = new Intent(ResultLandingPage.this, MainActivity.class);
            startActivity(intent);
        });
    }
}
