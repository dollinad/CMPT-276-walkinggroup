package ca.sfu.djlin.walkinggroup.app;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import ca.sfu.djlin.walkinggroup.R;
import ca.sfu.djlin.walkinggroup.dataobjects.Group;
import ca.sfu.djlin.walkinggroup.dataobjects.GroupCollection;

public class CreateGroup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        System.out.println("abcdefg");
        setup_create();
        setupbtn_back();

    }

    private void setupbtn_back() {
        Button btn_back=findViewById(R.id.group_btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void setup_create() {
        EditText editText=findViewById(R.id.group_name);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String name=editText.getText().toString();
                Button btn_confirm=findViewById(R.id.group_btn_yes);
                btn_confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Group group=new Group();
                        group.setName(name);
                        GroupCollection.getInstance().addgroups(group);
                        Toast.makeText(CreateGroup.this,"group created",Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(CreateGroup.this,MainActivity.class);
                        startActivity(intent);
                    }
                });

            }
        });

    }

    public static Intent makeintent(Context context){
        Intent intent =new Intent(context, CreateGroup.class);
        return intent;
    }


}
