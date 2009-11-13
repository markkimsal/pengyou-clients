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

import org.apache.commons.httpclient.HttpURL;
import org.apache.commons.httpclient.URIException;

public class DavContext {
    private String proxyHost = null;
    private int proxyPort;
    private String host;
    private int port;
    private String baseUrl = null;
    private String username = null;
    private String password = null;

    public DavContext(String host, int port, String baseUrl)
    {
        setBaseUrl(baseUrl);
        this.port = port;
        this.host = host;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public int getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = (baseUrl.charAt(baseUrl.length() - 1) == '/' ? baseUrl : baseUrl + "/");
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public HttpURL getHttpURL() throws URIException {
        HttpURL url = new HttpURL(this.host + ":" + this.port);
        url.setUserinfo(this.username, this.getPassword());
        return url;
    }
}
