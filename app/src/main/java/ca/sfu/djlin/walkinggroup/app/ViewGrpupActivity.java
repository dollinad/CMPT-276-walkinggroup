package ca.sfu.djlin.walkinggroup.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import ca.sfu.djlin.walkinggroup.R;
import ca.sfu.djlin.walkinggroup.dataobjects.Group;
import ca.sfu.djlin.walkinggroup.model.User;
import ca.sfu.djlin.walkinggroup.proxy.ProxyBuilder;
import ca.sfu.djlin.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

public class ViewGrpupActivity extends AppCompatActivity {
    private WGServerProxy proxy;
    String currentUserToken;
    String currentUserEmail;
    User currentUser;

    ArrayAdapter<Group> adapter;

    List<Group> currentGroups;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewgroups);

        Button button=findViewById(R.id.id_creteGroup);
        button.setVisibility(View.INVISIBLE);


        //Get intent
        Intent intent = getIntent();
        currentUserToken = intent.getStringExtra("Token");
        currentUserEmail = intent.getStringExtra("Email");

        //Build Proxy
        proxy = ProxyBuilder.getProxy(getString(R.string.apikey), currentUserToken);


        // Get current user information
        Call<User> caller = proxy.getUserByEmail(currentUserEmail);
        ProxyBuilder.callProxy(ViewGrpupActivity.this, caller, returnedUser -> responseCurrent(returnedUser));

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
        currentGroups=user.getMemberOfGroups();
        for(int i=0; i<user.getLeadsGroups().size(); i++){
            currentGroups.add(user.getLeadsGroups().get(i));
        }

        adapter = new myListAdapter();
        ListView list = findViewById(R.id.id_currentGrps);
        list.setAdapter(adapter);
    }


    //Adapetr for the list
    private class myListAdapter extends ArrayAdapter<Group> {
        public myListAdapter() {
            super(ViewGrpupActivity.this, R.layout.layout_viewgroups, currentGroups);
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

            proxy = ProxyBuilder.getProxy(getString(R.string.apikey), currentUserToken);

            //checking if the current user is currently in any groups
            if(currentUser.getMemberOfGroups().size()!=0) {
                Call<Group> call = proxy.getGroupById(currentUser.getMemberOfGroups().get(position).getId());
                ProxyBuilder.callProxy(ViewGrpupActivity.this, call, returnedGroup -> Groupreturned(returnedGroup, des, leader));
            }

            //checking if the current group is leading any groups
           if(currentUser.getLeadsGroups().size()==0) {
                Call<Group> caller = proxy.getGroupById(currentUser.getLeadsGroups().get(position).getId());
                ProxyBuilder.callProxy(ViewGrpupActivity.this, caller, returnedGroup -> GroupreturnedLead(returnedGroup, des, leader));
            }
            return itemView;
        }

        //response function for if the user is a part of any group
        private void GroupreturnedLead(Group returnedGroup, TextView des, TextView leader) {
            des.setText(returnedGroup.getGroupDescription());
            leader.setText(returnedGroup.getLeader().getName());
        }

        //response function for if the user is leading any groups
        private void Groupreturned(Group returnedGroup, TextView des, TextView leader) {
            des.setText(returnedGroup.getGroupDescription());
            leader.setText(returnedGroup.getLeader().getName());

        }
    }

    public static Intent launchIntentViewGroups(Context context) {
        Intent intentViewGroups = new Intent(context, ViewGrpupActivity.class);
        return intentViewGroups;
    }
}
