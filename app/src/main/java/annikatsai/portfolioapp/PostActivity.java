package annikatsai.portfolioapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.Map;

import annikatsai.portfolioapp.Models.Post;
import annikatsai.portfolioapp.Models.User;

public class PostActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private String postKey;
    private String TAG = "PostActivity";
    private TextView tvLocation;
    private EditText etTitle;
    private TextView tvBody;
    private TextView tvDate;
    private DatabaseReference mDatabase;
    private java.util.Calendar c = java.util.Calendar.getInstance();
    private final int REQUEST_CODE = 20;


    Uri photoUri = null;
    private FirebaseStorage mStorage;
    StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        etTitle = (EditText) findViewById(R.id.etTitle);
        tvBody = (TextView) findViewById(R.id.etBody);
        tvLocation = (TextView) findViewById(R.id.tvLocation);
        tvDate = (TextView) findViewById(R.id.tvDate);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Create an instance of FirebaseStorage
        mStorage = FirebaseStorage.getInstance();
        // Create a storage reference from our app
        storageRef = mStorage.getReferenceFromUrl("gs://fluted-alloy-136917.appspot.com");

        // Customizing Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("");

        Typeface titleFont = Typeface.createFromAsset(getAssets(), "fonts/Pacifico.ttf");
        toolbarTitle.setText("Make a Post");
        toolbarTitle.setTypeface(titleFont);
    }

    public void onAddClick(View view) {
        Intent i = new Intent(this, CameraActivity.class);
        startActivityForResult(i, REQUEST_CODE);
    }

    public void onSubmit(View view) {
        if (etTitle.getText() == null || etTitle.getText().toString().equals("")) {
            etTitle.setText("");
        }
        if (tvBody.getText() == null || tvBody.getText().toString().equals("")) {
            tvBody.setText("");
        }
        if (tvLocation.getText() == null || tvLocation.getText().toString().equals("")) {
            tvLocation.setText("");
        }
        if (tvDate.getText() == null || tvDate.getText().toString().equals("")) {
            tvDate.setText("");
        }

        final String title = etTitle.getText().toString();
        final String body = tvBody.getText().toString();
        final String location = tvLocation.getText().toString();
        final String date = tvDate.getText().toString();

        /*STORAGE FIREBASE CODE: START*/
        // Create a reference to "mountains.jpg"
        StorageReference mountainsRef = storageRef.child("mountains.jpg");
        // Create a reference to 'images/mountains.jpg'
        StorageReference mountainImagesRef = storageRef.child("images/mountains.jpg");
        // While the file names are the same, the references point to different files
        mountainsRef.getName().equals(mountainImagesRef.getName());    // true
        mountainsRef.getPath().equals(mountainImagesRef.getPath());    // false

        Uri file = Uri.fromFile(new File(photoUri.getPath()));
        StorageReference riversRef = storageRef.child("images/"+file.getLastPathSegment());
        UploadTask uploadTask = riversRef.putFile(file);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
            }
        });
        /*STORAGE FIREBASE CODE: END*/

        final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabase.child("users").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user == null) {
                    Toast.makeText(PostActivity.this, "Error: could not fetch user.", Toast.LENGTH_SHORT).show();
                } else {
                    composeNewPost(userId, title, body, location, date);
                }
//                Intent i = new Intent(PostActivity.this, TimelineActivity.class);
//                i.putExtra("postKey", postKey);
//                startActivity(i);
                finish();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "getUser:onCancelled", databaseError.toException());
            }
        });
    }

    private void composeNewPost(String userId, String title, String body, String location, String date) {

        postKey = mDatabase.child("users").child(userId).child("posts").push().getKey();
        Post newPost = new Post(userId, title, body, location, date);
        Map<String, Object> postValues = newPost.toMap();
        //mDatabase.child("posts").push().setValue(postValues);

//        Map<String, Object> updateChild = new HashMap<>();
//        updateChild.put("/user/" + userId + "/posts/" + postKey, postValues);
        mDatabase.child("users").child(userId).child("posts").child(postKey).updateChildren(postValues, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Toast.makeText(PostActivity.this, "Data could not be saved. " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PostActivity.this, "Data successfully saved", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                photoUri = data.getData();
                // Toast.makeText(getApplicationContext(), "URI is: " + photoUri.toString(), Toast.LENGTH_SHORT).show();
                TextView tvUri = (TextView) findViewById(R.id.tvUri);
                tvUri.setText(photoUri.toString());
            } else { // RESULT_CANCELED
                Toast.makeText(getApplicationContext(), "Picture wasn't selected!", Toast.LENGTH_SHORT).show();
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
}
