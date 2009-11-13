<%@ page import="org.pengyou.client.web.MenuItem"%>
<%@ page import="java.util.Iterator"%>
<%@ page import="org.pengyou.client.web.ObjectArrayIterator"%>
<%
 MenuItem[] menu = {
         new MenuItem("Home", "home", false, new String[]{"home", "index"}, true,
                 new MenuItem[]{new MenuItem("Log out", "disconnect", true,
                                              new String[]{"Logout","dialogLogout"}),
                                new MenuItem("Log in", "connect", false,
                                              new String[]{"Login","dialogLogin"})}
                 ),
         new MenuItem("Files", "files", true, new String[]{"files"},
                 new MenuItem[]{new MenuItem("New document", "NewFile", true,
                                              new String[]{"NewFile","dialogNewFile"}),
                         new MenuItem("New folder", "NewFolder", true,
                                       new String[]{"NewFolder","dialogNewFolder"})}
                 ),
         new MenuItem("HowTos", "howto", true, new String[]{"howto"})
 };

%>
<ul id='primary'>
<%
    String getParam= param;
    boolean isAuthenticated = sessionData.isAuthenticated(request, session);
    for (int i = 0; i < menu.length; i++){
        MenuItem m = menu[i];
        int display = 0;
        try {
            display = m.IsLevelOneDisplayable(getParam, isAuthenticated);
        } catch (Exception e) {
            if (e.getMessage().equals(MenuItem.INVALID_PARAMETER))
                response.sendRedirect(".");
        }
        if (display == MenuItem.DISPLAY_CURRENT){
        %>
        <li>
            <span><%=m._displayTitle%></span>
        <%Iterator it = new ObjectArrayIterator(m._childMenus);
          if (it.hasNext()){%>
            <ul id='secondary'>
          <%}
          while(it.hasNext()){
            MenuItem subMenu =(MenuItem)it.next();
            if (subMenu.IsLevelTwoDisplayable(getParam,isAuthenticated)== MenuItem.DISPLAY_LINK){%>
            <li>
                <a href="<%=subMenu._displayTitle%>"
                   onclick="return popDialog('<%=subMenu._retrieveStrings[0]%>',function(){<%=subMenu._retrieveStrings[1]%>();});">
                        <%=subMenu._displayTitle%>
                </a>
            </li>
            <%}

            }
            if (m._childMenus.length != 0){%>
            </ul>
              <%}%>
        </li>
            <%}
        else if (display == MenuItem.DISPLAY_LINK){%>
        <li><a href="<%=m._displayTitle%>" onclick ="return page('<%=m._fileName%>');"><%=m._displayTitle%></a></li>
            <%}
    }%>
</ul>
