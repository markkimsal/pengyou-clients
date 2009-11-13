<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@page import="org.pengyou.client.web.*"%>
<%@ page import="java.util.*"%>
<%
   InputStream xmlStream = application.getResourceAsStream("/WEB-INF/config.xml");
   ConfigurationSingleton conf = ConfigurationSingleton.getInstance(xmlStream);

        PYSession sessionData;
        if (session.getAttribute("sessionData") == null)
            sessionData = new PYSession(request, session);
        else
            sessionData=(PYSession) session.getAttribute("sessionData");
        session.setAttribute("sessionData", sessionData);
        String param = request.getParameter("p");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>
    <title>PengYou Web</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

    <link rel="stylesheet" type="text/css" href="css/style.css?<%=(new Date()).getTime()%>"/>
    <link rel="stylesheet" type="text/css" href="css/menus.css?<%=(new Date()).getTime()%>"/>
    <link rel="stylesheet" type="text/css" href="css/listStyle.css?<%=(new Date()).getTime()%>"/>
    <link rel="stylesheet" type="text/css" href="css/dialog.css?<%=(new Date()).getTime()%>"/>
    <script language="JavaScript" type="text/javascript" src="./js/error.js?<%=(new Date()).getTime()%>"></script>
    <script language="JavaScript" type="text/javascript" src="./js/index.js?<%=(new Date()).getTime()%>"></script>
    <script language="JavaScript" type="text/javascript" src="./js/clipBoard.js?<%=(new Date()).getTime()%>"></script>
    <script language="JavaScript" type="text/javascript" src="./js/dialog.js?<%=(new Date()).getTime()%>"></script>
</head>
<body>
<!--<div id='dev'>
    <a href="#" onclick="document.getElementById('debugDiv').innerHTML='';return false">clear debug</a>
    <input type='text' id="backgroundClr" value='' onchange='changeBackground(this.value);'/>
    <div id="debugDiv"></div>
</div>-->
<div id='global'>
   <div id="Cache">

        <table id="actionCache" cellspacing='0' cellpadding='0'>
            <tr>
                <td colspan="3" class="actionCacheCell">&nbsp;</td>
            </tr>
            <tr id="dialogTr"><td class="actionCacheCell">&nbsp;</td><td id="dialogSpace"><img src="img/loading.gif" alt="loading" title="Loading" /></td><td class="actionCacheCell">&nbsp;</td></tr>
            <tr>
                <td colspan="3" class="actionCacheCell">&nbsp;</td>
            </tr>
        </table>

    </div>
   <div id="topBox">
       <img id="logo" alt='PengYou Web' title='PengYou Web' src="./img/logoPengU.jpg" />
                <% if (sessionData.getUsr().length() != 0){%>
                <div id="login">
                <span id="username"><%= sessionData.getUsr()%></span>
                <a href="Log out"
                   onclick="return popDialog('Logout',function(){dialogLogout();});">
                        Log out
                </a></div>
       <%}%>
   </div>
 <div id="header">
<%@ include file="menu.jsp"%>
</div>
<div id="main">
<%
    if (param != null)
    {
        if (param.equals("home") || !sessionData.isAuthenticated(request,session))
        { %><%@ include file="home.jsp"%><%}
        else if (param.equals("files"))
        {%><%@ include file="resourceExplorer.jsp"%><%}
        else if (param.equals("howto"))
        {%><%@ include file="howto.jsp"%><%}
        else
        {%><%@ include file="home.jsp"%><%}
}
    else
    {%><%@ include file="home.jsp"%><%}
%>
    <br/>
</div><!--End Main -->
</div>
<iframe id="ExecFrame" src="#"></iframe>
</body>
</html>