package ca.sfu.djlin.walkinggroup.app.Messaging;

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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ca.cmpt276.walkinggroup.dataobjects.Message;
import ca.sfu.djlin.walkinggroup.R;
import ca.sfu.djlin.walkinggroup.dataobjects.Group;
import ca.sfu.djlin.walkinggroup.model.Session;
import ca.sfu.djlin.walkinggroup.model.User;
import ca.sfu.djlin.walkinggroup.proxy.ProxyBuilder;
import ca.sfu.djlin.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

public class LeaderMessagingActivity extends AppCompatActivity {
    private WGServerProxy proxy;

    ArrayAdapter<Group> groupListAdapter;
    List<Group> leadsGroupsList = new ArrayList();

    private String currentUserToken;
    private String currentUserEmail;
    private Long currentUserId;

    private String mSendMessageText;

    Session session;
    User currentUser;

    private boolean isEmergencyText = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_send_messages);

        // Set up proxy
        proxy = ProxyBuilder.getProxy(getString(R.string.apikey), currentUserToken);

        // Set up current user information
        session = Session.getSession(getApplicationContext());
        currentUser = session.getUser();
        proxy = session.getProxy();

        // Make a call to get current user information
        Long depth = new Long(1);
        Call<User> call = proxy.getUserById(currentUser.getId(), depth);
        ProxyBuilder.callProxy(LeaderMessagingActivity.this, call, returnedUser -> storeCurrentUser(returnedUser));

        // Build array adapter for group list
        groupListAdapter = new leadsGroupListAdapter(LeaderMessagingActivity.this, leadsGroupsList);
        ListView list = findViewById(R.id.leads_group_list);
        list.setAdapter(groupListAdapter);

        // Set up listener to send mail
        sendMailListener();
    }

    private void storeCurrentUser(User user) {
        Log.d("TAG", user.getLeadsGroups().toString());
        leadsGroupsList = user.getLeadsGroups();

        refreshGroupList();
    }

    private void refreshGroupList() {
        Log.d("TAG", "refreshMessageList: refreshing the message list: ");
        groupListAdapter.clear();
        groupListAdapter.addAll(leadsGroupsList);
        groupListAdapter.notifyDataSetChanged();
    }

    private class leadsGroupListAdapter extends ArrayAdapter<Group> {
        public leadsGroupListAdapter(Context context, List<Group> groups){
            super(context, R.layout.layout_group_list, groups);
        }

        View itemView;

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            // Get message data item for this position
            Group group = getItem(position);

            // Check if an existing view is being reused, otherwise inflate the view
            itemView = convertView;
            if(itemView == null){
                itemView = getLayoutInflater().inflate(R.layout.layout_group_list, parent, false);
            }

            // Lookup view for data population
            TextView groupName = (TextView) itemView.findViewById(R.id.group_name);

            // Update the body text
            groupName.setText(group.getGroupDescription());

            return itemView;
        }
    }
    private void sendMailListener() {
        ListView listView = (ListView) findViewById(R.id.leads_group_list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the groupId that user clicked on
                Long groupId = groupListAdapter.getItem(position).getId();
                Log.d("TAG", "The group id you clicked on is: " + groupId);

                // Build a dialog box
                AlertDialog.Builder builder = new AlertDialog.Builder(LeaderMessagingActivity.this);
                View viewInflated = LayoutInflater.from(LeaderMessagingActivity.this).inflate(R.layout.dialog_send_message, findViewById(R.id.leads_group_list), false);

                // Set up the input
                final EditText input = (EditText) viewInflated.findViewById(R.id.input);
                builder.setView(viewInflated);

                // Set up the buttons
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Remove dialog and grab information
                        dialog.dismiss();
                        mSendMessageText = input.getText().toString();

                        // Generate a new message
                        ca.cmpt276.walkinggroup.dataobjects.Message newMessage = new Message();
                        newMessage.setText(mSendMessageText);
                        newMessage.setEmergency(isEmergencyText);

                        // Make a new call to send the message
                        Call<List<ca.cmpt276.walkinggroup.dataobjects.Message>> call = proxy.newMessageToGroup(groupId, newMessage);
                        ProxyBuilder.callProxy(LeaderMessagingActivity.this, call, returnedList -> sendMessageResponse(returnedList));
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                // Display the dialog
                builder.show();

                // Onclick listener for checkbox
                CheckBox isEmergencyCheckbox = (CheckBox) viewInflated.findViewById(R.id.emergency_checkbox);
                isEmergencyCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        // Update the status of the checkbox
                        isEmergencyText = isChecked;
                    }
                });
            }
        });
    }
    private void sendMessageResponse(List<ca.cmpt276.walkinggroup.dataobjects.Message> listOfMessages) {
        Log.d("TAG", "sendMessageResponse: " + listOfMessages.toString());
    }

    public static Intent launchSendLeaderMessageIntent (Context context) {
        Intent intent = new Intent(context, LeaderMessagingActivity.class);
        return intent;
    }
}
