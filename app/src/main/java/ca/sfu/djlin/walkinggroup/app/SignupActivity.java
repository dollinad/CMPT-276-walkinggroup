package ca.sfu.djlin.walkinggroup.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import ca.sfu.djlin.walkinggroup.R;
import ca.sfu.djlin.walkinggroup.dataobjects.EarnedRewards;
import ca.sfu.djlin.walkinggroup.model.User;
import ca.sfu.djlin.walkinggroup.proxy.ProxyBuilder;
import ca.sfu.djlin.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

public class SignupActivity extends AppCompatActivity {
    // TO BE REMOVED PRIOR TO SUBMISSION
    private static final String TAG = "ServerTest";

    private WGServerProxy proxy;
    String user_name_string;
    String user_email_string;
    String password_confirmed_string;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_user_activity);

        // Build server proxy
        proxy = ProxyBuilder.getProxy(getString(R.string.apikey), null);

        // Setting up buttons
        setupCreateAccount();
    }

    public static Intent launchIntentSignup(Context context) {
        Intent intentSignup = new Intent(context, SignupActivity.class);
        return intentSignup;
    }

    private void setupCreateAccount() {
        // Setup text watcher for user's name
        EditText userName = findViewById(R.id.username_input);
        userName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                EditText user_name = findViewById(R.id.username_input);
                user_name_string = user_name.getText().toString();
            }
        });

        // Setup text watcher for user's email
        EditText userEmail = findViewById(R.id.useremail_input);
        userEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                EditText user_email = findViewById(R.id.useremail_input);
                user_email_string = user_email.getText().toString();
            }
        });

        // Setup text watcher for user's password
        EditText userPassword = findViewById(R.id.userconfirm_pass_input);
        //EditText user_password_notCon = findViewById(R.id.userpassword_input);
        userPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                EditText user_password = findViewById(R.id.userconfirm_pass_input);
                //String password_notcon_string = user_password_notCon.getText().toString();
                password_confirmed_string = user_password.getText().toString();
                //if(password_notcon_string.equals(password_confirmed_string)==false){
                    //Toast.makeText(getApplicationContext(), "Passwords do not match. PLease try again.", Toast.LENGTH_SHORT).show();
                    //user_password.setText("");
                //}
            }
        });

        Button createAccountBtn = findViewById(R.id.create_account);

        createAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create User instance
                User user = new User();

                // Set User information
                user.setName(user_name_string);
                user.setEmail(user_email_string);
                user.setPassword(password_confirmed_string);

                /*
                // Reward system to be implemented at another time
                user.setCurrentPoints(100);
                user.setTotalPointsEarned(2500);
                user.setRewards(new EarnedRewards());
                */

                // Make call to server
                Call<User> caller = proxy.createUser(user);
                ProxyBuilder.callProxy(SignupActivity.this, caller, returnedUser -> createUserResponse(returnedUser));
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
}
