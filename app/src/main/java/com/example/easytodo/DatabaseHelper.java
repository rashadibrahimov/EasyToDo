package com.example.easytodo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "TODOS";

    public static final String THEME_TABLE = "THEME";

    public static final String ID = "id";
    public static final String TITLE = "title";
    public static final String DATE = "date";
    public static final String TIME = "time";
    public static final String COLOR = "color";
    public static final String YEAR = "year";
    public static final String MONTH = "month";
    public static final String DAY = "day";
    public static final String HOUR = "hour";
    public static final String MINUTE = "minute";

    public static final String THEME = "theme";

    static final String DB_NAME = "easyToDo.DB";

    static final int DB_VERSION = 1;

    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            +  TITLE + " TEXT NOT NULL, " + DATE + " TEXT, " + TIME + " TEXT, " + COLOR + " INTEGER NOT NULL, " + YEAR + " INTEGER, " + MONTH +
            " INTEGER, " + DAY + " INTEGER, " + HOUR + " INTEGER, " + MINUTE + " INTEGER);";

    private static final String CREATE_THEME_TABLE = "CREATE TABLE " + THEME_TABLE + "(" + THEME + " TEXT NOT NULL);";

    private static final String INSERT_THEME = "INSERT INTO " + THEME_TABLE + "(" + THEME + ")" + "VALUES('AppTheme');";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
        db.execSQL(CREATE_THEME_TABLE);
        db.execSQL(INSERT_THEME);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}