package com.example.stopwatchapp;







import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private TextView timeView;
    private Button startButton;
    private Button holdButton;
    private Button resetButton;
    private ListView lapList;

    private Handler handler;
    private long startTime = 0L;
    private long timeInMilliseconds = 0L;
    private long updatedTime = 0L;
    private long timeSwapBuff = 0L;
    private int secs, mins, hrs;

    private boolean isRunning = false;
    private ArrayList<String> laps;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        timeView = findViewById(R.id.timeView);
        startButton = findViewById(R.id.startButton);
        holdButton = findViewById(R.id.holdButton);
        resetButton = findViewById(R.id.resetButton);
        lapList = findViewById(R.id.lapList);

        // Initialize other variables
        handler = new Handler();
        laps = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, laps);
        lapList.setAdapter(adapter);

        // Set up button click listeners
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isRunning) {
                    startTime = SystemClock.uptimeMillis();
                    handler.postDelayed(updateTimerThread, 0);
                    isRunning = true;
                    startButton.setText("Stop");
                } else {
                    timeSwapBuff += timeInMilliseconds;
                    handler.removeCallbacks(updateTimerThread);
                    isRunning = false;
                    startButton.setText("Start");
                }
            }
        });

        holdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isRunning) {
                    String lapTime = timeView.getText().toString();
                    laps.add(lapTime);
                    adapter.notifyDataSetChanged();
                }
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTime = 0L;
                timeInMilliseconds = 0L;
                timeSwapBuff = 0L;
                updatedTime = 0L;
                secs = 0;
                mins = 0;
                hrs = 0;
                timeView.setText("00:00:00");
                laps.clear();
                adapter.notifyDataSetChanged();
                if (isRunning) {
                    handler.removeCallbacks(updateTimerThread);
                    isRunning = false;
                    startButton.setText("Start");
                }
            }
        });
    }

    private Runnable updateTimerThread = new Runnable() {
        public void run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            updatedTime = timeSwapBuff + timeInMilliseconds;

            secs = (int) (updatedTime / 1000);
            mins = secs / 60;
            hrs = mins / 60;
            secs = secs % 60;
            mins = mins % 60;

            timeView.setText(String.format("%02d:%02d:%02d", hrs, mins, secs));
            handler.postDelayed(this, 0);
        }
    };
}