package annikatsai.portfolioapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

import annikatsai.portfolioapp.Models.Post;

public class TimelineActivity extends AppCompatActivity {

    private DatabaseReference mPostReference;
    private String TAG = "TimelineActivity";
    private ArrayList<Post> posts;
    private PostsArrayAdapter postAdapter;
    private ListView lvPosts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        // Customizing toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("");
        Typeface titleFont = Typeface.createFromAsset(getAssets(), "fonts/Pacifico.ttf");
        toolbarTitle.setTypeface(titleFont);

        // Create listener for reading data from database
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference myRef = database.getReference("https://fluted-alloy-136917.firebaseio.com/");
//        myRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Log.d(TAG, "Read success");
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.d(TAG, "Read failed");
//            }
//        });


//        String mPostKey = getIntent().getStringExtra("postKey");
//        if (mPostKey == null) {
//            throw new IllegalArgumentException("Must pass key");
//        }
//        mPostReference = FirebaseDatabase.getInstance().getReference().child("posts").child(mPostKey);

        // Sets up array list, adapter, and list view
        posts = new ArrayList<>();
        postAdapter = new PostsArrayAdapter(this, posts);
        lvPosts = (ListView) findViewById(R.id.lvPosts);
        lvPosts.setAdapter(postAdapter);
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//
//        ValueEventListener postListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Post post = dataSnapshot.getValue(Post.class);
//                postAdapter.add(post);
//                postAdapter.notifyDataSetChanged();
//                // Update UI
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
//                Toast.makeText(TimelineActivity.this, "Post could not load", Toast.LENGTH_SHORT).show();
//            }
//        };
//        mPostReference.addValueEventListener(postListener);
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.signOut) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public void onProfileView(MenuItem mi) {
        // Launch Profile
        Intent i = new Intent(this, ProfileActivity.class);
        startActivity(i);
    }

    public void onPostView(View view) {
        Intent i = new Intent(this, PostActivity.class);
        startActivity(i);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }

}
