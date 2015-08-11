package com.blogspot.stintech.runninghelper;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.sql.SQLException;



//this class handles DBlist data
public class DbCursorAdapter extends CursorAdapter {

    DbAdapter db;
    LayoutInflater inflater;

    //constructor
    public DbCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    //newView set
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return inflater.inflate(R.layout.layout,parent,false);
    }

    //binding view
    @Override
    public void bindView(View view, Context context, Cursor cursor)
    {
        TextView tvName, tvLoc, tvTime;
        //int id;
        Cursor tempCursor;

        //get UI elements
        tvName = (TextView)view.findViewById(R.id.birdnameTV);

        tvTime = (TextView)view.findViewById(R.id.timeTV);

        //create context adapter
        db = new DbAdapter(context);

        try {
            db.open();
            tempCursor = db.getOne(cursor.getLong(0));
            //id = tempCursor.getInt(0);
            tvName.setText(tempCursor.getString(1));

            tvTime.setText(tempCursor.getString(2)+" minutes");

            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
