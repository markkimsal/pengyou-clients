package org.pengyou.client.web;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Dec 11, 2006
 * Time: 5:29:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class MenuItem {
    public static final int DISPLAY_NONE = 0;
    public static final int DISPLAY_LINK = 1;
    public static final int DISPLAY_CURRENT = 2;
    public static final String INVALID_PARAMETER = "Invamid Parameter";

    public String _displayTitle;
    public String _fileName;
    public boolean _requireAuth;
    public String[] _retrieveStrings;
    public boolean _nullRetrieves;
    public MenuItem[] _childMenus;

    public MenuItem(String displayTitle, String fileName, boolean requireAuth, String[] retrieveStrings,
                    boolean nullRetrieves, MenuItem[] childMenus) {
        _displayTitle = displayTitle;
        _fileName = fileName;
        _requireAuth = requireAuth;
        _retrieveStrings = retrieveStrings;
        _nullRetrieves = nullRetrieves;
        _childMenus = childMenus;

    }

    public MenuItem(String displayTitle, String fileName, boolean requireAuth, String[] retrieveStrings) {
        _displayTitle = displayTitle;
        _fileName = fileName;
        _requireAuth = requireAuth;
        _retrieveStrings = retrieveStrings;
        _nullRetrieves = false;
        _childMenus = new MenuItem[]{};
    }

    public MenuItem(String displayTitle, String fileName, boolean requireAuth, String[] retrieveStrings, MenuItem[] childMenus) {
        _displayTitle = displayTitle;
        _fileName = fileName;
        _requireAuth = requireAuth;
        _retrieveStrings = retrieveStrings;
        _nullRetrieves = false;
        _childMenus = childMenus;
    }

    private boolean isInRetrieveStrings(String getParam) {
        if (getParam == null)
            return false;
        int i;
        for (i = 0; i < _retrieveStrings.length; i++)
            if (_retrieveStrings[i].equals(getParam))
                return true;
        return false;
    }

    public int IsLevelOneDisplayable(String getParam, boolean isAuthenticated) throws Exception {
        if (isInRetrieveStrings(getParam) && isAuthenticated == false)
            throw new Exception(INVALID_PARAMETER);
        if ((isInRetrieveStrings(getParam) && isAuthenticated == true) || (_nullRetrieves == true && getParam == null))
            return DISPLAY_CURRENT;
        if ((_requireAuth == true && isAuthenticated == true) || _requireAuth == false)
            return DISPLAY_LINK;
        return DISPLAY_NONE;
    }

    public int IsLevelTwoDisplayable(String getParam, boolean isAuthenticated) {
        if (_requireAuth == isAuthenticated)
            return DISPLAY_LINK;
        return DISPLAY_NONE;

    }
}