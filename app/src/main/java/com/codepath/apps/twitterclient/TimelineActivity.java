package com.codepath.apps.twitterclient;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
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
import com.astuetz.PagerSlidingTabStrip;
import com.codepath.apps.twitterclient.adapters.TweetsArrayAdapter;
import com.codepath.apps.twitterclient.fragments.HomeTimelineFragment;
import com.codepath.apps.twitterclient.fragments.MentionsTimelineFragment;
import com.codepath.apps.twitterclient.fragments.TweetsListFragment;
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
    private TwitterClient twitterClient = TwitterApplication.getTwitterClient();
    private static final int TWEET_REQUEST_CODE = 42;

    private ViewPager pager;
    private PagerSlidingTabStrip tabs;

    private TweetsPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pager = (ViewPager) findViewById(R.id.viewpager);
        pagerAdapter = new TweetsPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);
        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setViewPager(pager);
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
        if(id == R.id.action_tweet) {
            Intent i = new Intent(this, TweetActivity.class);
            startActivityForResult(i, TWEET_REQUEST_CODE);
            return true;
        }

        if(id == R.id.action_profile) {
            Intent i = new Intent(this, ProfileActivity.class);
            startActivity(i);
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


    private void createNewTweet(String text) {
        twitterClient.post(text, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    Tweet tweet = Tweet.fromJSONObject(response, Tweet.Type.NORMAL);
                    HomeTimelineFragment tweetsFragment = (HomeTimelineFragment) pagerAdapter.getItem(0);
                    tweetsFragment.insertTweet(tweet);
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

    private static class TweetsPagerAdapter extends FragmentPagerAdapter {
        private static String[] titles = {"Home", "Mentions"};
        private HomeTimelineFragment homeFragment;
        private MentionsTimelineFragment mentionsTimelineFragment;

        public TweetsPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
            homeFragment = new HomeTimelineFragment();
            mentionsTimelineFragment = new MentionsTimelineFragment();
        }

        @Override
        public Fragment getItem(int position) {
            switch(position) {
                case 0: return homeFragment;
                default: return mentionsTimelineFragment;
            }
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }
}
