package annikatsai.portfolioapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;

import annikatsai.portfolioapp.Models.Post;

public class TimelineActivity extends AppCompatActivity {

    ArrayList<Post> posts;
    PostsArrayAdapter postAdapter;
    ListView lvPosts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        // Sets up array list, adapter, and list view
        posts = new ArrayList<>();
        postAdapter = new PostsArrayAdapter(this, posts);
        lvPosts = (ListView) findViewById(R.id.lvPosts);
        lvPosts.setAdapter(postAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    public void onProfileView(MenuItem mi) {
        // Launch Profile
        Intent i = new Intent(this, ProfileActivity.class);
        startActivity(i);
    }

    public void onPostView(MenuItem item) {
        // Launch Post
        Intent i = new Intent(this, PostActivity.class);
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_timeline, menu);

//        MenuItem searchItem = menu.findItem(R.id.action_search);
//        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
//
//        int searchTextID = android.support.v7.appcompat.R.id.search_src_text;
//        TextView textView = (TextView) searchView.findViewById(searchTextID);
//        textView.setTextColor(Color.BLACK);
//        ((EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text))
//                .setHintTextColor(Color.BLACK);
//
//
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                // perform query here
//                Intent i = new Intent(TimelineActivity.this, SearchActivity.class);
//                i.putExtra("q", query);
//                startActivity(i);
//
//                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
//                // see https://code.google.com/p/android/issues/detail?id=24599
//                searchView.clearFocus();
//
//                return true;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                return false;
//            }
//        });
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }
}
