package com.example.easytodo;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class SnoozeOrRemoveActivity extends AppCompatActivity {

    Spinner spinner;
    TextView notificationTitle;
    Intent intent;
    int k;
    DatabaseHelper myHelper;
    SQLiteDatabase mDatabase;
    String theme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        myHelper = new DatabaseHelper(this);
        mDatabase = myHelper.getWritableDatabase();
        Cursor cursor = getAppTheme();
        cursor.moveToFirst();
        theme = cursor.getString(cursor.getColumnIndex(DatabaseHelper.THEME));

        if (theme.equals("DarkTheme")) {
            setTheme(R.style.DarkTheme);
            setTheme(R.style.DarkTheme_NoActionBar);
        }
        else {
            setTheme(R.style.AppTheme);
            setTheme(R.style.AppTheme_NoActionBar);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snooze_or_remove);
        k = 0;
        spinner = findViewById(R.id.spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.snooze_array, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        notificationTitle = findViewById(R.id.notificationTitle);

        intent = getIntent();
        notificationTitle.setText(intent.getStringExtra("notification_title"));
    }

    public void onRemoveClicked(View view) {
        mDatabase.delete(DatabaseHelper.TABLE_NAME, DatabaseHelper.TITLE + "='" + notificationTitle.getText().toString() + "'", null);
        startActivity(new Intent(this, MainActivity.class));
    }

    public Cursor getAppTheme() {
        return mDatabase.query(DatabaseHelper.THEME_TABLE, null, null, null, null, null, null);
    }

    public void onSnoozeClicked(View view) {
        if (intent.hasExtra("notification_title")) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("spinner", spinner.getSelectedItem().toString());
            intent.putExtra("snoozed_todo_title", notificationTitle.getText().toString());
            intent.putExtra("k", k);
            k++;
            startActivity(intent);
        }
    }
}