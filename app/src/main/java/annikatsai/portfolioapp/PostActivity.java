package annikatsai.portfolioapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

// extends AppCompatActivity
public class PostActivity extends TakePicActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
    }

    public void onAddClick(View view){
        // Launch TakePic
        Intent i = new Intent(this, TakePicActivity.class);
        startActivity(i);
    }
}
