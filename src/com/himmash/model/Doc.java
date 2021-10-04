package com.himmash.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Doc {
    private IntegerProperty id;
    private StringProperty designation;
    private StringProperty name;
    private StringProperty number;
    private StringProperty author;
    private IntegerProperty categoryId;
    private ObservableList<DocFiles> files = FXCollections.observableArrayList();
    private ObservableList<DocFiles> conFiles = FXCollections.observableArrayList();
    private Category category;

    public Doc() {
        this.id =  new SimpleIntegerProperty(0);
        this.designation = new SimpleStringProperty("");
        this.name = new SimpleStringProperty("");
        this.number = new SimpleStringProperty("");
        this.categoryId = new SimpleIntegerProperty(0);
        this.category = null;
        this.author = new SimpleStringProperty("");

    }

    public ObservableList<DocFiles> getConFiles() {
        return conFiles;
    }

    public void setConFiles(ObservableList<DocFiles> conFiles) {
        this.conFiles = conFiles;
    }

    public Doc(int id, String designation, String name, String number,
               int categoryId, Category category, String author ) {
        this.id =  new SimpleIntegerProperty(id);
        this.designation = new SimpleStringProperty(designation);
        this.name = new SimpleStringProperty(name);
        this.number = new SimpleStringProperty(number);
        this.categoryId = new SimpleIntegerProperty(categoryId);
        this.category = category;
        this.author = new SimpleStringProperty(author);
    }

    public String getAuthor() {
        return author.get();
    }

    public StringProperty authorProperty() {
        return author;
    }

    public void setAuthor(String author) {
        this.author.set(author);
    }

    public ObservableList<DocFiles> getFiles() {
        return files;
    }

    public void setFiles(ObservableList<DocFiles> files) {
        this.files = files;
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

    public String getDesignation() {
        return designation.get();
    }

    public StringProperty designationProperty() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation.set(designation);
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

    public String getNumber() {
        return number.get();
    }

    public StringProperty numberProperty() {
        return number;
    }

    public void setNumber(String number) {
        this.number.set(number);
    }

    public int getCategoryId() {
        return categoryId.get();
    }

    public IntegerProperty categoryIdProperty() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId.set(categoryId);
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
