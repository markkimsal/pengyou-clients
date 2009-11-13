package org.apache.webdav.lib;

import junit.framework.TestCase;
import org.apache.commons.httpclient.HttpURL;
import org.apache.commons.httpclient.HttpsURL;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: jeremi
 * Date: Oct 7, 2006
 * Time: 10:03:44 AM
 * To change this template use File | Settings | File Templates.
 */
public class WebdavResourceTest extends TestCase {
    private final String connectionUrl = "http://127.0.0.1" ;
    private final int connectionPort = 8080 ;
    private final String connectionBaseDirectory = "/pengyou/repository/default/" ;
    private final String connectionUsername = "toto" ;
    private final String connectionPassword = "titi" ;

    public final String COMMENT_AUTHOR = "author";
    public final String COMMENT_TEXT = "text";
    public final String COMMENT_DATE = "date";

    public final int COMMENT_AUTHOR_POS = 0;
    public final int COMMENT_TEXT_POS = 1;
    public final int COMMENT_DATE_POS = 2;

    public void testReport() throws IOException {
        WebdavResource res = connect("/contributors.txt");
        assertTrue(res.checkoutMethod());
        res.proppatchMethod("D:comment-8732894732-author", "jeremi");
        res.proppatchMethod("D:comment-8732894732-text", "that rox");
        String strdate = (new Date()).toString();
        res.proppatchMethod("D:comment-8732894732-date", strdate);
        assertTrue(res.checkinMethod());

        Enumeration props = res.propfindMethod(res.getHttpURL().getPath(), (Vector)null);
        assertNotNull(props);

        Collection comments = getComments(props);

        assertEquals(1, comments.size());

        String[] comment = (String[]) comments.iterator().next();


        assertEquals("jeremi", comment[COMMENT_AUTHOR_POS]);
        assertEquals("that rox", comment[COMMENT_TEXT_POS]);
        assertEquals(strdate, comment[COMMENT_DATE_POS]);

        assertEquals("jeremi", ((String[]) res.propfindMethod("comment-8732894732-author").nextElement())[1]);
        assertEquals("that rox", ((String[]) res.propfindMethod("comment-8732894732-text").nextElement())[1]);
        assertEquals(strdate, ((String[]) res.propfindMethod("comment-8732894732-date").nextElement())[1]);
    }


    private Collection getComments(Enumeration props){
        Map comments = new HashMap();

        while(props.hasMoreElements()){
            String[] prop = (String[]) props.nextElement();
            if (prop[0].startsWith("D:comment")){
                String key = prop[0].substring(0, prop[0].lastIndexOf("-"));
                if (comments.get(key)== null)
                    comments.put(key, new String[]{"", "", ""});
                
                if (prop[0].endsWith(COMMENT_AUTHOR)){
                    ((String[])comments.get(key))[COMMENT_AUTHOR_POS] = prop[1];
                }
                else if (prop[0].endsWith(COMMENT_DATE)){
                    ((String[])comments.get(key))[COMMENT_DATE_POS] = prop[1];
                }
                else if (prop[0].endsWith(COMMENT_TEXT)){
                    ((String[])comments.get(key))[COMMENT_TEXT_POS] = prop[1];              
                }
            }
        }
        return comments.values();
    }

    private HttpURL uriToHttpURL(String uri, String login, String password) throws org.apache.commons.httpclient.URIException {
        HttpURL httpURL =  uri.startsWith("https") ? new HttpsURL(uri)
                                                    : new HttpURL(uri);
        if (login != null && login.length() > 0)
            httpURL.setUserinfo(login, password);
        return httpURL;

    }

        public WebdavResource connect(String filePath)
    {
        String path = connectionBaseDirectory;
        if (!path.endsWith("/") && !path.endsWith("\\") && !filePath.startsWith("/") && !filePath.startsWith("\\")) {
            // append / to the path

             path += "/";
        }

        if ((path.endsWith("/") || path.endsWith("\\")) && (filePath.startsWith("/") || filePath.startsWith("\\"))) {
            // remove / from the path

             path = path.substring(0, path.length() - 1);
        }

        path = path + filePath;
        return connect(connectionUrl + ":" + connectionPort, path, connectionUsername, connectionPassword);
    }

    private WebdavResource connect(String uri, String filePath, String login, String password)
    {
        /** The WebDAV resource. */
        WebdavResource webdavResource = null;
        HttpURL httpURL = null;

        if (!uri.endsWith("/") && !uri.endsWith("\\") && !filePath.startsWith("/") && !filePath.startsWith("\\")) {
            // append / to the path

             uri+="/";
        }

        if ((uri.endsWith("/") || uri.endsWith("\\")) && (filePath.startsWith("/") || filePath.startsWith("\\"))) {
            // remove / from the path

             uri = uri.substring(0, uri.length() - 1);
        }

        uri = uri + filePath;




        try {
            // Set up for processing WebDAV resources
            httpURL = uriToHttpURL(uri, login, password);
            if (webdavResource == null) {
                webdavResource = new WebdavResource(httpURL, filePath);
                webdavResource.setDebug(0);

            } else {
                webdavResource.close();
                webdavResource.setHttpURL(httpURL);
            }
        }
        catch (Exception ex) {
            webdavResource = null;
            httpURL = null;
        }
        return webdavResource;
    }

}
