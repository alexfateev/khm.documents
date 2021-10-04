package com.himmash.utils;

import javafx.scene.control.Alert;

public class CustomAlert {
    public CustomAlert() {
    }

    public CustomAlert(Alert.AlertType type, String title, String headText, String message) {
        Log.Log(message);
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(headText);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public CustomAlert(Alert.AlertType type, String headText, String message) {
        Log.Log(message);
        Alert alert = new Alert(type);
        alert.setTitle("KHM Docs");
        alert.setHeaderText(headText);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public CustomAlert(Alert.AlertType type, String message) {
        Log.Log(message);
        Alert alert = new Alert(type);
        alert.setTitle("KHM Docs");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public Alert alert(Alert.AlertType type, String title, String headText, String message) {
        Log.Log(message);
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(headText);
        alert.setContentText(message);
        //alert.showAndWait();
        return alert;
    }


}
