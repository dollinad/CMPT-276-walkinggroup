package ca.sfu.djlin.walkinggroup.app.WelcomeAndSignUp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import ca.sfu.djlin.walkinggroup.R;
import ca.sfu.djlin.walkinggroup.Utilities;
import ca.sfu.djlin.walkinggroup.app.Map.MapActivityDrawer;
import ca.sfu.djlin.walkinggroup.dataobjects.EarnedRewards;
import ca.sfu.djlin.walkinggroup.model.Session;
import ca.sfu.djlin.walkinggroup.model.User;
import ca.sfu.djlin.walkinggroup.proxy.ProxyBuilder;
import ca.sfu.djlin.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

public class SignupActivity extends AppCompatActivity {

    private Session createUser;
    // TO BE REMOVED PRIOR TO SUBMISSION
    private static final String TAG = "SignupActivity";

    WGServerProxy proxy;
    String userNameString;
    String userEmailString;
    String userPasswordString;
    String userConfirmPasswordString;
    String userBirthYearString;
    Integer userBirthYearint;
    String userBirthMonthString;
    Integer userBirthMonthint;
    String userCellPhoneString;
    String userHomePhoneString;
    String userAddressString;
    String userCurrentGradeString;
    String userTeacherNameString;
    String EmergencyContactInfo;
    Integer userCurrentPoints;
    Integer userEarnedPoints;
    EarnedRewards userEarnedRewards;
    Session session;
    static User userToSend;
    Long UserId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        createUser = Session.getSession(getApplicationContext());
        // Build server proxy
        proxy = ProxyBuilder.getProxy(getString(R.string.apikey));

        // Setting up buttons
        setupCreateAccountInputs();
        setupCreateAccount();
    }


    public static Intent launchIntentSignup(Context context) {
        Intent intentSignup = new Intent(context, SignupActivity.class);
        return intentSignup;
    }

    private void setupCreateAccountInputs() {
        // Setup text watcher for user's name
        EditText userName = findViewById(R.id.name_input);
        userName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                EditText userName = findViewById(R.id.name_input);
                userNameString = userName.getText().toString();
            }
        });

        // Hide keyboard when is done typing
        userName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {
                    Utilities.hideKeyboard(SignupActivity.this);
                }
                return false;
            }
        });

        // Hide keyboard on focus change
        userName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    Utilities.hideKeyboardFocus(SignupActivity.this, view);
                }
            }
        });

        // Setup text watcher for user's email
        EditText userEmail = findViewById(R.id.email_input);
        userEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                EditText userEmail = findViewById(R.id.email_input);
                userEmailString = userEmail.getText().toString();
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
                    Utilities.hideKeyboard(SignupActivity.this);
                }
                return false;
            }
        });

        // Hide keyboard on focus change
        userEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    Utilities.hideKeyboardFocus(SignupActivity.this, view);
                }
            }
        });

        // Setup text watcher for user's password
        EditText userPassword = findViewById(R.id.password_input);
        userPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                EditText userPassword = findViewById(R.id.password_input);
                userPasswordString = userPassword.getText().toString();
            }
        });

        // Hide keyboard when is done typing
        userPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {
                    Utilities.hideKeyboard(SignupActivity.this);
                }
                return false;
            }
        });

        // Hide keyboard on focus change
        userPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    Utilities.hideKeyboardFocus(SignupActivity.this, view);
                }
            }
        });

        // Setup text watcher for user's password confirmation
        EditText userConfirmPassword = findViewById(R.id.confirm_password_input);
        userConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                EditText userConfirmPassword = findViewById(R.id.confirm_password_input);
                userConfirmPasswordString = userConfirmPassword.getText().toString();
            }
        });

        // Hide keyboard when is done typing
        userConfirmPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {
                    Utilities.hideKeyboard(SignupActivity.this);
                }
                return false;
            }
        });

        // Hide keyboard on focus change
        userConfirmPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    Utilities.hideKeyboardFocus(SignupActivity.this, view);
                }
            }
        });

        // Setup text watcher for user's birthYear
        EditText userBirthYear = findViewById(R.id.birth_year_input);
        userBirthYear.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                EditText userBirthYear = findViewById(R.id.birth_year_input);
                userBirthYearString = userBirthYear.getText().toString();
                if (!userBirthYearString.isEmpty()) {
                    userBirthYearint = Integer.parseInt(userBirthYearString);
                }
            }
        });

        // Hide keyboard when is done typing
        userBirthYear.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {
                    Utilities.hideKeyboard(SignupActivity.this);
                }
                return false;
            }
        });

        // Hide keyboard on focus change
        userBirthYear.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    Utilities.hideKeyboardFocus(SignupActivity.this, view);
                }
            }
        });


        // Setup text watcher for user's Birth Month
        EditText userBirthMonth = findViewById(R.id.birth_month_input);
        userBirthMonth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                EditText userBirthMonth = findViewById(R.id.birth_month_input);
                userBirthMonthString = userBirthMonth.getText().toString();
                if (!userBirthYearString.isEmpty()) {
                    userBirthMonthint = Integer.parseInt(userBirthMonthString);
                }
            }
        });

        // Hide keyboard when is done typing
        userBirthMonth.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {
                    Utilities.hideKeyboard(SignupActivity.this);
                }
                return false;
            }
        });

        // Hide keyboard on focus change
        userBirthMonth.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    Utilities.hideKeyboardFocus(SignupActivity.this, view);
                }
            }
        });

        // Setup text watcher for user's Celll Phone
        EditText userCellPhone = findViewById(R.id.cellphone_input);
        userCellPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                EditText userCellPhone = findViewById(R.id.cellphone_input);
                userCellPhoneString = userCellPhone.getText().toString();
            }
        });

        // Hide keyboard when is done typing
        userCellPhone.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {
                    Utilities.hideKeyboard(SignupActivity.this);
                }
                return false;
            }
        });

        // Hide keyboard on focus change
        userCellPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    Utilities.hideKeyboardFocus(SignupActivity.this, view);
                }
            }
        });

        // Setup text watcher for user's Home Phone
        EditText userHomePhone = findViewById(R.id.home_phone_input);
        userHomePhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                EditText userHomePhone = findViewById(R.id.home_phone_input);
                userHomePhoneString = userHomePhone.getText().toString();
            }
        });

        // Hide keyboard when is done typing
        userHomePhone.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {
                    Utilities.hideKeyboard(SignupActivity.this);
                }
                return false;
            }
        });

        // Hide keyboard on focus change
        userHomePhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    Utilities.hideKeyboardFocus(SignupActivity.this, view);
                }
            }
        });

        // Setup text watcher for user's address
        EditText userAddress = findViewById(R.id.address_input);
        userAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                EditText userAddress = findViewById(R.id.address_input);
                userAddressString = userAddress.getText().toString();
            }
        });

        // Hide keyboard when is done typing
        userAddress.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {
                    Utilities.hideKeyboard(SignupActivity.this);
                }
                return false;
            }
        });

        // Hide keyboard on focus change
        userAddress.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    Utilities.hideKeyboardFocus(SignupActivity.this, view);
                }
            }
        });

        // Setup text watcher for user's Current Grade
        EditText userGrade = findViewById(R.id.current_grade_input);
        userGrade.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                EditText userGrade = findViewById(R.id.current_grade_input);
                userCurrentGradeString = userGrade.getText().toString();
            }
        });

        // Hide keyboard when is done typing
        userGrade.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {
                    Utilities.hideKeyboard(SignupActivity.this);
                }
                return false;
            }
        });

        // Hide keyboard on focus change
        userGrade.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    Utilities.hideKeyboardFocus(SignupActivity.this, view);
                }
            }
        });

        // Setup text watcher for user's teachers name
        EditText userTeacherName = findViewById(R.id.teacher_name_input);
        userTeacherName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                EditText userTeacherName = findViewById(R.id.teacher_name_input);
                userTeacherNameString = userTeacherName.getText().toString();
            }
        });

        // Hide keyboard when is done typing
        userTeacherName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {
                    Utilities.hideKeyboard(SignupActivity.this);
                }
                return false;
            }
        });

        // Hide keyboard on focus change
        userTeacherName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    Utilities.hideKeyboardFocus(SignupActivity.this, view);
                }
            }
        });

        // Setup text watcher for user's emergency info
        EditText EmergencyName = findViewById(R.id.emergency_contact_input);
        EmergencyName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                EditText EmergencyName = findViewById(R.id.emergency_contact_input);
                EmergencyContactInfo = EmergencyName.getText().toString();
            }
        });

        // Hide keyboard when is done typing
        EmergencyName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {
                    Utilities.hideKeyboard(SignupActivity.this);
                }
                return false;
            }
        });

        // Hide keyboard on focus change
        EmergencyName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    Utilities.hideKeyboardFocus(SignupActivity.this, view);
                }
            }
        });
    }

    private void setupCreateAccount() {
        Button createAccountBtn = findViewById(R.id.create_account);
        createAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create User instance
                User user = new User();


                // Set User information
                user.setName(userNameString);
                user.setEmail(userEmailString);
                user.setPassword(userPasswordString);
                user.setBirthYear(userBirthYearint);
                user.setBirthMonth(userBirthMonthint);
                user.setAddress(userAddressString);
                user.setCellPhone(userCellPhoneString);
                user.setHomePhone(userHomePhoneString);
                user.setGrade(userCurrentGradeString);
                user.setTeacherName(userTeacherNameString);
                user.setEmergencyContactInfo(EmergencyContactInfo);
                user.setCurrentPoints(0);
                user.setTotalPointsEarned(0);
                //EarnedRewards earnedRewards= new EarnedRewards();
                //List<Drawable> earnedRewardsList=Collections.emptyList();
                //earnedRewards.setPossible_stickers(new EarnedRewards());
                user.setRewards(new EarnedRewards());
                //createUser.setUser(user);

                /*
                // Reward system to be implemented at another time
                user.setCurrentPoints(100);
                user.setTotalPointsEarned(2500);
                user.setRewards(new EarnedRewards());
                */

                // Check that passwords match
                if (!userPasswordString.equals(userConfirmPasswordString)) {
                    Toast.makeText(SignupActivity.this, SignupActivity.this.getString(R.string.password_check_toast), Toast.LENGTH_SHORT).show();
                } else {
                    // Make call to server
                    Call<User> caller = proxy.createUser(user);
                    ProxyBuilder.callProxy(SignupActivity.this, caller, returnedUser -> createUserResponse(returnedUser));
                }
            }
        });
    }

    // Create user response from server
    private void createUserResponse(User user) {
        userToSend = user;
        // User creation is successful

        // Grab the current token session
        notifyUserViaLogAndToast("Server replied with user: " + user.toString());

        // Add the user to the current session - currently crashes the app...
        if (user != null) {
            Log.d(TAG, "Attempting to set user to session");
            session.setUser(user);
        }

        // Register callback for token
        ProxyBuilder.setOnTokenReceiveCallback(token -> onReceiveToken(token));

        // Save user information
        saveUserInfo(user);

        // Create instance of user to use for login
        User createdUser = new User();
        createdUser.setEmail(userEmailString);
        createdUser.setPassword(userPasswordString);
        createdUser.setBirthMonth(userBirthMonthint);
        createdUser.setBirthYear(userBirthYearint);
        createdUser.setCellPhone(userCellPhoneString);
        createdUser.setHomePhone(userHomePhoneString);
        createdUser.setAddress(userAddressString);
        createdUser.setGrade(userCurrentGradeString);
        createdUser.setTeacherName(userTeacherNameString);
        createdUser.setEmergencyContactInfo(EmergencyContactInfo);
        createdUser.setCurrentPoints(0);
        createdUser.setTotalPointsEarned(0);
        EarnedRewards earnedRewards= new EarnedRewards();
        //List<Drawable> earnedRewardsList=Collections.emptyList();
        //earnedRewards.setPossible_stickers(null);
        user.setRewards(earnedRewards);
        createUser.setUser(createdUser);

        // Finish the login process
        Call<Void> caller = proxy.login(createdUser);
        ProxyBuilder.callProxy(SignupActivity.this, caller, returnedNothing -> response(returnedNothing));
    }

    // Save user information in Shared Preferences
    private void saveUserInfo(User user) {
        Log.i("REACHED", "LLLLLLLLL");
        SharedPreferences preferences = this.getSharedPreferences("User Session", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("Name", user.getName());
        editor.putString("Email", user.getEmail());
        editor.putLong("User Id", user.getId());
        UserId = user.getId();
        editor.apply();
    }

    private void launchMapActivity() {
        // Launch Map Activity

        Intent mapIntent = MapActivityDrawer.launchIntentMap(SignupActivity.this);
        //mapIntent.putExtra("userId", userId);
        startActivity(mapIntent);
        finish();
    }

    // Handle the token by generating a new Proxy which is encoded with it.
    private void onReceiveToken(String token) {
        // Replace the current proxy with one that uses the token!
        Log.w(TAG, "   --> NOW HAVE TOKEN: " + token);
        proxy = ProxyBuilder.getProxy(getString(R.string.apikey), token);
        createUser.setProxy(proxy);
        //Save token in shared preferences
        saveToken(token);
    }

    // Save token in shared preferences
    private void saveToken(String token) {
        SharedPreferences preferences = this.getSharedPreferences("User Session", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("Token", token);
        editor.apply();
    }

    // Login actually completes by calling this. Launch next activity after response
    private void response(Void returnedNothing) {
        launchMapActivity();
    }

    // Put message up in toast and logcat
    // -----------------------------------------------------------------------------------------
    private void notifyUserViaLogAndToast(String message) {
        Log.w(TAG, message);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public static Session sendUser(Context context, Session session) {
        SharedPreferences preferences = context.getSharedPreferences("User Session", MODE_PRIVATE);
        //Session createUser;

        String token = preferences.getString("Token", "");
        Long Id = preferences.getLong("User Id", 0);
        WGServerProxy proxy;
        proxy = ProxyBuilder.getProxy(context.getString(R.string.apikey), token);
        Call<User> call = proxy.getUserById(Id);
        ProxyBuilder.callProxy(context, call, returnedNothing -> session.setUser(returnedNothing));
        //User b=userToSend;
        session.setProxy(proxy);
        //session.setUser(b);
        return session;
    }

    private static void responseSingleton(User returnedNothing) {
        userToSend = returnedNothing;
    }
}
