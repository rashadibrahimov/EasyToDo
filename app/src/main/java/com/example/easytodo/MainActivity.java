package com.example.easytodo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private RecyclerView.LayoutManager layoutManager;
    private SQLiteDatabase mDatabase;
    private MyAdapter mAdapter;
    private DatabaseHelper myHelper;
    private RecyclerView recyclerView;
    private Cursor dateCursor;
    private Cursor timeCursor;
    private Cursor titleCursor;
    private int i;
    private int k;
    private int count;

    ImageView todosImageView;
    TextView todosTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        myHelper = new DatabaseHelper(this);
        mDatabase = myHelper.getWritableDatabase();
        Cursor cursor = getAppTheme();
        cursor.moveToFirst();
        String theme = cursor.getString(cursor.getColumnIndex(DatabaseHelper.THEME));
        if (theme.equals("DarkTheme")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            setTheme(R.style.DarkTheme);
        }
        else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new MyAdapter(this, getAllTodos());
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(mAdapter);

        todosImageView = findViewById(R.id.todosImageView);
        todosTextView = findViewById(R.id.todosTextView);

        k = 0;

        check();
        createNotificationChannel();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();

        if (intent.getIntExtra("k", -1) == k) {
            notifyMeAfterSnoozed();
            k++;
        }

        mAdapter.swapCursor(getAllTodos());
    }

    private void check() {
        int count = getAllTodos().getCount();
        if (count == 0) {
            todosImageView.setVisibility(View.VISIBLE);
            todosTextView.setVisibility(View.VISIBLE);
            i = 0;
            dateCursor = null;
            timeCursor = null;
            titleCursor = null;
        } else {
            todosImageView.setVisibility(View.GONE);
            todosTextView.setVisibility(View.GONE);
        }
    }


    public void onAddTodoClicked(View view) {
        startActivityForResult(new Intent(this, AddTodoActivity.class), 1);
    }

    public void add(String title, String date, String time, int color, int year, int month, int day, int hour, int minute) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.TITLE, title);
        contentValues.put(DatabaseHelper.DATE, date);
        contentValues.put(DatabaseHelper.TIME, time);
        contentValues.put(DatabaseHelper.COLOR, color);
        contentValues.put(DatabaseHelper.YEAR, year);
        contentValues.put(DatabaseHelper.MONTH, month);
        contentValues.put(DatabaseHelper.DAY, day);
        contentValues.put(DatabaseHelper.HOUR, hour);
        contentValues.put(DatabaseHelper.MINUTE, minute);

        mDatabase.insert(DatabaseHelper.TABLE_NAME, null, contentValues);
        mAdapter.swapCursor(getAllTodos());
    }

    public void update(String title, String date, String time, long id) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.TITLE, title);
        contentValues.put(DatabaseHelper.DATE, date);
        contentValues.put(DatabaseHelper.TIME, time);

        mDatabase.update(DatabaseHelper.TABLE_NAME, contentValues, DatabaseHelper.ID +  "=" +  id, null);
        mAdapter.swapCursor(getAllTodos());
    }

    public void delete(long id) {
        mDatabase.delete(DatabaseHelper.TABLE_NAME, DatabaseHelper.ID + "=" + id, null);
        mAdapter.swapCursor(getAllTodos());

        check();
    }

    public Cursor getAllTodos() {
        return mDatabase.query(DatabaseHelper.TABLE_NAME, null, null, null, null, null, null);
    }

    public Cursor getAppTheme() {
        return mDatabase.query(DatabaseHelper.THEME_TABLE, null, null, null, null, null, null);
    }

    public String getDate() {
        String date = "";

        Intent intent = getIntent();
        if (intent.hasExtra("spinner")) {
            dateCursor = mDatabase.query(DatabaseHelper.TABLE_NAME, new String[]{DatabaseHelper.DATE}, null, null, null, null, null);
        }

        if (dateCursor.getCount() > 0) {
            dateCursor.moveToPosition(i);
            date = dateCursor.getString(dateCursor.getColumnIndex(DatabaseHelper.DATE));
        }
        return date;
    }

    public String getUpdatedDate(int position) {
        String date = "";

        Intent intent = getIntent();
        if (intent.hasExtra("spinner")) {
            dateCursor = mDatabase.query(DatabaseHelper.TABLE_NAME, new String[]{DatabaseHelper.DATE}, null, null, null, null, null);
        }

        if (dateCursor.getCount() > 0) {
            dateCursor.moveToPosition(position);
            date = dateCursor.getString(dateCursor.getColumnIndex(DatabaseHelper.DATE));
        }
        return date;
    }

    public String getTime() {
        String time = "";

        Intent intent = getIntent();
        if (intent.hasExtra("spinner")) {
            timeCursor = mDatabase.query(DatabaseHelper.TABLE_NAME, new String[]{DatabaseHelper.TIME}, null, null, null, null, null);
        }

        if (timeCursor.getCount() > 0) {
            timeCursor.moveToPosition(i);
            time = timeCursor.getString(timeCursor.getColumnIndex(DatabaseHelper.TIME));
        }
        return time;
    }

    public String getUpdatedTime(int position) {
        String time = "";

        Intent intent = getIntent();
        if (intent.hasExtra("spinner")) {
            timeCursor = mDatabase.query(DatabaseHelper.TABLE_NAME, new String[]{DatabaseHelper.TIME}, null, null, null, null, null);
        }

        if (timeCursor.getCount() > 0) {
            timeCursor.moveToPosition(position);
            time = timeCursor.getString(timeCursor.getColumnIndex(DatabaseHelper.TIME));
        }
        return time;
    }

    public String getItemTitle() {
        String title = "";

        Intent intent = getIntent();
        if (intent.hasExtra("spinner")) {
            titleCursor = mDatabase.query(DatabaseHelper.TABLE_NAME, new String[]{DatabaseHelper.TITLE}, null, null, null, null, null);
        }

        if (titleCursor.getCount() > 0) {
            titleCursor.moveToPosition(i);
            title = titleCursor.getString(titleCursor.getColumnIndex(DatabaseHelper.TITLE));
        }
        return title;
    }

    public String getUpdatedItemTitle(int position) {
        String title = "";

        Intent intent = getIntent();
        if (intent.hasExtra("spinner")) {
            titleCursor = mDatabase.query(DatabaseHelper.TABLE_NAME, new String[]{DatabaseHelper.TITLE}, null, null, null, null, null);
        }

        if (titleCursor.getCount() > 0) {
            titleCursor.moveToPosition(position);
            title = titleCursor.getString(titleCursor.getColumnIndex(DatabaseHelper.TITLE));
        }
        return title;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.about:
                startActivity(new Intent(this, AboutActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1) {
            add(data.getStringExtra("title"), data.getStringExtra("date"), data.getStringExtra("time"),
                    data.getIntExtra("color",0), data.getIntExtra("year", 2020),
                    data.getIntExtra("month", 1), data.getIntExtra("day", 1), data.getIntExtra("hour", 0),
                    data.getIntExtra("minute", 0));

            count = getAllTodos().getCount();

            dateCursor = mDatabase.query(DatabaseHelper.TABLE_NAME, new String[]{DatabaseHelper.DATE}, null, null, null, null, null);
            timeCursor = mDatabase.query(DatabaseHelper.TABLE_NAME, new String[]{DatabaseHelper.TIME}, null, null, null, null, null);
            titleCursor = mDatabase.query(DatabaseHelper.TABLE_NAME, new String[]{DatabaseHelper.TITLE}, null, null, null, null, null);

            i = 0;

            if (getAllTodos().getCount() == 0) {
                i = 0;
            } else {
                while (i < count) {
                    notifyMe();
                    i++;
                }
            }
            check();
        }
        if (resultCode == RESULT_OK && requestCode == 2) {
            update(data.getStringExtra("title"), data.getStringExtra("date"), data.getStringExtra("time"), data.getLongExtra("id", 1));

            count = getAllTodos().getCount();

            dateCursor = mDatabase.query(DatabaseHelper.TABLE_NAME, new String[]{DatabaseHelper.DATE}, null, null, null, null, null);
            timeCursor = mDatabase.query(DatabaseHelper.TABLE_NAME, new String[]{DatabaseHelper.TIME}, null, null, null, null, null);
            titleCursor = mDatabase.query(DatabaseHelper.TABLE_NAME, new String[]{DatabaseHelper.TITLE}, null, null, null, null, null);

            if (getAllTodos().getCount() == 0) {
                i = 0;
            } else {
                stopNotification(data.getIntExtra("position", 0));
                notifyMeAfterUpdated(data.getIntExtra("position", 0));
            }
            check();
        }
    }

    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            delete((Long) viewHolder.itemView.getTag(R.string.id));
        }
    };

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "EasyToDoChannel";
            String description = "Channel for EasyToDo";
            NotificationChannel channel = new NotificationChannel("notify", name, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void notifyMe() {
        Intent intent = new Intent(this, ReminderBroadcast.class);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        String date;
        String time;
        String dateAndTime;
        String title;

        date = getDate();
        time = getTime();
        title = getItemTitle();

        dateAndTime = date + " " + time;

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM, yyyy HH:mm");

        try {
            Date mDate = sdf.parse(dateAndTime);
            long dateTimeInMillis = mDate.getTime();
            intent.putExtra("title", title);
            intent.putExtra("i", i);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, i, intent, 0);

            alarmManager.set(AlarmManager.RTC_WAKEUP, dateTimeInMillis, pendingIntent);

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void notifyMeAfterUpdated(int position) {
        Intent intent = new Intent(this, ReminderBroadcast.class);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        String date;
        String time;
        String dateAndTime;
        String title;

        date = getUpdatedDate(position);
        time = getUpdatedTime(position);
        title = getUpdatedItemTitle(position);

        dateAndTime = date + " " + time;

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM, yyyy HH:mm");

        try {
            Date mDate = sdf.parse(dateAndTime);
            long dateTimeInMillis = mDate.getTime();
            i = position + 1000000000;
            intent.putExtra("updatedTitle", title);
            intent.putExtra("i", i);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, i, intent, 0);

            alarmManager.set(AlarmManager.RTC_WAKEUP, dateTimeInMillis, pendingIntent);

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void notifyMeAfterSnoozed() {

        Intent intent = new Intent(this, ReminderBroadcast.class);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Intent snoozeIntent = getIntent();

        intent.putExtra("title", snoozeIntent.getStringArrayExtra("snoozed_todo_title"));
        //intent.putExtra("count", count);
        intent.putExtra("i", i);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, i, intent, 0);

        if (snoozeIntent.getStringExtra("spinner").equals("10 Minutes")) {
            long tenMinutes = System.currentTimeMillis() + 600000;
            String snoozeDate = getDateOrTime(tenMinutes, "dd MMM, yyyy");
            String snoozedTime = getDateOrTime(tenMinutes, "HH:mm");
            ContentValues valuesTenMinutes = new ContentValues();
            valuesTenMinutes.put(DatabaseHelper.DATE, snoozeDate);
            valuesTenMinutes.put(DatabaseHelper.TIME, snoozedTime);
            mDatabase.update(DatabaseHelper.TABLE_NAME, valuesTenMinutes, DatabaseHelper.TITLE + "='" + snoozeIntent.getStringExtra("snoozed_todo_title") + "'", null);
            alarmManager.set(AlarmManager.RTC_WAKEUP, tenMinutes, pendingIntent);
        }
        else if (snoozeIntent.getStringExtra("spinner").equals("30 Minutes")) {
            long thirtyMinutes = System.currentTimeMillis() + 1800000;
            String snoozeDate = getDateOrTime(thirtyMinutes, "dd MMM, yyyy");
            String snoozedTime = getDateOrTime(thirtyMinutes, "HH:mm");
            ContentValues valuesThirtyMinutes = new ContentValues();
            valuesThirtyMinutes.put(DatabaseHelper.DATE, snoozeDate);
            valuesThirtyMinutes.put(DatabaseHelper.TIME, snoozedTime);
            mDatabase.update(DatabaseHelper.TABLE_NAME, valuesThirtyMinutes, DatabaseHelper.TITLE + "='" + snoozeIntent.getStringExtra("snoozed_todo_title") + "'", null);
            alarmManager.set(AlarmManager.RTC_WAKEUP, thirtyMinutes, pendingIntent);
        }
        else {
            long oneHour = System.currentTimeMillis() + 3600000;
            String snoozeDate = getDateOrTime(oneHour, "dd MMM, yyyy");
            String snoozedTime = getDateOrTime(oneHour, "HH:mm");
            ContentValues valuesOneHour = new ContentValues();
            valuesOneHour.put(DatabaseHelper.DATE, snoozeDate);
            valuesOneHour.put(DatabaseHelper.TIME, snoozedTime);
            mDatabase.update(DatabaseHelper.TABLE_NAME, valuesOneHour, DatabaseHelper.TITLE + "='" + snoozeIntent.getStringExtra("snoozed_todo_title") + "'", null);
            alarmManager.set(AlarmManager.RTC_WAKEUP, oneHour, pendingIntent);
        }
    }

    private void stopNotification(int position) {
        Intent intent = new Intent(this, ReminderBroadcast.class);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        intent.putExtra("i", position);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, position, intent, 0);

        alarmManager.cancel(pendingIntent);

    }

    public static String getDateOrTime(long milliSeconds, String dateFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }
}
