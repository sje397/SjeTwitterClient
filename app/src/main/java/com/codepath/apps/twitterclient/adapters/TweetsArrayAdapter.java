package com.codepath.apps.twitterclient.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.twitterclient.R;
import com.codepath.apps.twitterclient.models.Tweet;
import com.codepath.apps.twitterclient.util.AgeFormatter;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;

import java.util.List;

/**
 * Created by sellis on 11/8/15.
 */
public class TweetsArrayAdapter extends ArrayAdapter<Tweet> {
    private static final StringBuilder builder = new StringBuilder();

    public TweetsArrayAdapter(Context context, List<Tweet> objects) {
        super(context, android.R.layout.simple_list_item_1, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Tweet tweet = getItem(position);
        final Holder holder;
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_tweet, parent, false);
            holder = new Holder();
            holder.ivProfile = (ImageView) convertView.findViewById(R.id.ivProfile);
            holder.tvUsername = (TextView) convertView.findViewById(R.id.tvUsername);
            holder.tvBody = (TextView) convertView.findViewById(R.id.tvBody);
            holder.tvAge = (TextView) convertView.findViewById(R.id.tvAge);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        Picasso.with(getContext()).load(tweet.getUser().getProfileImageUrl()).into(holder.ivProfile);
        holder.tvUsername.setText("@" + tweet.getUser().getScreenName() + " (" + tweet.getUser().getName() + ")");
        holder.tvBody.setText(tweet.getBody());

        holder.tvAge.setText(AgeFormatter.getAgeString(new DateTime(tweet.getCreatedAt())));

        return convertView;
    }

    private static class Holder {
        public ImageView ivProfile;
        public TextView tvUsername;
        public TextView tvBody;
        public TextView tvAge;
    }
}
