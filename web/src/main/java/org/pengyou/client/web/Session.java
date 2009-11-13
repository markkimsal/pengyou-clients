package org.pengyou.client.web;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Date: May 27, 2006
 * Time: 12:33:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class Session {
    String action, target;


    public Session(HttpServletRequest request){
       if (request.getSession().getAttribute("ClipBoard") == null)
          request.getSession().setAttribute("ClipBoard", new ArrayList());
    }
    public void storeData(HttpServletRequest request){
        target = request.getParameter("target");
        if (target != null){
           if (target.equals("ClipBoard"))
            manageClipBoard(request);
           else if (target.equals("SideBox")) {
               manageExpandedBox(request);
           }

        }
    }


    public void manageExpandedBox(HttpServletRequest request){
        action = request.getParameter("action");
        if (action != null){
            String box = request.getParameter("box");
            if (action.equals("expand"))
                setExpandedBox(request, box);
            else
                setExpandedBox(request, "");

        }
    }

    public void manageClipBoard(HttpServletRequest request){
        action = request.getParameter("action");
        if (action != null){
            String file = request.getParameter("file");
            ArrayList ClipBoard = (ArrayList) request.getSession().getAttribute("ClipBoard");
          
            if (action.equals("select"))
                ClipBoard.add(file);
            else
                ClipBoard.remove(file);
            request.getSession().setAttribute("ClipBoard",ClipBoard);
        }
    }

    public Object getClipBoard(HttpServletRequest request)
    {return request.getSession().getAttribute("ClipBoard");}

    public void    setExpandedBox(HttpServletRequest request, String boxName)
    {request.getSession().setAttribute("ExpandedBox", boxName);}

    public String   getExpandedBox(HttpServletRequest request)
    {
        String expandedBox = (String) request.getSession().getAttribute("ExpandedBox");
        if (expandedBox == null)
        {
            request.getSession().setAttribute("ExpandedBox","");
            expandedBox = "";
        }
        return expandedBox;
    }
}
