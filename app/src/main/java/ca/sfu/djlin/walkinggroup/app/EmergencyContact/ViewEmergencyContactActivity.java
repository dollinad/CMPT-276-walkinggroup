package ca.sfu.djlin.walkinggroup.app.EmergencyContact;

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
import ca.sfu.djlin.walkinggroup.model.Session;
import ca.sfu.djlin.walkinggroup.model.User;
import ca.sfu.djlin.walkinggroup.proxy.ProxyBuilder;
import ca.sfu.djlin.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

public class ViewEmergencyContactActivity extends AppCompatActivity {
    WGServerProxy proxy;
    User currentUser;
    Long currentUserId;
    String currentUserToken;
    String emergencyContactInfoEntered;
    Session session;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_contact_info);

        session = Session.getSession(getApplicationContext());
        proxy = session.getProxy();
        currentUser = session.getUser();
        currentUserId = currentUser.getId();
        // Get current user information
        Call<User> caller = proxy.getUserById(currentUserId);
        ProxyBuilder.callProxy(ViewEmergencyContactActivity.this, caller, returnedUser -> responseCurrent(returnedUser));

        setupEditbutton();
        EmergencyEdit();
    }

    private void responseCurrent(User returnedUser) {
        currentUser = returnedUser;
        TextView username = findViewById(R.id.username);
        username.setText(returnedUser.getName());
        EditText EmergencyContactInfo = findViewById(R.id.user_contact_info);
        EmergencyContactInfo.setText(returnedUser.getEmergencyContactInfo());
    }

    //get the new contact information
    private void EmergencyEdit() {
        Utilities.hideKeyboard(ViewEmergencyContactActivity.this);
        EditText Emergency = findViewById(R.id.user_contact_info);
        Emergency.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                EditText Emergency = findViewById(R.id.user_contact_info);
                emergencyContactInfoEntered = Emergency.getText().toString();
                currentUser.setEmergencyContactInfo(emergencyContactInfoEntered);
            }

        });
    }

    // Button that confirms the edit! Send the info to the user
    private void setupEditbutton() {
        ImageView editButton = findViewById(R.id.edit_contact_info);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), currentUser.getId() + "", Toast.LENGTH_SHORT).show();
                proxy = ProxyBuilder.getProxy(getString(R.string.apikey), currentUserToken);
                Call<User> call = proxy.editUser(currentUser.getId(), currentUser);
                ProxyBuilder.callProxy(ViewEmergencyContactActivity.this, call, returnedUser -> responseEdit(returnedUser));
            }
        });
    }

    private void responseEdit(User returnedUser) {
        currentUser = returnedUser;
    }

    public static Intent launchIntentViewEmergency(Context context) {
        Intent intentEmergency = new Intent(context, ViewEmergencyContactActivity.class);
        return intentEmergency;
    }
}
