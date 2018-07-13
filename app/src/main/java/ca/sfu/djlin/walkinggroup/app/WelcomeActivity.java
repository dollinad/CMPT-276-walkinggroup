package ca.sfu.djlin.walkinggroup.app;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import ca.sfu.djlin.walkinggroup.R;
import ca.sfu.djlin.walkinggroup.proxy.ProxyBuilder;
import ca.sfu.djlin.walkinggroup.proxy.WGServerProxy;

public class WelcomeActivity extends AppCompatActivity {

    private static final String TAG = "WelcomeActivity";

    private WGServerProxy proxy;
    public static Handler mMailCheckHandler;
    public static Runnable mMailStatusChecker;

    // Used for checking correct version of Google Play Services
    private static final int ERROR_DIALOG_REQUEST = 9001;
    public static final int REQUEST_CODE_LAUNCH_SIGNUP = 1111;
    public static final int REQUEST_CODE_LAUNCH_LOGIN = 1112;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // Build the server proxy
        proxy = ProxyBuilder.getProxy(getString(R.string.apikey));

        // Check for Google Play Services
        if (isServicesOK()) {
            // Setup buttons
            setupSignup();
            setupLogin();
            setUpReadMe();
            // Check user session
            isUserLoggedIn();
        }
    }

    private void setUpReadMe() {
        Button btn=findViewById(R.id.welcome_readme);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= ReadMe.launchReadMe(WelcomeActivity.this);
                startActivity(intent);
            }
        });
    }

    private void setupSignup(){
        Button signupBtn = findViewById(R.id.sign_up_btn);

        // Draw button icons
        Drawable drawableSignup = getResources().getDrawable(R.drawable.ic_adduser);
        drawableSignup.setBounds(0,0, (int) (drawableSignup.getIntrinsicHeight()),
                (int)(drawableSignup.getIntrinsicHeight()));
        signupBtn.setCompoundDrawables(drawableSignup, null, null, null);

        // Onclick listener
        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signupIntent = SignupActivity.launchIntentSignup(WelcomeActivity.this);
                startActivityForResult(signupIntent, REQUEST_CODE_LAUNCH_SIGNUP);
            }
        });
    }

    private void setupLogin() {
        Button loginBtn = findViewById(R.id.login_btn);

        // Draw button icons
        Drawable drawableLogin = getResources().getDrawable(R.drawable.ic_check);
        drawableLogin.setBounds(0,0, (int) (drawableLogin.getIntrinsicHeight()),
                (int)(drawableLogin.getIntrinsicHeight()));
        loginBtn.setCompoundDrawables(drawableLogin, null, null, null);

        // Onclick listener
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginIntent = LoginActivity.launchIntentLogin(WelcomeActivity.this);
                startActivityForResult(loginIntent, REQUEST_CODE_LAUNCH_LOGIN);
            }
        });
    }

    private void isUserLoggedIn() {
        // Check if user is currently logged in with Shared Preferences
        String[] data = getData(getApplicationContext());

        // If Shared Preferences is not empty
        Log.d(TAG, "isUserLoggedIn: " + data[0]);

        if(data[0] != null) {
            String token = data[0];
            proxy = ProxyBuilder.getProxy(getString(R.string.apikey), token);

            Log.i("TEST", "User is logged in!");

            // Start background task test
            // Note: To be used for checking for new messages
            mMailCheckHandler = new Handler();
            mMailStatusChecker = new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG, "Check for new mail");
                    mMailCheckHandler.postDelayed(mMailStatusChecker, 60000);
                }
            };
            mMailCheckHandler.post(mMailStatusChecker);
            // End background task

            // End start background task
            Intent intent = MapActivity.launchIntentMap(WelcomeActivity.this);
            startActivity(intent);
            finish();
        }

    }



    // Getting the data token and email using Shared Preferences
    private String[] getData(Context context) {
        SharedPreferences preferences = getSharedPreferences("User Session", MODE_PRIVATE);

        // Store values and return it
        String[] returnedData = new String[3];
        returnedData[0] = preferences.getString("Token", null);
        returnedData[1] = preferences.getString("Email", null);
        return returnedData;
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
            Log.d(TAG, "isServicesOK: Error occurred but we can fix it!");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(WelcomeActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(WelcomeActivity.this, WelcomeActivity.this.getString(R.string.map_request_issue_toast), Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public static Intent launchWelcomeIntent (Context context) {
        Intent intent = new Intent(context, WelcomeActivity.class);
        return intent;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // If the user already has a token, exit the application
        if(requestCode == REQUEST_CODE_LAUNCH_SIGNUP || requestCode == REQUEST_CODE_LAUNCH_LOGIN) {
            SharedPreferences preferences = getSharedPreferences("User Session", MODE_PRIVATE);
            String[] returnedData = new String[1];
            returnedData[0] = preferences.getString("Token", null);
            if (returnedData[0] != null) {
                finish();
            }
        }
    }
}
