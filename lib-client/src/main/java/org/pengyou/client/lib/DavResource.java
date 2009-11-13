/* ====================================================================
 *   Copyright 2005 J�r�mi Joslin.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 * ====================================================================
 */

package org.pengyou.client.lib;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.log4j.Logger;
import org.pengyou.client.lib.methods.PropFindMethod;
import org.pengyou.client.lib.methods.DepthSupport;
import org.pengyou.client.lib.methods.DeleteMethod;
import org.pengyou.client.lib.methods.PropPatchMethod;
import org.pengyou.client.lib.properties.ResourceTypeProperty;
import org.pengyou.client.lib.properties.LockDiscoveryProperty;
import org.pengyou.client.lib.properties.ExportProperty;

import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.util.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.text.DateFormat;

public class DavResource extends DavSession {
    protected String        path;
    protected int           statusCode;
    protected boolean       exist;
    protected byte[]        contentBody;

    protected String        displayName;
    protected long          contentLength;

    protected int           defaultDepth = DepthSupport.DEPTH_1;

    protected boolean       propfindExecuted = false;

    protected boolean       isNew = false;

    private Logger log = Logger.getLogger(DavResource.class);

    /**
     * list of available export format
     */
    protected List exportFormat = null;


    /**
     * An WebDAV property, contenttype.
     */
    protected String contentType = "";



   /**
     * An WebDAV property, resourcetype.
     */
    protected ResourceTypeProperty resourceType;


    /**
     * An WebDAV property, getlastmodified.
     */
    protected long lastModified;


    /**
     * An WebDAV property, creationdate.
     */
    protected long creationDate;


    /**
     * An WebDAV property, getetag.
     */
    protected String etag = "";

    /**
     * Owner information for locking and unlocking.
     */
    protected String owner = null;


    /**
     * An WebDAV property, ishidden.
     */
    protected boolean isHidden;


    /**
     * An WebDAV property, iscollection.
     */
   // protected boolean isCollection;


    /**
     * An WebDAV property, supportedlock.
     */
    protected String supportedLock = "";


    /**
     * An WebDAV property, lockdiscovery.
     */
    protected LockDiscoveryProperty lockDiscovery;


    // -------------------------------------- Constants for WebDAV properties.


     /**
      * The displayname property.
      */
     public static final String DISPLAYNAME = "displayname";


     /**
      * The getcontentlanguage property.
      */
     public static final String GETCONTENTLANGUAGE = "getcontentlanguage";


     /**
      * The getcontentlength property.
      */
     public static final String GETCONTENTLENGTH = "getcontentlength";


     /**
      * The getlastmodifed property.
      */
     public static final String GETLASTMODIFIED = "getlastmodified";


     /**
      * The creationdate property.
      */
     public static final String CREATIONDATE = "creationdate";


     /**
      * The resourcetype property.
      */
     public static final String RESOURCETYPE = "resourcetype";


     /**
      * The source property.
      */
     public static final String SOURCE = "source";


     /**
      * The getcontenttype property.
      */
     public static final String GETCONTENTTYPE = "getcontenttype";


     /**
      * The getetag property.
      */
     public static final String GETETAG = "getetag";


     /**
      * The ishidden property.
      */
     public static final String ISHIDDEN = "ishidden";


     /**
      * The iscollection property.
      */
     public static final String ISCOLLECTION = "iscollection";


     /**
      * The supportedlock property.
      */
     public static final String SUPPORTEDLOCK = "supportedlock";


     /**
      * The lockdiscovery property.
      */
     public static final String LOCKDISCOVERY = "lockdiscovery";

    /**
      * The lockdiscovery property.
      */
     public static final String PROPERTYEXPORTFORMAT = "exportformat";


        /**
     * Date formats using for Date parsing.
     */
    public static final SimpleDateFormat formats[] = {
        new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US),
            new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US),
            new SimpleDateFormat("EEEEEE, dd-MMM-yy HH:mm:ss zzz", Locale.US),
            new SimpleDateFormat("EEE MMMM d HH:mm:ss yyyy", Locale.US),
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US),
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'Z'", Locale.US)
    };


    /**
     * Table of the hrefs gotten in a collection.
     */
    protected WebdavResources childResources = new WebdavResources();
    private boolean followRedirects;

    public DavResource(String path, DavContext context)
    {
        setPath(path);
        this.context = context;
    }

    public InputStream getContentAsStream() throws IOException {
        return getContentAsStream(false);
    }

    public InputStream getContentAsStream(boolean forceReload) throws IOException {
        if ((contentBody != null && contentBody.length > 0 && !forceReload) || getMethods())
            return new ByteArrayInputStream(contentBody);
        return null;
    }

    /**
     * write the content to an output Stream
     * @param oStream
     * @param forceReload
     * @throws IOException
     */
    public void getContentAsStream(OutputStream oStream, boolean forceReload) throws IOException {
        if ((contentBody != null && contentBody.length > 0 && !forceReload) || getMethods())
            oStream.write(contentBody);
    }


    /**
     * write the content to an output Stream
     * @param oStream
     * @throws IOException
     */
    public void getContentAsStream(OutputStream oStream) throws IOException {
        getContentAsStream(oStream, false);
    }

    public void getExportContentAsStream(String fileExtension, OutputStream oStream) throws IOException {
        if(!getExportFormat().contains(fileExtension))
            throw new WebdavException(fileExtension + " is not a known export format");
        oStream.write(getMethods(fileExtension));

    }


    public void setContent(InputStream iStream) throws IOException {
        byte[] bytes = new byte[iStream.available()];
        iStream.read(bytes, 0, bytes.length);
        this.contentBody = bytes;
    }

    public boolean isCollection() throws IOException {
        return (getResourceType() != null ? getResourceType().isCollection() : null);
    }

    public ResourceTypeProperty getResourceType() throws IOException {
        if (!propfindExecuted)
            setAllProp();
        return resourceType;
    }

    protected void setLockDiscovery(LockDiscoveryProperty lockDiscovery) {
        this.lockDiscovery = lockDiscovery;
    }

    public String getDisplayName() throws IOException {
        if (!propfindExecuted)
            setAllProp();
        return displayName;
    }


    public String getPath() {
        try {
            return URIUtil.decode(path);
        } catch (URIException e) {
            return path;
        }
    }

    public int getStatusCode() {
        return statusCode;
    }

    public boolean isExist() throws IOException {
        if (!propfindExecuted && !this.isNew)
            setAllProp();
        return exist;
    }

    public long getContentLength() throws IOException {
        if (!propfindExecuted && !this.isNew)
            setAllProp();
        return contentLength;
    }

    public String getContentType() throws IOException {
        if (!propfindExecuted && !this.isNew)
            setAllProp();
        return contentType;
    }

    public long getLastModified() throws IOException {
        if (!propfindExecuted && !this.isNew)
            setAllProp();
        return lastModified;
    }

    public long getCreationDate() throws IOException {
        if (!propfindExecuted && !this.isNew)
            setAllProp();
        return creationDate;
    }

    public String getEtag() throws IOException {
        if (!propfindExecuted && !this.isNew)
            setAllProp();
        return etag;
    }

    public String getOwner() throws IOException {
        if (!propfindExecuted && !this.isNew)
            setAllProp();
        return owner;
    }

    public boolean isHidden() throws IOException {
        if (!propfindExecuted && !this.isNew)
            setAllProp();
        return isHidden;
    }

    public String getSupportedLock() throws IOException {
        if (!propfindExecuted && !this.isNew)
            setAllProp();
        return supportedLock;
    }

    public String getFullUrl() throws URIException {
        return context.getHttpURL() + ":" + context.getPort() + context.getBaseUrl() + path;
    }

    public String getHashCode() throws IOException {
        return  "R" + getDisplayName().hashCode() + "" + getCreationDate() + "" + getLastModified();
    }

    public List getExportFormat() throws IOException {
        if (!propfindExecuted && !this.isNew)
            setAllProp();
        return exportFormat;
    }

    public boolean isNew() throws IOException {
        if (!propfindExecuted && !this.isNew)
            setAllProp();
        return isNew;
    }

    public void setNew(boolean value)
    {
        this.isNew = value;
    }

    protected void setExistence(boolean exists) {
        this.exist = exists;
    }

    public void setPath(String path) {
        path = ((path.length() > 0) && path.charAt(0) == '/' ? path.substring(1) : path);
        try {
            this.path = URIUtil.encodePath(path);
        } catch (URIException e) {
            this.path = path;
            log.warn("can't encode the path ", e);
        }
    }

    protected void setEncodedPath(String path) {
        try {
            setPath(URIUtil.decode(path));
        } catch (URIException e) {
            this.path = path;
            log.warn("can't decode the path ", e);
        }
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }



    /**
     * Execute the DELETE method for the given path.
     *
     * @return true if the method is succeeded.
     * @exception HttpException
     * @exception IOException
     */
    protected boolean deleteMethod()
        throws HttpException, IOException {

        HttpClient client  = getSessionInstance();
        DeleteMethod method = new DeleteMethod(context.getBaseUrl() + path);
        method.setDebug(debug);
        method.setFollowRedirects(this.followRedirects);

        generateIfHeader(method);
        generateTransactionHeader(method);
        int statusCode = client.executeMethod(method);

        this.statusCode = statusCode;
        return (statusCode >= 200 && statusCode < 300);
    }


    /**
     * Execute a get on the server and fill in the contentBody
     * @return
     * @throws IOException
     */
    private boolean getMethods() throws IOException {

        try{
            this.contentBody = getMethods(null);
            return true;
        }
        catch(HttpException e) {
            return false;
        }
    }

    private byte[] getMethods(String fileExtension) throws IOException {
        HttpClient    client;

        client = getSessionInstance();
        GetMethod method = null;
        if (fileExtension == null)
            method = new GetMethod(context.getBaseUrl() + path);
        else
            method = new GetMethod(context.getBaseUrl() + path + "?OutputFormat=" + fileExtension);

        int statusCode = client.executeMethod(method);

        this.statusCode = statusCode;

        // get the file only if status is any kind of OK
        if (statusCode >= 200 && statusCode < 300)
            return method.getResponseBody();
        else {
            HttpException e =  new HttpException("can't get the resource");
            e.setReasonCode(statusCode);
            throw e;
        }
    }

    private Enumeration propfindMethod(int depth)
        throws HttpException, IOException {

        HttpClient client = getSessionInstance();
        // Change the depth for allprop
        PropFindMethod method = new PropFindMethod(context.getBaseUrl() + this.path, depth);

        method.setDebug(debug);

        // Default depth=infinity, type=allprop
        //generateTransactionHeader(method);
        int status = client.executeMethod(method);

        // Set status code.
        this.statusCode = status ;

        // Also accept OK sent by buggy servers.
        if (status != HttpStatus.SC_MULTI_STATUS
            && status != HttpStatus.SC_OK) {
            HttpException ex = new HttpException();
            ex.setReasonCode(status);
            throw ex;
        }
        //setWebdavProperties(method.getResponses());
        return method.getResponses();
    }


    /**
     * Execute PROPATCH method for the specified resource with the given
     * property.
     *
     * @param path the server relative path of the resource to act on
     * @param propertyName the property name string (in "DAV:" namespace)
     * @param propertyValue the property value string
     * If the proppatch action is being removed, the value is null or any.
     * @param action true if it's to be set, false if it's to be removed
     * @return true if the method is succeeded
     * @exception HttpException
     * @exception IOException
     */
    public boolean proppatchMethod(String propertyName,
                                   String propertyValue, boolean action)
        throws HttpException, IOException {

        Hashtable property = new Hashtable();
        property.put(propertyName, propertyValue);
        return proppatchMethod(property, action);
    }


       /**
     * Execute PROPATCH method for the specified resource with the given
     * properties.
     *
     * @param path the server relative path of the resource to act on
     * @param properties the name(= <code>String</code> or <code>PropertyName
     * </code> and value(= <code>String</code>) pairs for proppatch action
     * If the proppatch action is being removed, the value is null or any.
     * @param action true if it's being set, false if it's being removed
     * @return true if the method is succeeded.
     * @exception HttpException
     * @exception IOException
     */
    public boolean proppatchMethod(Hashtable properties,
                                   boolean action) throws HttpException, IOException {

        HttpClient client = getSessionInstance();
        PropPatchMethod method = new PropPatchMethod(context.getBaseUrl() + this.path);
        method.setDebug(debug);
        method.setFollowRedirects(this.followRedirects);

        generateIfHeader(method);
        Enumeration names = properties.keys();
        boolean hasSomething = false;
        if (names.hasMoreElements()) {
            hasSomething = true;
        }
        while (names.hasMoreElements()) {
            Object item = names.nextElement();
            if (item instanceof String) {
                String name = (String) item;
                String value = (String) properties.get(item);
                if (action) {
                    method.addPropertyToSet(name, value);
                } else {
                    method.addPropertyToRemove(name);
                }
            } else if (item instanceof PropertyName) {
                String name         = ((PropertyName) item).getLocalName();
                String namespaceURI = ((PropertyName) item).getNamespaceURI();
                String value        = (String) properties.get(item);
                if (action) {
                    method.addPropertyToSet(name, value, null, namespaceURI);
                } else {
                    method.addPropertyToRemove(name, null, namespaceURI);
                }
            } else {
                // unknown type, debug or ignore it
            }
        }
        if (hasSomething) {
            generateTransactionHeader(method);
            int statusCode = client.executeMethod(method);
            // Possbile Status Codes => SC_OK
            // WebdavStatus.SC_FORBIDDEN, SC_CONFLICT, SC_LOCKED, 507
            this.statusCode = statusCode;
            if (statusCode >= 200 && statusCode < 300) {
                return true;
            }
        }
        return false;
    }



    /**
     * save a file on the server
     * @return true if the save is Ok
     * @throws IOException
     *
     * TODO: generate an exception if the body is empty
     */
    public boolean save() throws IOException {
        if(this.contentBody != null)
            return this.putMethod();
        return false;
    }

    /**
     * Execute the PUT method for the given path.
     *
     * @param path the server relative path to put the data
     * @param is The input stream.
     * @return true if the method is succeeded.
     * @exception HttpException
     * @exception IOException
     */
    protected boolean putMethod()
        throws HttpException, IOException {

        log.debug("putMethod");
        InputStream is = new ByteArrayInputStream(this.contentBody);
        HttpClient client = getSessionInstance();
        PutMethod method = new PutMethod(context.getBaseUrl() + path);
        generateIfHeader(method);
        if (getContentType() != null && !getContentType().equals(""))
            method.setRequestHeader("Content-Type", getContentType());
        method.setRequestContentLength(PutMethod.CONTENT_LENGTH_CHUNKED);
        method.setRequestBody(is);
        generateTransactionHeader(method);
        int statusCode = client.executeMethod(method);

        this.statusCode = statusCode;
        return (statusCode >= 200 && statusCode < 300) ? true : false;
    }



    /**
     * Generates and adds the "Transaction" header if this method is part of
     * an externally controlled transaction.
     */
    protected void generateTransactionHeader(HttpMethod method) {
        if (this.client == null || method == null) return;

        WebdavState state = (WebdavState) this.client.getState();
        String txHandle = state.getTransactionHandle();
        if (txHandle != null) {
            method.setRequestHeader("Transaction", "<" + txHandle + ">");
        }
    }

    /**
     * Generate and add the If header to the specified HTTP method.
     */
    protected void generateIfHeader(HttpMethod method) {

        if (client == null) return;
        if (method == null) return;

        WebdavState state = (WebdavState) this.client.getState();
        String[] lockTokens = state.getAllLocks(method.getPath());

        if (lockTokens.length == 0) return;

        StringBuffer ifHeaderValue = new StringBuffer();

        for (int i = 0; i < lockTokens.length; i++) {
            ifHeaderValue.append("(<").append(lockTokens[i]).append(">) ");
        }

        method.setRequestHeader("If", ifHeaderValue.toString());

    }


        /**
     * Set WebDAV properties following to the given http URL.
     * This method is fundamental for getting information of a collection.
     *
     * @param responses An enumeration over {@link ResponseEntity} items, one
     * for each resource for which information was returned via PROPFIND.
     *
     * @exception HttpException
     * @exception IOException The socket error with a server.
     */
    protected void setWebdavProperties(Enumeration responses)
        throws HttpException, IOException {

        // Make the resources in the collection empty.
        childResources.removeAll();
        while (responses.hasMoreElements()) {

            ResponseEntity response =
                (ResponseEntity) responses.nextElement();

            boolean itself = false;
            String href = response.getHref();
            if (!href.startsWith("/"))
                href = URIUtil.getPath(href);
            href = decodeMarks(href);

            /*
             * Decode URIs to common (unescaped) format for comparison
             * as HttpClient.URI.setPath() doesn't escape $ and : chars.
             */
            //String httpURLPath = httpURL.getPath();
            String httpURLPath = context.getBaseUrl() + getPath();
            String escapedHref = URIUtil.decode(href);

            // Normalize them to both have trailing slashes if they differ by one in length.
            int lenDiff = escapedHref.length() - httpURLPath.length();
            int compareLen = 0;

            if ( lenDiff == -1 && !escapedHref.endsWith("/")) {
                compareLen = escapedHref.length();
                lenDiff = 0;
            }
            else
            if ( lenDiff == 1 && !httpURLPath.endsWith("/")) {
                compareLen = httpURLPath.length();
                lenDiff = 0;
            }

            // if they are the same length then compare them.
            if (lenDiff == 0) {
                if ((compareLen == 0 && httpURLPath.equals(escapedHref))
                    || httpURLPath.regionMatches(0, escapedHref, 0, compareLen))
                {
                    // escaped href and http path are the same
                    // Set the status code for this resource.
                    if (response.getStatusCode() > 0)
                        this.statusCode = response.getStatusCode();
                    this.exist = true;
                    itself = true;
                }
            }


            // Get to know each resource.
            DavResource workingResource = null;
            if (itself) {
                workingResource = this;
            }
            else {
                workingResource = createWebdavResource(client);
            }

            // clear the current lock set
            workingResource.setLockDiscovery(null);

            // Process the resource's properties
            Enumeration properties = response.getProperties();
            while (properties.hasMoreElements()) {

                Property property = (Property) properties.nextElement();

                // ------------------------------  Checking WebDAV properties
                workingResource.processProperty(property);
            }

            String displayName = workingResource.displayName;

            if (displayName == null || displayName.trim().equals("")) {
                displayName = getName(href);
            }
            if (!itself) {
                String myPath = getPath();
                String childPath = myPath + (myPath.endsWith("/") ? "" : "/") + DavResource.getName(href); //(myPath.endsWith("/") ? "" : "/") + URIUtil.getName(href);
                workingResource.setEncodedPath(childPath);
                workingResource.setExistence(true);
            }

            if (!itself)
                childResources.addResource(workingResource);
        }
    }

    /**
     * Get an array of pathnames and basic information denoting the WebDAV
     * resources in the denoted by this pathname.
     *
     * array 0: displayname
     * array 1: getcontentlength
     * array 2: iscollection or getcontentype
     * array 3: getlastmodifieddate
     * array 4: name
     * array 5: getcreationdate
     * array 6: getowner
     *
     * @return An array of pathnames and more denoting the resources.
     * @exception HttpException
     * @exception IOException
     */
    public Vector listBasic()
        throws HttpException, IOException {

        if (defaultDepth != DepthSupport.DEPTH_1 || !propfindExecuted)
            setAllProp(DepthSupport.DEPTH_1);
        Enumeration hrefs = childResources.getResourceNames();

        Vector hrefList = new Vector();
        while (hrefs.hasMoreElements()) {
            try {
                String resourceName = (String) hrefs.nextElement();
                DavResource currentResource =
                    childResources.getResource(resourceName);

                String[] longFormat = new String[7];
                // displayname.
                longFormat[0] = currentResource.getDisplayName();


                long length = currentResource.getContentLength();
                // getcontentlength
                longFormat[1] = new Long(length).toString();
                // resourcetype
                ResourceTypeProperty resourceTypeProperty =
                    currentResource.getResourceType();
                // getcontenttype
                String getContentType =
                    currentResource.getContentType();
                longFormat[2] = resourceTypeProperty.isCollection() ?
                    "COLLECTION" : getContentType ;
                Date date = new Date(currentResource.getLastModified());
                // getlastmodified
                // Save the dummy what if failed.
                longFormat[3] = (date == null) ? "-- -- ----" :
                    // Print the local fancy date format.
                    DateFormat.getDateTimeInstance().format(date);

                hrefList.addElement(longFormat);

                // real name of componente
                longFormat[4] = currentResource.getName();

                date = new Date(currentResource.getCreationDate());
                // getlastmodified
                // Save the dummy what if failed.
                longFormat[5] = (date == null) ? "-- -- ----" :
                    // Print the local fancy date format.
                    DateFormat.getDateTimeInstance().format(date);

               longFormat[6] = currentResource.getOwner();




            } catch (Exception e) {
                // FIXME: After if's gotten an exception, any solution
                log.error(e,e);
            }
        }

        return hrefList;
    }


    public Vector list()
        throws HttpException, IOException {

        if (defaultDepth != DepthSupport.DEPTH_1 || !propfindExecuted)
            setAllProp(DepthSupport.DEPTH_1);
        Enumeration hrefs = childResources.getResourceNames();

        Vector hrefList = new Vector();
        while (hrefs.hasMoreElements()) {
            try {
                String resourceName = (String) hrefs.nextElement();
                DavResource currentResource =
                    childResources.getResource(resourceName);
                hrefList.add(currentResource);

            } catch (Exception e) {
                // FIXME: After if's gotten an exception, any solution
                log.error(e,e);
            }
        }

        return hrefList;
    }



    /**
     * Set all properties for this resource.
     *
     */
    protected void setAllProp()
        throws HttpException, IOException {

        setAllProp(defaultDepth);
    }

    /**
     * Set all properties for this resource.
     *
     * @param depth The depth
     */
    protected void setAllProp(int depth)
        throws HttpException, IOException {

        try{
            Enumeration responses = propfindMethod(depth);
            setWebdavProperties(responses);
        }
        catch(HttpException e)
        {
            if (e.getReasonCode() == 404)
                this.exist = false;
            else
                throw e;
        }

        this.propfindExecuted = true;
    }


        /**
     * Create a new WebdavResource object (as a seperate method so that it can
     * be overridden by subclasses.
     *
     * @param client HttpClient to be used by this webdavresource.
     * @return A new WebdavResource object.
     */
    protected DavResource createWebdavResource(HttpClient client) {
        DavResource resource = new DavResource("", context);
        return resource;
    }

        /**
     * Process a property, setting various member variables depending
     * on what the property is.
     *
     * @param property The property to process.
     */
    protected void processProperty(Property property) {
        if (property.getLocalName().equals(DISPLAYNAME)) {
            this.displayName = property.getPropertyAsString();
        }
        else if (property.getLocalName().equals(GETCONTENTLENGTH)) {
            String getContentLength = property.getPropertyAsString();
            this.contentLength = Long.parseLong(getContentLength);
        }
        else if (property.getLocalName().equals(RESOURCETYPE)) {
            ResourceTypeProperty resourceType = (ResourceTypeProperty) property;
            this.resourceType = resourceType;
        }
        else if (property.getLocalName().equals(GETCONTENTTYPE)) {
            this.contentType = property.getPropertyAsString();
        }
        else if (property.getLocalName().equals(GETLASTMODIFIED)) {
            String getLastModified = property.getPropertyAsString();
            setLastModified(getLastModified);
        }
        else if (property.getLocalName().equals(CREATIONDATE)) {
            String creationDate = property.getPropertyAsString();
            setCreationDate(creationDate);
        }
        else if (property.getLocalName().equals(GETETAG)) {
            String getEtag = property.getPropertyAsString();
            this.etag = getEtag;
        }
        else if (property.getLocalName().equals(ISHIDDEN)) {
            String isHidden = property.getPropertyAsString();
            this.isHidden = new Boolean(isHidden).booleanValue();
        }
        else if (property.getLocalName().equals(ISCOLLECTION)) {
            String isCollection = property.getPropertyAsString();
             // this.isCollection = (new Boolean(isCollection)).booleanValue();
        }
        else if (property.getLocalName().equals(SUPPORTEDLOCK)) {
            String supportedLock = property.getPropertyAsString();
            this.supportedLock = supportedLock;
        }
        else if (property.getLocalName().equals(LOCKDISCOVERY)) {
            LockDiscoveryProperty lockDiscovery = (LockDiscoveryProperty) property;
            setLockDiscovery(lockDiscovery);
        }
        else if (property.getLocalName().equals(PROPERTYEXPORTFORMAT)) {
            ExportProperty exportProperty = (ExportProperty) property;
            this.exportFormat = exportProperty.getExportFormat();
        }
    }

        /**
     * Set the value of DAV property, getlastmodified.
     *
     * @param getLastModified The getlastmodified value.
     */
    protected void setLastModified(String getLastModified) {
        Date date = parseDate(getLastModified);
        if (date != null)
            this.lastModified = date.getTime();
    }

    /**
     * Set the value of DAV property, creationdate.
     *
     * @param creationDate The creationdate string.
     */
    protected void setCreationDate(String creationDate) {
        Date date = parseDate(creationDate);
        if (date != null)
            this.creationDate = date.getTime();
    }

    /**
      * Parse the <code>java.util.Date</code> string for HTTP-date.
      *
      * @return The parsed date.
      */
     protected Date parseDate(String dateValue) {
         // TODO: move to the common util package related to http.
         Date date = null;
         for (int i = 0; (date == null) && (i < formats.length); i++) {
             try {
                 synchronized (formats[i]) {
                     date = formats[i].parse(dateValue);
                 }
             } catch (ParseException e) {
             }
         }

         return date;
     }



   private static String decodeMarks(String input) {
        char[] sequence = input.toCharArray();
        StringBuffer decoded = new StringBuffer(sequence.length);
        for (int i = 0; i < sequence.length; i++) {
            if (sequence[i] == '%' && i < sequence.length - 2) {
                switch (sequence[i + 1]) {
                case '2':
                    switch (sequence[i + 2]) {
                    case 'd':
                    case 'D':
                        decoded.append('-');
                        i += 2;
                        continue;
                    case 'e':
                    case 'E':
                        decoded.append('.');
                        i += 2;
                        continue;
                    case '1':
                        decoded.append('!');
                        i += 2;
                        continue;
                    case 'a':
                    case 'A':
                        decoded.append('*');
                        i += 2;
                        continue;
                    case '7':
                        decoded.append('\'');
                        i += 2;
                        continue;
                    case '8':
                        decoded.append('(');
                        i += 2;
                        continue;
                    case '9':
                        decoded.append(')');
                        i += 2;
                        continue;
                    }
                    break;
                case '5':
                    switch (sequence[i + 2]) {
                    case 'f':
                    case 'F':
                        decoded.append('_');
                        i += 2;
                        continue;
                    }
                    break;
                case '7':
                    switch (sequence[i + 2]) {
                    case 'e':
                    case 'E':
                        decoded.append('~');
                        i += 2;
                        continue;
                    }
                    break;
                }
            }
            decoded.append(sequence[i]);
        }
        return decoded.toString();
    }


    private static String getName(String uri) {
        String escapedName = URIUtil.getName(
            uri.endsWith("/") ? uri.substring(0, uri.length() - 1): uri);
        try {
            return URIUtil.decode(escapedName);
        } catch (URIException e) {
            return escapedName;
        }
    }

    public String getName() {
        return getPath();
    }

}
