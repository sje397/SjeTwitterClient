package com.codepath.apps.twitterclient.fragments;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.activeandroid.query.From;
import com.activeandroid.query.Select;
import com.codepath.apps.twitterclient.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.List;

/**
 * Created by sellis on 11/12/15.
 */
public class MentionsTimelineFragment extends TweetsListFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        populateTimeline(1, 0, true);
    }

    public void insertTweet(Tweet newTweet) {
        insertTopTweet(newTweet);
    }

    @Override
    protected boolean populateTimeline(int page, int totalItemsCount, final boolean refresh) {
        Log.d("DEBUG", "populateTimeline(page = " + page + ", totalItemsCount = " + totalItemsCount + ", refresh = " + refresh + ")");
        if(!refresh && totalItemsCount / PAGE_SIZE >= page) return false;

        final long lastId = refresh ? 0 : getLastTweetId();
        if(isNetworkAvailable()) {
            twitterClient.getMentionsTimeline(lastId, PAGE_SIZE, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    try {
                        List<Tweet> newList = Tweet.fromJSONArray(response, Tweet.Type.MENTION);
                        addAll(newList, refresh);
                    } catch (JSONException | ParseException ex) {
                        ex.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d("DEBUG", "fail: " + errorResponse);
                    throwable.printStackTrace();

                    if (errorResponse == null) {
                        Toast.makeText(getActivity(), "Network problem - loading from db", Toast.LENGTH_SHORT).show();
                    } else {
                        try {
                            String err = "Error: " + errorResponse.getJSONArray("errors").getJSONObject(0).getString("message");
                            Toast.makeText(getActivity(), err, Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    loadTweetsFromDb(lastId, PAGE_SIZE);
                }
            });
        } else {
            loadTweetsFromDb(lastId, PAGE_SIZE);
        }

        return true;
    }

    @Override
    protected void loadTweetsFromDb(long lastId, int pageSize) {
        From select = new Select()
                .from(Tweet.class)
                .where("tweet_type = " + Tweet.Type.MENTION.ordinal());
        if(lastId > 0) {
            select = select.and("tweet_uid < " + lastId);
        }
        select = select.orderBy("tweet_uid DESC")
                .limit(pageSize);

        List<Tweet> newList = select.execute();

        addAll(newList, false);
    }

}
