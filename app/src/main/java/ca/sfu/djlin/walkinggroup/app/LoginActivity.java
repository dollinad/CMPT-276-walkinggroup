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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import ca.sfu.djlin.walkinggroup.R;
import ca.sfu.djlin.walkinggroup.model.User;
import ca.sfu.djlin.walkinggroup.proxy.ProxyBuilder;
import ca.sfu.djlin.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

public class LoginActivity extends AppCompatActivity {

    //Private User user;
    private WGServerProxy proxy;
    private String userEmailString;
    private String userPasswordString;
    private String token_use;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login_activity);

        // Build the server proxy
        proxy = ProxyBuilder.getProxy(getString(R.string.apikey));

        // Check to see if user is logged in
        isUserLoggedIn();

        setUpIcons();

        // setup login
        // checking if server username and password match
        setupLogin();
        // getemail();
    }

    private void setUpIcons() {
        // Setting user icon
        EditText loginEmail = findViewById(R.id.login_email);
        Drawable drawableLoginEmail = getResources().getDrawable(R.drawable.user_icon);
        drawableLoginEmail.setBounds(0,0, (int) (drawableLoginEmail.getIntrinsicHeight()*0.10),
                (int)(drawableLoginEmail.getIntrinsicHeight()*0.101));
        loginEmail.setCompoundDrawables(drawableLoginEmail, null, null, null);

        // Setting password icon
        EditText loginPassword = findViewById(R.id.login_password);
        Drawable drawablePassword = getResources().getDrawable(R.drawable.password);
        drawablePassword.setBounds(0,0, (int) (drawablePassword.getIntrinsicHeight()*0.05),
                (int)(drawablePassword.getIntrinsicHeight()*0.05));
        loginPassword.setCompoundDrawables(drawablePassword, null, null, null);
    }

    private void isUserLoggedIn() {
        // Check if user is currently logged in with Shared Preferences
        String[] data = getData(getApplicationContext());

        // If Shared Preferences is not empty
        if(data[0].isEmpty() == false) {
            String token = data[0];
            proxy = ProxyBuilder.getProxy(getString(R.string.apikey), token);

            // Need to change method of starting activity
            Intent intent = new Intent(LoginActivity.this, MapActivity.class);
            intent.putExtra("token", data[0]);
            intent.putExtra("email", data[1]);
            startActivity(intent);
        }
    }

    // Getting the data token and email using Shared Preferences
    private String[] getData(Context context) {
        SharedPreferences preferences = getSharedPreferences("Token", MODE_PRIVATE);

        // Store values and return it
        String[] returnedData = new String[3];
        returnedData[0] = preferences.getString("Token", "");
        System.out.println("zhuan"+returnedData[0]);
        returnedData[1] = preferences.getString("Email", "");
        System.out.println("zhuan"+returnedData[1]);
        return returnedData;
    }

    private void setupLogin() {
        EditText useremail = findViewById(R.id.login_email);
        useremail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                EditText useremail = findViewById(R.id.login_email);
                userEmailString = useremail.getText().toString();
            }
        });

        EditText userpassword = findViewById(R.id.login_password);
        userpassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                EditText userpassword = findViewById(R.id.login_password);
                userPasswordString = userpassword.getText().toString();
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
        // Save token using Shared Preferences
        saveToken(newToken);
        // Rebuild the proxy with updated token
        proxy = ProxyBuilder.getProxy(getString(R.string.apikey), newToken);
    }

    private void saveToken(String newToken) {
        SharedPreferences preferences = this.getSharedPreferences("Token", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("Token", newToken);
        // Save token shouldn't contain this?
        editor.putString("Email", userEmailString);
        editor.putString("User Password", userPasswordString);
        editor.apply();
    }

    // Login actually completes by calling this; nothing to do as it was all done when we got the token.
    private void response(Void returnedNothing) {
        // Launch the Maps Activity
        Intent intent = new Intent(LoginActivity.this, MapActivity.class);
        intent.putExtra("token",token_use);
        startActivity(intent);
    }

    /* Used for testing
    private void response(User user) {
        notifyUserViaLogAndToast("Server replied with user: " + user.toString());
        //userId = user.getId();
        userEmailString = user.getEmail();
    }

    private void getemail() {
        Button getemail = findViewById(R.id.getemail);
        getemail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Call<User> caller = proxy.getUserByEmail(userEmailString);
                ProxyBuilder.callProxy(LoginActivity.this, caller, returnedUser -> response(returnedUser));
            }
        });
    } */

    public static Intent LaunchIntent_login(Context context) {
        Intent intent_login = new Intent(context, LoginActivity.class);
        return intent_login;
    }
}
