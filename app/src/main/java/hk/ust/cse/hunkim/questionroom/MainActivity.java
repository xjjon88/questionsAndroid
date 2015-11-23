package hk.ust.cse.hunkim.questionroom;

import android.app.ListActivity;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.Comparator;

import hk.ust.cse.hunkim.questionroom.db.DBHelper;
import hk.ust.cse.hunkim.questionroom.db.DBUtil;
import hk.ust.cse.hunkim.questionroom.question.Question;
import hk.ust.cse.hunkim.questionroom.question.Reply;

public class MainActivity extends ListActivity {

    // TODO: change this to your own Firebase URL
    private static final String FIREBASE_URL = "https://xjquestions.firebaseio.com/";

    private String roomName;
    private Firebase mFirebaseRef;
    private ValueEventListener mConnectedListener;
    private QuestionListAdapter mChatListAdapter;

    private Toolbar toolbar;

    private DBUtil dbutil;

    public DBUtil getDbutil() {
        return dbutil;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (JoinActivity.mode ==0) {
            setTheme(R.style.Theme_Night);
        } else if (JoinActivity.mode ==1) {
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);

        //initialized once with an Android context.
        Firebase.setAndroidContext(this);

        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        assert (intent != null);

        // Make it a bit more reliable
        roomName = intent.getStringExtra(JoinActivity.ROOM_NAME);
        if (roomName == null || roomName.length() == 0) {
            roomName = "all";
        }

        setTitle("Room name: " + roomName);

        //setup title // Jonathan Yu
        TextView mRoomTitleTextView = (TextView) findViewById(R.id.roomTitle);
        mRoomTitleTextView.setText(roomName);

        // Setup our Firebase mFirebaseRef
        mFirebaseRef = new Firebase(FIREBASE_URL).child(roomName).child("questions");

        //Adds room title attribute to each room //Jonathan Yu
        Firebase mFBTitle = new Firebase(FIREBASE_URL).child(roomName);
        mFBTitle.child("roomTitle").setValue(roomName);

        // Setup our input methods. Enter key on the keyboard or pushing the send button
        EditText inputText = (EditText) findViewById(R.id.messageInput);
        inputText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_NULL && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    sendMessage();
                }
                return true;
            }
        });

        findViewById(R.id.sendButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

        // get the DB Helper
        DBHelper mDbHelper = new DBHelper(this);
        dbutil = new DBUtil(mDbHelper);
        
        Spinner spinner = (Spinner) findViewById(R.id.sortSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sort_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        addListenerOnSpinnerItemSelection();

    }

    // Add listener for sorting
    public void addListenerOnSpinnerItemSelection() {
        Spinner spinner = (Spinner) findViewById(R.id.sortSpinner);
        spinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }

    public class CustomOnItemSelectedListener implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {
            switch (parent.getSelectedItemPosition()) {
                case 0:
                    setSorting(Question.timeComparator);
                    break;
                case 1:
                    setSorting(Question.echoComparator);
                    break;
                case 2:
                    setSorting(Question.nechoComparator);
                    break;
            }
        }
        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }

    public void setSorting(Comparator<Question> Comparator){
        Question.sortingComparator = Comparator;
        ListView listView = getListView();
        mChatListAdapter.cleanup();
        mChatListAdapter = new QuestionListAdapter(
                mFirebaseRef.orderByChild("timestamp").limitToLast(200),
                this, R.layout.question, roomName);
        listView.setAdapter(mChatListAdapter);
    }


    @Override
    public void onStart() {
        super.onStart();

        // Setup our view and list adapter. Ensure it scrolls to the bottom as data changes
        final ListView listView = getListView();
        // Tell our list adapter that we only want 200 messages at a time
        mChatListAdapter = new QuestionListAdapter(
                mFirebaseRef.orderByChild("timestamp").limitToLast(200),
                this, R.layout.question, roomName);
        listView.setAdapter(mChatListAdapter);

        mChatListAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(mChatListAdapter.getCount() - 1);
            }
        });

        // Finally, a little indication of connection status
        mConnectedListener = mFirebaseRef.getRoot().child(".info/connected").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean connected = (Boolean) dataSnapshot.getValue();
                if (connected) {
                    Toast.makeText(MainActivity.this, "Connected to Firebase", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Disconnected from Firebase", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                // No-op
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        mFirebaseRef.getRoot().child(".info/connected").removeEventListener(mConnectedListener);
        mChatListAdapter.cleanup();
    }

    private void sendMessage() {
        EditText inputText = (EditText) findViewById(R.id.messageInput);
        String input = inputText.getText().toString();
        if (!input.equals("")) {
            // Create our 'model', a Chat object
            Question question = new Question(input);
            // Create a new, auto-generated child of that chat location, and save our chat data there
            mFirebaseRef.push().setValue(question);
            inputText.setText("");

            mChatListAdapter.notifyDataSetChanged();
        }
    }

    public void sendReply(Question question, Reply reply){
            int length = question.getReplies().size();


            final Firebase replies = mFirebaseRef.child(question.getKey()).child("replies/" + length);
            replies.setValue(reply);
            mChatListAdapter.notifyDataSetChanged();



    }


    public void updateNecho(String key){             //dislike

        final int change;

        if(dbutil.contains("p" + key) && !dbutil.contains("d"+key) ) {
            updateEcho(key);
        }


        if (dbutil.contains("d"+key)) {
            change = -1;
        }else{
            change = 1;
        }

        final Firebase echoRef1 = mFirebaseRef.child(key).child("necho");
        echoRef1.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Long echoValue1 = (Long) dataSnapshot.getValue();
                        Log.e("necho update:", "" + echoValue1);

                        echoRef1.setValue(echoValue1 + change);
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                }
        );

        final Firebase orderRef = mFirebaseRef.child(key).child("order");
        orderRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Long orderValue = (Long) dataSnapshot.getValue();
                        Log.e("Order update:", "" + orderValue);

                        orderRef.setValue(orderValue - change);
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                }
        );

        // Update SQLite DB
        if (dbutil.contains("d"+key)) {
            dbutil.delete("d" + key);
        }else

            dbutil.put("d" + key);
    }

    public void updateEcho(String key) {                    //like

        final int change;

        if(dbutil.contains("d" + key) && !dbutil.contains("p"+key)){
            updateNecho(key);
        }
        if (dbutil.contains("p"+key)) {
            change = -1;
        }else{
            change = 1;
        }
        final Firebase echoRef = mFirebaseRef.child(key).child("echo");
        echoRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Long echoValue = (Long) dataSnapshot.getValue();
                        Log.e("Echo update:", "" + echoValue);

                        echoRef.setValue(echoValue + change);
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                }
        );

        final Firebase orderRef = mFirebaseRef.child(key).child("order");
        orderRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Long orderValue = (Long) dataSnapshot.getValue();
                        Log.e("Order update:", "" + orderValue);

                        orderRef.setValue(orderValue + change);
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                }
        );

        if(dbutil.contains("p"+key)){
            dbutil.delete("p"+key);
        }else{
            dbutil.put("p"+key);
        }
    }

    public void removeKey(String key){
        dbutil.delete(key);
    }

    public void Close(View view) {
        finish();
    }
    
}
