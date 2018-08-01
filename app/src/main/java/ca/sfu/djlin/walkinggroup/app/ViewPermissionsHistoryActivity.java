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
    }

    private void retrievePendingRequests() {
        Call<List<PermissionRequest>> call = proxy.getPermissions(currentUser.getId());
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
