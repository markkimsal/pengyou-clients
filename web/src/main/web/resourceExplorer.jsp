<%@ page import="org.pengyou.client.web.File"%>
<%@ page import="org.pengyou.client.web.store.webDav.WebDavStore"%>
<%@ page import="java.io.InputStream"%>
<%@ page import="org.pengyou.client.web.MimeTypeIcons"%>
<%@ page import="java.io.OutputStream"%>
<%@ page import="java.util.*"%>
<%@ page import="org.pengyou.client.web.PYSession"%>
<%
    String path = "/";
    if (request.getParameter("path") != null && !request.getParameter("path").equals("/"))
        path = request.getParameter("path");
    if (path.endsWith("/") == true && path.length() > 1)
        path=path.substring(0,path.length() -1);
    WebDavStore store = new WebDavStore(path.equals("")?"/":path);
%>

<script type="text/javascript">
    <!--
  var expandedBox= '<%=sessionData.getExpandedBox(request,session)%>';
               var selectedItems = new Array(
  <%
   Map ClipBoard=sessionData.getClipBoard(request,session);
   if (ClipBoard != null)
   {
    Iterator it = ClipBoard.keySet().iterator();
    while (it.hasNext()) {
        Object key = it.next();
        Object value=ClipBoard.get(key);
        out.print("[\""+(String)key+"\",\""+(String)value+"\"]");
        if (it.hasNext())
         out.print(",");
        }
    }
/*       for (int i = 0; i < ClipBoard.size(); i++)
       {
           if (i!=0)
            out.print(",");
           out.print("\""+ClipBoard.get(i)+"\"");
      }
    }*/%>);

          //-->
</script>

<div id="rail">
    <input type="hidden" name="fullPath" id="fullPath" value="" />
    <ul id="pathRail">
        <li>Location</li>
        <li id="railRoot"><a href="index.jsp?p=files&amp;path=/" onclick="return applyDirectory('/')">Root</a></li>
        <%
            String[] pathList = path.split("/");
            String railBuild = "";
            for (int i = 1; i < pathList.length; i++) {
                railBuild += "/" + pathList[i];

        %>
        <li>&gt;</li>
        <li><a href="index.jsp?p=files&amp;path=<%=railBuild%>"><%=pathList[i]%></a></li>
        <%}
        %>
    </ul>
</div>

<div class="listThumb" id="fileList">

    <div id="divList">
        <div id="buttons">
            <a href="NewDocument" onclick="return popDialog('NewFile',function(){dialogNewFile();});">
                <img src="./img/actions/file_new.png" alt="New document" title="New document"/>
            </a>
            <a href="NewFolder" onclick="return popDialog('NewFolder',function(){dialogNewFolder();});">
                <img src="./img/actions/folder_new.png" alt="New Folder" title="New Folder"/>
            </a>
            <a href="Refresh" onclick="return reloadFiles();">
                <img src="./img/actions/reload.png" alt="Refresh" title="Refresh"/>
            </a>
            <a href="SimpleView" onclick="return(switchClass('fileList','listThumb'));">
                <img src="./img/icons/16x16/view_multicolumn.png" alt="Simple view" title="Simple view"/>
            </a>
            <a href="DetailedView" onclick="return(switchClass('fileList','listTab'));">
                <img src="./img/icons/16x16/view_text.png" alt="Detailed view" title="Detailed view"/></a>
            <a href="SelectAll" onclick="return selectAll();">
                <img src="./img/actions/select_all.png" alt="Select All" title="Select All"/>
            </a>
            <a href="InvertSelection" onclick="return invertSelection();">
                <img src="./img/actions/invert_selection.png" alt="Invert Selection" title="Invert Selection"/>
            </a>
        </div>
        <ul class="ulList" id="ulList">


        </ul>
    </div>
</div><!--End FileList -->
<%@ include file="fileSideBox.jsp" %>
<script type="text/javascript">
    <!--
    applyDirectory("<%=path%>");
    initFileExplorer();
    var listHeaders='<li class="liFile" id="headerList" style="display:none;"><div class="fileDetails"><ul class="detailsUl"><li class="fileName">File name<\/li><li class="fileSize">Size<\/li><li class="lastModif">Last modified<\/li><li class="creatDate">Created on<\/li><\/ul><\/div><\/li>';
    //-->
</script>