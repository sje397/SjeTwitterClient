package com.codepath.apps.twitterclient;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.activeandroid.query.Delete;
import com.activeandroid.query.From;
import com.activeandroid.query.Select;
import com.codepath.apps.twitterclient.adapters.TweetsArrayAdapter;
import com.codepath.apps.twitterclient.models.Tweet;
import com.codepath.apps.twitterclient.models.User;
import com.codepath.apps.twitterclient.util.EndlessScrollListener;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class TimelineActivity extends AppCompatActivity {
    private static final int TWEET_REQUEST_CODE = 42;
    private static final int PAGE_SIZE = 30;

    private TwitterClient twitterClient;

    private ListView lvTweets;
    private TweetsArrayAdapter adapter;
    private ArrayList<Tweet> tweets = new ArrayList<>();

    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        lvTweets = (ListView) findViewById(R.id.lvTweets);
        adapter = new TweetsArrayAdapter(this, tweets);
        lvTweets.setAdapter(adapter);


        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                populateTimeline(1, 0, true);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        lvTweets.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                return populateTimeline(page, totalItemsCount, false);
            }
        });

        twitterClient = TwitterApplication.getRestClient();
        populateTimeline(1, 0, true);
        //populateWithTestTweet();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_tweet) {
            Intent i = new Intent(this, TweetActivity.class);
            startActivityForResult(i, TWEET_REQUEST_CODE);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == TWEET_REQUEST_CODE && resultCode == RESULT_OK) {
            String tweetText = data.getStringExtra("tweetText");
            createNewTweet(tweetText);
        }
    }

    private void populateWithTestTweet() {
        Tweet testTweet = Tweet.getTestTweet();
        tweets.add(testTweet);
        adapter.notifyDataSetChanged();
    }

    private boolean populateTimeline(int page, int totalItemsCount, final boolean refresh) {
        Log.d("DEBUG", "populateTimeline(page = " + page + ", totalItemsCount = " + totalItemsCount + ", refresh = " + refresh + ")");
        if(!refresh && totalItemsCount / PAGE_SIZE >= page) return false;

        final long lastId = (tweets.isEmpty() || refresh ? 0 : tweets.get(tweets.size() - 1).getUid());
        if(isNetworkAvailable()) {
            twitterClient.getHomeTimeline(lastId, PAGE_SIZE, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    try {
                        List<Tweet> newList = Tweet.fromJSONArray(response);
                        if (refresh) {
                            Log.d("DEBUG", "Refresh - deleting all old users and tweets");
                            tweets.clear();
                            new Delete().from(Tweet.class).execute();
                            new Delete().from(User.class).execute();
                        }
                        for(final Tweet tweet: newList) {
                            tweet.getUser().save();
                            tweet.save();
                        }
                        tweets.addAll(newList);
                        adapter.notifyDataSetChanged();
                        swipeContainer.setRefreshing(false);


                    } catch (JSONException | ParseException ex) {
                        ex.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d("DEBUG", "fail: " + errorResponse);
                    throwable.printStackTrace();

                    if (errorResponse == null) {
                        Toast.makeText(TimelineActivity.this, "Network problem - loading from db", Toast.LENGTH_SHORT).show();
                    } else {
                        try {
                            String err = "Error: " + errorResponse.getJSONArray("errors").getJSONObject(0).getString("message");
                            Toast.makeText(TimelineActivity.this, err, Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    loadTweetsFromDb(lastId, PAGE_SIZE, refresh);
                }
            });
        } else {
            loadTweetsFromDb(lastId, PAGE_SIZE, refresh);
        }

        return true;
    }

    private void loadTweetsFromDb(long lastId, int pageSize, boolean refresh) {
        From select = new Select()
                .from(Tweet.class);
        if(lastId > 0) {
            select = select.where("tweet_uid < " + lastId);
        }
        select = select.orderBy("tweet_uid DESC")
                .limit(pageSize);

        List<Tweet> newList = select.execute();

        if (refresh) {
            tweets.clear();
        }
        tweets.addAll(newList);
        adapter.notifyDataSetChanged();
        swipeContainer.setRefreshing(false);
    }

    private void createNewTweet(String text) {
        twitterClient.post(text, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    Tweet tweet = Tweet.fromJSONObject(response);
                    tweet.save();
                    tweets.add(0, tweet);
                    adapter.notifyDataSetChanged();
                } catch (JSONException | ParseException ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", "fail: " + errorResponse);
                throwable.printStackTrace();

                if (errorResponse == null) {
                    Toast.makeText(TimelineActivity.this, "Error posting tweet", Toast.LENGTH_LONG).show();
                } else {
                    try {
                        String err = "Error: " + errorResponse.getJSONArray("errors").getJSONObject(0).getString("message");
                        Toast.makeText(TimelineActivity.this, err, Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }
}
