package ca.sfu.djlin.walkinggroup.app.Leaderboard;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import ca.sfu.djlin.walkinggroup.R;
import ca.sfu.djlin.walkinggroup.model.Session;
import ca.sfu.djlin.walkinggroup.model.User;
import ca.sfu.djlin.walkinggroup.proxy.ProxyBuilder;
import ca.sfu.djlin.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

public class LeaderBoard extends AppCompatActivity{
    Session data;
    WGServerProxy proxy;
    List<User> AllUsers;
    ArrayAdapter<User> adapter;
    int listSize;
    List<User> SortedUsers;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leader_board);
        data=Session.getSession(getApplicationContext());
        proxy=data.getProxy();

        populateListView();

    }

    private void populateListView() {
        Call<List<User>> caller=proxy.getUsers();
        ProxyBuilder.callProxy(LeaderBoard.this, caller, returnedList -> responseList(returnedList));
    }

    private void responseList(List<User> returnedList) {
        AllUsers=returnedList;
        listSize=returnedList.size();
        Sort();
    }

    private void Sort() {
        for(int i=listSize-1; i>=0; i--){
            for(int j=1; j<=i; j++){
                if(AllUsers.get(j-1).getTotalPointsEarned()<AllUsers.get(j).getTotalPointsEarned()){
                    User temp=AllUsers.get(j-1);
                    AllUsers.set(j-1, AllUsers.get(j));
                    AllUsers.set(j, temp);

                }
            }
        }
        adapter=new myListAdapter();
        ListView listView=findViewById(R.id.leader_board_list);
        listView.setAdapter(adapter);

    }

    public static Intent LeaderBoard (Context context) {
        Intent intent = new Intent(context, LeaderBoard.class);
        return intent;
    }

    private class myListAdapter extends ArrayAdapter<User> {
        public myListAdapter(){
            super(LeaderBoard.this, R.layout.layout_leaderboard, AllUsers);
        }
        View itemView;
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            itemView = convertView;
            if(itemView==null){
                itemView=getLayoutInflater().inflate(R.layout.layout_leaderboard, parent,false);
            }
            User highest=AllUsers.get(position);
            TextView name=itemView.findViewById(R.id.list_name_leaderBoard);
            TextView points=itemView.findViewById(R.id.points_leaderBoard);

            Call<User> call=proxy.getUserById(highest.getId());
            ProxyBuilder.callProxy(LeaderBoard.this, call, returnedUser -> respond(returnedUser, name, points));
            return itemView;

    }
        private void respond(User returnedUser, TextView name, TextView points) {
            String fullname=getCorrectName(returnedUser.getName());
            name.setText(fullname);
            points.setText(returnedUser.getTotalPointsEarned()+"");
        }
    }
    public String getCorrectName(String name){
        String returnName="";
        for(int i=0;i<name.length();i++){
            if(name.isEmpty()){
                return "Unknow";
            }
            else if(name.charAt(i)==' '){
                returnName=returnName+' '+name.charAt(i+1);
                return returnName;
            }
            else {
                returnName=returnName+name.charAt(i);
            }
        }

        return returnName;
    }
}

