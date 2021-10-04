package com.himmash.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Users {
    private IntegerProperty id;
    private StringProperty name;
    private IntegerProperty accessGroup;

    public Users(int id, String name, int accessGroup) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.accessGroup = new SimpleIntegerProperty(accessGroup);
    }

    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public int getAccessGroup() {
        return accessGroup.get();
    }

    public IntegerProperty accessGroupProperty() {
        return accessGroup;
    }

    public void setAccessGroup(int accessGroup) {
        this.accessGroup.set(accessGroup);
    }
}
