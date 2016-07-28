package annikatsai.portfolioapp;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import org.parceler.Parcels;

import annikatsai.portfolioapp.Models.Post;

public class ViewPostActivity extends AppCompatActivity {

    private Post post;

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

        post = Parcels.unwrap(getIntent().getParcelableExtra("post"));

        TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
        TextView tvLocation = (TextView) findViewById(R.id.tvLocation);
        TextView tvDate = (TextView) findViewById(R.id.tvDate);
        TextView tvBody = (TextView) findViewById(R.id.tvBody);

        tvTitle.setText(post.title);
        tvLocation.setText(post.location);
        tvDate.setText(post.date);
        tvBody.setText(post.body);
    }

    public void onFinishView(View view) {
        this.finish();
    }

    public void onShareClick(View view) {
        Intent shareIntent = new Intent();
        String title = post.title;
        String body = post.body;
        if (!post.fileName.equals("") || post.fileName != null) {
            Uri pictureUri = Uri.parse(post.fileName);
            shareIntent.putExtra(Intent.EXTRA_STREAM, pictureUri);
        }
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, title);
        shareIntent.putExtra(Intent.EXTRA_TEXT, body);

        shareIntent.setType("image/*");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(shareIntent, "Share"));
    }
}
