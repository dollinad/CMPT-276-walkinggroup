package ca.sfu.djlin.walkinggroup.dataobjects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
    private List<User> users=new ArrayList();
    private String name;


    public void addUser(User user){
        users.add(user);
    }
    public void setName(String name){this.name=name;}
    public String getName(){return name;}
    public User getUser(int index){return users.get(index);}


}
