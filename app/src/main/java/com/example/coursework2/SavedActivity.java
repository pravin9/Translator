package com.example.coursework2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class SavedActivity extends AppCompatActivity {

    String settingsName = "AppSettings";
    SQLiteDatabase sqliiteDatabase;
    String sqliteQuery;
    LinearLayout linearLayout;
    TableLayout tableLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved);
        linearLayout = (LinearLayout)findViewById(R.id.linear);

        final String[] langsFull = new String[]{"Afrikaans","Albanian","Arabic","Armenian","Azerbaijani","Bashkir","Basque","Belarusian","Bengali","Bosnian","Bulgarian","Catalan","Central Khmer","Chinese","Chuvash","Croatian","Czech","Danish","Dutch","English","Esperanto","Estonian","Finnish","French","Georgian","German","Greek","Gujarati","Haitian","Hebrew","Hindi","Hungarian","Indonesian","Irish","Italian","Japanese","Kazakh","Kirghiz","Korean","Kurdish","Latvian","Lithuanian","Malay","Malayalam","Maltese","Mongolian","Norwegian Bokmal","Norwegian Nynorsk","Panjabi","Persian","Polish","Portuguese","Pushto","Romanian","Russian","Serbian","Slovakian","Slovenian","Somali","Spanish","Swedish","Tamil","Telugu","Thai","Turkish","Ukrainian","Urdu","Vietnamese"};

        sqliiteDatabase = openOrCreateDatabase("NoteSaver", Context.MODE_PRIVATE, null);

        SharedPreferences prefs = getSharedPreferences(settingsName, 0);
        for( int i =0;i<langsFull.length;i++){
            if(prefs.contains(langsFull[i])){

                final Button button = new Button(this);
                button.setText(langsFull[i]);
                linearLayout.addView(button);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setContentView(R.layout.saved2_layout);
                        String button_name = button.getText().toString();
                        System.out.println(button_name);
                        for (int j = 0; j < langsFull.length; j++) {

                            if (button_name.equals(langsFull[j])) {
                                sqliteQuery = "SELECT translated_text FROM " + langsFull[j] + " ";
                                String sqliteQuery1 = "SELECT text FROM Words_Phrases";
                                Cursor cursor1 = sqliiteDatabase.rawQuery(sqliteQuery1,null);
                                Cursor cursor = sqliiteDatabase.rawQuery(sqliteQuery, null);
                                cursor.moveToFirst();
                                cursor1.moveToFirst();

                                do {

                                    //we can use c.getString(0) here
                                    //or we can get data using column index
                                    TextView textView = new TextView(getApplicationContext());
                                    TextView show = new TextView(getApplicationContext());
                                    //show_text = (TextView) findViewById(R.id.show_text);
                                    tableLayout = (TableLayout)findViewById(R.id.table);
                                    String english_text = cursor1.getString(cursor1.getColumnIndex("text"));
                                    String text = cursor.getString(cursor.getColumnIndex("translated_text"));

                                    System.out.println(text);
                                    TableRow row= new TableRow(getApplicationContext());
                                    TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
                                    row.setLayoutParams(lp);

                                    show.append(" "+text);
                                    show.setTextColor(Color.BLACK);
                                    show.setTextSize(16);
                                    show.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                                    textView.append(" - "+english_text);
                                    textView.setTextColor(Color.BLACK);
                                    textView.setTextSize(16);
                                    textView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                                    row.addView(show);
                                    row.addView(textView);
                                    tableLayout.addView(row,0);

                                    //display on text view

                                    //move next position until end of the data

                                } while (cursor.moveToNext() && cursor1.moveToNext());

                            }
                        }
                    }
                });


            }
        }

    }
}
