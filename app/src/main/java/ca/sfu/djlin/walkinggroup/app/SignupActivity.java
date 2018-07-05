package ca.sfu.djlin.walkinggroup.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import ca.sfu.djlin.walkinggroup.R;
import ca.sfu.djlin.walkinggroup.Utilities;
import ca.sfu.djlin.walkinggroup.model.User;
import ca.sfu.djlin.walkinggroup.proxy.ProxyBuilder;
import ca.sfu.djlin.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

public class SignupActivity extends AppCompatActivity {
    // TO BE REMOVED PRIOR TO SUBMISSION
    private static final String TAG = "SignupActivity";

    private WGServerProxy proxy;
    String userNameString;
    String userEmailString;
    String userPasswordString;
    String userConfirmPasswordString;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Build server proxy
        proxy = ProxyBuilder.getProxy(getString(R.string.apikey));

        // Setting up buttons
        setupCreateAccountInputs();
    }

    public static Intent launchIntentSignup(Context context) {
        Intent intentSignup = new Intent(context, SignupActivity.class);
        return intentSignup;
    }

    private void setupCreateAccountInputs() {
        // Setup text watcher for user's name
        EditText userName = findViewById(R.id.name_input);
        userName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                EditText userName = findViewById(R.id.name_input);
                userNameString = userName.getText().toString();
            }
        });

        // Hide keyboard when is done typing
        userName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {
                    Utilities.hideKeyboard(SignupActivity.this);
                }
                return false;
            }
        });

        // Hide keyboard on focus change
        userName.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    Utilities.hideKeyboardFocus(SignupActivity.this, view);
                }
            }
        });

        // Setup text watcher for user's email
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
                    Utilities.hideKeyboard(SignupActivity.this);
                }
                return false;
            }
        });

        // Hide keyboard on focus change
        userEmail.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    Utilities.hideKeyboardFocus(SignupActivity.this, view);
                }
            }
        });

        // Setup text watcher for user's password
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
                    Utilities.hideKeyboard(SignupActivity.this);
                }
                return false;
            }
        });

        // Hide keyboard on focus change
        userPassword.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    Utilities.hideKeyboardFocus(SignupActivity.this, view);
                }
            }
        });

        // Setup text watcher for user's password confirmation
        EditText userConfirmPassword = findViewById(R.id.confirm_password_input);
        userConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                EditText userConfirmPassword = findViewById(R.id.confirm_password_input);
                userConfirmPasswordString = userConfirmPassword.getText().toString();
            }
        });

        // Hide keyboard when is done typing
        userConfirmPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {
                    Utilities.hideKeyboard(SignupActivity.this);
                }
                return false;
            }
        });

        // Hide keyboard on focus change
        userConfirmPassword.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    Utilities.hideKeyboardFocus(SignupActivity.this, view);
                }
            }
        });

        Button createAccountBtn = findViewById(R.id.create_account);
        createAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create User instance
                User user = new User();

                // Set User information
                user.setName(userNameString);
                user.setEmail(userEmailString);
                user.setPassword(userPasswordString);

                /*
                // Reward system to be implemented at another time
                user.setCurrentPoints(100);
                user.setTotalPointsEarned(2500);
                user.setRewards(new EarnedRewards());
                */

                // Check that passwords match
                if (!userPasswordString.equals(userConfirmPasswordString)) {
                    String checkPasswords = getResources().getString(R.string.check_passwords);
                    Toast.makeText(SignupActivity.this, checkPasswords, Toast.LENGTH_SHORT).show();
                } else {
                    // Make call to server
                    Call<User> caller = proxy.createUser(user);
                    ProxyBuilder.callProxy(SignupActivity.this, caller, returnedUser -> createUserResponse(returnedUser));
                }
            }
        });
    }

    // Create user response from server
    private void createUserResponse(User user) {
        // User creation is successful

        // Grab the current token session
        notifyUserViaLogAndToast("Server replied with user: " + user.toString());

        // Register callback for token
        ProxyBuilder.setOnTokenReceiveCallback(token -> onReceiveToken(token));

        // Create instance of user to use for login
        User createdUser = new User();
        createdUser.setEmail(userEmailString);
        createdUser.setPassword(userPasswordString);

        // Finish the login process
        Call<Void> caller = proxy.login(createdUser);
        ProxyBuilder.callProxy(SignupActivity.this, caller, returnedNothing -> response(returnedNothing));

        // Save user information
        saveUserInfo(user);

        // Launch map activity
        launchMapActivity();
    }

    // Save user information in Shared Preferences
    private void saveUserInfo(User user) {
        SharedPreferences preferences = this.getSharedPreferences("User Session", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("Email", user.getEmail());
        editor.putLong("User Id", user.getId());
        editor.apply();
    }

    private void launchMapActivity() {
        // Launch Map Activity
        Intent mapIntent = MapActivity.launchIntentMap(SignupActivity.this);
        startActivity(mapIntent);
        finish();
    }

    // Handle the token by generating a new Proxy which is encoded with it.
    private void onReceiveToken(String token) {
        // Replace the current proxy with one that uses the token!
        Log.w(TAG, "   --> NOW HAVE TOKEN: " + token);
        proxy = ProxyBuilder.getProxy(getString(R.string.apikey), token);

        // Save token in shared preferences
        saveToken(token);
    }

    // Save token in shared preferences
    private void saveToken(String token) {
        SharedPreferences preferences = this.getSharedPreferences("User Session", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("Token", token);
        editor.apply();
    }

    // Login actually completes by calling this; nothing to do as it was all done
    // when we got the token.
    private void response(Void returnedNothing) {
        notifyUserViaLogAndToast("Server replied to login request (no content was expected).");
    }

    // Put message up in toast and logcat
    // -----------------------------------------------------------------------------------------
    private void notifyUserViaLogAndToast(String message) {
        Log.w(TAG, message);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
