package com.example.coursework2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
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
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.ibm.cloud.sdk.core.http.HttpMediaType;
import com.ibm.cloud.sdk.core.security.Authenticator;
import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.watson.developer_cloud.android.library.audio.StreamPlayer;
import com.ibm.watson.language_translator.v3.LanguageTranslator;
import com.ibm.watson.language_translator.v3.model.TranslateOptions;
import com.ibm.watson.language_translator.v3.model.TranslationResult;
import com.ibm.watson.language_translator.v3.util.Language;
import com.ibm.watson.text_to_speech.v1.TextToSpeech;
import com.ibm.watson.text_to_speech.v1.model.SynthesizeOptions;
import com.ibm.watson.text_to_speech.v1.model.Voices;

import java.util.ArrayList;

public class TranslationActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    SQLiteDatabase sqliiteDatabase;
    Button translate_button,speak_button;
    TextView translation_view;
    String sqliteQuery,translate_string,sqliteCheckQuery,message;
    RadioButton radioButton;
    RadioGroup radioGroup;
    Spinner spinner;
    Context context;
    String settingsTAG = "AppNameSettings";
    LanguageTranslator translationService;
    TranslationTask translationTask;
    private StreamPlayer player;
    private TextToSpeech textService;
    String[] voice_languages;
    String[] voices;
    String language_selected;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translation);

        player = new StreamPlayer();
        radioGroup = (RadioGroup)findViewById(R.id.radio_group);
        translation_view =(TextView) findViewById(R.id.translation_text_label);
        translate_button= (Button) findViewById(R.id.translate_button);
        spinner = (Spinner) findViewById(R.id.spinner);
        voice_languages = new String[]{"Arabic","Chinese","Dutch","English","French","German","Italian","Japanese","Portuguese","Spanish"};
        voices = new String[]{"ar-AR_OmarVoice","zh-CN_WangWeiVoice","nl-NL_LiamVoice","en-US_MichaelVoice","fr-FR_ReneeVoice","de-DE_DieterVoice","it-IT_FrancescaVoice","ja-JP_EmiVoice","pt-BR_IsabelaVoice","es-ES_EnriqueVoice"};
        final String[] langsFull = new String[]{"Afrikaans","Albanian","Arabic","Armenian","Azerbaijani","Bashkir","Basque","Belarusian","Bengali","Bosnian","Bulgarian","Catalan","Central Khmer","Chinese","Chuvash","Croatian","Czech","Danish","Dutch","English","Esperanto","Estonian","Finnish","French","Georgian","German","Greek","Gujarati","Haitian","Hebrew","Hindi","Hungarian","Indonesian","Irish","Italian","Japanese","Kazakh","Kirghiz","Korean","Kurdish","Latvian","Lithuanian","Malay","Malayalam","Maltese","Mongolian","Norwegian Bokmal","Norwegian Nynorsk","Panjabi","Persian","Polish","Portuguese","Pushto","Romanian","Russian","Serbian","Slovakian","Slovenian","Somali","Spanish","Swedish","Tamil","Telugu","Thai","Turkish","Ukrainian","Urdu","Vietnamese"};
        sqliiteDatabase = openOrCreateDatabase("NoteSaver", Context.MODE_PRIVATE, null);
        sqliteQuery = "SELECT text FROM Words_Phrases ORDER BY text ASC";
        ArrayList<String> spinner_list = new ArrayList<>();
        speak_button = (Button)findViewById(R.id.speak_button_label);
        textService = initTextToSpeechService();

        Cursor cursor = sqliiteDatabase.rawQuery(sqliteQuery,null);
        cursor.moveToFirst();

//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//        preferences.getBoolean()
//        System.out.println(preferences.getString("message",null));



        if(spinner != null){
            spinner.setOnItemSelectedListener(this);
        }

        SharedPreferences prefs = getSharedPreferences(settingsTAG, 0);
        for(int i =0;i<langsFull.length;i++){
            if(prefs.contains(langsFull[i])){
                spinner_list.add(langsFull[i]);
            }
        }

       ArrayAdapter<String> adapter = new  ArrayAdapter<String>(getApplicationContext(),R.layout.spinner_xml,spinner_list);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_xml);

       if(spinner != null){
           spinner.setAdapter(adapter);
        }


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


            translate_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int he = radioGroup.getCheckedRadioButtonId();
                    radioButton = (RadioButton) findViewById(he);
                    message = radioButton.getText().toString();

                    translationService = initLanguageTranslatorService();
//                System.out.println(translationService.listIdentifiableLanguages().execute().getResult());
                    translationTask = new TranslationTask();
                    translationTask.execute(message);


                    speak_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String translated_text = translation_view.getText().toString();
                            System.out.println(translated_text);
                            SynthesisTask synthesisTask = new SynthesisTask();
                            synthesisTask.execute(translated_text);
                        }
                    });

                }
            });



        }while(cursor.moveToNext());

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        language_selected = parent.getItemAtPosition(position).toString();
        sqliteCheckQuery = "SELECT translate_text FROM All_Languages WHERE languages='"+language_selected+"'";
        Cursor cursor = sqliiteDatabase.rawQuery(sqliteCheckQuery,null);
        cursor.moveToFirst();
        translate_string = cursor.getString(cursor.getColumnIndex("translate_text"));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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
                    .target(translate_string)
                    .build();
            TranslationResult result = translationService.translate(translateOptions).execute().getResult();
            System.out.println(result);
            String firstTranslation = result.getTranslations().get(0).getTranslation();
            return firstTranslation;
        }




        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            translation_view.setText(s);
            translation_view.setTextColor(Color.BLACK);
            translation_view.setTextSize(15);
            translation_view.setTypeface(translation_view.getTypeface(), Typeface.BOLD_ITALIC);
        }
    }


    private TextToSpeech initTextToSpeechService() {
        Authenticator authenticator = new IamAuthenticator(getString(R.string.text_speech_apikey));
        TextToSpeech service = new TextToSpeech(authenticator);
        service.setServiceUrl(getString(R.string.text_speech_url));
        return service;
    }



    private class SynthesisTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            for(int i=0;i<voice_languages.length;i++) {
                if(voice_languages[i].equals(language_selected)) {
                    SynthesizeOptions synthesizeOptions = new SynthesizeOptions.Builder()
                            .text(params[0])
                            .voice(voices[i])
                            .accept(HttpMediaType.AUDIO_WAV)
                            .build();
                    player.playStream(textService.synthesize(synthesizeOptions).execute()
                            .getResult());
                    i=voice_languages.length;
                }
            }return "Did synthesize";
        }
    }
}
