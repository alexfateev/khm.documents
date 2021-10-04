package com.himmash;

import com.himmash.controllers.DocEdit;
import com.himmash.controllers.DocFileSelectController;
import com.himmash.controllers.MainController;
import com.himmash.database.Config;
import com.himmash.database.DBHandler;
import com.himmash.model.Doc;
import com.himmash.model.DocFiles;
import com.himmash.utils.CustomAlert;
import com.himmash.utils.Utils;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class Main extends Application {
    private static DBHandler dbHandler;
    private Stage primaryStage;
    private BorderPane rootLayout;

    public static DBHandler getDbHandler() {
        return dbHandler;
    }

    public void loadProperties() {
        try (InputStream is = new FileInputStream("src/com/himmash/config.properties")) {
            Properties properties = new Properties();
            properties.load(is);
            Config.dbHost = properties.getProperty("db.Host");
            Config.dbPort = properties.getProperty("db.Port");
            Config.dbName = properties.getProperty("db.Name");
            Config.dbUser = properties.getProperty("db.User");
            Config.dbPass = properties.getProperty("db.Pass");
            Config.dbParam = properties.getProperty("db.Param");
        } catch (IOException e) {
            new CustomAlert(Alert.AlertType.ERROR, null, e.getMessage());
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        loadProperties();
        dbHandler = new DBHandler();

        this.primaryStage = primaryStage;
        this.primaryStage.setTitle(Config.programTitle);
        initRootLayout();

    }

    static public boolean openFile(String fileName) {
        File file = new File(fileName);
        Path dst = Paths.get(System.getProperty("java.io.tmpdir") + "\\" + file.getName());
        Path src = Paths.get(fileName);
        try {
            Files.copy(src, dst, REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Desktop desktop = null;
        boolean result = false;
        if (Desktop.isDesktopSupported()) {
            desktop = Desktop.getDesktop();
        }
        try {
            desktop.open(dst.toFile());
            dst.toFile().deleteOnExit();
            result = true;
        } catch (IOException ioe) {
            new CustomAlert(Alert.AlertType.ERROR, "KHM Docs", "Ошибка при открытии файла", ioe.getMessage());
        }
        return result;
    }

    public void initRootLayout() {
        try {
            System.out.println("Загружаем корневой макет из fxml файла.");
            // Загружаем корневой макет из fxml файла.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("views/main.fxml"));
            rootLayout = (BorderPane) loader.load();

            // Отображаем сцену, содержащую корневой макет.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
            MainController controller = loader.getController();
            controller.setMainApp(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }

    public boolean showDocEditDialog(Doc doc, String type, Utils.TypeModeDoc typeMode) {
        try {
            // Загружаем fxml-файл и создаём новую сцену
            // для всплывающего диалогового окна.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("Views/DocEdit.fxml"));
            Parent root = loader.load();

            // Создаём диалоговое окно Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Редактирование карточки");
            dialogStage.setResizable(false);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            // Передаём адресата в контроллер.
            DocEdit controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setDoc(doc, type, typeMode);
            controller.setMainApp(this);

            // Отображаем диалоговое окно и ждём, пока пользователь его не закроет
            dialogStage.showAndWait();

            return controller.isOkClicked();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean showFileDocSelectDialog(ObservableList<DocFiles> docFiles) {
        try {
            // Загружаем fxml-файл и создаём новую сцену
            // для всплывающего диалогового окна.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("Views/DocFileSelect.fxml"));
            Parent root = loader.load();

            // Создаём диалоговое окно Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Выбор файла");
            dialogStage.setResizable(false);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            // Передаём адресата в контроллер.
            DocFileSelectController controller = loader.getController();
            controller.setMainApp(this);
            controller.setConDocFiles(docFiles);
            controller.setDialogStage(dialogStage);
//            controller.setDoc(doc, type, typeMode);

            // Отображаем диалоговое окно и ждём, пока пользователь его не закроет
            dialogStage.showAndWait();

//            return controller.isOkClicked();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
