package com.example.easytodo;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

public class UpdateTodoActivity extends AppCompatActivity {

    Intent intent;
    EditText titleUpdate;
    DatabaseHelper myHelper;
    SQLiteDatabase mDatabase;
    Switch remindMeSwitchUpdate;
    LinearLayout dateTimeLayoutUpdate;
    TextView reminderSetForUpdate;
    TextView atUpdate;
    EditText dateUpdate;
    EditText timeUpdate;
    String theme;

    private int year;
    private int month;
    private int day;

    private int hour;
    private int minute;

    private Rect mRect = new Rect();

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
        setContentView(R.layout.activity_update_todo);

        intent = getIntent();

        titleUpdate = findViewById(R.id.titleUpdate);
        titleUpdate.setText(intent.getStringExtra("itemTitle"));
        titleUpdate.setSelection(titleUpdate.getText().length());
        titleUpdate.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        titleUpdate.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        myHelper = new DatabaseHelper(this);
        mDatabase = myHelper.getWritableDatabase();

        remindMeSwitchUpdate = findViewById(R.id.remindMeSwitchUpdate);
        dateTimeLayoutUpdate = findViewById(R.id.dateTimeLayoutUpdate);
        reminderSetForUpdate = findViewById(R.id.reminderSetForUpdate);
        atUpdate = findViewById(R.id.atUpdate);

        dateUpdate = findViewById(R.id.dateUpdate);
        timeUpdate = findViewById(R.id.timeUpdate);

        year = intent.getIntExtra("year", 2020);
        month = intent.getIntExtra("month", 1);
        day = intent.getIntExtra("day", 1);

        hour = intent.getIntExtra("hour", 0);
        minute = intent.getIntExtra("minute", 0);

        if (intent.hasExtra("date") && intent.hasExtra("time")) {
            remindMeSwitchUpdate.setChecked(true);
            dateUpdate.setText(intent.getStringExtra("date"));
            timeUpdate.setText(intent.getStringExtra("time"));
            dateTimeLayoutUpdate.setVisibility(View.VISIBLE);
            reminderSetForUpdate.setVisibility(View.VISIBLE);
            atUpdate.setText("@");
        }
        else {
            dateUpdate.setText(day + " " + DisplayMonthInCharacters(month) + ", " + year);
            timeUpdate.setText(displayHour(hour) + ":" + displayMinute(minute));

            reminderSetForUpdate.setText("Reminder set for " + dateUpdate.getText() + ", " + timeUpdate.getText());
        }

        reminderSetForUpdate.setText("Reminder set for " + dateUpdate.getText() + ", " + timeUpdate.getText());
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        final int action = ev.getActionMasked();

        int[] location = new int[2];
        titleUpdate.getLocationOnScreen(location);
        mRect.left = location[0];
        mRect.top = location[1];
        mRect.right = location[0] + titleUpdate.getWidth();
        mRect.bottom = location[1] + titleUpdate.getHeight();

        int x = (int) ev.getX();
        int y = (int) ev.getY();

        if (action == MotionEvent.ACTION_DOWN && !mRect.contains(x, y)) {
            InputMethodManager input = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            input.hideSoftInputFromWindow(titleUpdate.getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }

    private void showDatePicker() {
        DatePickerDialog date = DatePickerDialog.newInstance(onDate, year, month, day);

        if (theme.equals("DarkTheme")) {
            date.setThemeDark(true);
        }
        date.show(getSupportFragmentManager(), "Date Picker");
    }

    private void showTimePicker() {
        TimePickerDialog time = TimePickerDialog.newInstance(onTime, hour, minute, true);
        if (theme.equals("DarkTheme")) {
            time.setThemeDark(true);
        }
        time.show(getSupportFragmentManager(), "Time Picker");
    }

    DatePickerDialog.OnDateSetListener onDate = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePickerDialog view, int selectedYear, int selectedMonth, int selectedDay) {
            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;

            dateUpdate.setText(day + " " + DisplayMonthInCharacters(month) + ", " + year);
            reminderSetForUpdate.setText("Reminder set for " + dateUpdate.getText() + ", " + timeUpdate.getText());
        }
    };

    TimePickerDialog.OnTimeSetListener onTime = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePickerDialog view, int selectedHour, int selectedMinute, int selectedSecond) {
            hour = selectedHour;
            minute = selectedMinute;

            timeUpdate.setText(displayHour(hour) + ":" + displayMinute(minute));
            reminderSetForUpdate.setText("Reminder set for " + dateUpdate.getText() + ", " + timeUpdate.getText());
        }
    };


    public void onCloseClicked(View view) {
        finish();
    }

    public void onSendClicked(View view) {
        String titleText = titleUpdate.getText().toString();
        String dateText = dateUpdate.getText().toString();
        String timeText = timeUpdate.getText().toString();
        Intent data = new Intent();

        data.putExtra("title", titleText);

        if (remindMeSwitchUpdate.isChecked()) {
            data.putExtra("date", dateText);
            data.putExtra("time", timeText);
        }
        if (!(remindMeSwitchUpdate.isChecked())) {
            data.putExtra("date", (String) null);
            data.putExtra("time", (String) null);
        }

        data.putExtra("id", intent.getLongExtra("itemID", 1));
        data.putExtra("position", intent.getIntExtra("itemPosition", 0));

        setResult(RESULT_OK, data);

        finish();
    }

    public void onUpdateSwitchClicked(View view) {

        if (!(remindMeSwitchUpdate.isChecked()) && dateTimeLayoutUpdate.getVisibility() == View.VISIBLE && reminderSetForUpdate.getVisibility() == View.VISIBLE) {
            dateTimeLayoutUpdate.setVisibility(View.INVISIBLE);
            reminderSetForUpdate.setVisibility(View.INVISIBLE);
        }

        if (remindMeSwitchUpdate.isChecked() && dateTimeLayoutUpdate.getVisibility() == View.INVISIBLE && dateTimeLayoutUpdate.getVisibility() == View.INVISIBLE) {
            dateTimeLayoutUpdate.setVisibility(View.VISIBLE);
            reminderSetForUpdate.setVisibility(View.VISIBLE);
            atUpdate.setText("@");
        }
    }

    public String DisplayMonthInCharacters(int month) {
        switch (month) {
            case 0:
                return "Jan";
            case 1:
                return "Feb";
            case 2:
                return "Mar";
            case 3:
                return "Apr";
            case 4:
                return "May";
            case 5:
                return "Jun";
            case 6:
                return "Jul";
            case 7:
                return "Aug";
            case 8:
                return "Sep";
            case 9:
                return "Oct";
            case 10:
                return "Nov";
            case 11:
                return "Dec";
            default:
                return "";
        }
    }

    public String displayHour(int hour) {
        if(hour < 10) {
            return "0" + hour;
        }
        return String.valueOf(hour);
    }

    public String displayMinute(int minute) {
        if(minute < 10) {
            return "0" + minute;
        }
        return String.valueOf(minute);
    }

    public void onDateClickedUpdate(View view) {
        showDatePicker();
    }

    public void onTimeClickedUpdate(View view) {
        showTimePicker();
    }

    public Cursor getAppTheme() {
        return mDatabase.query(DatabaseHelper.THEME_TABLE, null, null, null, null, null, null);
    }
}