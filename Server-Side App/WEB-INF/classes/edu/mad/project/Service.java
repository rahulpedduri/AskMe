/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.mad.project;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.json.simple.JSONObject;

/**
 *
 * @author Phani Rahul
 */
public class Service extends HttpServlet {

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
            HttpSession session = request.getSession();
            Object status = request.getParameter("status");
            String cid = "";
            if (status != null && !((String) status).trim().equals("")) {

                if (((String) status).equalsIgnoreCase("waiting")) {
                    cid = (String) session.getAttribute("current_id");
                    boolean stat = false;
                    if (cid != null) {
                        try {
                            stat = Message.isServiced(cid, getServletContext());
                        } catch (ClassNotFoundException ex) {
                            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (SQLException ex) {
                            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (InstantiationException ex) {
                            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (IllegalAccessException ex) {
                            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    JSONObject resp = new JSONObject();
                    if (!stat && cid != null) {
                        //TODO: 

                        resp.put("status", "false");
                        resp.put("html", "<img src='img/ajax-loader.gif'/>");
                    } else if (stat && cid != null) {
                        //TODO: 
                        resp.put("status", "true");
                        String id = cid;
                        try {
                            Message m = new Message(id, getServletContext());
                            StringBuilder s = new StringBuilder();
                            s.append("<table><tr>");
                            s.append("<td>");
                            s.append(m.getMessage());
                            s.append("</td>");

                            s.append("<td>");
                            s.append(m.getResult());
                            s.append("</td>");
                            s.append("</tr></table>");
                            resp.put("html", s.toString());
                        } catch (ClassNotFoundException ex) {
                            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (SQLException ex) {
                            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (InstantiationException ex) {
                            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (IllegalAccessException ex) {
                            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else if (cid == null) {
                        resp.put("status", "true");
                        resp.put("html", "<span> Your query has timed out. <br/>Either it is lost on the way or that your phone is switched off! </span>");
                    }
                    out.print(resp);

                }
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
