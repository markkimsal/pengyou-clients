/* ====================================================================
 *   Copyright 2005 Jérémi Joslin.
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

import java.io.IOException;

public class DavSession {
    protected HttpClient client = null;
    protected DavContext context = null;
    protected Credentials hostCredentials = null;
    protected Credentials proxyCredentials = null;
    protected int debug = 0;


     public HttpClient getSessionInstance() throws IOException {
         return getSessionInstance(false);
     }
    /**
     * Get a <code>HttpClient</code> instance.
     * This method returns a new client instance, when reset is true.
     *
     * @param reset The reset flag to represent whether the saved information
     *              is used or not.
     * @return An instance of <code>HttpClient</code>.
     * @exception IOException
     */
    public HttpClient getSessionInstance(boolean reset)
        throws IOException {

        if (reset || client == null) {
            HttpURL httpURL = context.getHttpURL();
            client = new HttpClient();
            // Set a state which allows lock tracking
            client.setState(new WebdavState());
            HostConfiguration hostConfig = client.getHostConfiguration();
            hostConfig.setHost(httpURL);
            if (context.getProxyHost() != null && context.getProxyPort() > 0)
                hostConfig.setProxy(context.getProxyHost(), context.getProxyPort());

            if (hostCredentials == null) {
                String userName = httpURL.getUser();
                if (userName != null && userName.length() > 0) {
                    hostCredentials =
                        new UsernamePasswordCredentials(userName,
                                                        httpURL.getPassword());
                }
            }

            if (hostCredentials != null) {
                HttpState clientState = client.getState();
                clientState.setCredentials(null, httpURL.getHost(),
                                           hostCredentials);
                clientState.setAuthenticationPreemptive(true);
            }

            if (proxyCredentials != null) {
                client.getState().setProxyCredentials(null, context.getProxyHost(),
                                                      proxyCredentials);
            }
        }

        return client;
    }

    /**
     * Close an session and delete the connection information.
     *
     * @exception IOException Error in closing socket.
     */
    public void closeSession()
        throws IOException {
        if (client != null) {
            client.getHttpConnectionManager().getConnection(
                client.getHostConfiguration()).close();
            client = null;
        }
    }

}
