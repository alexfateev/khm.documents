package com.himmash.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class DocFiles {
    private IntegerProperty id;
    private IntegerProperty docId;
    private StringProperty name;
    private StringProperty path;
    private StringProperty docNumber;
    private StringProperty docName;
    private StringProperty docDesignation;
    private StringProperty docAuthor;

    public DocFiles() {
        this.id = new SimpleIntegerProperty(0);
        this.docId = new SimpleIntegerProperty(0);
        this.name = new SimpleStringProperty("");
        this.path = new SimpleStringProperty("");
        this.docNumber = new SimpleStringProperty("");
        this.docName = new SimpleStringProperty("");
        this.docDesignation = new SimpleStringProperty("");
        this.docAuthor = new SimpleStringProperty("");
    }

    public DocFiles(int id, int docId, String name, String path) {
        this.id = new SimpleIntegerProperty(id);
        this.docId = new SimpleIntegerProperty(docId);
        this.name = new SimpleStringProperty(name);
        this.path = new SimpleStringProperty(path);
        this.docNumber = new SimpleStringProperty("");
        this.docName = new SimpleStringProperty("");
        this.docDesignation = new SimpleStringProperty("");
        this.docAuthor = new SimpleStringProperty("");
    }

    public String getDocNumber() {
        return docNumber.get();
    }

    public StringProperty docNumberProperty() {
        return docNumber;
    }

    public void setDocNumber(String docNumber) {
        this.docNumber.set(docNumber);
    }

    public String getDocName() {
        return docName.get();
    }

    public StringProperty docNameProperty() {
        return docName;
    }

    public void setDocName(String docName) {
        this.docName.set(docName);
    }

    public String getDocDesignation() {
        return docDesignation.get();
    }

    public StringProperty docDesignationProperty() {
        return docDesignation;
    }

    public void setDocDesignation(String docDesignation) {
        this.docDesignation.set(docDesignation);
    }

    public String getDocAuthor() {
        return docAuthor.get();
    }

    public StringProperty docAuthorProperty() {
        return docAuthor;
    }

    public void setDocAuthor(String docAuthor) {
        this.docAuthor.set(docAuthor);
    }

    @Override
    public String toString() {
        return id.get() + "|" + docId.get()+"|"+name.get()+"|"+path.get();
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

    public int getDocId() {
        return docId.get();
    }

    public IntegerProperty docIdProperty() {
        return docId;
    }

    public void setDocId(int docId) {
        this.docId.set(docId);
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

    public String getPath() {
        return path.get();
    }

    public StringProperty pathProperty() {
        return path;
    }

    public void setPath(String path) {
        this.path.set(path);
    }
}
