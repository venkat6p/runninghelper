package com.blogspot.stintech.runninghelper;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Date;


//this class handles the database entry addition
public class AddEntry extends ActionBarActivity {

    //for handling UI
    EditText dateEt, timeEt;
    Button finishBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.addeditentry);

        //get UI handles
        dateEt = (EditText) findViewById(R.id.entryDateTV);

        timeEt = (EditText) findViewById(R.id.entrytimeTV);
        //timeEt.setEnabled(false);

        //to get time in format
        SimpleDateFormat s = new SimpleDateFormat("MM/dd/yyyy");

       dateEt.setText(s.format(new Date()));

        finishBtn = (Button) findViewById(R.id.buttonFinish);

        //on Button click insert entry
        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //insert
                HistoryViewer.db.insertOne(dateEt.getText().toString(),

                            timeEt.getText().toString());
                //return
                finish();
            }
        });

        //on clicking back button return
        ((Button) findViewById(R.id.buttonBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    /*@Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;


    }*/


}
