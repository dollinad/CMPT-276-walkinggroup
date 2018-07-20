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
import android.widget.Toast;

import java.util.List;

import ca.sfu.djlin.walkinggroup.R;
import ca.sfu.djlin.walkinggroup.dataobjects.Group;
import ca.sfu.djlin.walkinggroup.model.Session;
import ca.sfu.djlin.walkinggroup.model.User;
import ca.sfu.djlin.walkinggroup.proxy.ProxyBuilder;
import ca.sfu.djlin.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

public class ViewGrpupActivity extends AppCompatActivity {
    public static final int REQUEST_CODE_GETDATA = 1023;

    private WGServerProxy proxy;
    String currentUserToken;
    String currentUserEmail;
    User currentUser;
    Long groupId;
    User groupLeader;
    Session session;

    ArrayAdapter<Group> adapter;

    ArrayAdapter<Group> leaderadapter;

    List<Group> currentMemberGroups;
    List<Group> curretLeaderGroups;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewgroups);

        Button button=findViewById(R.id.id_creteGroup);
        button.setVisibility(View.INVISIBLE);


        //Get intent
        //Intent intent = getIntent();
        //currentUserToken = intent.getStringExtra("Token");
        //currentUserEmail = intent.getStringExtra("Email");

        //Build Proxy
        //proxy = ProxyBuilder.getProxy(getString(R.string.apikey), currentUserToken);
        session=Session.getSession(getApplicationContext());
        proxy=session.getProxy();
        currentUser=session.getUser();
        currentUserEmail=currentUser.getEmail();


        // Get current user information
        Call<User> caller = proxy.getUserByEmail(currentUserEmail);
        ProxyBuilder.callProxy(ViewGrpupActivity.this, caller, returnedUser -> responseCurrent(returnedUser));
        registerClickCallbackMember();
        registerClickCallbackLeader();

    }

    //response for current user
    private void responseCurrent(User user) {
        // Store retrieved user into currentUser
        currentUser = user;
        if(user.getMemberOfGroups().size()==0 && user.getLeadsGroups().size()==0){
            TextView textView=findViewById(R.id.currentgroups);
            textView.setText("You are currently not a part of any group!");
            Button button=findViewById(R.id.id_creteGroup);
            button.setVisibility(View.VISIBLE);
            Toast.makeText(getApplicationContext(), "KK", Toast.LENGTH_SHORT).show();
        }
        currentMemberGroups=user.getMemberOfGroups();
        curretLeaderGroups=user.getLeadsGroups();
        int j;
        adapter = new myListMemberAdapter();
        ListView list = findViewById(R.id.id_currentGrps);
        list.setAdapter(adapter);
        //j=1;
        leaderadapter= new myListLeaderAdapter();
        ListView listLeader=findViewById(R.id.currentlyLeaderOf);
        listLeader.setAdapter(leaderadapter);
    }


    //Adapter for the Member of list
    private class myListMemberAdapter extends ArrayAdapter<Group> {
        public myListMemberAdapter() {
                super(ViewGrpupActivity.this, R.layout.layout_viewgroups, currentMemberGroups);
        }

        View itemView;
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
           itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.layout_viewgroups, parent, false);
            }

            //list layout (des= description of the group and leader)
            TextView des = itemView.findViewById(R.id.idGrpDes);
            TextView leader = itemView.findViewById(R.id.idGrpLead);

            //proxy = ProxyBuilder.getProxy(getString(R.string.apikey), currentUserToken);
            //checking if the current user is currently in any groups
            Log.i("KIKIKIKIKI", currentUser.getMemberOfGroups().size()+"");
            Toast.makeText(getApplicationContext(), currentUser.getMemberOfGroups().size()+"HYHYH", Toast.LENGTH_SHORT).show();
            if(currentUser.getMemberOfGroups().size()!=0) {

                Call<Group> call = proxy.getGroupById(currentUser.getMemberOfGroups().get(position).getId());
                ProxyBuilder.callProxy(ViewGrpupActivity.this, call, returnedGroup -> Groupreturned(returnedGroup, des, leader));
            }

            return itemView;
        }

        //response function for if the user is leading any groups
        private void Groupreturned(Group returnedGroup, TextView des, TextView leader) {
            des.setText(returnedGroup.getGroupDescription());
            leader.setText(returnedGroup.getLeader().getName());
            groupId=returnedGroup.getId();

        }
    }

    //Adapter for the Member of list
    private class myListLeaderAdapter extends ArrayAdapter<Group> {
        public myListLeaderAdapter() {
            super(ViewGrpupActivity.this, R.layout.layout_viewgroups, curretLeaderGroups);
        }

        View itemView;
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.layout_viewgroups, parent, false);
            }

            //list layout (des= description of the group and leader)
            TextView des = itemView.findViewById(R.id.idGrpDes);
            TextView leader = itemView.findViewById(R.id.idGrpLead);

            //proxy = ProxyBuilder.getProxy(getString(R.string.apikey), currentUserToken);

            //checking if the current group is leading any groups
            if(currentUser.getLeadsGroups().size()!=0) {
                Call<Group> caller = proxy.getGroupById(currentUser.getLeadsGroups().get(position).getId());
                ProxyBuilder.callProxy(ViewGrpupActivity.this, caller, returnedGroup -> GroupreturnedLead(returnedGroup, des, leader));
            }
            return itemView;
        }

        //response function for if the user is a part of any group
        private void GroupreturnedLead(Group returnedGroup, TextView des, TextView leader) {
            des.setText(returnedGroup.getGroupDescription());
            leader.setText(returnedGroup.getLeader().getName());

            groupId=returnedGroup.getId();
        }

    }



    private void getUserById(Long Id){
        Call<User> caller = proxy.getUserById(Id);
        ProxyBuilder.callProxy(ViewGrpupActivity.this,caller,returnedUser -> userResponse(returnedUser));
    }

    private void userResponse(User returnedUser) {
        groupLeader=returnedUser;
    }

    private void registerClickCallbackMember() {
        ListView listView = findViewById(R.id.id_currentGrps);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Build Proxy
                //proxy = ProxyBuilder.getProxy(getString(R.string.apikey), currentUserToken);
                Call<Group> caller = proxy.getGroupById(currentUser.getMemberOfGroups().get(position).getId());
                ProxyBuilder.callProxy(ViewGrpupActivity.this, caller, returnedGroup -> GroupreturnedPass(returnedGroup));
            }
        });

    }

    private void registerClickCallbackLeader() {
        ListView listViewLeader = findViewById(R.id.currentlyLeaderOf);
        listViewLeader.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Build Proxy
                //proxy = ProxyBuilder.getProxy(getString(R.string.apikey), currentUserToken);
                Call<Group> caller = proxy.getGroupById(currentUser.getLeadsGroups().get(position).getId());
                ProxyBuilder.callProxy(ViewGrpupActivity.this, caller, returnedGroup -> GroupreturnedPass(returnedGroup));
            }
        });
    }

    private void GroupreturnedPass(Group returnedGroup) {
        Intent intent = GroupInfoActivity.launchGroupInfoIntent(ViewGrpupActivity.this);
        intent.putExtra("token",currentUserToken);
        intent.putExtra("email",currentUserEmail);
        intent.putExtra("groupId",returnedGroup.getId());
        startActivityForResult(intent,REQUEST_CODE_GETDATA);
        intent.removeExtra("token");
        intent.removeExtra("email");
        intent.removeExtra("groupId");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_GETDATA:
                if(resultCode == Activity.RESULT_OK)
                {
                    Long groupID_pass=GroupInfoActivity.getResultGroupId(data);
                    Intent intent=new Intent();
                    intent.putExtra("eventGroupId",groupID_pass);
                    setResult(Activity.RESULT_OK,intent);
                    finish();
                }
                else
                {
                    Log.i("My app","Activity cancelled.");
                }
        }
    }
    public  static Long getResultGroupId(Intent intent )
    {
        return intent.getLongExtra("eventGroupId",0);
    }
    public static Intent launchIntentViewGroups(Context context) {
        Intent intentViewGroups = new Intent(context, ViewGrpupActivity.class);
        return intentViewGroups;
    }
}
