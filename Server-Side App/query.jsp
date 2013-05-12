<%-- 
    Document   : query
    Created on : May 3, 2013, 9:38:34 PM
    Author     : Phani Rahul
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Welcome!! </title>
        <script type="text/javascript" src="js/jquery.js" ></script>
        <script>
            var d;
            $(document).ready(function() {
                $("#query_submit").click(function(){
                    $('#ajax_result').html("<img src='img/ajax-loader.gif'/>");
                    $(".ask").attr('disabled','disabled');
                    $.ajax({
                        url: '/PhoneQuery/AddMessage',
                        type: "GET",
                        data: { func: document.query.func.value, 
                            message: document.query.message.value,
                            query_submit: 'true'}                    
                        ,   success: function(data1){
                            
                            i = setInterval(function(){
                            $.ajax({
                                url: '/PhoneQuery/Service',
                                type: "GET",
                                data: { status: "waiting" },                            
                                success: function(data){
                                    d=data;
                                    //alert(data);
                                    var reply = jQuery.parseJSON( data );
                                
                                    if(reply.status == "true"){
                             
                                        clearInterval(i);
                                        $(".ask").removeAttr('disabled');
                                        $('#ajax_result').html(reply.html);
                                    
                                    }
                                }
                            })
                        },10000)
                        }
                           
                        
                        //alert("Success");
                        //1. PendingTransaction .. status=set_pending
                        //2. setInterval($.ajax{status=waiting}  ,1000);
                        //3. call Transaction servlet using ajax
                    })
                    return false;
                });
            });
        </script>
        <style>
            td{padding: 12px;}
            table{width: 500px;}
            </style>
    </head>
    <body>
        <h1>Welcome!!</h1>

        <form action="AddMessage" method="POST" name="query">
            <label for="message">Ask your phone: </label>
            <select name="func"  class="ask" >
                <option value="GetMissedCalls" >Get Missed Calls</option>
                <option value="GetMessages">Get Unread Messages</option>
                <option value="GetNumber">Get Number of</option>
                <option value="GetName">Get Name of</option>
                <option value="GetGeo">Get Current Location</option>
                
            </select>
            <input type="text" value="" name="message" class="ask"/>

            <input type="submit" value="Ask" name ="query_submit" class="ask" id="query_submit"/>
        </form>
        <br>
        <div id="ajax_result"></div>

    </body>
</html>
