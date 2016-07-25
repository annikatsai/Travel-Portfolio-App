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

    //private CallbackManager mCallbackManager;
    //ShareDialog shareDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);

        //shareDialog = new ShareDialog(this);

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

//        Button btnshareFB = (Button) findViewById(R.id.btnShare);
//        btnshareFB.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (ShareDialog.canShow(ShareLinkContent.class)) {
//                    ShareLinkContent linkContent = new ShareLinkContent.Builder()
//                            .setContentTitle("How to integrate Linkedin from your app")
//                            .setImageUrl(Uri.parse("https://www.numetriclabz.com/wp-content/uploads/2015/11/114.png"))
//                            .setContentDescription(
//                                    "simple LinkedIn integration")
//                            .setContentUrl(Uri.parse("https://www.numetriclabz.com/android-linkedin-integration-login-tutorial/"))
//                            .build();
//
//                    shareDialog.show(linkContent);  // Show facebook ShareDialog
//                }
//            }
//        });

        tvTitle.setText(post.getTitle());
        tvLocation.setText(post.location);
        tvDate.setText(post.getDate());
        tvBody.setText(post.getBody());
    }


    public void onFinishView(View view) {
        this.finish();
    }
}
