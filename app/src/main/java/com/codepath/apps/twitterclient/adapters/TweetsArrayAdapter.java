package com.codepath.apps.twitterclient.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.twitterclient.ProfileActivity;
import com.codepath.apps.twitterclient.R;
import com.codepath.apps.twitterclient.models.Tweet;
import com.codepath.apps.twitterclient.models.User;
import com.codepath.apps.twitterclient.util.AgeFormatter;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;

import java.util.List;

public class TweetsArrayAdapter extends ArrayAdapter<Tweet> {
    private boolean imagesEnabled = true;

    public TweetsArrayAdapter(Context context, List<Tweet> objects) {
        super(context, android.R.layout.simple_list_item_1, objects);
    }

    @Override
    public boolean areAllItemsEnabled()
    {
        return true;
    }

    @Override
    public boolean isEnabled(int arg0)
    {
        return true;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        final Tweet tweet = getItem(position);
        final Holder holder;
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_tweet, parent, false);
            holder = new Holder();
            holder.ivProfile = (ImageView) convertView.findViewById(R.id.ivProfile);
            holder.tvUsername = (TextView) convertView.findViewById(R.id.tvUsername);
            holder.tvBody = (TextView) convertView.findViewById(R.id.tvBody);
            holder.tvAge = (TextView) convertView.findViewById(R.id.tvAge);


            holder.ivProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    User user = tweet.getUser();

                    Intent i = new Intent(getContext(), ProfileActivity.class);
                    i.putExtra("screen_name", user.getScreenName());
                    getContext().startActivity(i);
                }
            });

            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        if(imagesEnabled) {
            if(tweet != null && tweet.getUser() != null && tweet.getUser().getProfileImageUrl() != null) {
                Picasso.with(getContext()).load(tweet.getUser().getProfileImageUrl()).into(holder.ivProfile);
            }
        } else {
            holder.ivProfile.setVisibility(View.GONE);
        }

        //holder.tvUsername.setText("@" + tweet.getUser().getScreenName() + " (" + tweet.getUser().getName() + ")");
        if(tweet.getUser() != null) {
            holder.tvUsername.setText(String.format(getContext().getString(R.string.user_title),
                    tweet.getUser().getScreenName(), tweet.getUser().getName()));
        }
        holder.tvBody.setText(tweet.getBody());

        holder.tvAge.setText(AgeFormatter.getAgeString(new DateTime(tweet.getCreatedAt())));

        return convertView;
    }

    public void setImagesEnabled(boolean enabled) {
        imagesEnabled = enabled;
    }

    private static class Holder {
        public ImageView ivProfile;
        public TextView tvUsername;
        public TextView tvBody;
        public TextView tvAge;
    }
}
