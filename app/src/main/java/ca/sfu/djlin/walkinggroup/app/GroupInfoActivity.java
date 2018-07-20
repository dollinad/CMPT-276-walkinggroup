package ca.sfu.djlin.walkinggroup.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import ca.sfu.djlin.walkinggroup.R;
import ca.sfu.djlin.walkinggroup.dataobjects.Group;
import ca.sfu.djlin.walkinggroup.model.Session;
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
    private User currentUser;
    private List<User> monitorsUsersList;
    int time;
    Session data;

    ArrayAdapter<User> adapterMemberList;

    private WGServerProxy proxy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);

        data=Session.getSession(getApplicationContext());
        proxy=data.getProxy();
        currentUser=data.getUser();
        monitorsUsersList = currentUser.getMonitorsUsers();

        currentUserId=currentUser.getId();
        // Get token to set up proxy
        retrieveIntentData();

        // Set up our proxy
        //proxy = ProxyBuilder.getProxy(getString(R.string.apikey), token);

        // Get information about current user
        //retrieveCurrentUserId();
        //retrieveCurrentUserInformation();

        //get Intent
        Intent intent=getIntent();
        groupId=intent.getLongExtra("groupId", 0);


        // Update Group Description
        Call<Group> call = proxy.getGroupById(groupId);
        ProxyBuilder.callProxy(GroupInfoActivity.this, call, returnedGroupInfo -> updateGroupDescription(returnedGroupInfo));

        // Refresh UI
        refreshUI();

        registerClickCallback();

        // Setup add user button
        Button addUserBtn = (Button) findViewById(R.id.add_user_btn);
        addUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get input string
                String addUserEmail;
                EditText addUserEmailInput = (EditText) findViewById(R.id.add_user_input);
                addUserEmail = addUserEmailInput.getText().toString();

                // Make a call to proxy to obtain the user id
                Call<User> call = proxy.getUserByEmail(addUserEmail);
                ProxyBuilder.callProxy(GroupInfoActivity.this, call, returnedUserInfo -> addUserResponse(returnedUserInfo));
                addUserEmailInput.setText("");
            }
        });

        // Setup remove user button
        Button removeUserBtn = (Button) findViewById(R.id.remove_user_btn);
        removeUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get input string
                String removeUserEmail;
                EditText removeUserEmailInput = (EditText) findViewById(R.id.remove_user_input);
                removeUserEmail = removeUserEmailInput.getText().toString();

                // First make a call to proxy to get the id of user to remove
                Call<User> call = proxy.getUserByEmail(removeUserEmail);
                // ProxyBuilder.callProxy(GroupInfoActivity.this, call, returnedUserInfo -> retrieveUserByEmail(returnedUserInfo));
                ProxyBuilder.callProxy(GroupInfoActivity.this, call, returnedUserInfo -> removeUserResponse(returnedUserInfo));
            }
        });

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

    private void refreshUI() {
        // Update the UI
        Call<Group> call = proxy.getGroupById(groupId);
        ProxyBuilder.callProxy(GroupInfoActivity.this, call, returnedGroupInfo -> retrieveGroupInfo(returnedGroupInfo));
    }

    // Set up group member list
    private class memberListAdapter extends ArrayAdapter<User> {
        public memberListAdapter(){
            super(GroupInfoActivity.this, R.layout.layout_monitoring_list, currentGroup.getMemberUsers());
        }
        View itemView;

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            itemView = convertView;
            if(itemView == null){
                itemView = getLayoutInflater().inflate(R.layout.layout_monitoring_list, parent, false);
            }

            // Make instance of user to retrieve information for
            User user = currentGroup.getMemberUsers().get(position);
            TextView name = itemView.findViewById(R.id.list_name);
            TextView email = itemView.findViewById(R.id.list_email);

            // Make a call to collect the name and email of the user
            Call<User> call = proxy.getUserById(user.getId());
            ProxyBuilder.callProxy(GroupInfoActivity.this, call, returnUser -> respond(returnUser, name, email));
            return itemView;
        }

        private void respond(User returnUser, TextView name, TextView email) {
            // Update the item view with user information
            name.setText(returnUser.getName());
            email.setText(returnUser.getEmail());
        }
    }

    private void registerClickCallback() {
        ListView listView = findViewById(R.id.createGroupmember_list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Build Proxy
                //proxy = ProxyBuilder.getProxy(getString(R.string.apikey), token);
                Intent intent = ViewBeingMonitoredByUsers.launchIntentBeingMonitored(GroupInfoActivity.this);
                //intent.putExtra("token", token);
                //Log.i("PLEASE WORK", returnedUser.getId()+"");
                intent.putExtra("UserId",monitorsUsersList.get(position).getId());
                startActivity(intent);
                //proxy=data.getProxy();
                //Call<User> caller = proxy.getUserById(monitorsUsersList.get(position).getId());
                //ProxyBuilder.callProxy(GroupInfoActivity.this, caller, returnedUser -> UserReturned(returnedUser));
            }
        });
    }
    private void UserReturned(User returnedUser) {
        Intent intent = ViewBeingMonitoredByUsers.launchIntentBeingMonitored(GroupInfoActivity.this);
        //intent.putExtra("token", token);
        Log.i("PLEASE WORK", returnedUser.getId()+"");
        intent.putExtra("UserId",returnedUser.getId());
        startActivity(intent);
    }
    private void addUserResponse(User returnedUser) {
        Long userId = returnedUser.getId();
        // Iterate through our list of who the current user monitors
        for (User user : monitorsUsersList) {
            // Add the user if he is being monitored by current user
            if (userId.equals(user.getId())) {
                Call<List<User>> call = proxy.addGroupMember(groupId, returnedUser);
                ProxyBuilder.callProxy(GroupInfoActivity.this, call, returnedUserList -> addMemberResponse(returnedUserList));
            }
        }
    }

    private void removeUserResponse(User returnedUser) {
        Long userId = returnedUser.getId();

        // Check if are the leader of the group
        if (currentGroup.getLeader() != null) {
            if (currentUserId.equals(currentGroup.getLeader().getId())) {
                // Proceed to remove user
                Call<Void> call = proxy.removeGroupMember(groupId, userId);
                ProxyBuilder.callProxy( GroupInfoActivity.this, call, returnedNothing -> deleteUserResponse(returnedNothing));
            }
        }

        // Iterate through our list of who the current user monitors
        for (User user : monitorsUsersList) {
            // Remove the user if he is being monitored by current user
            if (userId.equals(user.getId())) {
                Call<Void> call = proxy.removeGroupMember(groupId, userId);
                ProxyBuilder.callProxy(GroupInfoActivity.this, call, returnedNothing -> deleteUserResponse(returnedNothing));
            }
        }
    }

    private void retrieveGroupInfo(Group group) {
        Log.d(TAG, "retrieveGroupInfo: ");

        // Save group information into currentGroup
        currentGroup = group;

        // Set up array adapter
        adapterMemberList = new memberListAdapter();
        ListView list = findViewById(R.id.createGroupmember_list);
        list.setAdapter(adapterMemberList);
    }

    private void retrieveUserByEmail(User user) {
        // Obtain id of user to delete
        Long deleteUserId = user.getId();

        // Check if the current user is the leader and delete user
        Call<Group> call = proxy.getGroupById(groupId);
        ProxyBuilder.callProxy(GroupInfoActivity.this, call, returnedGroupInfo -> isLeaderResponse(returnedGroupInfo, deleteUserId));

    }

    private void isLeaderResponse(Group group, Long deleteUserId) {
        Log.d(TAG, "isLeaderResponse: the leader of this group is: " + group.getLeader().getId());

        // Determine if removal of member is allowed
        if (currentUserId.equals(group.getLeader().getId())) {
            // Proceed to remove user
            Call<Void> call = proxy.removeGroupMember(groupId, deleteUserId);
            ProxyBuilder.callProxy( GroupInfoActivity.this, call, returnedNothing -> deleteUserResponse(returnedNothing));
        } else {
            // Let user know they are not the leader
            Toast.makeText(GroupInfoActivity.this, GroupInfoActivity.this.getString(R.string.not_leader_toast), Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteUserResponse (Void response) {
        Toast.makeText(GroupInfoActivity.this, GroupInfoActivity.this.getString(R.string.user_deleted_toast), Toast.LENGTH_SHORT).show();

        // Update the UI
        refreshUI();
    }

    private void userGroupAdd(User user) {
        Log.d(TAG, "userGroupAdd: The currentUserId is: " + currentUserId);

        // After getting current user info response, make a call to add the user number to the group
        Call<List<User>> call = proxy.addGroupMember(groupId, user);
        ProxyBuilder.callProxy(GroupInfoActivity.this, call, returnedListOfUsers -> addMemberResponse(returnedListOfUsers));
    }

    private void addMemberResponse(List<User> listOfUsers) {
        Toast.makeText(GroupInfoActivity.this, GroupInfoActivity.this.getString(R.string.joined_group_toast), Toast.LENGTH_SHORT).show();

        // Update the UI
        refreshUI();
    }

    private void userGroupDelete(User user) {
        Log.d(TAG, "userGroupDelete: ");

        // After getting current user info response, make a call to delete the user number from the group
        Call<Void> call = proxy.removeGroupMember(groupId, user.getId());
        ProxyBuilder.callProxy(GroupInfoActivity.this, call, response -> removeMemberResponse(response));
    }

    private void removeMemberResponse(Void response) {
        Log.d(TAG, "removeMemberResponse: ");

        // Update the UI
        refreshUI();
    }

    private void getGroupMembersResponse(List<User> listOfUsers) {
        // Log.d(TAG, "getGroupMembersResponse: Displaying current members of the group: ");
        //dont need to do anything here.
    }

    private void updateGroupDescription (Group group) {

        Toast.makeText(getApplicationContext(), group.getGroupDescription()+"", Toast.LENGTH_SHORT).show();
        // Save information used to populate activity
        String groupDescription = group.getGroupDescription();

        TextView title = (TextView) findViewById(R.id.group_description);
        title.setText(groupDescription);
    }

    private void retrieveCurrentUserInformation() {
        Call<User> call = proxy.getUserById(currentUserId);
        ProxyBuilder.callProxy(GroupInfoActivity.this, call, returnedUser -> storeUserInfo(returnedUser));
    }

    private void storeUserInfo(User returnedUser) {
        currentUser = returnedUser;
        monitorsUsersList = returnedUser.getMonitorsUsers();
    }

    private void retrieveCurrentUserId() {
        SharedPreferences preferences = GroupInfoActivity.this.getSharedPreferences("User Session", MODE_PRIVATE);
        currentUserId = preferences.getLong("User Id", 0);
    }

    private void retrieveIntentData() {
        // Retrieve information from intent
        Intent intent = getIntent();
        groupId = intent.getLongExtra("groupId", 0);
        token = intent.getStringExtra("token");

        //System.out.println("speicalcase2"+token);
    }

    public static Intent launchGroupInfoIntent (Context context) {
        Intent intent = new Intent(context, GroupInfoActivity.class);
        return intent;
    }


    public void test(){
        Timer timer=new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if(time==5)
                    timer.cancel();
                System.out.println("honghonghuohuo1");
                time++;
            }
        },
        0,5000);
    }
}
