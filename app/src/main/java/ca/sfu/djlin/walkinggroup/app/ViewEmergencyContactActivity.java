package ca.sfu.djlin.walkinggroup.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import ca.sfu.djlin.walkinggroup.R;
import ca.sfu.djlin.walkinggroup.model.User;
import ca.sfu.djlin.walkinggroup.proxy.WGServerProxy;

public class ViewEmergencyContactActivity extends AppCompatActivity {
    WGServerProxy proxy;
    User CurrentUser;
    Long CurrentUserId;
    String currentUserToken;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewcontact_info);
        Intent intent=getIntent();
        CurrentUserId=intent.getLongExtra("User Id", 0);
        currentUserToken=intent.getStringExtra("Token");

        //proxy= ProxyBuilder.getProxy(getString(R.string.apikey), currentUserToken);

        // Get current user information
        //Call<User> caller = proxy.getUserById(CurrentUserId);
        //ProxyBuilder.callProxy(ViewEmergencyContactActivity.this, caller, returnedUser -> responseCurrent(returnedUser));


    }

    private void responseCurrent(User returnedUser) {
        CurrentUser=returnedUser;
        TextView EmeregyContactInfo=findViewById(R.id.UserContactInfo);
        EmeregyContactInfo.setText(returnedUser.getName());
    }

    public static Intent launchIntentViewEmergency(Context context) {
        Intent intentEmergency = new Intent(context, ViewEmergencyContactActivity.class);
        return intentEmergency;
    }
}
