package ca.sfu.djlin.walkinggroup.app;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import ca.sfu.djlin.walkinggroup.R;

public class ReadMeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_me);

        setUpBack();
    }

    private void setUpBack() {
        Button btn=findViewById(R.id.readme_back);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    public static Intent launchReadMe(Context context) {
        Intent intent = new Intent(context, ReadMeActivity.class);
        return intent;
    }
}
