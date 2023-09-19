package com.example.gridlayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.view.animation.AlphaAnimation;


import java.util.ArrayList;
import java.util.Random;
import java.util.List;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {
    ///// My variables /////
    private static final String bomb_emoji = "\uD83D\uDCA3";
    private static final int COLUMN_COUNT = 10;
    private static final int ROW_COUNT = 12;
    private static final int MINE_COUNT = 4;
    private boolean FLAGGING_MODE = false;
    private int flagged_mines_count = 4;
    private boolean[][] mine_loc_array;
    private boolean[][] flag_loc_array;
    private boolean[][] revealed_loc_array;
    private int[][] mine_count_at_cell_array;
    private boolean won = false;
    private int curr_clock = 0;
    private boolean running = true;

    // save the TextViews of all cells in an array, so later on,
    // when a TextView is clicked, we know which cell it is
    private ArrayList<TextView> cell_tvs;

    private int dpToPixel(int dp) {
        float density = Resources.getSystem().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //implement the boolean array for game logic purpose
        mine_loc_array = new boolean[ROW_COUNT][COLUMN_COUNT];
        flag_loc_array = new boolean[ROW_COUNT][COLUMN_COUNT];
        revealed_loc_array = new boolean[ROW_COUNT][COLUMN_COUNT];
        mine_count_at_cell_array = new int[ROW_COUNT][COLUMN_COUNT];

        // Initialize core elements
        initializeActionBar();
        initializeGrid();
        placeMines();
        getMineCountForAllCells();
    }

    private void getMineCountForAllCells() {
        for (int i = 0; i < ROW_COUNT; i++) {
            for (int j = 0; j < COLUMN_COUNT; j++) {
                if (!mine_loc_array[i][j]) {
                    int count = 0;
                    // loop around 8 adjacent cells to find bombs
                    for (int r = i - 1; r <= i + 1; r++) {
                        for (int c = j - 1; c <= j + 1; c++) {
                            if (r >= 0 && r < ROW_COUNT && c >= 0 && c < COLUMN_COUNT && mine_loc_array[r][c]) {
                                count++;
                            }
                        }
                    }
                    mine_count_at_cell_array[i][j] = count;
                }
            }
        }
    }


    private void revealOtherCells(int row, int col) {
        for (int i = row - 1; i <= row + 1; i++) {
            for (int j = col - 1; j <= col + 1; j++) {
                if (i >= 0 && i < ROW_COUNT && j >= 0 && j < COLUMN_COUNT) {
                    TextView tv = cell_tvs.get(i * COLUMN_COUNT + j);
                    if (tv.getCurrentTextColor() != Color.GRAY && !mine_loc_array[i][j]) {
                        revealed_loc_array[i][j] = true;
                        onClickTV(tv);
                    }
                }
            }
        }
    }

    private void initializeActionBar() {
        // Inflate the custom action bar layout
        View customActionBar = getLayoutInflater().inflate(R.layout.custom_actionbar, null);

        // Set the custom action bar layout as the action bar view
        getSupportActionBar().setCustomView(customActionBar);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false); // Hide the default title
    }

    private void initializeGrid() {
        initializeFlaggedMineCounter();
        initializeTimer();
        flagMineSwitcherButton();

        // Initialize the main grid
        cell_tvs = new ArrayList<TextView>();
        GridLayout grid = (GridLayout) findViewById(R.id.gridLayout01);
        LayoutInflater li = LayoutInflater.from(this);
        for (int i = 0; i<ROW_COUNT; i++) {
            for (int j=0; j<COLUMN_COUNT; j++) {
                TextView tv = (TextView) li.inflate(R.layout.custom_cell_layout, grid, false);
                //tv.setText(String.valueOf(i)+String.valueOf(j));
                tv.setTextColor(Color.GREEN);
                tv.setBackgroundColor(Color.GREEN);
                tv.setOnClickListener(this::onClickTV);

                GridLayout.LayoutParams lp = (GridLayout.LayoutParams) tv.getLayoutParams();
                lp.rowSpec = GridLayout.spec(i);
                lp.columnSpec = GridLayout.spec(j);

                grid.addView(tv, lp);

                cell_tvs.add(tv);
            }
        }
    }

    private void initializeFlaggedMineCounter() {
        // TextView: display count of flagged mines
        final TextView flagView = findViewById(R.id.minesCounterNumber);
        flagView.setText(String.valueOf(flagged_mines_count));
    }

    private void flagMineSwitcherButton() {
        // Find button via its ID
        Button flagMineSwitcherButton = findViewById(R.id.flag_mine_switcher);

        flagMineSwitcherButton.setOnClickListener(v -> {
            // Toggle FLAGGING_MODE and update button text
            FLAGGING_MODE = !FLAGGING_MODE;
            flagMineSwitcherButton.setText(FLAGGING_MODE ? R.string.flag : R.string.pick);
        });
    }

    public void initializeTimer(){
        final TextView timeView = findViewById(R.id.timerClockNumber);
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                timeView.setText(String.valueOf(curr_clock));
                if (running) {
                    curr_clock++;
                }
                handler.postDelayed(this, 1000);
            }
        });
    }

    private void placeMines() {
        // Randomly place mines on the grid
        int mines_num = 0;
        Random randomizer = new Random();

        while (mines_num < MINE_COUNT) {
            int r = randomizer.nextInt(ROW_COUNT);
            int c = randomizer.nextInt(COLUMN_COUNT);

            // If the randomized position doesn't already have mine, place mine there
            if (mine_loc_array[r][c] == true) continue;
            mine_loc_array[r][c] = true;
            mines_num += 1;
        }
    }


    public void revealAllMines() {
        for (int r = 0; r < ROW_COUNT; r++) {
            for (int c = 0; c < COLUMN_COUNT; c++) {
                if (mine_loc_array[r][c] == true) {
                    final TextView tv = cell_tvs.get(r * COLUMN_COUNT + c);
                    tv.setBackgroundColor(Color.RED);
                    tv.setText(bomb_emoji);

                    // Fade-in animation
                    AlphaAnimation animation = new AlphaAnimation(0, 1);
                    animation.setDuration(300); // Set the duration in milliseconds
                    tv.startAnimation(animation);
                }
            }
        }
    }

    private boolean checkGameStatus() {
        // Check if the game is over (true==win, false==lose and update UI
        // loop through the revealed_loc_array to see if all cells are revealed
        // return false if one or more cell(s) is not revealed
        for (int i = 0; i < ROW_COUNT; i++){
            for (int j = 0; j < COLUMN_COUNT; j++){
                if(revealed_loc_array[i][j] == false) return false;
            }
        }
        return true;
    }

    private void gameOver() {
        Intent intent = new Intent(MainActivity.this, ResultLandingPage.class);
        intent.putExtra("won", won);
        intent.putExtra("curr_clock", curr_clock);
        startActivity(intent);
    }

    private int findIndexOfCellTextView(TextView tv) {
        for (int n=0; n<cell_tvs.size(); n++) {
            if (cell_tvs.get(n) == tv)
                return n;
        }
        return -1;
    }

    public void onClickTV(View view){
        TextView tv = (TextView) view;
        int n = findIndexOfCellTextView(tv);
        int i = n/COLUMN_COUNT;
        int j = n%COLUMN_COUNT;

        if(!running) {
            gameOver();
        }
        // Main game logic
        else {
            // FLAGGING_MODE: Flagging/Unflagging a cell
            if (FLAGGING_MODE) {
                if (enterFlaggingMode(tv, i, j) == true) tv.setText(R.string.flag);
                else tv.setText(" ");
            }
            // !FLAGGING_MODE: click on bomb or miss a bomb
            else if (!flag_loc_array[i][j] && !FLAGGING_MODE) {
                enterMiningMode(tv, i, j);
            }

            // update the bool variables if the game condition is met
            if (checkGameStatus() == true) {
                running = false;
                won = true;
                gameOver();
            }
        }
    }

    public boolean enterFlaggingMode(TextView tv, int i, int j) {
        // unflag
        if (flag_loc_array[i][j]){
            flag_loc_array[i][j] = false;
            revealed_loc_array[i][j] = false;
            flagged_mines_count++;
            TextView temp = findViewById(R.id.minesCounterNumber);
            temp.setText(String.valueOf(flagged_mines_count));
            return false;
        }
        // flag
        else if (!revealed_loc_array[i][j] && !flag_loc_array[i][j]) {
            flag_loc_array[i][j] = true;
            revealed_loc_array[i][j] = true;
            flagged_mines_count--;
            TextView temp = findViewById(R.id.minesCounterNumber);
            temp.setText(String.valueOf(flagged_mines_count));
            return true;
        }

        return false;
    }

    public void enterMiningMode(TextView tv, int i, int j) {
        // Clicked on a bomb && not in FLAGGING_MODE && cell isn't flagged
        // Game over
        if (mine_loc_array[i][j] == true) {
            tv.setBackgroundColor(Color.RED);
            tv.setText(bomb_emoji);
            running = false;
            revealAllMines();

            Handler handler = new Handler();
            long delayMilliseconds = 3000;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    gameOver();
                }
            }, delayMilliseconds);
        }
        // Didn't click on bomb
        else {
            tv.setTextColor(Color.GRAY);
            tv.setBackgroundColor(Color.LTGRAY);
            revealed_loc_array[i][j] = true;

            int temp = mine_count_at_cell_array[i][j];
            if (temp > 0) {
                tv.setText(String.valueOf(temp));
            }
            else {
                revealOtherCells(i, j);
                tv.setText(" ");
            }
        }
    }
}