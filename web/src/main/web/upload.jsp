<%@ page import="org.apache.commons.fileupload.DiskFileUpload"%>
<%@ page import="java.util.Vector"%>
<%@ page import="java.util.List"%>
<%@ page import="org.apache.commons.fileupload.DefaultFileItem"%>
<%@ page import="java.io.InputStream"%>
<%@ page import="org.pengyou.client.web.store.webDav.WebDavStore"%>
<%@ page import="java.io.IOException"%>
<%@ page import="org.apache.commons.fileupload.disk.DiskFileItemFactory"%>
<%@ page import="org.apache.commons.fileupload.servlet.ServletFileUpload"%>
<%@ page import="java.util.Iterator"%>
<%@ page import="org.apache.commons.fileupload.FileItem"%>
<%@ page import="java.net.URLEncoder"%>
<%

    ServletFileUpload servletFileUpload = new ServletFileUpload(new DiskFileItemFactory());
    List fileUploadList = servletFileUpload.parseRequest(request);

    String error = "";

    String location = "";
    Vector formData = new Vector();

    Iterator it = fileUploadList.iterator();
    while (it.hasNext()) {
        FileItem fileItem = (FileItem) it.next();

        if (fileItem.getFieldName().equals("fileInput") && fileItem.getName() != null) {
            //InputStream fileis = fileitem.getInputStream();
            formData.add(fileItem);


        } else if (fileItem.getFieldName().equals("location")) {
            byte[] data = new byte[(int) fileItem.getSize()];
            InputStream fileis = fileItem.getInputStream();
            if (fileis != null) {
                fileis.read(data);
            }
            StringBuffer toto = new StringBuffer();
            for (int t = 0; t < (int) fileItem.getSize(); t++)
                location += (char) data[t];
            fileis.close();


        }


    }
    if (location.equals("") == true)
        location = "/";
    else if (location.endsWith("/") == false)
        location += "/";

    for (int j = 0; j < formData.size(); j++) {
        String fileName =((FileItem) formData.get(j)).getName();
        WebDavStore store = new WebDavStore(location + fileName/*URLEncoder.encode(fileName,"ISO-8859-1")*/);

        try {
            store.upload(((FileItem) formData.get(j)).getInputStream());
        } catch (IOException e) {
        }
    }
    response.sendRedirect("index.jsp?p=files&path=" + location);
%>