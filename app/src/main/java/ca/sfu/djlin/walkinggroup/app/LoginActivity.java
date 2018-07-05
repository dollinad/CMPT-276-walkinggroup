package ca.sfu.djlin.walkinggroup.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import ca.sfu.djlin.walkinggroup.R;
import ca.sfu.djlin.walkinggroup.Utilities;
import ca.sfu.djlin.walkinggroup.model.User;
import ca.sfu.djlin.walkinggroup.proxy.ProxyBuilder;
import ca.sfu.djlin.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    //Private User user;
    private WGServerProxy proxy;
    private String userEmailString;
    private String userPasswordString;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login_activity);

        // Build the server proxy
        proxy = ProxyBuilder.getProxy(getString(R.string.apikey));

        // Check to see if user is logged in
        isUserLoggedIn();

        // setup login
        setupLogin();
    }

    private void isUserLoggedIn() {
        // Check if user is currently logged in with Shared Preferences
        String[] data = getData(getApplicationContext());

        // If Shared Preferences is not empty
        if(data[0].isEmpty() == false) {
            String token = data[0];
            proxy = ProxyBuilder.getProxy(getString(R.string.apikey), token);

            // Need to change method of starting activity
            Intent intent = MapActivity.launchIntentMap(LoginActivity.this);
            startActivity(intent);
        }
    }

    // Getting the data token and email using Shared Preferences
    private String[] getData(Context context) {
        SharedPreferences preferences = getSharedPreferences("User Session", MODE_PRIVATE);

        // Store values and return it
        String[] returnedData = new String[2];
        returnedData[0] = preferences.getString("Token", null);
        returnedData[1] = preferences.getString("Email", null);
        return returnedData;
    }

    private void setupLogin() {
        EditText userEmail = findViewById(R.id.email_input);
        userEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                EditText userEmail = findViewById(R.id.email_input);
                userEmailString = userEmail.getText().toString();
            }
        });

        // Hide keyboard when is done typing
        userEmail.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {
                    Utilities.hideKeyboard(LoginActivity.this);
                }
                return false;
            }
        });

        // Hide keyboard on focus change
        userEmail.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    Utilities.hideKeyboardFocus(LoginActivity.this, view);
                }
            }
        });

        EditText userPassword = findViewById(R.id.password_input);
        userPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                EditText userPassword = findViewById(R.id.password_input);
                userPasswordString = userPassword.getText().toString();
            }
        });

        // Hide keyboard when is done typing
        userPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {
                    Utilities.hideKeyboard(LoginActivity.this);
                }
                return false;
            }
        });

        // Hide keyboard on focus change
        userPassword.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    Utilities.hideKeyboardFocus(LoginActivity.this, view);
                }
            }
        });

        Button loginBtn = findViewById(R.id.login_button);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Register for token received
                ProxyBuilder.setOnTokenReceiveCallback(token -> onReceiveToken(token));

                // Make call to login
                User user = new User();
                user.setEmail(userEmailString);
                user.setPassword(userPasswordString);

                // Finish the login process
                Call<Void> caller = proxy.login(user);
                ProxyBuilder.callProxy(LoginActivity.this, caller, returnedNothing -> response(returnedNothing));
            }
        });
    }

    // Handle the token by generating a new Proxy which is encoded with it.
    private void onReceiveToken(String newToken) {
        Log.d(TAG, "onReceiveToken: I just received the token " + newToken);

        // Save token using Shared Preferences
        saveUserInformation(newToken);

        // Rebuild the proxy with updated token
        proxy = ProxyBuilder.getProxy(getString(R.string.apikey), newToken);
    }

    private void saveUserInformation(String newToken) {
        SharedPreferences preferences = this.getSharedPreferences("User Session", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("Token", newToken);
        editor.putString("Email", userEmailString);
        editor.apply();
    }

    // Login actually completes by calling this; nothing to do as it was all done when we got the token.
    private void response(Void returnedNothing) {
        // Launch the Maps Activity
        Intent intent = MapActivity.launchIntentMap(LoginActivity.this);
        startActivity(intent);
        finish();
    }

    public static Intent launchIntentLogin (Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        return intent;
    }
}
