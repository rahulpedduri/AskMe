/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.mad.project;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Phani Rahul
 */
public class Login extends HttpServlet {

    private final String ON_SUCCESS = "query.jsp";
    private final String ON_FAIL = "login.jsp";
    private final String ON_LOGOUT="login.jsp";
    private String username;
    private String password;
    private HashMap messages;
    private HttpSession session;
    private User user;
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
            
          
            Object login = request.getParameter("login_submit");
            Object logout = request.getParameter("logout");
            
            if (logout != null && !((String) logout).trim().equals("")
                    && ((String) logout).trim().equals("true")) {
                if(session!=null) {
                            session.invalidate();
                        }
                response.sendRedirect(ON_LOGOUT);
                return;
            }
            else if (login != null && !((String) login).trim().equals("")) {
                username = request.getParameter("username");
                password = request.getParameter("password");

                messages = new HashMap();

                try {
                    user = new User(getServletContext());


                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SQLException ex) {
                    Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InstantiationException ex) {
                    Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
                }

                if (checkString(username) && checkString(password)) {
                    boolean log = false;
                    try {
                        log = user.loginCheck(username, password);
                    } catch (Exception ex) {
                        Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    System.out.println("login: " + log);
                    if (!log) {
                        messages.put("login.fail", "Username/Password is incorrect");
                        request.setAttribute("message", messages);
                        request.getRequestDispatcher(ON_FAIL).forward(request, response);
                        return;
                    } else {
                            setSession(request, response, username,false);
                           
                        request.setAttribute("message", messages);

                        System.out.println("redirecting..");
                        response.sendRedirect(ON_SUCCESS);
                        return;
                    }
                } else {
                    messages.put("login.unknown", "Login failed because on invalid values provided");
                    request.setAttribute("message", messages);
                    request.getRequestDispatcher(ON_FAIL).forward(request, response);
                }

            }
             boolean cookieMade=false;
            Cookie[] cookies = request.getCookies();
            for (int i = 0; i < cookies.length; i++) {
                Cookie cookie = cookies[i];
                if ("user".equals(cookie.getName())) {
                    cookieMade=true;
                    setSession(request,response, cookie.getValue(), cookieMade);
                    return;
                }
            }
            

        } finally {            
            out.close();
        }
    }
    
     private void setSession(HttpServletRequest request, HttpServletResponse response, String username, boolean redirect) {
        session = request.getSession();
        session.setAttribute("user", user);
    
        ServletContext context = getServletContext();
    	context.setAttribute(session.getId(), session);
        if(redirect){
            try {
                response.sendRedirect(ON_SUCCESS);       
            } catch (IOException ex) {
                Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private boolean checkString(String string) {
        if (string != null && !string.trim().equals("")) {
            return true;
        }
        return false;
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
