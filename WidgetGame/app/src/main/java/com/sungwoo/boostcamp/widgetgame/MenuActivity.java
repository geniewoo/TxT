package com.sungwoo.boostcamp.widgetgame;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class MenuActivity extends AppCompatActivity {
    long mDoubleTouch = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - mDoubleTouch < 700) {
            super.onBackPressed();
        } else {
            mDoubleTouch = System.currentTimeMillis();
            Toast.makeText(getApplicationContext(), getString(R.string.MENU_BACK_PRESSED), Toast.LENGTH_LONG).show();
        }
    }

}
