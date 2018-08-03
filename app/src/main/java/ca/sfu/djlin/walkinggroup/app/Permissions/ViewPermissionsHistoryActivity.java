package ca.sfu.djlin.walkinggroup.app.Permissions;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import java.util.Iterator;
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

    ArrayAdapter<PermissionRequest> permissionRequestListAdapter;
    List<PermissionRequest> permissionRequestList = new ArrayList();

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

        // Build array adapter for monitored by list
        setupArrayAdapter();

        // Setup on-click listener for permission history list items
        pendingPermissionListListener();
    }

    private void retrievePendingRequests() {
        Call<List<PermissionRequest>> call = proxy.getPermissions(currentUser.getId(), new Long(1));
        ProxyBuilder.callProxy(ViewPermissionsHistoryActivity.this, call, permissionList -> returnedResponse(permissionList));
    }

    private void returnedResponse(List<PermissionRequest> permissionList) {
        Log.d("TAG", "The returned permission list is" + permissionList.toString());

        // Update current message list
        permissionRequestList = permissionList;

        // Refresh the pending permissions list view
        refreshPermissionRequestList();
    }

    private void setupArrayAdapter() {
        permissionRequestListAdapter = new myPermissionRequestListAdapter(ViewPermissionsHistoryActivity.this, permissionRequestList);
        ListView list = findViewById(R.id.permissions_list);
        list.setAdapter(permissionRequestListAdapter);
    }

    // Implement permissions list adapter
    private class myPermissionRequestListAdapter extends ArrayAdapter<PermissionRequest> {
        public myPermissionRequestListAdapter(Context context, List<PermissionRequest> permissionRequests){
            super(context, R.layout.layout_permissions_history_list, permissionRequests);
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
                itemView = getLayoutInflater().inflate(R.layout.layout_permissions_history_list, parent, false);
            }

            // Set the text in the list view
            TextView permissionRequestMessage = (TextView) itemView.findViewById(R.id.permission_request_details);
            permissionRequestMessage.setText(permissionRequest.getMessage());

            return itemView;
        }
    }

    private void pendingPermissionListListener() {
        ListView list = (ListView) findViewById(R.id.permissions_list);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PermissionRequest permissionRequest = permissionRequestList.get(position);

                AlertDialog.Builder builder = new AlertDialog.Builder(ViewPermissionsHistoryActivity.this);
                View viewInflated = LayoutInflater.from(ViewPermissionsHistoryActivity.this).inflate(R.layout.dialog_view_permission_request_history, findViewById(R.id.permissions_list), false);

                // Set the details of the dialog box
                TextView status = (TextView) viewInflated.findViewById(R.id.permission_request_status);
                TextView details = (TextView) viewInflated.findViewById(R.id.permission_request_message);
                TextView approved = (TextView) viewInflated.findViewById(R.id.approved_by);
                TextView declined = (TextView) viewInflated.findViewById(R.id.denied_by);
                TextView pending = (TextView) viewInflated.findViewById(R.id.pending_users);

                // Log.d("TAG", "Permission Request Clicked: " + permissionRequest.toString());
                // Log.d("TAG", "Getting authorizors: " + permissionRequest.getAuthorizors());

                // Set up strings to update dialog box with
                String approvedUsers = "";
                String declinedUsers = "";
                String pendingUsers = "";

                // Iterate over the set of authorizations to retrieve who approved and denied the request
                Iterator<PermissionRequest.Authorizor> setIterator = permissionRequest.getAuthorizors().iterator();
                while(setIterator.hasNext()){
                    PermissionRequest.Authorizor request = setIterator.next();
                    if (request.getStatus() == WGServerProxy.PermissionStatus.PENDING) {
                        if (request.getWhoApprovedOrDenied() != null) {
                            pendingUsers += request.getWhoApprovedOrDenied().getName() + ", ";
                        }
                    }
                    if (request.getStatus() == WGServerProxy.PermissionStatus.APPROVED) {
                        if (request.getWhoApprovedOrDenied() != null) {
                            approvedUsers += request.getWhoApprovedOrDenied().getName() + ", ";
                        }
                    }
                    if (request.getStatus() == WGServerProxy.PermissionStatus.DENIED) {
                        if (request.getWhoApprovedOrDenied() != null) {
                            declinedUsers += request.getWhoApprovedOrDenied().getName() + ", ";
                        }
                    }
                    // Log.d("TAG", "Status is: " + request.getStatus());
                    // Log.d("TAG", "Who Approved and denied: " + request.getWhoApprovedOrDenied());
                }

                // Populate empty fields
                if (pendingUsers.isEmpty()) {
                    pendingUsers = "N/A";
                } else {
                    pendingUsers = pendingUsers.substring(0, pendingUsers.length() - 2);
                }
                if (approvedUsers.isEmpty()) {
                    approvedUsers = "N/A";
                } else {
                    approvedUsers = approvedUsers.substring(0, approvedUsers.length() - 2);
                }
                if (declinedUsers.isEmpty()) {
                    declinedUsers = "N/A";
                } else {
                    declinedUsers = declinedUsers.substring(0, declinedUsers.length() - 2);
                }

                // Set the text for dialog box fields
                status.setText(permissionRequest.getStatus().toString());
                details.setText(permissionRequest.getMessage());
                pending.setText(pendingUsers);
                approved.setText(approvedUsers);
                declined.setText(declinedUsers);

                // Set view for the dialog box
                builder.setView(viewInflated);

                // Set positive button for dialog box
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                // Show the dialog box
                builder.show();
            }
        });

    }

    private void refreshPermissionRequestList() {
        Log.d("TAG", "refreshPendingPermissionsList: refreshing the pending permissions list: ");
        permissionRequestListAdapter.clear();
        permissionRequestListAdapter.addAll(permissionRequestList);
        permissionRequestListAdapter.notifyDataSetChanged();
    }

    public static Intent launchPermissionsHistoryIntent(Context context) {
        Intent intent = new Intent(context, ViewPermissionsHistoryActivity.class);
        return intent;
    }
}
