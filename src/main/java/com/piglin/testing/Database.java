package com.piglin.testing;

import com.mysql.jdbc.AbandonedConnectionCleanupThread;

import java.sql.*;

/**
 * Created by swyna on 3/2/15.
 */
public class Database {

    private static Connection connection = null;
    private static Statement statement = null;
    private static PreparedStatement preparedStatement = null;
    private static ResultSet resultSet = null;

    public static void connect(String host, String databaseName, String user, String password) throws ClassNotFoundException, SQLException {
        // This will load the MySQL driver, each DB has its own driver
        Class.forName("com.mysql.jdbc.Driver");
        // Setup the connection with the DB
        connection = DriverManager.getConnection("jdbc:mysql://"+host+"/"+databaseName+"?"
                + "user="+user+"&password="+password);
    }

    public static int executeUpdate(String query) throws SQLException, ClassNotFoundException {

        int result;

        preparedStatement = connection.prepareStatement(query);
        result = preparedStatement.executeUpdate();
        return result;
    }

    public static void closeSQL() throws SQLException, InterruptedException {
        connection.close();
        AbandonedConnectionCleanupThread.shutdown();
    }

    public static DatabaseMetaData getDatabaseMetaData() throws SQLException {
        return connection.getMetaData();
    }

    public static ResultSet executeQuery(String query) throws SQLException {
        preparedStatement = connection.prepareStatement(query);
        return preparedStatement.executeQuery();
    }
}
