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
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.firebase.auth.FirebaseAuth;

import org.parceler.Parcels;

import annikatsai.portfolioapp.Models.Post;

public class EditPostActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private java.util.Calendar c = java.util.Calendar.getInstance();
    EditText etTitle;
    TextView tvLocation;
    TextView tvDate;
    EditText etBody;
    private String postKey;
    private Post editPost;

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

        editPost = Parcels.unwrap(getIntent().getParcelableExtra("editPost"));

        etTitle = (EditText) findViewById(R.id.etTitle);
        etTitle.append("");
        tvLocation = (TextView) findViewById(R.id.tvLocation);
        tvDate = (TextView) findViewById(R.id.tvDate);
        etBody = (EditText) findViewById(R.id.etBody);
        etBody.append("");

        postKey = editPost.getKey();
        if (editPost.getTitle().equals("")) {
            etTitle.setText("");
        } else {
            etTitle.append(editPost.getTitle());
        }
        if (editPost.getLocation().equals("")) {
            tvLocation.setText("");
        } else {
            tvLocation.append(editPost.getLocation());
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
    }

    public void onFinishEdit(View v) {
        final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Post post = new Post(userId, etTitle.getText().toString(), etBody.getText().toString(), tvLocation.getText().toString(), tvDate.getText().toString(), postKey);
        Intent i = new Intent(EditPostActivity.this, TimelineActivity.class);
        i.putExtra("editPost", Parcels.wrap(post));
//        i.putExtra("oldPost", Parcels.wrap(editPost));
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

    public void onAddClick(View view){
        Intent i = new Intent(this, CameraActivity.class);
        startActivity(i);
    }
}
