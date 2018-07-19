package ca.sfu.djlin.walkinggroup.app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ca.sfu.djlin.walkinggroup.R;
import ca.sfu.djlin.walkinggroup.app.messaging.LeaderMessagingActivity;
import ca.sfu.djlin.walkinggroup.model.Session;
import ca.sfu.djlin.walkinggroup.model.User;
import ca.sfu.djlin.walkinggroup.proxy.ProxyBuilder;
import ca.sfu.djlin.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

public class ViewMessagesActivity extends AppCompatActivity {
    private WGServerProxy proxy;

    ArrayAdapter<ca.cmpt276.walkinggroup.dataobjects.Message> messageListAdapter;
    List<ca.cmpt276.walkinggroup.dataobjects.Message> currentMessageList = new ArrayList();

    private String currentUserToken;
    private String currentUserEmail;
    private Long currentUserId;
    Session session;
    User currentUser;

    // Used for testing
    ca.cmpt276.walkinggroup.dataobjects.Message testMessage = new ca.cmpt276.walkinggroup.dataobjects.Message();
    // End used for testing

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_messages);

        // Retrieve current user information
       // getCurrentUserInformation();

        // Set up proxy, include depth of 1 to get more information about the users
        //proxy = ProxyBuilder.getProxy(getString(R.string.apikey), currentUserToken, 1);
        session=Session.getSession(getApplicationContext());
        currentUser=session.getUser();
        currentUserEmail=currentUser.getEmail();
        currentUserId=currentUser.getId();
        proxy = session.getProxy();

        Log.d("TAG", "The retrieved user is: " + currentUser.toString());

        // Send a request to get all the messages that the current user has
        Call<List<ca.cmpt276.walkinggroup.dataobjects.Message>> call = proxy.getMessages(currentUserId);
        ProxyBuilder.callProxy(ViewMessagesActivity.this, call, messageList -> getMessageListResponse(messageList));

        // Build array adapter for monitored by list
        messageListAdapter = new myMessagesListAdapter(ViewMessagesActivity.this, currentMessageList);
        ListView list = findViewById(R.id.messages_list);
        list.setAdapter(messageListAdapter);

        // Set up listener to read mail
        readMailListener();

        // Setup temporary button to send the test group some messages
        setupTestButtons();
        // End temporary button to send the test group some messages
    }

    private void getCurrentUserInformation() {
        // Get shared preferences
        SharedPreferences preferences = this.getSharedPreferences("User Session", MODE_PRIVATE);
        currentUserToken = preferences.getString("Token", null);
        currentUserEmail = preferences.getString("Email", null);
        currentUserId = preferences.getLong("User Id", 0);
    }

    private void getMessageListResponse(List<ca.cmpt276.walkinggroup.dataobjects.Message> messageList) {
        // Update current message list
        Log.d("TAG", "getMessageListResponse: attempting updating message list: ");
        currentMessageList = messageList;

        // Refresh the list
        refreshMessageList();
    }

    private void refreshMessageList() {
        Log.d("TAG", "refreshMessageList: refreshing the message list: ");
        messageListAdapter.clear();
        messageListAdapter.addAll(currentMessageList);
        messageListAdapter.notifyDataSetChanged();
    }

    private void setMailAsRead(ca.cmpt276.walkinggroup.dataobjects.Message returnedMessage) {

        // Make a call to set mail as read
        Long messageId = returnedMessage.getId();
        Call<ca.cmpt276.walkinggroup.dataobjects.Message> call = proxy.markMessageAsRead(messageId, true);
        ProxyBuilder.callProxy(ViewMessagesActivity.this, call, message -> setMailAsReadResponse(message));
    }

    private void setMailAsReadResponse(ca.cmpt276.walkinggroup.dataobjects.Message returnedMessage) {
        Long id = returnedMessage.getId();

        // Updates local current message list
        for (int i = 0; i < currentMessageList.size(); i++) {
            if (id.equals(currentMessageList.get(i).getId())) {
                currentMessageList.get(i).setIsRead(true);
            }
        }

        // Needs to do this in order to refresh listview
        messageListAdapter.clear();
        messageListAdapter.notifyDataSetChanged();

        // Send a request to get all the messages that the current user has -- Need this to refresh list view
        Call<List<ca.cmpt276.walkinggroup.dataobjects.Message>> call = proxy.getMessages(currentUserId);
        ProxyBuilder.callProxy(ViewMessagesActivity.this, call, messageList -> getMessageListResponse(messageList));
    }

    private class myMessagesListAdapter extends ArrayAdapter<ca.cmpt276.walkinggroup.dataobjects.Message> {
        public myMessagesListAdapter(Context context, List<ca.cmpt276.walkinggroup.dataobjects.Message> messages){
            super(context, R.layout.layout_messages_list, messages);
        }

        View itemView;

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            // Get message data item for this position
            ca.cmpt276.walkinggroup.dataobjects.Message message = getItem(position);

            // Check if an existing view is being reused, otherwise inflate the view
            itemView = convertView;
            if(itemView == null){
                itemView = getLayoutInflater().inflate(R.layout.layout_messages_list, parent, false);
            }

            // Lookup view for data population
            TextView messageSenderName = (TextView) itemView.findViewById(R.id.message_sender_name);
            TextView messageBodyText = (TextView) itemView.findViewById(R.id.message_body);

            // Update the body text
            messageSenderName.setText(message.getFromUser().getName());
            messageBodyText.setText(message.getText());

            // Bolds the message if unread
            if (!message.isRead()) {
                messageSenderName.setTextAppearance(ViewMessagesActivity.this, R.style.fontForUnreadMail);
                messageBodyText.setTextAppearance(ViewMessagesActivity.this, R.style.fontForUnreadMail);
            } else {
                messageSenderName.setTextAppearance(ViewMessagesActivity.this, R.style.fontForReadMail);
                messageBodyText.setTextAppearance(ViewMessagesActivity.this, R.style.fontForReadMail);
            }

            return itemView;
        }
    }

    private void readMailListener() {
        ListView listView = (ListView) findViewById(R.id.messages_list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // Display message in dialog
                ca.cmpt276.walkinggroup.dataobjects.Message messageToRead = currentMessageList.get(position);
                new AlertDialog.Builder(ViewMessagesActivity.this)
                        .setMessage(messageToRead.getText())
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        }).show();

                // Set message to be read
                if (!currentMessageList.get(position).isRead()) {
                    // Set the mail to be read
                    Long messageId = currentMessageList.get(position).getId();
                    Call<ca.cmpt276.walkinggroup.dataobjects.Message> call = proxy.getOneMessage(messageId);
                    ProxyBuilder.callProxy(ViewMessagesActivity.this, call, returnedMessage -> setMailAsRead(returnedMessage));
                }
            }
        });
    }

    public static Intent launchViewMessageIntent (Context context) {
        Intent intent = new Intent(context, ViewMessagesActivity.class);
        return intent;
    }

    // Used for testing
    private void setupTestButtons() {
        // Details for the test message
        testMessage.setText("This is a test message 10");
        testMessage.setEmergency(true);

        // Button to test sending a message
        Button sendMessageButton = (Button) findViewById(R.id.send_message_btn);
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Long groupNumber = new Long(105);
                Call<List<ca.cmpt276.walkinggroup.dataobjects.Message>> call = proxy.newMessageToGroup(groupNumber, testMessage);
                ProxyBuilder.callProxy(ViewMessagesActivity.this, call, sentMessageList -> sentMessageResponse(sentMessageList));
            }
        });

        // Button to join test group
        Button leaderSendMessageBtn = (Button) findViewById(R.id.launch_leader_send_btn);
        leaderSendMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = LeaderMessagingActivity.launchSendLeaderMessageIntent(ViewMessagesActivity.this);
                startActivity(intent);

            }
        });
    }

    private void getCurrentUserResponse(User user) {
        Long groupNumber = new Long(105);
        // Call<List<User>> addGroupMember(@Path("id") Long groupId, @Body User user);
        Call<List<User>> call = proxy.addGroupMember(groupNumber, user);
        ProxyBuilder.callProxy(ViewMessagesActivity.this, call, listOfMembers -> addGroupMemberResponse(listOfMembers));
    }

    private void addGroupMemberResponse(List<User> listOfUsers) {

    }

    private void sentMessageResponse(List<ca.cmpt276.walkinggroup.dataobjects.Message> messageList) {
        // Sent message returns a response of the message sent, so we have to make a call to retrieve the latest messages again
        Call<List<ca.cmpt276.walkinggroup.dataobjects.Message>> call = proxy.getMessages(currentUserId);
        ProxyBuilder.callProxy(ViewMessagesActivity.this, call, returnedMessageList -> getMessageListResponse(returnedMessageList));
    }
    // End used for testing
}
