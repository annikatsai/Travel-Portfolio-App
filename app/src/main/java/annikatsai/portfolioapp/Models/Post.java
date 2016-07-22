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
    //public String createdAt;
    //public String imagePath;

    public Post() {
    }

    public Post(String uid, String title, String body, String location, double latitude, double longitude, String date, String key, String locationKey, String fileName) {
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
    }

    // getters:
    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

//    public String getLocation() {
//        return location;
//    }

//    public Location getLocation() {return location;}

    public String getDate() {
        return date;
    }

    public String getKey() {
        return key;
    }

    public String getFileName(){
        return fileName;
    }


    /*public String getCreatedAt() {
        return createdAt;
    }*/

    /* Camera roll - figure how to set it up
    public String getImagePath(){
        return String.format("https://%s", imagePath);
    }*/

    // setters:
    public void setTitle(String title) {
        this.title = title;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setDate(String date) {
        this.date = date;
    }

    /*public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }*/

    /*public void setImagePath(String imagePath){
        this.imagePath = imagePath;
    }*/

    // handle parcelable
//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeString(this.title);
//        dest.writeString(this.body);
//        dest.writeString(this.date);
//        //dest.writeString(this.createdAt);
//        //dest.writeString(this.imagePath);
//    }

//    protected Post(Parcel in) {
//        this.title = in.readString();
//        this.body = in.readString();
//        this.date = in.readString();
//        //this.createdAt = in.readString();
//        //this.imagePath = in.readString();
//    }
//
//    public static final Parcelable.Creator<Post> CREATOR = new Parcelable.Creator<Post>() {
//        @Override
//        public Post createFromParcel(Parcel source) {
//            return new Post(source);
//        }
//
//        @Override
//        public Post[] newArray(int size) {
//            return new Post[size];
//        }
//    };



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
        return result;
    }
    // handle camera roll
}
