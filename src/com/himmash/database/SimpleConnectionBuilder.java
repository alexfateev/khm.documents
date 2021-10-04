package com.himmash.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SimpleConnectionBuilder implements ConnectionBuilder {

    public SimpleConnectionBuilder() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://" + Config.dbHost + ":" + Config.dbPort + "/" +
                Config.dbName + Config.dbParam, Config.dbUser, Config.dbPass);
    }
}
