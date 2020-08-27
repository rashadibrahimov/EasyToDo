package com.example.easytodo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity {

    private CheckBox nightModeCheckBox;
    private TextView nightModeOnOff;
    private DatabaseHelper myHelper;
    private SQLiteDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        myHelper = new DatabaseHelper(this);
        mDatabase = myHelper.getWritableDatabase();
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.DarkTheme);
            ContentValues contentValues = new ContentValues();
            contentValues.put(DatabaseHelper.THEME, "DarkTheme");

            mDatabase.update(DatabaseHelper.THEME_TABLE, contentValues, null, null);
        }
        else {
            setTheme(R.style.AppTheme);
            ContentValues contentValues = new ContentValues();
            contentValues.put(DatabaseHelper.THEME, "AppTheme");

            mDatabase.update(DatabaseHelper.THEME_TABLE, contentValues, null, null);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        nightModeCheckBox = findViewById(R.id.nightModeCheckBox);
        nightModeOnOff = findViewById(R.id.nightModeOnOff);

        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            nightModeCheckBox.setChecked(true);
            nightModeOnOff.setText("Night mode is on");
        }

        nightModeCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }
                else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
            }
        });
    }
}