package com.canoo.dolphin.workflow.server.model;

import com.canoo.dolphin.collections.ObservableList;
import com.canoo.dolphin.mapping.DolphinBean;
import com.canoo.dolphin.mapping.Property;

@DolphinBean("Activity")
public class Activity {

    private Property<String> id;
    private Property<String> activityName;
    private Property<String> description;
    private ObservableList<Activity> outgoingActivityIds;
    private Property<String> type;

    public String getId() {
        return id.get();
    }

    public void setId(String id) {
        this.id.set(id);
    }

    public String getActivityName() {
        return activityName.get();
    }

    public void setActivityName(String activityName) {
        this.activityName.set(activityName);
    }

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public ObservableList<Activity> getOutgoingActivityIds() {
        return outgoingActivityIds;
    }

    public String getType() {
        return type.get();
    }

    public void setType(String type) {
        this.type.set(type);
    }
}
