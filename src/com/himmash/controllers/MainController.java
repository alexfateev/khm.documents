package com.himmash.controllers;

import com.himmash.utils.CustomAlert;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import com.himmash.database.Config;
import com.himmash.database.DBHandler;
import com.himmash.Main;
import com.himmash.model.Category;
import com.himmash.model.Doc;
import com.himmash.model.DocFiles;
import com.himmash.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

public class MainController {
    @FXML
    private Button btnRefresh;

    @FXML
    private MenuItem miCatAdd;

    @FXML
    private MenuItem miCatEdit;

    @FXML
    private MenuItem miCatDelete;

    @FXML
    private MenuItem miDocAdd;

    @FXML
    private MenuItem miDocEdit;

    @FXML
    private MenuItem miDocDelete;

    @FXML
    private Button btnOpenCard;

    @FXML
    private TextField textSearch;

    @FXML
    private Button btnReset;

    @FXML
    private TreeTableView<Category> tableCategory;

    @FXML
    private TreeTableColumn<Category, String> colCategoryName;

    @FXML
    private TableView<Doc> tableDoc;

    @FXML
    private TableColumn<Doc, String> colDocNumber;

    @FXML
    private TableColumn<Doc, String> colDocDesignation;

    @FXML
    private TableColumn<Doc, String> colDocName;

    @FXML
    private TableColumn<Doc, String> colDocFile;

    @FXML
    private Label statusField;

    @FXML
    private HBox toolbar;

    @FXML
    private MenuButton btnCategory;

    @FXML
    private MenuButton btnCards;

    @FXML
    private TableView<DocFiles> tableFiles;

    @FXML
    private TableColumn<DocFiles, String> colFile;


    ObservableList<Doc> docs = FXCollections.observableArrayList();
    FilteredList<Doc> filteredData;
    DBHandler dbHandler;

    private Main mainApp;

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }

    public void setInterface() {
        toolbar.getChildren().clear();
        btnOpenCard.setDisable(true);
        miDocEdit.setDisable(true);
        miDocDelete.setDisable(true);
        if (Config.user == null) {
            toolbar.getChildren().addAll(btnRefresh, btnOpenCard, textSearch, btnReset);
        } else {
            toolbar.getChildren().addAll(btnRefresh, btnCategory, btnCards, btnOpenCard, textSearch, btnReset);
        }
    }

    @FXML
    private void refreshDate() {
        docs = dbHandler.getDoc();
        filteredData = new FilteredList<>(docs, p -> true);
        tableDoc.setItems(filteredData);
        tableCategory.setRoot(dbHandler.getCategoryTree());
    }

    public void checkAllFiles() {
        int count = 0;
        ObservableList<Doc> docs = FXCollections.observableArrayList();
        ObservableList<DocFiles> docFiles = FXCollections.observableArrayList();
        docs = dbHandler.getDoc();
        docFiles = dbHandler.getDocFiles();
        for (DocFiles file : docFiles) {
            if (Files.exists(Paths.get(Config.baseDirectory + "\\" + file.getDocNumber() + "\\" + file.getName()))) {
//                System.out.println(file.toString() + " не существует");
                count++;
            }
        }
        System.out.printf("Проверка файлов: %s из %s", count, docFiles.size());
    }

    private void openFile(String fileName) {
//        Desktop desktop = null;
//        if (Desktop.isDesktopSupported()) {
//            desktop = Desktop.getDesktop();
//        }
//        try {
//            desktop.open(new File(fileName));
//        } catch (IOException ioe) {
//            new CustomAlert(Alert.AlertType.ERROR, "KHM Docs", "Ошибка при открытии файла", ioe.getMessage());
//        }
        Main.openFile(fileName);
    }

    private void updateMenu(ContextMenu menu, List<DocFiles> files, Doc doc) {
        menu.getItems().clear();
        MenuItem addMenuItem = new MenuItem("Создать");
        MenuItem editMenuItem = new MenuItem("Редактировать");
        MenuItem deleteMenuItem = new MenuItem("Удалить");
        Menu openMenuItem = new Menu("Открыть");
        addMenuItem.setOnAction(e -> setMiDocAdd());
        editMenuItem.setOnAction(e -> setMiDocEdit());
        deleteMenuItem.setOnAction(e -> setMiDocDelete());

        for (DocFiles file : files) {

            MenuItem item = new MenuItem(file.getName());
            openMenuItem.getItems().add(item);
            item.setOnAction(e -> {
                openFile(Config.baseDirectory + "\\" + doc.getNumber() + "\\" + file.getName());
            });

        }
        menu.getItems().addAll(addMenuItem, editMenuItem, deleteMenuItem, openMenuItem);
//        menu.getItems().addAll(addMenuItem, editMenuItem, deleteMenuItem);

    }

    private void setTableDocParam() {

        tableDoc.setPlaceholder(new Label("Нет содержимого"));

        tableCategory.setRowFactory(ttv -> {
            ContextMenu contextMenu = new ContextMenu();
            MenuItem createMenuItem = new MenuItem("Создать");
            MenuItem editMenuItem = new MenuItem("Изменить");
            MenuItem deleteMenuItem = new MenuItem("Удалить");
            contextMenu.getItems().addAll(createMenuItem, editMenuItem, deleteMenuItem);
            TreeTableRow<Category> row = new TreeTableRow<Category>() {
                @Override
                public void updateItem(Category item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setContextMenu(null);
                    } else {
                        if (item.getPid() == 0) {
                            editMenuItem.setDisable(true);
                            deleteMenuItem.setDisable(true);
                        } else {
                            editMenuItem.setDisable(false);
                            editMenuItem.setDisable(false);
                        }
                    }
                    if (Config.user != null) {
                        setContextMenu(contextMenu);
                    } else {
                        setContextMenu(null);
                    }
                }

            };
            createMenuItem.setOnAction(evt -> setMiCatAdd());
            editMenuItem.setOnAction(evt -> setMiCatEdit());
            deleteMenuItem.setOnAction(evt -> setMiCatDelete());
            return row;
        });

        tableDoc.getSelectionModel().

                selectedItemProperty().

                addListener((obs, oldSelection, newSelection) ->

                {
                    if (newSelection != null) {
                        btnOpenCard.setDisable(false);
                        miDocEdit.setDisable(false);
                        miDocDelete.setDisable(false);
                    } else {
                        btnOpenCard.setDisable(true);
                        miDocEdit.setDisable(true);
                        miDocDelete.setDisable(true);
                    }
                });

        tableDoc.setRowFactory(tableView ->

        {
            final TableRow<Doc> row = new TableRow<>();
            final ContextMenu contextMenu = new ContextMenu();
            ListChangeListener<DocFiles> changeListener = (ListChangeListener.Change<? extends DocFiles> c) ->
                    updateMenu(contextMenu, row.getItem().getFiles(), row.getItem());

            row.itemProperty().addListener((obs, oldItem, newItem) -> {
                if (oldItem != null) {
                    oldItem.getFiles().removeListener(changeListener);
                }
                if (newItem == null) {
                    contextMenu.getItems().clear();
                } else {
                    newItem.getFiles().addListener(changeListener);
                    updateMenu(contextMenu, newItem.getFiles(), row.getItem());
                }
            });


            //contextMenu.getItems().addAll(openMenuItem, addMenuItem, editMenuItem, deleteMenuItem);
            // Set context menu on row, but use a binding to make it only show for non-empty rows:
            if (Config.user != null) {
                row.contextMenuProperty().bind(
                        Bindings.when(row.emptyProperty())
                                .then((ContextMenu) null)
                                .otherwise(contextMenu)
                );
            }


            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    setMiDocEdit();
                }
            });


            return row;
        });

        tableCategory.getSelectionModel().

                selectedItemProperty().

                addListener((obs, oldSelection, newSelection) ->

                {

                    if (newSelection != null) {
                        if (newSelection == tableCategory.getRoot()) {
                            filteredData.setPredicate(t -> {
                                miCatAdd.setDisable(false);
                                miCatEdit.setDisable(true);
                                miCatDelete.setDisable(true);
                                return true;
                            });
                        } else {
                            filteredData.setPredicate(t -> {
                                miCatAdd.setDisable(false);
                                miCatEdit.setDisable(false);
                                miCatDelete.setDisable(false);
                                return t.getCategoryId() == newSelection.getValue().getId();
                            });
                        }
                    } else {
                        //btnOpenFile.setDisable(true);
                    }
                });
    }

    @FXML
    void initialize() {
        setInterface();


        dbHandler = Main.getDbHandler();

        checkAllFiles();

        colDocNumber.setCellValueFactory(cellData -> cellData.getValue().numberProperty());
        colDocDesignation.setCellValueFactory(cellData -> cellData.getValue().designationProperty());
        colDocName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());

        colCategoryName.setCellValueFactory(c -> c.getValue().getValue().nameProperty());

        setTableDocParam();

        textSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(d -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Compare first name and last name of every person with filter text.
                String lowerCaseFilter = newValue.toLowerCase();

                if (d.getName().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches first name.
                } else if (d.getDesignation().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches last name.
                } else if (d.getNumber().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false; // Does not match.
            });
        });

        refreshDate();
    }

    @FXML
    private void setBtnReset() {
        textSearch.setText("");
    }

    @FXML
    private void setMiCatDelete() {
        if (tableCategory.getSelectionModel().getSelectedIndex() < 0) {
            new CustomAlert(Alert.AlertType.WARNING, "KHM Docs",
                    null, "Ничего не выбрано. Пожалуйста, выберите сначала категорию.");
        } else if (tableCategory.getSelectionModel().getSelectedItem() == tableCategory.getRoot()) {
            new CustomAlert(Alert.AlertType.WARNING, "KHM Docs",
                    null, "Невозможно удалить корневой элемент.");
        } else if (tableCategory.getSelectionModel().getSelectedItem().getChildren().size() != 0) {
            new CustomAlert(Alert.AlertType.WARNING, "KHM Docs",
                    null, "Невозможно удалить раздел содержащий другие подразделы.");
        } else if (filteredData.size() != 0) {
            new CustomAlert(Alert.AlertType.WARNING, "Невозможно удалить раздел содержащий карточки.");
        } else {
            Alert alert = new CustomAlert().alert(Alert.AlertType.CONFIRMATION, "KHM Docs",
                    null, "Вы действительно хотите удалить:\n" +
                            tableCategory.getSelectionModel().getSelectedItem().getValue().getName() + " ?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                TreeItem<Category> selectedItem = tableCategory.getSelectionModel().getSelectedItem();
                Category category = selectedItem.getValue();
                if (dbHandler.categoryDelete(category)) {
                    if (selectedItem.getParent().getChildren().remove(selectedItem)) {

                    }
                    ;

                }

                //dbHandler.categoryDelete(category);
            }
        }
    }

    @FXML
    private void setMiCatAdd() {
        if (tableCategory.getSelectionModel().getSelectedIndex() >= 0) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("KHM Docs");
            //dialog.setHeaderText("Создание новой категории");
            dialog.setHeaderText(null);
            dialog.setGraphic(null);
            dialog.setContentText("Введите наименование:");

// Traditional way to get the response value.
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(name -> {
                if (name.trim().length() > 0) {
                    TreeItem<Category> selectedItem = tableCategory.getSelectionModel().getSelectedItem();
                    Category category = new Category(0, selectedItem.getValue().getId(), name);
                    category.setId(dbHandler.categoryAdd(category));
                    TreeItem<Category> item = new TreeItem<>(category);
                    selectedItem.getChildren().add(item);
                }
            });
        } else {
            new CustomAlert(Alert.AlertType.WARNING,
                    "Ничего не выбрано. Пожалуйста, выберите сначала категорию.");
        }
    }

    @FXML
    private void setMiCatEdit() {
        if (tableCategory.getSelectionModel().getSelectedItem() == tableCategory.getRoot()) {
            new CustomAlert(Alert.AlertType.WARNING,
                    "Невозможно редактировать корневой элемент.");
        } else if (tableCategory.getSelectionModel().getSelectedIndex() < 0) {
            new CustomAlert(Alert.AlertType.WARNING,
                    "Ничего не выбрано. Пожалуйста, выберите сначала категорию.");
        } else {
            TextInputDialog dialog = new TextInputDialog(tableCategory.getSelectionModel().getSelectedItem().getValue().getName());
            dialog.setTitle("KHM Docs");
            dialog.setHeaderText(null);
            dialog.setGraphic(null);
            dialog.setContentText("Введите наименование:");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(name -> {
                if (name.trim().length() > 0) {
                    tableCategory.getSelectionModel().getSelectedItem().getValue().setName(name);
                    dbHandler.categoryEdit(tableCategory.getSelectionModel().getSelectedItem().getValue());
                }
            });
        }
    }

    @FXML
    private void setMiDocAdd() {
        if (tableCategory.getSelectionModel().getSelectedIndex() >= 0) {
            Doc doc = new Doc();
            doc.setCategoryId(tableCategory.getSelectionModel().getSelectedItem().getValue().getId());
            boolean okClicked = mainApp.showDocEditDialog(doc, "insert", Utils.TypeModeDoc.INSERT);
            System.out.println(doc.getCategoryId());
            if (okClicked) {
                dbHandler.docInsert(doc, null);
                docs.add(doc);
            }
        } else {
            new CustomAlert(Alert.AlertType.INFORMATION, "KHM Docs", null, "Сначала выберите категорию.");
        }
    }

    @FXML
    private void setMiDocEdit() {
        Doc selectedDoc = tableDoc.getSelectionModel().getSelectedItem();
        if (selectedDoc != null) {
            boolean okClicked = mainApp.showDocEditDialog(selectedDoc, "update", Utils.TypeModeDoc.EDIT);
            if (okClicked) {
                dbHandler.docUpdate(selectedDoc);
            }

        } else {
            // Ничего не выбрано.
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.initOwner(mainApp.getPrimaryStage());
            alert.setTitle("No Selection");
            alert.setHeaderText("No Person Selected");
            alert.setContentText("Please select a person in the table.");
            alert.showAndWait();
        }
    }

    @FXML
    private void setMiDocDelete() {
        int selectedIndex = tableDoc.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("KHM Docs");
            alert.setHeaderText(null);
            alert.setContentText("Вы действительно хотите удалить:\n" +
                    tableDoc.getSelectionModel().getSelectedItem().getName() + " ?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                //int index = tableDoc.getSelectionModel().getSelectedIndex();
                Doc doc = tableDoc.getSelectionModel().getSelectedItem();
                docs.remove(doc);
                dbHandler.docDelete(doc);
                deleteDirectory(new File(Config.baseDirectory + "\\" + doc.getNumber()));
            } else {
                // ... user chose CANCEL or closed the dialog
            }
        } else {
            // Ничего не выбрано.
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.initOwner(mainApp.getPrimaryStage());
            alert.setTitle("KHM Docs");
            alert.setHeaderText(null);
            alert.setContentText("Ничего не выбрано. Пожалуйста, выберите сначала документ.");
            alert.showAndWait();
        }
    }

    private boolean deleteDirectory(File file) {
        boolean result = false;
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                try {
                    Files.deleteIfExists(f.toPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                Files.deleteIfExists(file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    @FXML
    private void setBtnOpenFile() throws IOException {
//        System.out.println(System.getProperty("java.io.tmpdir"));
        setMiDocEdit();

    }
}
