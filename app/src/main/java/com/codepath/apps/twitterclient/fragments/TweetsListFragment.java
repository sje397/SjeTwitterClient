package com.codepath.apps.twitterclient.fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.activeandroid.query.Delete;
import com.activeandroid.query.From;
import com.activeandroid.query.Select;
import com.codepath.apps.twitterclient.R;
import com.codepath.apps.twitterclient.TwitterApplication;
import com.codepath.apps.twitterclient.TwitterClient;
import com.codepath.apps.twitterclient.adapters.TweetsArrayAdapter;
import com.codepath.apps.twitterclient.models.Tweet;
import com.codepath.apps.twitterclient.models.User;
import com.codepath.apps.twitterclient.util.EndlessScrollListener;

import java.util.ArrayList;
import java.util.List;

public abstract class TweetsListFragment extends Fragment {
    protected static final int PAGE_SIZE = 30;
    protected TwitterClient twitterClient = TwitterApplication.getTwitterClient();

    private ListView lvTweets;
    private TweetsArrayAdapter adapter;
    private ArrayList<Tweet> tweets = new ArrayList<>();

    private SwipeRefreshLayout swipeContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tweets_list, container, false);

        lvTweets = (ListView) v.findViewById(R.id.lvTweets);
        swipeContainer = (SwipeRefreshLayout) v.findViewById(R.id.swipeContainer);
        lvTweets.setAdapter(adapter);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                populateTimeline(1, 0, true);
            }
        });

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

        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        adapter = new TweetsArrayAdapter(getActivity(), tweets);
    }

    protected abstract boolean populateTimeline(int page, int totalItems, boolean refresh);

    protected void addAll(List<Tweet> newTweets, boolean clear) {
        if(clear) {
            Log.d("DEBUG", "Refresh - deleting all old users and tweets");
            tweets.clear();
            new Delete().from(Tweet.class).execute();
            new Delete().from(User.class).execute();
        }
        for(final Tweet tweet: newTweets) {
            tweet.getUser().save();
            tweet.save();
        }

        tweets.addAll(newTweets);
        adapter.notifyDataSetChanged();
        swipeContainer.setRefreshing(false);
    }

    protected void insertTopTweet(Tweet newTweet) {
        newTweet.getUser().save();
        newTweet.save();
        tweets.add(0, newTweet);
        adapter.notifyDataSetChanged();
    }

    protected long getLastTweetId() {
        return (tweets.isEmpty() ? 0 : tweets.get(tweets.size() - 1).getUid());
    }

    protected void loadTweetsFromDb(long lastId, int pageSize) {
        From select = new Select()
                .from(Tweet.class);
        if(lastId > 0) {
            select = select.where("tweet_uid < " + lastId);
        }
        select = select.orderBy("tweet_uid DESC")
                .limit(pageSize);

        List<Tweet> newList = select.execute();

        if(lastId == 0) {
            tweets.clear();
        }
        addAll(newList, false);
    }

    protected Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }
}
