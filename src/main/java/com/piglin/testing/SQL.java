package com.piglin.testing;

import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by swyna on 3/2/15.
 */
public class SQL {

    public static void dropTable(String name) throws SQLException, ClassNotFoundException {
        Database.executeUpdate("DROP TABLE " + name + ";");
    }

    public static void createTableIfNotExists(String name, String fields) throws SQLException, ClassNotFoundException {
        Database.executeUpdate("CREATE TABLE IF NOT EXISTS " + name + " (" + fields + ");");
    }

    public static void createTable(String name, String fields) throws SQLException, ClassNotFoundException {
        Database.executeUpdate("CREATE TABLE " + name + " (" + fields + ");");
    }

    public static java.sql.ResultSet executeQuery(String query) throws SQLException {
        return Database.executeQuery(query);
    }
}
