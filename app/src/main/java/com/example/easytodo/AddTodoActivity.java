package com.example.easytodo;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;
import java.util.Calendar;

public class AddTodoActivity extends AppCompatActivity {
    Switch remindMeSwitch;
    LinearLayout dateTimeLayout;
    TextView reminderSetFor;
    TextView at;
    EditText title;
    EditText date;
    EditText time;
    String theme;

    private int year;
    private int month;
    private int day;

    private int hour;
    private int minute;

    private int color;

    private DatabaseHelper myHelper;
    private SQLiteDatabase mDatabase;

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
        setContentView(R.layout.activity_add_todo);

        remindMeSwitch = findViewById(R.id.remindMeSwitch);
        dateTimeLayout = findViewById(R.id.dateTimeLayout);
        reminderSetFor = findViewById(R.id.reminderSetFor);
        at = findViewById(R.id.at);
        title = findViewById(R.id.addTitle);

        title.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        date = findViewById(R.id.date);
        time = findViewById(R.id.time);

        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);

        date.setText(day + " " + DisplayMonthInCharacters(month) + ", " + year);
        time.setText(displayHour(hour) + ":" + displayMinute(minute));

        reminderSetFor.setText("Reminder set for " + date.getText() + ", " + time.getText());

        color = ColorGenerator.MATERIAL.getRandomColor();

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        final int action = ev.getActionMasked();

        int[] location = new int[2];
        title.getLocationOnScreen(location);
        mRect.left = location[0];
        mRect.top = location[1];
        mRect.right = location[0] + title.getWidth();
        mRect.bottom = location[1] + title.getHeight();

        int x = (int) ev.getX();
        int y = (int) ev.getY();

        if (action == MotionEvent.ACTION_DOWN && !mRect.contains(x, y)) {
            InputMethodManager input = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            input.hideSoftInputFromWindow(title.getWindowToken(), 0);
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

            date.setText(day + " " + DisplayMonthInCharacters(month) + ", " + year);
            reminderSetFor.setText("Reminder set for " + date.getText() + ", " + time.getText());
        }
    };

    TimePickerDialog.OnTimeSetListener onTime = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePickerDialog view, int selectedHour, int selectedMinute, int selectedSecond) {
            hour = selectedHour;
            minute = selectedMinute;

            time.setText(displayHour(hour) + ":" + displayMinute(minute));
            reminderSetFor.setText("Reminder set for " + date.getText() + ", " + time.getText());
        }
    };

    public void onCloseClicked(View view) {
        finish();
    }

    public void onSendClicked(View view) {
        String titleText = title.getText().toString();
        String dateText = date.getText().toString();
        String timeText = time.getText().toString();
        Intent data = new Intent();
        data.putExtra("title", titleText);

        if (remindMeSwitch.isChecked()) {
            data.putExtra("date", dateText);
            data.putExtra("time", timeText);
            data.putExtra("year", year);
            data.putExtra("month", month);
            data.putExtra("day", day);
            data.putExtra("hour", hour);
            data.putExtra("minute", minute);
        }

        data.putExtra("color", color);

        setResult(RESULT_OK, data);

        finish();
    }

    public void onSwitchClicked(View view) {

        if (!(remindMeSwitch.isChecked()) && dateTimeLayout.getVisibility() == View.VISIBLE && reminderSetFor.getVisibility() == View.VISIBLE) {
            dateTimeLayout.setVisibility(View.INVISIBLE);
            reminderSetFor.setVisibility(View.INVISIBLE);
        }

        if (remindMeSwitch.isChecked() && dateTimeLayout.getVisibility() == View.INVISIBLE && dateTimeLayout.getVisibility() == View.INVISIBLE) {
            dateTimeLayout.setVisibility(View.VISIBLE);
            reminderSetFor.setVisibility(View.VISIBLE);
            at.setText("@");
        }
    }

    public void onDateClicked(View view) {
        showDatePicker();
    }

    public void onTimeClicked(View view) {
        showTimePicker();
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

    public Cursor getAppTheme() {
        return mDatabase.query(DatabaseHelper.THEME_TABLE, null, null, null, null, null, null);
    }
}