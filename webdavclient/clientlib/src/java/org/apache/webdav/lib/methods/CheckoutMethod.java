/*
 * $Header$
 * $Revision: 207563 $
 * $Date: 2004-08-02 23:45:51 +0800 (Mon, 02 Aug 2004) $
 *
 * ====================================================================
 *
 * Copyright 1999-2002 The Apache Software Foundation 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.webdav.lib.methods;

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.httpclient.HttpConnection;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpState;
import org.apache.webdav.lib.util.WebdavStatus;


/**
 * The Checkout method can be applied to a checked-in version-controlled
 * resource.
 *
 *
 *
 * <h3>Example Request</h3>
 * <pre>
 * Checkout /foo.html HTTP/1.1
 * Host: www.server.org
 * Content-Length: xx
 * </pre>
 *
 * <h3>Example Response</h3>
 * <pre>
 * HTTP/1.1 200 OK
 * </pre>
 *
 */
public class CheckoutMethod
    extends XMLResponseMethodBase {


    // -------------------------------------------------------------- Constants



    // ----------------------------------------------------- Instance Variables



    // ----------------------------------------------------------- Constructors


    /**
     * Method constructor.
     */
    public CheckoutMethod() {
    }


    /**
     * Method constructor.
     */
    public CheckoutMethod(String path) {
        super(path);
    }

    /**
     * Parse response.
     *
     * @param input Input stream
     */
    public void parseResponse(InputStream input, HttpState state, HttpConnection conn)
        throws IOException, HttpException {
        try
        {
            if (getStatusLine().getStatusCode() == WebdavStatus.SC_CONFLICT     ||
                getStatusLine().getStatusCode() == WebdavStatus.SC_FORBIDDEN ) {
                parseXMLResponse(input);
            }
        }
        catch (IOException e) {
                // FIX ME:  provide a way to deliver non xml data
        }
    }

    public String getName() {
        return "CHECKOUT";
    }
}
