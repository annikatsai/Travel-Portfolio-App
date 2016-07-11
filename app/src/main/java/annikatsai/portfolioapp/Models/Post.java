package annikatsai.portfolioapp.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class Post implements Parcelable {

    // the attributes
    public String title;
    public String body;
    public String date; // have to figure out how to incorporate this; it is not called in PostsArrayAdapter
    //public String createdAt;

    // getters:
    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public String getDate() {
        return date;
    }

    /*public String getCreatedAt() {
        return createdAt;
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

    // handle parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.body);
        dest.writeString(this.date);
        //dest.writeString(this.createdAt);
    }

    public Post() {
    }

    protected Post(Parcel in) {
        this.title = in.readString();
        this.body = in.readString();
        this.date = in.readString();
        //this.createdAt = in.readString();
    }

    public static final Parcelable.Creator<Post> CREATOR = new Parcelable.Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel source) {
            return new Post(source);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };

    // handle camera roll
}
