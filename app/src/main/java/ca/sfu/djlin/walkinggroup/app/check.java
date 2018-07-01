package ca.sfu.djlin.walkinggroup.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import ca.sfu.djlin.walkinggroup.R;
import ca.sfu.djlin.walkinggroup.proxy.WGServerProxy;

//----------------------------------------------------------------------------------------------//
//------THIS FILE ONLY CHECKS IF LOGIN WORKS!!-------

//------TO REMOVE LATER!!--------
//-----------------------------------------------------------------------------------------------//

public class check extends AppCompatActivity {
    String useremail;
    private WGServerProxy proxy;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkifloginworks);

        Intent intent = getIntent();
        useremail = intent.getStringExtra("name");
        Toast.makeText(getApplicationContext(), useremail, Toast.LENGTH_SHORT).show();

    }
}
