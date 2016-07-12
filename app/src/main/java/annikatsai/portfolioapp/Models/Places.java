package annikatsai.portfolioapp.Models;

/**
 * Created by annikatsai on 7/12/16.
 */
public class Places extends Object {
//        implements GeoDataApi, PlaceDetectionApi {

    private String id;
    private String name;
    private String icon;
    private String vicinity;
    private Double latitude;
    private Double longitude;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

//    @Override
//    public PendingResult<PlaceBuffer> addPlace(GoogleApiClient googleApiClient, AddPlaceRequest addPlaceRequest) {
//        return null;
//    }
//
//    @Override
//    public PendingResult<PlaceBuffer> getPlaceById(GoogleApiClient googleApiClient, String... strings) {
//        return null;
//    }
//
//    @Override
//    public PendingResult<AutocompletePredictionBuffer> getAutocompletePredictions(GoogleApiClient googleApiClient, String s, LatLngBounds latLngBounds, AutocompleteFilter autocompleteFilter) {
//        return null;
//    }
//
//    @Override
//    public PendingResult<PlacePhotoMetadataResult> getPlacePhotos(GoogleApiClient googleApiClient, String s) {
//        return null;
//    }
//
//    @Override
//    public PendingResult<PlaceLikelihoodBuffer> getCurrentPlace(GoogleApiClient googleApiClient, @Nullable PlaceFilter placeFilter) {
//        return null;
//    }
//
//    @Override
//    public PendingResult<Status> reportDeviceAtPlace(GoogleApiClient googleApiClient, PlaceReport placeReport) {
//        return null;
//    }

//    public static Places fromJSON(JSONObject json) {
//        Places places = new Places();
//        // Extract and fill the values
//        try {
//            places.id = json.getString("id");
//            places.name = json.getString("name");
//            places.email = json.getString("email");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        // Return a user
//        return places;
//    }

}
