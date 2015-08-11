package com.blogspot.stintech.runninghelper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.sql.SQLException;


//this class handles the history viewing and modifying
public class HistoryViewer extends ActionBarActivity {

    //for handling UI elements
    ListView list;
    static  DbAdapter db;
    Button finishBtn;
    DbCursorAdapter myCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history);

        //Initialize database
        db = new DbAdapter(this);

        //get UI handlers
        list = (ListView) findViewById(R.id.list);

        //open the database
        try {
            db.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //set the list adapter
        updateList();

        //register context menu
        registerForContextMenu(list);

        finishBtn = (Button) findViewById(R.id.historyBtnFinish);
        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    //updates list view
    void updateList()
    {
        //setting cursor adapter
        myCursorAdapter = new DbCursorAdapter(this,db.getAll(),1);

        //setting list adapter
        list.setAdapter(myCursorAdapter);

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo cinfo) {
        getMenuInflater().inflate(R.menu.longpress, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();

        //if delete is clicked
        if (item.getItemId() == R.id.delete_item)
        {

            //delete entry
            if (db.deleteOne((int) info.id)) {
                myCursorAdapter.changeCursor(db.getAll());
            }
            return true;

        }
        else if (item.getItemId() == R.id.edit_item)
        {
            //create a delete intent to edit items

            Intent i = new Intent(this,EditEntry.class);

            //pass list id
            i.putExtra("EDIT_ID",""+((int)info.id));

            //start activity
            startActivity(i);

            return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.add_item)
        {
            //create intent
            Intent i = new Intent(this,AddEntry.class);

            //start intent
            startActivity(i);

            //return
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onResume()
    {
        super.onResume();

        //reset list view
        myCursorAdapter.changeCursor(db.getAll());
        myCursorAdapter.notifyDataSetChanged();
    }

}
