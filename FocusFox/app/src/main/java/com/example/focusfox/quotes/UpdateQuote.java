package com.example.focusfox.quotes;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.focusfox.R;
import com.example.focusfox.database.DBManager;

public class UpdateQuote extends AppCompatActivity implements OnClickListener {
    private EditText quoteText;
    private Button updateQuote, deleteQuote;

    private long _id;

    //create an instance of DBManager
    private DBManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //Setting title in title bar to Edit This Quote
        setTitle("Edit This Quote");
        //Setting XML file for UpdateQuote Activity
        setContentView(R.layout.activity_update_quote);

        //Open the database connection to update/delete the chosen quote
        dbManager = new DBManager(this);
        dbManager.open();

        //Assigning values to the variables
        quoteText = (EditText) findViewById(R.id.quote_edittext);
        updateQuote = (Button) findViewById(R.id.quote_update);
        deleteQuote = (Button) findViewById(R.id.quote_delete);

        //Setting the TextWatcher listener to the editText field
        quoteText.addTextChangedListener(quoteUpdateWatcher);

        //getting the modify_intent from Quotes as well as the quote variables
        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        String quote = intent.getStringExtra("quote");

        _id = Long.parseLong(id);

        //set the editText field with the variable that was passed in with the intent
        quoteText.setText(quote);

        //initialise onClickListeners for the update and delete buttons
        updateQuote.setOnClickListener(this);
        deleteQuote.setOnClickListener(this);
    }

    //A text watcher is used to ensure that the fields are not null
    //If the field is empty, the button is disabled and is enabled when there is text in the field
    private TextWatcher quoteUpdateWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //Required function for TextWatcher to work
        }

        //When the editText field isn't empty, the button is re-enabled
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String quoteUpdate = quoteText.getText().toString().trim();
            updateQuote.setEnabled(!quoteUpdate.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {
            //Required function for TextWatcher to work
        }
    };

    //Intent to send user back to quote activity once they have updated/deleted the quote
    public void returnHome(){
        Intent home_intent = new Intent(getApplicationContext(), Quotes.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(home_intent);
    }

    //onClick function for the update and delete buttons
    @Override
    public void onClick(View view){
        switch (view.getId()){
            //if user has chosen to update the quote
            case R.id.quote_update:
                //Setting database variable to quote value in editText field
                String quote = quoteText.getText().toString();

                    //updating title and content of the selected quote
                    dbManager.quote_update(_id, quote);

                    //Run the returnHome intent
                    this.returnHome();
                break;

            //if user has chosen to delete the quote
            case R.id.quote_delete:
                //run the quote_delete function to remove the quote from the quote table
                dbManager.quote_delete(_id);

                //Run the returnHome intent
                this.returnHome();
                break;
        }
        //close the database connection
        dbManager.close();
    }

}
