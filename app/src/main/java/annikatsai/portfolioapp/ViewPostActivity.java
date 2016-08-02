package annikatsai.portfolioapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.io.File;

import annikatsai.portfolioapp.Models.Post;

public class ViewPostActivity extends AppCompatActivity {

    private Post post;
    private ImageView ivPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("");

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

        if (post.photoUrl != null && !(post.photoUrl.isEmpty())) {
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
        ivPreview.setBackgroundResource(0);
    }

    public void onShareClick(View view) {
//        Intent shareIntent = new Intent();
//        shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
//        String title = post.getTitle();
//        String body = post.getBody();
//        if (post.getFileName() != null && !post.getFileName().equals("")) {
//            Uri pictureUri = getPhotoFileUri(post.getFileName());
//            shareIntent.putExtra(Intent.EXTRA_STREAM, pictureUri);
//        }
//
//        shareIntent.putExtra(Intent.EXTRA_TEXT, title);
//        shareIntent.putExtra(Intent.EXTRA_TEXT, body);
//
//        shareIntent.setType("image/*");
//        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        startActivity(Intent.createChooser(shareIntent, "Share"));

        String text = post.getTitle();
        String body = post.getBody();
        Uri pictureUri = Uri.parse(post.photoUrl);
        //Toast.makeText(ViewPostActivity.this, pictureUri.toString(), Toast.LENGTH_SHORT).show();
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, text + ": " + body);
        //shareIntent.putExtra(Intent.EXTRA_STREAM, pictureUri);
        //shareIntent.setType("image/*");
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, pictureUri.toString());
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(shareIntent, "Share images..."));
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
        super.onBackPressed();
    }

    public final String APP_TAG = "ViewPostActivity";

    // Returns true if external storage for photos is available
    private boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }

    // Returns the Uri for a photo stored on disk given the fileName
    public Uri getPhotoFileUri(String fileName) {
        // Only continue if the SD Card is mounted
        if (isExternalStorageAvailable()) {
            // Get safe storage directory for photos
            // Use `getExternalFilesDir` on Context to access package-specific directories.
            // This way, we don't need to request external read/write runtime permissions.
            File mediaStorageDir = new File(
                    getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);
            // Create the storage directory if it does not exist
            if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
                Log.d(APP_TAG, "failed to create directory");
            }
            // Return the file target for the photo based on filename
            return Uri.fromFile(new File(mediaStorageDir.getPath() + File.separator + fileName));
        }
        return null;
    }
}
