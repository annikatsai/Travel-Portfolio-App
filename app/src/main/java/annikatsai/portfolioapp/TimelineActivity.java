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

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import annikatsai.portfolioapp.Models.Post;

public class TimelineActivity extends AppCompatActivity {

    private DatabaseReference mPostReference;
    private DatabaseReference mDatabase;
    private String TAG = "TimelineActivity";
    private ArrayList<Post> posts;
    private PostsArrayAdapter postAdapter;          // need to get count for profile, maybe add to database
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
        // Might have to have the childAdded listener and then the add value event listener
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("/users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/posts");
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                // add to adapter
                Post post = dataSnapshot.getValue(Post.class);
                postAdapter.add(post);
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                // find item in adapter and update
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                // find item in adapter and remove
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
//

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
            FirebaseAuth.getInstance().signOut();       // Firebase User sign out
            LoginManager.getInstance().logOut();        // Facebook sign out
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
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

    public void onProfileView(MenuItem item) {
        Intent i = new Intent(this, ProfileActivity.class);
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
    }
}
