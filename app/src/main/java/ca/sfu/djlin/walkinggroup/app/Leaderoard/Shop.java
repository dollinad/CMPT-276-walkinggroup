package ca.sfu.djlin.walkinggroup.app.Leaderoard;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.DragEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import ca.sfu.djlin.walkinggroup.R;
import ca.sfu.djlin.walkinggroup.dataobjects.EarnedRewards;
import ca.sfu.djlin.walkinggroup.model.Session;
import ca.sfu.djlin.walkinggroup.model.User;
import ca.sfu.djlin.walkinggroup.proxy.ProxyBuilder;
import ca.sfu.djlin.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

public class Shop extends AppCompatActivity{
    WGServerProxy proxy;
    Session data;
    User CurrentUser;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        data=Session.getSession(getApplicationContext());
        proxy=data.getProxy();
        CurrentUser=data.getUser();
        setContentView(R.layout.shop);
        ImageView magic1background=findViewById(R.id.stickerbackground1);
        ImageView magic2background=findViewById(R.id.sticker2background);
        ImageView magic3background=findViewById(R.id.sticke3background);
        ImageView magic4background=findViewById(R.id.sticker4background);
        ImageView magic5background=findViewById(R.id.sticker5background);
        ImageView magic6background=findViewById(R.id.sticker6background);
        EarnedRewards rewards=CurrentUser.getRewards();
        if(rewards!=null){
            List<Integer> alreadyEarned=rewards.getPossible_stickers();
            for(int i=0; i<alreadyEarned.size(); i++) {
                if (alreadyEarned.get(i) == (R.drawable.magic1)) {
                    magic1background.setImageDrawable(getDrawable(R.drawable.magic1));
                } else if (alreadyEarned.get(i) == (R.drawable.magic2)) {
                    magic2background.setImageDrawable(getDrawable(R.drawable.magic2));
                } else if (alreadyEarned.get(i) == (R.drawable.magic3)) {
                    magic3background.setImageDrawable(getDrawable(R.drawable.magic3));
                } else if (alreadyEarned.get(i) == (R.drawable.magic4)) {
                    magic4background.setImageDrawable(getDrawable(R.drawable.magic4));
                } else if (alreadyEarned.get(i) == (R.drawable.magic5)) {
                    magic5background.setImageDrawable(getDrawable(R.drawable.magic5));
                } else if (alreadyEarned.get(i) == (R.drawable.magic6)) {
                    magic6background.setImageDrawable(getDrawable(R.drawable.magic6));
                }
            }
        }

        TextView TotalPoints=findViewById(R.id.totalpointsearnedshop);
        TextView CurrentPoints=findViewById(R.id.currentpointsshop);
        TotalPoints.setText(CurrentUser.getTotalPointsEarned()+"");
        CurrentPoints.setText(CurrentUser.getCurrentPoints()+"");
        ImageView magic1=findViewById(R.id.sticker1);
        ImageView magic2=findViewById(R.id.sticker6);
        ImageView magic3=findViewById(R.id.sticker2);
        ImageView magic4=findViewById(R.id.sticker5);
        ImageView magic5=findViewById(R.id.sticker3);
        ImageView magic6=findViewById(R.id.sticker4);




        magic1background.setOnDragListener(dragListener);
        magic2background.setOnDragListener(dragListener);
        magic3background.setOnDragListener(dragListener);
        magic4background.setOnDragListener(dragListener);
        magic5background.setOnDragListener(dragListener);
        magic6background.setOnDragListener(dragListener);

        magic1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(CurrentUser.getCurrentPoints()>=10){
                    ClipData data=ClipData.newPlainText("magic1","magic1");
                    View.DragShadowBuilder shadowBuilder=new View.DragShadowBuilder(v);
                    v.startDrag(data, shadowBuilder, v, 0);
                    return true;
                }
                else{
                    Toast.makeText(getApplicationContext(), "You currently do not have enough points!", Toast.LENGTH_SHORT).show();
                    return false;
                }

            }
        });

        magic2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(CurrentUser.getCurrentPoints()>=15){
                    ClipData data=ClipData.newPlainText("magic2","magic2");
                    View.DragShadowBuilder shadowBuilder=new View.DragShadowBuilder(v);
                    v.startDrag(data, shadowBuilder, v, 0);
                    return true;
                }
                else {
                    Toast.makeText(getApplicationContext(), "You currently do not have enough points!", Toast.LENGTH_SHORT).show();
                    return false;
                }

            }
        });

        magic3.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(CurrentUser.getCurrentPoints()>=20){
                    ClipData data=ClipData.newPlainText("magic3","magic3");
                    View.DragShadowBuilder shadowBuilder=new View.DragShadowBuilder(v);
                    v.startDrag(data, shadowBuilder, v, 0);
                    return true;
                }
                else {
                    Toast.makeText(getApplicationContext(), "You currently do not have enough points!", Toast.LENGTH_SHORT).show();
                    return false;
                }

            }
        });

        magic4.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(CurrentUser.getCurrentPoints()>=30){
                    ClipData data=ClipData.newPlainText("magic4","magic4");
                    View.DragShadowBuilder shadowBuilder=new View.DragShadowBuilder(v);
                    v.startDrag(data, shadowBuilder, v, 0);
                    return true;
                }
                else {
                    Toast.makeText(getApplicationContext(), "You currently do not have enough points!", Toast.LENGTH_SHORT).show();
                    return false;
                }

            }
        });

        magic5.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(CurrentUser.getCurrentPoints()>=40){
                    ClipData data=ClipData.newPlainText("magic5","magic5");
                    View.DragShadowBuilder shadowBuilder=new View.DragShadowBuilder(v);
                    v.startDrag(data, shadowBuilder, v, 0);
                    return true;
                }
                else {
                    Toast.makeText(getApplicationContext(), "You currently do not have enough points!", Toast.LENGTH_SHORT).show();
                    return false;
                }

            }
        });

        magic6.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(CurrentUser.getCurrentPoints()>=55){
                    ClipData data=ClipData.newPlainText("magic6","magic6");
                    View.DragShadowBuilder shadowBuilder=new View.DragShadowBuilder(v);
                    v.startDrag(data, shadowBuilder, v, 0);
                    return true;
                }
                else {
                    Toast.makeText(getApplicationContext(), "You currently do not have enough points!", Toast.LENGTH_SHORT).show();
                    return false;
                }

            }
        });
    }


    View.OnDragListener dragListener=new View.OnDragListener(){
        EarnedRewards earnedRewardss;

        @Override
        public boolean onDrag(View v, DragEvent event) {
            final int dragAction=event.getAction();
            switch (dragAction) {
                case DragEvent.ACTION_DRAG_ENTERED:
                    View view= (View) event.getLocalState();
                    if(view.getId()==R.id.sticker1){
                        final Animation fadein= AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fadein);
                        ImageView magic1background=findViewById(R.id.stickerbackground1);
                        magic1background.startAnimation(fadein);
                    }
                    if(view.getId()==R.id.sticker2){
                        final Animation fadein= AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fadein);
                        ImageView magic2background=findViewById(R.id.sticker2background);
                        magic2background.startAnimation(fadein);
                    }
                    if(view.getId()==R.id.sticker3){
                        final Animation fadein= AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fadein);
                        ImageView magic3background=findViewById(R.id.sticke3background);
                        magic3background.startAnimation(fadein);
                    }
                    if(view.getId()==R.id.sticker4){
                        final Animation fadein= AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fadein);
                        ImageView magic4background=findViewById(R.id.sticker4background);
                        magic4background.startAnimation(fadein);
                    }
                    if(view.getId()==R.id.sticker5){
                        final Animation fadein= AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fadein);
                        ImageView magic5background=findViewById(R.id.sticker5background);
                        magic5background.startAnimation(fadein);
                    }
                    if(view.getId()==R.id.sticker6){
                        final Animation fadein= AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fadein);
                        ImageView magic6background=findViewById(R.id.sticker6background);
                        magic6background.startAnimation(fadein);
                    }
                    break;
                case  DragEvent.ACTION_DRAG_EXITED:
                    break;
                case DragEvent.ACTION_DROP:
                    view=(View) event.getLocalState();
                    if(view.getId()==R.id.sticker1) {
                        ImageView magic1background = findViewById(R.id.stickerbackground1);
                        magic1background.setImageDrawable(getDrawable(R.drawable.magic1));
                        CurrentUser.setCurrentPoints(CurrentUser.getCurrentPoints() - 10);
                        earnedRewardss=CurrentUser.getRewards();
                        List<Integer> list=earnedRewardss.getPossible_stickers();
                        list.add((R.drawable.magic1));
                        earnedRewardss.setPossible_stickers(list);
                        CurrentUser.setRewards(earnedRewardss);
                        editUser();
                    }
                    if(view.getId()==R.id.sticker2){
                        ImageView magic2background=findViewById(R.id.sticker2background);
                        magic2background.setImageDrawable(getDrawable(R.drawable.magic2));
                        CurrentUser.setCurrentPoints(CurrentUser.getCurrentPoints()-20);
                        earnedRewardss=CurrentUser.getRewards();
                        List<Integer> list=earnedRewardss.getPossible_stickers();
                        list.add((R.drawable.magic2));
                        earnedRewardss.setPossible_stickers(list);
                        CurrentUser.setRewards(earnedRewardss);
                        editUser();
                    }
                    if(view.getId()==R.id.sticker3){
                        ImageView magic3background=findViewById(R.id.sticke3background);
                        magic3background.setImageDrawable(getDrawable(R.drawable.magic3));
                        CurrentUser.setCurrentPoints(CurrentUser.getCurrentPoints()-40);
                        earnedRewardss=CurrentUser.getRewards();
                        List<Integer> list=earnedRewardss.getPossible_stickers();
                        list.add((R.drawable.magic3));
                        earnedRewardss.setPossible_stickers(list);
                        CurrentUser.setRewards(earnedRewardss);
                        editUser();
                    }
                    if(view.getId()==R.id.sticker4){
                        ImageView magic4background=findViewById(R.id.sticker4background);
                        magic4background.setImageDrawable(getDrawable(R.drawable.magic4));
                        CurrentUser.setCurrentPoints(CurrentUser.getCurrentPoints()-55);
                        earnedRewardss=CurrentUser.getRewards();
                        List<Integer> list=earnedRewardss.getPossible_stickers();
                        list.add((R.drawable.magic4));
                        earnedRewardss.setPossible_stickers(list);
                        CurrentUser.setRewards(earnedRewardss);
                        editUser();
                    }
                    if(view.getId()==R.id.sticker5){
                        ImageView magic5background=findViewById(R.id.sticker5background);
                        magic5background.setImageDrawable(getDrawable(R.drawable.magic5));
                        CurrentUser.setCurrentPoints(CurrentUser.getCurrentPoints()-30);
                        earnedRewardss=CurrentUser.getRewards();
                        List<Integer> list=earnedRewardss.getPossible_stickers();
                        list.add((R.drawable.magic5));
                        earnedRewardss.setPossible_stickers(list);
                        CurrentUser.setRewards(earnedRewardss);
                        editUser();
                    }
                    if(view.getId()==R.id.sticker6){
                        ImageView magic6background=findViewById(R.id.sticker6background);
                        magic6background.setImageDrawable(getDrawable(R.drawable.magic6));
                        CurrentUser.setCurrentPoints(CurrentUser.getCurrentPoints()-15);
                        earnedRewardss=CurrentUser.getRewards();
                        List<Integer> list=earnedRewardss.getPossible_stickers();
                        list.add((R.drawable.magic6));
                        earnedRewardss.setPossible_stickers(list);
                        CurrentUser.setRewards(earnedRewardss);
                        editUser();
                    }
                    break;
            }
            return true;
        }
    };

    private void editUser() {
        Call<User> call = proxy.editUser(CurrentUser.getId(), CurrentUser);
        ProxyBuilder.callProxy(Shop.this, call, returnedUser -> responseEdit(returnedUser));
    }
    private void responseEdit(User returnedUser) {
        //session.setUser(returnedUser);
        CurrentUser = returnedUser;
        TextView TotalPoints=findViewById(R.id.totalpointsearnedshop);
        TextView CurrentPoints=findViewById(R.id.currentpointsshop);
        TotalPoints.setText(CurrentUser.getTotalPointsEarned()+"");
        CurrentPoints.setText(CurrentUser.getCurrentPoints()+"");

    }


    public static Intent ShopIntent(Context context) {
        Intent ShopIntent = new Intent(context, Shop.class);
        return ShopIntent;
    }
}
