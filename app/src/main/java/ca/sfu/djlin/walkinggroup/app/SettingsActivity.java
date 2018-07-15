package ca.sfu.djlin.walkinggroup.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import ca.sfu.djlin.walkinggroup.R;
import ca.sfu.djlin.walkinggroup.Utilities;
import ca.sfu.djlin.walkinggroup.model.User;
import ca.sfu.djlin.walkinggroup.proxy.ProxyBuilder;
import ca.sfu.djlin.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

public class SettingsActivity extends AppCompatActivity{
    private WGServerProxy proxy;
    String token;
    String UserEmail;
    User CurrentUser;
   String nameEntered;
   Long UserId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Utilities.hideKeyboard(SettingsActivity.this);
        //Get intent
        Intent intent = getIntent();
        token = intent.getStringExtra("Token");
        UserId=intent.getLongExtra("User Id", 0);
        UserEmail = intent.getStringExtra("Email");

        //Build Proxy
        proxy = ProxyBuilder.getProxy(getString(R.string.apikey), token);


        // Get current user information
        Call<User> caller = proxy.getUserByEmail(UserEmail);

        ProxyBuilder.callProxy(SettingsActivity.this, caller, returnedUser -> responseCurrent(returnedUser));

        setupButton();


    }

    private void setupButton() {
       ImageView btn=findViewById(R.id.nameClick);
       btn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Toast.makeText(getApplicationContext(), CurrentUser.getId()+"", Toast.LENGTH_SHORT).show();
               proxy = ProxyBuilder.getProxy(getString(R.string.apikey), token);
               Call<User> call=proxy.editUser(CurrentUser.getId(), CurrentUser);
               ProxyBuilder.callProxy(SettingsActivity.this, call, returnedUser -> responseEdit(returnedUser));

           }
       });

    }

    //response for current user
    private void responseCurrent(User user) {
        // Store retrieved user into currentUser
        CurrentUser = user;
        //Toast.makeText(getApplicationContext(), CurrentUser.getName(), Toast.LENGTH_SHORT).show();
        EditText nameDisplay=findViewById(R.id.nameDisplay);
        nameDisplay.setText(CurrentUser.getName());
        NameEdit(CurrentUser);

        EditText EmailDisplay=findViewById(R.id.EmailDisplay);
        EmailDisplay.setText(UserEmail);
    }

    private void NameEdit(User user) {
        Utilities.hideKeyboard(SettingsActivity.this);
        EditText nameDisplay=findViewById(R.id.nameDisplay);
        nameDisplay.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                EditText nameDisplay=findViewById(R.id.nameDisplay);
                nameEntered=nameDisplay.getText().toString();
                CurrentUser.setName(nameEntered);
               // Toast.makeText(getApplicationContext(), CurrentUser.getName(), Toast.LENGTH_SHORT).show();
                //Build Proxy

            }

        });
       // CurrentUser.setName(nameEntered);
        Toast.makeText(getApplicationContext(), CurrentUser.getName()+"LOL", Toast.LENGTH_SHORT).show();
        //proxy = ProxyBuilder.getProxy(getString(R.string.apikey), token);
        //Call<User> call=proxy.editUser(CurrentUser.getId(), CurrentUser);
        //ProxyBuilder.callProxy(SettingsActivity.this, call, returnedUser -> responseEdit(returnedUser));


    }

    private void responseEdit(User returnedUser) {

        CurrentUser=returnedUser;
        //Toast.makeText(getApplicationContext(), nameEntered, Toast.LENGTH_SHORT).show();
    }

    public static Intent launchIntentSettings(Context context) {
        Intent intentSettings = new Intent(context, SettingsActivity.class);
        return intentSettings;
    }
}
