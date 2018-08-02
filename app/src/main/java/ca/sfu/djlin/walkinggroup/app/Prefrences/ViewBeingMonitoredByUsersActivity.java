package ca.sfu.djlin.walkinggroup.app.Prefrences;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import ca.sfu.djlin.walkinggroup.R;
import ca.sfu.djlin.walkinggroup.model.Session;
import ca.sfu.djlin.walkinggroup.model.User;
import ca.sfu.djlin.walkinggroup.proxy.ProxyBuilder;
import ca.sfu.djlin.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

public class ViewBeingMonitoredByUsersActivity extends AppCompatActivity {
    WGServerProxy proxy;
    String token;
    User getInfoOfUser;
    Long getInfoOfUserId;
    List<User> beingMonitoredBy;
    ArrayAdapter<User> adapter;
    Session session;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_being_monitored_by_info);
        Log.i("PLEASEWORK2", "hhhhh");
        Intent intent = getIntent();
        //token=intent.getStringExtra("token");
        getInfoOfUserId = intent.getLongExtra("userId", 0);

        session = Session.getSession(getApplicationContext());
        proxy = session.getProxy();
        // proxy = ProxyBuilder.getProxy(getString(R.string.apikey), token);

        Call<User> caller = proxy.getUserById(getInfoOfUserId);
        ProxyBuilder.callProxy(ViewBeingMonitoredByUsersActivity.this, caller, returnedUser -> UserReturned(returnedUser));

    }

    private void UserReturned(User returnedUser) {
        getInfoOfUser = returnedUser;
        Toast.makeText(getApplicationContext(), returnedUser.getName(), Toast.LENGTH_SHORT).show();
        TextView getInfoOfUserName = findViewById(R.id.get_user_info_of_user_name);
        getInfoOfUserName.setText(returnedUser.getName());
        Call<List<User>> call = proxy.getMonitoredByUsers(getInfoOfUserId);
        ProxyBuilder.callProxy(ViewBeingMonitoredByUsersActivity.this, call, returnedList -> ListReturned(returnedList));

    }

    private void ListReturned(List<User> returnedList) {
        beingMonitoredBy = returnedList;
        adapter = new myListAdapter();
        ListView listView = findViewById(R.id.users_monitoring_current);
        listView.setAdapter(adapter);
    }

    public static Intent launchIntentBeingMonitored(Context context) {
        Intent intentViewBeingMonitoredBy = new Intent(context, ViewBeingMonitoredByUsersActivity.class);
        return intentViewBeingMonitoredBy;
    }

    private class myListAdapter extends ArrayAdapter<User> {
        public myListAdapter() {
            super(ViewBeingMonitoredByUsersActivity.this, R.layout.layout_monitoring_list, beingMonitoredBy);
        }

        View itemView;

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.layout_being_monitored_indepth, parent, false);
            }
            User monitoredBy = beingMonitoredBy.get(position);

            TextView name = itemView.findViewById(R.id.in_depthlist_name);
            TextView email = itemView.findViewById(R.id.in_depthlist_email);
            TextView cellPhone = itemView.findViewById(R.id.in_depth_cellphone);
            TextView homePhone = itemView.findViewById(R.id.in_depth_home_phone);
            TextView Address = itemView.findViewById(R.id.in_depth_address);
            // Make a call to collect the name and email of the user
            Call<User> call = proxy.getUserById(monitoredBy.getId());
            ProxyBuilder.callProxy(ViewBeingMonitoredByUsersActivity.this, call, returnUser -> respond(returnUser, name, email, cellPhone, homePhone, Address));

            return itemView;
        }

        private void respond(User returnUser, TextView name, TextView email, TextView cellPhone, TextView homePhone, TextView Address) {
            // Update the item view with user information
            name.setText(returnUser.getName());
            email.setText("Email: " + returnUser.getEmail());
            cellPhone.setText("Cell Phone: " + returnUser.getCellPhone() + "");
            homePhone.setText("Home Phone: " + returnUser.getHomePhone() + "");
            Address.setText("Address: " + returnUser.getAddress());

        }
    }
}
