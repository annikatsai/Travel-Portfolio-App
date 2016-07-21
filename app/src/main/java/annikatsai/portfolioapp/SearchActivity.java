package annikatsai.portfolioapp;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Map;

import annikatsai.portfolioapp.Models.Post;

public class SearchActivity extends AppCompatActivity implements PostsArrayAdapter.PostsArrayAdapterCallback{

    private PostsArrayAdapter postsArrayAdapter;
    private ArrayList<Post> posts = new ArrayList<>();
    private DatabaseReference mDataBaseReference;
    private ListView lvSearchResults;
    private Post oldPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        String query = getIntent().getStringExtra("query");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("");
        Typeface titleFont = Typeface.createFromAsset(getAssets(), "fonts/Pacifico.ttf");
        toolbarTitle.setText("Search Results for: " + query);
        toolbarTitle.setTypeface(titleFont);

        lvSearchResults = (ListView) findViewById(R.id.lvSearchResults);
        postsArrayAdapter = new PostsArrayAdapter(this, posts);
        postsArrayAdapter.setCallback(this);
        lvSearchResults.setAdapter(postsArrayAdapter);
        mDataBaseReference = FirebaseDatabase.getInstance().getReference();

        performSearch(query);
        setupViewListeners();
    }

    private void performSearch(String query) {
        final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Query searchQuery = mDataBaseReference.child("users").child(userId).child("posts").orderByChild("title").equalTo(query);
        searchQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Post post = dataSnapshot.getValue(Post.class);
                postsArrayAdapter.add(post);
                postsArrayAdapter.notifyDataSetChanged();
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    public void onFinishSearch(View view) {
        setResult(RESULT_OK);
        finish();
    }

    private void setupViewListeners() {
        lvSearchResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                launchViewPost(i);
            }
        });
    }

    public void launchViewPost(int position) {
        Intent i = new Intent(SearchActivity.this, ViewPostActivity.class);
        i.putExtra("post", Parcels.wrap(posts.get(position)));
        startActivity(i);
    }

    int REQUEST_CODE = 10;

    @Override
    public void launchEditPost(int position) {
        oldPost = posts.get(position);
        Intent i = new Intent(SearchActivity.this, EditPostActivity.class);
        i.putExtra("editPost", Parcels.wrap(posts.get(position)));
        i.putExtra("code", "fromSearchActivity");
        startActivityForResult(i, REQUEST_CODE);
    }

    @Override
    public void deletePost(int position) {
        final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final Post post = posts.get(position);
        mDataBaseReference.child("users").child(userId).child("posts").child(post.getKey()).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Toast.makeText(SearchActivity.this, "Data could not be deleted. " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    postsArrayAdapter.remove(post);
                    postsArrayAdapter.notifyDataSetChanged();
                    Toast.makeText(SearchActivity.this, "Data successfully deleted", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // editing by deleting old and adding new
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Toast.makeText(SearchActivity.this, "Back in SearchActivity", Toast.LENGTH_SHORT).show();
        final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            final Post post = Parcels.unwrap(data.getParcelableExtra("editPost"));
            Map<String, Object> editedPost = post.toMap();
            mDataBaseReference
                    .child("users")
                    .child(userId)
                    .child("posts")
                    .child(post.getKey())
                    .setValue(editedPost, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError != null) {
                                Toast.makeText(SearchActivity.this, "Data could not be changed. " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            } else {
                                postsArrayAdapter.remove(oldPost);
                                postsArrayAdapter.add(post);
                                postsArrayAdapter.notifyDataSetChanged();
                                Toast.makeText(SearchActivity.this, post.getTitle(), Toast.LENGTH_SHORT).show();
                                Toast.makeText(SearchActivity.this, "Data successfully changed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}
