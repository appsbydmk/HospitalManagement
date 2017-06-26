package com.appsbydmk.hospitalmgmt;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.*;

public class DbConnectionHandler {
	private static Connection con = null;
	 
    public static Connection getConnection() {
        try {
            Class.forName(HelperConstants.JDBC_DRIVER);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DbConnectionHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            con = DriverManager.getConnection(HelperConstants.DB_URL, HelperConstants.USERNAME, HelperConstants.PASSWORD);
        } catch (SQLException ex) {
            Logger.getLogger(DbConnectionHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return con;
    }
}
