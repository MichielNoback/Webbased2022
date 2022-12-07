package nl.bioinf.dao;

import nl.bioinf.model.Role;
import nl.bioinf.model.User;
import nl.bioinf.noback.db_utils.DbCredentials;
import nl.bioinf.noback.db_utils.DbUser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;

public class MysqlDbConnector implements UserDao{
    private final String url;
    private final String dbUser;
    private final String dbPassword;
    private Connection connection;
    private static MysqlDbConnector instance = null;
    private PreparedStatement getUserPreparedStatement;
    private PreparedStatement insertUserPreparedStatement;

    /**
     * a main for demonstration purposes
     * @param args
     */
    public static void main(String[] args) {
        try {
            DbUser mySQLuser = DbCredentials.getMySQLuser();
            //String mySQLdbPassword = DbCredentials.getMySQLdbPassword();

            //connect
            MysqlDbConnector.createInstance(
                    "jdbc:mysql://staffdb.bin.bioinf.nl:3306/Michiel",
                    mySQLuser.getUserName(),
                    mySQLuser.getDatabasePassword());

            //insert a user
            MysqlDbConnector.getInstance().insertUser("Piet", "Pietissafe", "piet@example.com", Role.ADMIN);

            //fetch a user
            User user = MysqlDbConnector.getInstance().getUser("Piet", "Pietissafe");
            System.out.println("user = " + user);

            //a catch-all for database interaction exceptions
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public static void createInstance(String url, String dbUser, String dbPassword) throws DatabaseException {
        if (instance == null) {
            instance = new MysqlDbConnector(url, dbUser, dbPassword);
        }
    }

    public static MysqlDbConnector getInstance() {
        if (instance == null) {
            throw new IllegalStateException("instance is not initialized yet. Call createInstance() first!");
        }
        return instance;
    }

    private MysqlDbConnector(String url, String dbUser, String dbPassword) throws DatabaseException {
        this.url = url;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;

        //make the connection
        connect();
        createPreparedStatements();
    }

    private void createPreparedStatements() {
        try {
            //Prepare the SQL statement. The question marks are placeholders for repeated use with different data
            String fetchQuery = "SELECT * FROM Users WHERE user_name = ? AND user_password = ?";
            getUserPreparedStatement = connection.prepareStatement(fetchQuery);

            String insertQuery = "INSERT INTO Users (user_name, user_password, user_email, user_role) "
                    + " VALUES (?, ?, ?, ?)";
            insertUserPreparedStatement = connection.prepareStatement(insertQuery);

        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    private void connect() {
        try {
            //load driver class
            Class.forName("com.mysql.cj.jdbc.Driver");
            //create connection
            connection = DriverManager.getConnection(url, dbUser, dbPassword);
            System.out.println("connection ready!");
            //..which is risky
        } catch (Exception e) {
            e.printStackTrace();
            throw new DatabaseException("Something is wrong with the database, see cause Exception",
                    e.getCause());
        }
    }

    @Override
    public User getUser(String userName, String userPass) throws DatabaseException  {
        try {

            //set data on the "?" placeholders of the prepared statement
            getUserPreparedStatement.setString(1, userName);
            getUserPreparedStatement.setString(2, userPass);

            //execute
            ResultSet rs = getUserPreparedStatement.executeQuery();

            //if there is data, process it
            while (rs.next()) {
                String userMail = rs.getString("user_email");
                String userIdStr = rs.getString("user_id");
                String userRoleStr = rs.getString("user_role");
                Role role = Role.valueOf(userRoleStr);
                User user = new User(userName, userMail, userPass, role);

                //close resources
                rs.close();
                return user;
            }

        } catch (SQLException e) {
            //e.printStackTrace();
            throw new DatabaseException("Something is wrong with the database, see cause Exception",
                    e.getCause());
        }
        return null;
    }

    @Override
    public void insertUser(String userName, String userPass, String email, Role role) throws DatabaseException  {
        try{

            //set data on the "?" placeholders of the prepared statement
            insertUserPreparedStatement.setString(1, userName);
            insertUserPreparedStatement.setString(2, userPass);
            insertUserPreparedStatement.setString(3, email);
            insertUserPreparedStatement.setString(4, role.toString());

            //do the actual insert
            insertUserPreparedStatement.executeUpdate();

            //close resources
            insertUserPreparedStatement.close();
        } catch (Exception ex) {
            //ex.printStackTrace();
            throw new DatabaseException("Something is wrong with the database, see cause Exception",
                    ex.getCause());
        }
    }

    /**
     * close the connection!
     * @throws DatabaseException
     */
    public void disconnect() {
        try {
            insertUserPreparedStatement.close();
            getUserPreparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
