package com.example.coursework2;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import com.ibm.cloud.sdk.core.security.Authenticator;
import com.ibm.cloud.sdk.core.security.BasicAuthenticator;
import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.watson.language_translator.v3.LanguageTranslator;
import com.ibm.watson.language_translator.v3.model.IdentifiableLanguage;
import com.ibm.watson.language_translator.v3.model.IdentifiableLanguages;
import com.ibm.watson.language_translator.v3.model.IdentifiedLanguages;
import com.ibm.watson.language_translator.v3.model.TranslateOptions;
import com.ibm.watson.language_translator.v3.model.TranslationResult;
import com.ibm.watson.language_translator.v3.util.Language;
import com.ibm.watson.personality_insights.v3.model.ContentItem;

import org.apache.commons.codec.language.bm.Languages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class LanguageActivity extends AppCompatActivity {

    private String settingsTAG = "AppNameSettings";
    SQLiteDatabase sqliiteDatabase;
     LinearLayout linearLayout;
     LanguageTranslator translationService;
     TextView lang_text;
     String sqliteQuery,sqliteCheckQuery,sqliteCheckQuery1,getSqliteDisplay,message,queryCheck,queryBoolean,store,translate;

    ScrollView scrollView;
    Button subscribe_button;
    TranslationTask translationTask;
    Cursor cursor;
    RadioGroup radioGroup;
    Boolean checked = false;
    String string;
    ArrayList<String> subscribed_languages_list;
    int attempt=0;
    CheckBox checkBox;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);
        final String[] langs = new String[]{"af","sq","ar","hy","az","ba","eu","be","bn","bs","bg","ca","km","zh","cv","hr","cs","da","nl","en","eo","et","fi","fr","ka","de","el","gu","ht","he","hi","hu","id","ga","it","ja","kk","ky","ko","ku","lv","lt","ms","ml","mt","mn","nb","nn","pa","fa","pl","pt","ps","ro","ru","sr","sk","sl","so","es","sv","ta","te","th","tr","uk","ur","vi"};
        final String[] langsFull = new String[]{"Afrikaans","Albanian","Arabic","Armenian","Azerbaijani","Bashkir","Basque","Belarusian","Bengali","Bosnian","Bulgarian","Catalan","Central Khmer","Chinese","Chuvash","Croatian","Czech","Danish","Dutch","English","Esperanto","Estonian","Finnish","French","Georgian","German","Greek","Gujarati","Haitian","Hebrew","Hindi","Hungarian","Indonesian","Irish","Italian","Japanese","Kazakh","Kirghiz","Korean","Kurdish","Latvian","Lithuanian","Malay","Malayalam","Maltese","Mongolian","Norwegian Bokmal","Norwegian Nynorsk","Panjabi","Persian","Polish","Portuguese","Pushto","Romanian","Russian","Serbian","Slovakian","Slovenian","Somali","Spanish","Swedish","Tamil","Telugu","Thai","Turkish","Ukrainian","Urdu","Vietnamese"};
//        listView = (ListView)findViewById(R.id.listview1);
//        lang_text = (TextView) findViewById(R.id.language_text);
        final String[] checkboxarray = new String[68];
        linearLayout = (LinearLayout)findViewById(R.id.layout1);
        subscribe_button = (Button)findViewById(R.id.subscribe_button);
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences prefs = getSharedPreferences(settingsTAG, 0);
        final SharedPreferences.Editor editorString = prefs.edit();
        final SharedPreferences.Editor editor = preferences.edit();

        System.out.println(langs.length);
        System.out.println(langsFull.length);


        DBCreate();

        int count =1;

        for(int i=0;i<langsFull.length;i++) {
            sqliteCheckQuery = "SELECT languages FROM All_Languages WHERE languages='"+langsFull[i]+"'";
            cursor = sqliiteDatabase.rawQuery(sqliteCheckQuery,null);
            int row = cursor.getCount();
            cursor.moveToFirst();

            if(row==0) {
                    sqliteQuery = "INSERT INTO All_Languages (languages,translate_text) VALUES('" + langsFull[i] +"','" +langs[i]+"')";
                    sqliiteDatabase.execSQL(sqliteQuery);
                }
        }




        getSqliteDisplay = "SELECT languages FROM All_Languages ORDER BY languages ASC";
        queryCheck = "SELECT subscribed FROM Subscribed_Languages";

        Cursor cursor1 = sqliiteDatabase.rawQuery(getSqliteDisplay,null);
        Cursor cursor2 = sqliiteDatabase.rawQuery(queryCheck,null);

        cursor2.moveToFirst();
        cursor1.moveToFirst();


        do {
            String text =cursor1.getString(cursor.getColumnIndex("languages"));
            checkBox = new CheckBox(this);

            if (preferences.contains(text) && (preferences.getBoolean(text, false) == true)) {
                checkBox.setChecked(true);
            } else {
                checkBox.setChecked(false);
            }
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        System.out.println(checkBox.isChecked());
                        System.out.println(checkBox.getText());
                        checkBox = (CheckBox) buttonView;
                        message = checkBox.getText().toString();
                        if (checkBox.isChecked()) {
                            System.out.println("checked");
                            final int id = checkBox.getId();
                            System.out.println(message);


                            editor.putBoolean(message, true);
                            editorString.putString(message,message);
                            editorString.apply();
                            editor.apply();

                            Toast.makeText(LanguageActivity.this, "Subscribed languages successfully", Toast.LENGTH_LONG).show();
//                        }

//                                }else{
//                                sqliteCheckQuery1 = "SELECT subscribed FROM Subscribed_Languages WHERE subscribed='" + langs[id - 68] + "'";
//
//                                Cursor cursor3 = sqliiteDatabase.rawQuery(sqliteCheckQuery1, null);
//                                int row = cursor3.getCount();
//                                cursor3.moveToFirst();
//
//                                if (row == 0) {
//                                    System.out.println(id);
//                                    sqliteQuery = "INSERT INTO Subscribed_Languages (subscribed) VALUES('" + langs[id - 68] + "')";
//                                    sqliiteDatabase.execSQL(sqliteQuery);
//
//                                    editor.putBoolean(message, true);
//                                    editor.commit();
//                                    Toast.makeText(LanguageActivity.this, "Subscribed languages successfully", Toast.LENGTH_LONG).show();
//                                }
//                            }

                        }
                        if(!checkBox.isChecked()){
                            editor.putBoolean(message, false);
                            editorString.remove(message);
                            editorString.apply();
                            editor.apply();
                        }

                    }
                });
            subscribe_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(attempt==0){
                        sqliiteDatabase.execSQL("DROP TABLE IF EXISTS Subscribed_Languages");
                        attempt=1;
                    }
                    sqliiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS Subscribed_Languages(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, subscribed VARCHAR);");
                    for(int i =0;i<langsFull.length;i++){
                        if(prefs.contains(langsFull[i])){
                            sqliteQuery = "INSERT INTO Subscribed_Languages (subscribed) VALUES('" + langsFull[i] + "')";
                            sqliiteDatabase.execSQL(sqliteQuery);
                        }
                    }
                    Toast.makeText(LanguageActivity.this, "Subscribed languages successfully", Toast.LENGTH_SHORT).show();
                    sqliiteDatabase.close();
                    finish();

                }
            });




            checkBox.setId(View.generateViewId());
            checkBox.setText(text);
            checkBox.setTextSize(15);
            linearLayout.addView(checkBox);
        }while (cursor1.moveToNext());




        translationService = initLanguageTranslatorService();




//                System.out.println(translationService.listIdentifiableLanguages().execute().getResult());
//                translationTask = new TranslationTask();
//                translationTask.execute("hello");


    }



    public void DBCreate(){
        sqliiteDatabase = openOrCreateDatabase("NoteSaver", Context.MODE_PRIVATE, null);

        sqliiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS All_Languages(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, languages VARCHAR,translate_text VARCHAR);");
    }



    private LanguageTranslator initLanguageTranslatorService() {
        Authenticator authenticator = new IamAuthenticator(getString(R.string.language_translator_apikey));
        LanguageTranslator service = new LanguageTranslator("2018-05-01", authenticator);
        service.setServiceUrl(getString(R.string.language_translator_url));
        return service;
    }




    public class TranslationTask extends AsyncTask<String , Void, String> {

        @Override
        protected String doInBackground(String... params) {
            TranslateOptions translateOptions = new TranslateOptions.Builder()
                    .addText(params[0])
                    .source(Language.ENGLISH)
                    .target("es")
                    .build();
            TranslationResult result = translationService.translate(translateOptions).execute().getResult();
            System.out.println(result);
            String firstTranslation = result.getTranslations().get(0).getTranslation();
            return firstTranslation;
        }




        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            lang_text.setText(s);
        }
    }
}