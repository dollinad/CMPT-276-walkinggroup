package ca.sfu.djlin.walkinggroup.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ca.sfu.djlin.walkinggroup.R;
import ca.sfu.djlin.walkinggroup.dataobjects.Group;
import ca.sfu.djlin.walkinggroup.model.User;
import ca.sfu.djlin.walkinggroup.proxy.ProxyBuilder;
import ca.sfu.djlin.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

public class GroupInfoActivity extends AppCompatActivity {

    // Constants
    public static final String TAG = "GroupInfoActivity";

    // Variables
    private Long currentUserId;
    private Long groupId;
    private String token;
    private Group currentGroup;
    private User user;

    private WGServerProxy proxy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);

        retrieveCurrentUserId();
        retrieveIntentData();

        // Set up our proxy
        proxy = ProxyBuilder.getProxy(getString(R.string.apikey), token);

        // Send a request to retrieve group information
        Call<Group> call = proxy.getGroupById(groupId);
        ProxyBuilder.callProxy(GroupInfoActivity.this, call, returnedGroupInfo -> testResponse(returnedGroupInfo));

        // Setup join group button
        Button joinGroupBtn = (Button) findViewById(R.id.join_group_btn);
        joinGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Call proxy to retrieve current user
                Call<User> call = proxy.getUserById(currentUserId);
                ProxyBuilder.callProxy(GroupInfoActivity.this, call, returnedUser -> userGroupAdd(returnedUser));
            }
        });

        // Setup leave group button
        Button leaveGroupBtn = (Button) findViewById(R.id.leave_group_btn);
        leaveGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Call proxy to retrieve current user
                Call<User> call = proxy.getUserById(currentUserId);
                ProxyBuilder.callProxy(GroupInfoActivity.this, call, returnedUser -> userGroupDelete(returnedUser));
            }
        });
    }

    private void testResponse(Group group) {
        Log.d(TAG, "testResponse: ");
        ArrayList<User> listOfMembers = group.getMemberUsers();

        for (int i = 0; i < listOfMembers.size(); i++) {
            Log.d(TAG, "User Id" + listOfMembers.get(i).getId());
        }
    }

    private void retrieveCurrentUserId() {
        SharedPreferences preferences = GroupInfoActivity.this.getSharedPreferences("User Session", MODE_PRIVATE);
        currentUserId = preferences.getLong("User Id", 0);
    }

    private void userGroupAdd(User user) {
        Log.d(TAG, "userGroupAdd: ");
        // After getting current user info response, make a call to add the user number to the group
        Call<List<User>> call = proxy.addGroupMember(groupId, user);
        ProxyBuilder.callProxy(GroupInfoActivity.this, call, returnedListOfUsers -> addMemberResponse(returnedListOfUsers));
    }

    private void addMemberResponse(List<User> listOfUsers) {
        // Log.d(TAG, "addMemberResponse: Displaying current members of the group: ");
        // listOfUsers.toString(); - Doesn't work
    }

    private void userGroupDelete(User user) {
        Log.d(TAG, "userGroupDelete: ");

        // After getting current user info response, make a call to delete the user number from the group
        Call<Void> call = proxy.removeGroupMember(groupId, user.getId());
        ProxyBuilder.callProxy(GroupInfoActivity.this, call, response -> removeMemberResponse(response));
    }

    private void removeMemberResponse(Void response) {
        Log.d(TAG, "removeMemberResponse: ");

        // FOR TESTING PURPOSES, CHECK TO SEE WHO IS STILL IN THE GROUP
        Call<List<User>> call = proxy.getGroupMembers(groupId);
        ProxyBuilder.callProxy(GroupInfoActivity.this, call, returnedListOfUsers -> getGroupMembersResponse(returnedListOfUsers));
    }

    private void getGroupMembersResponse(List<User> listOfUsers) {
        // Log.d(TAG, "getGroupMembersResponse: Displaying current members of the group: ");
        // listOfUsers.toString(); - Doesn't work
    }

    private void groupInfoResponse (Group group) {
        // Save information used to populate activity
        String groupDescription = group.getGroupDescription();

        TextView title = (TextView) findViewById(R.id.group_description);
        title.setText(groupDescription);
    }

    private void retrieveIntentData() {
        // Retrieve information from intent
        Intent intent = getIntent();
        groupId = intent.getLongExtra("groupId", 0);
        token = intent.getStringExtra("token");
    }

    public static Intent launchGroupInfoIntent (Context context) {
        Intent intent = new Intent(context, GroupInfoActivity.class);
        return intent;
    }
}
