package org.pengyou.client.web.store.webDav;

import org.apache.webdav.lib.WebdavResource;
import org.apache.webdav.lib.methods.XMLResponseMethodBase;
import org.apache.log4j.Logger;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.util.URIUtil;
import org.pengyou.client.web.ConfigurationSingleton;
import org.dom4j.io.SAXReader;
import org.dom4j.Document;

import java.io.*;
import java.util.*;
import java.util.zip.ZipOutputStream;
import java.util.zip.ZipEntry;
import java.text.SimpleDateFormat;

public class WebDavStore {
    private static Logger log = Logger.getLogger(WebDavStore.class);
    private String connectionUrl;// = "http://jeremi.info";//"http://10.142.7.146";
    private int connectionPort;// = 8080 ;
    private String connectionBaseDirectory;// = "/pengyou/repository/default/" ;
    private String connectionUsername;// = "totto" ;
    private String connectionPassword;// = ".toto" ;
    private String filePath = "";
    private HttpURL httpURL = null;

    /**
     * The WebDAV resource.
     */
    private WebdavResource webdavResource = null;


    /**
     * Debug level for all debug messages
     */
    final static int DEBUG_ON = Integer.MAX_VALUE;

    /**
     * Debug level for no debug messages
     */
    final static int DEBUG_OFF = 0;
    /**
     * The debug level.
     */
    private int debugLevel = DEBUG_OFF;
    private ConfigurationSingleton configuration = ConfigurationSingleton.getInstance();

    public WebDavStore() {
        connect();
    }

    public WebDavStore(String filePath) {
        this.filePath = filePath;
        connect();
    }


    public void connect() {
        connectionUrl = configuration.getConfigurationValue("Server", "url");
        connectionPort = Integer.valueOf(configuration.getConfigurationValue("Server", "port")).intValue();
        connectionUsername = configuration.GetUsername();
        connectionPassword = configuration.GetPassword();
        connectionBaseDirectory = configuration.getConfigurationValue("Server", "basedirectory");
        String path = buildFilePath(this.filePath);
        connect(connectionUrl + ":" + connectionPort, path, connectionUsername, connectionPassword);
    }

    private void connect(String uri, String filePath, String login, String password) {

        if (!uri.endsWith("/") && !uri.endsWith("\\") && !filePath.startsWith("/") && !filePath.startsWith("\\")) {
            // append / to the path

            uri += "/";
        }

        if ((uri.endsWith("/") || uri.endsWith("\\")) && (filePath.startsWith("/") || filePath.startsWith("\\"))) {
            // remove / from the path

            uri = uri.substring(0, uri.length() - 1);
        }

        uri = uri + filePath;

        log.info("connect " + uri);

        webdavResource = allocResource(webdavResource, uri, login, password, filePath);
    }

    private WebdavResource allocResource(WebdavResource resource, String uri, String login, String password, String filePath) {
        try {
            // Set up for processing WebDAV resources
            httpURL = uriToHttpURL(uri, login, password);
            if (resource == null) {
                UsernamePasswordCredentials cred = new UsernamePasswordCredentials(
                        configuration.getConfigurationValue("Proxy", "username"),
                        configuration.getConfigurationValue("Proxy", "password"));

                if (configuration.getConfigurationValue("Proxy", "proxyUse").equals("True")) {
                    resource = new WebdavResource(httpURL, filePath,configuration.getConfigurationValue("Proxy", "host"),
                                                new Integer(configuration.getConfigurationValue("Proxy", "port")).intValue(),cred, true);

                }
                else
                    resource = new WebdavResource(httpURL, filePath);
                resource.setDebug(debugLevel);
            } else {
                resource.close();
                resource.setHttpURL(httpURL);
            }

        }
        catch (Exception ex) {
            handleException(ex);
            resource = null;
            httpURL = null;
        }
        return resource;
    }

    private String buildFilePath(String filePath) {
        String path = connectionBaseDirectory;
        if (!path.endsWith("/") && !path.endsWith("\\") && !filePath.startsWith("/") && !filePath.startsWith("\\")) {
            // append / to the path

            path += "/";
        }

        if ((path.endsWith("/") || path.endsWith("\\")) && (filePath.startsWith("/") || filePath.startsWith("\\"))) {
            // remove / from the path

            path = path.substring(0, path.length() - 1);
        }
        return path + filePath;
    }

    public boolean readContentAsStream(OutputStream ostream, boolean closeStream) throws Exception {
        if (webdavResource.getIsCollection())
            throw new Exception("this is not a file");

        if (webdavResource.getMethod(ostream, closeStream))
            return true;
        else
            throw new Exception("Error " + webdavResource.getStatusCode() + " during retriving the file");
    }

    public boolean upload(InputStream stream) throws IOException {
        boolean resourceExists = isExist();
        if (resourceExists)
            webdavResource.checkoutMethod();
        boolean putStatus = webdavResource.putMethod(stream);
        if (resourceExists)
            webdavResource.checkinMethod();
        return putStatus;
    }

    public Vector getVersions() throws HttpException {
        Vector versionVector = new Vector();
        try {
            Vector properties = new Vector();
            properties.add("version-name");
            properties.add("creator-displayname");
            properties.add("getlastmodified");
            properties.add("getcontentlength");
            Enumeration versions = webdavResource.reportMethod(webdavResource.getHttpURL(), 1);

            while (versions.hasMoreElements()) {
                
                String filename = (String) versions.nextElement();

                filename = URIUtil.decode(filename);
                WebdavResource res;
                res = allocResource(null, filename, connectionUsername, connectionPassword, filename.substring(connectionUrl.length() + 1 + String.valueOf(connectionPort).length()));
                Vector versionInfo = new Vector();
                versionInfo.add(webdavResource.getDisplayName());
                versionInfo.add(res.getDisplayName());
                versionInfo.add(filename.substring(filename.indexOf(connectionBaseDirectory)+connectionBaseDirectory.length()-1));
                SimpleDateFormat dateFormat = new SimpleDateFormat(configuration.getConfigurationValue("Application","dateformat"));
                Date lastModified = new Date(res.getGetLastModified());
                versionInfo.add(dateFormat.format(lastModified));
                versionInfo.add(res.getCreatorDisplayName());
                versionVector.add(versionInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return versionVector;
    }

    public boolean move(String src, String dest) throws IOException {
        return webdavResource.moveMethod(src, dest);
    }
/*    public boolean updateProperties()
    {
        try {
            webdavResource.setProperties(WebdavResource.BASIC, 0);
        } catch (IOException e) {
            log.error(e);
            return false;
        }
        return true;
    }    */

    public boolean isExist() {
        return webdavResource.getExistence();
    }

    public boolean isCollection() {
        return webdavResource.isCollection();
    }


    public String getDisplayName() {
        return webdavResource.getDisplayName();
    }

    public String getLocation() {
        return webdavResource.getPath().substring(connectionBaseDirectory.length() - 1);
    }

    public String getLastModified() {
        Date date = new Date(webdavResource.getGetLastModified());
        return date.toString();
    }

    public String getOwner() {
        return webdavResource.getCreatorDisplayName();
    }

    public long getContentLength() {
        return webdavResource.getGetContentLength();
    }

    public String getContentLengthDisplayable() {
        float len = webdavResource.getGetContentLength();
        String units = "bits";
        if (len >= 128 && len < 1024) {
            len /= 128;
            units = "bytes";
        } else if (len >= 1024 && len < 1048576) {
            len /= 1024;
            units = "kilobytes";
        } else if (len >= 1048576) {
            len /= 1048576;
            units = "megabytes";
        }
        String size = String.valueOf(len);
        return size.substring(0, size.indexOf(".") + 2) + " " + units;
    }

    public String getLastComment() {
        return webdavResource.getComment();
    }

    public class sortList implements Comparator {
        int sort;

        public sortList(int cell) {
            sort = cell;
        }

        public void SortBy(int cell) {
            sort = cell;
        }

        public int compare(Object o1, Object o2) {
            String s1 = (String) (((String[]) o1)[sort]);
            String s2 = (String) (((String[]) o2)[sort]);
            if (sort == 2) {
                if (s1.equals("COLLECTION")) s1 = ".COLLECTION";
                if (s2.equals("COLLECTION")) s2 = ".COLLECTION";
            }
            return s1.compareToIgnoreCase(s2);
        }
    }

    public Vector listBasic(){
        try {
            Vector list = webdavResource.listBasic();
            Comparator compare = new sortList(0);
            Collections.sort(list, compare);
            ((sortList) compare).SortBy(2);
            Collections.sort(list, compare);
            return list;
        }
        catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public void moveFrom(Collection listFiles) throws IOException {
        Iterator it = listFiles.iterator();
        while (it.hasNext()) {
            moveFrom((String) it.next());
        }
    }

    public boolean moveFrom(String fileName) throws IOException {
        boolean val = webdavResource.moveMethod(buildFilePath(fileName), buildFilePath(this.filePath) + fileName.substring(fileName.lastIndexOf("/") + 1, fileName.length()));
        return val;
    }

    public void copyFrom(Collection listFiles) throws IOException {
        Iterator it = listFiles.iterator();
        while (it.hasNext()) {
            copyFrom((String) it.next());
        }
    }

    public boolean copyFrom(String fileName) throws IOException {

        boolean val = webdavResource.copyMethod(buildFilePath(fileName), buildFilePath(this.filePath) + fileName.substring(fileName.lastIndexOf("/") + 1, fileName.length()));
        return val;
    }

    public boolean createFolder() throws IOException {
        return webdavResource.mkcolMethod();
    }

    public void deleteResource(Collection listFiles) throws IOException {
        Iterator it = listFiles.iterator();
        while (it.hasNext()) {
            deleteResource((String) it.next());
        }
    }

    public boolean deleteResource(String fileName) throws IOException {
        return webdavResource.deleteMethod(buildFilePath(fileName));
    }

    public void exportZip(Collection files, OutputStream oStream) throws Exception {
        ZipOutputStream zout = new ZipOutputStream(oStream);
        zout.setMethod(ZipOutputStream.DEFLATED);
        Iterator it = files.iterator();
        while (it.hasNext()) {
            String path = (String) it.next();
            exportZipEntry(path, zout);
        }
        zout.close();
        oStream.close();
    }

    /**
     * If the specified node is the defined non-collection nodetype a new
     * Zip entry is created and the exportContent is called on the IOManager
     * defined with this handler. If in contrast the specified node does not
     * represent a non-collection this method is called recursively for all
     * child nodes.
     *
     * @param zout
     * @throws IOException
     */
    private void exportZipEntry(String path, ZipOutputStream zout) throws Exception {
        WebDavStore file = new WebDavStore(path);
        if (!file.isCollection()) {
            if (path.startsWith("/"))
                path = path.substring(1);
            ZipEntry zEntry = new ZipEntry(path);
            zout.putNextEntry(zEntry);
            file.readContentAsStream(zout, false);
        } else {
            Vector list = file.listBasic();
            Iterator it = list.iterator();
            while (it.hasNext()) {
                exportZipEntry((path.lastIndexOf("/") == path.length() ? path : path + "/") + ((String[]) it.next())[0], zout);
            }
        }
    }

    private void handleException(Exception ex) {
        if (ex instanceof HttpException) {
            if (((HttpException) ex).getReasonCode() == HttpStatus.SC_METHOD_NOT_ALLOWED) {
                log.error("Not WebDAV-enabled?");
            } else if (((HttpException) ex).getReasonCode() == HttpStatus.SC_UNAUTHORIZED) {
                log.error("Unauthorized");
            } else {
                log.error(ex.getMessage(), ex);
            }
        } else if (ex instanceof IOException) {
            log.error(ex.getMessage(), ex);
        } else {
            log.error(ex.getMessage(), ex);
        }
    }

    void disconnect() {
        log.info("disconnect");
        try {
            webdavResource.close();
        } catch (IOException e) {
        } finally {
            // Make sure the connection closed.
            httpURL = null;
            webdavResource = null;
        }
    }

    private static HttpURL uriToHttpURL(String uri, String login, String password) throws URIException {
        HttpURL httpURL = uri.startsWith("https") ? new HttpsURL(uri)
                : new HttpURL(uri);
        if (login != null && login.length() > 0)
            httpURL.setUserinfo(login, password);
        return httpURL;

    }

}
