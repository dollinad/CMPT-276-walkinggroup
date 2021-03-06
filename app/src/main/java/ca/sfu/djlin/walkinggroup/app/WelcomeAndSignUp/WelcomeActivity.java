package ca.sfu.djlin.walkinggroup.app.WelcomeAndSignUp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import ca.sfu.djlin.walkinggroup.Utilities;
import ca.sfu.djlin.walkinggroup.app.Map.MapActivityDrawer;
import ca.sfu.djlin.walkinggroup.app.ReadMe.ReadMeActivity;
import ca.sfu.djlin.walkinggroup.model.Session;
import ca.sfu.djlin.walkinggroup.model.User;
import ca.sfu.djlin.walkinggroup.proxy.ProxyBuilder;
import ca.sfu.djlin.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

public class WelcomeActivity extends AppCompatActivity {

    private static final String TAG = "WelcomeActivity";

    Session session;
    private WGServerProxy proxy;
    static User userToSend;

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
        Button btn = findViewById(R.id.welcome_readme);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = ReadMeActivity.launchReadMe(WelcomeActivity.this);
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
        session = Session.getSession(getApplicationContext());
        // If Shared Preferences is not empty
        Log.d(TAG, "isUserLoggedIn: " + data[0]);

        if(data[0] != null) {
            String token = data[0];

            Long userId = Long.valueOf(data[2]);

            if(userId != null) {
                proxy = ProxyBuilder.getProxy(getString(R.string.apikey));
                proxy = ProxyBuilder.getProxy(getString(R.string.apikey), token);
                session.setProxy(proxy);
                Call<User> call = proxy.getUserById(userId);
                ProxyBuilder.callProxy(WelcomeActivity.this, call, returnedNothing -> responseSingleton(returnedNothing));
            }

        }
    }

    private void responseSingleton(User returnedNothing) {

        session.setUser(returnedNothing);

        // Start checking for new mail
        Utilities.startMessageChecking(WelcomeActivity.this, proxy, session.getUser());

        Intent intent = MapActivityDrawer.launchIntentMap(WelcomeActivity.this);
        startActivity(intent);
        finish();
    }

    // Getting the data token and email using Shared Preferences
    private String[] getData(Context context) {
        SharedPreferences preferences = getSharedPreferences("User Session", MODE_PRIVATE);

        // Store values and return it
        String[] returnedData = new String[3];
        returnedData[0] = preferences.getString("Token", null);
        returnedData[1] = preferences.getString("Email", null);
        returnedData[2]= String.valueOf(preferences.getLong("User Id", 0));

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
    public static Session sendUser(Context context, Session session){
        SharedPreferences preferences = context.getSharedPreferences("User Session", MODE_PRIVATE);
        String token = preferences.getString("Token", "");
        Long userId = preferences.getLong("User Id", 0);
        WGServerProxy proxy;
        proxy = ProxyBuilder.getProxy(context.getString(R.string.apikey), token);

        if(proxy != null){
            session.setProxy(proxy);
        }

        if(userId != 0) {
            Call<User> call = proxy.getUserById(userId);
            ProxyBuilder.callProxy(context, call, returnedNothing -> session.setUser(returnedNothing));
        }

        return session;
    }
    private static void responseSingletonUser(User returnedNothing) {
        userToSend = returnedNothing;
    }
}
