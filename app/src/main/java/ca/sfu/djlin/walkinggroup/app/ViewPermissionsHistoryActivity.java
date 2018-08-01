package ca.sfu.djlin.walkinggroup.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.List;

import ca.sfu.djlin.walkinggroup.R;
import ca.sfu.djlin.walkinggroup.dataobjects.PermissionRequest;
import ca.sfu.djlin.walkinggroup.model.Session;
import ca.sfu.djlin.walkinggroup.model.User;
import ca.sfu.djlin.walkinggroup.proxy.ProxyBuilder;
import ca.sfu.djlin.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

public class ViewPermissionsHistoryActivity extends AppCompatActivity{
    private WGServerProxy proxy;

    Session session;
    User currentUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_permissions_history);

        // Set up user session
        session = Session.getSession(getApplicationContext());
        currentUser = session.getUser();
        proxy = session.getProxy();

        Log.d("TAG", "The retrieved user is: " + currentUser.toString());

        // Make a call to retrieve current pending requests
        retrievePendingRequests();
    }

    private void retrievePendingRequests() {
        Call<List<PermissionRequest>> call = proxy.getPermissions(currentUser.getId());
        ProxyBuilder.callProxy(ViewPermissionsHistoryActivity.this, call, permissionList -> returnedResponse(permissionList));
    }

    private void returnedResponse(List<PermissionRequest> permissionList) {
        Log.d("TAG", "The returned permission list is" + permissionList.toString());

        // Update current message list
        // currentPendingPermissions = permissionList;

        // Refresh the pending permissions list view
        // refreshPendingPermissionsList();
    }

    public static Intent launchPermissionsHistoryIntent(Context context) {
        Intent intent = new Intent(context, ViewPermissionsHistoryActivity.class);
        return intent;
    }
}
