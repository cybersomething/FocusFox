package com.example.focusfox.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper{
    //Creating table names for the journal and quote tables.
    public static final String JOURNAL_TABLE_NAME = "JournalEntries";
    public static final String QUOTE_TABLE_NAME = "QuoteTable";

    //creating the ID variable for each table.
    public static final String _ID = "_id";

    //Title and Content variables for the journal table.
    public static final String TITLE = "title";
    public static final String CONTENT = "content";

    //Quote variable for the quote table.
    public static final String QUOTE = "quote";

    //Setting Database name to FOCUSFOX.DB
    static final String DB_NAME = "FOCUSFOX.DB";

    //Setting DB version to version 8.
    static final int DB_VERSION = 8;

    //SQL queries to create tables being set to CREATE_TABLE variables.
    private static final String JOURNAL_CREATE_TABLE = "create table " + JOURNAL_TABLE_NAME + "(" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TITLE + " TEXT NOT NULL, " + CONTENT + " TEXT)";
    private static String QUOTE_CREATE_TABLE = "CREATE TABLE " + QUOTE_TABLE_NAME + " (" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + QUOTE + " TEXT NOT NULL)";

    //Creating a helper object to create/open/manage the database.
    public DatabaseHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    //creating tables for journal and quote activities
    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(JOURNAL_CREATE_TABLE);
        db.execSQL(QUOTE_CREATE_TABLE);
    }

    //Dropping old tables and creating new ones when onUpgrade is called.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS " + JOURNAL_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + QUOTE_TABLE_NAME);
        onCreate(db);
    }
}