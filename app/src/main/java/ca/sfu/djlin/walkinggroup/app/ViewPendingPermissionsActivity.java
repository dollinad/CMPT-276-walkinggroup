package ca.sfu.djlin.walkinggroup.app;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

import ca.sfu.djlin.walkinggroup.R;
import ca.sfu.djlin.walkinggroup.model.Session;
import ca.sfu.djlin.walkinggroup.model.User;
import ca.sfu.djlin.walkinggroup.proxy.WGServerProxy;

public class ViewPendingPermissionsActivity extends AppCompatActivity {
    private WGServerProxy proxy;

    ArrayAdapter<ca.cmpt276.walkinggroup.dataobjects.Message> pendingPermissionsListAdapter;
    List<ca.cmpt276.walkinggroup.dataobjects.Message> currentPendingPermissions = new ArrayList();

    // private String currentUserToken;
    // private String currentUserEmail;
    // private Long currentUserId;
    Session session;
    User currentUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pending_permissions);

        // Set up user session
        session = Session.getSession(getApplicationContext());
        currentUser = session.getUser();
        // currentUserEmail = currentUser.getEmail();
        // currentUserId = currentUser.getId();
        proxy = session.getProxy();

        Log.d("TAG", "The retrieved user is: " + currentUser.toString());
    }

    public static Intent launchViewPendingPermissionsIntent(Context context) {
        Intent intent = new Intent(context, ViewPendingPermissionsActivity.class);
        return intent;
    }
}
