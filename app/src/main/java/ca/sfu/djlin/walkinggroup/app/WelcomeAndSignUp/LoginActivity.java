package ca.sfu.djlin.walkinggroup.app.WelcomeAndSignUp;

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

import ca.sfu.djlin.walkinggroup.R;
import ca.sfu.djlin.walkinggroup.Utilities;
import ca.sfu.djlin.walkinggroup.app.Map.MapActivityDrawer;
import ca.sfu.djlin.walkinggroup.model.Session;
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
    Session session;
    static User usertosend;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

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
        session = Session.getSession(getApplicationContext());
        //If Shared Preferences is not null
        if (data[0] != null) {
            String token = data[0];
            proxy = ProxyBuilder.getProxy(getString(R.string.apikey), token);
            //session.setProxy(proxy);
            Long UserId = Long.valueOf(data[2]);
            if (UserId != 0) {
                Call<User> call = proxy.getUserById(UserId);
                ProxyBuilder.callProxy(LoginActivity.this, call, returnedNothing -> responseSingleton(returnedNothing));
            }

            // Start checking for new mail
            Utilities.startMessageChecking(LoginActivity.this, proxy, session.getUser());

            // Need to change method of starting activity
            Intent intent = MapActivityDrawer.launchIntentMap(LoginActivity.this);
            intent.putExtra("userId", UserId);
            startActivity(intent);
        }
    }

    private void responseSingleton(User returnedNothing) {
        // Retrieve user id
        // Call<User> call = proxy.getUserByEmail(userEmailString);
        //ProxyBuilder.callProxy(LoginActivity.this, call, returnedUser -> getUserIdResponse(returnedUser));
        //session.setUser(returnedNothing);

    }

    // Getting the data token and email using Shared Preferences
    private String[] getData(Context context) {
        SharedPreferences preferences = getSharedPreferences("User Session", MODE_PRIVATE);

        // Store values and return it
        String[] returnedData = new String[3];
        returnedData[0] = preferences.getString("Token", null);
        returnedData[1] = preferences.getString("Email", null);
        returnedData[2] = String.valueOf(preferences.getLong("User Id", 0));
        return returnedData;
    }

    private void setupLogin() {
        //session=Session.getSession(getApplicationContext());
        EditText userEmail = findViewById(R.id.email_input);
        userEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

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
        userEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

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
        userPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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
                if (userEmailString != null) {
                    user.setEmail(userEmailString);
                }
                if (userPasswordString != null) {
                    user.setPassword(userPasswordString);
                }
                // Finish the login process
                if (user != null) {
                    Call<Void> caller = proxy.login(user);
                    ProxyBuilder.callProxy(LoginActivity.this, caller, returnedNothing -> response(returnedNothing));
                }
            }
        });
    }

    // Handle the token by generating a new Proxy which is encoded with it.
    private void onReceiveToken(String newToken) {
        Log.d(TAG, "onReceiveToken: I just received the token " + newToken);

        // Save token using Shared Preferences
        saveUserInformation(newToken);

        // Rebuild the proxy with updated token
        if (newToken != null) {
            proxy = ProxyBuilder.getProxy(getString(R.string.apikey), newToken);
            session.setProxy(proxy);
        }
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
        // Retrieve user id
        if (userEmailString != null) {
            Log.i("LOOK HERE", userEmailString + "");
            Call<User> call = proxy.getUserByEmail(userEmailString);
            ProxyBuilder.callProxy(LoginActivity.this, call, returnedUser -> getUserIdResponse(returnedUser));
        }
    }

    private void getUserIdResponse(User user) {
        if (user != null) {
            session.setUser(user);
        }

        // Store returned user id in shared preferences
        SharedPreferences preferences = LoginActivity.this.getSharedPreferences("User Session", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong("User Id", user.getId());
        editor.apply();

        // Start checking for new mail
        Utilities.startMessageChecking(LoginActivity.this, proxy, session.getUser());

        // Launch the Maps Activity
        Intent intent = MapActivityDrawer.launchIntentMap(LoginActivity.this);
        startActivity(intent);
        finish();
    }

    public static Intent launchIntentLogin(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        return intent;
    }

    public static Session sendUser(Context context, Session session) {
        SharedPreferences preferences = context.getSharedPreferences("User Session", MODE_PRIVATE);
        //Session createUser;

        String token = preferences.getString("Token", "");
        Long Id = preferences.getLong("User Id", 0);
        WGServerProxy proxy;
        proxy = ProxyBuilder.getProxy(context.getString(R.string.apikey), token);
        if (Id != 0) {
            Call<User> call = proxy.getUserById(Id);
            ProxyBuilder.callProxy(context, call, b -> session.setUser(b));
        }
        if (proxy != null) {
            session.setProxy(proxy);
        }
        //session.setUser(b);
        return session;
    }
}
