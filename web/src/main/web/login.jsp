<%@ page import="org.pengyou.client.web.PYSession"%>
<%
    PYSession data = (PYSession) session.getAttribute("sessionData");
    if (data != null)
    {
        data.authenticate(request);
        session.setAttribute("sessionData", data);
        response.sendRedirect("index.jsp");
    }
%>