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
import android.widget.TextView;
import android.widget.Toast;

import ca.sfu.djlin.walkinggroup.R;
import ca.sfu.djlin.walkinggroup.Utilities;
import ca.sfu.djlin.walkinggroup.model.User;
import ca.sfu.djlin.walkinggroup.proxy.ProxyBuilder;
import ca.sfu.djlin.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

public class ViewEmergencyContactActivity extends AppCompatActivity {
    WGServerProxy proxy;
    User CurrentUser;
    Long CurrentUserId;
    String currentUserToken;
    String EmergencyContactInfoEntered;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewcontact_info);
        Intent intent=getIntent();
        CurrentUserId=intent.getLongExtra("User Id", 0);
        currentUserToken=intent.getStringExtra("Token");

        proxy= ProxyBuilder.getProxy(getString(R.string.apikey), currentUserToken);

        // Get current user information
        Call<User> caller = proxy.getUserById(CurrentUserId);
        ProxyBuilder.callProxy(ViewEmergencyContactActivity.this, caller, returnedUser -> responseCurrent(returnedUser));

        setupEditbutton();
        EmergencyEdit();
    }

    private void responseCurrent(User returnedUser) {
        CurrentUser=returnedUser;
        TextView username=findViewById(R.id.Username);
        username.setText(returnedUser.getName());
        EditText EmergencyContactInfo=findViewById(R.id.UserContactInfo);
        EmergencyContactInfo.setText(returnedUser.getEmergencyContactInfo());
    }

    //get the new contact information
    private void EmergencyEdit() {
        Utilities.hideKeyboard(ViewEmergencyContactActivity.this);
        EditText Emergency=findViewById(R.id.UserContactInfo);
        Emergency.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                EditText Emergency=findViewById(R.id.UserContactInfo);
                EmergencyContactInfoEntered=Emergency.getText().toString();
                CurrentUser.setEmergencyContactInfo(EmergencyContactInfoEntered);
            }

        });
    }

    //button that confirms the edit! Send the info to the user
    private void setupEditbutton() {
        ImageView editButton=findViewById(R.id.editContactInfo);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), CurrentUser.getId()+"", Toast.LENGTH_SHORT).show();
                proxy = ProxyBuilder.getProxy(getString(R.string.apikey), currentUserToken);
                Call<User> call=proxy.editUser(CurrentUser.getId(), CurrentUser);
                ProxyBuilder.callProxy(ViewEmergencyContactActivity.this, call, returnedUser -> responseEdit(returnedUser));
            }
        });
    }

    private void responseEdit(User returnedUser) {
        CurrentUser=returnedUser;
    }

    public static Intent launchIntentViewEmergency(Context context) {
        Intent intentEmergency = new Intent(context, ViewEmergencyContactActivity.class);
        return intentEmergency;
    }
}
