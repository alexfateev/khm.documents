package com.himmash.controllers;

import com.himmash.database.DBHandler;
import com.himmash.Main;
import com.himmash.model.DocFiles;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class DocFileSelectController {

    @FXML
    private TextField searchField;

    @FXML
    private Button selectButton;

    @FXML
    private Button cancelButton;

    @FXML
    private TableView<DocFiles> tableDocFiles;

    @FXML
    private TableColumn<DocFiles, String> numberColumn;

    @FXML
    private TableColumn<DocFiles, String> designationColumn;

    @FXML
    private TableColumn<DocFiles, String> nameColumn;

    @FXML
    private TableColumn<DocFiles, String> fileNameColumn;

    private Stage dialogStage;
    private Main mainApp;
    private boolean okClicked = false;

    ObservableList<DocFiles> docFiles = FXCollections.observableArrayList();
    ObservableList<DocFiles> conDocFiles = FXCollections.observableArrayList();
    FilteredList<DocFiles> filteredData;
    DBHandler dbHandler;

    public boolean isOkClicked() {
        return true;
    }

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    @FXML
    public void setConDocFiles(ObservableList<DocFiles> conDocFiles) {
        this.conDocFiles = conDocFiles;
    }

    @FXML
    private void handleSelectFile() {
        if (tableDocFiles.getSelectionModel().getSelectedIndex() >= 0) {
            if (!check(tableDocFiles.getSelectionModel().getSelectedItem())) {
                conDocFiles.add(tableDocFiles.getSelectionModel().getSelectedItem());
                okClicked = true;
                dialogStage.close();
            }

        }
    }

    private boolean check(DocFiles docFile) {
        boolean result = false;
        for (DocFiles file : conDocFiles) {
            if (file.getId() == docFile.getId()) {
                result = true;
                break;
            }
        }
        return result;
    }

    @FXML
    void initialize() {
        dbHandler = new DBHandler();
        docFiles = dbHandler.getDocFiles();
        filteredData = new FilteredList<>(docFiles, p -> true);

        tableDocFiles.setPlaceholder(new Label("Нет содержимого"));
        tableDocFiles.setItems(filteredData);

        numberColumn.setCellValueFactory(c -> c.getValue().docNumberProperty());
        designationColumn.setCellValueFactory(c -> c.getValue().docDesignationProperty());
        nameColumn.setCellValueFactory(c -> c.getValue().docNameProperty());
        fileNameColumn.setCellValueFactory(c -> c.getValue().nameProperty());

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(d -> {
                // If filter text is empty, display all.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                if (d.getDocName().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches first name.
                } else if (d.getDocDesignation().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches last name.
                } else if (d.getDocNumber().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (d.getName().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else
                    return false; // Does not match.
            });
        });

    }
}
