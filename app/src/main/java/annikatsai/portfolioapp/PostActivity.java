package annikatsai.portfolioapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class PostActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
    }

    public void onAddClick(View view){
        // Launch TakePic
        //Intent i = new Intent(this, TakePicActivity.class);
        Intent i = new Intent(this, CameraActivity.class);
        startActivity(i);
    }
}
