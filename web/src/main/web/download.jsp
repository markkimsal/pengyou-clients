<%@ page import="java.io.OutputStream"%>
<%@ page import="org.pengyou.client.web.store.webDav.WebDavStore"%>
<%@ page import="org.pengyou.client.web.PYSession"%>
<%
    String file = request.getParameter("fileName");
    String rename = request.getParameter("rename");
    if (file != null)
    {
        WebDavStore store = new WebDavStore(file);
        OutputStream ostream = response.getOutputStream();

        if (store.isExist())
        {
            String displayName= rename != null ? rename : store.getDisplayName();
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=\""+displayName+"\"");
            //response.setContentLength((int) store.getContentLength());
            response.setStatus(response.SC_OK);
            store.readContentAsStream(ostream, true);
          }

        ostream.flush();
        ostream.close();
    }
%>