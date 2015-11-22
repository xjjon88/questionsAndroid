package hk.ust.cse.hunkim.questionroom;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
  

/**
 * A login screen that offers login via email/password.
 */
public class JoinActivity extends Activity {
    public static final String ROOM_NAME = "comp3111";
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    // UI references.
    private TextView roomNameView;

    private ListView mListView;
    private String[] suggestRooms = new String[]{
            "comp3111",
            "comp3511",
            "comp1023",
            "fin1195"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);


        // Set up the login form.
        roomNameView = (TextView) findViewById(R.id.room_name);

        roomNameView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_NULL && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    attemptJoin(textView);
                }
                return true;
            }
        });


        //suggested rooms list //Jonathan Yu
        mListView = (ListView) findViewById(R.id.suggestRoomListView);
        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.suggested_room, R.id.suggestedRoomTextView, suggestRooms);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                roomNameView.setText(suggestRooms[position]);
                Log.i("COMP3111", "setting text: " + suggestRooms[position]);


                attemptJoin((TextView) view.findViewById(R.id.suggestedRoomTextView));


            }
        });


    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptJoin(View view) {
        // Reset errors.
        roomNameView.setError(null);

        // Store values at the time of the login attempt.
        String room_name = roomNameView.getText().toString().toLowerCase();

        boolean cancel = false;

        // Check for a valid email address.
        if (TextUtils.isEmpty(room_name)) {
            roomNameView.setError(getString(R.string.error_field_required));

            cancel = true;
        } else if (!isEmailValid(room_name)) {
            roomNameView.setError(getString(R.string.error_invalid_room_name));
            cancel = true;
        }

        if (cancel) {
            //.roomNameView.setText("");
            roomNameView.requestFocus();
        } else {
            // Start main activity
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(ROOM_NAME, room_name);
            startActivity(intent);
        }
    }

    private boolean isEmailValid(String room_name) {
        // http://stackoverflow.com/questions/8248277
        // Make sure alphanumeric characters
        return !room_name.matches("^.*[^a-zA-Z0-9 ].*$");
    }
}

