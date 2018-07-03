package ca.sfu.djlin.walkinggroup.dataobjects;

import java.util.ArrayList;
import java.util.List;

public class GroupCollection {
    private static GroupCollection instance;
    private List<Group> group=new ArrayList();
    private int groupSize=0;


    public static GroupCollection getInstance(){
        if(instance==null){
            instance=new GroupCollection();
        }
        return instance;
    }


    public void addgroups(Group groups) {
        group.add(groups);
        groupSize++;
    }
    public void setGroupSize(int increse){
        groupSize=groupSize+increse;
    }
    public int getGroupSize(){
        return groupSize;
    }
    public Group getGroup(int index){
        return group.get(index);

    }
}
