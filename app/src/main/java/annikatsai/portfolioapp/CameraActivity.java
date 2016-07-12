package annikatsai.portfolioapp;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

// Roles of the Activity
// 1. Navigation between activities and fragments
// 2. Communication between fragments
// 3. Adding and removing fragments
// 4. ActionBar

public class CameraActivity extends AppCompatActivity {

    TakePicFragment takePicFragment;
    UploadPicFragment uploadPicFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        takePicFragment = new TakePicFragment();
        uploadPicFragment = new UploadPicFragment();
    }

    public void onOneClick(View view) {
        // 1. Get support fragment manager
        FragmentManager fm = getSupportFragmentManager();

        // 2. Create a transaction
        FragmentTransaction ft = fm.beginTransaction();

        // 3. Add or remove the fragment
        ft.replace(R.id.flContainer, uploadPicFragment);
        ft.addToBackStack("one");

        // 4. Commit the transaction
        ft.commit();
    }

    public void onTwoClick(View view) {
        // 1. Get support fragment manager
        FragmentManager fm = getSupportFragmentManager();

        // 2. Create a transaction
        FragmentTransaction ft = fm.beginTransaction();

        // 3. Add or remove the fragment
        ft.replace(R.id.flContainer, takePicFragment);
        ft.addToBackStack("two");

        // 4. Commit the transaction
        ft.commit();
    }
}
