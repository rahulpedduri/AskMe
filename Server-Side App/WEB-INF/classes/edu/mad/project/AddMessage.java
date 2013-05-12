/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.mad.project;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.json.simple.JSONValue;

//import com.google.android.gcm.server.Constants;
/**
 *
 * @author Phani Rahul
 */
public class AddMessage extends HttpServlet{

    private static final String REGISTRATION_IDS = "registration_ids";
    private static final String DATA = "data";

    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            Object query_submit = request.getParameter("query_submit");
            if (query_submit != null && !((String) query_submit).trim().equals("")) {

                String message = request.getParameter("message");
                String function = request.getParameter("func");

                HttpSession session = request.getSession();
                User user = (User) session.getAttribute("user");
                String ajax = request.getParameter("ajax");
                Timer t = new Timer();
                Message m=null;
                boolean flag=false;
                try {
                    m = new Message(getServletContext());
                    StringBuilder msg = new StringBuilder();

                    if (function != null && !(function).trim().equals("")) {
                        msg.append(function);
                    }
                    if (message != null && !(message).trim().equals("")) {
                        //TODO: to be changed to make the message
                        msg.append(":");
                        msg.append(message);
                    }
                    
                    m.setMessage(msg.toString());
                    m.setRegistration_id(user.getRegistration_id());                    
                    m.setUsername(user.getUsername());
                    
                   flag = m.addMessage();
                   if(flag){
                       session.setAttribute("current_id", m.getMessage_id());
                       t.schedule(new Scheduler(session, m.getMessage_id()), 120000);
                   }
//                    m.setMessage_id("2");
                    
                    /* TODO: Call AddMessage method to save to db */

                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(AddMessage.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SQLException ex) {
                    Logger.getLogger(AddMessage.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InstantiationException ex) {
                    Logger.getLogger(AddMessage.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(AddMessage.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                HashMap params = new HashMap();
                HashMap data = new HashMap();
                ArrayList a = new ArrayList();
                a.add(m.getRegistration_id());
                params.put(REGISTRATION_IDS, a);
                data.put(Res.MESSAGE, m.getMessage());
                data.put(Res.MESSAGE_ID, m.getMessage_id());
               // data.put(Res.SESSION,session);
                params.put(DATA, data);
                String d = JSONValue.toJSONString(params);      

                HttpURLConnection resp = postToServer(Res.GCM_URL, "application/json", d);

                if (resp.getResponseCode() == 200) {
                    System.out.println("Done...");
                } else {
                    System.out.println(resp.getResponseCode() + "..." + resp.getResponseMessage());

                }


            }

        } finally {
            out.close();
        }
    }

    protected static void makePair(StringBuilder temp, String name,
            String value) {
        if (temp != null) {
            temp.append('&')
                    .append(name)
                    .append('=')
                    .append(value);
        }
    }

    private HttpURLConnection postToServer(String url, String type, String body)
            throws IOException {
        if (url == null || body == null) {
            throw new IllegalArgumentException("arguments cannot be null");
        }

        byte[] bytes = body.getBytes();
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setDoOutput(true);
        conn.setUseCaches(false);
        conn.setFixedLengthStreamingMode(bytes.length);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", type);
        conn.setRequestProperty("Authorization", "key=" + Res.API_KEY);
        OutputStream out = conn.getOutputStream();
        try {
            out.write(bytes);
        } finally {
            out.close();
        }
        return conn;
    }
    
    private class Scheduler extends TimerTask{
        
        HttpSession session=null;
        String message_id=null;
        private Scheduler(HttpSession session, String message_id) {
            this.session = session;
            this.message_id = message_id;
        }

        @Override
        public void run() {
            if(session != null && session.getAttribute("current_id") != null &&
                    ((String)session.getAttribute("current_id")).equalsIgnoreCase(message_id)){
                session.removeAttribute("current_id");
            }
        }
        
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
