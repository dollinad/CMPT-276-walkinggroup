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

    // Start Daniel's Testing Playground
    private Long id;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }
    // End Daniel's Testing Playground

    private String groupDescription;

    //private Long leaderId;

    private List<Double> routeLatArray=new ArrayList();
    private List<Double> routeLngArray=new ArrayList();



    //private List<User> memberOfGroups=new ArrayList();




    //public void addUser(User user){
    //    users.add(user);
    //}
    public void setGroupDescription(String groupDescription){this.groupDescription=groupDescription;}
    public String getGroupDescription(){return groupDescription;}
    //public User getUser(int index){return users.get(index);}
    public void setRouteLatArray(List<Double> latArray){

        this.routeLatArray=latArray;
    }
    public void setRouteLngArray(List<Double> lngArray){

        this.routeLngArray=lngArray;
    }
    public List<Double> getRouteLatArray(){
        return routeLatArray;
    }
    public List<Double> getRouteLngArray(){
        return routeLngArray;
    }



    //public void setLeader(Long leader ){
    //    this.leader=leader;
   // }
    //public long getLeader(){return leader;}




}
