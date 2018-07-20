package ca.sfu.djlin.walkinggroup.app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import ca.sfu.djlin.walkinggroup.R;
import ca.sfu.djlin.walkinggroup.Utilities;
import ca.sfu.djlin.walkinggroup.model.Session;
import ca.sfu.djlin.walkinggroup.model.User;
import ca.sfu.djlin.walkinggroup.proxy.ProxyBuilder;
import ca.sfu.djlin.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

public class SettingsActivity extends AppCompatActivity{
    private WGServerProxy proxy;
    String token;
    String UserEmail;
    User CurrentUser;
    String nameEntered;
    String emailEntered;
    String birthYearEnteredString;
    Integer birthYearEnteredint;
    String birthMonthEnteredString;
    Integer birthMonthEnteredint;
    String cellPhoneEnteredString;
    String homePhoneEnteredString;
    String AddressEnteredString;
    String gradeEnteredString;
    String teacherNameEnteredString;
    String EmergencyContactInfoEntered;
    Long UserId;
    Session session;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Utilities.hideKeyboard(SettingsActivity.this);
        //Get intent
        Intent intent = getIntent();
        //token = intent.getStringExtra("Token");
        UserId=intent.getLongExtra("User Id", 0);
        UserEmail = intent.getStringExtra("Email");

        //Build Proxy
        //proxy = ProxyBuilder.getProxy(getString(R.string.apikey), token);
        session=Session.getSession(getApplicationContext());
        proxy=session.getProxy();
        CurrentUser=session.getUser();

        // Get current user information
        Call<User> caller = proxy.getUserByEmail(UserEmail);

        ProxyBuilder.callProxy(SettingsActivity.this, caller, returnedUser -> responseCurrent(returnedUser));

        setupNameEdit();
        setupBirthYearEdit();
        setupBirthMonthEdit();
        setupAddressEdit();
        setupHomePhoneEdit();
        setupCellPhoneEdit();
        setupEmailEdit();
        setupGradeEdit();
        setupTeacherEdit();
        setupEmergencyContactEdit();
        setupDeleteAccount();



    }

    private void setupDeleteAccount() {
        Button removeUser=findViewById(R.id.deleteAccount);
        removeUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(SettingsActivity.this)
                        .setMessage("Are you sure you want to delete your account?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //proxy = ProxyBuilder.getProxy(getString(R.string.apikey), token);
                                proxy=session.getProxy();
                                Call<Void> caller = proxy.deleteUser(UserId);
                                ProxyBuilder.callProxy(SettingsActivity.this, caller, returnNothing-> responseDelete(returnNothing));
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

    private void responseDelete(Void returnNothing) {
        Intent intent = WelcomeActivity.launchWelcomeIntent(SettingsActivity.this);

        SharedPreferences preferences = SettingsActivity.this.getSharedPreferences("User Session" , MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("Token");
        editor.remove("Email");
        editor.remove("User Id");
        editor.apply();
        startActivity(intent);
        finish();
    }

    private void setupNameEdit() {
       ImageView btn=findViewById(R.id.nameClick);
       btn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Toast.makeText(getApplicationContext(), CurrentUser.getId()+"", Toast.LENGTH_SHORT).show();
               //proxy = ProxyBuilder.getProxy(getString(R.string.apikey), token);
               proxy=session.getProxy();
               Call<User> call=proxy.editUser(CurrentUser.getId(), CurrentUser);
               ProxyBuilder.callProxy(SettingsActivity.this, call, returnedUser -> responseEdit(returnedUser));

           }
       });

    }

    private void setupBirthYearEdit() {
        ImageView btn=findViewById(R.id.BirthYearClick);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), CurrentUser.getId()+"", Toast.LENGTH_SHORT).show();
                //proxy = ProxyBuilder.getProxy(getString(R.string.apikey), token);
                // proxy=session.getProxy();
                Call<User> call=proxy.editUser(CurrentUser.getId(), CurrentUser);
                ProxyBuilder.callProxy(SettingsActivity.this, call, returnedUser -> responseEdit(returnedUser));

            }
        });
    }
    private void setupBirthMonthEdit() {
        ImageView btn=findViewById(R.id.BirthMonthClick);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), CurrentUser.getId()+"", Toast.LENGTH_SHORT).show();
                // proxy = ProxyBuilder.getProxy(getString(R.string.apikey), token);
                Call<User> call=proxy.editUser(CurrentUser.getId(), CurrentUser);
                ProxyBuilder.callProxy(SettingsActivity.this, call, returnedUser -> responseEdit(returnedUser));

            }
        });
    }

    private void setupAddressEdit() {
        ImageView btn=findViewById(R.id.AddressClick);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), CurrentUser.getId()+"", Toast.LENGTH_SHORT).show();
                // proxy = ProxyBuilder.getProxy(getString(R.string.apikey), token);
                Call<User> call=proxy.editUser(CurrentUser.getId(), CurrentUser);
                ProxyBuilder.callProxy(SettingsActivity.this, call, returnedUser -> responseEdit(returnedUser));

            }
        });
    }

    private void setupHomePhoneEdit() {
        ImageView btn=findViewById(R.id.HomePhoneClick);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), CurrentUser.getId()+"", Toast.LENGTH_SHORT).show();
                // proxy = ProxyBuilder.getProxy(getString(R.string.apikey), token);
                Call<User> call=proxy.editUser(CurrentUser.getId(), CurrentUser);
                ProxyBuilder.callProxy(SettingsActivity.this, call, returnedUser -> responseEdit(returnedUser));

            }
        });
    }

    private void setupCellPhoneEdit() {
        ImageView btn=findViewById(R.id.CellPhoneClick);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), CurrentUser.getId()+"", Toast.LENGTH_SHORT).show();
                // proxy = ProxyBuilder.getProxy(getString(R.string.apikey), token);
                Call<User> call=proxy.editUser(CurrentUser.getId(), CurrentUser);
                ProxyBuilder.callProxy(SettingsActivity.this, call, returnedUser -> responseEditEmail(returnedUser));
                SharedPreferences preferences = SettingsActivity.this.getSharedPreferences("User Session" , MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.apply();
                editor.remove("Token");
                editor.remove("Email");



            }
        });
    }

    private void responseEditEmail(User returnedUser) {
        CurrentUser=returnedUser;
        SharedPreferences preferences = SettingsActivity.this.getSharedPreferences("User Session" , MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.apply();
        editor.remove("Token");
        editor.remove("Email");
        // Register for token received
        ProxyBuilder.setOnTokenReceiveCallback(token -> onReceiveToken(token, returnedUser));
        Toast.makeText(getApplicationContext(), "Edited!", Toast.LENGTH_SHORT).show();
    }

    // Handle the token by generating a new Proxy which is encoded with it.
    private void onReceiveToken(String newToken, User returnedUser) {
        // Save token using Shared Preferences
        saveUserInformation(newToken, returnedUser);

        // Rebuild the proxy with updated token
        proxy = ProxyBuilder.getProxy(getString(R.string.apikey), newToken);
        // Finish the login process
        Call<Void> caller = proxy.login(returnedUser);
        ProxyBuilder.callProxy(SettingsActivity.this, caller, returnedNothing -> response(returnedNothing));
    }

    private void saveUserInformation(String newToken, User user) {
        SharedPreferences preferences = this.getSharedPreferences("User Session", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("Token", newToken);
        editor.putString("Email", user.getEmail());
        editor.apply();
    }

    // Login actually completes by calling this; nothing to do as it was all done when we got the token.
    private void response(Void returnedNothing) {
    }

    private void setupEmergencyContactEdit() {
        ImageView btn=findViewById(R.id.EmergencyClick);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), CurrentUser.getId()+"", Toast.LENGTH_SHORT).show();
                // proxy = ProxyBuilder.getProxy(getString(R.string.apikey), token);
                Call<User> call=proxy.editUser(CurrentUser.getId(), CurrentUser);
                ProxyBuilder.callProxy(SettingsActivity.this, call, returnedUser -> responseEdit(returnedUser));

            }
        });
    }

    private void setupEmailEdit() {
        ImageView btn=findViewById(R.id.EmailClick);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // proxy = ProxyBuilder.getProxy(getString(R.string.apikey), token);
                Call<User> call=proxy.editUser(CurrentUser.getId(), CurrentUser);
                ProxyBuilder.callProxy(SettingsActivity.this, call, returnedUser -> responseEdit(returnedUser));

            }
        });
    }

    private void setupGradeEdit() {
        ImageView btn=findViewById(R.id.CurrentGradeClick);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), CurrentUser.getId()+"", Toast.LENGTH_SHORT).show();
                // proxy = ProxyBuilder.getProxy(getString(R.string.apikey), token);
                Call<User> call=proxy.editUser(CurrentUser.getId(), CurrentUser);
                ProxyBuilder.callProxy(SettingsActivity.this, call, returnedUser -> responseEdit(returnedUser));

            }
        });
    }

    private void setupTeacherEdit() {
        ImageView btn=findViewById(R.id.TeachersNameClick);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), CurrentUser.getId()+"", Toast.LENGTH_SHORT).show();
                // proxy = ProxyBuilder.getProxy(getString(R.string.apikey), token);
                Call<User> call=proxy.editUser(CurrentUser.getId(), CurrentUser);
                ProxyBuilder.callProxy(SettingsActivity.this, call, returnedUser -> responseEdit(returnedUser));

            }
        });
    }



    //response for current user
    private void responseCurrent(User user) {
        // Store retrieved user into currentUser
        CurrentUser = user;
        //Toast.makeText(getApplicationContext(), CurrentUser.getName(), Toast.LENGTH_SHORT).show();
        EditText nameDisplay=findViewById(R.id.nameDisplay);
        nameDisplay.setText(CurrentUser.getName());
        NameEdit(CurrentUser);

        EditText EmailDisplay=findViewById(R.id.EmailDisplay);
        EmailDisplay.setText(UserEmail);
        EmailEdit();

        EditText birthYearDisplay=findViewById(R.id.birthYearDisplay);
        birthYearDisplay.setText(user.getBirthYear()+"");
        BirthYearEdit();

        EditText birthMonthDisplay=findViewById(R.id.birthMonthDisplay);
        birthMonthDisplay.setText(user.getBirthMonth()+"");
        BirthMonthEdit();

        EditText AddressDisplay=findViewById(R.id.adressDisplay);
        AddressDisplay.setText(user.getAddress());
        AddressEdit();

        EditText HomePhoneDisplay=findViewById(R.id.homePhoneDisplay);
        HomePhoneDisplay.setText(user.getHomePhone());
        HomePhoneEdit();

        EditText CellPhoneDisplay=findViewById(R.id.cellPhoneDisplay);
        CellPhoneDisplay.setText(user.getCellPhone());
        CellPhoneEdit();

        EditText GradeDisplay=findViewById(R.id.currentgradeDisplay);
        GradeDisplay.setText(user.getGrade());
        gradeEdit();

        EditText teacherNameDisplay=findViewById(R.id.teacgersNameDisplay);
        teacherNameDisplay.setText(user.getTeacherName());
        teacherNameEdit();

        EditText EmergencyContactInfo=findViewById(R.id.emergencyNameDisplay);
        EmergencyContactInfo.setText(user.getEmergencyContactInfo());
        EmergencyEdit();


    }

    private void EmergencyEdit() {
        Utilities.hideKeyboard(SettingsActivity.this);
        EditText Emergency=findViewById(R.id.emergencyNameDisplay);
        Emergency.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                EditText Emergency=findViewById(R.id.emergencyNameDisplay);
                EmergencyContactInfoEntered=Emergency.getText().toString();
                CurrentUser.setEmergencyContactInfo(EmergencyContactInfoEntered);
            }

        });
    }


    private void NameEdit(User user) {
        Utilities.hideKeyboard(SettingsActivity.this);
        EditText nameDisplay=findViewById(R.id.nameDisplay);
        nameDisplay.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                EditText nameDisplay=findViewById(R.id.nameDisplay);
                nameEntered=nameDisplay.getText().toString();
                CurrentUser.setName(nameEntered);
            }

        });
    }

    private void EmailEdit() {
        Utilities.hideKeyboard(SettingsActivity.this);
        EditText EmailDisplay=findViewById(R.id.EmailDisplay);
        EmailDisplay.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                EditText EmailDisplay=findViewById(R.id.EmailDisplay);
                emailEntered=EmailDisplay.getText().toString();
                CurrentUser.setEmail(emailEntered);
            }
        });
    }

    private void BirthYearEdit() {
        Utilities.hideKeyboard(SettingsActivity.this);
        EditText BirthYearDisplay=findViewById(R.id.birthYearDisplay);
        BirthYearDisplay.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                EditText BirthYearDisplay=findViewById(R.id.birthYearDisplay);
                birthYearEnteredString=BirthYearDisplay.getText().toString();
                if(!birthYearEnteredString.isEmpty()){
                    birthYearEnteredint=Integer.parseInt(birthYearEnteredString);
                    CurrentUser.setBirthYear(birthYearEnteredint);
                }
            }
        });
    }

    private void BirthMonthEdit() {
        Utilities.hideKeyboard(SettingsActivity.this);
        EditText BirthMonthDisplay=findViewById(R.id.birthMonthDisplay);
        BirthMonthDisplay.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                EditText BirthMonthDisplay=findViewById(R.id.birthMonthDisplay);
                birthMonthEnteredString=BirthMonthDisplay.getText().toString();
                if(!birthMonthEnteredString.isEmpty()){
                    birthMonthEnteredint=Integer.parseInt(birthMonthEnteredString);
                    CurrentUser.setBirthMonth(birthMonthEnteredint);
                }
            }
        });
    }

    private void CellPhoneEdit() {
        Utilities.hideKeyboard(SettingsActivity.this);
        EditText CellPhoneDisplay=findViewById(R.id.cellPhoneDisplay);
        CellPhoneDisplay.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                EditText CellPhoneDisplay=findViewById(R.id.cellPhoneDisplay);
                cellPhoneEnteredString=CellPhoneDisplay.getText().toString();
                CurrentUser.setCellPhone(cellPhoneEnteredString);
            }
        });
    }

    private void HomePhoneEdit() {
        Utilities.hideKeyboard(SettingsActivity.this);
        EditText homePhoneDisplay=findViewById(R.id.homePhoneDisplay);
        homePhoneDisplay.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                EditText homePhoneDisplay=findViewById(R.id.homePhoneDisplay);
                homePhoneEnteredString=homePhoneDisplay.getText().toString();
                CurrentUser.setHomePhone(homePhoneEnteredString);
            }
        });
    }

    private void gradeEdit() {
        Utilities.hideKeyboard(SettingsActivity.this);
        EditText GradeDisplay=findViewById(R.id.currentgradeDisplay);
        GradeDisplay.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                EditText GradeDisplay=findViewById(R.id.currentgradeDisplay);
                gradeEnteredString=GradeDisplay.getText().toString();
                CurrentUser.setGrade(gradeEnteredString);
            }
        });
    }

    private void AddressEdit() {
        Utilities.hideKeyboard(SettingsActivity.this);
        EditText AddressDisplay=findViewById(R.id.adressDisplay);
        AddressDisplay.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                EditText AddressDisplay=findViewById(R.id.adressDisplay);
                AddressEnteredString=AddressDisplay.getText().toString();
                CurrentUser.setAddress(AddressEnteredString);
            }
        });
    }

    private void teacherNameEdit() {
        Utilities.hideKeyboard(SettingsActivity.this);
        EditText TeacherDisplay=findViewById(R.id.teacgersNameDisplay);
        TeacherDisplay.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                EditText TeacherDisplay=findViewById(R.id.teacgersNameDisplay);
                teacherNameEnteredString=TeacherDisplay.getText().toString();
                CurrentUser.setTeacherName(teacherNameEnteredString);
            }
        });
    }

    private void responseEdit(User returnedUser) {
        Toast.makeText(getApplicationContext(), "Edited!", Toast.LENGTH_SHORT).show();
        CurrentUser=returnedUser;
    }

    public static Intent launchIntentSettings(Context context) {
        Intent intentSettings = new Intent(context, SettingsActivity.class);
        return intentSettings;
    }
}
