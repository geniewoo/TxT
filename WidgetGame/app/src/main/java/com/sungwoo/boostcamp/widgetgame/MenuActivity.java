package com.sungwoo.boostcamp.widgetgame;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class MenuActivity extends AppCompatActivity {
    long mFirstTapped = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - mFirstTapped < 700) {
            super.onBackPressed();
        } else {
            mFirstTapped = System.currentTimeMillis();
            Toast.makeText(getApplicationContext(), R.string.MENU_BACK_PRESSED, Toast.LENGTH_LONG).show();
        }
    }

}
