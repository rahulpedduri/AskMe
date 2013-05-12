/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.mad.project;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.servlet.ServletContext;

/**
 *
 * @author Phani Rahul
 */
public class User implements Serializable {
    

    private String username;
    private String password;
    private String registration_id;
    private static final String LOGIN_QUERY = "Select * from users where username=? and password=?";
    private static final String CHECK_USERNAME_QUERY = "Select * from users where username=?";
    private static final String INSERT_USER_QUERY = "insert into users(username,password,registration_id)"
            + "values(?,?,?)";
    private static final String UPDATE_REG_ID="update users set registration_id = ? where username = ?";
       private static final String GET_ALL="Select * from messages where registration_id = ? and username = ?";

    private Database db;

    public User(ServletContext context)
            throws ClassNotFoundException, SQLException,
            InstantiationException, IllegalAccessException {
        db = Database.getConnection(ConnectionParameters.getConnectionParameters(context));
    }
    
    public ArrayList getAllMessages(String regId) throws SQLException{
        
        PreparedStatement ps = db.getPreparedStatement(GET_ALL);
        ps.setString(1, regId);
        ps.setString(2, username);
        
        ResultSet rs = db.runPreparedStatementQuery(ps);
        ArrayList a= new ArrayList();
        while (rs.next()) {
            Message m = new Message();
            m.setMessage(rs.getString("message"));
            m.setMessage_id(rs.getString("message_id"));
            m.setResult(rs.getString("result"));
            
            a.add(m);
        }
        return a;
    }

    /**
     * checks to see if the username specified already exists.
     *
     * @param username
     * @returntrue if the username does not already exist
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static boolean checkUsernameExists(String username, ConnectionParameters cp)
            throws ClassNotFoundException, SQLException, InstantiationException,
            InstantiationException, IllegalAccessException {
        Database db = Database.getConnection(cp);
        PreparedStatement ps = db.getPreparedStatement(CHECK_USERNAME_QUERY);
        ps.setString(1, username);
        ResultSet rs = db.runPreparedStatementQuery(ps);
        if (rs.next()) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean updateRegistrationId(String username, String regId, ConnectionParameters cp)
            throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        Database db = Database.getConnection(cp);
        PreparedStatement ps = db.getPreparedStatement(UPDATE_REG_ID);
        ps.setString(1, regId);
        ps.setString(2, username);
        int rs = db.runPreparedStatementUpdate(ps);
        if (rs==1) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * checks to see if the username and password match the details on the
     * server
     *
     * @param username the username of the user
     * @param password the password of the user
     * @return true if login is a success
     *
     */
    public boolean loginCheck(String username, String password) throws SQLException {

        PreparedStatement ps = db.getPreparedStatement(LOGIN_QUERY);
        ps.setString(1, username);
        ps.setString(2, password);
        ResultSet rs = db.runPreparedStatementQuery(ps);
        if (rs.next()) {
            populate(rs);
            return true;
        } else {
            return false;
        }
    }

    private void populate(ResultSet rs) throws SQLException {
        this.username = rs.getString("username");
        this.registration_id = rs.getString("registration_id");
    }

    /**
     * saves the user object to the properties file
     *
     */
    public boolean save() throws SQLException {
        PreparedStatement ps = db.getPreparedStatement(INSERT_USER_QUERY);
        ps.setString(1, username);
        ps.setString(2, password);
        ps.setString(3, registration_id);
        int rs = db.runPreparedStatementUpdate(ps);
        if (rs > 0) {
            return true;
        } else {
            return false;
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRegistration_id() {
        return registration_id;
    }

    public void setRegistration_id(String registration_id) {
        this.registration_id = registration_id;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
