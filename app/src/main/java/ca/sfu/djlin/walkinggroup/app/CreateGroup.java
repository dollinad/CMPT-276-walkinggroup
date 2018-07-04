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

import ca.sfu.djlin.walkinggroup.R;
import ca.sfu.djlin.walkinggroup.dataobjects.Group;
import ca.sfu.djlin.walkinggroup.proxy.ProxyBuilder;
import ca.sfu.djlin.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;


public class CreateGroup extends AppCompatActivity {

    private WGServerProxy proxy;
    LatLng latLng;
    private String token;

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
                        group.setName(name);
                        group.setMarker(latLng);
                        Intent intent_2=new Intent();
                        intent_2.putExtra("groupName",name);

                        Call<Group> caller = proxy.createGroup(group);
                        ProxyBuilder.callProxy(CreateGroup.this, caller, returnedUser -> createGroupResponse(returnedUser));

                        Toast.makeText(CreateGroup.this,"group created",Toast.LENGTH_SHORT).show();
                        /*Intent intent=new Intent(CreateGroup.this,MainActivity.class);
                        startActivity(intent);
                        */
                        setResult(Activity.RESULT_OK, intent_2);
                        finish();;
                    }
                });

            }
        });

    }


    private void createGroupResponse(Group group) {
        //notifyUserViaLogAndToast("Server replied with user: " + user.toString());

        // Returned information
        Long groupId = group.getId();
        String groupName = group.getName();
    }
    public static Intent makeintent(Context context){
        Intent intent =new Intent(context, CreateGroup.class);
        return intent;
    }
    public static String getresult(Intent intent){
        return intent.getStringExtra("groupName");

    }

}
