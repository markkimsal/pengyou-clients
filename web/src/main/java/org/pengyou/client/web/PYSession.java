package org.pengyou.client.web;


import org.pengyou.client.web.store.webDav.WebDavStore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.io.IOException;
import java.lang.reflect.Array;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Date: May 27, 2006
 * Time: 12:33:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class PYSession {
    String action;
    String target;
    String usr;
    String pwd;
    String errorBuff;
    Map ClipBoard;

    public PYSession(HttpServletRequest request, HttpSession session){
        this.usr="";
        this.pwd="";
        ClipBoard= new HashMap();
    }

    public void storeData(HttpServletResponse response, HttpServletRequest request, HttpSession session) throws IOException {
        target = request.getParameter("target");
        if (target != null){
           if (target.equals("ClipBoard"))
            manageClipBoard(response,request,session);
           else if (target.equals("SideBox")) {
               manageExpandedBox(request,session);
           }
           else if (target.equals("FileList")) {
               manageFileList(request,session);
           }

        }
    }


    public void manageFileList(HttpServletRequest request, HttpSession session) throws IOException {
        String path=request.getParameter("path");
        String action=request.getParameter("action");
        if (action!=null && action.equals("createFolder"))
            if (path != null && path.equals("") == false){
                WebDavStore store= new WebDavStore((path.endsWith("/") == true ? path:path+"/")+request.getParameter("name"));
                store.createFolder();
            }
    }

    public void manageExpandedBox(HttpServletRequest request, HttpSession session){
        action = request.getParameter("action");
        if (action != null){
            String box = request.getParameter("box");
            if (action.equals("expand"))
                setExpandedBox(request,session, box);
            else
                setExpandedBox(request,session, "");

        }
    }

    public void removeFromClipBoard(String filename){
        ClipBoard.remove(filename);
    }

    public void manageClipBoard(HttpServletResponse response, HttpServletRequest request, HttpSession session) throws IOException {
        action = request.getParameter("action");
        if (action != null){
            String file = request.getParameter("file");

            if (action.equals("select"))
            {
                String iconSmall=request.getParameter("icon");
                ClipBoard.put(file,iconSmall);
            }

            else if (action.equals("unselect"))
                removeFromClipBoard(file);
            else if (action.equals("move"))
                moveClipBoardItems(request, session);
            else if (action.equals("copy"))
               copyClipBoardItems(request, session);
            else if (action.equals("compress"))
               compressClipBoardItems(response, session);
            else if (action.equals("remove"))
                removeClipBoardItems(request, session);
        }
    }

    public Map getClipBoard(HttpServletRequest request, HttpSession session)
    {return ClipBoard;}

    public void removeClipBoardItems(HttpServletRequest request, HttpSession session) throws IOException {
      WebDavStore store=new WebDavStore(request.getParameter("path"));
      store.deleteResource(ClipBoard.keySet());
      ClipBoard.clear();
    }

    public boolean moveClipBoardItems(HttpServletRequest request, HttpSession session) throws IOException {
        if (request.getParameter("path") != null && request.getParameter("path").equals("") == false){
            WebDavStore store=new WebDavStore(request.getParameter("path").endsWith("/") == true?request.getParameter("path"):request.getParameter("path")+"/");
            store.moveFrom(ClipBoard.keySet());
            ClipBoard.clear();
        }
        return true;
    }

    public boolean compressClipBoardItems(HttpServletResponse response, HttpSession session) throws IOException {
            WebDavStore store=new WebDavStore("/");
        if (ClipBoard.size()!=0){
        try {
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=\"Export.zip\"");
            store.exportZip(ClipBoard.keySet(),response.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        }
        return true;
    }

    public boolean copyClipBoardItems(HttpServletRequest request, HttpSession session) throws IOException {
        if (request.getParameter("path") != null && request.getParameter("path").equals("") == false){
            WebDavStore store=new WebDavStore(request.getParameter("path").endsWith("/") == true?request.getParameter("path"):request.getParameter("path")+"/");
            store.copyFrom(ClipBoard.keySet());
            ClipBoard.clear();
        }
        return true;
    }

    public void    setExpandedBox(HttpServletRequest request, HttpSession session, String boxName)
    {session.setAttribute("ExpandedBox", boxName);}

    public String   getExpandedBox(HttpServletRequest request, HttpSession session)
    {
        String expandedBox = (String) session.getAttribute("ExpandedBox");
        if (expandedBox == null)
        {
            session.setAttribute("ExpandedBox","");
            expandedBox = "";
        }
        return expandedBox;
    }

    public boolean     authenticate(HttpServletRequest request)
    {
        if (request.getParameter("login") != null && request.getParameter("login").equals("0"))
        {
            this.usr="";
            this.pwd="";
            ConfigurationSingleton.SetUsername(this.usr);
            ConfigurationSingleton.SetPassword(this.pwd);
            ClipBoard.clear();
            return true;
        }

        if(request.getParameter("user")==null ||request.getParameter("pwd")==null)
            return false;
        this.usr=request.getParameter("user");
        ConfigurationSingleton.SetUsername(this.usr);
        this.pwd=request.getParameter("pwd");
        ConfigurationSingleton.SetPassword(this.pwd);
        return true;
}

    public String getUsr() {
        return usr;
    }

    public String getPwd() {
        return pwd;
    }

    public void setUsr(String usr) {
        this.usr = usr;
    }

    public boolean isAuthenticated(HttpServletRequest request, HttpSession session)
    {
        return (this.usr.equals("") || this.pwd.equals("")) ? false : true;
    }
}
