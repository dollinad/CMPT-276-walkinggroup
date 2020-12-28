package ca.sfu.djlin.walkinggroup.app.Rewards;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import ca.sfu.djlin.walkinggroup.R;
import ca.sfu.djlin.walkinggroup.app.Leaderoard.Shop;
import ca.sfu.djlin.walkinggroup.model.Session;
import ca.sfu.djlin.walkinggroup.model.User;
import ca.sfu.djlin.walkinggroup.proxy.WGServerProxy;

public class popUpReachedDestination extends AppCompatDialogFragment {
    private WGServerProxy proxy;
    Session data;
    User CurrentUser;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //create the view to show
        View v= LayoutInflater.from(getActivity())
                .inflate(R.layout.popup_reached_destination, null);

        Button positive= v.findViewById(R.id.popup_okay);
        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        data=Session.getSession(getContext());
        proxy=data.getProxy();
        CurrentUser=data.getUser();
        Toast.makeText(getContext(), CurrentUser.getName()+"", Toast.LENGTH_SHORT).show();

        TextView points=v.findViewById(R.id.earnedPoints);
        points.setText(CurrentUser.getTotalPointsEarned()+1+"");
        TextView current=v.findViewById(R.id.CurrentTotalPoints);
        current.setText(CurrentUser.getCurrentPoints()+1+"");


        //create a button listner
        DialogInterface.OnClickListener listener=new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ///Intent pass_intent = com.example.dollina.cmpt_ass3.main_menu.makeIntent(getActivity());
                //startActivity(pass_intent);
            }
        };

        Button shop=v.findViewById(R.id.popup_shop);
        shop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= Shop.ShopIntent(getContext());
                startActivity(intent);
            }
        });

        //build alert dialog
        return  new AlertDialog.Builder(getActivity())
                .setTitle("Congratulations!")
                .setView(v)
                .create();

    }


}

