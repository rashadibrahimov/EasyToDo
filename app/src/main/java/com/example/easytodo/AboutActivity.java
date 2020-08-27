package com.example.easytodo;

import androidx.appcompat.app.AppCompatActivity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

public class AboutActivity extends AppCompatActivity {

    private DatabaseHelper myHelper;
    private SQLiteDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        myHelper = new DatabaseHelper(this);
        mDatabase = myHelper.getWritableDatabase();
        Cursor cursor = getAppTheme();
        cursor.moveToFirst();
        String theme = cursor.getString(cursor.getColumnIndex(DatabaseHelper.THEME));

        if (theme.equals("DarkTheme")) {
            setTheme(R.style.DarkTheme);
        }
        else {
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
    }

    public Cursor getAppTheme() {
        return mDatabase.query(DatabaseHelper.THEME_TABLE, null, null, null, null, null, null);
    }
}