package ca.sfu.djlin.walkinggroup.app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import ca.sfu.djlin.walkinggroup.R;
import ca.sfu.djlin.walkinggroup.model.User;
import ca.sfu.djlin.walkinggroup.proxy.ProxyBuilder;
import ca.sfu.djlin.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

public class PreferencesActivity extends AppCompatActivity {

    private WGServerProxy proxy;
    String UserToAddEmail;
    String UserToken;
    ArrayAdapter<User> adapter;
    ArrayAdapter<User> adapterMonitored;
    User CurrentUser;
    List<User> monitorsUsers;
    User ToADD;
    String nametobeadded;
    String CurrentUserEmail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoring_config);

        //get intent
        Intent intent=getIntent();
        UserToken=intent.getStringExtra("token");
        CurrentUserEmail=intent.getStringExtra("email");

        //proxy
        proxy=ProxyBuilder.getProxy(getString(R.string.apikey), UserToken);
        Call<User> caller=proxy.getUserByEmail(CurrentUserEmail);
        ProxyBuilder.callProxy(PreferencesActivity.this, caller, returnedUser -> responseCurrent(returnedUser));

        //function to get user from server
        getUser();

        //remove from monitors
        deleteMonitors();
        
        //remove from monitored By
        deleteMonitoredBy();


    }

    private void deleteMonitoredBy() {
        ListView list=findViewById(R.id.monitoredList);
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                User toRemove=CurrentUser.getMonitoredByUsers().get(position);
                new AlertDialog.Builder(PreferencesActivity.this)
                        .setMessage("Are you sure you wnat to remove this user?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                proxy=ProxyBuilder.getProxy(getString(R.string.apikey), UserToken);
                                Call<Void> caller=proxy.removeFromMonitoredByUsers(CurrentUser.getId(), toRemove.getId());
                                ProxyBuilder.callProxy(PreferencesActivity.this, caller, returnNothing-> responseMonitoredNothing(returnNothing, position));
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                        startActivity(getIntent());
                    }
                }).show();
                return false;
            }
        });
    }

    private void responseMonitoredNothing(Void returnNothing, int position){
        CurrentUser.getMonitoredByUsers().remove(position);
        adapterMonitored.notifyDataSetChanged();
        monitoredBy();
    }

    private void deleteMonitors() {
        ListView list=findViewById(R.id.monitoringList);
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                User userToRemove=monitorsUsers.get(position);
                new AlertDialog.Builder(PreferencesActivity.this)
                        .setMessage("Are you sure you want to remove this User?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                proxy=ProxyBuilder.getProxy(getString(R.string.apikey), UserToken);
                                Call<Void> caller=proxy.removeFromMonitorsUsers(CurrentUser.getId(), userToRemove.getId());
                                ProxyBuilder.callProxy(PreferencesActivity.this, caller, returnNothing-> response(returnNothing, position));
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                        startActivity(getIntent());
                    }
                }).show();
                return false;
            }
        });
    }

    private void response(Void returnedNothing, int position) {
       monitorsUsers.remove(position);
       CurrentUser.setMonitoredByUsers(monitorsUsers);
       adapter.notifyDataSetChanged();
       Refresh();
       finish();
       startActivity(getIntent());
    }

    private void responseCurrent(User user) {
        CurrentUser=user;
        monitoredBy();
        Toast.makeText(getApplicationContext(), "Current User"+user.getName(),Toast.LENGTH_SHORT).show();
        Log.i("BEFORE", CurrentUser.getName());
        Refresh();
    }

    private void Refresh() {
        Log.i("REACHED", "nnn");
        proxy=ProxyBuilder.getProxy(getString(R.string.apikey), UserToken);;
        Call<List<User>> call=proxy.getMonitorsUsers(CurrentUser.getId());
        ProxyBuilder.callProxy(PreferencesActivity.this, call, returnedList -> response(returnedList));
    }

    private void response(List<User> list) {
        //notifyUserViaLogAndToast("Server replied with user: " + user.toString());
        monitorsUsers=list;
        CurrentUser.setMonitorsUsers(monitorsUsers);
        adapter=new MyListAdapter();
        ListView list2=findViewById(R.id.monitoringList);
        list2.setAdapter(adapter);
    }


    private void getUser() {
        EditText userEmail=findViewById(R.id.PreferecnesUserEmail);
        userEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                EditText userEmail=findViewById(R.id.PreferecnesUserEmail);
                UserToAddEmail=userEmail.getText().toString();
            }
        });

        Button AddButton=findViewById(R.id.PreferencesAdd);
        AddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //proxy
                proxy=ProxyBuilder.getProxy(getString(R.string.apikey), UserToken);
                Call<User> caller=proxy.getUserByEmail(UserToAddEmail);
                ProxyBuilder.callProxy(PreferencesActivity.this, caller, returnedUser -> response(returnedUser));
            }
        });
    }

    private void response(User user) {
        notifyUserViaLogAndToast("Server replied with user: " + user.toString());
        CurrentUser.setMonitorsUsers(CurrentUser.getMonitorsUsers());
        Call<List<User>> call=proxy.addToMonitorsUsers(CurrentUser.getId(), user);
        ProxyBuilder.callProxy(PreferencesActivity.this, call, returnedList->response(returnedList));

        Call<List<User>> caller=proxy.addToMonitoredByUsers(user.getId(),CurrentUser);
        ProxyBuilder.callProxy(PreferencesActivity.this, caller, returnedList-> responseMonitored(returnedList));
        adapter.notifyDataSetChanged();
        Refresh();
        //UserToAddEmail = user.getEmail();
    }

    private void responseMonitored(List<User> returnedList) {

    }


    // Put message up in toast and logcat
    // -----------------------------------------------------------------------------------------
    private void notifyUserViaLogAndToast(String message) {
        Log.i("PPP", message);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }




    public static Intent launchIntentPreferences(Context context) {
        Intent intentPreferences = new Intent(context, PreferencesActivity.class);
        return intentPreferences;
    }

    private class MyListAdapter extends ArrayAdapter<User> {
        public MyListAdapter(){
            super(PreferencesActivity.this, R.layout.layout_monitoring_list, CurrentUser.getMonitorsUsers());
        }
        View itemView;
        @NonNull
        @Override
       public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            itemView=convertView;
            if(itemView==null){
                itemView=getLayoutInflater().inflate(R.layout.layout_monitoring_list, parent, false);
            }
            User Current=CurrentUser.getMonitorsUsers().get(position);
            Toast.makeText(getApplicationContext(), Current.getId()+"", Toast.LENGTH_SHORT).show();
            TextView name=itemView.findViewById(R.id.LayoutName);
            TextView email=itemView.findViewById(R.id.LayoutEmail);
            Call<User> call=proxy.getUserById(Current.getId());
            ProxyBuilder.callProxy(PreferencesActivity.this, call, returnUser -> respond(returnUser, name, email));

            return itemView;
        }

        private void respond(User returnUser, TextView name, TextView email) {
             //name=itemView.findViewById(R.id.LayoutName);
            name.setText(returnUser.getName());
           //email=itemView.findViewById(R.id.LayoutEmail);
            email.setText(returnUser.getEmail());
        }
    }

    private class MyListAdapterMonitored extends ArrayAdapter<User> {
        public MyListAdapterMonitored(){
            super(PreferencesActivity.this, R.layout.layout_monitoring_list, CurrentUser.getMonitoredByUsers());
        }
        View itemView;

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            itemView=convertView;
            if(itemView==null){
                itemView=getLayoutInflater().inflate(R.layout.layout_monitoring_list, parent, false);
            }
            User MonitoredBy=CurrentUser.getMonitoredByUsers().get(position);
            TextView name=itemView.findViewById(R.id.LayoutName);
            TextView email=itemView.findViewById(R.id.LayoutEmail);
            Call<User> call=proxy.getUserById(MonitoredBy.getId());
            ProxyBuilder.callProxy(PreferencesActivity.this, call, returnUser ->respond(returnUser, name, email));
            return itemView;
        }
        private void respond(User returnUser, TextView name, TextView email) {
            //name=itemView.findViewById(R.id.LayoutName);
            name.setText(returnUser.getName());
            //email=itemView.findViewById(R.id.LayoutEmail);
            email.setText(returnUser.getEmail());
        }
    }

    private void monitoredBy() {
        proxy=ProxyBuilder.getProxy(getString(R.string.apikey), UserToken);
        //Toast.makeText(getApplicationContext(),CurrentUser.getId()+"", Toast.LENGTH_SHORT).show();
        Call<List<User>> call=proxy.getMonitoredByUsers(CurrentUser.getId());
        ProxyBuilder.callProxy(PreferencesActivity.this, call, returnedListMon-> responseMonitoredBy(returnedListMon));
        adapterMonitored=new MyListAdapterMonitored();
        ListView list=findViewById(R.id.monitoredList);
        list.setAdapter(adapterMonitored);
    }

    private void responseMonitoredBy(List<User> returnedList) {
        CurrentUser.setMonitoredByUsers(returnedList);

    }



}
