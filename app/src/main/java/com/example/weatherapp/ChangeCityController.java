package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class ChangeCityController extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_city_layout);

        final EditText editText = (EditText) findViewById(R.id.editText);
        final ImageView imageView = (ImageView) findViewById(R.id.imageView);
        final Button findBtn = (Button) findViewById(R.id.button);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cityName=editText.getText().toString();
                Intent intent = new Intent(ChangeCityController.this,MainActivity.class);
                intent.putExtra("cityname",cityName);
                startActivity(intent);
            }
        });
    }
}
