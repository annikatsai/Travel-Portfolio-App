package annikatsai.portfolioapp;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

    }
}
