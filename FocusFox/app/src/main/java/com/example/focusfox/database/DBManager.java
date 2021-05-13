package com.example.focusfox.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DBManager {
    private DatabaseHelper dbHelper;
    private Context context;
    private SQLiteDatabase database;

    public DBManager(Context c){
        context = c;
    }

    //Function for opening the database
    public DBManager open() throws  SQLException{
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    //Function for closing the database.
    public void close(){
        dbHelper.close();
    }

    //Function to insert the title and content from the AddEntry activity
    public void journal_insert(String title, String content){
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.TITLE, title);
        contentValue.put(DatabaseHelper.CONTENT, content);
        database.insert(DatabaseHelper.JOURNAL_TABLE_NAME, null, contentValue);
    }

    //Function to grab all the journal entries from the journal table.
    public Cursor journal_fetch(){
        String[] columns = new String[] { DatabaseHelper._ID, DatabaseHelper.TITLE, DatabaseHelper.CONTENT };
        Cursor cursor = database.query(DatabaseHelper.JOURNAL_TABLE_NAME, columns, null, null, null, null, null);
        if (cursor != null){
            cursor.moveToFirst();
        }
        return cursor;
    }

    //Function to update the title and content using the entry ID as an identifier.
    public int journal_update(long _id, String title, String content){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.TITLE, title);
        contentValues.put(DatabaseHelper.CONTENT, content);
        int i = database.update(DatabaseHelper.JOURNAL_TABLE_NAME, contentValues, DatabaseHelper._ID + " = " + _id, null);
        return i;
    }

    //Function to search the Journal for the entered variable in the search bar of the Journal activity
    public Cursor searchJournal(String entryTitle) {
        //Using a raw query instead of database.query
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + DatabaseHelper.JOURNAL_TABLE_NAME + " WHERE " + DatabaseHelper.TITLE + " LIKE '%" + entryTitle + "%'";
        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor == null) {
            return null;
        } else if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        return cursor;
    }

    //Function to delete the journal entry associated with the given ID
    public void journal_delete(long _id){
        database.delete(DatabaseHelper.JOURNAL_TABLE_NAME, DatabaseHelper._ID + " = " + _id, null);
    }

    //Function to delete all the entries within the journal table
    public void journal_delete_all(){
        database.delete(DatabaseHelper.JOURNAL_TABLE_NAME, null, null);
        dbHelper.close();
    }

    //Function to insert a new quote entry into the quote table
    public void quote_insert(String quote){
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.QUOTE, quote);
        database.insert(DatabaseHelper.QUOTE_TABLE_NAME, null, contentValue);
    }

    //Generate a random quote from the quote table to be displayed on the main activity screen
    public String getRandomData() {
        String[] columns = new String[] { DatabaseHelper.QUOTE };
        Cursor cursor = database.query(DatabaseHelper.QUOTE_TABLE_NAME + " ORDER BY RANDOM() LIMIT 1", new String[] {DatabaseHelper.QUOTE}, null, null, null,null,null);
        if (cursor.moveToFirst()){
            return cursor.getString(cursor.getColumnIndex(DatabaseHelper.QUOTE));
        }
        return "No Entries";
    }

    //Function to display all entries within the quote table in the quote Activity screen
    public Cursor quote_fetch(){
        String[] columns = new String[] { DatabaseHelper._ID, DatabaseHelper.QUOTE };
        Cursor cursor = database.query(DatabaseHelper.QUOTE_TABLE_NAME, columns, null, null, null, null, null);
        if (cursor != null){
            cursor.moveToFirst();
        }
        return cursor;
    }

    //Function to update the quote using the quote_id as an identifier
    public int quote_update(long quote_id, String quote){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.QUOTE, quote);
        int i = database.update(DatabaseHelper.QUOTE_TABLE_NAME, contentValues, DatabaseHelper._ID + " = " + quote_id, null);
        return i;
    }

    //delete a quote using the quote_id as an identifier
    public void quote_delete(long quote_id){
        database.delete(DatabaseHelper.QUOTE_TABLE_NAME, DatabaseHelper._ID + " = " + quote_id, null);
    }

    //Function to delete all the quotes within the quote table
    public void quote_delete_all(){
        database.delete(DatabaseHelper.QUOTE_TABLE_NAME, null, null);
        dbHelper.close();
    }

    //Function to search the table with the entered quote from the search bar in the quote activity
    public Cursor searchQuotes(String quote) {
        //Open connection to read only
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + DatabaseHelper.QUOTE_TABLE_NAME + " WHERE " + DatabaseHelper.QUOTE + " LIKE '%" + quote + "%'";
        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor == null) {
            return null;
        } else if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        return cursor;
    }

    //Function to count the number of entries in the quote table, used to display the quote guidance message to the user in Main Activity Screen
    public int quote_count(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        int count = (int) DatabaseUtils.queryNumEntries(db, DatabaseHelper.QUOTE_TABLE_NAME);
        return count;
    }
}