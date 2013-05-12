/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.mad.project;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletContext;

/**
 *
 * @author Phani Rahul
 */
public class Message  implements Serializable {
 
    private String message_id;
    private String message;
    private String registration_id;
    private String serviced;
    private String result;
    private String username;
    private static final String GET_MESSAGE=" Select * from messages where message_id = ?";
    private static final String ADD_MESSAGE = "Insert into messages "
            + "(message_id, message, serviced, registration_id, username)"
            + "values (?,?,0,?,?)";
    private static final String SERVICE_MESSAGE = "Update messages set "
            + "serviced=1 , result = ?  where message_id= ? ";
    private static final String NEXT_SEQ = "select messages_seq.NEXTVAL from dual";
    private Database db;

    public Message(){
        
    }
    public Message(ServletContext context)
            throws ClassNotFoundException, SQLException,
            InstantiationException, IllegalAccessException {
        db = Database.getConnection(ConnectionParameters.getConnectionParameters(context));
    }
    public Message(String id, ServletContext context)
            throws ClassNotFoundException, SQLException,
            InstantiationException, IllegalAccessException {
        db = Database.getConnection(ConnectionParameters.getConnectionParameters(context));
        this.message_id= id;
        populate(id);
    }
    private void populate(String id) throws SQLException{
        PreparedStatement ps = db.getPreparedStatement(GET_MESSAGE);
        ps.setString(1, id);
        ResultSet rs = db.runPreparedStatementQuery(ps);
        if(rs.next()){
            this.message_id = rs.getString("message_id");
            this.message = rs.getString("message");
            this.serviced = rs.getString("serviced");
            this.registration_id = rs.getString("registration_id");
            this.result = rs.getString("result");
            this.username = rs.getString("username");
        }
        
    }

    public boolean addMessage() throws SQLException {
        if ((message == null || message.equalsIgnoreCase(""))
                || (registration_id == null || registration_id.equalsIgnoreCase(""))
                || (username == null || username.equalsIgnoreCase(""))) {
            return false;
        }
        PreparedStatement ps = db.getPreparedStatement(NEXT_SEQ);
        ResultSet rs = db.runPreparedStatementQuery(ps);
        if(rs.next()){
            this.message_id = rs.getString(1);
        }
        ps = db.getPreparedStatement(ADD_MESSAGE);
        ps.setString(1, this.message_id);
        ps.setString(2, message);
        ps.setString(3, registration_id);
        ps.setString(4, username);
        int i = db.runPreparedStatementUpdate(ps);
        if (i == 1) {           
            return true;
        } else {
            return false;
        }
    }
   

    public boolean serviceMessage() throws SQLException {
        if ((message_id == null || message_id.equalsIgnoreCase(""))
                || (result == null || result.equalsIgnoreCase(""))) {
            return false;
        }
        PreparedStatement ps = db.getPreparedStatement(SERVICE_MESSAGE);
        ps.setString(1, this.result);
        ps.setInt(2, Integer.valueOf(this.message_id));
        
        int i = db.runPreparedStatementUpdate(ps);
        if (i == 1) {           
            return true;
        } else {
            return false;
        }
    }
    public static boolean isServiced(String cid, ServletContext sc) 
            throws ClassNotFoundException, SQLException, 
            InstantiationException, IllegalAccessException {
        
        Database db = Database.getConnection(ConnectionParameters.getConnectionParameters(sc));
        PreparedStatement ps = db.getPreparedStatement(GET_MESSAGE);
        ps.setString(1, cid);
        ResultSet rs = db.runPreparedStatementQuery(ps);
        if(rs.next()){
            int  i  = rs.getInt("serviced");
            if(i == 0){ 
                return false;
            }else{
                return true;
            }
        }
        return false;
    }

    public String getMessage_id() {
        return message_id;
    }

    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRegistration_id() {
        return registration_id;
    }

    public void setRegistration_id(String registration_id) {
        this.registration_id = registration_id;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }    
    

    @Override
    public String toString() {
        return " { " 
                + "\""+Res.MESSAGE_ID+"\""+": " + "\""+ message_id + "\""+", "
                +"\""+Res.QUERY+"\""+": " + "\""+message +"\""+ ", "
                +"\""+Res.RESULT+"\""+": " + "\""+result +"\""
                + " }";
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    
}
