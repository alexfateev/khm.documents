package com.himmash.database;

import com.himmash.model.Users;

public class Config {
    public static String dbName = "khm_documents";
    public static String dbHost = "192.168.128.60";
    public static String dbPort = "3306";
    public static String dbUser = "root";
    public static String dbPass = "Vecrek";
    public static String dbParam = "?useUnicode=true&serverTimezone=UTC";

    public static String baseDirectory = "\\fs01\\KHMDocuments";
    public static String lastOpenPath = "\\";

    public static String programTitle = "Документы СМК КХМ";

    public static Users user;
}
