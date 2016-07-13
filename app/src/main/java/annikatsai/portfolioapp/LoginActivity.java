package annikatsai.portfolioapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    private LoginButton mLoginButton;
    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        mLoginButton = (LoginButton)findViewById(R.id.login_button);
        mLoginButton.setReadPermissions(Arrays.asList("public_profile", "email"));
                //"public_profile", "email", "user_birthday", "user_friends"));

        // Initial button that prompts user to begin and disappears upon being clicked
        final Button btnBegin = (Button) findViewById(R.id.btnBegin);
        mLoginButton.setVisibility(View.INVISIBLE);

        btnBegin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                ObjectAnimator fadeAway = ObjectAnimator.ofFloat(view, "alpha", 0.0f);
                fadeAway.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
//                        Animation fadeIn = new AlphaAnimation(0, 1);
//                        fadeIn.setInterpolator(new AccelerateInterpolator());
//                        fadeIn.setDuration(1000);
                        mLoginButton.setVisibility(View.VISIBLE);
                    }
                });
                fadeAway.start();
            }
        });

        // Checks if user is already logged in
        AccessToken token;
        token = AccessToken.getCurrentAccessToken();

        if (token == null) {
            //Means user is not logged in
            // Create callback to handle results of login and register callbackManager
            mLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    // Launch TimelineActivity
                    Intent i = new Intent(LoginActivity.this, TimelineActivity.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }

                @Override
                public void onCancel() {
                    Toast.makeText(LoginActivity.this, "Login attempt canceled", Toast.LENGTH_SHORT);
                }

                @Override
                public void onError(FacebookException exception) {
                    Toast.makeText(LoginActivity.this, "Login attempt failed", Toast.LENGTH_SHORT);
                }
            });
        } else {
            Intent i = new Intent(LoginActivity.this, TimelineActivity.class);
            startActivity(i);
        }
    }

    // Handles return from login
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    // onClick method from login button
    public void onLoginButtonClick(View view) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile"));
            }
        });
    }
}
