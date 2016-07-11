package annikatsai.portfolioapp.Models;

import org.json.JSONException;
import org.json.JSONObject;


public class User {

    public String id;
    public String email;
    public String name;
    public String coverPhotoUrl;
//    public String profileImageUrl;

    public User () {}

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public String getId() {
        return id;
    }

//    public String getProfileImageUrl() {
//        return profileImageUrl;
//    }

    public String getCoverPhotoUrl() {
        return coverPhotoUrl;
    }

    public void setCoverPhotoUrl(String coverPhotoUrl) {
        this.coverPhotoUrl = coverPhotoUrl;
    }

    public String getName() {
        return name;
    }

    public static User fromJSON(JSONObject json) {
        User u = new User();
        // Extract and fill the values
        try {
            u.id = json.getString("id");
            u.name = json.getString("name");
            u.email = json.getString("email");
            //u.profileImageUrl = json.getString("profile_image_url");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Return a user
        return u;
    }
}
