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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Map;

import annikatsai.portfolioapp.Models.Post;

public class TimelineActivity extends AppCompatActivity {

    private DatabaseReference mDataBaseReference;
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

        mDataBaseReference = FirebaseDatabase.getInstance().getReference();

        // Create listener for reading data from database
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
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
                Post post = dataSnapshot.getValue(Post.class);
                postAdapter.add(post);
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                // find item in adapter and remove
                Post post = dataSnapshot.getValue(Post.class);
                postAdapter.remove(post);
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // Sets up array list, adapter, and list view
        posts = new ArrayList<>();
        postAdapter = new PostsArrayAdapter(this, posts);
        lvPosts = (ListView) findViewById(R.id.lvPosts);
        lvPosts.setAdapter(postAdapter);

        setupViewListeners();
    }


    // Think of ways to display this to user
    // - buttons on list view
    // - pull up menu when user clicks on the item
    private void setupViewListeners() {

        // View Posts
//        lvPosts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                // Post post equals to post selected
//                // Post post = posts.get(i);
//                // notify data set changed
//                // postAdapter.notifyDataSetChanged();
//                // launch view post activity
//                launchViewPost(i);
//
//            }
//        });

        // Edit Posts
        lvPosts.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                // Post post equals to post selected
                // Post post = posts.get(i);
                // notify data set changed
                // postAdapter.notifyDataSetChanged();
                // launch edit post activity
                launchEditPost(i);
                return true;
            }
        });


        // Deleting Posts
//        final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        lvPosts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Post post = posts.get(i);
//                mDataBaseReference.child("users").child(userId).child("posts").child(post.getKey()).removeValue(new DatabaseReference.CompletionListener() {
//                    @Override
//                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
//                        if (databaseError != null) {
//                            Toast.makeText(TimelineActivity.this, "Data could not be deleted. " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
//                        } else {
//                            Toast.makeText(TimelineActivity.this, "Data successfully deleted", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//            }
//        });
    }

    private void launchViewPost(int position) {
        Intent i = new Intent(TimelineActivity.this, ViewPostActivity.class);
        i.putExtra("post", Parcels.wrap(posts.get(position)));
        startActivity(i);
    }

    int REQUEST_CODE = 5;

    private void launchEditPost(int position) {
        Intent i = new Intent(TimelineActivity.this, EditPostActivity.class);
        i.putExtra("editPost", Parcels.wrap(posts.get(position)));
        startActivityForResult(i, REQUEST_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            Post post = Parcels.unwrap(data.getParcelableExtra("editPost"));
            Map<String, Object> editedPost = post.toMap();
            mDataBaseReference
                    .child("users")
                    .child(userId)
                    .child("posts")
                    .child(post.getKey())
                    .updateChildren(editedPost, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        Toast.makeText(TimelineActivity.this, "Data could not be changed. " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(TimelineActivity.this, "Data successfully changed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            postAdapter.remove(post);
        }
    }

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
