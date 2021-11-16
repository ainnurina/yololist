package com.example.yololist;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    //private Button showList;
    //private TextView nameText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //showList = findViewById(R.id.button);
        //nameText = findViewById(R.id.textView);

        //showList.setOnClickListener(new View.OnClickListener() {
            //@Override
            //public void onClick(View view) {
            //    nameText.setText("Welcome to Shopping List!");
            //}
        //});
    }
}