package annikatsai.portfolioapp.Models;

import org.json.JSONException;
import org.json.JSONObject;


public class User {

    public String name;
    public String profileImageUrl;

    public User () {}

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public String getName() {
        return name;
    }

    public static User fromJSON(JSONObject json) {
        User u = new User();
        // Extract and fill the values
        try {
            u.name = json.getString("name");
            u.profileImageUrl = json.getString("profile_image_url");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Return a user
        return u;
    }
}
