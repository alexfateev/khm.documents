package com.himmash.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Category {
    private IntegerProperty id;
    private StringProperty name;
    private IntegerProperty pid;

    public Category(String name) {
        this.id = new SimpleIntegerProperty(0);
        this.pid = new SimpleIntegerProperty(0);
        this.name = new SimpleStringProperty(name);
    }

    public Category(int id, int pid, String name) {
        this.id = new SimpleIntegerProperty(id);
        this.pid = new SimpleIntegerProperty(pid);
        this.name = new SimpleStringProperty(name);
    }

    public int getPid() {
        return pid.get();
    }

    public IntegerProperty pidProperty() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid.set(pid);
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
}
