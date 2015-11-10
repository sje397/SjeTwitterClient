package com.codepath.apps.twitterclient;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class TweetActivity extends AppCompatActivity {
    private EditText etTweet;
    private TextView tvCount;
    private Button btnTweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet);

        etTweet = (EditText) findViewById(R.id.etTweet);
        tvCount = (TextView) findViewById(R.id.tvCount);
        btnTweet = (Button) findViewById(R.id.btnTweet);
        btnTweet.setEnabled(false);

        etTweet.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEND && event.getAction() == KeyEvent.ACTION_DOWN) {
                    Intent i = new Intent();
                    i.putExtra("tweetText", etTweet.getText().toString());
                    setResult(RESULT_OK, i);
                    finish();
                    return true;
                }
                return false;
            }
        });

        etTweet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                int len = s.length();
                tvCount.setText("" + (140 - len));
                btnTweet.setEnabled(len > 0);
            }
        });
    }

    public void onClick(View view) {
        Intent i = new Intent();
        i.putExtra("tweetText", etTweet.getText().toString());
        setResult(RESULT_OK, i);
        finish();
    }
}
