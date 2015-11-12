package com.codepath.apps.twitterclient;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.FlickrApi;
import org.scribe.builder.api.TwitterApi;

import android.content.Context;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class;
	public static final String REST_URL = "https://api.twitter.com/1.1";
	public static final String REST_CONSUMER_KEY = "OrtJxAWkLQ4xttJv9UZVDY1Iq";
	public static final String REST_CONSUMER_SECRET = "FacUskZ6oUOJeR9db8fcArZkdfffBJMj5lwmduOztwO8CYIGLt";
	public static final String REST_CALLBACK_URL = "oauth://cpsjetwitterclient";

    public static final int TIMEOUT = 2000;

	public TwitterClient(Context context) {
        super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
        client.setTimeout(TIMEOUT);
        client.setConnectTimeout(TIMEOUT);
	}

	public void getHomeTimeline(long lastId, int pageSize, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/home_timeline.json");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("count", pageSize);
        if(lastId != 0) {
            params.put("max_id", lastId - 1);
        }
		client.get(apiUrl, params, handler);
	}

    public void post(String text, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/update.json");
        // Can specify query string params directly or through RequestParams.
        RequestParams params = new RequestParams();
        params.put("status", text);
        client.post(apiUrl, params, handler);
    }

    public void getMentionsTimeline(long lastId, int pageSize, JsonHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/mentions_timeline.json");
        // Can specify query string params directly or through RequestParams.
        RequestParams params = new RequestParams();
        params.put("count", pageSize);
        if(lastId != 0) {
            params.put("max_id", lastId - 1);
        }
        client.get(apiUrl, params, handler);
    }

    public void getUserTimeline(String screenName, long lastId, int pageSize, JsonHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/user_timeline.json");
        // Can specify query string params directly or through RequestParams.
        RequestParams params = new RequestParams();
        params.put("screen_name", screenName);
        params.put("count", pageSize);
        if(lastId != 0) {
            params.put("max_id", lastId - 1);
        }
        client.get(apiUrl, params, handler);
    }

    public void getUserInfo(String screenName, JsonHttpResponseHandler handler) {
        if(screenName == null) {
            getMyInfo(handler);
        }

        String apiUrl = getApiUrl("users/lookup.json");
        // Can specify query string params directly or through RequestParams.
        RequestParams params = new RequestParams();
        params.put("screen_name", screenName);
        params.put("include_entities", false);
        client.get(apiUrl, params, handler);
    }

    private void getMyInfo(JsonHttpResponseHandler handler) {
        String apiUrl = getApiUrl("account/verify_credentials.json");
        // Can specify query string params directly or through RequestParams.
        client.get(apiUrl, null, handler);
    }
}