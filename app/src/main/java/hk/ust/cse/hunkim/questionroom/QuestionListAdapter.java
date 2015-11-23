package hk.ust.cse.hunkim.questionroom;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.client.Query;

import java.util.Collections;
import java.util.List;

import hk.ust.cse.hunkim.questionroom.db.DBUtil;
import hk.ust.cse.hunkim.questionroom.question.Question;
import hk.ust.cse.hunkim.questionroom.question.Reply;

/**
 * @author greg
 * @since 6/21/13
 * <p/>
 * This class is an example of how to use FirebaseListAdapter. It uses the <code>Chat</code> class to encapsulate the
 * data for each individual chat message
 */
public class QuestionListAdapter extends FirebaseListAdapter<Question> {

    // The mUsername for this client. We use this to indicate which messages originated from this user
    private String roomName;
    MainActivity activity;

    public QuestionListAdapter(Query ref, Activity activity, int layout, String roomName) {
        super(ref, Question.class, layout, activity);

        // Must be MainActivity
        assert (activity instanceof MainActivity);

        this.activity = (MainActivity) activity;
    }

    /**
     * Bind an instance of the <code>Chat</code> class to our view. This method is called by <code>FirebaseListAdapter</code>
     * when there is a data change, and we are given an instance of a View that corresponds to the layout that we passed
     * to the constructor, as well as a single <code>Chat</code> instance that represents the current data to bind.
     *
     * @param view     A view instance corresponding to the layout we passed to the constructor.
     * @param question An instance representing the current state of a chat message
     */
    @Override
    protected void populateView(final View view, final Question question) {
        DBUtil dbUtil = activity.getDbutil();


        // Map a Chat object to an entry in our listview
        int echo = question.getEcho();//like button
        Button echoButton = (Button) view.findViewById(R.id.echo);
        echoButton.setText("" + echo);
        echoButton.setTextColor(Color.BLUE);

        echoButton.setTag(question.getKey()); // Set tag for button

        echoButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       activity.updateEcho((String) view.getTag());
                    }
                }

        );

        int necho = question.getNecho();//dislike button
        Button echoButton1 = (Button) view.findViewById(R.id.echo1);
        echoButton1.setText("" + necho);
        echoButton1.setTextColor(Color.BLUE);

        echoButton1.setTag(question.getKey()); // Set tag for button

        echoButton1.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       activity.updateNecho((String) view.getTag());
                    }
                }

        );

        String msgString = "";

        question.updateNewQuestion();
        if (question.isNewQuestion()) {
            msgString += "<font color=red>NEW </font>";
        }

        msgString += "<B>" + question.getHead() + "</B>";
        msgString = badWordFilter(msgString);


        final TextView headTextView = (TextView) view.findViewById(R.id.head_textView);

        headTextView.setText(Html.fromHtml(msgString));

        msgString = question.getDesc();
        msgString = badWordFilter(msgString);

        ((TextView) view.findViewById(R.id.body_textView)).setText(Html.fromHtml(msgString));



        view.setTag(question.getKey());  // store key in the view


        //Setup hide / expand of question body //Jonathan Yu
        TextView textView = (TextView) view.findViewById(R.id.head_textView);
        final LinearLayout layout = (LinearLayout) view.findViewById(R.id.question_body_layout);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ( layout.getVisibility() == View.GONE)
                {
                    //expandedChildList.set(arg2, true);
                    layout.setVisibility(View.VISIBLE);
                    headTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.up, 0, 0, 0);
                }
                else
                {
                    //expandedChildList.set(arg2, false);
                    layout.setVisibility(View.GONE);
                    headTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.down, 0, 0, 0);
                }
            }
        });

        //Setup pinned threads //Jonathan Yu
        boolean pinned = question.isPinned();
        if(pinned){
            textView.setBackgroundColor(Color.GREEN);
        }else{
            textView.setBackgroundColor(Color.WHITE);
        }


        //Add replies to the question //Jonathan Yu
        LinearLayout replyContainer = (LinearLayout) view.findViewById(R.id.replyContainer);
        replyContainer.removeAllViews();

       
            for (Reply reply : question.getReplies()
                    ) {
                LinearLayout replyLayout = (LinearLayout) LayoutInflater.from(view.getContext()).inflate(R.layout.reply, null);
                TextView replyTextView = (TextView) replyLayout.findViewById(R.id.reply_textView);
                replyTextView.setText(badWordFilter(reply.getHead()));
                replyContainer.addView(replyLayout);
            }


        //Add Reply Dialog //Jonathan Yu
        Button replyButton = (Button) view.findViewById(R.id.replyButton);
        replyButton.setOnClickListener(new View.OnClickListener() {

            String reply;
            @Override
            public void onClick(View v) {

                final EditText replyTextView = new EditText(view.getContext());

                replyTextView.setHint("Reply Here");

                new AlertDialog.Builder(view.getContext())
                        .setTitle(badWordFilter(question.getHead()))
                        .setView(replyTextView)
                        .setPositiveButton("Post", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                reply = replyTextView.getText().toString();
                                Reply newReply = new Reply(reply);
                                activity.sendReply(question, newReply);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                            }
                        })
                        .show();
            }
        });

        //setup hidden threads // Jonathan Yu
        boolean hidden = question.isHidden();
        LinearLayout questionContainer = (LinearLayout) view.findViewById(R.id.question_layout_container);

        if(hidden){
            questionContainer.setVisibility(View.GONE);
        }else{
            questionContainer.setVisibility(View.VISIBLE);
        }

    }

    @Override
    protected void sortModels(List<Question> mModels) {
        Collections.sort(mModels, Question.sortingComparator);
    }

    @Override
    protected void setKey(String key, Question model) {
        model.setKey(key);
    }

    /**
     * Filters out profane words, returns filtered string
     * @param message
     * @return
     */
    protected String badWordFilter(String message){
        String  []filterWords = {
                "anal",
                "anus",
                "ass",
                "bastard",
                "bitch",
                "boob",
                "cock",
                "cum",
                "cunt",
                "dick",
                "dildo",
                "dyke",
                "fag",
                "faggot",
                "fuck",
                "fuk",
                "handjob",
                "homo",
                "jizz",
                "kike",
                "kunt",
                "muff",
                "nigger",
                "penis",
                "piss",
                "poop",
                "pussy",
                "queer",
                "rape",
                "semen",
                "sex",
                "shit",
                "slut",
                "titties",
                "twat",
                "vagina",
                "vulva",
                "wank"};

        int i;
        String toBeFilter= message;

        for(i=0;i<filterWords.length;i++)	{
            //Pattern p = Pattern.compile(filterWords[i],Pattern.CASE_INSENSITIVE);
            String regex= filterWords[i];
            //	Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            //	String re = new RegExp(swear[i],"gi");
            String replacement="";
            for(int j=0;j<filterWords[i].length();j++) replacement = replacement +"*";
            toBeFilter = toBeFilter.replaceAll("(?i)"+regex,replacement);


        }
        return toBeFilter;

    }


}
