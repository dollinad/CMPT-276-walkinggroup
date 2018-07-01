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

import ca.sfu.djlin.walkinggroup.R;
import ca.sfu.djlin.walkinggroup.model.User;
import ca.sfu.djlin.walkinggroup.proxy.ProxyBuilder;
import ca.sfu.djlin.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

public class LoginActivity extends AppCompatActivity {

    //private User user;
    private WGServerProxy proxy;
    private String useremail_string;
    private String userpassword_string;
    private String token_use;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login_activity);


        //Build the server proxy
        proxy = ProxyBuilder.getProxy(getString(R.string.apikey), null);

        //checking if user has already logged in before using shared Preferences
        String[] data= getData(getApplicationContext());
        if(data[0].isEmpty()==false) {
            Intent intent=new Intent(LoginActivity.this, check.class);
            intent.putExtra("name", data[1]);
            startActivity(intent);
        }

        //setting user icon
        EditText login_emial=findViewById(R.id.login_email);
        Drawable drawable_loginemail=getResources().getDrawable(R.drawable.user_icon);
        drawable_loginemail.setBounds(0,0, (int) (drawable_loginemail.getIntrinsicHeight()*0.10),
                (int)(drawable_loginemail.getIntrinsicHeight()*0.101));
        login_emial.setCompoundDrawables(drawable_loginemail, null, null, null);

        //setting the password icon
        EditText login_password= findViewById(R.id.login_password);
        Drawable drawable_password=getResources().getDrawable(R.drawable.password);
        drawable_password.setBounds(0,0, (int) (drawable_password.getIntrinsicHeight()*0.05),
                (int)(drawable_password.getIntrinsicHeight()*0.05));
        login_password.setCompoundDrawables(drawable_password, null, null, null);

        //setup login
        //checking if server username and password match
        setupLogin();
        getemail();


    }

    //getting the data (token and email) using the Shared Preferences
    private String[] getData(Context context) {
        SharedPreferences pref=getSharedPreferences("Token", MODE_PRIVATE);
        String[] return_data=new String[3];
        return_data[0]=pref.getString("token", "");
        return_data[1]=pref.getString("user name", "");
        return return_data;
    }


    private void setupLogin() {
        EditText useremail=findViewById(R.id.login_email);
        useremail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                EditText useremail=findViewById(R.id.login_email);
                useremail_string =useremail.getText().toString();

            }
        });

        EditText userpassword=findViewById(R.id.login_password);
        userpassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                EditText userpassword=findViewById(R.id.login_password);
                userpassword_string=userpassword.getText().toString();
            }
        });

        Button login=findViewById(R.id.login_login);
        //Toast.makeText(getApplicationContext(), useremail_string, Toast.LENGTH_LONG).show();
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Register for token received
                ProxyBuilder.setOnTokenReceiveCallback(token -> onReceiveToken(token));

                //make call to login
                User user=new User();
                user.setEmail(useremail_string);
                user.setPassword(userpassword_string);

                Call<Void> caller_login=proxy.login(user);
                ProxyBuilder.callProxy(LoginActivity.this, caller_login, returnedNothing -> response(returnedNothing));

                Intent intent = new Intent(LoginActivity.this, check.class);
                startActivity(intent);


            }
        });
    }

    // Handle the token by generating a new Proxy which is encoded with it.
    private void onReceiveToken(String token) {
        // Replace the current proxy with one that uses the token!
        token_use=token;

        //Save token using Shared Preferences
        saveToken(token_use);
        proxy = ProxyBuilder.getProxy(getString(R.string.apikey), token);
    }

    private void saveToken(String token_use) {
        SharedPreferences pref=this.getSharedPreferences("Token", MODE_PRIVATE);
        SharedPreferences.Editor editor=pref.edit();
        editor.putString("token", token_use);
        editor.putString("user name", useremail_string);
        editor.putString("user password", userpassword_string);
        editor.apply();
    }

    // Login actually completes by calling this; nothing to do as it was all done
    // when we got the token.
    private void response(Void returnedNothing) {
        notifyUserViaLogAndToast("Server replied to login request (no content was expected).");
    }

    private void getemail() {
        Button getemail=findViewById(R.id.getemail);
        getemail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Call<User> caller = proxy.getUserByEmail(useremail_string);
                ProxyBuilder.callProxy(LoginActivity.this, caller, returnedUser -> response(returnedUser));
            }
        });

    }

    private void response(User user) {
        notifyUserViaLogAndToast("Server replied with user: " + user.toString());
        //userId = user.getId();
        useremail_string = user.getEmail();
    }


    public static Intent LaunchIntent_login(Context context) {
        Intent intent_login = new Intent(context, LoginActivity.class);
        return intent_login;

    }


    // Put message up in toast and logcat
    // -----------------------------------------------------------------------------------------
    private void notifyUserViaLogAndToast(String message) {
        Log.i("PPP", message);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
