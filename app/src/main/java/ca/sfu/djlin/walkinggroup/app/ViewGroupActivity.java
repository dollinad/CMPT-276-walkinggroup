package ca.sfu.djlin.walkinggroup.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import ca.sfu.djlin.walkinggroup.R;
import ca.sfu.djlin.walkinggroup.dataobjects.Group;
import ca.sfu.djlin.walkinggroup.model.Session;
import ca.sfu.djlin.walkinggroup.model.User;
import ca.sfu.djlin.walkinggroup.proxy.ProxyBuilder;
import ca.sfu.djlin.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

public class ViewGroupActivity extends AppCompatActivity {
    public static final int REQUEST_CODE_GET_DATA = 1023;

    private WGServerProxy proxy;
    String currentUserToken;
    String currentUserEmail;
    User currentUser;
    Long groupId;
    User groupLeader;
    Session session;

    ArrayAdapter<Group> adapter;

    ArrayAdapter<Group> leaderAdapter;

    List<Group> currentMemberGroups;
    List<Group> currentLeaderGroups;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_groups);

        Button button = findViewById(R.id.id_create_group);
        button.setVisibility(View.INVISIBLE);

        //Build Proxy
        session = Session.getSession(getApplicationContext());
        proxy = session.getProxy();
        currentUser = session.getUser();
        currentUserEmail = currentUser.getEmail();

        // Get current user information
        Call<User> caller = proxy.getUserByEmail(currentUserEmail);
        ProxyBuilder.callProxy(ViewGroupActivity.this, caller, returnedUser -> responseCurrent(returnedUser));
        registerClickCallbackMember();
        registerClickCallbackLeader();

    }

    //response for current user
    private void responseCurrent(User user) {
        // Store retrieved user into currentUser
        currentUser = user;
        if (user.getMemberOfGroups().size() == 0 && user.getLeadsGroups().size() == 0) {
            TextView textView = findViewById(R.id.current_groups);
            textView.setText("You are currently not a part of any group!");
            Button button = findViewById(R.id.id_create_group);
            button.setVisibility(View.VISIBLE);
        }
        currentMemberGroups = user.getMemberOfGroups();
        currentLeaderGroups = user.getLeadsGroups();

        adapter = new myListMemberAdapter();
        ListView list = findViewById(R.id.id_current_groups);
        list.setAdapter(adapter);

        leaderAdapter = new myListLeaderAdapter();
        ListView listLeader = findViewById(R.id.currently_leader_of);
        listLeader.setAdapter(leaderAdapter);
    }


    // Adapter for the Member of list
    private class myListMemberAdapter extends ArrayAdapter<Group> {
        public myListMemberAdapter() {
            super(ViewGroupActivity.this, R.layout.layout_view_groups, currentMemberGroups);
        }

        View itemView;

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.layout_view_groups, parent, false);
            }

            TextView description = itemView.findViewById(R.id.id_group_description);
            TextView leader = itemView.findViewById(R.id.id_group_leader);

            // Check if the current user is currently in any groups
            if (currentUser.getMemberOfGroups().size() != 0) {
                Call<Group> call = proxy.getGroupById(currentUser.getMemberOfGroups().get(position).getId());
                ProxyBuilder.callProxy(ViewGroupActivity.this, call, returnedGroup -> groupReturned(returnedGroup, description, leader));
            }

            return itemView;
        }

        //response function for if the user is leading any groups
        private void groupReturned(Group returnedGroup, TextView description, TextView leader) {
            description.setText(returnedGroup.getGroupDescription());
            leader.setText(returnedGroup.getLeader().getName());
            groupId = returnedGroup.getId();

        }
    }

    //Adapter for the Member of list
    private class myListLeaderAdapter extends ArrayAdapter<Group> {
        public myListLeaderAdapter() {
            super(ViewGroupActivity.this, R.layout.layout_view_groups, currentLeaderGroups);
        }

        View itemView;

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.layout_view_groups, parent, false);
            }

            // List layout (des= description of the group and leader)
            TextView description = itemView.findViewById(R.id.id_group_description);
            TextView leader = itemView.findViewById(R.id.id_group_leader);

            // Checking if the current group is leading any groups
            if (currentUser.getLeadsGroups().size() != 0) {
                Call<Group> caller = proxy.getGroupById(currentUser.getLeadsGroups().get(position).getId());
                ProxyBuilder.callProxy(ViewGroupActivity.this, caller, returnedGroup -> groupReturnedLeader(returnedGroup, description, leader));
            }
            return itemView;
        }

        //response function for if the user is a part of any group
        private void groupReturnedLeader(Group returnedGroup, TextView description, TextView leader) {
            description.setText(returnedGroup.getGroupDescription());
            leader.setText(returnedGroup.getLeader().getName());

            groupId = returnedGroup.getId();
        }
    }


    private void getUserById(Long Id) {
        Call<User> caller = proxy.getUserById(Id);
        ProxyBuilder.callProxy(ViewGroupActivity.this, caller, returnedUser -> userResponse(returnedUser));
    }

    private void userResponse(User returnedUser) {
        groupLeader = returnedUser;
    }

    private void registerClickCallbackMember() {
        ListView listView = findViewById(R.id.id_current_groups);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Build Proxy
                Call<Group> caller = proxy.getGroupById(currentUser.getMemberOfGroups().get(position).getId());
                ProxyBuilder.callProxy(ViewGroupActivity.this, caller, returnedGroup -> groupReturnedPass(returnedGroup));
            }
        });

    }

    private void registerClickCallbackLeader() {
        ListView listViewLeader = findViewById(R.id.currently_leader_of);
        listViewLeader.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Build Proxy
                Call<Group> caller = proxy.getGroupById(currentUser.getLeadsGroups().get(position).getId());
                ProxyBuilder.callProxy(ViewGroupActivity.this, caller, returnedGroup -> groupReturnedPass(returnedGroup));
            }
        });
    }

    private void groupReturnedPass(Group returnedGroup) {
        Intent intent = GroupInfoActivity.launchGroupInfoIntent(ViewGroupActivity.this);
        intent.putExtra("token", currentUserToken);
        intent.putExtra("email", currentUserEmail);
        intent.putExtra("groupId", returnedGroup.getId());
        startActivityForResult(intent, REQUEST_CODE_GET_DATA);
        intent.removeExtra("token");
        intent.removeExtra("email");
        intent.removeExtra("groupId");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_GET_DATA:
                if (resultCode == Activity.RESULT_OK) {
                    Long groupID_pass = GroupInfoActivity.getResultGroupId(data);
                    Intent intent = new Intent();
                    intent.putExtra("eventGroupId", groupID_pass);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                } else {
                    Log.i("My app", "Activity cancelled.");
                }
        }
    }

    public static Long getResultGroupId(Intent intent) {
        return intent.getLongExtra("eventGroupId", 0);
    }

    public static Intent launchIntentViewGroups(Context context) {
        Intent intentViewGroups = new Intent(context, ViewGroupActivity.class);
        return intentViewGroups;
    }
}
