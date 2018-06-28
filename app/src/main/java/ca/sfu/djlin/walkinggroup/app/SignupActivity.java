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
    //REMOVE!!!!
    private static final String TAG = "ServerTest";

    private WGServerProxy proxy;
    String user_name_string;
    String user_email_string;
    String password_confirmed_string;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_user_activity);


        //Build the server proxy
        proxy = ProxyBuilder.getProxy(getString(R.string.apikey), null);

        //setting up Create Account button
        setupCreateAccount();
    }

    public static Intent LaunchIntent_signup(Context context) {
        Intent intent_signup = new Intent(context, SignupActivity.class);
        return intent_signup;

    }

    private void setupCreateAccount() {
        //name
        EditText user_name = findViewById(R.id.username_input);
        user_name.addTextChangedListener(new TextWatcher() {
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

        //email
        EditText user_email = findViewById(R.id.useremail_input);
        user_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                EditText user_email=findViewById(R.id.useremail_input);
                user_email_string=user_email.getText().toString();
            }
        });

        //password
        EditText user_password = findViewById(R.id.userconfirm_pass_input);
        //EditText user_password_notCon = findViewById(R.id.userpassword_input);
        user_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                EditText user_password = findViewById(R.id.userconfirm_pass_input);
                //String password_notcon_string = user_password_notCon.getText().toString();
                password_confirmed_string=user_password.getText().toString();
                //if(password_notcon_string.equals(password_confirmed_string)==false){
                    //Toast.makeText(getApplicationContext(), "Passwords do not match. PLease try again.", Toast.LENGTH_SHORT).show();
                    //user_password.setText("");
                //}
            }
        });


        Button CreateAccount=findViewById(R.id.create_account);

        CreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Build User
                User user = new User();
                user.setName(user_name_string);
                user.setEmail(user_email_string);
                user.setPassword(password_confirmed_string);

                //REMOVE LATER
                user.setCurrentPoints(100);
                user.setTotalPointsEarned(2500);
                user.setRewards(new EarnedRewards());

                // Make call
                Call<User> caller = proxy.createUser(user);
                ProxyBuilder.callProxy(SignupActivity.this, caller, returnedUser -> response(returnedUser));
            }
        });
    }
    private void response(User user) {

        notifyUserViaLogAndToast("Server replied with user: " + user.toString());
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
