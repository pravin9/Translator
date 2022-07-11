package com.example.coursework2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button add_button,display_button,edit_button,language_button, translator_button,translateAll_button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        add_button = (Button)findViewById(R.id.add_button);
        display_button = (Button)findViewById(R.id.display_button);
        edit_button = (Button)findViewById(R.id.edit_button);
        language_button = (Button)findViewById(R.id.language_button);
        translator_button = (Button)findViewById(R.id.translator_button);
        translateAll_button = (Button)findViewById(R.id.translate_save_button);

        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,AddActivity.class);
                startActivity(intent);
            }
        });

        display_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,DisplayActivity.class);
                startActivity(intent);
            }
        });

        edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,EditActivity.class);
                startActivity(intent);
            }
        });

        language_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,LanguageActivity.class);
                startActivity(intent);
            }
        });

        translator_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,TranslationActivity.class);
                startActivity(intent);
            }
        });

        translateAll_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,TranslateAllActivity.class);
                startActivity(intent);
            }
        });
    }
}
