package com.example.focusfox.journal;

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

public class UpdateJournal extends AppCompatActivity implements OnClickListener {

    //Declaring the variables to be used for the activity
    private EditText titleText;
    private Button updateBtn, deleteBtn;
    private EditText contentText;

    private long _id;

    //create an instance of DBManager
    private DBManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Setting XML file for UpdateJournal Activity
        setContentView(R.layout.activity_update_journal);
        //Setting title in title bar to Edit This Entry
        setTitle("Edit This Entry");

        //Open the database connection to update/delete the journal entry
        dbManager = new DBManager(this);
        dbManager.open();

        //Assigning values to the variables
        titleText = (EditText) findViewById(R.id.title_edittext);
        contentText = (EditText) findViewById(R.id.content_edittext);
        updateBtn = (Button) findViewById(R.id.btn_update);
        deleteBtn = (Button) findViewById(R.id.btn_delete);

        //Setting the TextWatcher listener to both editText fields
        titleText.addTextChangedListener(journalUpdateWatcher);
        contentText.addTextChangedListener(journalUpdateWatcher);

        //getting the modify_intent from Journal as well as the journal variables
        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        String title = intent.getStringExtra("title");
        String content = intent.getStringExtra("content");

        _id = Long.parseLong(id);

        //set the editText fields with the variables that were passed in with the intent
        titleText.setText(title);
        contentText.setText(content);

        //initialise onClickListeners for the update and delete buttons
        updateBtn.setOnClickListener(this);
        deleteBtn.setOnClickListener(this);
    }

    //A text watcher is used to ensure that the fields are not null
    //If the fields are empty, the button is disabled and is enabled when there is text in the field
    private TextWatcher journalUpdateWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //Required function for TextWatcher to work
        }

        //When the editText fields aren't empty, the button is re-enabled
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String titleInput = titleText.getText().toString().trim();
            String contentInput = contentText.getText().toString().trim();

            updateBtn.setEnabled(!titleInput.isEmpty() && !contentInput.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {
            //Required function for TextWatcher to work
        }
    };

    //Intent to send user back to journal activity once they have updated/deleted journal entry
    public void returnHome() {
        Intent home_intent = new Intent(getApplicationContext(), Journal.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(home_intent);
    }

    //onClick function for the update and delete buttons
    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            //if user has chosen to update the journal entry
            case R.id.btn_update:
                //Setting database variables to values in editText fields
                String title = titleText.getText().toString();
                String content = contentText.getText().toString();

                //updating title and content of the selected entry
                dbManager.journal_update(_id, title, content);

                //Run the returnHome intent
                this.returnHome();
                break;

            //if user has chosen to delete the entry
            case R.id.btn_delete:
                //run the journal_delete function to remove the entry from the journal table
                dbManager.journal_delete(_id);

                //run the returnHome intent
                this.returnHome();
                break;
        }
        //Close the database connection
        dbManager.close();
    }
}