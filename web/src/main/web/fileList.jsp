<%@ page import="java.util.Iterator" %>
<%@ page import="org.pengyou.client.web.MimeTypeIcons" %>
<%@ page import="java.io.InputStream" %>
<%@ page import="org.pengyou.client.web.File" %>
<%@ page import="java.util.Vector" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="org.pengyou.client.web.PYSession" %>
<%@ page import="org.pengyou.client.web.store.webDav.WebDavStore" %>
<%@ page import="java.util.Map"%>
<%


    String path = "";
    if (request.getParameter("path") != null && !request.getParameter("path").equals("/"))
        path = request.getParameter("path");

    Map ClipBoard = ((PYSession) session.getAttribute("sessionData")).getClipBoard(request, session);
                               
    WebDavStore store = new WebDavStore(path.equals("") ? "/" : path);
    Vector dir = store.listBasic();

    if (dir.size() > 0) {
        Iterator i = dir.iterator();
        File fileWrapper = new File();
        InputStream xmlStream = application.getResourceAsStream("/WEB-INF/filetype.xml");
        MimeTypeIcons icons = new MimeTypeIcons(xmlStream);
        while (i.hasNext()) {
            fileWrapper.setFileInfo((String[]) i.next());
%>
            <%@ include file="file.jsp" %>
      <%}
  } else {%>
<br /><br /><br /><br /><span id="emptyDirectory">This directory is empty</span>
<%}%>
