package annikatsai.portfolioapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Map;

import annikatsai.portfolioapp.Models.Location;
import annikatsai.portfolioapp.Models.Post;

public class TimelineActivity extends AppCompatActivity implements PostsArrayAdapter.PostsArrayAdapterCallback {

    private DatabaseReference mDataBaseReference;
    private String TAG = "TimelineActivity";
    private ArrayList<Post> posts = new ArrayList<>();
    private PostsArrayAdapter postAdapter;
    private RecyclerView rvPosts;
    private Post oldPost;
    private int postPosition;

    private FirebaseStorage mStorage;
    StorageReference storageRef;

    StorageReference picRef;
    String fileName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        // Sets up array list, adapter, list view, and listeners
        postAdapter = new PostsArrayAdapter(this, posts);
        postAdapter.setCallback(this);

        rvPosts = (RecyclerView) findViewById(R.id.rvPosts);
        rvPosts.setAdapter(postAdapter);
        rvPosts.setLayoutManager(new LinearLayoutManager(this));
        rvPosts.setHasFixedSize(true);

        setupViewListeners();

        // Customizing toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("");
        Typeface titleFont = Typeface.createFromAsset(getAssets(), "fonts/Pacifico.ttf");
        toolbarTitle.setText("Roam");
        toolbarTitle.setTypeface(titleFont);

        mDataBaseReference = FirebaseDatabase.getInstance().getReference();

        // Create an instance of FirebaseStorage
        mStorage = FirebaseStorage.getInstance();
        // Create a storage reference from our app. Note: might need to edit gs:// below
        storageRef = mStorage.getReferenceFromUrl("gs://travel-portfolio-app.appspot.com");

        // Create listener for reading data from database
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            DatabaseReference myRef = database.getReference("/users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/posts");
            myRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    // add to adapter
                    Post post = dataSnapshot.getValue(Post.class);
//                    postAdapter.add(post);
                    posts.add(post);
                    postAdapter.notifyDataSetChanged();
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//                    Post post = dataSnapshot.getValue(Post.class);
//                    postAdapter.remove(oldPost);
//                    postAdapter.add(post);
//                    postAdapter.notifyDataSetChanged();
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {}
                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
        }
    }

    private void setupViewListeners() {
        ItemClickSupport.addTo(rvPosts).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                postPosition = position;
                launchViewPost(position);
            }
        });
    }

    public void launchViewPost(int position) {
        Intent i = new Intent(TimelineActivity.this, ViewPostActivity.class);
        i.putExtra("post", Parcels.wrap(posts.get(position)));
        startActivity(i);
    }

    public int getPostPosition(Post post) {
        return postPosition;
    }

    int REQUEST_CODE = 5;

    @Override
    public void launchEditPost(int position) {
        Intent i = new Intent(TimelineActivity.this, EditPostActivity.class);
        oldPost = posts.get(position);
        i.putExtra("editPost", Parcels.wrap(posts.get(position)));
        i.putExtra("code", "fromTimeline");
        startActivityForResult(i, REQUEST_CODE);
    }

    @Override
    public void deletePost(int position) {
        postPosition = position;
        final int pos = position;

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Delete");
        alertDialogBuilder
                .setMessage("Are you sure you want to delete?")
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                        final Post post = posts.get(pos);
                        fileName = posts.get(pos).fileName;
                        // Toast.makeText(getApplicationContext(), "File Name: " + fileName, Toast.LENGTH_LONG).show();
                        if((fileName != null) && !(fileName.isEmpty())){
                            picRef = storageRef.child("users").child(userId).child(fileName);
                        }
                        mDataBaseReference.child("users").child(userId).child("posts").child(post.key).removeValue(new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if (databaseError != null) {
                                    Toast.makeText(TimelineActivity.this, "Data could not be deleted. " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                } else {
//                                    postAdapter.remove(post);
                                    posts.remove(post);
                                    postAdapter.notifyDataSetChanged();

                                    if (!(fileName.isEmpty())) {
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
                                    postAdapter.notifyDataSetChanged();
                                    Toast.makeText(TimelineActivity.this, "Data successfully deleted", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        mDataBaseReference.child("users").child(userId).child("locations").child(post.locationKey).removeValue(new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if (databaseError != null) {
                                    Toast.makeText(TimelineActivity.this, "Data could not be deleted. " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(TimelineActivity.this, "Data successfully deleted", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
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

    // editing by deleting old and adding new?
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Toast.makeText(TimelineActivity.this, "Back in TimelineActivity", Toast.LENGTH_SHORT).show();
        final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE) {
                final Post post = Parcels.unwrap(data.getParcelableExtra("editPost"));
                Location loc = Parcels.unwrap(data.getParcelableExtra("latLngLocation"));
                Map<String, Object> editedLocation = loc.locationToMap();
                mDataBaseReference.child("users").child(userId).child("locations").child(post.locationKey).setValue(editedLocation, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError != null) {
                            Toast.makeText(TimelineActivity.this, "Location could not be changed" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(TimelineActivity.this, "Location successfully changed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                Map<String, Object> editedPost = post.toMap();
                mDataBaseReference
                        .child("users")
                        .child(userId)
                        .child("posts")
                        .child(post.key)
                        .setValue(editedPost, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if (databaseError != null) {
                                    Toast.makeText(TimelineActivity.this, "Data could not be changed. " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(TimelineActivity.this, "Data successfully changed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                posts.remove(oldPost);
                posts.add(post);
                postAdapter.notifyDataSetChanged();
                if(post.fileName == null){
                    fileName = "";
                }

            } else if (requestCode == SEARCHACTIVITY_REQUESTCODE) {
//                postAdapter.clear();
                for (int i = 0; i < postAdapter.getItemCount(); i++) {
                    posts.remove(i);
                }
                Query searchQuery = mDataBaseReference.child("users").child(userId).child("posts").orderByKey();
                searchQuery.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Post post = dataSnapshot.getValue(Post.class);
//                        postAdapter.add(post);
                        posts.add(post);
                        postAdapter.notifyDataSetChanged();
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
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.signOut) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Sign Out");
            alertDialogBuilder
                    .setMessage("Are you sure you want to sign out?")
                    .setCancelable(true)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            LoginManager.getInstance().logOut();        // Facebook sign out
                            startActivity(new Intent(TimelineActivity.this, LoginActivity.class));
                            finish();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public void onPostView(View view) {
        Intent i = new Intent(this, PostActivity.class);
        startActivity(i);
    }

    int SEARCHACTIVITY_REQUESTCODE = 10;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                Intent intent = new Intent(TimelineActivity.this, SearchActivity.class);
                intent.putExtra("query", query);
                startActivityForResult(intent, SEARCHACTIVITY_REQUESTCODE);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
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
