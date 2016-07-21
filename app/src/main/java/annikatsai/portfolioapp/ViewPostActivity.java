package annikatsai.portfolioapp;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import org.parceler.Parcels;

import annikatsai.portfolioapp.Models.Post;

public class ViewPostActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("");

        Typeface titleFont = Typeface.createFromAsset(getAssets(), "fonts/Pacifico.ttf");
        toolbarTitle.setText("View Post");
        toolbarTitle.setTypeface(titleFont);

        Post post = Parcels.unwrap(getIntent().getParcelableExtra("post"));

        TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
        TextView tvLocation = (TextView) findViewById(R.id.tvLocation);
        TextView tvDate = (TextView) findViewById(R.id.tvDate);
        TextView tvBody = (TextView) findViewById(R.id.tvBody);

        tvTitle.setText(post.getTitle());
        tvLocation.setText(post.getLocation());
        tvDate.setText(post.getDate());
        tvBody.setText(post.getBody());
    }


    public void onFinishView(View view) {
        this.finish();
    }
}
