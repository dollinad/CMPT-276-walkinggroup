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

    private WGServerProxy proxy;
    private LatLng intendedLatLng;
    private String token;
    private Long currentUserId;

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

        // Get current user id
        SharedPreferences preferences = CreateGroupActivity.this.getSharedPreferences("User Session", MODE_PRIVATE);
        currentUserId = preferences.getLong("User Id", 0);

        // Set up proxy
        proxy = ProxyBuilder.getProxy(getString(R.string.apikey),token);

        // Set up create group button
        setup_create();
        setupbtn_back();
    }

    private void setupbtn_back() {
        Button btn_back=findViewById(R.id.group_btn_cancel);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void setup_create() {
        EditText editText=findViewById(R.id.group_description_input);
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
                Button btn_confirm = findViewById(R.id.confirm_btn);
                btn_confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        group.setGroupDescription(name);
                        List<Double> lat = new ArrayList();
                        lat.add(intendedLatLng.latitude);
                        List<Double> lng = new ArrayList();
                        lng.add(intendedLatLng.longitude);
                        group.setRouteLatArray(lat);
                        group.setRouteLngArray(lng);

                        // Add leader user to newly created group
                        User leader = new User();
                        leader.setId(currentUserId);
                        group.setLeader(leader);

                        // Call<User> currentUser = proxy.getUserByEmail(email);
                        // ProxyBuilder.callProxy(CreateGroupActivity.this,currentUser, returnedUser -> getCurrentUserResponse(returnedUser));
                        // System.out.println("end call");
                        // group.setLeader(id);

                        Call<Group> caller = proxy.createGroup(group);
                        ProxyBuilder.callProxy(CreateGroupActivity.this, caller, returnedGroup -> createGroupResponse(returnedGroup));
                    }
                });
            }
        });
    }

    /*
    private void getCurrentUserResponse(User returnedUser){
        System.out.println("start response");
        Toast.makeText(CreateGroupActivity.this,"Got users! See logcat.",Toast.LENGTH_LONG).show();
        returnedUser.toString();
        System.out.println("end response");
        id=returnedUser.getId();
        System.out.println("the id is "+id);
    } */

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
        Intent intent =new Intent(context, CreateGroupActivity.class);
        return intent;
    }
    public static String getresult(Intent intent){
        return intent.getStringExtra("groupName");
    }

}
