package com.codepath.apps.twitterclient.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by sellis on 11/8/15.
 */
@Table(name = "users")
public class User extends Model {
    @Column(name = "name")
    private String name;
    @Column(name = "user_uid", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    private long uid;
    @Column(name = "screen_name")
    private String screenName;
    @Column(name = "profile_image_url")
    private String profileImageUrl;

    public User() {
        super();
    }

    public String getName() {
        return name;
    }

    public long getUid() {
        return uid;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public static User fromJSONObject(JSONObject obj) throws JSONException {
        long uid = obj.getLong("id");

        final User user;
        List<User> users = new Select().from(User.class).where("user_uid = ?", uid).execute();
        if(!users.isEmpty()) {
            user = users.get(0);
        } else {
            user = new User();
        }

        user.name = obj.getString("name");
        user.uid = uid;
        user.screenName = obj.getString("screen_name");
        user.profileImageUrl = obj.getString("profile_image_url");

        return user;
    }

    public static User getTestUser() {
        User user = new User();
        user.name = "Bob Jane";
        user.uid = 1;
        user.screenName = "Bob";
        user.profileImageUrl = "https://www.google.com/url?sa=i&rct=j&q=&esrc=s&source=images&cd=&cad=rja&uact=8&ved=0CAcQjRxqFQoTCKG9zP_wgskCFdZciAodiR4Gdg&url=http%3A%2F%2Fwww.vwmin.org%2Ffranchise-info-about-us-info-bob-jane-t-marts.html&psig=AFQjCNFSwtzDWsXKJW05tjUiHMXSY2_hOg&ust=1447142741858148";

        return user;
    }
}
