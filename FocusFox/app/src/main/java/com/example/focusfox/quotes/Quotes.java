package com.example.focusfox.quotes;

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

public class Quotes extends AppCompatActivity {

    //create an instance of DBManager
    private DBManager dbManager;

    //Declaring the variables to be used for the activity
    Cursor quoteCursor;
    private ListView listView;
    private SimpleCursorAdapter quoteAdapter;
    final String[] from  = new String[] {
            DatabaseHelper._ID, DatabaseHelper.QUOTE
    };
    final int[] to = new int[] {
            R.id.quote_id, R.id.quotes
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Setting XML file for Quote Activity
        setContentView(R.layout.activity_quotes);
        //Setting title in title bar to FocusFox - Quotes
        setTitle("FocusFox - Quotes");

        //Open the database connection to get quotes from the table
        dbManager = new DBManager(this);
        dbManager.open();

        //Get each quote from the database
        quoteCursor = dbManager.quote_fetch();

        //Initialising the listView, if there are no quotes the empty placeholder is displayed.
        listView = (ListView) findViewById(R.id.quote_list_view);
        listView.setEmptyView(findViewById(R.id.quote_empty));

        //set the adapter to grab the quotes
        quoteAdapter = new SimpleCursorAdapter(this, R.layout.activity_view_quotes, quoteCursor, from, to, 0);
        quoteAdapter.notifyDataSetChanged();

        //Add each quote entry to the listView
        listView.setAdapter(quoteAdapter);

        //When a quote is clicked, the user is able to edit or delete the entry
        listView.setOnItemClickListener((AdapterView.OnItemClickListener) (parent, view, position, viewId) -> {
            //Displaying the id and quote within the listview.
            TextView idTextView = (TextView) view.findViewById(R.id.quote_id);
            TextView quoteTextView = (TextView) view.findViewById(R.id.quotes);

            //setting quote variables to the values of the textView fields
            String id = idTextView.getText().toString();
            String quote = quoteTextView.getText().toString();

            //Declaring the modify_intent and passing the id and quote to the UpdateQuote class
            Intent modify_intent = new Intent(getApplicationContext(), UpdateQuote.class);
            modify_intent.putExtra("quote", quote);
            modify_intent.putExtra("id", id);

            startActivity(modify_intent);
        });
    }

    //This function is used to allow the user to search for quotes.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar
        getMenuInflater().inflate(R.menu.quote_menu, menu);

        //Initialising the searchmanager and searchview for the quote activity
        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.searchView).getActionView();
        searchView.setSearchableInfo(manager.getSearchableInfo(getComponentName()));

        //Creating the onQueryListener for the search view
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String quote) {
                quoteCursor = dbManager.searchQuotes(quote);
                //If the cursor is empty, display a no record toast
                if (quoteCursor == null) {
                    Toast.makeText(Quotes.this, "No records found!", Toast.LENGTH_LONG).show();
                }
                //if the cursor has quotes, display the number of quotes found
                else {
                    Toast.makeText(Quotes.this, quoteCursor.getCount() + " records found!", Toast.LENGTH_LONG).show();
                }
                //Set the listView to the quote(s) within the cursor
                quoteAdapter.swapCursor(quoteCursor);
                return false;
            }

            //Each time the searchView is changed, check for quotes with current value
            @Override
            public boolean onQueryTextChange(String entryTitle) {
                quoteCursor = dbManager.searchQuotes(entryTitle);
                //if the cursor contains quotes, display them in the listview
                if (quoteCursor != null) {
                    quoteAdapter.swapCursor(quoteCursor);
                }
                return false;
            }

        });
        return true;
    }

    //This function is for the menu options to add quotes and to delete all the quotes
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //If Add Quote is selected, take the user to the AddQuote class
        if (id == R.id.add_quote) {
            //Intent to take user to addQuote class
            Intent add_quote = new Intent(this, AddQuote.class);
            startActivity(add_quote);
        }
        //if Delete All Quotes is selected, display an alert dialog to confirm deletion
        else if (id == R.id.delete_quotes){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(true);
            builder.setTitle("Delete All Quotes?");
            builder.setMessage("This cannot be undone");
            //If user confirms deletion of quotes, delete all quotes within table and confirm with toast message.
            builder.setPositiveButton("Confirm",
                    (dialog, which) -> {
                        dbManager.quote_delete_all();
                        Intent home_intent = new Intent(getApplicationContext(), Quotes.class)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(home_intent);
                        Toast.makeText(getApplicationContext(), "Quotes have been successfully deleted", Toast.LENGTH_LONG).show();
                    });
            //If user clicks cancel, close dialog and display toast to confirm that no quotes were deleted.
            builder.setNegativeButton("Cancel", (dialog, which) -> Toast.makeText(getApplicationContext(), "No quotes were deleted.", Toast.LENGTH_LONG).show());

            AlertDialog dialog = builder.create();
            dialog.show();
        }
        return super.onOptionsItemSelected(item);
    }
}
