package ca.sfu.djlin.walkinggroup.app;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import ca.sfu.djlin.walkinggroup.R;

public class WecomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_activity);

        //setting adduser icon
        Button signup=findViewById(R.id.sign_up);
        Drawable drawable_signup=getResources().getDrawable(R.drawable.adduser);
        drawable_signup.setBounds(0,0, (int) (drawable_signup.getIntrinsicHeight()*0.07),
                                          (int)(drawable_signup.getIntrinsicHeight()*0.07));
        signup.setCompoundDrawables(drawable_signup, null, null, null);

        //setting the login icon
        Button login= findViewById(R.id.login);
        Drawable drawable_login=getResources().getDrawable(R.drawable.login);
        drawable_login.setBounds(0,0, (int) (drawable_login.getIntrinsicHeight()*0.05),
                (int)(drawable_login.getIntrinsicHeight()*0.05));
        login.setCompoundDrawables(drawable_login, null, null, null);

        //SETUP SIGNUP BUTTON
        setupSignup();

        //SETUP LOGIN BUTTON
        setupLogin();

    }

    private void setupSignup(){
        Button signup=findViewById(R.id.sign_up);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signup_intent=SignupActivity.LaunchIntent_signup(WecomeActivity.this);
                startActivity(signup_intent);
            }
        });

    }

    private void setupLogin() {
    }
}
