/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.mad.project;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 *
 * @author Phani Rahul
 */
public class MLogin extends HttpServlet {

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
        response.setContentType("text/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            String reg = request.getParameter("registration_id");
            String key = request.getParameter(Res.KEY);
            User user = null;
            try {
                
                if (username != null && !username.trim().equals("") 
                        && password != null && !password.trim().equals("")
                        && reg != null && !reg.trim().equals("")
                        && key.equals(Res.API_KEY)) {
                  user = new User(getServletContext());
                  
                  if(user.loginCheck(username, password)){
                      HashMap resp = new HashMap();
                      resp.put(Res.OKAY,"true");
                      if(!reg.equals(user.getRegistration_id())){
                          User.updateRegistrationId(username, reg, 
                                  ConnectionParameters.getConnectionParameters(getServletContext()));
                      }
                      ArrayList a = user.getAllMessages(reg);
                      resp.put(Res.MESSAGES, a);
                      String json = JSONValue.toJSONString(resp); 
                      
                      out.print(json);
                  }else{
                      JSONObject resp = new JSONObject();
                      resp.put(Res.ERROR,Res.ERROR_LOGIN);
                      out.print(resp);
                  }
                }
                
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(MLogin.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(MLogin.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InstantiationException ex) {
                Logger.getLogger(MLogin.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(MLogin.class.getName()).log(Level.SEVERE, null, ex);
            }


        } finally {
            out.close();
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
