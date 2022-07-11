package com.example.coursework2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ibm.cloud.sdk.core.security.Authenticator;
import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.watson.developer_cloud.android.library.audio.StreamPlayer;
import com.ibm.watson.language_translator.v3.LanguageTranslator;
import com.ibm.watson.language_translator.v3.model.TranslateOptions;
import com.ibm.watson.language_translator.v3.model.TranslationResult;
import com.ibm.watson.language_translator.v3.util.Language;
import com.ibm.watson.text_to_speech.v1.TextToSpeech;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class TranslateAllActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private String settingsName = "AppSettings";
    SQLiteDatabase sqliiteDatabase;
    Button translate_button,show_saved_button;
    String sqliteQuery, translate_string, sqliteCheckQuery, getSqlSaveQuery;
    Spinner spinner;
    String settingsTAG = "AppNameSettings";
    LanguageTranslator translationService;
    TranslationTask translationTask;
    String[] voice_languages;
    String[] voices;
    String language_selected;
    SharedPreferences preferences;
    SharedPreferences.Editor editorString;
    String text;
    ArrayList<String> normal_list,translated_list;
    int no =0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate_all);
        translate_button = (Button) findViewById(R.id.translateAll_button);
        show_saved_button = (Button) findViewById(R.id.show_saved_button);
        spinner = (Spinner) findViewById(R.id.spinner);
        voice_languages = new String[]{"Arabic", "Chinese", "Dutch", "English", "French", "German", "Italian", "Japanese", "Portuguese", "Spanish"};
        voices = new String[]{"ar-AR_OmarVoice", "zh-CN_WangWeiVoice", "nl-NL_LiamVoice", "en-US_MichaelVoice", "fr-FR_ReneeVoice", "de-DE_DieterVoice", "it-IT_FrancescaVoice", "ja-JP_EmiVoice", "pt-BR_IsabelaVoice", "es-ES_EnriqueVoice"};
        final String[] langsFull = new String[]{"Afrikaans", "Albanian", "Arabic", "Armenian", "Azerbaijani", "Bashkir", "Basque", "Belarusian", "Bengali", "Bosnian", "Bulgarian", "Catalan", "Central Khmer", "Chinese", "Chuvash", "Croatian", "Czech", "Danish", "Dutch", "English", "Esperanto", "Estonian", "Finnish", "French", "Georgian", "German", "Greek", "Gujarati", "Haitian", "Hebrew", "Hindi", "Hungarian", "Indonesian", "Irish", "Italian", "Japanese", "Kazakh", "Kirghiz", "Korean", "Kurdish", "Latvian", "Lithuanian", "Malay", "Malayalam", "Maltese", "Mongolian", "Norwegian Bokmal", "Norwegian Nynorsk", "Panjabi", "Persian", "Polish", "Portuguese", "Pushto", "Romanian", "Russian", "Serbian", "Slovakian", "Slovenian", "Somali", "Spanish", "Swedish", "Tamil", "Telugu", "Thai", "Turkish", "Ukrainian", "Urdu", "Vietnamese"};
        sqliiteDatabase = openOrCreateDatabase("NoteSaver", Context.MODE_PRIVATE, null);
        sqliteQuery = "SELECT text FROM Words_Phrases";
        ArrayList<String> spinner_list = new ArrayList<>();
        preferences = getSharedPreferences(settingsName, 0);
        editorString = preferences.edit();
        normal_list = new ArrayList<>();
        translated_list = new ArrayList<>();




//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//        preferences.getBoolean()
//        System.out.println(preferences.getString("message",null));


        if (spinner != null) {
            spinner.setOnItemSelectedListener(this);
        }

        SharedPreferences prefs = getSharedPreferences(settingsTAG, 0);
        for (int i = 0; i < langsFull.length; i++) {
            if (prefs.contains(langsFull[i])) {
                spinner_list.add(langsFull[i]);
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_xml, spinner_list);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_xml);

        if (spinner != null) {
            spinner.setAdapter(adapter);
        }

        translate_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                no=0;
                Cursor cursor = sqliiteDatabase.rawQuery(sqliteQuery, null);
                cursor.moveToFirst();
                do {


                    text = cursor.getString(cursor.getColumnIndex("text"));


                        translationService = initLanguageTranslatorService();
//                System.out.println(translationService.listIdentifiableLanguages().execute().getResult());
                        translationTask = new TranslationTask();
                        translationTask.execute(text);


                } while (cursor.moveToNext());
            }
        });

        show_saved_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TranslateAllActivity.this,SavedActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        language_selected = parent.getItemAtPosition(position).toString();
        sqliteCheckQuery = "SELECT translate_text FROM All_Languages WHERE languages='" + language_selected + "'";
        Cursor cursor = sqliiteDatabase.rawQuery(sqliteCheckQuery, null);
        cursor.moveToFirst();
        translate_string = cursor.getString(cursor.getColumnIndex("translate_text"));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private LanguageTranslator initLanguageTranslatorService() {
        Authenticator authenticator = new IamAuthenticator(getString(R.string.language_translator_apikey));
        LanguageTranslator service = new LanguageTranslator("2018-05-01", authenticator);
        service.setServiceUrl(getString(R.string.language_translator_url));
        return service;
    }


    public class TranslationTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            TranslateOptions translateOptions = new TranslateOptions.Builder()
                    .addText(params[0])
                    .source(Language.ENGLISH)
                    .target(translate_string)
                    .build();
            TranslationResult result = translationService.translate(translateOptions).execute().getResult();
            System.out.println(result.getTranslations().toString());
            String firstTranslation = result.getTranslations().get(0).getTranslation();
            return firstTranslation;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            String name = language_selected;

            if(no==0){
                sqliiteDatabase.execSQL("DROP TABLE IF EXISTS " + name);
                no=1;
            }
            sqliiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + name + " (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, translated_text VARCHAR);");

            editorString.putString(name, name);
            editorString.commit();

            String checkQuery = "SELECT translated_text FROM " + name + " WHERE translated_text='" + s + "'";
            Cursor cursor = sqliiteDatabase.rawQuery(checkQuery, null);
            int row = cursor.getCount();
            cursor.moveToFirst();

            getSqlSaveQuery = "INSERT INTO " + name + " (translated_text) VALUES('" + s + "');";
            sqliiteDatabase.execSQL(getSqlSaveQuery);

//                    } else {
//                            String query_text = cursor.getString(cursor.getColumnIndex("translated_text"));
//                            if (!(query_text.equals(s))) {
//                                getSqlSaveQuery = "INSERT INTO " + name + " (translated_text) VALUES('"+ s + "');";
//                                sqliiteDatabase.execSQL(getSqlSaveQuery);
//                            }
        //}

            Toast.makeText(TranslateAllActivity.this, "Done Translating", Toast.LENGTH_SHORT).show();
        }

    }

}
