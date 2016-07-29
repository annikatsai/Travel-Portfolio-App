package annikatsai.portfolioapp;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import annikatsai.portfolioapp.Models.User;

public class ProfileActivity extends AppCompatActivity {

    User user;
    Integer numPosts;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        numPosts = 0;

        // Customizing Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("");

        Typeface titleFont = Typeface.createFromAsset(getAssets(), "fonts/Pacifico.ttf");
        toolbarTitle.setText("Profile");
        toolbarTitle.setTypeface(titleFont);

        // Access Token needed for getting user info
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Loads user info from Facebook Graph API with necessary parameters and parsing JSON
        GraphRequest request = GraphRequest.newMeRequest(
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        // Application code
                        String coverPhoto = "";
                        try {
                            JSONObject JOSource = object.getJSONObject("cover");
                            coverPhoto = JOSource.getString("source");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        user = User.fromJSON(object);
                        user.setCoverPhotoUrl(coverPhoto);
                        populateProfileInfo(user);
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,cover,link");
        request.setParameters(parameters);
        request.executeAsync();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("/users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/posts");
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                numPosts++;
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                numPosts--;
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    // Loading TextViews and ImageViews
    private void populateProfileInfo(final User user) {
        ImageView ivProfilePicture = (ImageView) findViewById(R.id.ivProfilePicture);
        ivProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(user.getLink()));
                startActivity(browserIntent);
            }
        });
        ImageView ivCoverPhoto = (ImageView) findViewById(R.id.ivCoverPhoto);
        ivCoverPhoto.setAlpha(0.5F);
        TextView tvName = (TextView) findViewById(R.id.tvName);
        TextView tvNumPosts = (TextView) findViewById(R.id.tvNumPosts);
        TextView tvEmail = (TextView) findViewById(R.id.tvEmail);

        if (user.getEmail() != null) {
            tvEmail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        } else {
            tvEmail.setText("No email available");
        }

        tvNumPosts.setText(numPosts.toString());
        tvName.setText(user.getName());
        Picasso.with(this).load(user.getCoverPhotoUrl()).into(ivCoverPhoto);
        Picasso.with(this).load("https://graph.facebook.com/" + user.getId() + "/picture?type=large").into(ivProfilePicture);
    }

    public void launchMap(View view) {
        Intent i = new Intent(this, MapsActivity.class);
        startActivity(i);
    }
}
