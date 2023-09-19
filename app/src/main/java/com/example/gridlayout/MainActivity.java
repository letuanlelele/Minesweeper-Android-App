package com.example.gridlayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;
import java.util.List;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {
    ///// My variables /////
    private static final int COLUMN_COUNT = 10;
    private static final int ROW_COUNT = 12;
    private static final int MINE_COUNT = 4;
    private boolean FLAGGING_MODE = false;
    private int flagged_mines_count = 4;
    private boolean[][] mine_loc_array;
    private boolean[][] flag_loc_array;

    // Not mine
    private int clock = 0;
    private boolean running = true;

    private boolean gameWin = false;



    private int[][] mineCount;
    private boolean[][] openLocations;

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

        openLocations = new boolean[ROW_COUNT][COLUMN_COUNT];
        mineCount = new int[ROW_COUNT][COLUMN_COUNT];

        // Initialize core elements
        initializeActionBar();
        initializeGrid();
        placeMines();









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
                timeView.setText(String.valueOf(clock));
                if (running) {
                    clock++;
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

    private void calculateMineCounts() {
        // Calculate the number of mines adjacent to each cell
    }

    private void revealCell(int row, int col) {
        // Recursive method to reveal cells and handle adjacent empty cells
    }

    private void flagCell(int row, int col) {
        // Toggle flag on a cell
    }

    private void checkGameStatus() {
        // Check if the game is over (win or lose) and update UI
    }

    private void restartGame() {
        // Reset the game state and UI
    }

    private int findIndexOfCellTextView(TextView tv) {
        for (int n=0; n<cell_tvs.size(); n++) {
            if (cell_tvs.get(n) == tv)
                return n;
        }
        return -1;
    }

    public void onClickTV(View view){
        // Handle cell clicks
        TextView tv = (TextView) view;
        int n = findIndexOfCellTextView(tv);
        int i = n/COLUMN_COUNT;
        int j = n%COLUMN_COUNT;
        tv.setText(String.valueOf(i)+String.valueOf(j));
        if (tv.getCurrentTextColor() == Color.GRAY) {
            tv.setTextColor(Color.GREEN);
            tv.setBackgroundColor(Color.parseColor("lime"));
        }else {
            tv.setTextColor(Color.GRAY);
            tv.setBackgroundColor(Color.LTGRAY);
        }
    }
}