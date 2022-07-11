package com.example.coursework2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddActivity extends AppCompatActivity {
    Boolean checkEditTextEmpty ;
    String sqliteQuery;
    String sqliteCheckQuery;
    SQLiteDatabase sqliiteDatabase;
    EditText typedText;
    String textWordPhrase;
    String text;
    Button save_button;
    Cursor cursor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        typedText = (EditText)findViewById(R.id.add_words_phrases);
        save_button = (Button)findViewById(R.id.save_button);



    }

    public void savePhrase(View view) {
        DBCreate();
        SubmitData();
        System.out.println(getDatabasePath("NoteSaver"));
    }

    public void DBCreate(){
        sqliiteDatabase = openOrCreateDatabase("NoteSaver", Context.MODE_PRIVATE, null);

        sqliiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS Words_Phrases(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, text VARCHAR);");
    }

    public void SubmitData(){
        textWordPhrase = typedText.getText().toString();

        CheckEditTextIsEmpty(textWordPhrase);


        if(checkEditTextEmpty)
        {
            sqliteCheckQuery = "SELECT text FROM Words_Phrases WHERE text='"+textWordPhrase+"'";
            cursor = sqliiteDatabase.rawQuery(sqliteCheckQuery,null);
            int row = cursor.getCount();
            cursor.moveToFirst();

            if(row==0) {
                System.out.println(sqliteCheckQuery);
                sqliteQuery = "INSERT INTO Words_Phrases (text) VALUES('" + textWordPhrase.toLowerCase() + "');";
                sqliiteDatabase.execSQL(sqliteQuery);

                Toast.makeText(AddActivity.this, "Data Submit Successfully", Toast.LENGTH_LONG).show();
                Clear();
            }
            else{
                text = cursor.getString(cursor.getColumnIndex("text"));
                if(text.equals(textWordPhrase)) {
                    Toast.makeText(AddActivity.this, "Data Already Exists", Toast.LENGTH_LONG).show();
                    Clear();
                }
            }
        }
        else {
            Toast.makeText(AddActivity.this,"Please Fill the Field", Toast.LENGTH_LONG).show();
        }


    }


    public void CheckEditTextIsEmpty(String text){
        if(TextUtils.isEmpty(text)){
            checkEditTextEmpty = false ;
        }
        else {
            checkEditTextEmpty = true ;
        }
    }

    public void Clear(){
        typedText.getText().clear();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
        }
    }

}
