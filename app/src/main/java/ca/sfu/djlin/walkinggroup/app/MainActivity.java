package ca.sfu.djlin.walkinggroup.app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import ca.sfu.djlin.walkinggroup.R;
import ca.sfu.djlin.walkinggroup.dataobjects.Group;
import ca.sfu.djlin.walkinggroup.dataobjects.GroupCollection;
import ca.sfu.djlin.walkinggroup.model.User;

public class MainActivity extends AppCompatActivity {
    private Group group;
    private User user;
    private GroupCollection groupcollection;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ini();

    }

    private void ini() {
        groupcollection=GroupCollection.getInstance();
        System.out.println(groupcollection.getGroupSize());
        System.out.println(groupcollection.getGroup(0).getName());
        //System.out.println(groupcollection.getGroup(0));

    }
}
