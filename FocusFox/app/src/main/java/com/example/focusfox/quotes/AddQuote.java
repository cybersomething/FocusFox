package com.example.focusfox.quotes;

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


public class AddQuote extends AppCompatActivity implements View.OnClickListener {

    //Declaring the variables to be used for the activity
    private Button addQuote;
    private EditText quoteEditText;

    //create an instance of DBManager
    private DBManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Setting title in title bar to Add Quote
        setTitle("Add Quote");
        //Setting XML file for AddQuote Activity
        setContentView(R.layout.activity_add_quote);

        //Assigning values to the variables
        quoteEditText = (EditText) findViewById(R.id.content_edittext);
        addQuote = (Button) findViewById(R.id.add_quote);

        //Setting the TextWatcher listener to the quote editText field
        quoteEditText.addTextChangedListener(quoteTextWatcher);

        //Open the database connection to add Quote entry
        dbManager = new DBManager(this);
        dbManager.open();

        //initialise onClickListeners for the addQuote button
        addQuote.setOnClickListener(this);
    }

    //A text watcher is used to ensure that the field is not null
    //If the field is empty, the button is disabled and is enabled when there is text in the field
    private TextWatcher quoteTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //Required function for TextWatcher to work
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String quoteInput = quoteEditText.getText().toString().trim();
            //When the editText field isn't empty, the button is re-enabled
            addQuote.setEnabled(!quoteInput.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {
        //Required function for TextWatcher to work
        }
    };

    //When user clicks the addQuote button, the entry is added to the database and the user is taken to the Journal activity.
    @Override
    public void onClick(View view) {
        //Setting database variables to values in editText fields
        final String quote = quoteEditText.getText().toString();

        //Adding quote into quote table
        dbManager.quote_insert(quote);

        //Intent to send user back to Quote Activity
        Intent main = new Intent(AddQuote.this, Quotes.class)
            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(main);

        //Close the database connection
        dbManager.close();
    }
}
