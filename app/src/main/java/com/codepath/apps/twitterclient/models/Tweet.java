package com.codepath.apps.twitterclient.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by sellis on 11/8/15.
 */
@Table(name = "tweets")
public class Tweet extends Model {
    private static final DateTimeFormatter format =
            DateTimeFormat.forPattern("EEE MMM dd HH:mm:ss Z yyyy").withLocale(Locale.ENGLISH);

    @Column(name = "body")
    private String body;
    @Column(name = "tweet_uid", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    private long uid;
    @Column(name = "user", onUpdate = Column.ForeignKeyAction.NO_ACTION, onDelete = Column.ForeignKeyAction.NO_ACTION)
    private User user;
    @Column(name = "created")
    private long createdAt;

    public Tweet() {
        super();
    }

    public String getBody() {
        return body;
    }

    public long getUid() {
        return uid;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public User getUser() {
        return user;
    }

    public static Tweet fromJSONObject(JSONObject obj) throws JSONException, ParseException {
        Tweet tweet = new Tweet();
        tweet.body = obj.getString("text");
        tweet.uid = obj.getLong("id");
        tweet.createdAt = format.parseDateTime(obj.getString("created_at")).getMillis();
        tweet.user = User.fromJSONObject(obj.getJSONObject("user"));

        return tweet;
    }

    public static List<Tweet> fromJSONArray(JSONArray array) throws JSONException, ParseException {
        ArrayList<Tweet> tweets = new ArrayList<>();
        for(int i = 0; i < array.length(); i++) {
            tweets.add(fromJSONObject(array.getJSONObject(i)));
        }

        return tweets;
    }

    public static Tweet getTestTweet() {
        Tweet tweet = new Tweet();
        tweet.body = "Here's a test tweet";
        tweet.uid = 1;
        tweet.createdAt = DateTime.now().getMillis();
        tweet.user = User.getTestUser();
        return tweet;
    }
}
