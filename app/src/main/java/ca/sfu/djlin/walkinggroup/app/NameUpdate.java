package ca.sfu.djlin.walkinggroup.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import ca.sfu.djlin.walkinggroup.R;
import ca.sfu.djlin.walkinggroup.model.User;
import ca.sfu.djlin.walkinggroup.proxy.WGServerProxy;

public class NameUpdate extends AppCompatActivity {
    private WGServerProxy proxy;
    String nameEntered;
    User CurrentUser;
    String token;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.updatename);

    }
}
