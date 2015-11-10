package com.codepath.apps.twitterclient;

import android.app.Application;
import android.content.Context;

import com.activeandroid.ActiveAndroid;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

import net.danlew.android.joda.JodaTimeAndroid;

/*
 * This is the Android application itself and is used to configure various settings
 * including the image cache in memory and on disk. This also adds a singleton
 * for accessing the relevant rest client.
 *
 *     TwitterClient client = TwitterApplication.getRestClient();
 *     // use client to send requests to API
 *
 */
public class TwitterApplication extends Application {
	private static Context context;

	@Override
	public void onCreate() {
		super.onCreate();
		TwitterApplication.context = this;
		Iconify.with(new FontAwesomeModule());
        JodaTimeAndroid.init(this);
        ActiveAndroid.initialize(this, true);
	}

	public static TwitterClient getRestClient() {
		return (TwitterClient) TwitterClient.getInstance(TwitterClient.class, TwitterApplication.context);
	}
}