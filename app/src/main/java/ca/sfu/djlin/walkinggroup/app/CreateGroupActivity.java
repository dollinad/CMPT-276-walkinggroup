package ca.sfu.djlin.walkinggroup.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

    private WGServerProxy proxy;
    LatLng latLng;
    private String token;
    Long id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        Intent intent=getIntent();
        token=intent.getStringExtra("token");

        proxy = ProxyBuilder.getProxy(getString(R.string.apikey),token);
        setup_create();
        setupbtn_back();

    }

    private void setupbtn_back() {
        Button btn_back=findViewById(R.id.group_btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void setup_create() {
        EditText editText=findViewById(R.id.group_name);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String name=editText.getText().toString();
                Button btn_confirm=findViewById(R.id.group_btn_yes);
                btn_confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent=getIntent();
                        latLng=new LatLng(intent.getDoubleExtra("lag",0),intent.getDoubleExtra("lng",0));
                        Group group=new Group();
                        group.setGroupDescription(name);
                        List<Double> lat=new ArrayList();
                        lat.add(latLng.latitude);
                        List<Double> lng=new ArrayList();
                        lng.add(latLng.longitude);
                        group.setRouteLatArray(lat);
                        group.setRouteLngArray(lng);
                        String email=intent.getStringExtra("email");
                        System.out.println(email);
                        System.out.println("start call");
                        Call<User> calleruser= proxy.getUserByEmail(email);
                        ProxyBuilder.callProxy(CreateGroupActivity.this,calleruser, returnuser -> createResponse(returnuser));
                        System.out.println("end call");
                       // group.setLeader(id);
                        Intent intent_2=new Intent();
                        intent_2.putExtra("groupName",name);

                        Call<Group> caller = proxy.createGroup(group);

                        ProxyBuilder.callProxy(CreateGroupActivity.this, caller, returnedUser -> createGroupResponse(returnedUser));

                        Toast.makeText(CreateGroupActivity.this,"group created",Toast.LENGTH_SHORT).show();
                        setResult(Activity.RESULT_OK, intent_2);
                        finish();;
                    }
                });

            }
        });

    }

    private void createResponse(User returnuser){
        System.out.println("start response");
        Toast.makeText(CreateGroupActivity.this,"Got users! See logcat.",Toast.LENGTH_LONG).show();
       returnuser.toString();
        System.out.println("end response");
       id=returnuser.getId();
       System.out.println("the id is "+id);
    }
    private void createGroupResponse(Group group) {
        //notifyUserViaLogAndToast("Server replied with user: " + user.toString());

        // Returned information
        Long groupId = group.getId();
        String groupName = group.getGroupDescription();
    }
    public static Intent makeintent(Context context){
        Intent intent =new Intent(context, CreateGroupActivity.class);
        return intent;
    }
    public static String getresult(Intent intent){
        return intent.getStringExtra("groupName");

    }

}
