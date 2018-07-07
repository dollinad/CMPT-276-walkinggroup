package ca.sfu.djlin.walkinggroup.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import ca.sfu.djlin.walkinggroup.R;
import ca.sfu.djlin.walkinggroup.dataobjects.Group;
import ca.sfu.djlin.walkinggroup.model.User;
import ca.sfu.djlin.walkinggroup.proxy.ProxyBuilder;
import ca.sfu.djlin.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;


public class CreateGroupActivity extends AppCompatActivity {

    // Constants
    public static final String TAG = "CreateGroup";
    private static final int GET_MARKER_CODE = 9999;

    private WGServerProxy proxy;
    private LatLng intendedLatLng;
    private LatLng meetingMarkerLatLng;

    private List<Double> latList = new ArrayList();
    private List<Double> lngList = new ArrayList();

    private String token;
    private Long currentUserId;

    private Boolean retrieveMarkerLocationFlag = true;

    private Group group = new Group();
    Long id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        // Retrieve data from intent
        Intent intent = getIntent();
        token = intent.getStringExtra("token");

        // Store current group information
        intendedLatLng = new LatLng(intent.getDoubleExtra("lat",0),intent.getDoubleExtra("lng",0));

        // Add the intended group location to lists
        latList.add(intendedLatLng.latitude);
        lngList.add(intendedLatLng.longitude);
        group.setRouteLatArray(latList);
        group.setRouteLngArray(lngList);

        // Get current user id
        SharedPreferences preferences = CreateGroupActivity.this.getSharedPreferences("User Session", MODE_PRIVATE);
        currentUserId = preferences.getLong("User Id", 0);

        // Set up proxy
        proxy = ProxyBuilder.getProxy(getString(R.string.apikey),token);

        // Buttons
        setupCreateButton();
        setupBackButton();
        setupAddMeetingLocationButton();
    }

    private void setupAddMeetingLocationButton() {
        Button addMeetingLocationButton = findViewById(R.id.add_meeting_btn);
        addMeetingLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // When user clicks on button, launch an activity for intent. We want to find out where the user clicked on the map
                Intent intent = MapActivity.launchIntentMapForMarker(CreateGroupActivity.this);
                intent.putExtra("Retrieve Marker", retrieveMarkerLocationFlag);
                startActivityForResult(intent, GET_MARKER_CODE);

                // Once activity intent finishes, return the coordinates back to us and store this information
            }
        });
    }

    private void setupBackButton() {
        Button backButton = findViewById(R.id.group_btn_cancel);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void setupCreateButton() {
        EditText editText = findViewById(R.id.group_description_input);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String name = editText.getText().toString();
                Button confirmButton = findViewById(R.id.confirm_btn);
                confirmButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        group.setGroupDescription(name);

                        // Add leader user to newly created group
                        User leader = new User();
                        leader.setId(currentUserId);
                        group.setLeader(leader);

                        Call<Group> caller = proxy.createGroup(group);
                        ProxyBuilder.callProxy(CreateGroupActivity.this, caller, returnedGroup -> createGroupResponse(returnedGroup));
                    }
                });
            }
        });
    }


    private void createGroupResponse(Group group) {
        Log.d(TAG, "createGroupResponse: ");

        // Define variables to store
        Long groupId = group.getId();
        String groupDescription = group.getGroupDescription();

        // Store information in intent
        Intent intent = new Intent();
        intent.putExtra("groupId", groupId);
        intent.putExtra("groupName", groupDescription);

        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    public static Intent makeintent(Context context){
        Intent intent = new Intent(context, CreateGroupActivity.class);
        return intent;
    }
    public static String getresult(Intent intent){
        return intent.getStringExtra("groupName");
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case GET_MARKER_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    // Store the retrieved map marker locations
                    Intent intent = getIntent();
                    meetingMarkerLatLng =  new LatLng(intent.getDoubleExtra("lat",0),intent.getDoubleExtra("lng",0));

                    Log.d(TAG, "Latitude: " + meetingMarkerLatLng.latitude);
                    Log.d(TAG, "Longitude: " + meetingMarkerLatLng.longitude);

                    // Add lat and long to list
                    latList.add(meetingMarkerLatLng.latitude);
                    lngList.add(meetingMarkerLatLng.longitude);

                    // Update group information
                    group.setRouteLatArray(latList);
                    group.setRouteLngArray(lngList);
                }
        }
    }

}
