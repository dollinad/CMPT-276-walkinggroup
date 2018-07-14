package ca.sfu.djlin.walkinggroup.dataobjects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Arrays;
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

    private User leader;
    private ArrayList<User> memberUsers = new ArrayList();
    private String customJson;

    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getLeader() {
        return leader;
    }

    public void setLeader(User leader) {
        this.leader = leader;
    }

    public void setGroupDescription(String groupDescription){
        this.groupDescription=groupDescription;
    }
    public String getGroupDescription(){
        return groupDescription;
    }

    public ArrayList<User> getMemberUsers() {
        return memberUsers;
    }

    public void setMemberUsers(ArrayList<User> memberUsers) {
        this.memberUsers = memberUsers;
    }


    private List<Double> routeLatArray = new ArrayList();
    private List<Double> routeLngArray = new ArrayList();

    //public User getUser(int index){return users.get(index);}
    public void setRouteLatArray(List<Double> latArray){
        this.routeLatArray = latArray;
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

    public String getCustomJson() {
        return customJson;
    }

    public void setCustomJson(String customJson) {
        this.customJson = customJson;
    }

    @Override
    public String toString() {
        return "Group{" +
                "groupDescription='" + groupDescription + '\'' +
                ", routeLatArray=" + routeLatArray +
                ", routeLngArray=" + routeLngArray +
                ", leader=" + leader +
                ", memberUsers=" + memberUsers +
                ", customJson='" + customJson + '\'' +
                ", id=" + id +
                ", hasFullData=" + hasFullData +
                ", href='" + href + '\'' +
                '}';
    }
}
