//package annikatsai.portfolioapp;
//
//import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.v7.app.AppCompatActivity;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.AutoCompleteTextView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.api.GoogleApiClient;
//import com.google.android.gms.location.places.Places;
//
//public class SearchLocationActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
//
//    protected GoogleApiClient mGoogleApiClient;
//    private PlacesAutoCompleteAdapter mAdapter;
//    private AutoCompleteTextView mAutocompleteView;
//    private TextView mPlaceDetailsText;
//    private TextView mPlaceDetailsAttribution;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_search_location);
//
//        // Have to call connect() and disconnect() explicitly
//        mGoogleApiClient = new GoogleApiClient
//                .Builder(this)
//                .addApi(Places.GEO_DATA_API)
//                .addApi(Places.PLACE_DETECTION_API)
//                .enableAutoManage(this, this)
//                .build();
//
//        mAutocompleteView = (AutoCompleteTextView) findViewById(R.id.tvAutoComplete);
//        mAutocompleteView.setAdapter(new PlacesAutoCompleteAdapter(this, R.layout.autocomplete_list_item));
//
////        mAdapter = new PlacesAutoCompleteAdapter(this, mGoogleApiClient, null, null);
////        mAutocompleteView.setAdapter(mAdapter);
//        mAutocompleteView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                // Get data associated with the specified position
//                // in the list (AdapterView)
//                String description = (String) parent.getItemAtPosition(position);
//                Toast.makeText(SearchLocationActivity.this, description, Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    @Override
//    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//        Toast.makeText(this, "Connection Failed", Toast.LENGTH_SHORT).show();
//    }
//}
