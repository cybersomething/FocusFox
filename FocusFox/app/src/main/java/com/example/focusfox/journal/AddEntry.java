package com.example.focusfox.journal;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.focusfox.R;
import com.example.focusfox.database.DBManager;

public class AddEntry extends AppCompatActivity implements View.OnClickListener{

    //Declaring the variables to be used for the activity
    private Button addEntryButton;
    private EditText titleEditText;
    private EditText contentEditText;

    //create an instance of DBManager
    private DBManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Setting title in title bar to Add New Journal Entry
        setTitle("Add New Journal Entry");
        //Setting XML file for AddEntry Activity
        setContentView(R.layout.activity_add_entry);

        //Assigning values to the variables
        titleEditText = (EditText) findViewById(R.id.title_edittext);
        contentEditText = (EditText) findViewById(R.id.content_edittext);
        addEntryButton = (Button) findViewById(R.id.add_entry);

        //Setting the TextWatcher listener to both editText fields
        titleEditText.addTextChangedListener(journalTextWatcher);
        contentEditText.addTextChangedListener(journalTextWatcher);

        //Open the database connection to add Journal entry
        dbManager = new DBManager(this);
        dbManager.open();

        //initialise onClickListeners for the addEntry button
        addEntryButton.setOnClickListener(this);
    }

    //A text watcher is used to ensure that the fields are not null
    //If the fields are empty, the button is disabled and is enabled when there is text in the field
    private TextWatcher journalTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //Required function for TextWatcher to work
        }

        //When the editText fields aren't empty, the button is re-enabled
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String titleInput = titleEditText.getText().toString().trim();
            String contentInput = contentEditText.getText().toString().trim();

            addEntryButton.setEnabled(!titleInput.isEmpty() && !contentInput.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {
        //Required function for TextWatcher to work
        }
    };

    //When user clicks the add entry button, the entry is added to the database and the user is taken to the Journal activity.
    @Override
    public void onClick(View view){
        //Setting database variables to values in editText fields
        final String title = titleEditText.getText().toString();
        final String content = contentEditText.getText().toString();

        //Adding title and content as an entry into journal table
        dbManager.journal_insert(title, content);

        //Intent to send user back to Journal Activity
        Intent main = new Intent(AddEntry.this, Journal.class)
            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(main);

        //Close the database connection
        dbManager.close();
    }
}