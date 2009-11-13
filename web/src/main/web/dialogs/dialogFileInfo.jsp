<%@ page import="org.pengyou.client.web.store.webDav.WebDavStore"%>
<%@ page import="java.util.Vector"%>
<%@ page import="java.util.Iterator"%>
<%String fileName=request.getParameter("target");
WebDavStore res = new WebDavStore(fileName);
Vector versions = res.getVersions();
    if (versions.contains("jcr:rootVersion"))
        versions.remove("jcr:rootVersion");
//    versions.removeElement(versions.lastElement());
Iterator it = versions.iterator();
%>
<form id="upload" action="upload.jsp" enctype="multipart/form-data" method="post">
<table id='dialogTable'>
    <tr>
     <th colspan='2' class="dialogHeader">ID Card</th>
    </tr>
    <tr>
        <td>Document name</td>
        <td>
           <span id='fileName'><%=res.getDisplayName()%></span>
        </td>
    </tr>
    <tr>
        <td>Remote location</td>
        <td><span id='fileLocation'><%=res.getLocation()%></span></td>
    </tr>
    <tr>
        <td valign='top'>Size</td>
        <td valign='top'><span id='fileSize'><%=res.getContentLengthDisplayable()%></span></td>
    </tr>
    <tr>
        <td valign='top'>Created by</td>
        <td valign='top'><span id='fileOwner'><%=res.getOwner()%></span></td>
    </tr>
    <tr>
        <td valign='top'>Last modified on</td>
        <td valign='top'><span id='fileLastModified'><%=res.getLastModified()%></span></td>
    </tr>
    <tr>
        <td valign='top'>Description</td>
        <td valign='top'><span id='fileDescription'><%=res.getLastComment()%></span></td>
    </tr>
    <tr>
        <td valign='top'>History</td>
        <td valign='top'>
           <select id='fileVersions' onchange="if (this.value!='')downloadFile(this.value);">
               <option value=""></option>
               <option value="<%=res.getLocation()%>">Current version</option>
               <%while(it.hasNext()){
                Vector attributes=(Vector)it.next();%>
                  <option value="<%=attributes.get((2))%>&rename=<%=attributes.get(0)%>">Version <%=attributes.get((1)) + " on " +attributes.get((3))%></option>
               <%}%>
           </select>
           <br /><i>Please select the version you want to download.</i>
        </td>
    </tr>
    <tr>
        <td colspan="2" align='center'><input type="button" value="Close" onclick="discardCurrentDialog();"/></td>
    </tr>
</table>
</form>
