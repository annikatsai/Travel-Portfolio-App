package annikatsai.portfolioapp;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import annikatsai.portfolioapp.Models.Location;
import annikatsai.portfolioapp.Models.Post;

public class EditPostActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private java.util.Calendar c = java.util.Calendar.getInstance();
    EditText etTitle;
    TextView tvLocation;
    TextView tvDate;
    EditText etBody;
    private String postKey;
    private Post editPost;
    private String code;
    private String locationKey;
    private Location latLngLocation;

    private FirebaseStorage mStorage;
    private StorageReference storageRef;

    private String fileName;
    private StorageReference picRef;

    private final int CAMERA_REQUEST_CODE = 13;
    private String userId;

    private String newFileName = null;
    private StorageReference newPicRef;
    private Uri downloadUrl;
    private String newPhotoUrl;
    private String photoUrl;

    ImageView ivPreview, ivPlus;

    private String realOrientation;
    private String photoType;
    private String newRealOrientation;
    private String newPhotoType;

    Post post;
    Boolean trash = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("");

        Typeface titleFont = Typeface.createFromAsset(getAssets(), "fonts/Pacifico.ttf");
        toolbarTitle.setText("Edit Post");
        toolbarTitle.setTypeface(titleFont);

        mStorage = FirebaseStorage.getInstance();
        storageRef = mStorage.getReferenceFromUrl("gs://travel-portfolio-app.appspot.com");
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        editPost = Parcels.unwrap(getIntent().getParcelableExtra("editPost"));
        code = getIntent().getStringExtra("code");
        locationKey = editPost.locationKey;
        LatLng latLng = new LatLng(editPost.latitude, editPost.longitude);
        latLngLocation = new Location(latLng, editPost.location);

        etTitle = (EditText) findViewById(R.id.etTitle);
        etTitle.append("");
        tvLocation = (TextView) findViewById(R.id.tvLocation);
        tvDate = (TextView) findViewById(R.id.tvDate);
        etBody = (EditText) findViewById(R.id.etBody);
        etBody.setScroller(new Scroller(getApplicationContext()));
        etBody.setMaxLines(10);
        etBody.setVerticalScrollBarEnabled(true);
        etBody.setMovementMethod(new ScrollingMovementMethod());
        etBody.append("");

        ivPlus = (ImageView) findViewById(R.id.ivPlus);
        ivPreview = (ImageView) findViewById(R.id.ivPreview);
        ivPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAddClick(view);
            }
        });

        postKey = editPost.getKey();
        if (editPost.getTitle().equals("")) {
            etTitle.setText("");
        } else {
            etTitle.append(editPost.getTitle());
        }
        if (editPost.location.equals("")) {
            tvLocation.setText("");
        } else {
            tvLocation.append(editPost.location);
        }
        if (editPost.getDate().equals("")) {
            tvDate.setText("");
        } else {
            tvDate.append(editPost.getDate());
        }
        if (editPost.getBody().equals("")) {
            etBody.setText("");
        } else {
            etBody.append(editPost.getBody());
        }

        fileName = editPost.fileName;
        photoType = editPost.photoType;
        realOrientation = editPost.realOrientation;
        if ((fileName != null) && !(fileName.isEmpty())) {
            picRef = storageRef.child("users").child(userId).child(fileName);
        }
        photoUrl = editPost.photoUrl;
        if ((photoUrl != null) && !(photoUrl.isEmpty())) {
            ivPreview.setImageResource(android.R.color.transparent); //clear out the old image for a recycled view
            loadImage(photoUrl);
            ivPreview.setBackgroundResource(0);
            ivPlus.setVisibility(View.INVISIBLE);
        }
    }

    public void onFinishEdit(View v) {
        final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        latLngLocation.setLocationKey(locationKey);
        if (newFileName != null && !(newFileName.isEmpty())) {
            if (fileName != null && !(fileName.isEmpty())) {
                deletePicRef(picRef);
            }
            post = new Post(userId, etTitle.getText().toString(), etBody.getText().toString(), latLngLocation.name, latLngLocation.latitude, latLngLocation.longitude, tvDate.getText().toString(), postKey, locationKey, newFileName, newPhotoUrl, newRealOrientation, newPhotoType);
        } else if (fileName != null && !(fileName.isEmpty())) {
            post = new Post(userId, etTitle.getText().toString(), etBody.getText().toString(), latLngLocation.name, latLngLocation.latitude, latLngLocation.longitude, tvDate.getText().toString(), postKey, locationKey, fileName, photoUrl, realOrientation, photoType);
        } else {
            post = new Post(userId, etTitle.getText().toString(), etBody.getText().toString(), latLngLocation.name, latLngLocation.latitude, latLngLocation.longitude, tvDate.getText().toString(), postKey, locationKey, "", "", "", "");
        }

        if(trash == true){
            deletePicRef(picRef);
        }

        Intent i = new Intent();
        if (code.equals("fromTimeline")) {
            i = new Intent(EditPostActivity.this, TimelineActivity.class);
        } else if (code.equals("fromSearchActivity")) {
            i = new Intent(EditPostActivity.this, SearchActivity.class);
        }
        i.putExtra("editPost", Parcels.wrap(post));
        i.putExtra("latLngLocation", Parcels.wrap(latLngLocation));
        setResult(RESULT_OK, i);
        finish();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                LatLng latlng = place.getLatLng();
                latLngLocation.latitude = latlng.latitude;
                latLngLocation.longitude = latlng.longitude;
                latLngLocation.setLatLngLocation(latlng);
                latLngLocation.setName(place.getName().toString());
                tvLocation.setText(place.getName());
                Log.i("TAG", "Place: " + place.getName());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i("TAG", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        } else if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                newFileName = data.getExtras().getString("fileName");
                newPicRef = storageRef.child("users").child(userId).child(newFileName);
                downloadUrl = data.getData();
                newPhotoUrl = downloadUrl.toString();
                newRealOrientation = data.getExtras().getString("realOrientation");
                newPhotoType = data.getExtras().getString("photoType");
                ivPreview.setImageResource(android.R.color.transparent);
                editPost.photoUrl = newPhotoUrl;
                editPost.realOrientation = newRealOrientation;
                editPost.photoType = newPhotoType;
                loadImage(newPhotoUrl);
                ivPreview.setBackgroundResource(0);
                ivPlus.setVisibility(View.INVISIBLE);
            }
        }
    }

    // attach to an onclick handler to show the date picker
    public void showDatePickerDialog(View v) {
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
    }

    public void onAddClick(View view) {
        Intent i = new Intent(this, CameraActivity.class);
        i.putExtra("activity", "EditPost");
        startActivityForResult(i, CAMERA_REQUEST_CODE);
    }

    public void hideSoftKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
                        if (newFileName != null && !(newFileName.isEmpty())) {
                            deletePicRef(newPicRef);
                        }
                        if (fileName != null && !(fileName.isEmpty())){
//                            Toast.makeText(EditPostActivity.this, "Don't delete pifRef!", Toast.LENGTH_SHORT).show();
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
//                Toast.makeText(getApplicationContext(), "Error deleting pic from database", Toast.LENGTH_LONG).show();
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

    public void loadImage(String url){
        if (url != null && !(url.isEmpty())) {
            if (editPost.photoType.equals("vertical")) {
                if (editPost.realOrientation.equals("left")) {
                    Picasso.with(this).load(url).fit().centerCrop().into(ivPreview);
                } else if (editPost.realOrientation.equals("original")) {
                    Picasso.with(this).load(url).fit().centerCrop().rotate(90f).into(ivPreview);
                } else if (editPost.realOrientation.equals("right")) {
                    Picasso.with(this).load(url).fit().centerCrop().rotate(180f).into(ivPreview);
                } else { // upsideDown
                    Picasso.with(this).load(url).fit().centerCrop().rotate(270f).into(ivPreview);
                }
            }
            if (editPost.photoType.equals("horizontal")) {
                if (editPost.realOrientation.equals("original")) {
                    Picasso.with(this).load(url).fit().centerCrop().into(ivPreview);
                } else if (editPost.realOrientation.equals("right")) {
                    Picasso.with(this).load(url).fit().centerCrop().rotate(90f).into(ivPreview);
                } else if (editPost.realOrientation.equals("upsideDown")) {
                    Picasso.with(this).load(url).fit().centerCrop().rotate(180f).into(ivPreview);
                } else { // left
                    Picasso.with(this).load(url).fit().centerCrop().rotate(270f).into(ivPreview);
                }
            }
        }
    }

    public void superOnBackPressed() {
        super.onBackPressed();
    }
}
