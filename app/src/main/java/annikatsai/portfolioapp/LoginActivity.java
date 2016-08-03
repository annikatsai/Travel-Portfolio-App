package annikatsai.portfolioapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;

import annikatsai.portfolioapp.Models.User;

public class LoginActivity extends AppCompatActivity {

    private LoginButton mLoginButton;
    private CallbackManager mCallbackManager;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;
    private String TAG = "LoginActivity";
    public static boolean firstLogin = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

        final Animation fadeOutWelcome, rotateIcon;
        fadeOutWelcome = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);
        rotateIcon = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate);

        Typeface titleFont = Typeface.createFromAsset(getAssets(), "fonts/Pacifico.ttf");
        final TextView appTitle = (TextView) findViewById(R.id.tvTitle);
        final TextView welcomePrompt = (TextView) findViewById(R.id.tvWelcome);
        welcomePrompt.setVisibility(View.VISIBLE);
        final ImageView ivIcon = (ImageView) findViewById(R.id.ivIcon);
        ivIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ivIcon.startAnimation(rotateIcon);
            }
        });
        appTitle.setTypeface(titleFont);
        appTitle.setText("Roam");
        welcomePrompt.setTypeface(titleFont);
        welcomePrompt.setText("Welcome to");


        // Auth state listener responds to change in user's sign-in state
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                //FirebaseUser user = firebaseAuth.getCurrentUser();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    // User is signed in
//                    Toast.makeText(LoginActivity.this, "User not null", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    if (firstLogin) {
                        User u = new User(user.getUid(), user.getEmail(), user.getDisplayName());
                        mDatabase.child("users").child(user.getUid()).setValue(u);
                        firstLogin = false;
                    }
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
//                    Toast.makeText(LoginActivity.this, "User null", Toast.LENGTH_SHORT).show();

                }
            }
        };

        mCallbackManager = CallbackManager.Factory.create();
        mLoginButton = (LoginButton)findViewById(R.id.login_button);
        mLoginButton.setReadPermissions("public_profile", "email", "user_friends");

        // If user already logged into Facebook, launch TimelineActivity (skip log in page)
        if (AccessToken.getCurrentAccessToken() != null) {
            Intent i = new Intent(LoginActivity.this, TimelineActivity.class);
            startActivity(i);
        }

        mLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // Launch TimelineActivity

//                Toast.makeText(LoginActivity.this, "Login attempt success", Toast.LENGTH_SHORT).show();
                handleFacebookAccessToken(loginResult.getAccessToken());
//                Toast.makeText(LoginActivity.this, "User: " + FirebaseAuth.getInstance().getCurrentUser().getDisplayName(), Toast.LENGTH_SHORT).show();
                Intent i = new Intent(LoginActivity.this, TimelineActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

//                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//                if (firebaseUser != null) {
//                    User user = new User(firebaseUser.getUid(), firebaseUser.getDisplayName(), firebaseUser.getEmail());
//                    mDatabase.child("users").child(firebaseUser.getUid()).setValue(user);
//                }

            }

            @Override
            public void onCancel() {
//                Toast.makeText(LoginActivity.this, "Login attempt canceled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException exception) {
//                Toast.makeText(LoginActivity.this, "Login attempt failed", Toast.LENGTH_SHORT).show();
            }
        });

        // Initial button that prompts user to begin and disappears upon being clicked
        final Button btnBegin = (Button) findViewById(R.id.btnBegin);
        mLoginButton.setVisibility(View.INVISIBLE);

        // Button animation
        btnBegin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                ObjectAnimator fadeAway = ObjectAnimator.ofFloat(view, "alpha", 0.0f);
                fadeAway.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mLoginButton.setVisibility(View.VISIBLE);
                    }
                });
                fadeAway.start();
                ivIcon.startAnimation(rotateIcon);
//                welcomePrompt.startAnimation(fadeOutWelcome);
                welcomePrompt.startAnimation(fadeOutWelcome);
            }
        });
    }

    // Exchanges Facebook credential for firebase credential and authenticate
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
//                            Toast.makeText(LoginActivity.this, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    // Handles return from login
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);

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
