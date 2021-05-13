package com.example.focusfox.journal;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.focusfox.R;
import com.example.focusfox.database.DBManager;
import com.example.focusfox.database.DatabaseHelper;

public class Journal extends AppCompatActivity {

    //create an instance of DBManager
    private DBManager dbManager;

    //Declaring the variables to be used for the activity
    private ListView listView;
    Cursor cursor;
    private SimpleCursorAdapter adapter;
    final String[] from = new String[]{
            DatabaseHelper._ID, DatabaseHelper.TITLE, DatabaseHelper.CONTENT
    };
    final int[] to = new int[]{
            R.id.id, R.id.title, R.id.content
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Setting XML file for journal Activity
        setContentView(R.layout.activity_journal);
        //Setting title in title bar to FocusFox - Journal
        setTitle("FocusFox - Journal");

        //Open the database connection to get journal entries
        dbManager = new DBManager(this);
        dbManager.open();

        //Get each journal entry from the database
        cursor = dbManager.journal_fetch();

        //Initialising the listView, if there are no entries the empty placeholder is displayed.
        listView = findViewById(R.id.list_view);
        listView.setEmptyView(findViewById(R.id.empty));

        //set the adapter to grab the journal entries
        adapter = new SimpleCursorAdapter(this, R.layout.activity_view_entry, cursor, from, to, 0);
        adapter.notifyDataSetChanged();

        //Add each journal entry to the listView
        listView.setAdapter(adapter);

        //When a journal entry is clicked, the user is able to edit or delete the entry
        listView.setOnItemClickListener((AdapterView.OnItemClickListener) (parent, view, position, viewId) -> {
            //Displaying the id, title and the content of the entry within the listview.
            TextView idTextView = (TextView) view.findViewById(R.id.id);
            TextView titleTextView = (TextView) view.findViewById(R.id.title);
            TextView contentTextView = (TextView) view.findViewById(R.id.content);

            //setting database variables to the values of the textView fields
            String id = idTextView.getText().toString();
            String title = titleTextView.getText().toString();
            String content = contentTextView.getText().toString();

            //Declaring the modify_intent and passing the journal entry and id to the updateJournal class
            Intent modify_intent = new Intent(getApplicationContext(), UpdateJournal.class);
            modify_intent.putExtra("title", title);
            modify_intent.putExtra("content", content);
            modify_intent.putExtra("id", id);

            startActivity(modify_intent);
        });
    }

    //This function is used to allow the user to search for journal entries.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar
        getMenuInflater().inflate(R.menu.menu, menu);

        //Initialising the searchmanager and searchview for the Journal activity
        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.searchView).getActionView();
        searchView.setSearchableInfo(manager.getSearchableInfo(getComponentName()));

        //Creating the on query listener for the search view
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String entryTitle) {
                cursor = dbManager.searchJournal(entryTitle);
                //If the cursor is empty, display a no record toast
                if (cursor == null) {
                    Toast.makeText(Journal.this, "No records found!", Toast.LENGTH_LONG).show();
                } else {
                    //if the cursor has entries, display the number of entries found
                    Toast.makeText(Journal.this, cursor.getCount() + " records found!", Toast.LENGTH_LONG).show();
                }
                //Set the listView to the entry(ies) within the cursor
                adapter.swapCursor(cursor);
                return false;
            }

            //Each time the searchView is changed, check for entries with current value
            @Override
            public boolean onQueryTextChange(String entryTitle) {
                cursor = dbManager.searchJournal(entryTitle);
                //if the cursor contains entries, display them in the listview
                if (cursor != null) {
                    adapter.swapCursor(cursor);
                }
                return false;
            }

        });
        return true;
    }


    //This function is for the menu options to add entries and to delete all the entries
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //If Add Entry is selected, take the user to the add entry class
        if (id == R.id.add_entry) {
            Intent add_entry = new Intent(this, AddEntry.class);
            startActivity(add_entry);
        }
        //if Delete All Entries is selected, display an alert dialog to confirm deletion
        else if (id == R.id.deleteEntries){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(true);
            builder.setTitle("Delete All Entries?");
            builder.setMessage("This cannot be undone");

            //If user confirms deletion of entries, delete all entries within table and confirm with toast message.
            builder.setPositiveButton("Confirm",
                    (dialog, which) -> {
                        dbManager.journal_delete_all();
                        Intent home_intent = new Intent(getApplicationContext(), Journal.class)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(home_intent);
                        Toast.makeText(getApplicationContext(), "Entries have been successfully deleted", Toast.LENGTH_SHORT).show();
                    });

            //If user clicks cancel, close dialog and display toast to confirm that no entries were deleted.
            builder.setNegativeButton("Cancel", (dialog, which) -> Toast.makeText(getApplicationContext(), "No entries were deleted.", Toast.LENGTH_SHORT).show());

            AlertDialog dialog = builder.create();
            dialog.show();
        }
        return super.onOptionsItemSelected(item);
    }
}