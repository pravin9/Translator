package com.example.coursework2;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class EditActivity extends AppCompatActivity {

    SQLiteDatabase sqliiteDatabase;
    Button update_button,save_button;
    EditText editText;
    String sqliteQuery;
    String updateQuery;
    RadioButton radioButton;
    RadioGroup radioGroup;
    String message,string;
    int attempt=0;


    //@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        radioGroup = (RadioGroup)findViewById(R.id.radio_group);
        save_button =(Button)findViewById(R.id.save_button);
        editText =(EditText)findViewById(R.id.translation_text_label);
        //radioButton = (RadioButton)findViewById(R.id.radio_button);
        update_button = (Button) findViewById(R.id.translate_button);
        sqliiteDatabase = openOrCreateDatabase("NoteSaver", Context.MODE_PRIVATE, null);
        sqliteQuery = "SELECT text FROM Words_Phrases";
//        sqliiteDatabase.execSQL(sqliteQuery);
        Cursor cursor = sqliiteDatabase.rawQuery(sqliteQuery,null);
        cursor.moveToFirst();

        do
        {
            //we can use c.getString(0) here
            //or we can get data using column index

            int count = cursor.getCount();
            String text =cursor.getString(cursor.getColumnIndex("text"));
            System.out.println(text+count);
                radioButton = new RadioButton(this);
                radioButton.setId(View.generateViewId());
                radioButton.setText(text);
                radioButton.setTextSize(15);
                radioGroup.addView(radioButton);

                radioButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(attempt==1){
                            int id = radioGroup.getCheckedRadioButtonId();
                            radioButton = (RadioButton) findViewById(id);
                            message = radioButton.getText().toString();
                            editText.setText(message);
                        }
                    }
                });




                update_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int id1 = radioGroup.getCheckedRadioButtonId();
                        radioButton = (RadioButton) findViewById(id1);
                        System.out.println(id1);
                        if(id1<0){
                            Toast.makeText(EditActivity.this,"Radio Button not selected",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            int id = radioGroup.getCheckedRadioButtonId();
                            radioButton = (RadioButton) findViewById(id);
                            message = radioButton.getText().toString();
                            editText.setText(message);
                            attempt = 1;
                        }
                    }
                });


                save_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        string = editText.getText().toString();
                        String checkExistence;
                        String selectQuery ="SELECT text FROM Words_Phrases WHERE text='"+string+"'";
                        Cursor cursor1 = sqliiteDatabase.rawQuery(selectQuery,null);
                        cursor1.moveToFirst();


                        if(cursor1.getCount()==0){
                            updateQuery = "UPDATE Words_Phrases SET text='" + string.toLowerCase() + "' WHERE text='" + message + "'";
                            Cursor cursor = sqliiteDatabase.rawQuery(updateQuery, null);
                            cursor.moveToFirst();
//                        Intent intent = new Intent(EditActivity.this,EditActivity2.class);
//                        intent.putExtra(extraMessage,message);
//                        startActivity(intent);
                            finish();
                            startActivity(getIntent());
                        }
                         else {
                            checkExistence = cursor1.getString(cursor1.getColumnIndex("text"));
                            if (checkExistence.equals(string)) {
                                Toast.makeText(EditActivity.this, "Data Already Exists", Toast.LENGTH_LONG).show();
                            }
                        }

                    }
                });


            //display on text view
            //display_text.append(text+"\n");

            //move next position until end of the data
        }while(cursor.moveToNext());

    }


}
