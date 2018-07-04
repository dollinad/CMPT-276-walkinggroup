package ca.sfu.djlin.walkinggroup.dataobjects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import ca.sfu.djlin.walkinggroup.model.User;
/**
 * Store information about the walking groups.
 *
 * WARNING: INCOMPLETE! Server returns more information than this.
 * This is just to be a placeholder and inspire you how to do it.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Group extends IdItemBase{




    private String groupDescription;
    private Long leader;





    private List<User> users=new ArrayList();
    private LatLng latLng;



    public void addUser(User user){
        users.add(user);
    }
    public void setName(String name){this.groupDescription=name;}
    public String getName(){return groupDescription;}
    public User getUser(int index){return users.get(index);}
    public void setMarker(LatLng latLng){
        this.latLng=latLng;
    }
    public void setLeader(Long leader ){
        this.leader=leader;
    }
    public long getLeader(){return leader;}




}
