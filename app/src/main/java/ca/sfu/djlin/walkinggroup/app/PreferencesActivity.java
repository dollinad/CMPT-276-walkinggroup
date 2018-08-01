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
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import ca.sfu.djlin.walkinggroup.R;
import ca.sfu.djlin.walkinggroup.Utilities;
import ca.sfu.djlin.walkinggroup.model.Session;
import ca.sfu.djlin.walkinggroup.model.User;
import ca.sfu.djlin.walkinggroup.proxy.ProxyBuilder;
import ca.sfu.djlin.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

public class PreferencesActivity extends AppCompatActivity {
    int counter = 0;

    public static final String TAG = "PreferencesActivity";
    private WGServerProxy proxy;
    String userToAddEmail;
    String userToBeMonitoredBy;
    String currentUserToken;

    ArrayAdapter<User> adapter;
    ArrayAdapter<User> adapterMonitored;

    User currentUser;
    List<User> monitorsUsers;
    List<User> monitoredByUsers;

    String currentUserEmail;
    Session session;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoring_config);

        // Get intent
        //Intent intent = getIntent();
        //currentUserToken = intent.getStringExtra("Token");
        //currentUserEmail = intent.getStringExtra("Email");

        // Build proxy
        //proxy = ProxyBuilder.getProxy(getString(R.string.apikey), currentUserToken);
        session=Session.getSession(getApplicationContext());
        proxy=session.getProxy();
        currentUser=session.getUser();
        Log.d(TAG, "The current user session is: " + currentUser.toString());
        currentUserEmail=currentUser.getEmail();
        Log.d(TAG, "The current User Email is: " + currentUserEmail);


        // Get current user information
        // Call<User> caller = proxy.getUserByEmail(currentUserEmail);
        // ProxyBuilder.callProxy(PreferencesActivity.this, caller, returnedUser -> responseCurrent(returnedUser));

        // Set up input and button to add monitored user
        setupAddMonitoringUser();

        // Set up input and button to add user to be monitored by
        setupAddMonitoredUser();

        //remove from monitors
        deleteMonitors();

        //remove from monitored By
        deleteMonitoredBy();

        EditEmergencyInfoFormonitoringUser();

        ViewMyChildsGroups();
    }



    private void deleteMonitors() {
        ImageView h=findViewById(R.id.remove_user);
        ListView list = findViewById(R.id.monitoring_list);
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                User userToRemove = monitorsUsers.get(position);
                new AlertDialog.Builder(PreferencesActivity.this)
                        .setMessage("Are you sure you want to remove this User?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //proxy = ProxyBuilder.getProxy(getString(R.string.apikey), currentUserToken);
                                proxy=session.getProxy();
                                Call<Void> caller = proxy.removeFromMonitorsUsers(currentUser.getId(), userToRemove.getId());
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
        currentUser.setMonitoredByUsers(monitorsUsers);
        adapter.notifyDataSetChanged();
        refresh();
    }

    private void deleteMonitoredBy() {
        ListView list = findViewById(R.id.monitored_by_list);
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                User toRemove = currentUser.getMonitoredByUsers().get(position);
                new AlertDialog.Builder(PreferencesActivity.this)
                        .setMessage("Are you sure you wnat to remove this user?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //proxy = ProxyBuilder.getProxy(getString(R.string.apikey), currentUserToken);
                                proxy=session.getProxy();
                                Call<Void> caller = proxy.removeFromMonitoredByUsers(currentUser.getId(), toRemove.getId());
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
        currentUser.getMonitoredByUsers().remove(position);
        adapterMonitored.notifyDataSetChanged();
        monitoredBy();
    }


    private void responseCurrent(User user) {
        // Store retrieved user into currentUser
        Log.d(TAG, "responseCurrent: " + user);
        currentUser = user;
        monitoredBy();
        refresh();
    }

    private void refresh() {
        // proxy = ProxyBuilder.getProxy(getString(R.string.apikey), currentUserToken);;
        // Call<List<User>> call = proxy.getMonitorsUsers(currentUser.getId());
        // ProxyBuilder.callProxy(PreferencesActivity.this, call, returnedList -> response(returnedList));
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
        if (adapterMonitored != null) {
            adapterMonitored.notifyDataSetChanged();
        }
    }

    private void response(List<User> list) {
        monitorsUsers = list;
        currentUser.setMonitorsUsers(monitorsUsers);

        // Build new adapter
        adapter = new myListAdapterMonitors();
        ListView monitoringList = findViewById(R.id.monitoring_list);
        monitoringList.setAdapter(adapter);
    }

    private void setupAddMonitoringUser() {
        EditText userEmail = findViewById(R.id.add_monitored_user_input);
        userEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                EditText userEmail = findViewById(R.id.add_monitored_user_input);
                userToAddEmail = userEmail.getText().toString();
            }
        });

        // Hide keyboard when is done typing
        userEmail.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {
                    Utilities.hideKeyboard(PreferencesActivity.this);
                }
                return false;
            }
        });

        // Hide keyboard on focus change
        userEmail.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    Utilities.hideKeyboardFocus(PreferencesActivity.this, view);
                }
            }
        });

        Button AddButton = findViewById(R.id.add_monitored_user_button);
        AddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Build proxy
                //proxy = ProxyBuilder.getProxy(getString(R.string.apikey), currentUserToken);
                proxy=session.getProxy();
                Call<User> caller = proxy.getUserByEmail(userToAddEmail);
                ProxyBuilder.callProxy(PreferencesActivity.this, caller, returnedUser -> response(returnedUser));
            }
        });
    }

    private void setupAddMonitoredUser() {
        EditText userEmail = findViewById(R.id.add_monitored_by_user_input);
        userEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                EditText userEmail = findViewById(R.id.add_monitored_by_user_input);
                userToBeMonitoredBy = userEmail.getText().toString();
            }
        });

        // Hide keyboard when is done typing
        userEmail.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {
                    Utilities.hideKeyboard(PreferencesActivity.this);
                }
                return false;
            }
        });

        // Hide keyboard on focus change
        userEmail.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    Utilities.hideKeyboardFocus(PreferencesActivity.this, view);
                }
            }
        });

        Button AddButton = findViewById(R.id.add_monitored_by_user_btn);
        AddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Build proxy
                //proxy = ProxyBuilder.getProxy(getString(R.string.apikey), currentUserToken);
                proxy=session.getProxy();
                Call<User> caller = proxy.getUserByEmail(userToBeMonitoredBy);
                ProxyBuilder.callProxy(PreferencesActivity.this, caller, returnedUser -> responseToBeMonitored(returnedUser));
            }
        });
    }

    private void responseToBeMonitored(User user) {
        // notifyUserViaLogAndToast("Server replied with user: " + user.toString());

        Call<List<User>> call = proxy.addToMonitoredByUsers(currentUser.getId(), user);
        ProxyBuilder.callProxy(PreferencesActivity.this, call, returnedList-> responseAddMonitoring(returnedList));

        Call<List<User>> called = proxy.addToMonitorsUsers(user.getId(), currentUser);
        ProxyBuilder.callProxy(PreferencesActivity.this, called, returnedList-> responseMonitored(returnedList));

        refresh();
    }

    private void responseToBeMonitored(List<User> list) {
        monitoredByUsers = list;
        currentUser.setMonitoredByUsers(monitoredByUsers);

        // Build new adapter
        adapterMonitored = new myListAdapterMonitored();
        ListView monitoredByList = findViewById(R.id.monitored_by_list);
        monitoredByList.setAdapter(adapterMonitored);
    }

    private void response(User user) {
        // notifyUserViaLogAndToast("Server replied with user: " + user.toString());

        currentUser.setMonitorsUsers(currentUser.getMonitorsUsers());
        Call<List<User>> call = proxy.addToMonitorsUsers(currentUser.getId(), user);
        ProxyBuilder.callProxy(PreferencesActivity.this, call, returnedList->addMonitoredUser(returnedList));

        // Call<List<User>> caller = proxy.addToMonitoredByUsers(user.getId(), currentUser);
        // ProxyBuilder.callProxy(PreferencesActivity.this, caller, returnedList-> responseMonitored(returnedList));
        // adapter.notifyDataSetChanged();

        refresh();
    }

    private void responseAddMonitoring(List<User> returnedList) {

    }

    private void addMonitoredUser(List<User> returnedList) {

    }

    private void responseMonitored(List<User> returnedList) {

    }

    private void notifyUserViaLogAndToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public static Intent launchIntentPreferences(Context context) {
        Intent intentPreferences = new Intent(context, PreferencesActivity.class);
        return intentPreferences;
    }

    private class myListAdapterMonitors extends ArrayAdapter<User> {
        public myListAdapterMonitors(){
            super(PreferencesActivity.this, R.layout.layout_pref_list, currentUser.getMonitorsUsers());
        }
        View itemView;

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            itemView = convertView;
            if(itemView == null){
                itemView = getLayoutInflater().inflate(R.layout.layout_pref_list, parent, false);
            }

            // Make instance of user to retrieve information for
            User monitors = currentUser.getMonitorsUsers().get(position);
            TextView name = itemView.findViewById(R.id.list_name);
            TextView email = itemView.findViewById(R.id.list_email);
            ImageView remove=itemView.findViewById(R.id.remove_user);

            // Make a call to collect the name and email of the user
            Call<User> call = proxy.getUserById(monitors.getId());
            ProxyBuilder.callProxy(PreferencesActivity.this, call, returnUser -> respond(returnUser, name, email, remove, position));

            return itemView;
        }
    }

    private void respond(User returnUser, TextView name, TextView email, ImageView remove, int position) {
            // Update the item view with user information
            name.setText(returnUser.getName());
            email.setText(returnUser.getEmail());
            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(PreferencesActivity.this)
                            .setMessage("Are you sure you want to remove this User?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    proxy = ProxyBuilder.getProxy(getString(R.string.apikey), currentUserToken);
                                    Call<Void> caller = proxy.removeFromMonitorsUsers(currentUser.getId(), returnUser.getId());
                                    ProxyBuilder.callProxy(PreferencesActivity.this, caller, returnNothing-> response(returnNothing, position));
                                }
                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                            startActivity(getIntent());
                        }
                    }).show();
                }
            });
    }

    //Call back function for item click (edit emergency contact info function)
    private void EditEmergencyInfoFormonitoringUser() {
        ListView monitoring = findViewById(R.id.monitoring_list);
        monitoring.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Intent passIntent = ViewEmergencyContactActivity.launchIntentViewEmergency(PreferencesActivity.this);
                passIntent.putExtra("User Id", monitorsUsers.get(position).getId());
                passIntent.putExtra("Token", currentUserToken);
                startActivity(passIntent);
                return true;
            }
        });
    }

    //call back for view child's group
    private void ViewMyChildsGroups() {
        ListView monitoring = findViewById(R.id.monitoring_list);
        monitoring.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent passIntent = ViewGroupActivity.launchIntentViewGroups(PreferencesActivity.this);
                passIntent.putExtra("Email", monitorsUsers.get(position).getEmail());
                passIntent.putExtra("Token", currentUserToken);
                startActivity(passIntent);
            }
        });
    }




    private class myListAdapterMonitored extends ArrayAdapter<User> {
        public myListAdapterMonitored(){
            super(PreferencesActivity.this, R.layout.layout_pref_list, currentUser.getMonitoredByUsers());
        }
        View itemView;

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            itemView = convertView;
            if(itemView == null){
                itemView = getLayoutInflater().inflate(R.layout.layout_pref_list, parent, false);
            }

            // Make instance of user to retrieve information for
            User monitoredBy = currentUser.getMonitoredByUsers().get(position);
            TextView name = itemView.findViewById(R.id.list_name);
            TextView email = itemView.findViewById(R.id.list_email);
            ImageView remove=itemView.findViewById(R.id.remove_user);

            // Make a call to collect the name and email of the user
            Call<User> call = proxy.getUserById(monitoredBy.getId());
            ProxyBuilder.callProxy(PreferencesActivity.this, call, returnUser -> respond(returnUser, name, email, remove, position));

            return itemView;
        }

        private void respond(User returnUser, TextView name, TextView email, ImageView remove, int position) {
            // Update the item view with user information
            name.setText(returnUser.getName());
            email.setText(returnUser.getEmail());
            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(PreferencesActivity.this)
                            .setMessage("Are you sure you wnat to remove this user?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    proxy = ProxyBuilder.getProxy(getString(R.string.apikey), currentUserToken);
                                    Call<Void> caller = proxy.removeFromMonitoredByUsers(currentUser.getId(), returnUser.getId());
                                    ProxyBuilder.callProxy(PreferencesActivity.this, caller, returnNothing-> responseMonitoredNothing(returnNothing, position));
                                }
                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                    startActivity(getIntent());
                                }
                            }).show();
                }
            });
        }
    }

    private void monitoredBy() {
        // Make a call to server to retrieve monitored by users
        // proxy = ProxyBuilder.getProxy(getString(R.string.apikey), currentUserToken);

        Call<List<User>> call = proxy.getMonitoredByUsers(currentUser.getId());
        ProxyBuilder.callProxy(PreferencesActivity.this, call, returnedListMon -> responseMonitoredByUsers(returnedListMon));

        // Build array adapter for monitored by list
        adapterMonitored = new myListAdapterMonitored();
        ListView list = findViewById(R.id.monitored_by_list);
        list.setAdapter(adapterMonitored);
    }

    private void responseMonitoredByUsers(List<User> returnedList) {
        // Update the current user with the list of who they are monitored by
        currentUser.setMonitoredByUsers(returnedList);
    }
}
