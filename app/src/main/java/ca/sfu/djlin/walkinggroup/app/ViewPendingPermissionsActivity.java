package ca.sfu.djlin.walkinggroup.app;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
        setupArrayAdapter();

        // Setup on-click listener for permission request list items
        setupPendingPermissionListener();
    }

    private void setupArrayAdapter() {
        pendingPermissionsListAdapter = new myPendingPermissionsListAdapter(ViewPendingPermissionsActivity.this, currentPendingPermissions);
        ListView list = findViewById(R.id.pending_permissions_list);
        list.setAdapter(pendingPermissionsListAdapter);
    }

    // Implement pending permissions list adapter
    private class myPendingPermissionsListAdapter extends ArrayAdapter<PermissionRequest> {
        public myPendingPermissionsListAdapter(Context context, List<PermissionRequest> permissionRequests){
            super(context, R.layout.layout_pending_permissions_list, permissionRequests);
        }
        View itemView;

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            // Get permission request data for the particular list item position
            PermissionRequest permissionRequest = getItem(position);

            // Check if an existing view is being reused, otherwise inflate the view
            itemView = convertView;
            if(itemView == null){
                itemView = getLayoutInflater().inflate(R.layout.layout_pending_permissions_list, parent, false);
            }

            // Set the text in the list view
            TextView permissionRequestMessage = (TextView) itemView.findViewById(R.id.permission_request_details);
            permissionRequestMessage.setText(permissionRequest.getMessage());

            return itemView;
        }
    }

    private void setupPendingPermissionListener() {
        ListView listView = findViewById(R.id.pending_permissions_list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get pending request clicked from list view
                PermissionRequest pendingPermission = currentPendingPermissions.get(position);

                AlertDialog.Builder builder = new AlertDialog.Builder(ViewPendingPermissionsActivity.this);
                View viewInflated = LayoutInflater.from(ViewPendingPermissionsActivity.this).inflate(R.layout.dialog_view_pending_permission_request, findViewById(R.id.pending_permissions_list), false);

                // Set the details of the dialog box
                TextView permissionRequestMessage = (TextView) viewInflated.findViewById(R.id.permission_request_message);
                permissionRequestMessage.setText(pendingPermission.getMessage());

                // Set view for the dialog box
                builder.setView(viewInflated);

                // Set positive button for dialog box
                builder.setPositiveButton("APPROVE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // TODO: Approve the request
                    }
                });

                // Set negative button for dialog box
                builder.setNegativeButton("DENY", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // TODO: Deny the request
                    }
                });

                // Show the dialog box
                builder.show();
            }
        });
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
