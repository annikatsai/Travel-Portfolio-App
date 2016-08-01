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

import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import annikatsai.portfolioapp.Models.Post;

public class ViewPostActivity extends AppCompatActivity {

    private Post post;
    private ImageView ivPreview;

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

        tvTitle.setText(post.getTitle());
        tvLocation.setText(post.location);
        tvDate.setText(post.getDate());
        tvBody.setText(post.getBody());
        ivPreview = (ImageView) findViewById(R.id.ivPreview);

        if(post.photoUrl != null && !(post.photoUrl.isEmpty())) {
            // Load image
            if (post.photoType.equals("vertical")) {
                if (post.realOrientation.equals("left")) {
                    Picasso.with(this).load(post.photoUrl).fit().centerCrop().into(ivPreview);
                } else if (post.realOrientation.equals("original")) {
                    Picasso.with(this).load(post.photoUrl).fit().centerCrop().rotate(90f).into(ivPreview);
                } else if (post.realOrientation.equals("right")) {
                    Picasso.with(this).load(post.photoUrl).fit().centerCrop().rotate(180f).into(ivPreview);
                } else { // upsideDown
                    Picasso.with(this).load(post.photoUrl).fit().centerCrop().rotate(270f).into(ivPreview);
                }
            }
            if (post.photoType.equals("horizontal")) {
                if (post.realOrientation.equals("original")) {
                    Picasso.with(this).load(post.photoUrl).fit().centerCrop().into(ivPreview);
                } else if (post.realOrientation.equals("right")) {
                    Picasso.with(this).load(post.photoUrl).fit().centerCrop().rotate(90f).into(ivPreview);
                } else if (post.realOrientation.equals("upsideDown")) {
                    Picasso.with(this).load(post.photoUrl).fit().centerCrop().rotate(180f).into(ivPreview);
                } else { // left
                    Picasso.with(this).load(post.photoUrl).fit().centerCrop().rotate(270f).into(ivPreview);
                }
            }
        } else {
            Picasso.with(this).load(R.drawable.default_photo).fit().centerCrop().into(ivPreview);
        }
    }

    public void onShareClick(View view) {
        Intent shareIntent = new Intent();
        String title = post.getTitle();
        String body = post.getBody();
        if (post.getFileName() != null && !post.getFileName().equals("")) {
            Uri pictureUri = Uri.parse(post.getFileName());
            shareIntent.putExtra(Intent.EXTRA_STREAM, pictureUri);
        }
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, title);
        shareIntent.putExtra(Intent.EXTRA_TEXT, body);

        shareIntent.setType("image/*");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(shareIntent, "Share"));
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
        super.onBackPressed();
    }
}
