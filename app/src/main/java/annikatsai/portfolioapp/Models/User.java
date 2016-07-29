package annikatsai.portfolioapp.Models;

import org.json.JSONException;
import org.json.JSONObject;


public class User {

    public String id;
    public String email;
    public String name;
    public String coverPhotoUrl;
    public String link;

    public User () {}

    public User (String id, String email, String name) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.link = "";
    }

    public String getEmail() {
        return email;
    }

    public String getLink() { return link; }

    public String getId() {
        return id;
    }

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
            u.link = json.getString("link");
        } catch (JSONException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
        // Return a user
        return u;
    }
}
