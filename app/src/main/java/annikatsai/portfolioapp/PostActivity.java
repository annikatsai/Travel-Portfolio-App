package annikatsai.portfolioapp;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Scroller;
import android.widget.TextView;

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
import com.squareup.picasso.Picasso;

import java.util.Map;

import annikatsai.portfolioapp.Models.Location;
import annikatsai.portfolioapp.Models.Post;
import annikatsai.portfolioapp.Models.User;

public class PostActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    private String postKey, locationKey;
    private String TAG = "PostActivity";
    private TextView tvLocation;
    private ImageView ivPreview;
    private EditText etTitle;
    private TextView etBody;
    private TextView tvDate;
    private DatabaseReference mDatabase;
    private java.util.Calendar c = java.util.Calendar.getInstance();
    private final int REQUEST_CODE = 20;
    private Location latlngLocation;
    private String locationName;
    private FloatingActionButton fabDate, fabLocation, fabSubmit;

    private FirebaseStorage mStorage;
    private StorageReference storageRef;

    private String fileName;
    private StorageReference picRef;
    private String userId;
    private Animation clickExpand, clickContract;

    private Uri downloadUrl;
    private String photoUrl;

    private ImageView ivPlus;

    private String realOrientation;
    private String photoType;

    Boolean trash = false;
    Boolean camera = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        clickExpand = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_fade_in);
        clickContract = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_fade_out);

        etTitle = (EditText) findViewById(R.id.etTitle);
        etBody = (TextView) findViewById(R.id.etBody);
        etBody.setScroller(new Scroller(getApplicationContext()));
        etBody.setMaxLines(10);
        etBody.setVerticalScrollBarEnabled(true);
        etBody.setMovementMethod(new ScrollingMovementMethod());
        tvLocation = (TextView) findViewById(R.id.tvLocation);
        tvDate = (TextView) findViewById(R.id.tvDate);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        fabDate = (FloatingActionButton) findViewById(R.id.fabDate);
        fabLocation = (FloatingActionButton) findViewById(R.id.fabLocation);
        fabSubmit = (FloatingActionButton) findViewById(R.id.fabSubmit);
        fabDate.setVisibility(View.INVISIBLE);
        fabLocation.setVisibility(View.INVISIBLE);
        fabSubmit.setVisibility(View.INVISIBLE);

        ivPreview = (ImageView) findViewById(R.id.ivPreview);
        ivPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAddClick(view);
            }
        });
        ivPlus = (ImageView) findViewById(R.id.ivPlus);

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

        mStorage = FirebaseStorage.getInstance();
        storageRef = mStorage.getReferenceFromUrl("gs://travel-portfolio-app.appspot.com");
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        fabDate.show();
        fabLocation.show();
        fabSubmit.show();
    }

    public void onAddClick(View view) {
        Intent i = new Intent(this, CameraActivity.class);
        i.putExtra("activity", "Post");
        startActivityForResult(i, REQUEST_CODE);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Cancel");
        alertDialogBuilder
                .setMessage("Are you sure you want to exit?")
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        superOnBackPressed();
                        fabDate.hide();
                        fabLocation.hide();
                        fabSubmit.hide();
                        if(fileName != null) {
                            // Delete the file
                            picRef.delete().addOnSuccessListener(new OnSuccessListener() {
                                @Override
                                public void onSuccess(Object o) {}
                                public void onSuccess(Void aVoid) {}
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Uh-oh, an error occurred!
//                                    Toast.makeText(getApplicationContext(), "Error deleting pic from database", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void superOnBackPressed() {
        super.onBackPressed();
    }

    public void onSubmit(View view) {

        fabSubmit.startAnimation(clickExpand);
        fabSubmit.startAnimation(clickContract);

        if (etTitle.getText() == null || etTitle.getText().toString().equals("")) {
            etTitle.setText("");
        }
        if (etBody.getText() == null || etBody.getText().toString().equals("")) {
            etBody.setText("");
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
        if(photoUrl == null){
            photoUrl = "";
        }
        if(realOrientation == null){
            realOrientation = "";
        }

        final String title = etTitle.getText().toString();
        final String body = etBody.getText().toString();
        final String date = tvDate.getText().toString();

        if(trash == true && camera == false){
            deletePicRef(picRef);
            fileName = "";
            realOrientation = "";
            photoType = "";
            photoUrl = "";
            picRef = null;
        }

        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user == null) {
//                    Toast.makeText(PostActivity.this, "Error: could not fetch user.", Toast.LENGTH_SHORT).show();
                } else {
                    Location location;
                    location = addLocationToMap(userId);
                    composeNewPost(userId, title, body, location.name, location.latitude, location.longitude, date, locationKey, fileName, photoUrl, realOrientation, photoType);
                }
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "getUser:onCancelled", databaseError.toException());
            }
        });
        fabDate.hide();
        fabLocation.hide();
        fabSubmit.hide();
    }

    private Location addLocationToMap(String userId) {
        locationKey = mDatabase.child("users").child(userId).child("locations").push().getKey();
        latlngLocation.setLocationKey(locationKey);
        Map<String, Object> locationValues = latlngLocation.locationToMap();
        mDatabase.child("users").child(userId).child("locations").child(locationKey).setValue(locationValues, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
//                    Toast.makeText(PostActivity.this, "Location save failed" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
//                    Toast.makeText(PostActivity.this, "Location save success", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return latlngLocation;
    }

    private void composeNewPost(String userId, String title, String body, String locationName, double latitude, double longitude, String date, String locationKey, String fileName, String photoUrl, String realOrientation, String photoType) {
        postKey = mDatabase.child("users").child(userId).child("posts").push().getKey();
        Post newPost = new Post(userId, title, body, locationName, latitude, longitude, date, postKey, locationKey, fileName, photoUrl, realOrientation, photoType);
        Map<String, Object> postValues = newPost.toMap();
        mDatabase.child("users").child(userId).child("posts").child(postKey).updateChildren(postValues, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
//                    Toast.makeText(PostActivity.this, "Data could not be saved. " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
//                    Toast.makeText(PostActivity.this, "Data successfully saved", Toast.LENGTH_SHORT).show();
                }
            }
        });
        if (fileName !=  null && !fileName.isEmpty()) {
            latlngLocation.setPhoto(fileName, photoUrl, realOrientation, photoType);
            Map<String, Object> locationValues = latlngLocation.locationToMap();
            mDatabase.child("users").child(userId).child("locations").child(locationKey).setValue(locationValues, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null) {
//                        Toast.makeText(PostActivity.this, "Location save failed" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
//                        Toast.makeText(PostActivity.this, "Location save success", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    public void selectLocation(View view) {
        fabLocation.startAnimation(clickExpand);
        fabLocation.startAnimation(clickContract);
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
        fabLocation.clearAnimation();
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, i);
                LatLng loc = place.getLatLng();
                locationName = place.getName().toString();
                latlngLocation.setLatLngLocation(loc);
                latlngLocation.latitude = loc.latitude;
                latlngLocation.longitude = loc.longitude;
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
                if(picRef != null && trash == true){
                    deletePicRef(picRef);
                    fileName = "";
                    realOrientation = "";
                    photoType = "";
                    photoUrl = "";
                    picRef = null;
                    camera = true;
                }
                fileName = i.getExtras().getString("fileName");
                downloadUrl = i.getData();

                realOrientation = i.getExtras().getString("realOrientation");
                photoType = i.getExtras().getString("photoType");

                photoUrl = downloadUrl.toString();
                ivPlus.setVisibility(View.INVISIBLE);

                // Load image
                if (photoType.equals("vertical")) {
                    if (realOrientation.equals("left")) {
                        Picasso.with(this).load(photoUrl).fit().centerCrop().into(ivPreview);
                    } else if (realOrientation.equals("original")) {
                        Picasso.with(this).load(photoUrl).fit().centerCrop().rotate(90f).into(ivPreview);
                    } else if (realOrientation.equals("right")) {
                        Picasso.with(this).load(photoUrl).fit().centerCrop().rotate(180f).into(ivPreview);
                    } else { // upsideDown
                        Picasso.with(this).load(photoUrl).fit().centerCrop().rotate(270f).into(ivPreview);
                    }
                }
                if (photoType.equals("horizontal")) {
                    if (realOrientation.equals("original")) {
                        Picasso.with(this).load(photoUrl).fit().centerCrop().into(ivPreview);
                    } else if (realOrientation.equals("right")) {
                        Picasso.with(this).load(photoUrl).fit().centerCrop().rotate(90f).into(ivPreview);
                    } else if (realOrientation.equals("upsideDown")) {
                        Picasso.with(this).load(photoUrl).fit().centerCrop().rotate(180f).into(ivPreview);
                    } else { // left
                        Picasso.with(this).load(photoUrl).fit().centerCrop().rotate(270f).into(ivPreview);
                    }
                }
                picRef = storageRef.child("users").child(userId).child(fileName);
                ivPreview.setBackgroundResource(0);
            } else { // RESULT_CANCELED
//                Toast.makeText(getApplicationContext(), "Picture wasn't selected!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // attach to an onclick handler to show the date picker
    public void showDatePickerDialog(View v) {
        fabDate.startAnimation(clickExpand);
        fabDate.startAnimation(clickContract);
        hideSoftKeyboard(v);
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
        fabDate.clearAnimation();
    }

    public void hideSoftKeyboard(View view){
        InputMethodManager imm =(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void deletePicRef(StorageReference ref) {
        // Deletes the file
        ref.delete().addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
            }

            public void onSuccess(Void aVoid) {
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
                Toast.makeText(getApplicationContext(), "Error deleting pic from database", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void onRemoveClick(View view) {
        trash = true;
        fileName = "";
        ivPreview.setImageResource(android.R.color.transparent);
        ivPreview.setBackgroundResource(R.drawable.dotted);
        ivPlus.setVisibility(View.VISIBLE);
    }
}
