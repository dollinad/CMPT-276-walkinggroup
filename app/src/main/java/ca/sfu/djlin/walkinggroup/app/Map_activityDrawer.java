package ca.sfu.djlin.walkinggroup.app;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ca.sfu.djlin.walkinggroup.R;
import ca.sfu.djlin.walkinggroup.Utilities;
import ca.sfu.djlin.walkinggroup.dataobjects.Group;
import ca.sfu.djlin.walkinggroup.model.User;
import ca.sfu.djlin.walkinggroup.proxy.ProxyBuilder;
import ca.sfu.djlin.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

public class Map_activityDrawer extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {
    private static final String TAG = "MapActivity";

    // Constants
    private static final int REQUEST_CODE_GET_DATA = 1024;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;;

    // Map Permissions
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;

    // Google Map Related
    private Boolean mLocationPermissionsGranted = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    public static GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LatLng currentposition = new LatLng(0,0);
    private LatLng latlng;
    List<Marker> markers = new ArrayList();
    private Marker meetingMarker;

    // Create HashMap used for storing group ID
    private HashMap<Marker, Long> mHashMap = new HashMap<Marker, Long>();

    // Proxy
    private WGServerProxy proxy;

    // User Variables
    String token;
    String currentUserEmail;
    String currentUserName;
    Long selectedGroupId;
    Long UserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_drawer_test);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        SharedPreferences preferences = this.getSharedPreferences("User Session", MODE_PRIVATE);
        token = preferences.getString("Token", null);
        //currentUserEmail = preferences.getString("Email", null);
        //currentUserName=preferences.getString("Name", null);
        UserId=preferences.getLong("User Id", 0);



        // Build new proxy
        proxy = ProxyBuilder.getProxy(getString(R.string.apikey), token);

        Call<User> call=proxy.getUserById(UserId);
        ProxyBuilder.callProxy(Map_activityDrawer.this, call, returnedNothing -> responseCurrent(returnedNothing));


        getLocationPermission();

    }

    private void responseCurrent(User returnedNothing) {
        //Navigation Drawer Header layout
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header=navigationView.getHeaderView(0);

        //customising navigation drawer Header
        TextView name=(TextView)header.findViewById(R.id.navUserName);
        name.setText(returnedNothing.getName());
        TextView email=(TextView)header.findViewById(R.id.navUserEmail);
        email.setText(returnedNothing.getEmail());

    }

    //                                              MAP FUNCTIONS

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

        SharedPreferences preferences = Map_activityDrawer.this.getSharedPreferences("User Session", MODE_PRIVATE);
        token = preferences.getString("Token", null);
        currentUserEmail = preferences.getString("Email", null);
        Log.d(TAG, "onMapReady: The current token is: " + token);
        proxy = ProxyBuilder.getProxy(getString(R.string.apikey), token);

        Intent intent=getIntent();
        if(intent.getExtras()!=null) {
            latlng=new LatLng(intent.getDoubleExtra("lat",0), intent.getDoubleExtra("lng", 0));
            String groupName=intent.getStringExtra("groupName");
            Marker marker = mMap.addMarker(new MarkerOptions().position(latlng).title(groupName));
            markers.add(marker);
            mHashMap.put(marker, intent.getLongExtra("groupId", 0));
        }

        setMapClickListeners();
        setGroupMarker();

        // Listener for group marker clicks
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener(){
            @Override
            public boolean onMarkerClick(Marker marker) {
                // Obtain groupId
                Long groupId = mHashMap.get(marker);
                selectedGroupId = groupId;
                Log.d(TAG, "The groupId retrieved was: " + groupId);

                // Draw the meeting location marker
                if (!marker.equals(meetingMarker)) {
                    Call<Group> call = proxy.getGroupById(groupId);
                    ProxyBuilder.callProxy(Map_activityDrawer.this, call, returnedGroup -> drawMeetingMarker(returnedGroup));
                } else if (marker.equals(meetingMarker)){
                    Toast.makeText(Map_activityDrawer.this, Map_activityDrawer.this.getString(R.string.meeting_location), Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(Map_activityDrawer.this, Map_activityDrawer.this.getString(R.string.no_meeting_location), Toast.LENGTH_SHORT).show();
                }

                return true;
            }
        });
    }

    public void setGroupMarker(){
        Log.d(TAG, "setGroupMarker: The current token is " + token);
        Call<List<Group>> caller = proxy.getGroups();
        ProxyBuilder.callProxy(Map_activityDrawer.this, caller, returnedGroups -> response(returnedGroups));
    }

    //Response for GroupMarker function
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

        // Hides keyboard
        Utilities.hideKeyboard(Map_activityDrawer.this);

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

                            //System.out.println(currentLocation.getLatitude());
                            currentposition=new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM, "My Location");
                        } else {
                            Log.d(TAG, "Current location is null!");
                            Toast.makeText(Map_activityDrawer.this, Map_activityDrawer.this.getString(R.string.unable_to_get_location), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: Security Exception: " + e.getMessage());
        }
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
                    meetingMarkerIntent.putExtra("meetingLat", latLng.latitude);
                    meetingMarkerIntent.putExtra("meetingLng", latLng.longitude);

                    setResult(Activity.RESULT_OK, meetingMarkerIntent);

                    finish();
                } else {
                    Log.d(TAG, "This is going to start a new activity");
                    Intent intent = new Intent(Map_activityDrawer.this, CreateGroupActivity.class);
                    intent.putExtra("lat", latLng.latitude);
                    intent.putExtra("lng", latLng.longitude);
                    intent.putExtra("token", token);
                    latlng = latLng;

                    startActivityForResult(intent, REQUEST_CODE_GET_DATA);
                }
            }
        });
    }


    private void drawMeetingMarker(Group group) {
        Log.d(TAG, "drawMeetingMarker: ");

        // Assuming stored meeting location is at index 1
        if (group.getRouteLatArray().size() > 1 && group.getRouteLngArray().size() > 1) {
            double lat = group.getRouteLatArray().get(1);
            double lng = group.getRouteLngArray().get(1);

            Log.d(TAG, "retrieved lat: " + lat);
            Log.d(TAG, "retrieved lng: " + lng);

            LatLng latLng = new LatLng(lat, lng);

            // Add marker to map
            int btnWidth = 70;
            int btnHeight = 100;
            Bitmap originBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.marker);
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(originBitmap, btnWidth, btnHeight, false);

            Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).title("Meeting Location for: " + group.getGroupDescription())
                    .icon(BitmapDescriptorFactory.fromBitmap(scaledBitmap)));
            // Add marker to list
            markers.add(marker);

            // Remove meeting marker
            if (meetingMarker != null) {
                meetingMarker.remove();
            }
            // Set new meeting marker to draw next time
            meetingMarker = marker;
        } else {
            Toast.makeText(Map_activityDrawer.this, Map_activityDrawer.this.getString(R.string.no_meeting_location), Toast.LENGTH_SHORT).show();
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
        Utilities.hideKeyboard(Map_activityDrawer.this);
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

    private void initMap() {
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(Map_activityDrawer.this);
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


    //                                                                              NAVIGATION DARWER RELATED

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.map_activity_drawer_test, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    //Items for the navigation drawer (Onclicks for each drawer)
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.logout) {
            logout();
        }
        else if (id == R.id.getLocation) {
            Log.d(TAG, "onClick: clicked gps icon");
            getDeviceLocation();
        }
        else if (id == R.id.createGroup) {
            Log.d(TAG, "Clicking on group info button");

            // Launch Group Info Activity and pass groupId
            Intent intent = CreateGroupActivity.makeintent(Map_activityDrawer.this);
            intent.putExtra("groupId", selectedGroupId);
            intent.putExtra("token", token);
            startActivity(intent);
        }
        else if (id == R.id.MonitoringPrefrences) {
            Toast.makeText(getApplicationContext(), "PPP", Toast.LENGTH_SHORT).show();
            Intent pass_intent = PreferencesActivity.launchIntentPreferences(Map_activityDrawer.this);

            SharedPreferences preferences = Map_activityDrawer.this.getSharedPreferences("User Session", MODE_PRIVATE);
            token = preferences.getString("Token", null);
            currentUserEmail = preferences.getString("Email", null);

            pass_intent.putExtra("Token", token);
            pass_intent.putExtra("Email", currentUserEmail);
            startActivity(pass_intent);
        }
        else if(id==R.id.viewGroups){
            Toast.makeText(getApplicationContext(), "PPP", Toast.LENGTH_SHORT).show();
            Intent pass_intent = ViewGrpupActivity.launchIntentViewGroups(Map_activityDrawer.this);

            SharedPreferences preferences = Map_activityDrawer.this.getSharedPreferences("User Session", MODE_PRIVATE);
            token = preferences.getString("Token", null);
            currentUserEmail = preferences.getString("Email", null);

            pass_intent.putExtra("Token", token);
            pass_intent.putExtra("Email", currentUserEmail);
            startActivity(pass_intent);
        }
        else if(id==R.id.Drawersettings){
            Intent pass_intent=SettingsActivity.launchIntentSettings(Map_activityDrawer.this);
            SharedPreferences preferences = Map_activityDrawer.this.getSharedPreferences("User Session", MODE_PRIVATE);
            token = preferences.getString("Token", null);
            currentUserEmail = preferences.getString("Email", null);

            pass_intent.putExtra("Token", token);
            pass_intent.putExtra("Email", currentUserEmail);
            pass_intent.putExtra("User Id", UserId);

            startActivity(pass_intent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //Navigation drawer related function (Came with the class)
    @Override
    public void onPointerCaptureChanged(boolean hasCapture) { }

    //                                                                  ANDROID BACK BUTTON AND LOGOUT

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    private void logout() {
        Log.d(TAG, "logout: Attempting to logout...");
        Intent intent = WelcomeActivity.launchWelcomeIntent(Map_activityDrawer.this);

        SharedPreferences preferences = Map_activityDrawer.this.getSharedPreferences("User Session" , MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("Token");
        editor.remove("Email");
        editor.remove("User Id");
        editor.apply();
        startActivity(intent);
        finish();
    }

    //                                                                  INTENTS

    public static Intent launchIntentMap(Context context) {
        Intent intent = new Intent(context, Map_activityDrawer.class);
        return intent;
    }

    public static Intent launchIntentMapForMarker(Context context) {
        Intent intent = new Intent(context, Map_activityDrawer. class);
        return intent;
    }

}
