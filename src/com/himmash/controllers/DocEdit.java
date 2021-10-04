package com.himmash.controllers;

import com.himmash.Main;
import com.himmash.model.DocFiles;
import com.himmash.utils.CustomAlert;
import com.himmash.utils.LoadFile;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import com.himmash.database.Config;
import com.himmash.database.DBHandler;
import com.himmash.model.Doc;
import com.himmash.utils.Log;
import com.himmash.utils.Utils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static com.himmash.database.Config.baseDirectory;

public class DocEdit {
    @FXML
    private Button btnSave;

    @FXML
    private Button btnCancel;

    @FXML
    private TextField numberField;

    @FXML
    private TextField designationField;

    @FXML
    private TextField nameField;

    @FXML
    private TableView<DocFiles> tableFiles;

    @FXML
    private TableColumn<DocFiles, String> colFileName;

    @FXML
    private TableColumn<DocFiles, String> colFilePath;

    @FXML
    private Button btnOpen;

    @FXML
    private Button btnLoad;

    @FXML
    private Button btnDelete;

    @FXML
    private TableView<DocFiles> tableConnectFiles;

    @FXML
    private TableColumn<DocFiles, String> colConFilesNumber;

    @FXML
    private TableColumn<DocFiles, String> colConFilesDesignation;

    @FXML
    private TableColumn<DocFiles, String> colConFilesFileName;

    @FXML
    private Button btnConFilesOpen;

    @FXML
    private Button btnConFilesAdd;

    @FXML
    private Button btnConFilesDelete;

    @FXML
    private TextField authorField;

    @FXML
    private HBox toolbarBottom;

    @FXML
    private VBox toolbarFiles;

    @FXML
    private VBox toolbarConFiles;

    private Stage dialogStage;
    private Doc oldDoc;
    private Doc doc;
    private ObservableList<DocFiles> docFiles = FXCollections.observableArrayList();
    private ObservableList<DocFiles> conDocFiles = FXCollections.observableArrayList();
    private boolean okClicked = false;
    private String type;
    private Utils.TypeModeDoc typeMode;
    private Main mainApp;

    List<File> fileList;
    List<File> fileListLoad = new ArrayList<>();
    List<File> fileListDelete = new ArrayList<>();

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    void initialize() {
//        tableConnectFiles.setItems(conDocFiles);
//        colConFilesNumber.setCellValueFactory(cellData -> cellData.getValue().docNumberProperty());
//        colConFilesDesignation.setCellValueFactory(cellData -> cellData.getValue().docDesignationProperty());
//        colConFilesFileName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        tableConnectFiles.setDisable(true);
        btnConFilesOpen.setDisable(true);
        btnConFilesAdd.setDisable(true);
        btnConFilesDelete.setDisable(true);

        colFileName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        Button btnOk = new Button("Закрыть");
        btnOk.setOnAction(e -> handleCancel());

        tableFiles.setPlaceholder(new Label("Нет содержимого"));
        tableConnectFiles.setPlaceholder(new Label("Нет содержимого"));

        btnDelete.setDisable(true);
        btnOpen.setDisable(true);

        btnConFilesOpen.setDisable(true);
        btnConFilesDelete.setDisable(true);

        toolbarFiles.getChildren().clear();
        toolbarConFiles.getChildren().clear();
        toolbarBottom.getChildren().clear();

        if (Config.user == null) {
            toolbarFiles.getChildren().addAll(btnOpen);
            toolbarConFiles.getChildren().addAll(btnConFilesOpen);
            toolbarBottom.getChildren().addAll(btnOk);
        } else {
            toolbarFiles.getChildren().addAll(btnOpen, btnLoad, btnDelete);
            toolbarConFiles.getChildren().addAll(btnConFilesOpen, btnConFilesAdd, btnConFilesDelete);
            toolbarBottom.getChildren().addAll(btnSave, btnCancel);
        }

        tableFiles.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) ->
        {
            btnOpen.setDisable(newSelection == null);
            btnDelete.setDisable(newSelection == null);
        });

        tableConnectFiles.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            btnConFilesOpen.setDisable(newSelection == null);
            btnConFilesDelete.setDisable(newSelection == null);
        });

        tableFiles.setRowFactory(tableView -> {
            final TableRow<DocFiles> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    handleOpenFile();
                }
            });
            return row;
        });

        tableConnectFiles.setRowFactory(tableView -> {
            final TableRow<DocFiles> row = new TableRow<>();
            row.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2 && (!row.isEmpty())) {
                    handleOpenConFile();
                }
            });
            return row;
        });
    }


    @FXML
    private void handleOpenFile() {
        if (tableFiles.getSelectionModel().getSelectedIndex() >= 0) {
            DocFiles docFile = tableFiles.getSelectionModel().getSelectedItem();
            System.out.println("Открываю..." + docFile.getName());
            if (Files.exists(Paths.get(baseDirectory + "\\" + docFile.getDocNumber() + "\\"
                    + docFile.getName()))) {
                openFile(baseDirectory + "\\" + docFile.getDocNumber() + "\\"
                        + docFile.getName());
            } else {
                openFile(docFile.getPath());
                System.out.println("Файл будет загружен при сохранении карточки");
            }
        } else {
            System.out.println("Извините, но ничего не выбрано");
        }
    }

    @FXML
    private void handleOpenConFile() {
        if (tableConnectFiles.getSelectionModel().getSelectedIndex() >= 0) {
            DocFiles docFile = tableConnectFiles.getSelectionModel().getSelectedItem();
            System.out.println("Открываю..." + docFile.getName());
            if (Files.exists(Paths.get(baseDirectory + "\\" + docFile.getDocNumber() + "\\"
                    + docFile.getName()))) {
                openFile(baseDirectory + "\\" + docFile.getDocNumber() + "\\"
                        + docFile.getName());
            } else {
                System.out.println("Файл будет загружен при сохранении карточки");
            }
        } else {
            System.out.println("Извините, но ничего не выбрано");
        }
    }

    @FXML
    private void handleDeleteConFile(){
        //TODO удаленые связанного файла
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setDoc(Doc doc, String type, Utils.TypeModeDoc typeMode) {
        System.out.println(doc.getCategoryId());
        this.type = type;
        this.typeMode = typeMode;
        this.oldDoc = new Doc();
        oldDoc.setNumber(doc.getNumber());
        if (doc == null) {
            this.doc = new Doc(0, "", "", "", 0, null, "");
        } else {
            this.doc = doc;
        }
        numberField.setText(this.doc.getNumber());
        designationField.setText(this.doc.getDesignation());
        nameField.setText(this.doc.getName());
        authorField.setText(this.doc.getAuthor());
        docFiles.addAll(this.doc.getFiles());

        tableConnectFiles.setItems(doc.getConFiles());
        tableFiles.setItems(docFiles);
        checkUser();
    }

    public void checkUser() {
        if (Config.user != null) {
            numberField.setEditable(true);
            designationField.setEditable(true);
            nameField.setEditable(true);
            authorField.setEditable(true);
        } else {
            numberField.setEditable(false);
            designationField.setEditable(false);
            nameField.setEditable(false);
            authorField.setEditable(false);
        }
    }

    public boolean isOkClicked() {
        return okClicked;
    }

    @FXML
    private void handleOk() {
        dialogStage.close();
    }

    @FXML
    private void handleSave() {
        if (isValid()) {
            doc.setNumber(numberField.getText());
            doc.setDesignation(designationField.getText());
            doc.setName(nameField.getText());
            doc.setFiles(docFiles);
            doc.setAuthor(authorField.getText());
            //TODO сохранение связанных файлов

            if (updateCatalog(doc)) {
                okClicked = true;
                dialogStage.close();
            }
        }
    }

    @FXML
    private void handleConFilesAdd() {
        mainApp.showFileDocSelectDialog(conDocFiles);
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    private boolean isValid() {
        String errorMessage = "";
        if (numberField.getText() == null || numberField.getText().length() == 0) {
            errorMessage += "Не указан порядковый № в нулевом указателе.\n";
        }
        if (designationField.getText() == null || designationField.getText().length() == 0) {
            errorMessage += "Не указано обозначение.\n";
        }
        if (nameField.getText() == null || nameField.getText().length() == 0) {
            errorMessage += "Не указанано наименование.\n";
        }
        if (docFiles == null || docFiles.isEmpty()) {
            errorMessage += "Не загружен ни один файал.\n";
        }
        if (type.equals("insert")) {
            errorMessage = isValidOnInsert(errorMessage);
        } else {
            errorMessage = isValidOnUpdate(errorMessage);
        }
        if (errorMessage.length() == 0) {
            return true;
        } else {
            // Показываем сообщение об ошибке.
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.initOwner(dialogStage);
            alert.setTitle("KHM Docs");
            alert.setHeaderText("Пожалуйста, проверте данные");
            alert.setContentText(errorMessage);
            alert.showAndWait();
            return false;
        }
    }

    private String isValidOnInsert(String errorMessage) {
        System.out.println("isValidOnInsert");
        if (new DBHandler().docExistByNumber(numberField.getText())) {
            errorMessage += "Карточка с указанным номером уже существует.\n";
        }
        return errorMessage;
    }

    private String isValidOnUpdate(String errorMessage) {
        System.out.println("isValidOnUpdate");
        if (!oldDoc.getNumber().equals(numberField.getText())) {
            System.out.println("if (!oldDoc.getNumber().equals(numberField.getText()))");
            if (new DBHandler().docExistByNumber(numberField.getText())) {
                errorMessage += "Карточка с указанным номером уже существует.\n";
            }
        }
        return errorMessage;
    }

    private boolean loadFile() {
        boolean result = false;
        LoadFile load = new LoadFile("Load File");
        load.setSourceFiles(fileListLoad);
        load.setPath(baseDirectory + "\\" + numberField.getText());
        load.start();
        try {
            load.join();
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private boolean deleteFile() {
        boolean result = false;
        try {
            for (File file : fileListDelete) {
                Files.delete(file.toPath());
            }
            result = true;
        } catch (Exception e) {
            Log.Log(e.getMessage());
            System.out.println(e.getMessage());
        }
        return result;
    }

    private boolean updateCatalog(Doc doc) {
        boolean result = false;


        if (deleteFile()) {
            if (!oldDoc.getNumber().equals(numberField.getText()) && (typeMode == Utils.TypeModeDoc.EDIT)) {
                //Если карточка редактируется и был изменен номер, то необходимо переименовать папку
                Path src = Paths.get(baseDirectory + "\\" + oldDoc.getNumber());
                Path dst = Paths.get(baseDirectory + "\\" + numberField.getText());
                try {
                    Files.move(src, src.resolveSibling(dst), REPLACE_EXISTING);
                } catch (FileAlreadyExistsException ex) {
                    System.out.println("Target file already exists");
                } catch (IOException ex) {
                    System.out.format("I/O error: %s%n", ex);
                }
            }

            if (loadFile()) {
                result = true;
            }
        }

        return result;
    }

    @FXML
    private void setBtnLoad() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(Config.lastOpenPath));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Все файлы", "*.pdf", "*.doc", "*.docx", "*.djvu"));
        fileList = fileChooser.showOpenMultipleDialog(dialogStage);
        if (fileList != null) {
            for (File file : fileList) {
                Config.lastOpenPath = file.getParent();
                if (containFile(docFiles, file)) {
                    //Если файл с таким именем присутствует в списке, то спрашиваем что с ним делать
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("KHM Docs");
                    alert.setHeaderText("Файл с таким именем уже существует");
                    alert.setContentText("Выберите действие которое необходимо совершить");

                    ButtonType buttonTypeOne = new ButtonType("Заменить", ButtonBar.ButtonData.YES);
                    ButtonType buttonTypeTwo = new ButtonType("Пропустить", ButtonBar.ButtonData.NO);
                    ButtonType buttonTypeCancel = new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE);

                    alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo, buttonTypeCancel);
                    Optional<ButtonType> optional = alert.showAndWait();

                    if (optional.get().getButtonData() == ButtonType.YES.getButtonData()) {
                        //Заменить
                        fileListLoad.add(file);
                    } else if (optional.get().getButtonData() == ButtonType.NO.getButtonData()) {
                        //Пропустить
                    } else {
                        //Отмена
                        break;
                    }
                } else {
                    //Если такого файла нет в списке то добавим его в список и добавим в список загружаемых
                    DocFiles fileDoc = new DocFiles(0, doc.getId(), file.getName(), file.getAbsolutePath());
                    fileDoc.setDocNumber(numberField.getText());
                    docFiles.add(fileDoc);
                    fileListLoad.add(file);
                }
            }
        }
    }

    private boolean containFile(ObservableList<DocFiles> list, File file) {
        /*Проверям не присутствует ли файл с таким же именем в списке уже загруженных
         */
        boolean result = false;
        for (DocFiles docFile : list) {
            if (docFile.getName().equals(file.getName())) {
                result = true;
                break;
            }
        }
        return result;
    }

    @FXML
    private void setBtnDelete() {
        if (tableFiles.getSelectionModel().getSelectedIndex() >= 0) {
            DocFiles docFile = tableFiles.getSelectionModel().getSelectedItem();

            Alert alert = new CustomAlert().alert(Alert.AlertType.CONFIRMATION, "KHM Docs",
                    null, "Вы действительно хотите удалить:\n" +
                            docFile.getName() + " ?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                fileListDelete.add(new File(baseDirectory + "\\" + docFile.getDocNumber() + "\\" +
                        docFile.getName()));
                docFiles.remove(docFile);
            }
        }
    }

    private boolean openFile(String fileName) {
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

}
