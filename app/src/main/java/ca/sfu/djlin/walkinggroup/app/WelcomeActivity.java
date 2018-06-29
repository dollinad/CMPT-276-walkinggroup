package ca.sfu.djlin.walkinggroup.app;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import ca.sfu.djlin.walkinggroup.R;

public class WelcomeActivity extends AppCompatActivity {

    private static final String TAG = "WelcomeActivity";

    // Used for checking correct version of Google Play Services
    private static final int ERROR_DIALOG_REQUEST = 9001;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_activity);

        //setting adduser icon
        Button signup=findViewById(R.id.sign_up);
        Drawable drawable_signup=getResources().getDrawable(R.drawable.adduser);
        drawable_signup.setBounds(0,0, (int) (drawable_signup.getIntrinsicHeight()*0.07),
                (int)(drawable_signup.getIntrinsicHeight()*0.07));
        signup.setCompoundDrawables(drawable_signup, null, null, null);

        //setting the login icon
        Button login= findViewById(R.id.login);
        Drawable drawable_login=getResources().getDrawable(R.drawable.login);
        drawable_login.setBounds(0,0, (int) (drawable_login.getIntrinsicHeight()*0.05),
                (int)(drawable_login.getIntrinsicHeight()*0.05));
        login.setCompoundDrawables(drawable_login, null, null, null);

        //SETUP SIGNUP BUTTON
        setupSignup();

        //SETUP LOGIN BUTTON
        setupLogin();

        //Check for Google Play Services
        if (isServicesOK()) {
            init();
        }
    }

    private void setupSignup(){
        Button signup=findViewById(R.id.sign_up);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signup_intent=SignupActivity.LaunchIntent_signup(WelcomeActivity.this);
                startActivity(signup_intent);
            }
        });

    }

    private void setupLogin() {
    }

    private void init () {
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WelcomeActivity.this, MapActivity.class);
                startActivity(intent);
            }
        });

    }

    // Check to see if Google Play Services is properly enabled and up-to-date
    public boolean isServicesOK() {
        Log.d(TAG, "isServicesOK: Checking:");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(WelcomeActivity.this);

        if (available == ConnectionResult.SUCCESS) {
            // Everything is fine
            Log.d(TAG, "isServicesOK: Google Play Services is working!");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Log.d(TAG, "isServicesOK: Error occured but we can fix it!");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(WelcomeActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "You can't make map requests!", Toast.LENGTH_SHORT).show();
        }

        return false;

    }
}
