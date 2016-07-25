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
import com.google.android.gms.maps.model.LatLng;
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

import java.util.Map;

import annikatsai.portfolioapp.Models.Location;
import annikatsai.portfolioapp.Models.Post;
import annikatsai.portfolioapp.Models.User;

public class PostActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    private String postKey, locationKey;
    private String TAG = "PostActivity";
    private TextView tvLocation;
    private EditText etTitle;
    private TextView tvBody;
    private TextView tvDate;
    private DatabaseReference mDatabase;
    private java.util.Calendar c = java.util.Calendar.getInstance();
    private final int REQUEST_CODE = 20;
    private Location latlngLocation;
    private String locationName;
    Uri photoUri = null;

    private FirebaseStorage mStorage;
    StorageReference storageRef;

    String fileName;
    StorageReference picRef;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        etTitle = (EditText) findViewById(R.id.etTitle);
        tvBody = (TextView) findViewById(R.id.etBody);
        tvLocation = (TextView) findViewById(R.id.tvLocation);
        tvDate = (TextView) findViewById(R.id.tvDate);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Customizing Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("");

        Typeface titleFont = Typeface.createFromAsset(getAssets(), "fonts/Pacifico.ttf");
        toolbarTitle.setText("Make a Post");
        toolbarTitle.setTypeface(titleFont);

        latlngLocation = new Location(null, "");

        // Create an instance of FirebaseStorage
        mStorage = FirebaseStorage.getInstance();
        // Create a storage reference from our app. Note: might need to edit gs:// below
        storageRef = mStorage.getReferenceFromUrl("gs://travel-portfolio-app.appspot.com");
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        // Toast.makeText(getApplicationContext(), "User ID: " + userId, Toast.LENGTH_SHORT).show();
    }

    public void onAddClick(View view) {
        Intent i = new Intent(this, CameraActivity.class);
        startActivityForResult(i, REQUEST_CODE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(photoUri != null) {
            // Delete the file
            picRef.delete().addOnSuccessListener(new OnSuccessListener() {
                @Override
                public void onSuccess(Object o) {}
                public void onSuccess(Void aVoid) {}
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Uh-oh, an error occurred!
                    Toast.makeText(getApplicationContext(), "Error deleting pic from database", Toast.LENGTH_LONG).show();
                }
            });
        }
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
        if(fileName == null){
            fileName = "";
        }

        final String title = etTitle.getText().toString();
        final String body = tvBody.getText().toString();
        final String location = tvLocation.getText().toString();
        final String date = tvDate.getText().toString();

        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user == null) {
                    Toast.makeText(PostActivity.this, "Error: could not fetch user.", Toast.LENGTH_SHORT).show();
                } else {
                    Location location;
                    location = addLocationToMap(userId);
                    composeNewPost(userId, title, body, location.name, location.latitude, location.longitude, date, locationKey, fileName);
                }
                finish();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "getUser:onCancelled", databaseError.toException());
            }
        });
    }

    private Location addLocationToMap(String userId) {
        locationKey = mDatabase.child("users").child(userId).child("locations").push().getKey();
        latlngLocation.setLocationKey(locationKey);
        Map<String, Object> locationValues = latlngLocation.locationToMap();
        mDatabase.child("users").child(userId).child("locations").child(locationKey).setValue(locationValues, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Toast.makeText(PostActivity.this, "Location save failed" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PostActivity.this, "Location save success", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return latlngLocation;
    }

    private void composeNewPost(String userId, String title, String body, String locationName, double latitude, double longitude, String date, String locationKey, String fileName) {
        postKey = mDatabase.child("users").child(userId).child("posts").push().getKey();
        Post newPost = new Post(userId, title, body, locationName, latitude, longitude, date, postKey, locationKey, fileName);
        Map<String, Object> postValues = newPost.toMap();

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
    protected void onActivityResult(int requestCode, int resultCode, Intent i) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, i);
                LatLng loc = place.getLatLng();
                locationName = place.getName().toString();
                //latlngLocation = new Location(loc, locationName);
                latlngLocation.setLatLngLocation(loc);
                latlngLocation.setName(locationName);
                tvLocation.setText(place.getName());
                Log.i("TAG", "Place: " + place.getName());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, i);
                Log.i("TAG", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {}
        }
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                photoUri = i.getData();
                fileName = i.getExtras().getString("fileName");
                picRef = storageRef.child("users").child(userId).child(fileName);
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
}
