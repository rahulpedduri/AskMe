<%-- 
    Document   : index
    Created on : May 3, 2013, 6:21:39 PM
    Author     : Phani Rahul
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%> 
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Login</title>
    </head>
    <body>
        <h1>Login to query the phone</h1>
        <form action="Login" method="POST" >
            <label for="username">Username</label>
            <input type="text" value="" name="username"/>
            <br/>
            <label for="password">Password</label>
            <input type="password" value="" name="password"/>
            <br/>
            <input type="submit" value="Login"  name="login_submit"/>
        </form>
        
    </body>
</html>
