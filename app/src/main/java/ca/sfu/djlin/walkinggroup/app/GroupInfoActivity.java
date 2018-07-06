package ca.sfu.djlin.walkinggroup.app;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
    private Long groupId;
    private String token;
    private Group currentGroup;

    private WGServerProxy proxy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);

        retrieveIntentData();

        // Set up our proxy
        proxy = ProxyBuilder.getProxy(getString(R.string.apikey), token);

        // Send a request to retrieve group information
        Call<Group> call = proxy.getGroupById(groupId);
        ProxyBuilder.callProxy(GroupInfoActivity.this, call, returnedGroupInfo -> groupInfoResponse(returnedGroupInfo));

        // Setup join group button
        Button joinGroupBtn = (Button) findViewById(R.id.join_group_btn);
        joinGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        // Setup leave group button
        Button leaveGroupBtn = (Button) findViewById(R.id.leave_group_btn);
        leaveGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
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
