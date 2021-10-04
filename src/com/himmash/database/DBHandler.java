package com.himmash.database;

import com.himmash.model.Category;
import com.himmash.model.Doc;
import com.himmash.model.DocFiles;
import com.himmash.model.Users;
import com.himmash.utils.CustomAlert;
import com.himmash.utils.Log;
import com.himmash.utils.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.Alert;
import javafx.scene.control.TreeItem;

import java.io.File;
import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBHandler extends Config {
    private Connection connection = null;

    private ConnectionBuilder builder = new SimpleConnectionBuilder();

    private Connection getConnect() throws SQLException {
        return builder.getConnection();
    }

    public DBHandler() {
        System.out.println("dbHandler");
        connection = getConnection();
        loadParameters();
        user = getUser(Utils.getFullUserName());
    }

    public boolean isUserAdmin(String userName) {
        boolean result = false;
        String query = "SELECT * FROM " + Const.TABLE_USERS + " WHERE " + Const.USER_NAME + "=?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, userName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                result = true;
            }
            rs.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return result;
    }

    public Users getUser(String userName) {
        Users user = null;
        String query = "SELECT * FROM " + Const.TABLE_USERS + " WHERE " + Const.USER_NAME + "=?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, userName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                user = new Users(rs.getInt(Const.USER_ID), rs.getString(Const.USER_NAME),
                        rs.getInt(Const.USER_ACCESS_GROUP));
            }
            rs.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return user;
    }

    public void loadParameters() {
        String query = "SELECT * FROM " + Const.TABLE_SETTINGS + " LIMIT 1";
        try (PreparedStatement ps = connection.prepareStatement(query); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                baseDirectory = rs.getString(Const.SETTING_BASE_DIRECTORY);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public boolean checkFile(DocFiles docFile) {
        //Ищем файл на совпадение
        boolean result = false;
        String query = "SELECT * FROM " + Const.TABLE_FILES;
        return result;
    }

    public Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName + dbParam, dbUser, dbPass);
            connection.setAutoCommit(false);
            System.out.println("Database connected.");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            new CustomAlert(Alert.AlertType.ERROR, null, e.getMessage());
        }
        return connection;
    }

    public Connection getConnection(boolean autoCommit) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName + dbParam, dbUser, dbPass);
            connection.setAutoCommit(autoCommit);
            System.out.println("Database connected. AutoCommit: " + autoCommit);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            new CustomAlert(Alert.AlertType.ERROR, "KHM Docs", null, e.getMessage());
        }
        return connection;
    }

    public TreeItem<Category> getCategoryTree() {
        Map<Integer, TreeItem<Category>> itemById = new HashMap<>();
        Map<Integer, Integer> parent = new HashMap<>();
        TreeItem<Category> root = getRootCategory();
        root.setExpanded(true);
        String query = "SELECT * FROM " + Const.TABLE_CATEGORY;
        try (PreparedStatement ps = connection.prepareStatement(query); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Category category = new Category(rs.getInt(Const.CAT_ID),
                        rs.getInt(Const.CAT_PID),
                        rs.getString(Const.CAT_NAME));
                itemById.put(rs.getInt(Const.CAT_ID), new TreeItem<>(category));
                parent.put(rs.getInt(Const.CAT_ID), rs.getInt(Const.CAT_PID));
            }
            for (Map.Entry<Integer, TreeItem<Category>> entry : itemById.entrySet()) {
                Integer key = entry.getKey();
                Integer p = parent.get(key);
                TreeItem<Category> parentItem = itemById.get(p);
                if (p == root.getValue().getId()) {
                    root.getChildren().add(entry.getValue());
                } else {
                    parentItem.getChildren().add(entry.getValue());
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return root;
    }

    public TreeItem<Category> getRootCategory() {
        TreeItem<Category> root = null;
        String query = "SELECT * FROM " + Const.TABLE_COMPANY;
        try (PreparedStatement ps = connection.prepareStatement(query); ResultSet rs = ps.executeQuery()) {
            rs.next();
            root = new TreeItem<>(new Category(rs.getInt(Const.COM_ID), 0, rs.getString(Const.COM_NAME)));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return root;
    }

    public boolean categoryDelete(Category category) {
        boolean result = false;
        String query = "DELETE FROM " + Const.TABLE_CATEGORY + " WHERE " + Const.CAT_ID + "=?";
        try (PreparedStatement st = connection.prepareStatement(query)) {
            if (!st.getConnection().getAutoCommit()) {
                st.getConnection().setAutoCommit(false);
            }
            st.setInt(1, category.getId());
            st.execute();
            result = true;
            connection.commit();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return result;
    }

    public void categoryEdit(Category category) {
        String query = "UPDATE " + Const.TABLE_CATEGORY + " SET " + Const.CAT_NAME + "=? WHERE " + Const.CAT_ID + "=?";
        try (PreparedStatement st = connection.prepareStatement(query)) {
            st.setString(1, category.getName());
            st.setInt(2, category.getId());
            st.execute();
            connection.commit();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public int categoryAdd(Category category) {
        int result = 0;
        String query = "INSERT INTO " + Const.TABLE_CATEGORY + "(" + Const.CAT_NAME + "," + Const.CAT_PID + ") VALUES(?,?)";
        try (PreparedStatement st = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            st.setString(1, category.getName());
            st.setInt(2, category.getPid());
            st.execute();
            ResultSet rs = st.getGeneratedKeys();
            rs.next();
            result = rs.getInt(1);
            connection.commit();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return result;
    }

    public int docFilesInsert(Doc doc, DocFiles file) {
        String query = "INSERT INTO " + Const.TABLE_FILES + "(" + Const.FILES_DOC_ID + "," +
                Const.FILES_NAME + "," + Const.FILES_PATH + ") VALUES(?,?,?)";
        int index = 0;
        try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, doc.getId());
            ps.setString(2, file.getName());
            ps.setString(3, file.getPath());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            rs.next();
            index = rs.getInt(1);
            rs.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return index;
    }

    public void docFilesUpdate(DocFiles file) {
        String query = "UPDATE + " + Const.TABLE_FILES + " SET " + Const.FILES_DOC_ID + "=?, " +
                Const.FILES_NAME + "=?, " + Const.FILES_PATH + "=? WHERE " + Const.FILES_ID + "=?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, file.getDocId());
            ps.setString(2, file.getName());
            ps.setString(3, file.getPath());
            ps.setInt(4, file.getId());
            ps.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void docFilesDelete(DocFiles file) {
        String query = "DELETE FROM " + Const.TABLE_FILES + " WHERE " + Const.FILES_ID + "=?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, file.getId());
            ps.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public boolean docFilesDeleteAllInDoc(Doc doc) {
        String query = "DELETE FROM " + Const.TABLE_FILES + " WHERE " + Const.FILES_DOC_ID + "=?";
        boolean result = false;
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, doc.getId());
            ps.execute();
            result = true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return result;
    }

    public ObservableList<Doc> getDoc() {
        String query = "SELECT * FROM " + Const.TABLE_DOCS;
        ObservableList<Doc> docs = FXCollections.observableArrayList();
        ObservableList<DocFiles> docFiles = getDocFiles();
        try (PreparedStatement st = connection.prepareStatement(query); ResultSet rs = st.executeQuery()) {
            while (rs.next()) {
                FilteredList<DocFiles> filteredList = new FilteredList<>(docFiles);
                filteredList.setPredicate(t -> {
                    try {
                        return t.getDocId() == rs.getInt(Const.DOC_ID);
                    } catch (SQLException e) {
                        Log.Log(e.getMessage());
                    }
                    return false;
                });
                Doc doc = new Doc();
                doc.setId(rs.getInt(Const.DOC_ID));
                doc.setDesignation(rs.getString(Const.DOC_DESIGNATION));
                doc.setName(rs.getString(Const.DOC_NAME));
                doc.setNumber(rs.getString(Const.DOC_NUMBER));
                doc.setCategoryId(rs.getInt(Const.DOC_CATEGORY_ID));
                doc.setAuthor(rs.getString(Const.DOC_AUTHOR));
                doc.setFiles(filteredList);
                doc.setConFiles(getConnectionDocFiles(doc));
                docs.add(doc);
            }
        } catch (SQLException e) {
            Log.Log(e.getMessage());
        }
        return docs;
    }

    public int docInsert(Doc doc, List<File> files) {
        String query = "INSERT INTO " + Const.TABLE_DOCS + "(" + Const.DOC_NUMBER + "," + Const.DOC_DESIGNATION + ","
                + Const.DOC_NAME + "," + Const.DOC_AUTHOR + "," + Const.DOC_CATEGORY_ID + ") VALUES(?,?,?,?,?)";
        int index = 0;
        try (PreparedStatement st = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            st.setString(1, doc.getNumber());
            st.setString(2, doc.getDesignation());
            st.setString(3, doc.getName());
            st.setString(4, doc.getAuthor());
            st.setInt(5, doc.getCategoryId());
            st.executeUpdate();
            ResultSet rs = st.getGeneratedKeys();
            rs.next();
            index = rs.getInt(1);
            doc.setId(index);
            docFilesDeleteAllInDoc(doc);
            for (DocFiles file : doc.getFiles()) {
                docFilesInsert(doc, file);
            }
            connection.commit();
            rs.close();
        } catch (SQLException e) {
            Log.Log(e.getMessage());
        }
        return index;
    }

    public void docUpdate(Doc doc) {
        String query = "UPDATE " + Const.TABLE_DOCS + " SET " + Const.DOC_NUMBER + "=?, " +
                Const.DOC_DESIGNATION + "=?, " + Const.DOC_NAME + "=?," + Const.DOC_AUTHOR + "=?," +
                Const.DOC_CATEGORY_ID + "=?" + " WHERE " + Const.DOC_ID + "=?";
        try (PreparedStatement st = connection.prepareStatement(query)) {
            st.setString(1, doc.getNumber());
            st.setString(2, doc.getDesignation());
            st.setString(3, doc.getName());
            st.setString(4, doc.getAuthor());
            st.setInt(5, doc.getCategoryId());
            st.setInt(6, doc.getId());
            st.execute();
            docFilesDeleteAllInDoc(doc);
            for (DocFiles file : doc.getFiles()) {
                docFilesInsert(doc, file);
            }
            connection.commit();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void docDelete(Doc doc) {
        String query = "DELETE FROM " + Const.TABLE_DOCS + " WHERE " + Const.DOC_ID + "=?";
        try (PreparedStatement st = connection.prepareStatement(query)) {
            st.setInt(1, doc.getId());
            st.execute();
            docFilesDeleteAllInDoc(doc);
            connection.commit();
        } catch (SQLException e) {
            Log.Log(e.getMessage());
        }
    }

    public boolean docExistByNumber(String number) {
        //Возвращает true если есть карточка с указанным номером
        boolean result = false;
        String query = "SELECT " + Const.DOC_NUMBER + " FROM " + Const.TABLE_DOCS +
                " WHERE " + Const.DOC_NUMBER + "=? LIMIT 1";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, number);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                result = true;
            }
            rs.close();
        } catch (SQLException throwables) {
            Log.Log(throwables.getMessage());
        }
        return result;
    }

    public ObservableList<DocFiles> getConnectionDocFiles(Doc doc) {
        ObservableList<DocFiles> conDocFiles = FXCollections.observableArrayList();
        String query = "SELECT * FROM " + Const.TABLE_CONNECTION_FILES +
                " LEFT JOIN " + Const.TABLE_FILES +
                " ON " + Const.CON_FILE_ID + "=" + Const.FILES_ID +
                " LEFT JOIN " + Const.TABLE_DOCS +
                " ON " + Const.FILES_DOC_ID + "=" + Const.DOC_ID +
                " WHERE " + Const.CON_DOC_ID + "=?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, doc.getId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                DocFiles file = new DocFiles();
                file.setDocDesignation(rs.getString(Const.DOC_DESIGNATION));
                file.setDocNumber(rs.getString(Const.DOC_NUMBER));
                file.setDocName(rs.getString(Const.DOC_NAME));
                file.setId(rs.getInt(Const.CON_FILE_ID));
                file.setDocId(rs.getInt(Const.CON_DOC_ID));
                file.setName(rs.getString(Const.FILES_NAME));
                conDocFiles.add(file);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return conDocFiles;
    }

    public ObservableList<DocFiles> getDocFiles() {
        ObservableList<DocFiles> docFiles = FXCollections.observableArrayList();
        String query = "SELECT * FROM " + Const.TABLE_FILES + " LEFT JOIN " + Const.TABLE_DOCS + " ON " + Const.FILES_DOC_ID + "=" + Const.DOC_ID;
        try (PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                DocFiles df = new DocFiles(rs.getInt(Const.FILES_ID), rs.getInt(Const.FILES_DOC_ID),
                        rs.getString(Const.FILES_NAME), rs.getString(Const.FILES_PATH));
                df.setDocNumber(rs.getString(Const.DOC_NUMBER));
                df.setDocName(rs.getString(Const.DOC_NAME));
                df.setDocDesignation(rs.getString(Const.DOC_DESIGNATION));
                docFiles.add(df);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return docFiles;
    }
}
