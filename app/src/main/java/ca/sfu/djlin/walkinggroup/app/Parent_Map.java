package ca.sfu.djlin.walkinggroup.app;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import ca.sfu.djlin.walkinggroup.R;
import ca.sfu.djlin.walkinggroup.Utilities;
import ca.sfu.djlin.walkinggroup.dataobjects.GpsLocation;
import ca.sfu.djlin.walkinggroup.dataobjects.Group;
import ca.sfu.djlin.walkinggroup.model.User;
import ca.sfu.djlin.walkinggroup.proxy.ProxyBuilder;
import ca.sfu.djlin.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

public class Parent_Map extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = "Parent_Map";

    // Constants
    private static final int REQUEST_CODE_GET_DATA = 1024;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;;

    // Map Permissions
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;

    // Widgets
    private EditText mSearchText;
    private ImageView mGps;
    private ImageView mLogout;
    private ImageView mGroupInfo;
    private ImageView mMonitorSettings;
    private Timer timer=new Timer();
    private Timer timer_get=new Timer();
    // Google Map Related
    private Boolean mLocationPermissionsGranted = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private GoogleMap mMap;
    private LatLng currentposition = new LatLng(0,0);
    private LatLng latlng;
    List<Marker> markers = new ArrayList();
    List<Marker> marker_user=new ArrayList();

    String temp_name;
    int groupSize=0;
    private String time="1991-1-1,11:11:11-";
    // Create HashMap used for storing group ID
    private HashMap<Marker, Long> mHashMap = new HashMap<Marker, Long>();

    // Proxy
    private WGServerProxy proxy;

    //count the times of uploading data or dowanloading data from server
    int times=0;
    int counts=0;
    int index =0;
    // User Variables
    String token;
    String currentUserEmail;
    User currentUser=new User();


    GpsLocation gpsLocation=new GpsLocation();

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
            setUpStart();
            setUpStop();
            setGroupMarker();
        }

        // Need to check ordering for this
        SharedPreferences preferences = Parent_Map.this.getSharedPreferences("User Session", MODE_PRIVATE);
        token = preferences.getString("Token", null);
        currentUserEmail = preferences.getString("Email", null);
        getUserId();
        Log.d(TAG, "onMapReady: The current token is: " + token);
        proxy = ProxyBuilder.getProxy(getString(R.string.apikey), token);

        Intent intent=getIntent();
        if(intent!=null) {
            latlng=new LatLng(intent.getDoubleExtra("lat",0), intent.getDoubleExtra("lng", 0));
            String groupName=intent.getStringExtra("groupName");
            Marker marker = mMap.addMarker(new MarkerOptions().position(latlng).title(groupName));
            markers.add(marker);
            // System.out.println(markers.size());
            // System.out.println(markers.get(0));

            // Store marker in HashMap for onClick retrieval
            mHashMap.put(marker, intent.getLongExtra("groupId", 0));
        }
        // End need to check order for this
        //setGroupMarker();

        // Listener for group marker clicks
       /* mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener(){
            @Override
            public boolean onMarkerClick(Marker marker) {
                // Obtain groupId
                Long groupId = mHashMap.get(marker);
                selectedGroupId = groupId;
                Log.d(TAG, "The groupId retrieved was: " + groupId);

                // Draw the meeting location marker
                if (!marker.equals(meetingMarker)) {
                    Call<Group> call = proxy.getGroupById(groupId);
                    ProxyBuilder.callProxy(Parent_Map.this, call, returnedGroup -> drawMeetingMarker(returnedGroup));
                } else if (marker.equals(meetingMarker)){
                    Toast.makeText(Parent_Map.this, Parent_Map.this.getString(R.string.meeting_location), Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(Parent_Map.this, Parent_Map.this.getString(R.string.no_meeting_location), Toast.LENGTH_SHORT).show();
                }

                return true;
            }
        });
        */
    }



    private void setUpStop() {
        Button button=findViewById(R.id.button_stop);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("timer cancel");
                timer.cancel();
                timer_get.cancel();
                timer=new Timer();
                timer_get=new Timer();
            }
        });

    }

    private void setUpStart() {
        Button button=findViewById(R.id.button_start);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("timer start");
                updateGpsLoaction();
            }
        });

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader__map);
        mSearchText = (EditText) findViewById(R.id.search_input);
        mGps = (ImageView) findViewById(R.id.ic_gps);
        mLogout = (ImageView) findViewById(R.id.ic_logout);
        mGroupInfo = (ImageView) findViewById(R.id.ic_group_info);
        mMonitorSettings = (ImageView) findViewById(R.id.ic_settings);

        // Logout listener
        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                timer_get.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        startDowanloadGpsLocation();
                        counts++;
                    }
                },0,5000);
            }
        });

        getLocationPermission();

        SharedPreferences preferences = Parent_Map.this.getSharedPreferences("User Session", MODE_PRIVATE);
        token = preferences.getString("Token", null);
        currentUserEmail = preferences.getString("Email", null);
        // Build new proxy
        proxy = ProxyBuilder.getProxy(getString(R.string.apikey), token);

        // Set up onclick listeners
    }

    public void setGroupMarker(){
        Log.d(TAG, "setGroupMarker: The current token is " + token);
        Call<List<Group>> caller = proxy.getGroups();
        ProxyBuilder.callProxy(Parent_Map.this, caller, returnedGroups -> response(returnedGroups));
    }

    private void response(List<Group> returnedGroups) {
        Log.d(TAG, "The current token is: " + token);
        int i = 0;
        for (Group group : returnedGroups) {
            double lat = group.getRouteLatArray().get(i);
            double lng = group.getRouteLngArray().get(i);
            LatLng latLng=new LatLng(lat,lng);

            Log.d(TAG, "The type of groupID is: " + group.getId());
            // Add marker to map
            Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).title(group.getGroupDescription()));
            // Add marker to list
            markers.add(marker);
            // Store marker in HashMap for onClick retrieval
            mHashMap.put(marker, group.getId());
        }
    }



    private void init() {
        Log.d(TAG, "init: initializing");

        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                // Check to see if user presses keys to initiate a search
                // ACTION_DOWN is the return key on the keyboard
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {

                    mSearchText.clearFocus();
                    geoLocate();
                }
                return false;
            }
        });

        // Center back to user location
        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked gps icon");
                getDeviceLocation();
            }
        });

        // Hides keyboard
        Utilities.hideKeyboard(Parent_Map.this);

    }

    private void geoLocate() {
        Log.d(TAG, "geoLocate: geolocating");

        // Get search string from search text box
        String searchString = mSearchText.getText().toString();

        // Create new geocoder
        Geocoder geocoder = new Geocoder(Parent_Map.this);

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
                            Toast.makeText(Parent_Map.this, Parent_Map.this.getString(R.string.unable_to_get_location), Toast.LENGTH_SHORT).show();
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
        Utilities.hideKeyboard(Parent_Map.this);
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

    public static Intent launchIntentMap(Context context) {
        Intent intent = new Intent(context, Parent_Map.class);
        return intent;
    }

    public static Intent launchIntentMapForMarker(Context context) {
        Intent intent = new Intent(context, Parent_Map. class);
        return intent;
    }

    private void initMap() {
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(Parent_Map.this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mLocationPermissionsGranted = false;

        switch(requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0) {
                    for (int i = 0 ; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            mLocationPermissionsGranted = false;
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionsGranted = true;
                    // Initialize map
                    initMap();
                }
            }
        }
    }

    //upload Gps location for each user
    public void updateGpsLoaction(){
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if(times==10) {
                    times = 0;
                    timer.cancel();
                }
                else {
                    getDeviceLocation();
                    time = time + 1;
                    GpsLocation gpsLocation = new GpsLocation();
                    gpsLocation.setGpsLocation(currentposition, time);
                    Call<GpsLocation> caller = proxy.setLastGpsLocation(currentUser.getId(), gpsLocation);
                    ProxyBuilder.callProxy(Parent_Map.this, caller, returnGps -> gpsResponse(returnGps));
                    times++;
                }
            }
        },0,3000);
    }

    //get the size of user list of monitoring
    public void getUserSize(){
        groupSize=currentUser.getMonitorsUsers().size();

    }

    //get the members that you are monitoring
    public void startDowanloadGpsLocation(){

        List<User> users=currentUser.getMonitorsUsers();
        for(int i=0;i<users.size();i++){
            Call<User> caller=proxy.getUserById(users.get(i).getId());
            ProxyBuilder.callProxy(Parent_Map.this,caller, returnUsers->singleUserResponse(returnUsers));
        }
    }
    //get users gps location information
    private void singleUserResponse(User returnUser) {
        System.out.println("dijici");
        Call<GpsLocation> caller=proxy.getLastGpsLocation(returnUser.getId());
        temp_name=returnUser.getName();
        ProxyBuilder.callProxy(this,caller,returnGps->gpsResponseForEachUser(returnGps));

    }
    //each user return a gps location to show in the map
    private void gpsResponseForEachUser(GpsLocation returnGps) {
        System.out.println("gps is " +returnGps.toString());
        int btnWidth = 70;
        int btnHeight = 100;
        Bitmap originBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.user);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(originBitmap, btnWidth, btnHeight, false);

        System.out.println("jicia");
        if(marker_user.isEmpty()==true) {
            marker_user.add(mMap.addMarker(new MarkerOptions().position(returnGps.toLatlng(returnGps))
                    .title(temp_name).icon(BitmapDescriptorFactory.fromBitmap(scaledBitmap))));
        }
        else if(marker_user.isEmpty()==false) {
            if(index <marker_user.size()) {
                if (marker_user.get(index) != null) {
                    marker_user.get(index).remove();
                }
                marker_user.set(index, mMap.addMarker(new MarkerOptions().position(returnGps.toLatlng(returnGps))
                        .title(temp_name).icon(BitmapDescriptorFactory.fromBitmap(scaledBitmap))));
            }
            else{
                marker_user.add(mMap.addMarker(new MarkerOptions().position(returnGps.toLatlng(returnGps))
                        .title(temp_name).icon(BitmapDescriptorFactory.fromBitmap(scaledBitmap))));
            }
        }
        System.out.println("important "+groupSize);
        index++;
        index = index %groupSize;
        //marker_user.add(marker);
        System.out.println(returnGps.toLatlng(returnGps));
    }

    //do thing for get gps response by proxy
    private void gpsResponse(GpsLocation returnGps) {
        System.out.println("gpslocation done");
    }



    //get current user id
    public void getUserId(){
        Call<User> caller=proxy.getUserByEmail(currentUserEmail);
        ProxyBuilder.callProxy(this,caller,returnedUser->userResponse(returnedUser));
    }

    private void userResponse(User returnedUser) {
        currentUser=returnedUser;
        getUserSize();

    }
}
