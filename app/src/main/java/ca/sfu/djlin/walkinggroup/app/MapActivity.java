package ca.sfu.djlin.walkinggroup.app;

import android.Manifest;
import android.app.Activity;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ca.sfu.djlin.walkinggroup.R;
import ca.sfu.djlin.walkinggroup.Utilities;
import ca.sfu.djlin.walkinggroup.proxy.WGServerProxy;

import ca.sfu.djlin.walkinggroup.dataobjects.Group;
import ca.sfu.djlin.walkinggroup.proxy.ProxyBuilder;
import retrofit2.Call;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = "MapActivity";

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

    // Google Map Related
    private Boolean mLocationPermissionsGranted = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private GoogleMap mMap;
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
    Long selectedGroupId;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Log.d(TAG, "onMapReady: map is ready");
        Toast.makeText(MapActivity.this, "Map is ready", Toast.LENGTH_SHORT).show();

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

        // Need to check ordering for this
        SharedPreferences preferences = MapActivity.this.getSharedPreferences("User Session", MODE_PRIVATE);
        token = preferences.getString("Token", null);
        currentUserEmail = preferences.getString("Email", null);
        Log.d(TAG, "onMapReady: The current token is: " + token);
        proxy = ProxyBuilder.getProxy(getString(R.string.apikey), token);
        // End need to check order for this

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
                // Call<Group> getGroupById(@Path("id") Long groupId);
                Call<Group> call = proxy.getGroupById(groupId);
                ProxyBuilder.callProxy(MapActivity.this, call, returnedGroup -> drawMeetingMarker(returnedGroup));

                return true;
            }
        });
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        mSearchText = (EditText) findViewById(R.id.search_input);
        mGps = (ImageView) findViewById(R.id.ic_gps);
        mLogout = (ImageView) findViewById(R.id.ic_logout);
        mGroupInfo = (ImageView) findViewById(R.id.ic_group_info);

        // Logout listener
        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });

        getLocationPermission();

        SharedPreferences preferences = this.getSharedPreferences("User Session", MODE_PRIVATE);
        token = preferences.getString("Token", null);
        currentUserEmail = preferences.getString("Email", null);

        // Build new proxy
        proxy = ProxyBuilder.getProxy(getString(R.string.apikey), token);

        setupGroupInfoButton();
    }

    public void setGroupMarker(){
        Log.d(TAG, "setGroupMarker: The current token is " + token);
        Call<List<Group>> caller = proxy.getGroups();
        ProxyBuilder.callProxy(MapActivity.this, caller, returnedGroups -> response(returnedGroups));
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

    private void notifyUserViaLogAndToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void logout() {
        Log.d(TAG, "logout: Attempting to logout...");
        Intent intent = WelcomeActivity.launchWelcomeIntent(MapActivity.this);

        SharedPreferences preferences = MapActivity.this.getSharedPreferences("User Session" , MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("Token");
        editor.remove("Email");
        editor.remove("User Id");
        editor.apply();
        startActivity(intent);
        finish();
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
        Utilities.hideKeyboard(MapActivity.this);

    }

    private void geoLocate() {
        Log.d(TAG, "geoLocate: geolocating");

        // Get search string from search text box
        String searchString = mSearchText.getText().toString();

        // Create new geocoder
        Geocoder geocoder = new Geocoder(MapActivity.this);

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
            // Toast.makeText(MapActivity.this, "The address returned is: " + address.toString() ,Toast.LENGTH_SHORT).show();

            // Move camera to lat and lng
            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM, address.getAddressLine(0));
        } else {
            Log.d(TAG, "Unable to find an address!");
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
                    Intent intent = new Intent(MapActivity.this, CreateGroupActivity.class);
                    intent.putExtra("lat", latLng.latitude);
                    intent.putExtra("lng", latLng.longitude);
                    intent.putExtra("token", token);
                    latlng = latLng;

                    startActivityForResult(intent, REQUEST_CODE_GET_DATA);
                }
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_GET_DATA:
                if (resultCode == Activity.RESULT_OK) {
                    String groupName = CreateGroupActivity.getresult(data);
                    Long groupId = data.getLongExtra("groupId", 0);

                    Marker marker = mMap.addMarker(new MarkerOptions().position(latlng).title(groupName));
                    markers.add(marker);
                    // System.out.println(markers.size());
                    // System.out.println(markers.get(0));

                    // Store marker in HashMap for onClick retrieval
                    mHashMap.put(marker, groupId);
                } else {
                    Log.i("My app", "Activity cancelled.");
                }
        }
    }

    private void setupGroupInfoButton() {
        mGroupInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Clicking on group info button");

                // Launch Group Info Activity and pass groupId
                Intent intent = GroupInfoActivity.launchGroupInfoIntent(MapActivity.this);
                intent.putExtra("groupId", selectedGroupId);
                intent.putExtra("token", token);
                startActivity(intent);
            }
        });
    }

    //function for action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater new_menu=getMenuInflater();
        new_menu.inflate(R.menu.map_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // Action bar preference button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Checking id
        if(item.getItemId() == R.id.settings){
            Intent pass_intent = PreferencesActivity.launchIntentPreferences(MapActivity.this);

            SharedPreferences preferences = this.getSharedPreferences("User Session", MODE_PRIVATE);
            token = preferences.getString("Token", null);
            currentUserEmail = preferences.getString("Email", null);

            pass_intent.putExtra("Token", token);
            pass_intent.putExtra("Email", currentUserEmail);
            startActivity(pass_intent);
        }
        return super.onOptionsItemSelected(item);
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
            Toast.makeText(MapActivity.this, MapActivity.this.getString(R.string.no_meeting_location), Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(MapActivity.this, "Unable to get current location!", Toast.LENGTH_SHORT).show();
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
        Utilities.hideKeyboard(MapActivity.this);
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
        Intent intent = new Intent(context, MapActivity.class);
        return intent;
    }

    public static Intent launchIntentMapForMarker(Context context) {
        Intent intent = new Intent(context, MapActivity. class);
        return intent;
    }

    private void initMap() {
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(MapActivity.this);
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
}
