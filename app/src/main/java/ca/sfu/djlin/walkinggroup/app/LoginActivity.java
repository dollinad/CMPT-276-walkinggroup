package ca.sfu.djlin.walkinggroup.app;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import ca.sfu.djlin.walkinggroup.R;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login_activity);

        //setting user icon
        EditText login_emial=findViewById(R.id.login_email);
        Drawable drawable_loginemail=getResources().getDrawable(R.drawable.user_icon);
        drawable_loginemail.setBounds(0,0, (int) (drawable_loginemail.getIntrinsicHeight()*0.10),
                (int)(drawable_loginemail.getIntrinsicHeight()*0.101));
        login_emial.setCompoundDrawables(drawable_loginemail, null, null, null);

        //setting the password icon
        EditText login_password= findViewById(R.id.login_password);
        Drawable drawable_password=getResources().getDrawable(R.drawable.password);
        drawable_password.setBounds(0,0, (int) (drawable_password.getIntrinsicHeight()*0.05),
                (int)(drawable_password.getIntrinsicHeight()*0.05));
        login_password.setCompoundDrawables(drawable_password, null, null, null);
    }

    public static Intent LaunchIntent_login(Context context) {
        Intent intent_login = new Intent(context, LoginActivity.class);
        return intent_login;

    }
}
