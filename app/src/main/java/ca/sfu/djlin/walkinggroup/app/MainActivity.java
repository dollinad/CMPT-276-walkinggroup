package ca.sfu.djlin.walkinggroup.app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import ca.sfu.djlin.walkinggroup.R;
import ca.sfu.djlin.walkinggroup.dataobjects.Group;
import ca.sfu.djlin.walkinggroup.model.User;

public class MainActivity extends AppCompatActivity {
    private Group group;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ini();

    }

    private void ini() {
    }
}
