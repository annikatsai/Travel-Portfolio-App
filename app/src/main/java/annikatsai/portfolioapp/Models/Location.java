package annikatsai.portfolioapp.Models;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.Exclude;

import org.parceler.Parcel;

import java.util.HashMap;
import java.util.Map;

@Parcel
public class Location extends Object {

    public Location() {}

    public String name;
    public double latitude;
    public double longitude;
    public LatLng latLngLocation;
    public String locationKey;
    public String fileName;
    public String photoUrl;
    public String realOrientation;
    public String photoType;

    public Location(LatLng loc, String name) {
        this.latLngLocation = loc;
        if (this.latLngLocation != null) {
            this.latitude = latLngLocation.latitude;
            this.longitude = latLngLocation.longitude;
        }
        this.name = name;
        this.realOrientation = null;
        this.fileName = null;
        this.photoUrl = null;
        this.photoType = null;
    }

    public void setPhoto(String fileName, String photoUrl, String realOrientation, String photoType) {
        this.fileName = fileName;
        this.photoUrl = photoUrl;
        this.realOrientation = realOrientation;
        this.photoType = photoType;
    }

    public String getLocationKey() {
        return locationKey;
    }

    public LatLng getLatLngLocation() {
        return latLngLocation;
    }

    public void setLocationKey(String key) {
        this.locationKey = key;
    }

    public void setLatLngLocation(LatLng loc) { this.latLngLocation = loc; }

    public void setName(String name) { this.name = name; }

    @Exclude
    public Map<String, Object> locationToMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("latitude", latitude);
        result.put("longitude", longitude);
        result.put("locationKey", locationKey);
        result.put("realOrientation", realOrientation);
        result.put("fileName", fileName);
        result.put("photoUrl", photoUrl);
        result.put("photoType", photoType);
        return result;
    }
}
