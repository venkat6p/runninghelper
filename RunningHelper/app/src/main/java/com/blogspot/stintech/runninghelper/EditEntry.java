package com.blogspot.stintech.runninghelper;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;



//this class handles the database entry modification
public class EditEntry extends ActionBarActivity {


    //for handling UI
    EditText nameEt, locEt, timeEt;
    Button finishBtn;
    int id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.addeditentry);

        //get UI handles
        nameEt = (EditText) findViewById(R.id.entryDateTV);

        timeEt = (EditText) findViewById(R.id.entrytimeTV);

        id= Integer.parseInt(getIntent().getExtras().getString("EDIT_ID"));

        //read db entry
        Cursor c = HistoryViewer.db.getOne(id);

        //filling text
        nameEt.setText(c.getString(1));

        timeEt.setText(c.getString(2));

        finishBtn = (Button) findViewById(R.id.buttonFinish);

        //onclicking button update
        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //update
                HistoryViewer.db.updateOne(id, nameEt.getText().toString(), timeEt.getText().toString());
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
