package com.codepath.apps.twitterclient;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.twitterclient.fragments.UserTimelineFragment;
import com.codepath.apps.twitterclient.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ProfileActivity extends AppCompatActivity {
    private TwitterClient client = TwitterApplication.getTwitterClient();
    private User user;

    private TextView tvUsername;
    private TextView tvTagline;
    private TextView tvFollowers;
    private TextView tvFollowing;
    private ImageView ivPic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        String screenName = getIntent().getStringExtra("screen_name");

        tvUsername = (TextView) findViewById(R.id.tvUsername);
        tvTagline = (TextView) findViewById(R.id.tvTagline);
        tvFollowers = (TextView) findViewById(R.id.tvFollowers);
        tvFollowing = (TextView) findViewById(R.id.tvFollowing);
        ivPic = (ImageView) findViewById(R.id.ivPic);

        client.getUserInfo(screenName, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                try {
                    user = User.fromJSONObject(response.getJSONObject(0));
                    setDetails(user);
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    user = User.fromJSONObject(response);
                    setDetails(user);
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", "fail: " + errorResponse);
                throwable.printStackTrace();

                if (errorResponse == null) {
                    Toast.makeText(ProfileActivity.this, "Network problem loading profile data", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        String err = "Error: " + errorResponse.getJSONArray("errors").getJSONObject(0).getString("message");
                        Toast.makeText(ProfileActivity.this, err, Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        UserTimelineFragment userTimelineFragment = UserTimelineFragment.newInstance(screenName);
        ft.replace(R.id.frList,userTimelineFragment);
        ft.commit();
    }

    private void setDetails(User user) {
        tvUsername.setText(user.getName());
        tvTagline.setText(user.getTagline());
        tvFollowers.setText(String.format(getString(R.string.followers_text), user.getFollowersCount()));
        tvFollowing.setText(String.format(getString(R.string.following_text), user.getFollowingCount()));

        Picasso.with(this).load(user.getProfileImageUrl()).into(ivPic);
    }
}
