package ca.sfu.djlin.walkinggroup.app;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ca.sfu.djlin.walkinggroup.R;
import ca.sfu.djlin.walkinggroup.Utilities;
import ca.sfu.djlin.walkinggroup.dataobjects.Group;
import ca.sfu.djlin.walkinggroup.model.Session;
import ca.sfu.djlin.walkinggroup.model.User;
import ca.sfu.djlin.walkinggroup.proxy.ProxyBuilder;
import ca.sfu.djlin.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;


public class CreateGroupActivity extends AppCompatActivity implements OnMapReadyCallback {

    // Constants
    public static final String TAG = "CreateGroup";
    private static final int GET_MARKER_CODE = 9999;

    // Constants
    private static final int REQUEST_CODE_GET_DATA = 1024;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;

    // Map Permissions
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;

    private WGServerProxy proxy;
    private LatLng intendedLatLng;
    private LatLng meetingMarkerLatLng;

    private List<Double> latList = new ArrayList();
    private List<Double> lngList = new ArrayList();

    private String token;
    private Long currentUserId;
    private Long groupId;
    private Group currentGroup;
    private User currentUser;
    private List<User> monitorsUsersList;
    ArrayAdapter<User> adapterMemberList;
    String groupDescription;

    Session data;

    private Group group = new Group();


    // Google Map Related
    private Boolean mLocationPermissionsGranted = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LatLng currentposition = new LatLng(0,0);
    private LatLng latlng;
    List<Marker> markers = new ArrayList();
    private Marker meetingMarker;
    Boolean markerexists = false;
    Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        // Retrieve data from intent
        Intent intent = getIntent();
        token = intent.getStringExtra("token");

        // Store current group information
        intendedLatLng = new LatLng(intent.getDoubleExtra("lat",0),intent.getDoubleExtra("lng",0));

        getLocationPermission();

        // Get data from singleton
        data = Session.getSession(getApplicationContext());
        proxy = data.getProxy();
        currentUser = data.getUser();
        currentUserId = currentUser.getId();

        // Setup Buttons
        setupConfirmGroup();
        setupBackButton();
        setupConfirmButton();
        setupAddUserButton();
        setupRemoveUserButton();
    }

    private void setupRemoveUserButton() {
        Button removeUserBtn = (Button) findViewById(R.id.createGroupremove_user_btn);
        removeUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get input string
                String removeUserEmail;
                EditText removeUserEmailInput = (EditText) findViewById(R.id.createGroupremove_user_input);
                removeUserEmail = removeUserEmailInput.getText().toString();

                // First make a call to proxy to get the id of user to remove
                Call<User> call = proxy.getUserByEmail(removeUserEmail);
                ProxyBuilder.callProxy(CreateGroupActivity.this, call, returnedUserInfo -> removeUserResponse(returnedUserInfo));
            }
        });
    }

    private void setupAddUserButton() {
        Button addUserBtn = (Button) findViewById(R.id.createGroupadd_user_btn);
        addUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get input string
                String addUserEmail;
                EditText addUserEmailInput = (EditText) findViewById(R.id.createGroupadd_user_input);
                addUserEmail = addUserEmailInput.getText().toString();

                // Make a call to proxy to obtain the user id
                Call<User> call = proxy.getUserByEmail(addUserEmail);
                ProxyBuilder.callProxy(CreateGroupActivity.this, call, returnedUserInfo -> addUserResponse(returnedUserInfo));
            }
        });
    }

    // Response for Add user
    private void addUserResponse(User returnedUser) {
        Long userId = returnedUser.getId();

        // Iterate through our list of who the current user monitors
        for (User user : monitorsUsersList) {
            // Add the user if he is being monitored by current user
            if (userId.equals(user.getId())) {

                EditText addUser=findViewById(R.id.createGroupadd_user_input);
                addUser.setText("");
                Call<List<User>> call = proxy.addGroupMember(groupId, returnedUser);
                ProxyBuilder.callProxy(CreateGroupActivity.this, call, returnedUserList -> addMemberResponse(returnedUserList));
            }
        }
    }

    // response for adding member
    private void addMemberResponse(List<User> listOfUsers) {
        Toast.makeText(CreateGroupActivity.this, CreateGroupActivity.this.getString(R.string.joined_group_toast), Toast.LENGTH_SHORT).show();
        // Update the UI
        refreshUI();
    }

    // response for remove User
    private void removeUserResponse(User returnedUser) {
        Long userId = returnedUser.getId();

        // Check if are the leader of the group
        if (currentGroup.getLeader() != null) {
            if (currentUserId.equals(currentGroup.getLeader().getId())) {
                // Proceed to remove user
                EditText removeUser=findViewById(R.id.createGroupremove_user_input);
                removeUser.setText("");
                Call<Void> call = proxy.removeGroupMember(groupId, userId);
                ProxyBuilder.callProxy( CreateGroupActivity.this, call, returnedNothing -> deleteUserResponse(returnedNothing));
            }
        }

        // Iterate through our list of who the current user monitors
        for (User user : monitorsUsersList) {
            // Remove the user if he is being monitored by current user
            if (userId.equals(user.getId())) {
                Call<Void> call = proxy.removeGroupMember(groupId, userId);
                ProxyBuilder.callProxy(CreateGroupActivity.this, call, returnedNothing -> deleteUserResponse(returnedNothing));
            }
        }
    }

    // response for deleting user
    private void deleteUserResponse (Void response) {
        Toast.makeText(CreateGroupActivity.this,CreateGroupActivity.this.getString(R.string.user_deleted_toast), Toast.LENGTH_SHORT).show();

        // Update the UI
        refreshUI();
    }

    // setup Confirm Group
    private void setupConfirmGroup() {
        Button confirmGroup=findViewById(R.id.confirm_btn);
        confirmGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Store information in intent
                Intent intent = MapActivityDrawer.launchIntentMap(getApplicationContext());
                intent.putExtra("groupId", groupId);
                intent.putExtra("groupName", groupDescription);
                intent.putExtra("lat", intendedLatLng.latitude);
                intent.putExtra("lng", intendedLatLng.longitude);
                startActivity(intent);
                finish();
            }
        });
    }

    //Function to create group (getting the id of the group)
    private void setupConfirmButton() {
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
                Button confirmButton = findViewById(R.id.confirmGroup);
                confirmButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        group.setGroupDescription(name);
                        group.setLeader(currentUser);
                        Call<Group> caller = proxy.createGroup(group);
                        ProxyBuilder.callProxy(CreateGroupActivity.this, caller, returnedGroup -> createGroupResponse(returnedGroup));
                    }
                });
            }
        });
    }

    //Response for creating the group
    private void createGroupResponse(Group group) {
        Log.d(TAG, "createGroupResponse: ");

        // Define variables to store
        groupId = group.getId();
        groupDescription = group.getGroupDescription();
        currentUser.getLeadsGroups().add(group);
        Log.i("NMNMNM", currentUser.getLeadsGroups().get(0)+"");
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

    // Function for retrieving Current user info
    private void retrieveCurrentUserInformation() {
        // Set up proxy
        proxy = ProxyBuilder.getProxy(getString(R.string.apikey),token);
        Call<User> call = proxy.getUserById(currentUserId);
        ProxyBuilder.callProxy(CreateGroupActivity.this, call, returnedUser -> storeUserInfo(returnedUser));
    }



    // Map functions
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Log.d(TAG, "onMapReady: map is ready");

        if (mLocationPermissionsGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);

            // Hide the default location button because the position for it cannot be moved
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            // Enable zoom controls
            mMap.getUiSettings().setZoomControlsEnabled(true);
            // Disable Map Toolbar
            mMap.getUiSettings().setMapToolbarEnabled(false);

            // Initialize search box listeners
            init();
        }
        setMapClickListeners();
    }

    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting device location");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (mLocationPermissionsGranted){
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Found Location!");
                            Location currentLocation = (Location) task.getResult();

                            System.out.println(currentLocation.getLatitude());
                            currentposition=new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM, "My Location");
                        } else {
                            Log.d(TAG, "Current location is null!");
                            Toast.makeText(CreateGroupActivity.this, CreateGroupActivity.this.getString(R.string.unable_to_get_location), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: Security Exception: " + e.getMessage());
        }
    }

    private void moveCamera(LatLng latLng, float zoom, String title) {
        // Move camera to location
        Log.d(TAG, "moveCamera: Moving the camera to lat: " + latLng.latitude + ", lng: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        // Omits self location and adds marker
        if (title != "My Location") {
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(title);
            mMap.addMarker(options);
        }

        // Hides keyboard
        Utilities.hideKeyboard(CreateGroupActivity.this);
    }

    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {FINE_LOCATION, COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }


    private void init() {
        Log.d(TAG, "init: initializing");
        EditText searchText=findViewById(R.id.search_input);
        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                // Check to see if user presses keys to initiate a search
                // ACTION_DOWN is the return key on the keyboard
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {

                    searchText.clearFocus();
                    geoLocate();
                }
                return false;
            }
        });
        // Hides keyboard
        Utilities.hideKeyboard(CreateGroupActivity.this);
    }

    public void setMapClickListeners(){
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                // Remove meeting markers
                if (meetingMarker != null) {
                    meetingMarker.remove();
                }

                Boolean flag;


                Intent meetingMarkerIntent = getIntent();
                flag = meetingMarkerIntent.getBooleanExtra("Retrieve Marker", false);

                if (flag) {
                    Log.d(TAG, "This intent was started by create groups!");

                    meetingMarkerLatLng =  new LatLng(latLng.latitude,latLng.longitude);

                    // Add lat and long to list
                    latList.add(meetingMarkerLatLng.latitude);
                    lngList.add(meetingMarkerLatLng.longitude);

                    // Update group information
                    group.setRouteLatArray(latList);
                    group.setRouteLngArray(lngList);
                } else {

                    if(markerexists==true){
                        mMap.clear();
                    }
                    // Add the intended group location to lists
                    intendedLatLng=new LatLng(latLng.latitude, latLng.longitude);
                    latlng = latLng;
                    latList.add(intendedLatLng.latitude);
                    lngList.add(intendedLatLng.longitude);
                    group.setRouteLatArray(latList);
                    group.setRouteLngArray(lngList);
                    marker=mMap.addMarker(new MarkerOptions().position(latlng).title(groupDescription));
                    markerexists=true;
                }
            }
        });
    }

    private void geoLocate() {
        Log.d(TAG, "geoLocate: geolocating");

        // Get search string from search text box
        EditText SearchText=findViewById(R.id.search_input);
        String searchString = SearchText.getText().toString();

        // Create new geocoder
        Geocoder geocoder = new Geocoder(CreateGroupActivity.this);

        // Create new address arraylist
        List<Address> list = new ArrayList<>();

        // Try to populate arraylist
        try {
            list = geocoder.getFromLocationName(searchString, 1);
        } catch (IOException e){
            Log.e(TAG, "geoLocate: IOException: " + e.getMessage());
        }

        // Address find
        if (list.size() > 0) {
            Address address = list.get(0);
            Log.d(TAG, "Found an address: " + address.toString());

            // Move camera to lat and lng
            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM, address.getAddressLine(0));
        } else {
            Log.d(TAG, "Unable to find an address!");
        }
    }


    // GROUP FUNCTIONS

    // Refresh UI for the adapter
    private void refreshUI() {
        // Update the UI
        Call<Group> call = proxy.getGroupById(groupId);
        ProxyBuilder.callProxy(CreateGroupActivity.this, call, returnedGroupInfo -> retrieveGroupInfo(returnedGroupInfo));
    }

    private void retrieveGroupInfo(Group group) {
        Log.d(TAG, "retrieveGroupInfo: ");

        // Save group information into currentGroup
        currentGroup = group;

        // Set up array adapter
        adapterMemberList = new memberListAdapter();
        ListView list = findViewById(R.id.createGroupmember_list);
        list.setAdapter(adapterMemberList);
    }

    // Set up group member list (ADAPTER FOR THE LIST)
    private class memberListAdapter extends ArrayAdapter<User> {
        public memberListAdapter(){
            super(CreateGroupActivity.this, R.layout.layout_monitoring_list, currentGroup.getMemberUsers());
        }
        View itemView;

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            itemView = convertView;
            if(itemView == null){
                itemView = getLayoutInflater().inflate(R.layout.layout_monitoring_list, parent, false);
            }

            // Make instance of user to retrieve information for
            User user = currentGroup.getMemberUsers().get(position);
            TextView name = itemView.findViewById(R.id.list_name);
            TextView email = itemView.findViewById(R.id.list_email);

            // Make a call to collect the name and email of the user
            Call<User> call = proxy.getUserById(user.getId());
            ProxyBuilder.callProxy(CreateGroupActivity.this, call, returnUser -> respond(returnUser, name, email));

            return itemView;
        }

        private void respond(User returnUser, TextView name, TextView email) {
            // Update the item view with user information
            name.setText(returnUser.getName());
            email.setText(returnUser.getEmail());
        }
    }

    private void storeUserInfo(User returnedUser) {
        currentUser = returnedUser;
        monitorsUsersList = returnedUser.getMonitorsUsers();
    }

    public static Intent makeintent(Context context){
        Intent intent = new Intent(context, CreateGroupActivity.class);
        return intent;
    }

    public static String getresult(Intent intent){
        return intent.getStringExtra("groupName");
    }

    private void initMap() {
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(CreateGroupActivity.this);
    }
}
