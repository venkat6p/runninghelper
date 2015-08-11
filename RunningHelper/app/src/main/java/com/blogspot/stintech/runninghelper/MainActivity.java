package com.blogspot.stintech.runninghelper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;

/*

----------------------- Running Helper ------------------------

Note:
Testing;You might want to set the times to couple of seconds for faster testing.
Extra time currently is intentionally ignored.

This application helps the runners to train themselves for interval running training,
the app saves the history in Local database for tracking,
the app provides user option to add the running done outside the app for recording.

I have personally found the need for this application for my running,
So i choose to make this application.I have been using this application
whenever i go for running.

Programmatic concepts used;
- Databases
- Multi threading
- Timer
- Handler
- Wakelock
- Vibration

App Guide:

The user chooses the interval training parameters -
the run time - user runs for this much time,
the walk time -  user is supposed to walk this much time,
Extra time - It's an option if user wants to do some activity before starting running
Total time - The time of the total training activity after which timer stop
and the data is recorded in database.

When the start button is pressed when user starts running the first time,
the app starts informing user through phone vibration when user needs to
start or stop the  running or walking.

Testing;You might want to set the times to couple of seconds for faster testing

The current status of the training (Run/walk) is shown below the timer.

Use can use the stop button while training to stop.

There is a link to the activity History to show the list of the activity history
for user tracking.

User has option to edit, delete, and add the activities.

Database entry is created only if runtime is greater than one minute.
 */

// code comments are kept for future improvements

public class MainActivity extends ActionBarActivity
{
    //for getting UI handlers and using them
    Button startBtn;//, pauseBtn;

    EditText runTime, walkTime, extraTime, totalTime;
    TextView timeText, tvHistory, tvStatus;

    //to store details of running
    int runt, walkt,extrat,totalt, currentcycle=0;

    boolean status = false; //true for running, false for idle

    long startTime;

    PowerManager.WakeLock wakeLock;

    //To handle the timer operations in a separate thread
    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {


        @Override
        public void run() {

            //long millis = (System.currentTimeMillis() - startTime);

            //get system time and get minutes passed
            int seconds = (int) ((System.currentTimeMillis() - startTime) / 1000);
            int minutes = seconds / 60;

            //timeText.setText(String.valueOf(seconds%60));

            //set the timer text in UI
            timeText.setText(String.format("%d:%02d", minutes, seconds%60));


            //running time over
            if((seconds-currentcycle*(runt+walkt)) == runt)
            {
                tvStatus.setText("walk ...");

                //vibrate for 200ms
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(200);
            }

            //when walking ime is over
            else if((seconds - currentcycle*(runt+walkt)) == (runt+walkt))
            {
                tvStatus.setText("run ...");
                currentcycle = currentcycle +1;

                //vibrate for 200ms
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(200);

            }

            //target time reached
            if(minutes >= totalt)
            {
                //set UI elemets
                status = false;
                tvStatus.setText("");
                runTime.setEnabled(true);
                walkTime.setEnabled(true);
                extraTime.setEnabled(true);
                totalTime.setEnabled(true   );

                //stop this timer
                timerHandler.removeCallbacks(timerRunnable);

                startBtn.setText("Start");

                //add DB entry
                if(minutes > 0) {
                    SimpleDateFormat s = new SimpleDateFormat("MM/dd/yyyy");
                    try {
                        HistoryViewer.db.insertOne(s.format(new Date()).toString(), "" + minutes);
                    } catch (Exception e) {
                        Toast.makeText(getBaseContext(), "Error Storing to database", Toast.LENGTH_LONG);
                    }
                }
                wakeLock.release();
            }
            else
                timerHandler.postDelayed(timerRunnable, 500);

            //timerTextView.setText(String.format("%d:%02d", minutes, seconds));

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get UI handles
        startBtn = (Button) findViewById(R.id.statusBtn);
        //pauseBtn = (Button) findViewById(R.id.pauseBtn);
        runTime = (EditText) findViewById(R.id.runET);
        walkTime = (EditText) findViewById(R.id.walkET);
        extraTime = (EditText) findViewById(R.id.extraET);
        totalTime = (EditText) findViewById(R.id.totalET);
        timeText = (TextView) findViewById(R.id.timeTV);
        tvHistory = (TextView) findViewById(R.id.tvHistory);
        tvStatus = (TextView) findViewById(R.id.tvStatus);

        //set wake lock to keep app awake even when phone is in deep sleep
        PowerManager mgr = (PowerManager)getSystemService(Context.POWER_SERVICE);
        //final PowerManager.WakeLock
                wakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakeLock");

        //when start is pressed
        startBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //starting running
                if(status == false)
                {
                   // getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    //acquire wake lock
                    wakeLock.acquire();

                    //set UI elements
                    status = true;
                    tvStatus.setText("run ...");
                    startBtn.setText("Stop");
                    runt = Integer.parseInt(runTime.getText().toString());
                    extrat = Integer.parseInt(extraTime.getText().toString());
                    walkt = Integer.parseInt(walkTime.getText().toString());
                    totalt = Integer.parseInt(totalTime.getText().toString());

                    runTime.setEnabled(false);
                    walkTime.setEnabled(false);
                    extraTime.setEnabled(false);
                    totalTime.setEnabled(false);


                    //Timer timer = new Timer();

                    //start timer
                    startTime = System.currentTimeMillis();
                    timerHandler.postDelayed(timerRunnable, 0);




                }
                else
                {
                    //when stop is pressed

                    //set uI
                    status = false;
                    tvStatus.setText("");
                    runTime.setEnabled(true);
                    walkTime.setEnabled(true);
                    extraTime.setEnabled(true);
                    totalTime.setEnabled(true   );

                    //stop timer
                    timerHandler.removeCallbacks(timerRunnable);

                    //show the new button
                    startBtn.setText("Start");

                    //add DB entry
                    SimpleDateFormat s = new SimpleDateFormat("MM/dd/yyyy");
                    try {
                        HistoryViewer.db.insertOne(s.format(new Date()).toString(), totalTime.getText().toString());
                    }
                    catch (Exception e)
                    {
                        Toast.makeText(getBaseContext(),"Error Storing to database",Toast.LENGTH_LONG);
                    }

                    //release wake lock
                    wakeLock.release();
                }


            }
        });

       /* pauseBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(status == true)
                {
                    status = false;
                    pauseBtn.setText("Un Pause");
                    timerHandler.removeCallbacks(timerRunnable);
                }
                else
                {
                    status =true;
                    pauseBtn.setText("Pause");
                    timerHandler.postDelayed(timerRunnable, 0);
                }
            }
        });*/

        //set history link underlined
        tvHistory.setPaintFlags(tvHistory.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        //on history click
        tvHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(),HistoryViewer.class);
                startActivity(i);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_info, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.info_item)
        {
           //information Dialog
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Information");
            alertDialog.setMessage("------ Running Helper -----\n" +
                    "\n" +
                    "Note:\n" +
                    "Testing: You might want to set the times to couple of seconds for faster testing.\n" +
                    "Extra time currently is intentionally ignored.\n" +
                    "\n" +
                    "This application helps the runners to train themselves for interval running training,\n" +
                    "the app saves the history in Local database for tracking,\n" +
                    "the app provides user option to add the running done outside the app for recording.\n" +
                    "\n" +

                    "Programmatic concepts used;\n" +
                    "- Databases\n" +
                    "- Multi threading\n" +
                    "- Timer\n" +
                    "- Handler\n" +
                    "- Wakelock\n" +
                    "- Vibration\n" +
                    "\n" +
                    "App Guide:\n" +
                    "\n" +
                    "The user chooses the interval training parameters -\n" +
                    "the run time - user runs for this much time,\n" +
                    "the walk time -  user is supposed to walk this much time,\n" +
                    "Extra time - It's an option if user wants to do some activity before starting running\n" +
                    "Total time - The time of the total training activity after which timer stop\n" +
                    "and the data is recorded in database.\n" +
                    "\n" +
                    "When the start button is pressed when user starts running the first time,\n" +
                    "the app starts informing user through phone vibration when user needs to\n" +
                    "start or stop the  running or walking.\n" +
                    "\n" +
                    "Testing;You might want to set the times to couple of seconds for faster testing\n" +
                    "\n" +
                    "The current status of the training (Run/walk) is shown below the timer.\n" +
                    "\n" +
                    "Use can use the stop button while training to stop.\n" +
                    "\n" +
                    "There is a link to the activity History to show the list of the activity history\n" +
                    "for user tracking.\n" +
                    "\n" +
                    "User has option to edit, delete, and add the activities. " +
                    "\nDatabase entry is created only if runtime is greater than one minute.");

            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
            //return
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
