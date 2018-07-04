package ca.sfu.djlin.walkinggroup.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import ca.sfu.djlin.walkinggroup.R;
import ca.sfu.djlin.walkinggroup.model.User;
import ca.sfu.djlin.walkinggroup.proxy.ProxyBuilder;
import ca.sfu.djlin.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

public class SignupActivity extends AppCompatActivity {
    // TO BE REMOVED PRIOR TO SUBMISSION
    private static final String TAG = "ServerTest";

    private WGServerProxy proxy;
    String userNameString;
    String userEmailString;
    String userPasswordString;
    String userConfirmPasswordString;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_user_activity);

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

        // Hide keyboard
        userName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    hideKeyboard(SignupActivity.this);
                }
                return false;
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

        // Hide keyboard
        userEmail.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    hideKeyboard(SignupActivity.this);
                }
                return false;
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

        // Hide keyboard
        userPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    hideKeyboard(SignupActivity.this);
                }
                return false;
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

        // Hide keyboard
        userConfirmPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    hideKeyboard(SignupActivity.this);
                }
                return false;
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
                    Log.d(TAG, "User password: " + userPasswordString);
                    Log.d(TAG, "User confirm password: " + userConfirmPasswordString);
                    Toast.makeText(SignupActivity.this, "Please check your passwords!", Toast.LENGTH_SHORT).show();
                } else {
                    // Make call to server
                    Call<User> caller = proxy.createUser(user);
                    ProxyBuilder.callProxy(SignupActivity.this, caller, returnedUser -> createUserResponse(returnedUser));

                    // Launch Map Activity
                    //Intent mapIntent = MapActivity.launchIntentMap(SignupActivity.this);
                    //startActivity(mapIntent);
                    finish();
                }
            }
        });
    }

    // Create user response from server
    private void createUserResponse(User user) {
        notifyUserViaLogAndToast("Server replied with user: " + user.toString());

        // Returned information
        Long userId = user.getId();
        String userEmail = user.getEmail();
    }

    // Handle the token by generating a new Proxy which is encoded with it.
    private void onReceiveToken(String token) { ;
        // Replace the current proxy with one that uses the token!
        Log.w(TAG, "   --> NOW HAVE TOKEN: " + token);
        proxy = ProxyBuilder.getProxy(getString(R.string.apikey), token);
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

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
