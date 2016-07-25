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

    public Location(LatLng loc, String name) {
        this.latLngLocation = loc;
        if (this.latLngLocation != null) {
            this.latitude = latLngLocation.latitude;
            this.longitude = latLngLocation.longitude;
        }
        this.name = name;
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
        return result;
    }
}
