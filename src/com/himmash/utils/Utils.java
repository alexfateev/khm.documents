package com.himmash.utils;

public class Utils {
    private static String userDomain = System.getenv("USERDOMAIN");
    private static String computerName = System.getenv("COMPUTERNAME");
    private static String userName = System.getenv("USERNAME");

    public enum TypeModeDoc {INSERT, EDIT}

    public static String getUserDomain() {
        return userDomain;
    }

    public static String getComputerName() {
        return computerName;
    }

    public static String getUserName() {
        return userName;
    }

    public static String getFullUserName() {
        return getUserDomain() + "\\" + getUserName();
    }
}
