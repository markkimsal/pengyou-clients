<%@ page import="org.pengyou.client.web.PYSession"%>

<%
    PYSession sessionData =(PYSession)session.getAttribute("sessionData");
    sessionData.storeData(response,request,session);
    session.setAttribute("sessionData", sessionData);
%>