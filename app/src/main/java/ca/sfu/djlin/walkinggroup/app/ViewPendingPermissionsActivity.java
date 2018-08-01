package ca.sfu.djlin.walkinggroup.app;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ca.sfu.djlin.walkinggroup.R;
import ca.sfu.djlin.walkinggroup.dataobjects.PermissionRequest;
import ca.sfu.djlin.walkinggroup.model.Session;
import ca.sfu.djlin.walkinggroup.model.User;
import ca.sfu.djlin.walkinggroup.proxy.ProxyBuilder;
import ca.sfu.djlin.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

public class ViewPendingPermissionsActivity extends AppCompatActivity {
    private WGServerProxy proxy;

    ArrayAdapter<PermissionRequest> pendingPermissionsListAdapter;
    List<PermissionRequest> currentPendingPermissions = new ArrayList();

    Session session;
    User currentUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pending_permissions);

        // Set up user session
        session = Session.getSession(getApplicationContext());
        currentUser = session.getUser();
        proxy = session.getProxy();

        Log.d("TAG", "The retrieved user is: " + currentUser.toString());

        // Make a call to retrieve current pending requests
        Call<List<PermissionRequest>> call = proxy.getPermissions(currentUser.getId(), WGServerProxy.PermissionStatus.PENDING);
        ProxyBuilder.callProxy(ViewPendingPermissionsActivity.this, call, permissionList -> returnedResponse(permissionList));

        // Build array adapter for monitored by list
        pendingPermissionsListAdapter = new myPendingPermissionsListAdapter(ViewPendingPermissionsActivity.this, currentPendingPermissions);
        ListView list = findViewById(R.id.pending_permissions_list);
        list.setAdapter(pendingPermissionsListAdapter);
    }

    private class myPendingPermissionsListAdapter extends ArrayAdapter<PermissionRequest> {
        public myPendingPermissionsListAdapter(Context context, List<PermissionRequest> permissionRequests){
            super(context, R.layout.layout_pending_permissions_list, permissionRequests);
        }
        View itemView;

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            // Get Permission Request data item for this position
            PermissionRequest permissionRequest = getItem(position);

            // Check if an existing view is being reused, otherwise inflate the view
            itemView = convertView;
            if(itemView == null){
                itemView = getLayoutInflater().inflate(R.layout.layout_pending_permissions_list, parent, false);
            }

            TextView permissionRequestMessage = (TextView) itemView.findViewById(R.id.permission_request_details);
            permissionRequestMessage.setText(permissionRequest.getMessage());

            Log.d("TAG", "The clicked permission request is " + permissionRequest.toString());

            return itemView;
        }
    }

    private void returnedResponse(List<PermissionRequest> permissionList) {
        Log.d("TAG", "The returned permission list is" + permissionList.toString());

        // Update current message list
        currentPendingPermissions = permissionList;

        // Refresh the pending permissions list view
        refreshPendingPermissionsList();
    }

    private void refreshPendingPermissionsList() {
        Log.d("TAG", "refreshPendingPermissionsList: refreshing the pending permissions list: ");
        pendingPermissionsListAdapter.clear();
        pendingPermissionsListAdapter.addAll(currentPendingPermissions);
        pendingPermissionsListAdapter.notifyDataSetChanged();
    }

    public static Intent launchViewPendingPermissionsIntent(Context context) {
        Intent intent = new Intent(context, ViewPendingPermissionsActivity.class);
        return intent;
    }
}
