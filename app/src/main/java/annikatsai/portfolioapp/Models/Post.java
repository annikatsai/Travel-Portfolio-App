package annikatsai.portfolioapp.Models;


import com.google.firebase.database.Exclude;

import org.parceler.Parcel;

import java.util.HashMap;
import java.util.Map;

@Parcel
public class Post extends Object {

    public String uid;
    public String location;
    public double latitude;
    public double longitude;
    public String title;
    public String body;
    public String date;
    public String key;
    public String locationKey;
    public String fileName;
    public String photoUrl;

    public Post() {
    }

    public Post(String uid, String title, String body, String location, double latitude, double longitude, String date, String key, String locationKey, String fileName, String photoUrl) {
        this.title = title;
        this.uid = uid;
        this.body = body;
        this.date = date;
        this.key = key;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationKey = locationKey;
        this.fileName = fileName;
        this.photoUrl = photoUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public String getDate() {
        return date;
    }

    public String getKey() {
        return key;
    }

    public String getFileName(){
        return fileName;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("title", title);
        result.put("body", body);
        result.put("location", location);
        result.put("longitude", longitude);
        result.put("latitude", latitude);
        result.put("date", date);
        result.put("key", key);
        result.put("locationKey", locationKey);
        result.put("fileName", fileName);
        result.put("photoUrl", photoUrl);
        return result;
    }
}
