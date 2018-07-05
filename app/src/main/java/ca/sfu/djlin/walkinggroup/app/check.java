package ca.sfu.djlin.walkinggroup.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;

import ca.sfu.djlin.walkinggroup.R;
import ca.sfu.djlin.walkinggroup.model.User;
import ca.sfu.djlin.walkinggroup.proxy.ProxyBuilder;
import ca.sfu.djlin.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

//----------------------------------------------------------------------------------------------//
//------THIS FILE ONLY CHECKS IF LOGIN WORKS!!-------

//------TO REMOVE LATER!!--------
//-----------------------------------------------------------------------------------------------//

public class check extends AppCompatActivity {
    String useremail;
    private WGServerProxy proxy;
    String token;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkifloginworks);



        Intent intent = getIntent();
        useremail = intent.getStringExtra("email");
        System.out.println("abcooo"+useremail);
        token=intent.getStringExtra("token");
        proxy = ProxyBuilder.getProxy(getString(R.string.apikey), token);
        Toast.makeText(getApplicationContext(), useremail, Toast.LENGTH_SHORT).show();
        setupbtn_back();
        setupbtn_create();

    }

    private void setupbtn_create() {
        Button btn_create=findViewById(R.id.login_create);
        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(check.this,MapActivity.class);
                intent.putExtra("token",token);
                intent.putExtra("email",useremail);
                startActivity(intent);


            }
        });
    }

    private void setupbtn_back() {
        Button btn_back=findViewById(R.id.login_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
/*
    private void test() {
        Button btn = findViewById(R.id.login_create);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Make call
                Call<List<User>> caller = proxy.getUsers();
                ProxyBuilder.callProxy(check.this, caller, returnedUsers -> response(returnedUsers));
            }
        });
    }

    private void response(List<User> returnedUsers) {
        notifyUserViaLogAndToast("Got list of " + returnedUsers.size() + " users! See logcat.");
        Log.i("aa", "All Users:");
        for (User user : returnedUsers) {
            Log.i("aa", "    User: " + user.toString());
        }
    }

    private void notifyUserViaLogAndToast(String message) {
        Log.i("aa", message);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
    */
}
