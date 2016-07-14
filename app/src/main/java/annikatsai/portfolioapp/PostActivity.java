package annikatsai.portfolioapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

public class PostActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private TextView tvLocation;
    private TextView tvDate;
    private java.util.Calendar c = java.util.Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        tvLocation = (TextView) findViewById(R.id.tvLocation);
        tvDate = (TextView) findViewById(R.id.tvDate);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("");

        Typeface titleFont = Typeface.createFromAsset(getAssets(), "fonts/Pacifico.ttf");
        toolbarTitle.setText("Make a Post");
        toolbarTitle.setTypeface(titleFont);
    }

//    public void selectLocation(View view) {
//        Intent intent = new Intent(this, SearchLocationActivity.class);
//        startActivity(intent);
//    }

    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    public void selectLocation(View view) {
        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                tvLocation.setText(place.getName());
                Log.i("TAG", "Place: " + place.getName());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i("TAG", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    // attach to an onclick handler to show the date picker
    public void showDatePickerDialog(View v) {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    // handle the date selected
    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        // store the values selected into a Calendar instance
        c.set(java.util.Calendar.YEAR, year);
        c.set(java.util.Calendar.MONTH, monthOfYear);
        c.set(java.util.Calendar.DAY_OF_MONTH, dayOfMonth);

        int day = view.getDayOfMonth();
        int month = view.getMonth() + 1;
        int getYear = view.getYear();

        String date = "" + String.valueOf(month) + "/" + String.valueOf(day)
                + "/" + String.valueOf(getYear);
        tvDate.setText(date);
    }


//    private AdapterView.OnItemClickListener mAutocompleteClickListener = new AdapterView.OnItemClickListener() {
//        @Override
//        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//            final AutocompletePrediction item = (AutocompletePrediction) mAdapter.getItem(i);
//            final String placeId = item.getPlaceId();
//            final CharSequence primaryText = item.getPrimaryText(null);
//
//            Log.i("TAG", "Autocomplete item selected: " + primaryText);
//
//            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId);
//            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
//
//            Toast.makeText(getApplicationContext(), "Clicked: " + primaryText, Toast.LENGTH_SHORT).show();
//            Log.i("TAG", "Called getPlaceById to get Place details for " + placeId);
//
//        }
//    };
//
//    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
//        @Override
//        public void onResult(@NonNull PlaceBuffer places) {
//            if (!places.getStatus().isSuccess()) {
//                // Request did not complete successfully
//                Log.e("TAG", "Place query did not complete. Error: " + places.getStatus().toString());
//                places.release();
//                return;
//            }
//
//            // Get the Place object from the buffer.
//            final Place place = places.get(0);
//
//            // Format details fo the place for display and show it in a TextView
//            // mPlaceDetailsText.setText(formatPlaceDetails(getResources(), place.getName(), place.getId(),
//            // place.getAddress(), place.getPhoneNumber(), place.getWebsiteUri()));
//
//            // Display the third party attributions if set.
//            final CharSequence thirdPartyAttribution = places.getAttributions();
//            if (thirdPartyAttribution == null) {
//                mPlaceDetailsAttribution.setVisibility(View.GONE);
//            } else {
//                mPlaceDetailsAttribution.setVisibility(View.VISIBLE);
//                mPlaceDetailsAttribution.setText(Html.fromHtml(thirdPartyAttribution.toString()));
//            }
//
//            Log.i("TAG", "Place details received: " + place.getName());
//            places.release();
//        }
//    };

    // formatPlaceDetails

//    @Override
//    public void onConnectionFailed(ConnectionResult connectionResult) {
//
//        Log.e("TAG", "onConnectionFailed: ConnectionResult.getErrorCode() = "
//                + connectionResult.getErrorCode());
//
//        // TODO(Developer): Check error code and notify the user of error state and resolution.
//        Toast.makeText(this,
//                "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(),
//                Toast.LENGTH_SHORT).show();
//    }



    public void onAddClick(View view){
        // Launch TakePic
        //Intent i = new Intent(this, TakePicActivity.class);
        Intent i = new Intent(this, CameraActivity.class);
        startActivity(i);
    }
}
