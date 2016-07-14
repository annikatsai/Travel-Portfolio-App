package annikatsai.portfolioapp;

import android.app.Application;

import com.facebook.FacebookSdk;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(getApplicationContext());
    }
}
