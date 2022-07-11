package com.example.coursework2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


public class DisplayActivity extends AppCompatActivity {
    TextView display_text;
    SQLiteDatabase sqliiteDatabase;
    String sqliteQuery;
    ListView listView;
    ArrayList<String> display_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
//        display_text = (TextView)findViewById(R.id.display_text);
        sqliiteDatabase = openOrCreateDatabase("NoteSaver", Context.MODE_PRIVATE, null);
        listView = (ListView)findViewById(R.id.listView);
        display_list = new ArrayList<>();
        sqliteQuery = "SELECT text FROM Words_Phrases ORDER BY text ASC";
//        sqliiteDatabase.execSQL(sqliteQuery);
        Cursor cursor = sqliiteDatabase.rawQuery(sqliteQuery,null);
//        display_text.setText("");
        cursor.moveToFirst();

        do
        {
            //we can use c.getString(0) here
            //or we can get data using column index

            String text =cursor.getString(cursor.getColumnIndex("text"));

            //display on text view
                display_list.add(text);


            //move next position until end of the data
        }while(cursor.moveToNext());

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,display_list);
        listView.setAdapter(arrayAdapter);


    }



}
